package utility.helper;

import api.seller.product.APIGetProductDetail;
import api.seller.product.APIGetProductDetail.ProductInformation.Model.VersionLanguage;
import api.seller.product.APIGetProductDetail.ProductInformation.ShippingInfo;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static api.seller.product.APIGetProductDetail.ProductInformation;
import static org.apache.commons.lang.math.JVMRandom.nextLong;
import static org.apache.commons.lang.math.RandomUtils.nextBoolean;
import static org.apache.commons.lang.math.RandomUtils.nextInt;

/**
 * Helper class to generate mock product information for testing or simulation purposes.
 */
public class ProductHelper {
    public static long MAX_PRICE = 99_999_999_999L;

    /**
     * Holds initialization parameters for generating product information.
     */
    @Data
    public static class InitProductInfo {
        private ProductInformation currentProductInfo; // Existing product info for updates
        private boolean hasModel;                        // Indicates if the product has variations
        private boolean noCost;                         // Indicates if the product should have zero cost
        private boolean noDiscount;                     // Indicates if the product has no discount
        private boolean manageByIMEI;                   // Indicates inventory management by IMEI
        private boolean hasSEO;                          // Indicates if SEO attributes are present
        private boolean hasDimension;                    // Indicates if dimension info is included
        private boolean hasLot;                          // Indicates if the product is managed by lot
        private boolean hasAttribution;                  // Indicates custom attributes for the product
        private boolean onWeb;                           // Visibility on web
        private boolean onApp;                           // Visibility on app
        private boolean inStore;                         // Visibility in store
        private boolean inGoSOCIAL;                     // Visibility in GoSOCIAL
        private List<Integer> allBranchesIds;           // All branch IDs where the product is available
        private List<Integer> activeBranchIds;          // Active branch IDs where product is in stock
        private List<String> langCodes;                 // Available language codes for the product
        private String defaultLangCode;                  // Default language code
        private List<Integer> vatIds;                    // List of VAT IDs
        private List<String> vatNames;                   // List of VAT names
        private int[] branchStock;                       // Stock quantities for branches
        private boolean changStatus;                     // Change product/variation status
    }


