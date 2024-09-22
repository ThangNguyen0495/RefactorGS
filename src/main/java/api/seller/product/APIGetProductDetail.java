package api.seller.product;

import api.seller.login.APISellerLogin;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.restassured.response.Response;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import utility.APIUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static api.seller.product.APIGetProductDetail.ProductInformation.MainLanguage;

/**
 * Provides functionality to retrieve and process product details from the API.
 * This class interacts with the API to fetch product details and provides utility methods to process
 * and analyze product data, including handling product variations and multi-language support.
 */
public class APIGetProductDetail {

    private final APISellerLogin.LoginInformation loginInfo;

    /**
     * Constructs an instance of APIGetProductDetail with the specified credentials.
     *
     * @param credentials The credentials used to authenticate with the API.
     */
    public APIGetProductDetail(APISellerLogin.Credentials credentials) {
        loginInfo = new APISellerLogin().getSellerInformation(credentials);
    }

    /**
     * Represents detailed information about a product, including its pricing,
     * descriptions, shipping details, attributes, and stock information.
     * It includes various nested classes to handle different aspects of the product,
     * such as models, categories, shipping info, and language-specific details.
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ProductInformation {
        private String lastModifiedDate;
        private int id;
        private String name;
        private String currency;
        private String description;
        private long orgPrice;
        private int discount;
        private long newPrice;
        private ShippingInfo shippingInfo;
        private boolean deleted;
        private List<Model> models;
        private boolean hasModel;
        private boolean showOutOfStock;
        private String seoTitle;
        private String seoDescription;
        private String seoKeywords;
        private String barcode;
        private String seoUrl;
        private List<BranchStock> branches;
        private List<MainLanguage> languages;
        private List<ItemAttribute> itemAttributes;
        private int taxId;
        private String taxName;
        private double taxRate;
        private double taxAmount;
        private long costPrice;
        private boolean onApp;
        private boolean onWeb;
        private boolean inStore;
        private boolean inGosocial;
        private boolean enabledListing;
        private Boolean isHideStock;
        private String inventoryManageType;
        private String bhStatus;
        private boolean lotAvailable;
        private boolean expiredQuality;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class ShippingInfo {
            private int weight;
            private int width;
            private int height;
            private int length;
        }

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Model {
            private int id;
            private String name;
            private String sku;
            private long orgPrice;
            private long newPrice;
            private String label;
            private String orgName;
            private String description;
            private String barcode;
            private String versionName;
            private boolean useProductDescription;
            private boolean reuseAttributes;
            private String status;
            private List<BranchStock> branches;
            private List<VersionLanguage> languages;
            private List<ItemAttribute> modelAttributes;
            private long costPrice;

            @Data
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class VersionLanguage {
                private String language;
                private String name;
                private String label;
                private String description;
                private String versionName;
            }
        }

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class BranchStock {
            private int branchId;
            private int totalItem;
            private int soldItem;
        }

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class MainLanguage {
            private String language;
            private String name;
            private String description;
            private String seoTitle;
            private String seoDescription;
            private String seoKeywords;
            private String seoUrl;
        }

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class ItemAttribute {
            private String attributeName;
            private String attributeValue;
            private boolean isDisplay;
        }
    }

    /**
     * Retrieves product information from the API based on the specified product ID.
     *
     * @param productId The ID of the product to retrieve.
     * @return A {@link ProductInformation} object containing details of the requested product.
     */
    public ProductInformation getProductInformation(int productId) {
        // Logger
        LogManager.getLogger().info("===== STEP =====> [GetProductInfo] ProductId: {} ", productId);

        Response response = new APIUtils().get("/itemservice/api/beehive-items/%d".formatted(productId), loginInfo.getAccessToken());

        return switch (response.getStatusCode()) {
            case 200 -> response.as(ProductInformation.class);
            case 404 -> {
                var productInfo = new ProductInformation();
                productInfo.setId(productId);
                productInfo.setDeleted(true);
                yield productInfo;
            }
            default ->
                    throw new AssertionError("Can not get product detail, response: \n%s.".formatted(response.asPrettyString()));
        };
    }

