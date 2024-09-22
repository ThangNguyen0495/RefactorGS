package pages.web.seller.product.all_products;

import api.seller.login.APISellerLogin;
import api.seller.product.APIGetProductDetail;
import api.seller.product.APIGetProductList;
import api.seller.setting.APIGetBranchList;
import api.seller.setting.APIGetStoreDefaultLanguage;
import api.seller.setting.APIGetStoreLanguage;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import utility.ProductUtils;
import utility.PropertiesUtils;
import utility.VariationUtils;
import utility.WebUtils;

import java.time.Instant;
import java.util.*;
import java.util.stream.IntStream;

import static org.apache.commons.lang.math.RandomUtils.nextBoolean;
import static org.apache.commons.lang.math.RandomUtils.nextInt;

public class BaseProductPage extends BaseProductElement {
    // WebDriver and WebUtils
    private final WebDriver driver;
    private final WebUtils webUtils;

    // Logger
    private final Logger logger = LogManager.getLogger();

    // Product Information
    private APIGetProductDetail.ProductInformation apiProductInfo;
    private APIGetProductDetail.ProductInformation utilsProductInfo;

    // Credentials and Information Lists
    private APISellerLogin.Credentials credentials;

    /**
     * -- SETTER --
     *  Sets whether discounts are not applicable.
     *
     * @param noDiscount True if no discount is applicable, otherwise false.
     */
    // Default Values and Flags
    @Setter
    private boolean noDiscount = nextBoolean();
    /**
     * -- SETTER --
     *  Sets whether cost is not applicable.
     *
     * @param noCost True if no cost is applicable, otherwise false.
     */
    @Setter
    private boolean noCost = nextBoolean();
    /**
     * -- SETTER --
     *  Sets whether the product has dimensions.
     *
     * @param hasDimension True if the product has dimensions, otherwise false.
     */
    @Setter
    private boolean hasDimension = false;
    /**
     * -- SETTER --
     *  Sets whether SEO information should be included for the product.
     *
     * @param hasSEO True if SEO information should be included, otherwise false.
     */
    @Setter
    private boolean hasSEO = false;
    /**
     * -- SETTER --
     *  Sets whether inventory management should be by lot date.
     *
     * @param manageByLotDate True if inventory management should be by lot date, otherwise false.
     */
    @Setter
    private boolean manageByLotDate = false;
    /**
     * -- SETTER --
     *  Sets whether attributions should be included for the product.
     *
     * @param hasAttribution True if attributions should be included, otherwise false.
     */
    @Setter
    private boolean hasAttribution = false;
    private boolean showOnApp = true;
    private boolean showOnWeb = true;
    private boolean showInStore = true;
    private boolean showInGoSocial = true;

    // Product and Language Information
    private String defaultLanguage;
    private List<String> activeBranchNames;
    private List<String> storeLanguageCodes;
    private List<String> storeLanguageNames;

    // Constructor
    public BaseProductPage(WebDriver driver) {
        this.driver = driver;

        // Initialize common utilities
        this.webUtils = new WebUtils(driver);
    }

    /**
     * Fetches necessary information including login credentials, branch details, and store language details.
     * This method updates the state of the ProductPage with relevant information for branches, languages,
     * and the default language setting based on the seller's information.
     *
     * @param credentials The API credentials containing login information (username, password).
     * @return The current instance of ProductPage, with updated information for method chaining.
     */
    public BaseProductPage fetchInformation(APISellerLogin.Credentials credentials) {
        // Update credentials for further API requests
        this.credentials = credentials;

        // Retrieve and store branch information
        List<APIGetBranchList.BranchInformation> branchInfoList = new APIGetBranchList(credentials).getBranchInformation();

        // Retrieve and store language information
        List<APIGetStoreLanguage.LanguageInformation> languageInfoList = new APIGetStoreLanguage(credentials).getStoreLanguageInformation();

        // Retrieve the default language of the seller
        defaultLanguage = new APIGetStoreDefaultLanguage(credentials).getDefaultLanguage();

        // Get active branch names and IDs
        activeBranchNames = APIGetBranchList.getActiveBranchNames(branchInfoList);

        // Get all store language codes and names
        storeLanguageCodes = APIGetStoreLanguage.getAllStoreLanguageCodes(languageInfoList);
        storeLanguageNames = APIGetStoreLanguage.getAllStoreLanguageNames(languageInfoList);

        // Return the current instance of ProductPage for method chaining
        return this;
    }

    /**
     * Fetches product information from ProductUtils based on the provided parameters.
     *
     * @param isManagedByIMEI Whether the product is managed by IMEI.
     * @param hasModel        Whether the product has a model.
     * @param branchStock     Array of branch stock quantities.
     */
    private void fetchProductInformation(boolean isManagedByIMEI, boolean hasModel, int[] branchStock) {
        // Generate product information using the provided parameters
        utilsProductInfo = ProductUtils.generateProductInformation(credentials,
                defaultLanguage,
                hasModel,
                noCost,
                noDiscount,
                isManagedByIMEI,
                hasSEO,
                hasDimension,
                manageByLotDate || ((apiProductInfo != null) && apiProductInfo.isLotAvailable()),
                hasAttribution,
                showOnWeb,
                showOnApp,
                showInStore,
                showInGoSocial,
                branchStock);
    }

    /**
     * Fetches product information from the API.
     *
     * @param productId The ID of the product to fetch information for.
     * @return An instance of APIGetProductDetail containing the product information.
     */
    private APIGetProductDetail.ProductInformation fetchProductInformation(int productId) {
        return new APIGetProductDetail(credentials).getProductInformation(productId);
    }

    /**
     * Sets the selling platforms for the product.
     *
     * @param showOnApp      True if the product should be shown on the app.
     * @param showOnWeb      True if the product should be shown on the web.
     * @param showInStore    True if the product should be shown in-store.
     * @param showInGoSocial True if the product should be shown on GoSocial.
     */
    public void setSellingPlatform(boolean showOnApp, boolean showOnWeb, boolean showInStore, boolean showInGoSocial) {
        this.showOnApp = showOnApp;
        this.showOnWeb = showOnWeb;
        this.showInStore = showInStore;
        this.showInGoSocial = showInGoSocial;
    }

    /**
     * Resets all boolean variables to their default values.
     */
    private void resetAllVariables() {
        this.noDiscount = nextBoolean();
        this.noCost = nextBoolean();
        this.hasDimension = false;
        this.hasSEO = false;
        this.manageByLotDate = false;
        this.hasAttribution = false;
        this.showOnApp = true;
        this.showOnWeb = true;
        this.showInStore = true;
        this.showInGoSocial = true;
    }