    /**
     * Generates a new {@link ProductInformation} object based on the provided {@link InitProductInfo} parameters.
     * It builds the product details including models, stock information, item attributes, and visibility settings.
     *
     * @param initProductInfo An instance of {@link InitProductInfo} containing the necessary parameters to generate the product information.
     * @return A fully populated {@link ProductInformation} object with the specified attributes and settings.
     */
    public static ProductInformation generateProductInformation(InitProductInfo initProductInfo) {
        // Case 1: Edit product/variation translation
        if (initProductInfo.getLangCodes() != null) {
            var newProductInfo = initProductInfo.getCurrentProductInfo();
            boolean hasModel = newProductInfo.isHasModel();
            String defaultLanguage = initProductInfo.getDefaultLangCode();
            boolean isManagedInventoryByIMEI = newProductInfo.getInventoryManageType().equals("IMEI_SERIAL_NUMBER");

            // Generate main languages for the product
            newProductInfo.setLanguages(generateMainLanguages(initProductInfo.getLangCodes(),
                    defaultLanguage, hasModel, isManagedInventoryByIMEI, true));
            newProductInfo.setName(APIGetProductDetail.getMainProductName(newProductInfo, defaultLanguage));
            newProductInfo.setDescription(APIGetProductDetail.getMainProductDescription(newProductInfo, defaultLanguage));

            // Generate languages for models if the product has variations
            if (newProductInfo.isHasModel()) {
                List<ProductInformation.Model> models = new ArrayList<>();
                newProductInfo.getModels().forEach(model -> {
                    // Generate localized languages for the model
                    model.setLanguages(generateVersionLanguages(model.getLabel(), model.getName(),
                            false, "",
                            initProductInfo.getLangCodes(), defaultLanguage));

                    // Set version name and description
                    model.setVersionName(model.getLanguages().getFirst().getVersionName());
                    model.setDescription(model.getLanguages().getFirst().getDescription());

                    models.add(model);
                });
                newProductInfo.setModels(models);
            }
            return newProductInfo; // Return updated product info with translations
        }

        // Case 2: Change product/variation status
        if (initProductInfo.isChangStatus()) {
            var newProductInfo = initProductInfo.getCurrentProductInfo();
            // Invert the current status of the product
            newProductInfo.setBhStatus(initProductInfo.getCurrentProductInfo().getBhStatus().equals("ACTIVE") ? "INACTIVE" : "ACTIVE");

            // Update the status of models if the product has variations
            if (newProductInfo.isHasModel()) {
                List<ProductInformation.Model> models = new ArrayList<>();
                newProductInfo.getModels().forEach(model -> {
                    model.setStatus(nextBoolean() ? "ACTIVE" : "INACTIVE");
                    models.add(model);
                });
                newProductInfo.setModels(models);
            }
            return newProductInfo; // Return updated product info with status changes
        }

        // Case 3: Add product/variation attribution
        if (initProductInfo.getCurrentProductInfo() != null && initProductInfo.isHasAttribution()) {
            var newProductInfo = initProductInfo.getCurrentProductInfo();
            // Update item attributes for the product
            newProductInfo.setItemAttributes(generateItemAttributes(true));

            // Update model attributes if the product has variations
            if (newProductInfo.isHasModel()) {
                List<ProductInformation.Model> models = new ArrayList<>();
                newProductInfo.getModels().forEach(model -> {
                    model.setModelAttributes(generateItemAttributes(true));
                    models.add(model);
                });
                newProductInfo.setModels(models);
            }
            return newProductInfo; // Return updated product info with attributions
        }

        // Case 4: Create/edit basic product information
        // Determine if inventory is managed by IMEI based on the current product info or the parameter
        boolean manageInventoryByIMEI = ((initProductInfo.getCurrentProductInfo() != null)
                                         && initProductInfo.getCurrentProductInfo().getInventoryManageType().equals("IMEI_SERIAL_NUMBER"))
                                        || initProductInfo.isManageByIMEI();

        // Build the basic product information using the provided parameters
        ProductInformation newProductInfo = buildBasicProductInfo(
                initProductInfo.getCurrentProductInfo(),
                initProductInfo.getDefaultLangCode(),
                initProductInfo.isHasModel(),
                initProductInfo.isNoCost(),
                initProductInfo.isNoDiscount(),
                manageInventoryByIMEI,
                initProductInfo.isHasLot(),
                initProductInfo.getVatIds(),
                initProductInfo.getVatNames());

        // Set the ID for the new product info if the current product info exists
        if (initProductInfo.getCurrentProductInfo() != null) {
            newProductInfo.setId(initProductInfo.getCurrentProductInfo().getId());
        }

        // Generate shipping info based on whether dimensions are available
        newProductInfo.setShippingInfo(generateShippingInfo(initProductInfo.isHasDimension()));

        // Set languages based on the provided language codes and product attributes
        newProductInfo.setLanguages(generateMainLanguages(initProductInfo.getLangCodes(), initProductInfo.getDefaultLangCode(),
                initProductInfo.isHasModel(), manageInventoryByIMEI, initProductInfo.isHasSEO()));

        // Generate branch stock information if necessary
        newProductInfo.setBranches(generateBranchStocksIfNecessary(initProductInfo.getCurrentProductInfo(),
                initProductInfo.isHasModel(), initProductInfo.getAllBranchesIds(),
                initProductInfo.getActiveBranchIds(), initProductInfo.isHasLot(),
                initProductInfo.getBranchStock()));

        // Generate item attributes based on whether custom attributes are needed
        newProductInfo.setItemAttributes(generateItemAttributes(initProductInfo.isHasAttribution()));

        // Conditionally generate models (variations) for the product
        newProductInfo.setModels(generateModelsIfNecessary(initProductInfo.getCurrentProductInfo(), newProductInfo,
                initProductInfo.isHasModel(), initProductInfo.isNoCost(),
                initProductInfo.isNoDiscount(), initProductInfo.isHasAttribution(),
                initProductInfo.getLangCodes(), initProductInfo.getDefaultLangCode(),
                initProductInfo.getAllBranchesIds(), initProductInfo.getActiveBranchIds(),
                initProductInfo.getBranchStock()));

        // Set visibility flags for the product in various channels
        setVisibilityFlags(newProductInfo, initProductInfo.isOnApp(), initProductInfo.isOnWeb(),
                initProductInfo.isInStore(), initProductInfo.isInGoSOCIAL());

        // Additional stock visibility settings
        newProductInfo.setShowOutOfStock(nextBoolean()); // Randomly decide whether to show out-of-stock products
        newProductInfo.setIsHideStock(nextBoolean());    // Randomly decide whether to hide stock information
        newProductInfo.setPriority(nextInt(100));        // Set a random priority for the product

        return newProductInfo; // Return the fully populated ProductInformation object
    }