    /**
     * Retrieves the main product name for a specified language from the provided {@link ProductInformation}.
     *
     * @param productInformation Represents detailed information about a product, including its pricing,
     *                           descriptions, shipping details, attributes, and stock information.
     *                           It includes various nested classes to handle different aspects of the product,
     *                           such as models, categories, shipping info, and language-specific details.
     * @param language           The language code for which to retrieve the main product name.
     * @return The product name in the specified language. If the name is not available for the specified
     * language, an empty string is returned.
     */
    public static String getMainProductName(ProductInformation productInformation, String language) {
        return productInformation.getLanguages().stream()
                .filter(mainLanguage -> mainLanguage.getLanguage().equals(language))
                .findFirst()
                .map(MainLanguage::getName)
                .orElse("");
    }

    /**
     * Retrieves the main product description for a specified language from the provided {@link ProductInformation}.
     *
     * @param productInformation Represents detailed information about a product, including its pricing,
     *                           descriptions, shipping details, attributes, and stock information.
     *                           It includes various nested classes to handle different aspects of the product,
     *                           such as models, categories, shipping info, and language-specific details.
     * @param language           The language code for which to retrieve the main product description.
     * @return The product description in the specified language. If the description is not available for
     * the specified language, an empty string is returned.
     */
    public static String getMainProductDescription(ProductInformation productInformation, String language) {
        return productInformation.getLanguages().stream()
                .filter(mainLanguage -> mainLanguage.getLanguage().equals(language))
                .findFirst()
                .map(MainLanguage::getDescription)
                .orElse("");
    }

    /**
     * Retrieves the variation group name for a specified language from the provided {@link ProductInformation}.
     *
     * @param productInformation Represents detailed information about a product, including its pricing,
     *                           descriptions, shipping details, attributes, and stock information.
     *                           It includes various nested classes to handle different aspects of the product,
     *                           such as models, categories, shipping info, and language-specific details.
     * @param language           The language code for which to retrieve the variation group name.
     * @return The variation group name in the specified language. If the language is not found,
     * an empty string is returned.
     */
    public static String getVariationGroupName(ProductInformation productInformation, String language) {
        return productInformation.getModels().getFirst().getLanguages().stream()
                .filter(modelLanguage -> modelLanguage.getLanguage().equals(language))
                .map(ProductInformation.Model.VersionLanguage::getLabel)
                .findFirst()
                .orElse("");
    }

    /**
     * Extracts the listing prices of variations.
     *
     * @param productInformation Represents detailed information about a product, including its pricing,
     *                           descriptions, shipping details, attributes, and stock information.
     *                           It includes various nested classes to handle different aspects of the product,
     *                           such as models, categories, shipping info, and language-specific details.
     * @return A list of listing prices for each variation model.
     */
    public static List<Long> getVariationListingPrice(ProductInformation productInformation) {
        return productInformation.getModels().stream()
                .map(ProductInformation.Model::getOrgPrice)
                .toList();
    }

    /**
     * Extracts the listing price of a specific variation based on its index.
     *
     * @param productInformation Represents detailed information about a product, including its pricing,
     *                           descriptions, shipping details, attributes, and stock information.
     *                           It includes various nested classes to handle different aspects of the product,
     *                           such as models, categories, shipping info, and language-specific details.
     * @param variationIndex           The index of the variation model for which to retrieve the listing price.
     * @return The listing price for the specified variation model.
     */
    public static Long getVariationListingPrice(ProductInformation productInformation, int variationIndex) {
        return productInformation.getModels().get(variationIndex).getOrgPrice();
    }