    /**
     * Navigates to the create product page and handles UI cleanup.
     *
     * @return The current instance of ProductPage for method chaining.
     */
    public BaseProductPage navigateToCreateProductPage() {
        driver.get("%s/product/create".formatted(PropertiesUtils.getDomain()));
        logger.info("Navigated to create product page.");

        return this;
    }


    /**
     * Navigates to the product update page for a given product ID,
     * removes any old wholesale product configurations if necessary,
     * and handles page refreshing and UI cleanup.
     *
     * @param productId The ID of the product to update.
     * @return The current instance of ProductPage for method chaining.
     */
    public BaseProductPage navigateToUpdateProductPage(int productId) {
        // Retrieve product details from the API
        apiProductInfo = fetchProductInformation(productId);

        // Navigate to the product update page
        navigateToProductPage(productId);

        return this;
    }

    /**
     * Navigates to the product update page using the given product ID.
     *
     * @param productId The ID of the product to navigate to.
     */
    private void navigateToProductPage(int productId) {
        driver.get("%s/product/edit/%s".formatted(PropertiesUtils.getDomain(), productId));
        logger.info("Navigated to product update page, productId: {}", productId);
    }

    /**
     * Inputs the product name into the relevant field.
     */
    void inputProductName() {
        webUtils.sendKeys(loc_txtProductName, utilsProductInfo.getName());
        logger.info("Input product name: {}", utilsProductInfo.getName());
    }

    /**
     * Inputs the product description into the relevant field.
     */
    void inputProductDescription() {
        webUtils.sendKeys(loc_txaProductDescription, utilsProductInfo.getDescription());
        logger.info("Input product description: {}", utilsProductInfo.getDescription());
    }

    /**
     * Uploads product images. Removes any existing images before uploading new ones.
     *
     * @param imageFile The image file names to upload.
     */
    void uploadProductImage(String... imageFile) {
        // Remove old product images
        List<WebElement> removeImageIcons = webUtils.getListElement(loc_icnRemoveImages);
        if (!removeImageIcons.isEmpty()) {
            IntStream.iterate(removeImageIcons.size() - 1, index -> index >= 0, index -> index - 1)
                    .forEach(index -> webUtils.clickJS(loc_icnRemoveImages, index));
            logger.info("Removed existing product images.");
        }

        // Upload new product images
        Arrays.stream(imageFile).map(imgFile -> System.getProperty("user.dir") + "/src/main/resources/files/images/" + imgFile).forEach(filePath -> {
            webUtils.uploads(imgUploads, filePath);
            logger.info("Uploaded product image: {}", filePath);
        });
    }

    /**
     * Selects the VAT option based on the product's tax name.
     */
    void selectVAT() {
        webUtils.clickJS(loc_ddvSelectedVAT);
        logger.info("Opened VAT dropdown.");

        String vatName = utilsProductInfo.getTaxName();
        webUtils.clickJS(vatName.equals("tax.value.include") ? loc_ddvNoVAT : loc_ddvOthersVAT(vatName));
        logger.info("Selected VAT: {}", vatName);
    }

    /**
     * Inputs a SKU for a product without variations.
     */
    void inputWithoutVariationProductSKU() {
        String sku = "SKU" + Instant.now().toEpochMilli();
        webUtils.sendKeys(loc_txtWithoutVariationSKU, sku);
        logger.info("Input SKU: {}", sku);
    }

    /**
     * Updates the SKU for a product without variations for each branch.
     */
    void updateWithoutVariationProductSKU() {
        webUtils.click(loc_txtWithoutVariationSKU);
        logger.info("Opened update SKU popup.");

        webUtils.getElement(loc_dlgUpdateSKU);
        logger.info("Update SKU popup is visible.");

        IntStream.range(0, activeBranchNames.size()).forEach(brIndex -> {
            String sku = "SKU_%s_%s".formatted(activeBranchNames.get(brIndex), Instant.now().toEpochMilli());
            webUtils.sendKeys(loc_dlgUpdateSKU_txtInputSKU, brIndex, sku);
            logger.info("Updated SKU for branch {}: {}", activeBranchNames.get(brIndex), sku);
        });

        webUtils.click(loc_ttlUpdateSKU);
        webUtils.click(loc_dlgCommons_btnUpdate);
        logger.info("Closed Update SKU popup.");
    }

    /**
     * Sets inventory management options based on the product's inventory type and settings.
     */
    void setManageInventory() {
        if (!driver.getCurrentUrl().contains("/edit/") && utilsProductInfo.getInventoryManageType().equals("IMEI_SERIAL_NUMBER")) {
            webUtils.click(loc_ddlManageInventory);
            webUtils.click(loc_ddvManageInventoryByIMEI);
            logger.info("Set inventory management to IMEI/Serial Number.");
        }

        if (!"IMEI_SERIAL_NUMBER".equals(utilsProductInfo.getInventoryManageType()) && manageByLotDate) {
            if (!webUtils.isCheckedJS(loc_chkManageStockByLotDate)) {
                webUtils.clickJS(loc_chkManageStockByLotDate);
                logger.info("Enabled manage stock by lot date.");
            }
        } else {
            logger.info("Manage inventory by: {}", "IMEI/Serial Number".equals(utilsProductInfo.getInventoryManageType()) ? "IMEI/Serial Number" : "Product");
        }
    }

    /**
     * Configures storefront display options for the product.
     */
    void setSFDisplay() {
        if (!webUtils.isCheckedJS(loc_chkDisplayIfOutOfStock)) {
            webUtils.clickJS(loc_chkDisplayIfOutOfStock);
            logger.info("Configured display if out of stock.");
        }

        if (webUtils.isCheckedJS(loc_chkHideRemainingStock)) {
            webUtils.clickJS(loc_chkHideRemainingStock);
            logger.info("Configured hide remaining stock.");
        }
    }

    /**
     * Sets the product priority.
     *
     * @param priority The priority value to set (1-100).
     */
    void setPriority(int priority) {
        webUtils.sendKeys(loc_txtPriority, String.valueOf(priority));
        logger.info("Set product priority: {}", priority);
    }

    /**
     * Sets product dimensions (weight, length, width, height).
     */
    void setProductDimension() {
        String dimension = hasDimension ? "10" : "0";

        webUtils.sendKeys(loc_txtWeight, dimension);
        logger.info("Input weight: {}", dimension);

        webUtils.sendKeys(loc_txtLength, dimension);
        logger.info("Input length: {}", dimension);

        webUtils.sendKeys(loc_txtWidth, dimension);
        logger.info("Input width: {}", dimension);

        webUtils.sendKeys(loc_txtHeight, dimension);
        logger.info("Input height: {}", dimension);
    }

