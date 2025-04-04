package api.seller.product;

import api.seller.login.APISellerLogin;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import utility.APIUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.IntStream;

/**
 * API utility class for retrieving and managing product information from a store's API.
 */
public class APIGetProductList {

    private final APISellerLogin.LoginInformation loginInfo;
    private final APISellerLogin.Credentials credentials;

    /**
     * Constructs an instance of APIGetProductList with seller credentials.
     *
     * @param credentials The credentials used for login to the seller's account.
     */
    public APIGetProductList(APISellerLogin.Credentials credentials) {
        this.credentials = credentials;
        this.loginInfo = new APISellerLogin().getSellerInformation(credentials);
    }

    /**
     * Represents a product with various attributes.
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Product {
        private int id;
        private String name;
        private int remainingStock;
        private String bhStatus;
        private List<String> saleChannels;
        private int variationNumber;
        private List<ModelInfo> modelInfos;
        private String createdDate;
        private Long orgPrice;
        private Long newPrice;
        private Long costPrice;
        private String currency;
        private boolean hasConversion;

        /**
         * Represents model information associated with a product.
         */
        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class ModelInfo {
            private int modelId;
            private String modelName;
            private Long stockAlertNumber;
        }
    }

    public enum ProductFilterType {
        RECENT_UPDATED, STOCK_HIGH_TO_LOW, STOCK_LOW_TO_HIGH,
        PRIORITY_HIGH_TO_LOW, PRIORITY_LOW_TO_HIGH,
        STATUS, CHANNEL, PLATFORM, BRANCH, COLLECTION, DEFAULT
    }

    private String getFilterPath(ProductFilterType filterType, String value) {
        String getAllProductPath = "/itemservice/api/store/dashboard/%s/items-v2?page=0&size=100&itemType=BUSINESS_PRODUCT".formatted(loginInfo.getStore().getId());

        switch (filterType) {
            case RECENT_UPDATED:
                getAllProductPath += "&sort=lastModifiedDate,desc";
                break;
            case STOCK_HIGH_TO_LOW:
                getAllProductPath += "&sort=stock,desc";
                break;
            case STOCK_LOW_TO_HIGH:
                getAllProductPath += "&sort=stock,asc";
                break;
            case PRIORITY_HIGH_TO_LOW:
                getAllProductPath += "&sort=priority,desc&sort=lastModifiedDate,desc";
                break;
            case PRIORITY_LOW_TO_HIGH:
                getAllProductPath += "&sort=priority,asc&sort=lastModifiedDate,desc";
                break;
            case STATUS:
                getAllProductPath += "&sort=lastModifiedDate,desc&bhStatus=" + value;
                break;
            case CHANNEL:
                getAllProductPath += "&sort=lastModifiedDate,desc&saleChannel=" + value;
                break;
            case PLATFORM:
                getAllProductPath += "&sort=lastModifiedDate,desc&platform=" + value;
                break;
            case BRANCH:
                getAllProductPath += "&sort=lastModifiedDate,desc&branchIds=" + value;
                break;
            case COLLECTION:
                getAllProductPath += "&sort=lastModifiedDate,desc&collectionId=" + value;
                break;
        }

        return getAllProductPath;
    }


    /**
     * Constructs the API path to retrieve the list of products based on search criteria.
     *
     * @param keyword   The keyword to search for in product names.
     * @param pageIndex The page index for pagination.
     * @param branchIds Optional branch IDs for filtering products by branch.
     * @return The API path as a string.
     */
    private String getListProductPath(String keyword, int pageIndex, int... branchIds) {
        String branchId = branchIds.length == 0 ? "" : String.valueOf(branchIds[0]);
        return String.format("/itemservice/api/store/dashboard/%s/items-v2?page=%s&size=100&bhStatus=&itemType=BUSINESS_PRODUCT&sort=lastModifiedDate,desc&branchIds=%s&searchItemName=%s&searchType=PRODUCT_NAME",
                loginInfo.getStore().getId(), pageIndex, branchId, keyword);
    }

    /**
     * Sends a request to the API to get all products based on search criteria.
     *
     * @param keywords  The keywords to search for.
     * @param pageIndex The page index for pagination.
     * @param branchIds Optional branch IDs for filtering products by branch.
     * @return The response from the API request.
     */
    private Response getProductListResponse(String keywords, int pageIndex, int... branchIds) {
        return new APIUtils().get(getListProductPath(keywords, pageIndex, branchIds), loginInfo.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

    /**
     * Retrieves all product information based on the search keyword and branch IDs.
     *
     * @param keyword   The keyword to search for in product names.
     * @param branchIds Optional branch IDs for filtering products by branch.
     * @return A list of products matching the search criteria.
     */
    public List<Product> getAllProductInformation(String keyword, int... branchIds) {
        List<Product> products = new ArrayList<>();
        // Get the total number of products
        int totalOfProducts = Integer.parseInt(getProductListResponse(keyword, 0, branchIds).getHeader("X-Total-Count"));

        // Calculate the number of pages
        int numberOfPages = (totalOfProducts + 99) / 100; // Ensure rounding up

        // Fetch product data from all pages
        List<String> responseStrings = IntStream.range(0, numberOfPages)
                .parallel()
                .mapToObj(pageIndex -> getProductListResponse(keyword, pageIndex, branchIds).asPrettyString())
                .toList();

        responseStrings.forEach(responseString -> {
            try {
                products.addAll(new ObjectMapper().readValue(responseString, new TypeReference<List<Product>>() {
                }));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error parsing JSON response", e);
            }
        });
        return products;
    }

    public List<Product> getProductInformationInFirstPage(ProductFilterType filterType, String value) {
        String responseString = new APIUtils()
                .get(getFilterPath(filterType, value), loginInfo.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .asPrettyString();

        try {
            return new ObjectMapper().readValue(responseString, new TypeReference<List<Product>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing JSON response", e);
        }
    }

    /**
     * Searches for a product ID by its name.
     * <p>
     * This method retrieves all product information and searches for a product with the exact matching name.
     * It returns the product's ID if found, or throws an exception if the product is not found.
     *
     * @param name The name of the product to search for.
     * @return The product ID if found.
     * @throws NoSuchElementException if no product with the given name is found.
     */
    public int searchProductIdByName(String name) {
        return getAllProductInformation(name).parallelStream()
                .filter(product -> product.getName().equals(name))
                .findAny()
                .map(Product::getId)
                .orElseThrow(() -> new RuntimeException("Product with name '" + name + "' not found"));
    }

    /**
     * Retrieves the remaining stock quantity for a product by its ID.
     * <p>
     * This method fetches product information from Elasticsearch and searches for a product with the given ID.
     * If the product is found, it returns the remaining stock quantity. If the product is not found, it returns 0.
     * </p>
     * <p>
     * The search is performed across all branches specified in {@code branchIds}. If no branch IDs are provided,
     * the search will include all available branches.
     * </p>
     *
     * @param productId The ID of the product to search for.
     * @param branchIds (Optional) The branch IDs to filter the search. If none are provided, all branches are considered.
     * @return The remaining stock quantity of the product, or {@code 0} if the product is not found.
     */
    public int fetchRemainingStockByProductId(int productId, int... branchIds) {
        // Logger
        LogManager.getLogger().info("Get product stock from Elasticsearch, id: {} ", productId);

        // Get product name
        String productName = new APIGetProductDetail(credentials).getProductInformation(productId).getName();

        // Fetch and return product's remaining stock
        return getAllProductInformation(productName, branchIds).parallelStream()
                .filter(product -> product.getId() == productId)
                .findAny()
                .map(Product::getRemainingStock)
                .orElse(0);
    }

    /**
     * Checks whether a product is deleted from Elasticsearch by its ID.
     *
     * @param productId The ID of the product to check.
     * @return {@code true} if the product is deleted from Elasticsearch, {@code false} otherwise.
     */
    public boolean isProductDeletedFromElasticsearch(int productId) {
        // Logger
        LogManager.getLogger().info("Verify product is deleted from Elasticsearch, id: {} ", productId);

        // Get product name
        String productName = new APIGetProductDetail(credentials).getProductInformation(productId).getName();

        // Check if the product exists in Elasticsearch
        return getAllProductInformation(productName).parallelStream()
                .noneMatch(product -> product.getId() == productId);
    }

    /**
     * Retrieves the status of a product by its ID.
     *
     * @param productId The ID of the product whose status is to be retrieved.
     * @return The status of the product if found, or an empty string if the product is not found.
     */
    public String fetchElasticsearchProductStatus(int productId) {
        // Logger
        LogManager.getLogger().info("Get product status from Elasticsearch, id: {} ", productId);

        // Retrieve the product name using the product ID
        String productName = new APIGetProductDetail(credentials).getProductInformation(productId).getName();

        // Fetch the product status from Elasticsearch
        return getAllProductInformation(productName).parallelStream()
                .filter(product -> product.getId() == productId)
                .findAny()
                .map(Product::getBhStatus)
                .orElse("");
    }

}