    /**
     * Extracts the selling prices of variations.
     *
     * @param productInformation Represents detailed information about a product, including its pricing,
     *                           descriptions, shipping details, attributes, and stock information.
     *                           It includes various nested classes to handle different aspects of the product,
     *                           such as models, categories, shipping info, and language-specific details.
     * @return A list of selling prices for each variation model.
     */
    public static List<Long> getVariationSellingPrice(ProductInformation productInformation) {
        return productInformation.getModels().stream()
                .map(ProductInformation.Model::getNewPrice)
                .toList();
    }

    /**
     * Extracts the selling price of a specific variation based on its index.
     *
     * @param productInformation Represents detailed information about a product, including its pricing,
     *                           descriptions, shipping details, attributes, and stock information.
     *                           It includes various nested classes to handle different aspects of the product,
     *                           such as models, categories, shipping info, and language-specific details.
     * @param variationIndex           The index of the variation model for which to retrieve the selling price.
     * @return The selling price for the specified variation model.
     */
    public static Long getVariationSellingPrice(ProductInformation productInformation, int variationIndex) {
        return productInformation.getModels().get(variationIndex).getNewPrice();
    }

    /**
     * Extracts the cost prices of variations.
     *
     * @param productInformation Represents detailed information about a product, including its pricing,
     *                           descriptions, shipping details, attributes, and stock information.
     *                           It includes various nested classes to handle different aspects of the product,
     *                           such as models, categories, shipping info, and language-specific details.
     * @return A list of cost prices for each variation model.
     */
    public static List<Long> getVariationCostPrice(ProductInformation productInformation) {
        return productInformation.getModels().stream()
                .map(ProductInformation.Model::getCostPrice)
                .toList();
    }

    /**
     * Extracts the cost price of a specific variation based on its index.
     *
     * @param productInformation Represents detailed information about a product, including its pricing,
     *                           descriptions, shipping details, attributes, and stock information.
     *                           It includes various nested classes to handle different aspects of the product,
     *                           such as models, categories, shipping info, and language-specific details.
     * @param variationIndex           The index of the variation model for which to retrieve the cost price.
     * @return The cost price for the specified variation model.
     */
    public static Long getVariationCostPrice(ProductInformation productInformation, int variationIndex) {
        return productInformation.getModels().get(variationIndex).getCostPrice();
    }


    /**
     * Retrieves a list of variation model IDs.
     *
     * @param productInformation Represents detailed information about a product, including its pricing,
     *                           descriptions, shipping details, attributes, and stock information.
     *                           It includes various nested classes to handle different aspects of the product,
     *                           such as models, categories, shipping info, and language-specific details.
     * @return A list of variation model IDs. If the product has no variations, a list containing a single {@code null} is returned.
     */
    public static List<Integer> getVariationModelList(ProductInformation productInformation) {
        return productInformation.getModels().stream()
                .map(ProductInformation.Model::getId)
                .toList();
    }

    /**
     * Retrieves the variation model ID at the specified index.
     *
     * @param productInformation Represents detailed information about a product, including its pricing,
     *                           descriptions, shipping details, attributes, and stock information.
     *                           It includes various nested classes to handle different aspects of the product,
     *                           such as models, categories, shipping info, and language-specific details.
     * @param variationIndex     The index of the variation model to retrieve.
     * @return The variation model ID at the specified index. If the index is out of bounds, -1 is returned.
     */
    public static int getVariationModelId(ProductInformation productInformation, int variationIndex) {
        List<Integer> modelIds = getVariationModelList(productInformation);
        // Check if the variation index is within bounds and return the ID, otherwise return -1
        return (variationIndex >= 0 && variationIndex < modelIds.size()) ? modelIds.get(variationIndex) : -1;
    }

    /**
     * Creates a list of barcodes for variations.
     *
     * @param productInformation Represents detailed information about a product, including its pricing,
     *                           descriptions, shipping details, attributes, and stock information.
     *                           It includes various nested classes to handle different aspects of the product,
     *                           such as models, categories, shipping info, and language-specific details.
     * @return A list of barcodes for each variation model.
     */
    public static List<String> getBarcodeList(ProductInformation productInformation) {
        return productInformation.getModels().stream()
                .map(ProductInformation.Model::getBarcode)
                .toList();
    }