    /**
     * Selects the platforms where the product should be shown (App, Web, In-store, GoSocial).
     */
    void selectPlatform() {
        if (webUtils.getElement(loc_chkApp).isSelected() != showOnApp) {
            webUtils.clickJS(loc_chkApp);
            logger.info("Configured show on App.");
        }

        if (webUtils.getElement(loc_chkWeb).isSelected() != showOnWeb) {
            webUtils.clickJS(loc_chkWeb);
            logger.info("Configured show on Web.");
        }

        if (webUtils.getElement(loc_chkInStore).isSelected() != showInStore) {
            webUtils.clickJS(loc_chkInStore);
            logger.info("Configured show in-store.");
        }

        if (webUtils.getElement(loc_chkGoSocial).isSelected() != showInGoSocial) {
            webUtils.clickJS(loc_chkGoSocial);
            logger.info("Configured show on GoSocial.");
        }
    }

    /**
     * Adds and inputs attributions if applicable.
     */
    void addAttribution() {
        // Remove existing attributions
        List<WebElement> deleteAttributionIcons = webUtils.getListElement(loc_icnDeleteAttribution);
        if (!deleteAttributionIcons.isEmpty()) {
            IntStream.iterate(deleteAttributionIcons.size() - 1, index -> index >= 0, index -> index - 1)
                    .forEach(index -> webUtils.clickJS(loc_icnDeleteAttribution, index));
            logger.info("Removed existing attributions.");
        }

        // Add new attributions if required
        if (hasAttribution) {
            int numOfAttributes = nextInt(10);
            IntStream.range(0, numOfAttributes).forEachOrdered(ignored -> webUtils.clickJS(loc_btnAddAttribution));
            logger.info("Added {} attributions.", numOfAttributes);

            long epoch = Instant.now().toEpochMilli();
            IntStream.range(0, numOfAttributes).forEach(attIndex -> {
                webUtils.sendKeys(loc_txtAttributionName, attIndex, "name_%s_%s".formatted(attIndex, epoch));
                webUtils.sendKeys(loc_txtAttributionValue, attIndex, "value_%s_%s".formatted(attIndex, epoch));
                if (!Objects.equals(webUtils.isCheckedJS(loc_chkDisplayAttribute, attIndex), nextBoolean())) {
                    webUtils.clickJS(loc_chkDisplayAttribute, attIndex);
                }
            });
        }
    }

    /**
     * Inputs SEO information including title, description, keywords, and URL.
     */
    void inputSEO() {
        // SEO Title
        String title = "[%s] Auto - SEO Title - %s".formatted(defaultLanguage, Instant.now().toEpochMilli());
        webUtils.sendKeys(loc_txtSEOTitle, title);
        logger.info("SEO title: {}.", title);

        // SEO Description
        String description = "[%s] Auto - SEO Description - %s".formatted(defaultLanguage, Instant.now().toEpochMilli());
        webUtils.sendKeys(loc_txtSEODescription, description);
        logger.info("SEO description: {}.", description);

        // SEO Keywords
        String keyword = "[%s] Auto - SEO Keyword - %s".formatted(defaultLanguage, Instant.now().toEpochMilli());
        webUtils.sendKeys(loc_txtSEOKeywords, keyword);
        logger.info("SEO keyword: {}.", keyword);

        // SEO URL
        String url = "%s%s".formatted(defaultLanguage, Instant.now().toEpochMilli());
        webUtils.sendKeys(loc_txtSEOUrl, url);
        logger.info("SEO URL: {}.", url);
    }


    /**
     * Initializes basic product information by inputting and configuring various product details.
     */
    void initBasicProductInformation() {
        // Input basic product details
        inputProductName();
        inputProductDescription();

        // Upload product images
        uploadProductImage("images.jpg");

        // Configure product tax and inventory settings
        selectVAT();
        setManageInventory();

        // Configure storefront display settings
        setSFDisplay();

        // Set additional product attributes
        setPriority(nextInt(100) + 1);
        setProductDimension();
        selectPlatform();
        if (hasAttribution) {
            addAttribution();
        }

        // Input SEO information if applicable
        if (hasSEO) {
            inputSEO();
        }
    }

    /**
     * Inputs the prices for a product without variations.
     * <p>
     * This method populates the listing price, selling price, and cost price fields on the product page
     * and logs the values inputted for debugging and verification purposes.
     * </p>
     */
    public void inputWithoutVariationPrice() {
        // Input listing price
        webUtils.sendKeys(loc_txtWithoutVariationListingPrice, String.valueOf(utilsProductInfo.getOrgPrice()));
        logger.info("Listing price: {}", String.format("%,d", utilsProductInfo.getOrgPrice())); // Log the listing price

        // Input selling price
        webUtils.sendKeys(loc_txtWithoutVariationSellingPrice, String.valueOf(utilsProductInfo.getNewPrice()));
        logger.info("Selling price: {}", String.format("%,d", utilsProductInfo.getNewPrice())); // Log the selling price

        // Input cost price
        webUtils.sendKeys(loc_txtWithoutVariationCostPrice, String.valueOf(utilsProductInfo.getCostPrice()));
        logger.info("Cost price: {}", String.format("%,d", utilsProductInfo.getCostPrice())); // Log the cost price
    }

