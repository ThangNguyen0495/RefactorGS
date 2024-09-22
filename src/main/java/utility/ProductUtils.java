package utility;

import api.seller.login.APISellerLogin;
import api.seller.product.APIGetProductDetail.ProductInformation;
import api.seller.setting.APIGetBranchList;
import api.seller.setting.APIGetVATList;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static org.apache.commons.lang.math.JVMRandom.nextLong;
import static org.apache.commons.lang.math.RandomUtils.nextBoolean;
import static org.apache.commons.lang.math.RandomUtils.nextInt;

public class ProductUtils {
    private static final long MAX_PRICE = 99999999999L; // Max allowable price

    /**
     * Generates a ProductInformation object with various product details such as price, models,
     * branch stocks, SEO data, and more. This method includes several options to toggle features
     * like models, discount, SEO, stock visibility, etc.
     *
     * @param credentials    API credentials for authentication
     * @param langKey        Language key for localized fields
     * @param hasModel       Whether the product has multiple models/variations
     * @param noCost         Whether the product has no cost price
     * @param noDiscount     Whether the product has no discount price
     * @param manageByIMEI   Whether the product is managed by IMEI
     * @param hasSEO         Whether SEO fields should be populated
     * @param hasDimension   Whether the product has dimensional data
     * @param hasLot         Whether the product has lot availability information
     * @param hasAttribution Whether the product has item attributes
     * @param onWeb          Whether the product is available on the web
     * @param onApp          Whether the product is available on the app
     * @param inStore        Whether the product is available in physical stores
     * @param inGoSOCIAL     Whether the product is available in GoSOCIAL
     * @param branchStock    Array of stock quantities across branches
     * @return A populated ProductInformation object
     */
    public static ProductInformation generateProductInformation(APISellerLogin.Credentials credentials, String langKey,
                                                                boolean hasModel, boolean noCost, boolean noDiscount,
                                                                boolean manageByIMEI, boolean hasSEO, boolean hasDimension,
                                                                boolean hasLot, boolean hasAttribution, boolean onWeb, boolean onApp,
                                                                boolean inStore, boolean inGoSOCIAL, int... branchStock) {

        // Fetch list of branch IDs with status 'ACTIVE' using the APIGetBranchList.
        List<APIGetBranchList.BranchInformation> branchInfoList = new APIGetBranchList(credentials).getBranchInformation();
        List<Integer> branchIds = APIGetBranchList.getActiveBranchIds(branchInfoList);

        // Initialize the basic product information such as name, description, and default attributes
        ProductInformation productInfo = initializeProductInfo(langKey, hasModel, manageByIMEI, hasSEO,
                hasLot, branchStock, branchIds);

        // Generate random pricing values (organization price, new price, and cost price)
        long orgPrice = nextLong(MAX_PRICE);
        long newPrice = noDiscount ? orgPrice : nextLong(orgPrice);
        long costPrice = noCost ? 0 : nextLong(newPrice);

        // Set the prices into the ProductInformation object
        setPrices(productInfo, orgPrice, newPrice, costPrice);

        // Generate models and variations if the product has models/variations
        productInfo.setHasModel(hasModel);
        if (hasModel) {
            productInfo.setModels(generateModels(langKey, noDiscount, noCost, hasLot, branchStock, branchIds));
        }

        // If product has dimensions, set shipping info
        if (hasDimension) {
            productInfo.setShippingInfo(generateShippingInfo());
        }

        // Set remaining attributes like stock visibility, SEO, lot availability, and IMEI management
        setOtherAttributes(productInfo, credentials, hasAttribution, manageByIMEI, hasLot,
                onWeb, onApp, inStore, inGoSOCIAL);

        return productInfo;
    }

    /**
     * Initializes a ProductInformation object with basic product details such as name, description,
     * branch stocks, and SEO data (if applicable).
     *
     * @param langKey        Language key for localization
     * @param hasModel       Whether the product has multiple models
     * @param manageByIMEI   Whether the product is managed by IMEI
     * @param hasSEO         Whether SEO fields should be populated
     * @param branchStock    Array of branch stock quantities
     * @param branchIds      List of branch IDs
     * @return An initialized ProductInformation object
     */
    private static ProductInformation initializeProductInfo(String langKey, boolean hasModel, boolean manageByIMEI,
                                                            boolean hasSEO, boolean hasLot, int[] branchStock,
                                                            List<Integer> branchIds) {
        ProductInformation productInfo = new ProductInformation();

        // Construct the product name based on IMEI management and model/variation status
        String modelType = manageByIMEI ? "IMEI" : "PRODUCT";
        String variationStatus = hasModel ? "Variation" : "without variation";
        String productName = "[%s] Auto - %s - %s - %s".formatted(langKey, modelType, variationStatus, LocalDateTime.now().toString().substring(0, 19));
        String productDescription = "[%s] product description".formatted(langKey);

        productInfo.setId(nextInt(10000));
        productInfo.setName(productName);
        productInfo.setDescription(productDescription);
        productInfo.setHasModel(hasModel);

        // Generate language data for different regions or versions
        productInfo.setLanguages(generateMainLanguages(langKey, productName, productDescription, hasSEO));
        productInfo.setBranches(generateBranchStocks(branchIds, hasLot, branchStock));

        return productInfo;
    }