    /**
     * Builds the basic product information, including essential details like name, price, inventory management type, and VAT details.
     *
     * @param currentProductInfo The current product information. If null, a new product is being created.
     * @param defaultLangCode    The default language code for the product name and description.
     * @param hasModel           Indicates if the product has models (variations).
     * @param noCost             Indicates if the product has no associated cost.
     * @param noDiscount         Indicates if the product has no discount applied.
     * @param manageByIMEI       Indicates if the product is managed by IMEI or serial number.
     * @param hasLot             Indicates if the product is managed by lot.
     * @param vatIds             List of available VAT IDs for tax purposes.
     * @param vatNames           List of corresponding VAT names for the available VAT IDs.
     * @return A basic {@link ProductInformation} object with key attributes such as price, name, and VAT details populated.
     */
    private static ProductInformation buildBasicProductInfo(
            ProductInformation currentProductInfo, String defaultLangCode, boolean hasModel, boolean noCost,
            boolean noDiscount, boolean manageByIMEI, boolean hasLot, List<Integer> vatIds, List<String> vatNames) {

        ProductInformation newProductInfo = new ProductInformation();

        // Set product model status
        newProductInfo.setHasModel(hasModel);

        // Determine inventory management type
        newProductInfo.setInventoryManageType(manageByIMEI ? "IMEI_SERIAL_NUMBER" : "PRODUCT");

        // Check if lot management is available
        newProductInfo.setLotAvailable((!manageByIMEI && hasLot) || (currentProductInfo != null && currentProductInfo.isLotAvailable()));

        // Set expired quality based on current product info or randomly
        newProductInfo.setExpiredQuality((currentProductInfo != null && currentProductInfo.isExpiredQuality()) ||
                                         (newProductInfo.isLotAvailable() && nextBoolean()));

        // Generate product name and description
        newProductInfo.setName(generateProductName(defaultLangCode, hasModel, manageByIMEI, newProductInfo.isLotAvailable()));
        newProductInfo.setDescription("[%s] Product description %s".formatted(defaultLangCode, LocalDateTime.now()));

        // Set original and new price
        newProductInfo.setOrgPrice(nextLong(MAX_PRICE));
        newProductInfo.setNewPrice(noDiscount ? newProductInfo.getOrgPrice() : nextLong(newProductInfo.getOrgPrice()));

        // Set cost price, zero if no cost
        newProductInfo.setCostPrice(noCost ? 0 : nextLong(newProductInfo.getNewPrice()));

        // Randomly select and assign VAT details to the product
        int vatIndex = nextInt(vatIds.size()); // Randomly select an index from the available VAT IDs
        newProductInfo.setTaxId(vatIds.get(vatIndex)); // Set the selected VAT ID
        newProductInfo.setTaxName(vatNames.get(vatIndex)); // Set the corresponding VAT name

        return newProductInfo;
    }

    /**
     * Generates a product name based on several factors like language, model availability, and inventory type.
     *
     * @param defaultLangCode The default language code.
     * @param hasModel        Indicates if the product has models.
     * @param manageByIMEI    Indicates if the product is managed by IMEI or serial number.
     * @return A formatted product name that includes the default language, model information, and current timestamp.
     */
    private static String generateProductName(String defaultLangCode, boolean hasModel, boolean manageByIMEI, boolean lotAvailable) {
        // Determine variation text based on model availability
        String variationText = hasModel ? "Variation" : "Without variation";

        // Determine inventory management text based on management type
        String manageText = manageByIMEI ? "IMEI" : "Normal";

        String lotText = lotAvailable ? "LOT" : "NoLOT";

        // Format and return the product name with relevant details and timestamp
        return "[%s][%s][%s] Product name - %s - %s".formatted(
                defaultLangCode,
                variationText,
                lotText,
                manageText,
                LocalDateTime.now().toString().substring(0, 19)
        );
    }