    /**
     * Adds IMEI numbers for each branch.
     * <p>
     * This method opens the IMEI management popup, selects all branches, removes any existing IMEI numbers,
     * and then inputs new IMEI numbers based on the provided variation value and branch stock.
     * If the product does not have variations, the {@code variationValue} should be {@code null}.
     * </p>
     *
     * @param variationValue The variation value to prefix IMEI numbers, or {@code null} if the product has no variation.
     * @param branchStock    The list of stock quantities for each branch.
     */
    private void addIMEIForEachBranch(String variationValue, List<Integer> branchStock) {
        // Open the dropdown to select branches
        webUtils.click(loc_dlgAddIMEISelectedBranch);
        logger.info("[Add IMEI popup] Open all branches dropdown.");

        // Select all branches if not already selected
        if (!webUtils.isCheckedJS(loc_dlgAddIMEI_chkSelectAllBranches)) {
            webUtils.clickJS(loc_dlgAddIMEI_chkSelectAllBranches);
        } else {
            webUtils.click(loc_dlgAddIMEISelectedBranch);
        }
        logger.info("[Add IMEI popup] Select all branches.");

        // Remove old IMEI numbers
        int bound = webUtils.getListElement(loc_dlgAddIMEI_icnDeleteIMEI).size();
        IntStream.iterate(bound - 1, index -> index >= 0, index -> index - 1)
                .forEach(index -> webUtils.clickJS(loc_dlgAddIMEI_icnDeleteIMEI, index));
        logger.info("Remove old IMEI.");

        // Input new IMEI numbers for each branch
        for (int brIndex = 0; brIndex < activeBranchNames.size(); brIndex++) {
            String branchName = activeBranchNames.get(brIndex);
            int branchStockIndex = activeBranchNames.indexOf(branchName);
            for (int imeiIndex = 0; imeiIndex < branchStock.get(branchStockIndex); imeiIndex++) {
                String imei = "%s%s_IMEI_%s_%s\n".formatted(
                        variationValue != null ? "%s_".formatted(variationValue) : "",
                        branchName,
                        Instant.now().toEpochMilli(),
                        imeiIndex
                );
                webUtils.sendKeys(loc_dlgAddIMEI_txtAddIMEI, brIndex, imei);
                logger.info("Input IMEI: {}", imei.replace("\n", ""));
            }
            logger.info("{}[{}] Add IMEI, stock: {}",
                    variationValue == null ? "" : "[%s]".formatted(variationValue),
                    branchName,
                    branchStock.get(branchStockIndex));
        }

        // Save the added IMEI numbers
        webUtils.click(loc_dlgAddIMEI_btnSave);
        logger.info("Close Add IMEI popup.");
    }

    /**
     * Inputs stock quantities for a product without variations.
     * <p>
     * Depending on the inventory management type, this method either opens the IMEI management popup and adds
     * IMEI numbers, or updates the stock quantities directly for each branch.
     * </p>
     */
    public void inputWithoutVariationStock() {
        if (!utilsProductInfo.isLotAvailable()) {
            if (utilsProductInfo.getInventoryManageType().equals("IMEI_SERIAL_NUMBER")) {
                // Open the Add IMEI popup if managing by IMEI
                webUtils.click(loc_txtWithoutVariationBranchStock);
                logger.info("[Create] Open Add IMEI popup without variation product.");

                // Add IMEI numbers for each branch
                addIMEIForEachBranch(null, APIGetProductDetail.getBranchStocks(utilsProductInfo, null));
                logger.info("[Create] Complete add stock for IMEI product.");
            } else {
                // Update stock for normal product
                IntStream.range(0, activeBranchNames.size()).forEach(brIndex -> {
                    webUtils.sendKeys(loc_txtWithoutVariationBranchStock, brIndex, String.valueOf(APIGetProductDetail.getBranchStocks(utilsProductInfo, null).get(brIndex)));
                    logger.info("[{}] Input stock: {}", activeBranchNames.get(brIndex), APIGetProductDetail.getBranchStocks(utilsProductInfo, null).get(brIndex));
                });
                logger.info("[Create] Complete update stock for Normal product.");
            }
        } else {
            logger.info("[AddWithoutVariationStock] Product is managed by lot-date.");
            logger.info("[AddWithoutVariationStock] Stock updates are not allowed on the product detail page.");
            logger.info("[AddWithoutVariationStock] Please update stock on the Lot detail page.");
        }
    }

    /**
     * Adds normal stock quantities for each branch, considering variations.
     * <p>
     * Opens the stock update popup, selects all branches, inputs stock quantities into the fields, and logs the actions.
     * This method handles both updating existing stock and adding new stock for branches.
     * </p>
     *
     * @param branchStock The list of stock quantities for each branch.
     * @param varIndex    The index of the variation if applicable.
     */
    private void addNormalStockForEachBranch(List<Integer> branchStock, int varIndex) {
        // Open the dropdown to select branches
        webUtils.click(loc_dlgUpdateStock_ddvSelectedBranch);
        logger.info("[Update stock popup] Open all branches dropdown.");

        // Select all branches if not already selected
        webUtils.checkCheckbox(loc_dlgUpdateStock_chkSelectAllBranches);
        logger.info("[Update stock popup] Select all branches.");

        // Switch to the change stock tab
        webUtils.click(loc_dlgUpdateStock_tabChange);

        // Input a stock quantity into the visible stock input field
        int stock = Collections.max(branchStock) + 1; // Use a quantity greater than the maximum stock to update
        webUtils.sendKeys(loc_dlgUpdateStock_txtStockValue, String.valueOf(stock));

        // Input stock quantities for each branch
        activeBranchNames.forEach(branchName -> {
            int branchStockIndex = activeBranchNames.indexOf(branchName);
            if (!webUtils.getListElement(loc_dlgUpdateStock_txtBranchStock(branchName)).isEmpty()) {
                webUtils.sendKeys(loc_dlgUpdateStock_txtBranchStock(branchName), String.valueOf(branchStock.get(branchStockIndex)));
                logger.info("{}[{}] Update stock: {}",
                        utilsProductInfo.isHasModel() ? "" : "[%s]".formatted(APIGetProductDetail.getVariationValues(utilsProductInfo, defaultLanguage).get(varIndex)),
                        branchName,
                        branchStock.get(branchStockIndex));
            } else {
                logger.info("{}[{}] Add stock: {}",
                        utilsProductInfo.isHasModel() ? "" : "[%s]".formatted(APIGetProductDetail.getVariationValues(utilsProductInfo, defaultLanguage).get(varIndex)),
                        branchName,
                        stock);
            }
        });

        // Close the Update stock popup
        webUtils.click(loc_dlgCommons_btnUpdate);
        logger.info("Close Update stock popup.");
    }

    /**
     * Updates stock quantities for a product without variations.
     * <p>
     * Depending on the inventory management type, this method either opens the IMEI management popup and adds
     * IMEI numbers, or updates the stock quantities directly for each branch. It logs the actions performed during
     * the update process.
     * </p>
     */
    public void updateWithoutVariationStock() {
        if (!utilsProductInfo.isLotAvailable()) {
            if (utilsProductInfo.getInventoryManageType().equals("IMEI_SERIAL_NUMBER")) {
                // Open the Add IMEI popup if managing by IMEI
                webUtils.click(loc_txtWithoutVariationBranchStock);
                logger.info("[Update] Open Add IMEI popup without variation product.");

                // Add IMEI numbers for each branch
                addIMEIForEachBranch(null, APIGetProductDetail.getBranchStocks(utilsProductInfo, null));
                logger.info("[Update] Complete add stock for IMEI product.");
            } else {
                // Open the Update stock popup for normal product
                webUtils.click(loc_txtWithoutVariationBranchStock);
                logger.info("Open Update stock popup.");

                // Add stock for each branch
                addNormalStockForEachBranch(APIGetProductDetail.getBranchStocks(utilsProductInfo, null), 0);
                logger.info("[Update] Complete update stock for Normal product.");
            }
        } else {
            logger.info("[UpdateWithoutVariationStock] Product is managed by lot-date.");
            logger.info("[UpdateWithoutVariationStock] Stock updates are not allowed on the product detail page.");
            logger.info("[UpdateWithoutVariationStock] Please update stock on the Lot detail page.");
        }
    }

