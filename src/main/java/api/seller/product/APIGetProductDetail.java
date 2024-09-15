package api.seller.product;

import api.seller.login.APIDashboardLogin;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import utility.APIUtils;

import java.util.*;
import java.util.stream.Collectors;

import static api.seller.product.APIGetProductDetail.ProductInformation.MainLanguage;

/**
 * Provides functionality to retrieve and process product details from the API.
 * This class interacts with the API to fetch product details and provides utility methods to process
 * and analyze product data, including handling product variations and multi-language support.
 */
public class APIGetProductDetail {

    private final APIDashboardLogin.SellerInformation loginInfo;

    /**
     * Constructs an instance of APIGetProductDetail with the specified credentials.
     *
     * @param credentials The credentials used to authenticate with the API.
     */
    public APIGetProductDetail(APIDashboardLogin.Credentials credentials) {
        loginInfo = new APIDashboardLogin().getSellerInformation(credentials);
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
        private boolean deleted = true;
        private List<Model> models = new ArrayList<>();
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
        private boolean inGoSocial;
        private boolean enabledListing;
        private boolean isHideStock;
        private String inventoryManageType;
        private String bhStatus;
        private boolean lotAvailable;
        private boolean expiredQuality;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Category {
            private long id;
            private int cateId;
            private int level;
            private long itemId;
        }

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
        LogManager.getLogger().info("Get information of productId: {}", productId);

        return new APIUtils().get("/itemservice/api/beehive-items/%d".formatted(productId), loginInfo.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .as(ProductInformation.class);
    }

    /**
     * Creates a map of product names by language from the provided {@link ProductInformation}.
     *
     * @param productInformation Represents detailed information about a product, including its pricing,
     *                           descriptions, shipping details, attributes, and stock information.
     *                           It includes various nested classes to handle different aspects of the product,
     *                           such as models, categories, shipping info, and language-specific details.
     * @return A map where the key is the language code and the value is the product name in that language.
     */
    public static Map<String, String> getMainProductNameMap(ProductInformation productInformation) {
        return productInformation.getLanguages().stream()
                .collect(Collectors.toMap(MainLanguage::getLanguage,
                        MainLanguage::getName));
    }

    /**
     * Creates a map of product descriptions by language from the provided {@link ProductInformation}.
     *
     * @param productInformation Represents detailed information about a product, including its pricing,
     *                           descriptions, shipping details, attributes, and stock information.
     *                           It includes various nested classes to handle different aspects of the product,
     *                           such as models, categories, shipping info, and language-specific details.
     * @return A map where the key is the language code and the value is the product description in that language.
     * If a description is not available for a specific language, the value in the map will be null.
     */
    public static Map<String, String> getMainProductDescriptionMap(ProductInformation productInformation) {
        return productInformation.getLanguages().stream()
                .collect(Collectors.toMap(MainLanguage::getLanguage,
                        MainLanguage::getDescription));
    }


    /**
     * Creates a map of variation group names for a specific language from the provided {@link ProductInformation}.
     *
     * @param productInformation Represents detailed information about a product, including its pricing,
     *                           descriptions, shipping details, attributes, and stock information.
     *                           It includes various nested classes to handle different aspects of the product,
     *                           such as models, categories, shipping info, and language-specific details.
     * @param language           The specific language code for which to retrieve variation group names.
     * @return A map where the key is the language code and the value is the variation group name in that language.
     * If the specified language is not found, the map will be empty.
     */
    public static Map<String, String> getVariationGroupNameMap(ProductInformation productInformation, String language) {
        // Get the languages from the first model (assuming there is at least one model)
        var languages = productInformation.getModels().stream()
                .findFirst()
                .map(ProductInformation.Model::getLanguages)
                .orElse(Collections.emptyList());

        // Filter and collect the group names for the specified language
        return languages.stream()
                .filter(lang -> language.equals(lang.getLanguage()))
                .collect(Collectors.toMap(ProductInformation.Model.VersionLanguage::getLanguage,
                        ProductInformation.Model.VersionLanguage::getLabel));
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
     * Creates a list of variation model IDs.
     *
     * @param productInformation Represents detailed information about a product, including its pricing,
     *                           descriptions, shipping details, attributes, and stock information.
     *                           It includes various nested classes to handle different aspects of the product,
     *                           such as models, categories, shipping info, and language-specific details.
     * @return A list of variation model IDs.
     */
    public static List<Integer> getVariationModelList(ProductInformation productInformation) {
        return productInformation.getModels().stream()
                .map(ProductInformation.Model::getId)
                .toList();
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
     * Creates a list of variation statuses.
     *
     * @param productInformation Represents detailed information about a product, including its pricing,
     *                           descriptions, shipping details, attributes, and stock information.
     *                           It includes various nested classes to handle different aspects of the product,
     *                           such as models, categories, shipping info, and language-specific details.
     * @return A list of statuses for each variation model.
     */
    public static List<String> getVariationStatus(ProductInformation productInformation) {
        return productInformation.getModels().stream()
                .map(ProductInformation.Model::getStatus)
                .toList();
    }

    /**
     * Creates a map of product stock quantities by variation model ID.
     *
     * @param productInformation Represents detailed information about a product, including its pricing,
     *                           descriptions, shipping details, attributes, and stock information.
     *                           It includes various nested classes to handle different aspects of the product,
     *                           such as models, categories, shipping info, and language-specific details.
     * @return A map where the key is the variation model ID and the value is a list of stock quantities per branch.
     */
    public static Map<Integer, List<Integer>> getProductStockQuantityMap(ProductInformation productInformation) {
        Map<Integer, List<Integer>> stockMap = new HashMap<>();
        productInformation.getModels().forEach(model -> {
            List<Integer> variationStock = model.getBranches().stream()
                    .map(branchStock -> branchStock.getTotalItem() - branchStock.getSoldItem())
                    .toList();
            stockMap.put(model.getId(), variationStock);
        });
        return stockMap;
    }

    /**
     * Creates a map of version names by variation model ID and language.
     *
     * @param productInformation Represents detailed information about a product, including its pricing,
     *                           descriptions, shipping details, attributes, and stock information.
     *                           It includes various nested classes to handle different aspects of the product,
     *                           such as models, categories, shipping info, and language-specific details.
     * @param defaultName        The default name to use if a version-specific name is not provided.
     * @return A map where the key is the variation model ID and the value is another map of language codes to version names.
     */
    public static Map<Integer, Map<String, String>> getVersionNameMap(ProductInformation productInformation, String defaultName) {
        return productInformation.getModels().stream()
                .collect(Collectors.toMap(ProductInformation.Model::getId,
                        model -> model.getLanguages().stream()
                                .collect(Collectors.toMap(
                                        ProductInformation.Model.VersionLanguage::getLanguage,
                                        lang -> Optional.ofNullable(lang.getVersionName()).orElse(defaultName),
                                        (existing, _) -> existing))));
    }

    /**
     * Creates a map of version descriptions by variation model ID and language.
     *
     * @param productInformation Represents detailed information about a product, including its pricing,
     *                           descriptions, shipping details, attributes, and stock information.
     *                           It includes various nested classes to handle different aspects of the product,
     *                           such as models, categories, shipping info, and language-specific details.
     * @param defaultDescription The default description to use if a version-specific description is not provided.
     * @return A map where the key is the variation model ID and the value is another map of language codes to version descriptions.
     */
    public static Map<Integer, Map<String, String>> getVersionDescriptionMap(ProductInformation productInformation, String defaultDescription) {
        return productInformation.getModels().stream()
                .collect(Collectors.toMap(ProductInformation.Model::getId,
                        model -> model.getLanguages().stream()
                                .collect(Collectors.toMap(
                                        ProductInformation.Model.VersionLanguage::getLanguage,
                                        lang -> model.isUseProductDescription() ? defaultDescription : Optional.ofNullable(lang.getDescription()).orElse(defaultDescription),
                                        (existing, _) -> existing))));
    }

    /**
     * Creates a map of variation values by language.
     *
     * @param productInformation Represents detailed information about a product, including its pricing,
     *                           descriptions, shipping details, attributes, and stock information.
     *                           It includes various nested classes to handle different aspects of the product,
     *                           such as models, categories, shipping info, and language-specific details.
     * @return A map where the key is the language code and the value is a list of variation values in that language.
     */
    public static Map<String, List<String>> getVariationValuesMap(ProductInformation productInformation) {
        Map<String, List<String>> valuesMap = new HashMap<>();
        productInformation.getModels().forEach(model -> model.getLanguages().forEach(language ->
                valuesMap.computeIfAbsent(language.getLanguage(), _ -> new ArrayList<>()).add(language.getName())));
        return valuesMap;
    }
}