    /**
     * Generates shipping information such as weight and dimensions.
     *
     * @param hasDimension Indicates if the product has dimensions for shipping.
     * @return A {@link ShippingInfo} object with randomly generated dimensions if applicable; otherwise, all dimensions are set to zero.
     */
    private static ShippingInfo generateShippingInfo(boolean hasDimension) {
        // Generate dimensions based on whether the product has dimensions
        return new ShippingInfo(
                hasDimension ? nextInt(100) : 0, // Length
                hasDimension ? nextInt(100) : 0, // Width
                hasDimension ? nextInt(100) : 0, // Height
                hasDimension ? nextInt(100) : 0  // Weight
        );
    }

    /**
     * Conditionally generates models (variations) for the product if applicable.
     * If the product does not have models, it returns an empty list. Otherwise,
     * it generates the product's variations based on the provided parameters.
     *
     * @param currentProductInfo The current product information. If null, a new product is being created.
     * @param newProductInfo     The new {@link ProductInformation} object being created or updated.
     * @param hasModel           Indicates if the product has variations (models). If false, no models are generated.
     * @param noCost             Indicates whether the product or its models should have zero cost price.
     * @param noDiscount         Indicates whether the product or its models should not have any discount applied.
     * @param hasAttribution     Indicates if the product or its models should include custom attributes.
     * @param langCodes          A list of language codes available for the product.
     * @param defaultLangCode    The default language code to be used for naming and descriptions.
     * @param allBranchesIds     A list of all branch IDs where the product is available.
     * @param activeBranchIds    A list of active branch IDs where the product currently has stock.
     * @param branchStock        Stock quantities for the product across different branches, provided as variable-length arguments.
     * @return A list of {@link ProductInformation.Model} objects representing the product's variations.
     * If the product does not have models, it returns an empty list.
     */
    private static List<ProductInformation.Model> generateModelsIfNecessary(ProductInformation currentProductInfo, ProductInformation newProductInfo, boolean hasModel,
                                                                            boolean noCost, boolean noDiscount, boolean hasAttribution,
                                                                            List<String> langCodes, String defaultLangCode,
                                                                            List<Integer> allBranchesIds, List<Integer> activeBranchIds, int... branchStock) {
        // Return an empty list if there are no models
        if (!hasModel) return java.util.List.of();
        // Generate and return the models based on the provided parameters
        return generateModels(currentProductInfo, newProductInfo, noDiscount, noCost, hasAttribution, langCodes, defaultLangCode, allBranchesIds, activeBranchIds, branchStock);
    }


    /**
     * Generates the models (variations) for the product, including details such as pricing, stock, and custom attributes.
     * This method creates variations of the product based on random selections from a variation map and the provided parameters.
     *
     * @param currentProductInfo The current {@link ProductInformation} object containing existing product details.
     * @param newProductInfo     The new {@link ProductInformation} object being created or updated.
     * @param noDiscount         Indicates whether the product should have no discount applied (true if no discount).
     * @param noCost             Indicates whether the product should be marked with zero cost (true if zero cost).
     * @param hasAttribution     Indicates whether the product has custom attributes to include in the variations.
     * @param langCodes          A list of language codes representing the available languages for the product variations.
     * @param defaultLangCode    The default language code to use if no specific language is provided.
     * @param allBranchesIds     A list of all branch IDs where the product is available.
     * @param activeBranchIds    A list of branch IDs where the product currently has stock.
     * @param branchStock        Stock quantities for the product across different branches, provided as variable-length arguments.
     * @return A list of {@link ProductInformation.Model} objects representing the generated variations of the product.
     * Each model contains specific details such as pricing, stock levels, and attributes.
     */
    private static List<ProductInformation.Model> generateModels(ProductInformation currentProductInfo, ProductInformation newProductInfo, boolean noDiscount, boolean noCost,
                                                                 boolean hasAttribution, List<String> langCodes, String defaultLangCode,
                                                                 List<Integer> allBranchesIds, List<Integer> activeBranchIds, int... branchStock) {
        // Retrieve the variation map based on the current product information and default language code
        Map<String, List<String>> variationMap = getVariationMap(currentProductInfo, defaultLangCode);

        // Get the variation name and values from the variation map
        String variationName = VariationHelper.getVariationName(variationMap);
        List<String> variationValues = VariationHelper.getVariationValues(variationMap);

        List<ProductInformation.Model> models = new ArrayList<>();

        // Iterate through variation values and generate a model for each
        IntStream.range(0, variationValues.size()).forEach(variationIndex ->
                models.add(generateModel(currentProductInfo, variationName, variationValues.get(variationIndex),
                        newProductInfo, noDiscount, noCost, hasAttribution, langCodes, defaultLangCode, allBranchesIds, activeBranchIds, branchStock, variationIndex)));

        return models; // Return the list of generated models
    }