    /**
     * Adds variations for a product by generating random variations, deleting any existing ones,
     * and inputting the new variation names and values.
     *
     * @throws InterruptedException if the thread is interrupted during sleep
     */
    public void addVariations() throws InterruptedException {
        if (!utilsProductInfo.isLotAvailable()) {
            // Generate variation names and values
            var variationGroupName = APIGetProductDetail.getVariationGroupName(utilsProductInfo, defaultLanguage);
            var variationValues = APIGetProductDetail.getVariationValues(utilsProductInfo, defaultLanguage);

            // Generate and log variation map
            var variationMap = VariationUtils.getVariationMap(variationGroupName, variationValues);
            logger.info("Variation map: {}", variationMap);

            // Log variation list
            logger.info("Variation list: {}", variationValues);

            // Delete old variations
            deleteOldVariations();

            // Add new variations
            addNewVariationGroups(variationMap);

            // Click on variations label to save
            webUtils.click(loc_lblVariations);
        } else {
            logger.info("Product managed stock by lot-date, not allowed to update variations.");
        }
    }


    /**
     * Deletes all existing variations by clicking on the delete icons in reverse order.
     */
    private void deleteOldVariations() {
        List<WebElement> deleteVariationIcons = webUtils.getListElement(loc_btnDeleteVariation);
        IntStream.iterate(deleteVariationIcons.size() - 1, index -> index >= 0, index -> index - 1)
                .forEach(index -> webUtils.clickJS(loc_btnDeleteVariation, index));
        logger.info("Removed old variations.");
    }

    /**
     * Adds new variation groups based on the generated variation map.
     *
     * @param variationMap The map containing variation names as keys and their corresponding values as a list
     * @throws InterruptedException if the thread is interrupted during sleep
     */
    private void addNewVariationGroups(Map<String, List<String>> variationMap) throws InterruptedException {
        // Click to add variation groups
        IntStream.range(0, variationMap.keySet().size()).forEachOrdered(ignored -> webUtils.clickJS(loc_btnAddVariation));
        logger.info("Added new variation groups.");

        // Loop through each variation group and input name and values
        for (int groupIndex = 0; groupIndex < variationMap.keySet().size(); groupIndex++) {
            String varName = variationMap.keySet().stream().toList().get(groupIndex);
            logger.info("Input variation name: {}", varName);

            inputVariationName(varName, groupIndex);
            inputVariationValues(variationMap.get(varName), groupIndex);
        }
    }

    /**
     * Inputs the variation name into the appropriate input field.
     *
     * @param varName    The name of the variation to be input
     * @param groupIndex The index of the variation group
     */
    private void inputVariationName(String varName, int groupIndex) {
        webUtils.sendKeys(loc_txtVariationName, groupIndex, varName);
    }

    /**
     * Inputs the variation values for a given variation group.
     *
     * @param varValues  The list of values to be input for the variation group
     * @param groupIndex The index of the variation group
     * @throws InterruptedException if the thread is interrupted during sleep
     */
    private void inputVariationValues(List<String> varValues, int groupIndex) throws InterruptedException {
        for (String varValue : varValues) {
            webUtils.getElement(loc_txtVariationValue, groupIndex).sendKeys(varValue);

            // Wait for suggestion to appear
            Thread.sleep(500);

            // Complete the input of variation value by pressing Enter
            webUtils.getElement(loc_txtVariationValue, groupIndex).sendKeys(Keys.chord(Keys.ENTER));
            logger.info("Input variation value: {}", varValue);
        }
    }

    /**
     * Inputs the price details for variations of a product.
     * <p>
     * This method selects all variations, opens the Update Price popup, and then inputs listing, selling,
     * and cost prices for each variation. The values are logged for verification.
     * </p>
     */
    void inputVariationPrice() {
        // Select all variations
        webUtils.checkCheckbox(loc_tblVariation_chkSelectAll);

        // Open the actions dropdown
        webUtils.clickJS(loc_tblVariation_lnkSelectAction);

        // Open the Update Price popup
        webUtils.click(loc_tblVariation_ddvActions);

        // Input price details for each variation
        IntStream.range(0, APIGetProductDetail.getVariationValues(utilsProductInfo, defaultLanguage).size())
                .forEachOrdered(varIndex -> {
                    // Get current variation
                    String variation = APIGetProductDetail.getVariationValues(utilsProductInfo, defaultLanguage).get(varIndex);

                    // Input listing price
                    long listingPrice = APIGetProductDetail.getVariationListingPrice(utilsProductInfo, varIndex);
                    webUtils.sendKeys(loc_dlgUpdatePrice_txtListingPrice, varIndex, String.valueOf(listingPrice));
                    logger.info("[{}] Listing price: {}.", variation, String.format("%,d", listingPrice));

                    // Input selling price
                    long sellingPrice = APIGetProductDetail.getVariationSellingPrice(utilsProductInfo, varIndex);
                    webUtils.sendKeys(loc_dlgUpdatePrice_txtSellingPrice, varIndex, String.valueOf(sellingPrice));
                    logger.info("[{}] Selling price: {}.", variation, String.format("%,d", sellingPrice));

                    // Input cost price
                    long costPrice = APIGetProductDetail.getVariationCostPrice(utilsProductInfo, varIndex);
                    webUtils.sendKeys(loc_dlgUpdatePrice_txtCostPrice, varIndex, String.valueOf(costPrice));
                    logger.info("[{}] Cost price: {}.", variation, String.format("%,d", costPrice));
                });

        // Click around to ensure changes are applied
        webUtils.click(loc_ttlUpdatePrice);

        // Close the Update Price popup
        webUtils.click(loc_dlgCommons_btnUpdate);
    }