    /**
     * Retrieves the status of a variation model by its index.
     *
     * @param productInformation Represents detailed information about a product, including its pricing,
     *                           descriptions, shipping details, attributes, and stock information.
     *                           It includes various nested classes to handle different aspects of the product,
     *                           such as models, categories, shipping info, and language-specific details.
     * @param variationIndex     The index of the variation model to retrieve the status for.
     * @return The status of the variation model at the specified index. If the index is invalid, an empty string is returned.
     */
    public static String getVariationStatus(ProductInformation productInformation, int variationIndex) {
        List<ProductInformation.Model> models = productInformation.getModels();
        // Check if the variation index is valid and return the status, otherwise return an empty string
        return (variationIndex >= 0 && variationIndex < models.size()) ? models.get(variationIndex).getStatus() : "";
    }

    /**
     * Creates a map of product stock quantities indexed by model ID, with each model containing
     * a map of branch IDs and their corresponding stock.
     * <p>
     * This method calculates stock quantities for each variation model and associates the stock
     * with the relevant branch. If the product does not have variations, it uses a null key for
     * the model and associates stock with branches directly.
     *
     * @param productInformation Represents detailed information about a product, including pricing,
     *                           descriptions, shipping details, attributes, and stock information.
     *                           It contains various nested classes to manage different aspects of the product.
     * @return A map where the key is the model ID and the value is another map where the key is the branch ID
     * and the value is the stock quantity.
     */
    public static Map<Integer, Map<Integer, Integer>> getModelBranchStockMap(ProductInformation productInformation) {
        Map<Integer, Map<Integer, Integer>> modelBranchStockMap = new HashMap<>();

        // Check if the product has variations
        if (productInformation.isHasModel()) {
            // Iterate over each variation model
            productInformation.getModels().forEach(model -> {
                // Create a map to store branchId -> stock for each model
                Map<Integer, Integer> branchStockMap = new HashMap<>();

                // Populate branchId -> stock map for this model
                model.getBranches().forEach(branchStock -> {
                    branchStockMap.put(branchStock.getBranchId(), branchStock.getTotalItem() - branchStock.getSoldItem());
                });

                // Add modelId -> branchId -> stock map to the main map
                modelBranchStockMap.put(model.getId(), branchStockMap);
            });
        } else {
            // No variations; map stock by branch with null as the modelId key
            Map<Integer, Integer> branchStockMap = new HashMap<>();
            productInformation.getBranches().forEach(branchStock -> {
                branchStockMap.put(branchStock.getBranchId(), branchStock.getTotalItem() - branchStock.getSoldItem());
            });
            modelBranchStockMap.put(null, branchStockMap);
        }

        return modelBranchStockMap;
    }

    /**
     * Retrieves the available stock for a specific model variation at a specified branch.
     * <p>
     * This method looks up the stock quantity for a product variation (identified by its model ID)
     * at a particular branch (identified by its branch ID). It computes the stock available after
     * accounting for sold items using the product information.
     *
     * @param productInfo Represents detailed information about a product, including pricing,
     *                    descriptions, shipping details, attributes, and stock information.
     *                    It contains various nested classes to manage different aspects of the product.
     * @param modelId     The ID of the model variation for which to retrieve stock. If the product
     *                    does not have variations, this should be null.
     * @param branchId    The ID of the branch where the stock is to be retrieved.
     * @return The available stock quantity for the specified model variation at the given branch.
     * @throws NullPointerException If the model ID or branch ID does not exist in the product information.
     */
    public static int getStockByModelAndBranch(ProductInformation productInfo, Integer modelId, int branchId) {
        // Retrieve the stock for the specified model and branch
        return getModelBranchStockMap(productInfo).get(modelId).get(branchId);
    }