    /**
     * Retrieves the variation map for a given product, including the variation names and values.
     * If the product does not have lot management or is null, a random variation map is generated.
     *
     * @param currentProductInfo The current {@link ProductInformation} object containing details about the product.
     * @param defaultLangCode    The default language code to be used when fetching variation names and values.
     * @return A map where the keys are variation names and the values are lists of corresponding variation values.
     * If the product is not available or does not support lot management, a random variation map is returned.
     */
    private static Map<String, List<String>> getVariationMap(ProductInformation currentProductInfo, String defaultLangCode) {
        // Check if the current product information is null or does not support lot management
        if ((currentProductInfo == null || !currentProductInfo.isLotAvailable()))
            return VariationHelper.randomVariationMap(defaultLangCode); // Generate and return a random variation map

        // Retrieve variation name and values from the product details
        var variationName = APIGetProductDetail.getVariationName(currentProductInfo, defaultLangCode);
        var variationValues = APIGetProductDetail.getVariationValues(currentProductInfo, defaultLangCode);

        // Construct and return the variation map with the retrieved names and values
        return VariationHelper.getVariationMap(variationName, variationValues);
    }

    /**
     * Generates a single model for the product, encapsulating details such as pricing, stock, and custom attributes.
     *
     * @param currentProductInfo The current {@link ProductInformation} object containing details about the product.
     * @param variationName      The name of the variation (e.g., "Color").
     * @param variationValue     The specific value of the variation (e.g., "Red").
     * @param productInfo        The base {@link ProductInformation} object containing the product's details.
     * @param noDiscount         Indicates if the product is to be created without any discount applied (true if no discount).
     * @param noCost             Indicates if the product should be marked with no cost (true if zero cost).
     * @param hasAttribution     Indicates whether the product has custom attributes to include in the model.
     * @param langCodes          A list of language codes representing the languages available for the product model.
     * @param defaultLangCode    The default language code to use if no specific language is provided.
     * @param allBranchesIds     A list of all branch IDs where the product is available.
     * @param activeBranchIds    A list of branch IDs where the product is currently in stock.
     * @param branchStock        Stock quantities for the product across different branches, provided as an array.
     * @param index              The index of the current model, used for identification purposes.
     * @return A populated {@link ProductInformation.Model} object representing the generated product variation,
     * including pricing, stock information, and attributes.
     */
    private static ProductInformation.Model generateModel(ProductInformation currentProductInfo, String variationName, String variationValue, ProductInformation productInfo,
                                                          boolean noDiscount, boolean noCost, boolean hasAttribution,
                                                          List<String> langCodes, String defaultLangCode, List<Integer> allBranchesIds, List<Integer> activeBranchIds,
                                                          int[] branchStock, int index) {
        ProductInformation.Model model = new ProductInformation.Model();

        // Set the model's unique ID and variation name
        model.setId(index);
        model.setName(variationValue);
        model.setVersionName("[%s] Version name %s".formatted(variationValue, LocalDateTime.now()));
        model.setSku("%s_SKU%s".formatted(model.getName(), Instant.now().toEpochMilli()));

        // Set original, new, and cost prices for the model
        model.setOrgPrice(nextLong(MAX_PRICE)); // Generate a random original price
        model.setNewPrice(noDiscount ? model.getOrgPrice() : nextLong(model.getOrgPrice())); // Determine new price based on discount
        model.setCostPrice(noCost ? 0 : nextLong(model.getNewPrice())); // Set cost price based on noCost flag

        // Set variation label and stock information
        model.setLabel(variationName);
        model.setBranches(generateBranchStocks(currentProductInfo, true, variationValue, allBranchesIds, activeBranchIds, productInfo.isLotAvailable(), branchStock));

        // Assign status and description usage flag
        model.setStatus("ACTIVE"); // Set model status
        model.setUseProductDescription(nextBoolean()); // Determine if product description should be used
        model.setDescription(model.isUseProductDescription()
                ? productInfo.getDescription()
                : "[%s] Version description %s".formatted(variationValue, LocalDateTime.now()));

        // Generate languages for the model based on the provided variation details
        model.setLanguages(generateVersionLanguages(variationName, variationValue, model.isUseProductDescription(), productInfo.getDescription(), langCodes, defaultLangCode));

        // Randomly decide whether to reuse attributes and set them accordingly
        model.setReuseAttributes(nextBoolean());
        model.setModelAttributes(model.isReuseAttributes() ? productInfo.getItemAttributes() : generateItemAttributes(hasAttribution));

        return model; // Return the constructed model
    }