    /**
     * Inputs the stock quantities for variations of a product.
     * <p>
     * This method opens the Update Stock popup for each variation, and depending on the inventory management type,
     * either adds IMEI numbers or updates normal stock quantities.
     * </p>
     */
    void inputVariationStock() {
        if (!utilsProductInfo.isLotAvailable()) {
            // Input stock quantity for each variation
            IntStream.range(0, APIGetProductDetail.getVariationValues(utilsProductInfo, defaultLanguage).size())
                    .forEach(varIndex -> {
                        webUtils.clickJS(loc_tblVariation_txtStock, varIndex);
                        if (utilsProductInfo.getInventoryManageType().equals("IMEI_SERIAL_NUMBER")) {
                            addIMEIForEachBranch(
                                    APIGetProductDetail.getVariationValues(utilsProductInfo, defaultLanguage).get(varIndex),
                                    APIGetProductDetail.getBranchStocks(utilsProductInfo, APIGetProductDetail.getVariationModelId(utilsProductInfo, varIndex))
                            );
                        } else {
                            addNormalStockForEachBranch(
                                    APIGetProductDetail.getBranchStocks(utilsProductInfo, APIGetProductDetail.getVariationModelId(utilsProductInfo, varIndex)),
                                    varIndex
                            );
                        }
                    });
        } else {
            logger.info("[Add/UpdateVariationStock] Product is managed by lot-date.");
            logger.info("[Add/UpdateVariationStock] Stock updates are not allowed on the product detail page.");
            logger.info("[Add/UpdateVariationStock] Please update stock on the Lot detail page.");
        }
    }

    /**
     * Inputs SKU values for variations of a product.
     * <p>
     * This method opens the Update SKU popup for each variation and inputs SKUs for each branch. Each SKU is generated
     * based on the variation and branch information and logged for verification.
     * </p>
     */
    void inputVariationSKU() {
        // Input SKU for each variation
        for (int varIndex = 0; varIndex < APIGetProductDetail.getVariationValues(utilsProductInfo, defaultLanguage).size(); varIndex++) {
            // Open the Update SKU popup
            webUtils.clickJS(loc_tblVariation_txtSKU, varIndex);

            // Input SKU for each branch
            for (int brIndex = 0; brIndex < activeBranchNames.size(); brIndex++) {
                String sku = "SKU_%s_%s_%s".formatted(
                        APIGetProductDetail.getVariationValues(utilsProductInfo, defaultLanguage).get(varIndex),
                        activeBranchNames.get(brIndex),
                        Instant.now().toEpochMilli()
                );
                webUtils.sendKeys(loc_dlgUpdateSKU_txtInputSKU, brIndex, sku);
                logger.info("[Update SKU popup] Input SKU: {}", sku);
            }

            // Click around to ensure changes are applied
            webUtils.click(loc_ttlUpdateSKU);

            // Close the Update SKU popup
            webUtils.click(loc_dlgCommons_btnUpdate);
        }
    }

    /**
     * Uploads images for variations of a product.
     * <p>
     * This method opens the image upload popup for each variation and uploads the specified image files. The file paths
     * are logged for verification.
     * </p>
     *
     * @param imageFile The image files to be uploaded.
     */
    void uploadVariationImage(String... imageFile) {
        IntStream.range(0, APIGetProductDetail.getVariationValues(utilsProductInfo, defaultLanguage).size())
                .forEach(varIndex -> {
                    webUtils.click(loc_tblVariation_imgUploads, varIndex);
                    logger.info("Open upload variation image popup.");

                    Arrays.stream(imageFile)
                            .map(imgFile -> System.getProperty("user.dir") + "/src/main/resources/files/images/" + imgFile)
                            .forEach(filePath -> {
                                webUtils.uploads(loc_dlgUploadsImage_btnUploads, filePath);
                                logger.info("[Upload variation image popup] Upload images, file path: {}", filePath);
                            });

                    // Close the upload image popup
                    webUtils.click(loc_dlgCommons_btnUpdate);
                });
    }

    /**
     * Changes the status of a product.
     * <p>
     * This method retrieves the current product information, navigates to the product detail page, and updates the
     * product status if it differs from the specified status. Logs the status change for verification.
     * </p>
     *
     * @param status    The new status to set for the product.
     * @param productId The ID of the product to update.
     * @return The current instance of `BaseProductPage` for method chaining.
     */
    public BaseProductPage changeProductStatus(String status, int productId) {
        if (!status.equals(apiProductInfo.getBhStatus())) {
            // Log the status change
            logger.info("Change product status, id: {}", productId);

            // Wait for the page to load
            webUtils.getElement(loc_lblSEOSetting);

            // Change status
            webUtils.clickJS(loc_btnDeactivate);

            logger.info("Change product status from {} to {}", apiProductInfo.getBhStatus(), status);
        }
        return this;
    }

    /**
     * Deletes a product.
     * <p>
     * This method retrieves the current product information, navigates to the product detail page, and deletes the product
     * if it is not already marked as deleted. Logs the deletion for verification.
     * </p>
     */
    public void deleteProduct() {
        if (!utilsProductInfo.isDeleted()) {
            // Log the deletion
            logger.info("Delete product id: {}", apiProductInfo.getId());

            // Wait for the page to load
            webUtils.getElement(loc_lblSEOSetting);

            // Open the confirmation delete popup
            webUtils.click(loc_btnDelete);

            // Confirm and close the deleted popup
            webUtils.click(loc_dlgConfirmDelete_btnOK);
        }
    }

    /**
     * Completes the creation of a product.
     * <p>
     * This method saves the changes, verifies successful creation by checking for a success notification, and retrieves
     * the product ID after creation. Logs the completion of product creation for verification.
     * </p>
     */
    void completeCreateProduct() {
        // Save changes
        webUtils.click(loc_btnSave);

        // Check and close the success notification popup
        Assert.assertFalse(webUtils.getListElement(loc_dlgSuccessNotification, 30000).isEmpty(), "[Create product] Cannot create product.");
        webUtils.click(loc_dlgNotification_btnClose);

        // Log the product creation
        logger.info("Wait and get product id after creation.");

        // Wait for API response and get product ID
        int productId = new APIGetProductList(credentials).searchProductIdByName(utilsProductInfo.getName());

        // Log the completion of product creation
        logger.info("Complete create product, id: {}", productId);
    }

    /**
     * Completes the update of a product.
     * <p>
     * This method saves the changes and verifies the update by checking for a success notification. Logs the completion
     * of the product update for verification.
     * </p>
     */
    void completeUpdateProduct() {
        // Save changes
        webUtils.clickJS(loc_btnSave);

        // Verify and close the success notification popup
        Assert.assertFalse(webUtils.getListElement(loc_dlgSuccessNotification, 30000).isEmpty(), "[Update product] Cannot update product.");
        webUtils.click(loc_dlgNotification_btnClose);
    }

