package api.seller.product;

import api.seller.login.APIDashboardLogin;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import lombok.Data;
import utility.APIUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * API utility class for retrieving and managing product information from a store's API.
 */
public class APIGetProductList {

    private final APIDashboardLogin.SellerInformation loginInfo;

    /**
     * Constructs an instance of APIGetProductList with seller credentials.
     *
     * @param credentials The credentials used for login to the seller's account.
     */
    public APIGetProductList(APIDashboardLogin.Credentials credentials) {
        this.loginInfo = new APIDashboardLogin().getSellerInformation(credentials);
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
    private Response getAllProductsResponse(String keywords, int pageIndex, int... branchIds) {
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
        int totalOfProducts = Integer.parseInt(getAllProductsResponse(keyword, 0, branchIds).getHeader("X-Total-Count"));

        // Calculate the number of pages
        int numberOfPages = (totalOfProducts + 99) / 100; // Ensure rounding up

        // Fetch product data from all pages
        List<String> responseStrings = IntStream.range(0, numberOfPages)
                .parallel()
                .mapToObj(pageIndex -> getAllProductsResponse(keyword, pageIndex, branchIds).asPrettyString())
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

    /**
     * Searches for a product ID by its name.
     *
     * @param name The name of the product to search for.
     * @return The product ID if found, or 0 if not found.
     */
    public int searchProductIdByName(String name) {
        return getAllProductInformation(name).parallelStream()
                .filter(product -> product.getName().equals(name))
                .findAny()
                .map(Product::getId)
                .orElse(0);
    }
}