    /**
     * Creates a map of stock quantities for each model variation, indexed by variation model ID.
     * <p>
     * This method calculates available stock quantities for each variation model by considering
     * the total items and sold items across branches. If the product does not have variations,
     * it associates stock quantities with a null key.
     *
     * @param productInformation Represents detailed information about a product, including pricing,
     *                           descriptions, shipping details, attributes, and stock information.
     *                           It contains various nested classes to manage different aspects of the product.
     * @return A map where the key is the variation model ID and the value is a list of stock quantities per branch.
     */
    public static Map<Integer, List<Integer>> getStockMapByModel(ProductInformation productInformation) {
        Map<Integer, List<Integer>> stockMap = new HashMap<>();

        // Check if the product has variations
        if (productInformation.isHasModel()) {
            productInformation.getModels().forEach(model -> {
                List<Integer> variationStock = model.getBranches().stream()
                        .map(branchStock -> branchStock.getTotalItem() - branchStock.getSoldItem())
                        .toList();
                stockMap.put(model.getId(), variationStock);
            });
        } else {
            // No variations; map to null key
            stockMap.put(null, productInformation.getBranches().stream()
                    .map(branchStock -> branchStock.getTotalItem() - branchStock.getSoldItem())
                    .toList());
        }
        return stockMap;
    }

    /**
     * Retrieves a list of stock quantities for a specific variation model ID or for all main branches if the model ID is not provided.
     * <p>
     * The method fetches stock quantities from the product information. If a model ID is provided, it retrieves
     * stock quantities associated with that model. If the model ID is {@code null}, it retrieves stock quantities
     * from the main branches.
     *
     * @param productInformation Represents detailed information about a product, including pricing,
     *                           descriptions, shipping details, attributes, and stock information.
     * @param modelId            The variation model ID for which to retrieve stock quantities. If {@code null},
     *                           the method retrieves stock quantities from the main branches.
     * @return A list of stock quantities per branch. If {@code modelId} is provided, the list contains stock quantities
     *         for the branches associated with that model. If {@code modelId} is {@code null}, the list contains stock
     *         quantities from the main branches, calculated as the total items minus sold items.
     * @throws IllegalArgumentException if the modelId does not exist in the stock map.
     */
    public static List<Integer> getBranchStocks(ProductInformation productInformation, Integer modelId) {
        Map<Integer, List<Integer>> stockQuantityMap = getStockMapByModel(productInformation);

        // Retrieve stock quantities for the specific model or main branches
        List<Integer> branchStocks = stockQuantityMap.get(modelId);

        // Check if the modelId exists in the stock map
        if (branchStocks == null) {
            throw new IllegalArgumentException("The specified modelId does not exist in the stock map.");
        }

        return branchStocks;
    }


    /**
     * Calculates the total stock quantity for all models and branches in the given product information.
     * <p>
     * This method first retrieves a map of model IDs to their stock quantities per branch using
     * {@link #getStockMapByModel(ProductInformation)}. It then sums all the stock quantities
     * for each branch across all models.
     *
     * @param productInformation The {@code ProductInformation} object containing the stock details for the product's models and branches.
     * @return The total stock quantity across all models and branches.
     */
    public static int getTotalStockQuantity(ProductInformation productInformation) {
        // Generate the stock map
        Map<Integer, List<Integer>> stockMap = getStockMapByModel(productInformation);

        // Sum all stock quantities in the map
        return stockMap.values().stream()
                .flatMap(List::stream)
                .mapToInt(Integer::intValue)
                .sum();
    }

    /**
     * Checks if the product is in stock based on the stock quantities for its models across all branches.
     * <p>
     * This method retrieves a map of model IDs to their stock quantities per branch using
     * {@link #getStockMapByModel(ProductInformation)} and checks if the maximum stock quantity
     * for any branch is greater than 0.
     *
     * @param productInformation The {@code ProductInformation} object containing stock details for the product's models and branches.
     * @return {@code true} if any branch has stock greater than 0, otherwise {@code false}.
     */
    public static boolean isProductInStock(ProductInformation productInformation) {
        // Generate the stock map
        Map<Integer, List<Integer>> stockMap = getStockMapByModel(productInformation);

        // Check if any branch has stock greater than 0
        return stockMap.values().stream()
                .flatMap(List::stream)
                .anyMatch(stock -> stock > 0);
    }