    /**
     * Removes old wholesale product configurations if they exist.
     */
    private void removeOldWholesaleConfig() {
        if (webUtils.isCheckedJS(loc_chkAddWholesalePricing)) {
            webUtils.clickJS(loc_chkAddWholesalePricing); // Uncheck to remove old config
            webUtils.click(loc_dlgConfirm_btnOK); // Confirm removal
            webUtils.click(loc_btnSave); // Save changes

            // Verify removal was successful
            boolean isSuccessNotificationPresent = !webUtils.getListElement(loc_dlgSuccessNotification).isEmpty();
            Assert.assertTrue(isSuccessNotificationPresent, "Failed to remove old wholesale configuration.");
            logger.info("Old wholesale configuration removed successfully.");

            // Refresh page
            driver.navigate().refresh();
        }
    }

    /**
     * Navigates to the wholesale pricing page for the specified product.
     *
     * @param driver      The WebDriver instance to use for interaction with the web page.
     * @param productInfo The product information containing details like ID and models.
     * @return An instance of WholesaleProductPage for further interactions and method chaining.
     */
    private WholesaleProductPage navigateToWholesaleProductPage(WebDriver driver, APIGetProductDetail.ProductInformation productInfo) {
        // Remove old wholesale product configuration if present
        removeOldWholesaleConfig();

        // Ensure the 'Add Wholesale Pricing' checkbox is selected
        webUtils.checkCheckbox(loc_chkAddWholesalePricing);

        // Click the 'Configure Wholesale Pricing' button
        webUtils.clickJS(loc_btnConfigureWholesalePricing);

        // Return a new instance of WholesaleProductPage for further actions
        return new WholesaleProductPage(driver, productInfo, defaultLanguage);
    }

    /**
     * Configures wholesale pricing for a product based on its information.
     */
    public void configWholesaleProduct() {
        // Navigate to the wholesale product page and obtain the page object
        WholesaleProductPage wholesalePage = navigateToWholesaleProductPage(driver, apiProductInfo);

        // Configure the product's wholesale pricing based on its model presence
        if (apiProductInfo.isHasModel()) {
            // Add wholesale product variation if the product has models
            wholesalePage.addWholesaleProductVariation();
        } else {
            // Add wholesale product without variation if no models are present
            wholesalePage.addWholesaleProductWithoutVariation();
        }

        // Save the wholesale product configuration
        webUtils.click(loc_btnSave);
    }

    /**
     * Removes old conversion unit configurations if they exist.
     */
    private void removeOldConversionUnit() {
        if (webUtils.isCheckedJS(loc_chkAddConversionUnit)) {
            webUtils.uncheckCheckbox(loc_chkAddConversionUnit); // Uncheck to remove old config
            webUtils.click(loc_btnSave); // Save changes

            // Verify removal was successful
            boolean isSuccessNotificationPresent = !webUtils.getListElement(loc_dlgSuccessNotification).isEmpty();
            Assert.assertTrue(isSuccessNotificationPresent, "Failed to remove old conversion unit configuration.");
            logger.info("Old conversion unit configuration removed successfully.");

            // Refresh page
            driver.navigate().refresh();
        }
    }

    /**
     * Navigates to the Conversion Unit page for the specified product.
     *
     * @param driver      The WebDriver instance to use for interaction with the web page.
     * @param credentials The API credentials required for accessing product details.
     * @param productInfo The product information containing details like ID and inventory management type.
     * @return An instance of ConversionUnitPage for further interactions and method chaining.
     */
    private ConversionUnitPage navigateToConversionUnitPage(WebDriver driver, APISellerLogin.Credentials credentials, APIGetProductDetail.ProductInformation productInfo) {
        // Remove old conversion unit configuration if present
        removeOldConversionUnit();

        // Check the 'Add Conversion Unit' checkbox to enable configuration
        webUtils.checkCheckbox(loc_chkAddConversionUnit);

        // Handle conversion unit configuration based on inventory management type
        if (apiProductInfo.getInventoryManageType().equals("IMEI_SERIAL_NUMBER")) {
            logger.info("Conversion units are not supported for products managed by IMEI/Serial at this time.");
        } else {
            // Click the 'Configure Add Conversion Unit' button
            webUtils.click(loc_btnConfigureAddConversionUnit);
        }

        // Return a new instance of ConversionUnitPage for further actions
        return new ConversionUnitPage(driver, credentials, productInfo, defaultLanguage);
    }

    /**
     * Configures the conversion unit for a product based on whether it has variations.
     */
    public void configConversionUnit() {
        // Navigate to the Conversion Unit page and obtain the page object
        ConversionUnitPage conversionUnitPage = navigateToConversionUnitPage(driver, credentials, apiProductInfo);

        // Configure the conversion unit based on whether the product has variations
        conversionUnitPage.addConversionUnitConfiguration();
    }

    /**
     * Creates a product without variations.
     * <p>
     * This method generates the product information, initializes the basic product details, inputs price and stock details
     * (if applicable), and completes the product creation process. The process is logged for tracking.
     * </p>
     *
     * @param isIMEIProduct Indicates if the product uses IMEI numbers.
     * @param branchStock   Stock quantities for each branch.
     */
    public void createWithoutVariationProduct(boolean isIMEIProduct, int... branchStock) {
        // Log the start of product creation
        logger.info("===== STEP =====> [CreateWithoutVariationProduct] START... ");

        // Generate product information
        fetchProductInformation(isIMEIProduct, false, branchStock);

        // Initialize product details
        initBasicProductInformation();
        inputWithoutVariationPrice();
        inputWithoutVariationStock();
        inputWithoutVariationProductSKU();
        completeCreateProduct();

        // Reset local variables for next test
        resetAllVariables();

        // Log the completion of product creation
        logger.info("===== STEP =====> [CreateWithoutVariationProduct] DONE!!! ");

    }