    /**
     * Generates a localized VersionLanguage for a specific language.
     *
     * @param variationName      The name of the variation (e.g., "Color").
     * @param variationValue     The value of the variation (e.g., "Red").
     * @param reuseDescription   If true, reuses the default product description for the default language.
     * @param defaultDescription The default product description, used if reuseDescription is true.
     * @param languageKey        The current language key for which to generate the localized version.
     * @param defaultLanguage    The code of the default language (e.g., "en").
     * @return A localized VersionLanguage object for the specified language.
     */
    private static VersionLanguage generateVersionLanguage(
            String variationName, String variationValue, boolean reuseDescription,
            String defaultDescription, String languageKey, String defaultLanguage) {

        // Replace the default language code in variationValue and variationName with the current language key
        String localizedValue = variationValue.replaceAll(defaultLanguage, languageKey);
        String localizedName = variationName.replaceAll(defaultLanguage, languageKey);

        // Determine whether to reuse the default description or generate a new one
        String description = reuseDescription && languageKey.equals(defaultLanguage)
                ? defaultDescription
                : "[%s][%s] Version description ".formatted(languageKey, localizedValue)
                  + LocalDateTime.now().toString().substring(0, 19);

        // Generate a localized version name with the current timestamp
        String versionName = "[%s][%s] Version name ".formatted(languageKey, localizedValue)
                             + LocalDateTime.now().toString().substring(0, 19);

        // Create and return the VersionLanguage object for the current language
        return new VersionLanguage(languageKey, localizedValue, localizedName, description, versionName);
    }

    /**
     * Generates localized version languages for models, with an option to reuse the default description.
     *
     * @param variationName      The name of the variation (e.g., "Color").
     * @param variationValue     The value of the variation (e.g., "Red").
     * @param reuseDescription   If true, reuses the default product description for the default language.
     * @param defaultDescription The default product description, used if reuseDescription is true.
     * @param languageKeys       A list of language codes (e.g., ["en", "fr", "de"]) for which to generate localized versions.
     *                           This should only be provided when editing translations.
     * @param defaultLanguage    The code of the default language (e.g., "en").
     * @return A list of localized VersionLanguage objects for the model. If languageKeys is null, returns a list containing
     * a single VersionLanguage object using the default language.
     */
    private static List<VersionLanguage> generateVersionLanguages(
            String variationName, String variationValue, boolean reuseDescription,
            String defaultDescription, List<String> languageKeys, String defaultLanguage) {

        // Check if languageKeys is null, which indicates that no translation editing is required
        if (languageKeys == null) {
            // Generate and return a single VersionLanguage object for the default language
            return List.of(generateVersionLanguage(variationName, variationValue, reuseDescription,
                    defaultDescription, defaultLanguage, defaultLanguage));
        }

        // For each language key, generate a VersionLanguage object by calling generateVersionLanguage
        return languageKeys.stream()
                .map(languageKey -> generateVersionLanguage(variationName, variationValue, reuseDescription,
                        defaultDescription, languageKey, defaultLanguage))
                .toList(); // Collect the generated VersionLanguage objects into a list
    }