    /**
     * Retrieves the version name for a specific variation model ID and language. If a version-specific name
     * is not found for the provided model ID and language, the main product name is returned as the default.
     *
     * @param productInformation Represents detailed information about a product, including its pricing,
     *                           descriptions, shipping details, attributes, and stock information.
     *                           It includes various nested classes to handle different aspects of the product,
     *                           such as models, categories, shipping info, and language-specific details.
     * @param modelId            The variation model ID for which to retrieve the version name.
     * @param language           The language code for which to retrieve the version name.
     * @return The version name for the specified model ID and language. If not found, the main product name for the language is returned.
     */
    public static String getVersionName(ProductInformation productInformation, Integer modelId, String language) {
        // Find the model with the specified modelId
        return productInformation.getModels().stream()
                .filter(model -> modelId.equals(model.getId()))
                .findFirst()
                // If the model is found, find the version name for the specified language
                .map(model -> model.getLanguages().stream()
                        .filter(versionLanguage -> versionLanguage.getLanguage().equals(language))
                        .findFirst()
                        .map(ProductInformation.Model.VersionLanguage::getVersionName)
                        // Return the version name if found, otherwise return the main product name
                        .orElse(getMainProductName(productInformation, language)))
                // If the model is not found, return the main product name
                .orElse(getMainProductName(productInformation, language));
    }

    /**
     * Retrieves the version description for a specific variation model ID and language. If a version-specific description
     * is not found for the provided model ID and language, the main product description is returned as the fallback.
     *
     * @param productInformation Represents detailed information about a product, including its pricing,
     *                           descriptions, shipping details, attributes, and stock information.
     *                           It includes various nested classes to handle different aspects of the product,
     *                           such as models, categories, shipping info, and language-specific details.
     * @param modelId            The variation model ID for which to retrieve the version description.
     * @param language           The language code for which to retrieve the version description.
     * @return The version description for the specified model ID and language. If not found, the main product description for the language is returned.
     */
    public static String getVersionDescription(ProductInformation productInformation, int modelId, String language) {
        // Find the model with the specified modelId
        return productInformation.getModels().stream()
                .filter(model -> model.getId() == modelId)
                .findFirst()
                // If the model is found, find the version description for the specified language
                .map(model -> model.getLanguages().stream()
                        .filter(versionLanguage -> versionLanguage.getLanguage().equals(language))
                        .findFirst()
                        .map(ProductInformation.Model.VersionLanguage::getDescription)
                        // Return the version description if found, otherwise return the main product description
                        .orElse(getMainProductDescription(productInformation, language)))
                // If the model is not found, return the main product description
                .orElse(getMainProductDescription(productInformation, language));
    }

    /**
     * Retrieves a list of variation values for a specific language.
     *
     * @param productInformation Represents detailed information about a product, including its pricing,
     *                           descriptions, shipping details, attributes, and stock information.
     *                           It includes various nested classes to handle different aspects of the product,
     *                           such as models, categories, shipping info, and language-specific details.
     * @param language           The language code for which to retrieve variation values.
     * @return A list of variation values in the specified language. If no values are found, an empty list is returned.
     */
    public static List<String> getVariationValues(ProductInformation productInformation, String language) {
        return productInformation.getModels().stream()
                .flatMap(model -> model.getLanguages().stream()
                        .filter(versionLanguage -> versionLanguage.getLanguage().equals(language))
                        .map(ProductInformation.Model.VersionLanguage::getName))
                .toList();
    }