    /**
     * Creates a product with variations.
     * <p>
     * This method generates the product information, initializes the basic product details, adds variations, uploads
     * images, inputs price, stock, and SKU details, and completes the product creation process. The process is logged
     * for tracking.
     * </p>
     *
     * @param isIMEIProduct Indicates if the product uses IMEI numbers.
     * @param branchStock   Stock quantities for each branch.
     * @throws Exception If an error occurs during the process.
     */
    public void createVariationProduct(boolean isIMEIProduct, int... branchStock) throws Exception {
        // Log the start of product creation
        logger.info("===== STEP =====> [CreateVariationProduct] START... ");

        // Generate product information
        fetchProductInformation(isIMEIProduct, true, branchStock);

        // Initialize product details
        initBasicProductInformation();
        addVariations();
        uploadVariationImage("images.jpg");
        inputVariationPrice();
        inputVariationStock();
        inputVariationSKU();
        completeCreateProduct();

        // Reset local variables for next test
        resetAllVariables();

        // Log the completion of product creation
        logger.info("===== STEP =====> [CreateVariationProduct] DONE!!! ");

    }

    /**
     * Updates a product without variations.
     * <p>
     * This method generates updated product information, initializes product details, inputs price and stock details
     * (if applicable), updates SKU, and completes the product update process. The process is logged for tracking.
     * </p>
     *
     * @param branchStock Stock quantities for each branch.
     */
    public void updateWithoutVariationProduct(int... branchStock) {
        // Log the start of product update
        logger.info("===== STEP =====> [UpdateWithoutVariationProduct] START... ");

        // Generate updated product information
        fetchProductInformation(apiProductInfo.getInventoryManageType().equals("IMEI_SERIAL_NUMBER"), true, branchStock);

        // Initialize product details
        initBasicProductInformation();
        inputWithoutVariationPrice();
        updateWithoutVariationStock();
        updateWithoutVariationProductSKU();
        completeUpdateProduct();

        // Reset local variables for next test
        resetAllVariables();

        // Log the completion of product update
        logger.info("===== STEP =====> [UpdateWithoutVariationProduct] DONE!!! ");
    }

    /**
     * Updates a product with variations.
     * <p>
     * This method generates updated product information, initializes product details, and updates variations, if applicable.
     * It uploads images, inputs price, stock, and SKU details, and completes the product update process. The process
     * is logged for tracking.
     * </p>
     *
     * @param branchStock Stock quantities for each branch.
     * @return The current instance of `BaseProductPage` for method chaining.
     * @throws InterruptedException If an error occurs during the process.
     */
    public BaseProductPage updateVariationProduct(int... branchStock) throws InterruptedException {
        // Log the start of product update
        logger.info("===== STEP =====> [UpdateVariationProduct] START... ");

        // Generate updated product information
        fetchProductInformation(apiProductInfo.getInventoryManageType().equals("IMEI_SERIAL_NUMBER"), true, branchStock);

        // Initialize product details
        initBasicProductInformation();

        addVariations();
        uploadVariationImage("images.jpg");
        inputVariationPrice();
        inputVariationStock();
        inputVariationSKU();
        completeUpdateProduct();

        // Reset local variables for next test
        resetAllVariables();

        // Log the completion of product update
        logger.info("===== STEP =====> [UpdateVariationProduct] DONE!!! ");

        return this;
    }

    /**
     * Changes the status of variations for a specific product.
     * <p>
     * This method retrieves the current product information, updates the status of each variation, and logs the status
     * change process.
     * </p>
     *
     * @param productId The ID of the product whose variations' status is to be changed.
     */
    public void changeVariationStatus(int productId) {
        // Log the start of changing variation status
        logger.info("===== STEP =====> [ChangeProductStatus] START... ");

        // Get current product information
        var productInfo = fetchProductInformation(productId);

        // Update variation status
        IntStream.range(0, APIGetProductDetail.getVariationModelList(productInfo).size()).forEach(varIndex ->
                new VariationDetailPage(driver, varIndex, productInfo, credentials)
                        .changeVariationStatus(List.of("ACTIVE", "INACTIVE").get(nextInt(2)))
        );

        // Log the completion of changing variation status
        logger.info("===== STEP =====> [ChangeProductStatus] DONE!!! ");
    }

    /**
     * Edits the translations for variations of a specific product.
     * <p>
     * This method retrieves the product information, updates the name and description of each variation, and logs the
     * translation process.
     * </p>
     *
     * @param productId The ID of the product for which translations are to be edited.
     */
    public void editVariationTranslation(int productId) {
        // Log the start of adding variation translations
        logger.info("===== STEP =====> [AddVariationTranslation] START... ");

        // Get current product information
        var productInfo = fetchProductInformation(productId);

        IntStream.range(0, APIGetProductDetail.getVariationModelList(productInfo).size()).forEach(varIndex ->
                new VariationDetailPage(driver, varIndex, productInfo, credentials).updateVariationProductNameAndDescription()
        );

        // Log the completion of adding variation translations
        logger.info("===== STEP =====> [AddVariationTranslation] DONE!!! ");
    }

    /**
     * Edits the translations for a specific product.
     * <p>
     * This method navigates to the product detail page, retrieves available store languages, and adds translations for
     * each language that has not been translated yet. The process is logged for tracking.
     * </p>
     */
    public void editTranslation() {
        // Log the start of adding product translations
        logger.info("===== STEP =====> [AddProductTranslation] START... ");

        // Retrieve the list of store languages and product information
        List<String> langCodeList = new ArrayList<>(storeLanguageCodes);
        List<String> langNameList = new ArrayList<>(storeLanguageNames);

        // Remove the default language from the list
        langCodeList.remove(defaultLanguage);
        logger.info("Languages not yet translated: {}", langCodeList);

        // Open the edit translation popup
        webUtils.clickJS(loc_lblEditTranslation);

        // Add translations for each language that has not been translated yet
        langCodeList.forEach(langCode -> {
            String langName = langNameList.get(storeLanguageCodes.indexOf(langCode));
            new EditTranslationPopup(driver, langCode, langName, defaultLanguage)
                    .addTranslation(apiProductInfo);
        });

        // Save the updated translations
        completeUpdateProduct();

        // Log the completion of adding product translations
        logger.info("===== STEP =====> [AddProductTranslation] DONE!!! ");
    }

    /**
     * Adds or updates variation attributes for a product.
     * <p>
     * This method retrieves the current product information, updates attributes for each variation, and logs the process.
     * </p>
     */
    public void addVariationAttribution() {
        // Log the start of adding variation attributes
        logger.info("===== STEP =====> [AddVariationAttribution] START... ");

        // Update variation attributes
        IntStream.range(0, APIGetProductDetail.getVariationModelList(apiProductInfo).size()).forEach(varIndex ->
                new VariationDetailPage(driver, varIndex, apiProductInfo, credentials).updateAttribution()
        );

        // Log the completion of adding variation attributes
        logger.info("===== STEP =====> [AddVariationAttribution] DONE!!! ");
    }
}