    /**
     * Conditionally generates branch stock information based on the presence of product models.
     *
     * @param currentProductInfo The current {@link ProductInformation} object containing details about the product.
     * @param hasModel           Indicates if the product has models (true if it has models).
     * @param allBranchesIds     List of all branch IDs where the product is available.
     * @param activeBranchIds    List of branch IDs where the product is currently in stock.
     * @param hasLot             Indicates if the product is managed by lot (true if managed by lot).
     * @param branchStock        Stock quantities for branches, provided as a variable-length argument.
     * @return A list of {@link ProductInformation.Branch} objects representing stock information for the product.
     * Returns an empty list if the product has models.
     */
    private static List<ProductInformation.Branch> generateBranchStocksIfNecessary(ProductInformation currentProductInfo, boolean hasModel, List<Integer> allBranchesIds, List<Integer> activeBranchIds, boolean hasLot, int... branchStock) {
        return hasModel ? java.util.List.of() : generateBranchStocks(currentProductInfo, false, "", allBranchesIds, activeBranchIds, hasLot, branchStock);
    }

    /**
     * Generates branch stock information for the product based on availability and stock levels.
     *
     * @param currentProductInfo The current {@link ProductInformation} object containing details about the product.
     * @param hasModel           Indicates if the product has models (true if it has models).
     * @param variationValue     The value of the variation (e.g., "Red"). This may be an empty string if not applicable.
     * @param allBranchesIds     List of all branch IDs where the product is available.
     * @param activeBranchIds    List of branch IDs where the product is currently in stock.
     * @param hasLot             Indicates if the product is managed by lot (true if managed by lot).
     * @param branchStock        Stock quantities for branches, provided as a variable-length argument.
     * @return A list of {@link ProductInformation.Branch} objects representing stock information for the product.
     */
    private static List<ProductInformation.Branch> generateBranchStocks(ProductInformation currentProductInfo, boolean hasModel, String variationValue, List<Integer> allBranchesIds, List<Integer> activeBranchIds, boolean hasLot, int... branchStock) {
        // When currentProductInfo is null or is not managed stock by lot
        // Create and return branch stock information based on the available branch IDs and stock quantities.
        if (currentProductInfo == null || !currentProductInfo.isLotAvailable()) {
            return IntStream.range(0, allBranchesIds.size())
                    .mapToObj(index -> new ProductInformation.Branch(
                            allBranchesIds.get(index),
                            activeBranchIds.contains(allBranchesIds.get(index)) && !hasLot && index < branchStock.length
                                    ? branchStock[index] // Assign stock from branchStock if conditions are met
                                    : 0,               // Otherwise, assign zero stock
                            0,
                            generateSKU(hasModel, variationValue, activeBranchIds, allBranchesIds.get(index)),
                            activeBranchIds.contains(allBranchesIds.get(index)) ? "ACTIVE" : "INACTIVE"
                    ))
                    .toList();
        }

        // When the current product has models
        // Find the model that matches the variation value and return its associated branch stock information.
        if (currentProductInfo.isHasModel()) {
            return currentProductInfo.getModels().stream()
                    .filter(model -> model.getName().equals(variationValue))
                    .findFirst()
                    .map(ProductInformation.Model::getBranches)
                    .orElseThrow(() -> new IllegalArgumentException("Model not found for variation: " + variationValue));
        }

        // When the current product does not have models
        // Return the list of branches from the current product's information.
        return currentProductInfo.getBranches();
    }

    /**
     * Generates a Stock Keeping Unit (SKU) based on the product's branch ID and variation information.
     *
     * @param hasModel        Indicates if the product has variations (models).
     * @param variationValue  The value of the variation (e.g., "Red" for a color variation).
     * @param activeBranchIds List of branch IDs that are currently active and hold stock.
     * @param branchId        The ID of the branch for which the SKU is being generated.
     * @return A SKU string formatted as "variationValue_SKU_branchId_timestamp" if the product has models,
     * or "SKU_branchId_timestamp" if it does not. Returns an empty string if the branch ID is not active.
     */
    private static String generateSKU(boolean hasModel, String variationValue, List<Integer> activeBranchIds, int branchId) {
        if (!activeBranchIds.contains(branchId)) return "";
        String baseSKU = "SKU_%s_%s".formatted(branchId, Instant.now().toEpochMilli());

        // Add variation-specific part to SKU if the product has variations
        if (hasModel) {
            return "%s_%s".formatted(variationValue, baseSKU);
        }

        return baseSKU; // For products without variations
    }