    /**
     * Retrieves a specific variation value for a given language and variation index.
     * <p>
     * This method accesses the variation values for a particular language, and based on the provided index,
     * returns the corresponding value. If the index is out of bounds, it returns an empty string.
     *
     * @param productInformation Represents detailed information about a product, including pricing,
     *                           descriptions, shipping details, attributes, and stock information.
     *                           This object includes various nested classes to handle different aspects of the product,
     *                           such as models, categories, shipping info, and language-specific details.
     * @param language           The language code to retrieve the variation value.
     * @param variationIndex     The index of the variation value to retrieve.
     * @return The variation value at the specified index for the given language, or an empty string if the index is out of bounds.
     */
    public static String getVariationValue(ProductInformation productInformation, String language, int variationIndex) {
        List<String> variationValues = getVariationValues(productInformation, language);
        return (variationIndex >= 0 && variationIndex < variationValues.size()) ? variationValues.get(variationIndex) : "";
    }

    /**
     * Retrieves a list of boolean values indicating whether each attribute for the specified variation index
     * is set to be displayed. If the product has no variations, it retrieves the display status of the main attributes.
     *
     * @param productInformation Represents detailed information about a product, including pricing,
     *                           descriptions, shipping details, attributes, and stock information.
     *                           This object includes various nested classes to handle different aspects of the product,
     *                           such as models, categories, shipping info, and language-specific details.
     * @param variationIndex     The index of the variation for which to check the display status of attributes.
     *                           If the product has no variations, this parameter is ignored.
     * @return A list of boolean values, where {@code true} indicates that the attribute is set to be displayed, and {@code false} otherwise.
     */
    public static List<Boolean> getDisplayAttributesStatus(ProductInformation productInformation, int variationIndex) {
        // Get attribute configs
        return productInformation.isHasModel()
                ? productInformation.getModels().get(variationIndex).getModelAttributes().stream()
                .map(ProductInformation.ItemAttribute::isDisplay).toList()
                : productInformation.getItemAttributes().stream()
                .map(ProductInformation.ItemAttribute::isDisplay).toList();
    }

    /**
     * Retrieves the attribute names for a given variation index. If the product has no variations,
     * it returns the attribute names for the main attributes.
     *
     * @param productInformation Represents detailed information about a product, including pricing,
     *                           descriptions, shipping details, attributes, and stock information.
     *                           This object includes various nested classes to handle different aspects of the product,
     *                           such as models, categories, shipping info, and language-specific details.
     * @param variationIndex     The index of the variation for which to retrieve attribute names.
     *                           If the product has no variations, this parameter is ignored.
     * @return A list of attribute names as Strings.
     */
    public static List<String> getAttributeNames(ProductInformation productInformation, int variationIndex) {
        return productInformation.isHasModel()
                ? productInformation.getModels().get(variationIndex).getModelAttributes().stream()
                .map(ProductInformation.ItemAttribute::getAttributeName).toList()
                : productInformation.getItemAttributes().stream()
                .map(ProductInformation.ItemAttribute::getAttributeName).toList();
    }

    /**
     * Retrieves the attribute values for a given variation index. If the product has no variations,
     * it returns the attribute values for the main attributes.
     *
     * @param productInformation Represents detailed information about a product, including pricing,
     *                           descriptions, shipping details, attributes, and stock information.
     *                           This object includes various nested classes to handle different aspects of the product,
     *                           such as models, categories, shipping info, and language-specific details.
     * @param variationIndex     The index of the variation for which to retrieve attribute values.
     *                           If the product has no variations, this parameter is ignored.
     * @return A list of attribute values as Strings.
     */
    public static List<String> getAttributeValues(ProductInformation productInformation, int variationIndex) {
        return productInformation.isHasModel()
                ? productInformation.getModels().get(variationIndex).getModelAttributes().stream()
                .map(ProductInformation.ItemAttribute::getAttributeValue).toList()
                : productInformation.getItemAttributes().stream()
                .map(ProductInformation.ItemAttribute::getAttributeValue).toList();
    }

}