    /**
     * Sets product pricing information (original, new, and cost price).
     *
     * @param productInfo The ProductInformation object
     * @param orgPrice    The original price of the product
     * @param newPrice    The new price after discount (if applicable)
     * @param costPrice   The cost price of the product
     */
    private static void setPrices(ProductInformation productInfo, long orgPrice, long newPrice, long costPrice) {
        productInfo.setOrgPrice(orgPrice);
        productInfo.setNewPrice(newPrice);
        productInfo.setCostPrice(costPrice);
    }

    /**
     * Generates a list of models for the product, including variations and branch stock data.
     *
     * @param langKey     Language key for localization
     * @param noDiscount  Whether the product has no discount
     * @param noCost      Whether the product has no cost price
     * @param branchStock Array of branch stock quantities
     * @param branchIds   List of branch IDs
     * @return List of ProductInformation.Model objects
     */
    private static List<ProductInformation.Model> generateModels(String langKey, boolean noDiscount, boolean noCost, boolean hasLot,
                                                                 int[] branchStock, List<Integer> branchIds) {
        List<ProductInformation.Model> models = new ArrayList<>();
        Map<String, List<String>> variationMap = VariationUtils.randomVariationMap(langKey);
        List<String> variationList = VariationUtils.getVariationList(variationMap);
        String variationGroup = VariationUtils.getVariationName(variationMap);

        // Iterate through the variations and create models for each
        IntStream.range(0, variationList.size()).forEach(variationIndex -> models.add(createModel(variationList.get(variationIndex), variationGroup, branchIds, hasLot, branchStock, langKey,
                noDiscount, noCost)));

        return models;
    }

    /**
     * Creates a model object with variation data, pricing, branch stocks, and language-specific data.
     *
     * @param variationName  The variation name (e.g., color, size)
     * @param variationGroup The group label for the variation (e.g., "Color", "Size")
     * @param branchIds      List of branch IDs
     * @param branchStock    Array of branch stock quantities
     * @param langKey        Language key for localization
     * @param noDiscount     Whether the product has no discount
     * @param noCost         Whether the product has no cost price
     * @return A populated ProductInformation.Model object
     */
    private static ProductInformation.Model createModel(String variationName, String variationGroup, List<Integer> branchIds, boolean hasLot,
                                                        int[] branchStock, String langKey, boolean noDiscount, boolean noCost) {
        ProductInformation.Model model = new ProductInformation.Model();
        model.setId(nextInt(10000));
        model.setName(variationName);
        model.setOrgPrice(nextLong(MAX_PRICE));
        model.setNewPrice(noDiscount ? model.getOrgPrice() : nextLong(model.getOrgPrice()));
        model.setLabel(variationGroup);
        model.setBranches(generateBranchStocks(branchIds, hasLot, branchStock));
        model.setLanguages(generateVersionLanguages(langKey, variationName, variationGroup));
        model.setCostPrice(noCost ? 0 : model.getNewPrice());
        return model;
    }

    /**
     * Generates branch stock data for the product.
     *
     * @param branchIds   List of branch IDs
     * @param branchStock Array of branch stock quantities
     * @return List of ProductInformation.BranchStock objects
     */
    private static List<ProductInformation.BranchStock> generateBranchStocks(List<Integer> branchIds, boolean hasLot, int[] branchStock) {
        List<ProductInformation.BranchStock> branchStocks = new ArrayList<>();

        // Assign stock quantities to each branch
        IntStream.range(0, branchIds.size()).forEach(branchIndex -> {
            ProductInformation.BranchStock stock = new ProductInformation.BranchStock();
            stock.setBranchId(branchIds.get(branchIndex));
            stock.setTotalItem((hasLot || branchIndex >= branchStock.length) ? 0 : branchStock[branchIndex]);
            stock.setSoldItem(0);
            branchStocks.add(stock);
        });
        return branchStocks;
    }