    /**
     * Generates main language information for a specific language, including SEO fields.
     *
     * @param languageKey  The current language key for which to generate the main language.
     * @param hasModel     Indicates if the product has models.
     * @param manageByIMEI Indicates if the product is managed by IMEI.
     * @param hasSEO       Indicates if the product has SEO fields.
     * @return A MainLanguage object for the specified language.
     */
    private static ProductInformation.MainLanguage generateMainLanguage(
            String languageKey, boolean hasModel, boolean manageByIMEI, boolean hasSEO) {

        String localDateTime = LocalDateTime.now().toString().substring(0, 19);
        long localEpochTime = Instant.now().toEpochMilli();

        // Generate the main language information with optional SEO fields
        return new ProductInformation.MainLanguage(
                languageKey,
                "[%s][%s][%s] Product name ".formatted(languageKey, hasModel ? "Variation" : "Without variation", manageByIMEI ? "IMEI" : "Normal") + localDateTime,
                "[%s] Product description ".formatted(languageKey) + localDateTime,
                hasSEO ? "[%s] SEO Title - ".formatted(languageKey) + localEpochTime : "",
                hasSEO ? "[%s] SEO Description - ".formatted(languageKey) + localEpochTime : "",
                hasSEO ? "[%s] SEO Keyword - ".formatted(languageKey) + localEpochTime : "",
                hasSEO ? "%s-seo-url-".formatted(languageKey) + localEpochTime : ""
        );
    }

    /**
     * Generates main language information for the product, including SEO fields, for all language keys.
     *
     * @param languageKeys    List of language keys for the product. Can be null if not editing translations.
     * @param defaultLanguage The default language key to use.
     * @param hasModel        Indicates if the product has models.
     * @param manageByIMEI    Indicates if the product is managed by IMEI.
     * @param hasSEO          Indicates if the product has SEO fields.
     * @return A list of MainLanguage objects for the product.
     */
    private static List<ProductInformation.MainLanguage> generateMainLanguages(
            List<String> languageKeys, String defaultLanguage, boolean hasModel, boolean manageByIMEI, boolean hasSEO) {

        // If languageKeys is provided, generate MainLanguage objects for each language key
        if (languageKeys != null) {
            return languageKeys.stream()
                    .map(languageKey -> generateMainLanguage(languageKey, hasModel, manageByIMEI, hasSEO))
                    .toList();
        }

        // If languageKeys is null, create a single MainLanguage object using the default language
        return List.of(generateMainLanguage(defaultLanguage, hasModel, manageByIMEI, hasSEO));
    }

    /**
     * Generates random item attributes for the product if applicable.
     *
     * @param hasAttribution Indicates if the product has custom attributes.
     * @return A list of ItemAttribute objects for the product.
     */
    private static List<ProductInformation.ItemAttribute> generateItemAttributes(boolean hasAttribution) {
        if (!hasAttribution) return java.util.List.of();
        return IntStream.range(0, nextInt(10) + 1)
                .mapToObj(index -> new ProductInformation.ItemAttribute(
                        "Attribute name " + index,
                        "Attribute value " + index,
                        nextBoolean()
                ))
                .toList();
    }

    /**
     * Sets the visibility flags for the product on various platforms like web, app, store, and GoSOCIAL.
     *
     * @param productInfo The product information to be updated.
     * @param onApp       Indicates if the product is visible on the app.
     * @param onWeb       Indicates if the product is visible on the web.
     * @param inStore     Indicates if the product is available in stores.
     * @param inGoSOCIAL  Indicates if the product is available on GoSOCIAL.
     */
    private static void setVisibilityFlags(ProductInformation productInfo, boolean onApp, boolean onWeb, boolean inStore, boolean inGoSOCIAL) {
        // Update the product's visibility status on the app
        productInfo.setOnApp(onApp);

        // Update the product's visibility status on the web
        productInfo.setOnWeb(onWeb);

        // Update the product's availability status in physical stores
        productInfo.setInStore(inStore);

        // Update the product's availability status on GoSOCIAL platform
        productInfo.setInGosocial(inGoSOCIAL);
    }
}