    /**
     * Sets other attributes like SEO, IMEI management, and lot availability for the product.
     *
     * @param productInfo    The ProductInformation object
     * @param credentials    API credentials
     * @param hasAttribution Whether the product has item attributes
     * @param manageByIMEI   Whether the product is managed by IMEI
     * @param hasLot         Whether the product has lot availability information
     * @param onWeb          Whether the product is available on the web
     * @param onApp          Whether the product is available on the app
     * @param inStore        Whether the product is available in physical stores
     * @param inGoSOCIAL     Whether the product is available in GoSOCIAL
     */
    private static void setOtherAttributes(ProductInformation productInfo, APISellerLogin.Credentials credentials, boolean hasAttribution,
                                           boolean manageByIMEI, boolean hasLot, boolean onWeb, boolean onApp,
                                           boolean inStore, boolean inGoSOCIAL) {
        var vatInfoList = new APIGetVATList(credentials).getVATInformation();
        var vatNames = APIGetVATList.getVATNames(vatInfoList);

        // Set remaining attributes like SEO and stock management
        productInfo.setTaxId(nextInt(100));
        productInfo.setTaxName(vatNames.get(nextInt(vatNames.size())));
        productInfo.setOnApp(onApp);
        productInfo.setOnWeb(onWeb);
        productInfo.setInStore(inStore);
        productInfo.setInGosocial(inGoSOCIAL);
        productInfo.setInventoryManageType(manageByIMEI ? "IMEI_SERIAL_NUMBER" : "PRODUCT");
        productInfo.setBhStatus("ACTIVE");
        productInfo.setLotAvailable(hasLot);
        productInfo.setExpiredQuality(nextBoolean());

        if (hasAttribution) {
            productInfo.setItemAttributes(generateItemAttributes(nextInt(10)));
        }
    }

    /**
     * Generates shipping info for the product if dimensional data is available.
     *
     * @return A populated ProductInformation.ShippingInfo object
     */
    private static ProductInformation.ShippingInfo generateShippingInfo() {
        ProductInformation.ShippingInfo shippingInfo = new ProductInformation.ShippingInfo();
        shippingInfo.setWeight(nextInt(100));
        shippingInfo.setWidth(nextInt(100));
        shippingInfo.setHeight(nextInt(100));
        shippingInfo.setLength(nextInt(100));
        return shippingInfo;
    }

    /**
     * Generates language-specific data for the main product information.
     *
     * @param langKey            Language key for localization
     * @param productName        The product name
     * @param productDescription The product description
     * @param hasSEO             Whether SEO fields should be populated
     * @return List of ProductInformation.MainLanguage objects
     */
    private static List<ProductInformation.MainLanguage> generateMainLanguages(String langKey, String productName,
                                                                               String productDescription, boolean hasSEO) {
        List<ProductInformation.MainLanguage> languages = new ArrayList<>();
        ProductInformation.MainLanguage language = new ProductInformation.MainLanguage();
        language.setLanguage(langKey);
        language.setName(productName);
        language.setDescription(productDescription);

        if (hasSEO) {
            long timeStamp = Instant.now().toEpochMilli();
            language.setSeoTitle("SEO Title " + timeStamp);
            language.setSeoDescription("SEO Description " + timeStamp);
            language.setSeoKeywords("SEO Keywords " + timeStamp);
            language.setSeoUrl("seo-url-" + timeStamp);
        }

        languages.add(language);
        return languages;
    }

    /**
     * Generates item attributes for the product, such as color, size, etc.
     *
     * @param numberOfAttribution The number of attributes to generate
     * @return List of ProductInformation.ItemAttribute objects
     */
    private static List<ProductInformation.ItemAttribute> generateItemAttributes(int numberOfAttribution) {
        List<ProductInformation.ItemAttribute> attributes = new ArrayList<>();
        IntStream.rangeClosed(1, numberOfAttribution).forEach(attributeIndex -> {
            ProductInformation.ItemAttribute attribute = new ProductInformation.ItemAttribute();
            attribute.setAttributeName("Attribute Name " + attributeIndex);
            attribute.setAttributeValue("Attribute Value " + attributeIndex);
            attribute.setDisplay(nextBoolean());
            attributes.add(attribute);
        });
        return attributes;
    }

    /**
     * Generates language-specific data for model versions.
     *
     * @param langKey    Language key for localization
     * @param modelName  The name of the model
     * @param modelLabel The label for the variation (e.g., color, size)
     * @return List of ProductInformation.Model.VersionLanguage objects
     */
    private static List<ProductInformation.Model.VersionLanguage> generateVersionLanguages(String langKey, String modelName,
                                                                                           String modelLabel) {
        List<ProductInformation.Model.VersionLanguage> languages = new ArrayList<>();
        ProductInformation.Model.VersionLanguage language = new ProductInformation.Model.VersionLanguage();
        language.setLanguage(langKey);
        language.setLabel(modelLabel);
        language.setName(modelName);
        languages.add(language);
        return languages;
    }
}
