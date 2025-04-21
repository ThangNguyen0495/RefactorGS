package pages.web.seller.product.all_products;

import api.seller.login.APISellerLogin;
import api.seller.product.APIGetProductDetail;
import api.seller.product.APIGetProductList;
import api.seller.setting.APIGetBranchList;
import api.seller.setting.APIGetStoreDefaultLanguage;
import api.seller.setting.APIGetStoreLanguage;
import api.seller.setting.APIGetVATList;
import api.seller.user_feature.APIGetUserFeature;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;
import utility.PropertiesUtils;
import utility.WebUtils;
import utility.helper.ProductHelper;
import utility.helper.VariationHelper;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static api.seller.user_feature.APIGetUserFeature.*;
import static org.apache.commons.lang.math.RandomUtils.nextBoolean;

public class BaseProductPage extends BaseProductElement {
    // WebDriver and WebUtils
    private final WebDriver driver;
    private final WebUtils webUtils;

    // Logger
    private final Logger logger = LogManager.getLogger();

    /**
     * Holds the product information as it currently exists.
     * If the product is being created (new product), this will be null.
     */
    private APIGetProductDetail.ProductInformation currentProductInfo;

    /**
     * Holds the new product information that is being created or updated.
     * This is the target state for the product's data.
     */
    private APIGetProductDetail.ProductInformation newProductInfo;

    // Credentials and Information Lists
    private APISellerLogin.Credentials credentials;


    // Default Values and Flags
    /**
     * -- SETTER --
     * Sets whether discounts are not applicable.
     */
    @Setter
    private boolean noDiscount = nextBoolean();
    /**
     * -- SETTER --
     * Sets whether cost is not applicable.
     */
    @Setter
    private boolean noCost = nextBoolean();
    /**
     * -- SETTER --
     * Sets whether the product has dimensions.
     */
    @Setter
    private boolean hasDimension = false;
    /**
     * -- SETTER --
     * Sets whether SEO information should be included for the product.
     */
    @Setter
    private boolean hasSEO = false;
    /**
     * -- SETTER --
     * Sets whether inventory management should be by lot date.
     */
    @Setter
    private boolean manageByLotDate = false;
    /**
     * -- SETTER --
     * Sets whether attributions should be included for the product.
     */
    @Setter
    private boolean hasAttribution = false;
    private boolean showOnApp;
    private boolean showOnWeb;
    private boolean showInStore;
    private boolean showInGoSocial;

    // Product and Language Information
    private String defaultLanguage;
    private List<String> activeBranchNames;
    private List<Integer> allBranchesIds;
    private List<Integer> activeBranchIds;
    private List<String> storeLanguageCodes;
    private List<String> storeLanguageNames;

    // User features
    private List<UserPackage> userPackages;

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
        List<APIGetBranchList.BranchInformation> branchInfos = new APIGetBranchList(credentials).getBranchInformation();

        // Retrieve and store language information
        List<APIGetStoreLanguage.LanguageInformation> languageInfos = new APIGetStoreLanguage(credentials).getStoreLanguageInformation();

        // Retrieve the default language of the seller
        this.defaultLanguage = new APIGetStoreDefaultLanguage(credentials).getDefaultLanguage();

        // Get active branch names and IDs
        this.activeBranchNames = APIGetBranchList.getActiveBranchNames(branchInfos);
        this.activeBranchIds = APIGetBranchList.getActiveBranchIds(branchInfos);

        // Get all branches IDs
        allBranchesIds = APIGetBranchList.getBranchIds(branchInfos);

        // Get all store language codes and names
        this.storeLanguageCodes = APIGetStoreLanguage.getAllStoreLanguageCodes(languageInfos);
        this.storeLanguageNames = APIGetStoreLanguage.getAllStoreLanguageNames(languageInfos);

        // Get all user packages
        this.userPackages = new APIGetUserFeature(credentials).getUserFeature();

        // Init platform information
        this.showOnApp = hasGoAPP(userPackages);
        this.showOnWeb = hasGoWEB(userPackages);
        this.showInGoSocial = hasGoSOCIAL(userPackages);
        this.showInStore = hasGoPOS(userPackages);

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
        // Retrieve tax information (VAT details)
        var vatInfos = new APIGetVATList(credentials).getVATInformation();
        List<Integer> vatIds = APIGetVATList.getVATIds(vatInfos); // List of VAT IDs
        List<String> vatNames = APIGetVATList.getVATNames(vatInfos); // List of VAT names

        // Create an InitProductInfo object with the required parameters
        ProductHelper.InitProductInfo initProductInfo = new ProductHelper.InitProductInfo();
        initProductInfo.setCurrentProductInfo(currentProductInfo);
        initProductInfo.setHasModel(hasModel);
        initProductInfo.setNoCost(noCost);
        initProductInfo.setNoDiscount(noDiscount);
        initProductInfo.setManageByIMEI(isManagedByIMEI);
        initProductInfo.setHasSEO(hasSEO);
        initProductInfo.setHasDimension(hasDimension);
        initProductInfo.setHasLot(manageByLotDate || (currentProductInfo != null && currentProductInfo.isLotAvailable()));
        initProductInfo.setHasAttribution(hasAttribution);
        initProductInfo.setOnWeb(showOnWeb);
        initProductInfo.setOnApp(showOnApp);
        initProductInfo.setInStore(showInStore);
        initProductInfo.setInGoSOCIAL(showInGoSocial);
        initProductInfo.setAllBranchesIds(allBranchesIds);
        initProductInfo.setActiveBranchIds(activeBranchIds);
        initProductInfo.setDefaultLangCode(defaultLanguage);
        initProductInfo.setVatIds(vatIds);
        initProductInfo.setVatNames(vatNames);
        initProductInfo.setBranchStock(branchStock);

        // Generate product information using the InitProductInfo object
        this.newProductInfo = ProductHelper.generateProductInformation(initProductInfo);
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
        this.showOnApp = showOnApp && hasGoAPP(userPackages);
        this.showOnWeb = showOnWeb && hasGoWEB(userPackages);
        this.showInGoSocial = showInGoSocial && hasGoSOCIAL(userPackages);
        this.showInStore = showInStore && hasGoPOS(userPackages);
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
        this.showOnApp = hasGoAPP(userPackages);
        this.showOnWeb = hasGoWEB(userPackages);
        this.showInGoSocial = hasGoSOCIAL(userPackages);
        this.showInStore = hasGoPOS(userPackages);
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
     */
    public void navigateToUpdateProductPage(int productId) {
        // Retrieve product details from the API
        currentProductInfo = fetchProductInformation(productId);

        // Navigate to the product update page
        navigateToProductPage(productId);

    }

    /**
     * Navigates to the product update page using the given product ID.
     *
     * @param productId The ID of the product to navigate to.
     */
    private void navigateToProductPage(int productId) {
        driver.navigate().refresh();
        driver.get("%s/product/edit/%s".formatted(PropertiesUtils.getDomain(), productId));
        driver.navigate().refresh();
        logger.info("Navigated to product update page, productId: {}", productId);
    }

    /**
     * Inputs the product name into the relevant field.
     */
    void inputProductName() {
        webUtils.sendKeys(loc_txtProductName, newProductInfo.getName());
        logger.info("Input product name: {}", newProductInfo.getName());
    }

    /**
     * Inputs the product description into the relevant field.
     */
    void inputProductDescription() {
        webUtils.sendKeys(loc_txaProductDescription, newProductInfo.getDescription());
        logger.info("Input product description: {}", newProductInfo.getDescription());
    }

    /**
     * Uploads product images. Removes any existing images before uploading new ones.
     *
     * @param imageFile The image file names to upload.
     */
    void uploadProductImage(String... imageFile) {
        removeExistingImages();
        uploadNewImages(imageFile);
        uploadVariationImages(imageFile);
    }

    /**
     * Removes existing product images if present.
     */
    private void removeExistingImages() {
        List<WebElement> removeImageIcons = webUtils.getListElement(loc_icnRemoveImages);
        if (!removeImageIcons.isEmpty()) {
            IntStream.iterate(removeImageIcons.size() - 1, index -> index >= 0, index -> index - 1)
                    .forEach(index -> {
                        webUtils.clickJS(loc_icnRemoveImages, index);
                        // Work-around with alt text issue
                        if (!webUtils.getListElement(loc_dlgAltText_btnCancel).isEmpty()) {
                            webUtils.click(loc_dlgAltText_btnCancel);
                        }
                    });
            logger.info("Removed existing product images.");
        }
    }

    /**
     * Uploads new product images from the specified file names.
     *
     * @param imageFile The image file names to upload.
     */
    private void uploadNewImages(String... imageFile) {
        Arrays.stream(imageFile)
                .map(this::getImageFilePath)
                .forEach(filePath -> {
                    webUtils.uploads(imgUploads, filePath);
                    logger.info("Uploaded product image: {}", filePath);
                });
    }

    /**
     * Uploads images for product variations.
     *
     * @param imageFile The image file names to upload for variations.
     */
    private void uploadVariationImages(String... imageFile) {
        IntStream.range(0, APIGetProductDetail.getVariationValues(newProductInfo, defaultLanguage).size())
                .forEach(varIndex -> {
                    webUtils.click(loc_tblVariation_imgUploads, varIndex);
                    logger.info("Open upload variation image popup.");

                    Arrays.stream(imageFile)
                            .map(this::getImageFilePath)
                            .forEach(filePath -> {
                                webUtils.uploads(loc_dlgUploadsImage_btnUploads, filePath);
                                logger.info("[Upload variation image popup] Upload images, file path: {}", filePath);
                            });

                    // Close the upload image popup
                    webUtils.click(loc_dlgCommons_btnUpdate);
                });
    }

    /**
     * Constructs the full file path for an image given its file name.
     *
     * @param imgFile The image file name.
     * @return The full path to the image file.
     */
    private String getImageFilePath(String imgFile) {
        return System.getProperty("user.dir") + "/src/main/resources/files/images/" + imgFile;
    }

    /**
     * Selects the VAT option based on the product's tax name.
     */
    void selectVAT() {
        webUtils.click(loc_ddvSelectedVAT);
        logger.info("Opened VAT dropdown.");

        String vatName = newProductInfo.getTaxName();
        webUtils.clickJS(vatName.equals("tax.value.include") ? loc_ddvNoVAT : loc_ddvOthersVAT(vatName));
        logger.info("Selected VAT: {}", vatName);
    }

    /**
     * Inputs a SKU for a product, either with or without variations.
     *
     * @param isUpdated if true, updates the existing SKU; otherwise, creates a new SKU.
     */
    private void addProductSKU(boolean isUpdated) {
        if (newProductInfo.isHasModel()) {
            // Handle SKU input for each variation of the product
            handleVariationSKU();
            return;
        }

        // Handle SKU input for products without variations
        handleWithoutVariationSKU(isUpdated);
    }

    /**
     * Handles SKU input for each variation of the product.
     */
    private void handleVariationSKU() {
        int variationCount = APIGetProductDetail.getVariationValues(newProductInfo, defaultLanguage).size();
        // Update SKU for each variation
        IntStream.range(0, variationCount).forEach(varIndex -> processSKUForBranches(loc_tblVariation_txtSKU, varIndex));
    }

    /**
     * Handles SKU input for products without variations.
     *
     * @param isUpdated if true, updates the existing SKU; otherwise, creates a new SKU.
     */
    private void handleWithoutVariationSKU(boolean isUpdated) {
        if (isUpdated) {
            // Open the SKU update popup and update SKU for each branch
            processSKUForBranches(loc_txtWithoutVariationSKU, 0);
            return;
        }

        // Generate a new SKU and input it directly

        String sku = APIGetProductDetail.getSKUForModelAndBranch(newProductInfo, null, allBranchesIds.getFirst());
        webUtils.sendKeys(loc_txtWithoutVariationSKU, sku);
        logger.info("Input SKU for product without variation: {}", sku);
    }

    /**
     * Opens the SKU popup and inputs SKUs for each branch, either for a variation or without variations.
     *
     * @param skuLocator     the locator of the SKU element to interact with.
     * @param variationIndex the ID of the product model (variation) being processed, or null for products without variations.
     */
    private void processSKUForBranches(By skuLocator, int variationIndex) {
        // Open the update SKU popup
        webUtils.click(skuLocator, variationIndex);
        logger.info("Opened the update SKU popup.");

        // Input SKU for each branch
        IntStream.range(0, activeBranchNames.size()).forEach(branchIndex -> {
            String sku = getSKU(variationIndex, branchIndex);
            String branchName = activeBranchNames.get(branchIndex);
            webUtils.sendKeys(loc_dlgUpdateSKU_txtInputSKU(branchName), sku);
            logger.info("Entered SKU for branch '{}': {}", branchName, sku);
        });

        // Apply changes and close the popup
        webUtils.click(loc_dlgCommons_btnUpdate); // Confirm the changes
        logger.info("Update SKU popup closed after applying changes.");
    }

    /**
     * Retrieves the SKU (Stock Keeping Unit) for a specific variation and branch.
     *
     * @param variationIndex The index of the variation model. If the product has no variations, this is ignored.
     * @param branchIndex    The index of the branch for which the SKU is being retrieved.
     * @return The SKU for the specified variation and branch.
     */
    private String getSKU(int variationIndex, int branchIndex) {
        Integer modelId = newProductInfo.isHasModel()
                ? APIGetProductDetail.getVariationModelId(newProductInfo, variationIndex)
                : null;
        return APIGetProductDetail.getSKUForModelAndBranch(newProductInfo, modelId, activeBranchIds.get(branchIndex));
    }

    /**
     * Sets inventory management options based on the product's inventory type and settings.
     */
    void setManageInventory(boolean isUpdated) {
        // Check if the inventory is updated; exit early if it is
        if (isUpdated) return;

        // Determine the inventory management type
        String inventoryManageType = newProductInfo.getInventoryManageType();

        // Exit early if managing by product
        if (!inventoryManageType.equals("IMEI_SERIAL_NUMBER")) return;

        // Change manage inventory to IMEI
        webUtils.click(loc_ddlManageInventory);
        webUtils.click(loc_ddvManageInventoryByIMEI);

        // Log the inventory management type
        logger.info("Manage inventory by: IMEI");
    }

    /**
     * Manages stock by lot-date and excludes expired quantity if applicable.
     */
    private void managedStockByLot() {
        // Check if lot-date stock management is available for this product
        if (!newProductInfo.isLotAvailable()) {
            // If lot-date management is not available, return early
            return;
        }

        // Enable the "Manage Stock By Lot Date" checkbox if lot-date management is available
        webUtils.checkCheckbox(loc_chkManageStockByLotDate);

        // Check if the product should exclude expired quantity
        if (!newProductInfo.isExpiredQuality()) {
            // If excluding expired quantity is not applicable, return early
            return;
        }

        // Enable the "Exclude Expired Quantity" checkbox if applicable
        webUtils.checkCheckbox(loc_chkExcludeExpiredQuality);
    }


    /**
     * Sets the product priority.
     */
    void setPriority() {
        webUtils.sendKeys(loc_txtPriority, newProductInfo.getPriority());
        logger.info("Set product priority: {}", newProductInfo.getPriority());
    }

    /**
     * Sets product dimensions (weight, length, width, height).
     */
    private void setProductDimension() {
        setDimension(loc_txtWeight, newProductInfo.getShippingInfo().getWeight(), "weight");
        setDimension(loc_txtLength, newProductInfo.getShippingInfo().getLength(), "length");
        setDimension(loc_txtWidth, newProductInfo.getShippingInfo().getWidth(), "width");
        setDimension(loc_txtHeight, newProductInfo.getShippingInfo().getHeight(), "height");
    }

    /**
     * Sends the specified dimension value to the provided field and logs the action.
     *
     * @param dimensionLocator The locator for the dimension field.
     * @param dimension        The dimension value to be set.
     * @param dimensionName    The name of the dimension for logging purposes.
     */
    private void setDimension(By dimensionLocator, int dimension, String dimensionName) {
        webUtils.sendKeys(dimensionLocator, dimension);
        logger.info("Input {}: {}", dimensionName, dimension);
    }


    /**
     * Configures storefront display options for the product.
     */
    void configStorefrontDisplay() {
        configureDisplayOutOfStock();
        configureHideRemainingStock();
    }

    /**
     * Configures the "Display if Out of Stock" setting.
     */
    private void configureDisplayOutOfStock() {
        if (newProductInfo.isShowOutOfStock()) {
            webUtils.checkCheckbox(loc_chkDisplayIfOutOfStock);
            logger.info("Enabled display if out of stock.");
        } else {
            webUtils.uncheckCheckbox(loc_chkDisplayIfOutOfStock);
            logger.info("Disabled display if out of stock.");
        }
    }

    /**
     * Configures the "Hide Remaining Stock" setting.
     */
    private void configureHideRemainingStock() {
        if (newProductInfo.getIsHideStock()) {
            webUtils.checkCheckbox(loc_chkHideRemainingStock);
            logger.info("Enabled hide remaining stock.");
        } else {
            webUtils.uncheckCheckbox(loc_chkHideRemainingStock);
            logger.info("Disabled hide remaining stock.");
        }
    }

    /**
     * Selects the platforms where the product should be shown (App, Web, In-store, GoSocial).
     */
    private void configSellingPlatform() {
        configurePlatformApp();
        configurePlatformWeb();
        configurePlatformInStore();
        configurePlatformGoSocial();
    }

    /**
     * Configures the "Show on App" setting.
     */
    private void configurePlatformApp() {
        if (newProductInfo.isOnApp()) {
            webUtils.checkCheckbox(loc_chkApp);
            logger.info("Enabled show on App.");
        } else {
            webUtils.uncheckCheckbox(loc_chkApp);
            logger.info("Disabled show on App.");
        }
    }

    /**
     * Configures the "Show on Web" setting.
     */
    private void configurePlatformWeb() {
        if (newProductInfo.isOnWeb()) {
            webUtils.checkCheckbox(loc_chkWeb);
            logger.info("Enabled show on Web.");
        } else {
            webUtils.uncheckCheckbox(loc_chkWeb);
            logger.info("Disabled show on Web.");
        }
    }

    /**
     * Configures the "Show in-store" setting.
     */
    private void configurePlatformInStore() {
        if (newProductInfo.isInStore()) {
            webUtils.checkCheckbox(loc_chkInStore);
            logger.info("Enabled show in-store.");
        } else {
            webUtils.uncheckCheckbox(loc_chkInStore);
            logger.info("Disabled show in-store.");
        }
    }

    /**
     * Configures the "Show on GoSocial" setting.
     */
    private void configurePlatformGoSocial() {
        if (newProductInfo.isInGosocial()) {
            webUtils.checkCheckbox(loc_chkGoSocial);
            logger.info("Enabled show on GoSocial.");
        } else {
            webUtils.uncheckCheckbox(loc_chkGoSocial);
            logger.info("Disabled show on GoSocial.");
        }
    }

    /**
     * Adds and inputs attributions if applicable.
     */
    private void addProductAttribution() {
        // Remove existing attributions
        removeExistingAttributions();

        // Add new attributions
        int numOfAttributes = newProductInfo.getItemAttributes().size();
        addNewAttributions(numOfAttributes);
    }

    /**
     * Removes existing attributions associated with the current product.
     * If no product is being created (currentProductInfo is not null),
     * this method will locate and remove any existing attribution elements.
     */
    private void removeExistingAttributions() {
        // If the product is being created, exit the method early
        if (currentProductInfo == null) return;

        // Find and remove old attribution icons if any exist
        List<WebElement> deleteAttributionIcons = webUtils.getListElement(loc_icnDeleteAttribution);
        if (!deleteAttributionIcons.isEmpty()) {
            // Iterate over the list of delete icons in reverse order to click and remove them
            IntStream.iterate(deleteAttributionIcons.size() - 1, index -> index >= 0, index -> index - 1)
                    .forEach(index -> webUtils.clickJS(loc_icnDeleteAttribution, index));
            logger.info("Removed existing attributions.");
        }
    }

    /**
     * Adds the specified number of new attributions and inputs details for each attribution.
     * <p>
     * This method first clicks the button to add the specified number of new attributions
     * using JavaScript, and then iterates over the same count to input the details
     * for each new attribution.
     *
     * @param numOfAttributes The number of attributions to add and configure.
     */
    private void addNewAttributions(int numOfAttributes) {
        if (numOfAttributes == 0) return;
        // Add new attributions by clicking the 'Add Attribution' button
        IntStream.range(0, numOfAttributes).forEachOrdered(ignored -> webUtils.clickJS(loc_btnAddAttribution));
        logger.info("Added {} attributions.", numOfAttributes);

        // Input details for each new attribution
        IntStream.range(0, numOfAttributes).forEach(this::inputAttributionDetails);
    }

    /**
     * Inputs the details for a specific attribution.
     *
     * @param attIndex The index of the attribution.
     */
    private void inputAttributionDetails(int attIndex) {
        webUtils.sendKeys(loc_txtAttributionName, attIndex, newProductInfo.getItemAttributes().get(attIndex).getAttributeName());
        webUtils.sendKeys(loc_txtAttributionValue, attIndex, newProductInfo.getItemAttributes().get(attIndex).getAttributeValue());
        if (newProductInfo.getItemAttributes().get(attIndex).getIsDisplay()) {
            webUtils.checkCheckbox(loc_chkDisplayAttribute, attIndex);
        }
    }


    /**
     * Inputs SEO information including title, description, keywords, and URL.
     */
    public void inputProductSEO() {
        // SEO Title
        String title = APIGetProductDetail.retrieveSEOTitle(newProductInfo, defaultLanguage);
        webUtils.sendKeys(loc_txtSEOTitle, title);
        logger.info("SEO title: {}.", title);

        // SEO Description
        String description = APIGetProductDetail.retrieveSEODescription(newProductInfo, defaultLanguage);
        webUtils.sendKeys(loc_txtSEODescription, description);
        logger.info("SEO description: {}.", description);

        // SEO Keywords
        String keyword = APIGetProductDetail.retrieveSEOKeywords(newProductInfo, defaultLanguage);
        webUtils.sendKeys(loc_txtSEOKeywords, keyword);
        logger.info("SEO keyword: {}.", keyword);

        // SEO URL
        String url = APIGetProductDetail.retrieveSEOUrl(newProductInfo, defaultLanguage);
        webUtils.sendKeys(loc_txtSEOUrl, url);
        logger.info("SEO URL: {}.", url);
    }

    /**
     * Handles the product pricing, prioritizing variations if the product has them.
     * <p>
     * If the product has variations, the method updates prices for each variation.
     * If the product does not have variations, it handles pricing for a single product instance.
     * The function returns early once the appropriate pricing update is performed.
     * </p>
     */
    public void handleProductPrice() {
        if (newProductInfo.isHasModel()) { // Handle pricing for products with variations
            handlePriceForVariations();
            return;
        }
        // Handle pricing for products without variations
        handlePriceForWithoutVariations();
    }

    /**
     * Handles the pricing details for a product without variations.
     * <p>
     * This method inputs the listing price, selling price, and cost price for a product
     * that does not have any variations.
     * </p>
     */
    private void handlePriceForWithoutVariations() {
        // Input listing price
        webUtils.sendKeys(loc_txtWithoutVariationListingPrice, newProductInfo.getOrgPrice());
        logger.info("Listing price: {}", String.format("%,d", newProductInfo.getOrgPrice()));

        // Input selling price
        webUtils.sendKeys(loc_txtWithoutVariationSellingPrice, newProductInfo.getNewPrice());
        logger.info("Selling price: {}", String.format("%,d", newProductInfo.getNewPrice()));

        // Input cost price
        webUtils.sendKeys(loc_txtWithoutVariationCostPrice, newProductInfo.getCostPrice());
        logger.info("Cost price: {}", String.format("%,d", newProductInfo.getCostPrice()));
    }

    /**
     * Handles the pricing details for products with variations.
     * <p>
     * This method selects all variations, opens the Update Price popup, and then
     * inputs the listing price, selling price, and cost price for each variation.
     * </p>
     */
    private void handlePriceForVariations() {
        // Select all variations
        webUtils.checkCheckbox(loc_tblVariation_chkSelectAll);

        // Open the actions dropdown
        webUtils.clickJS(loc_tblVariation_lnkSelectAction);

        // Open the Update Price popup
        webUtils.click(loc_tblVariation_ddvActions);

        // Input price details for each variation
        List<String> variationValues = APIGetProductDetail.getVariationValues(newProductInfo, defaultLanguage);
        IntStream.range(0, variationValues.size())
                .forEachOrdered(varIndex -> inputVariationPrice(varIndex, variationValues.get(varIndex)));

        // Ensure changes are applied and close the Update Price popup
        webUtils.click(loc_ttlUpdatePrice);
        webUtils.click(loc_dlgCommons_btnUpdate);
        logger.info("Prices updated for all variations.");
    }

    /**
     * Inputs the pricing details for a specific variation.
     * <p>
     * This method handles the listing price, selling price, and cost price for a specific
     * variation of the product.
     * </p>
     *
     * @param varIndex  The index of the variation in the variation list.
     * @param variation The name or value of the variation.
     */
    private void inputVariationPrice(int varIndex, String variation) {
        // Input listing price
        long listingPrice = APIGetProductDetail.getVariationListingPrice(newProductInfo, varIndex);
        webUtils.sendKeys(loc_dlgUpdatePrice_txtListingPrice, varIndex, listingPrice);
        logger.info("[{}] Listing price: {}.", variation, String.format("%,d", listingPrice));

        // Input selling price
        long sellingPrice = APIGetProductDetail.getVariationSellingPrice(newProductInfo, varIndex);
        webUtils.sendKeys(loc_dlgUpdatePrice_txtSellingPrice, varIndex, sellingPrice);
        logger.info("[{}] Selling price: {}.", variation, String.format("%,d", sellingPrice));

        // Input cost price
        long costPrice = APIGetProductDetail.getVariationCostPrice(newProductInfo, varIndex);
        webUtils.sendKeys(loc_dlgUpdatePrice_txtCostPrice, varIndex, costPrice);
        logger.info("[{}] Cost price: {}.", variation, String.format("%,d", costPrice));
    }

    /**
     * Adds IMEI numbers for each branch.
     * <p>
     * This method opens the IMEI management popup, selects all branches, removes any existing IMEI numbers,
     * and then inputs new IMEI numbers based on the provided variation value and branch stock.
     * </p>
     *
     * @param varIndex The index of the product variation.
     */
    private void addIMEIForEachBranch(int varIndex) {
        // Open the dropdown to select branches
        openBranchSelection();

        // Remove old IMEI numbers
        removeOldIMEIs();

        // Loop through each branch to add new IMEI numbers
        IntStream.range(0, activeBranchNames.size()).forEach(branchIndex -> {
            int branchId = activeBranchIds.get(branchIndex);  // Get the branch ID

            // Get the stock count for the branch and the variation value
            int stock = APIGetProductDetail.getStockByModelAndBranch(newProductInfo, newProductInfo.isHasModel() ? varIndex : null, branchId);
            String variationValue = newProductInfo.isHasModel() ? "" : APIGetProductDetail.getVariationValue(newProductInfo, defaultLanguage, varIndex);

            // Add IMEI numbers for the branch's stock
            IntStream.range(0, stock).mapToObj(imeiIndex -> generateIMEI(variationValue, activeBranchNames.get(branchIndex), imeiIndex))
                    .forEach(imei -> {
                        webUtils.sendKeysToTagInput(loc_dlgAddIMEI_txtAddIMEI(activeBranchNames.get(branchIndex)), imei);
                        logger.info("Input IMEI: {}", imei.replace("\n", ""));
                    });

            // Log information about the IMEI input
            logger.info("{}[{}] Add IMEI, stock: {}",
                    !variationValue.isEmpty() ? String.format("[%s]", variationValue) : "",
                    activeBranchNames.get(branchIndex),
                    stock);
        });

        // Save the added IMEI numbers and close the popup
        webUtils.click(loc_dlgAddIMEI_btnSave);
        logger.info("Close Add IMEI popup.");
    }

    /**
     * Opens the branch selection dropdown and selects all branches if not already selected.
     */
    private void openBranchSelection() {
        webUtils.click(loc_dlgAddIMEISelectedBranch);
        logger.info("[Add IMEI popup] Open all branches dropdown.");

        if (!webUtils.isCheckedJS(loc_dlgAddIMEI_chkSelectAllBranches)) {
            webUtils.clickJS(loc_dlgAddIMEI_chkSelectAllBranches);
        } else {
            webUtils.click(loc_dlgAddIMEISelectedBranch);
        }
        logger.info("[Add IMEI popup] Select all branches.");
    }

    /**
     * Removes all old IMEI numbers by clicking the delete icon for each existing IMEI entry.
     */
    private void removeOldIMEIs() {
        int bound = webUtils.getListElement(loc_dlgAddIMEI_icnDeleteIMEI).size();
        IntStream.iterate(bound - 1, index -> index >= 0, index -> index - 1)
                .forEach(index -> webUtils.clickJS(loc_dlgAddIMEI_icnDeleteIMEI, index));
        logger.info("Remove old IMEI.");
    }

    /**
     * Generates an IMEI string based on the variation, branch name, and the current timestamp.
     *
     * @param variationValue The variation value for the IMEI.
     * @param branchName     The name of the branch.
     * @param imeiIndex      The index of the current IMEI for this branch.
     * @return The generated IMEI string.
     */
    private String generateIMEI(String variationValue, String branchName, int imeiIndex) {
        String imeiValue = String.format("%s_IMEI_%s_%s\n", branchName, Instant.now().toEpochMilli(), imeiIndex);

        // Return early if variationValue is empty
        if (variationValue.isEmpty()) {
            return imeiValue;
        }

        // Return the IMEI string with the variation value included
        return String.format("%s_%s", variationValue, imeiValue);
    }

    /**
     * Adds or updates stock quantities for each branch, considering product variations if applicable.
     * <p>
     * This method opens the stock update popup, selects all branches, inputs stock quantities into the fields,
     * and logs the actions. It handles both updating existing stock and adding new stock for branches.
     * If no stock change is detected, the process will exit early.
     * </p>
     *
     * @param modelId The ID of the variation model, or null if there are no variations.
     */
    private void addOrUpdateStockForBranches(Integer modelId) {
        // Open the branch selection dropdown
        webUtils.click(loc_dlgUpdateStock_ddvSelectedBranch);
        logger.info("[Update stock popup] Open branch selection dropdown.");

        // Select all branches
        webUtils.checkCheckbox(loc_dlgUpdateStock_chkSelectAllBranches);
        logger.info("[Update stock popup] All branches selected.");

        // Switch to the "Change Stock" tab
        webUtils.click(loc_dlgUpdateStock_tabChange);

        // Get the maximum stock to be updated
        int maximumBranchStock = APIGetProductDetail.getMaximumBranchStockForModel(newProductInfo, modelId);
        webUtils.sendKeys(loc_dlgUpdateStock_txtStockValue, maximumBranchStock + 1);
        logger.info("Apply stock: '{}' to all branches", maximumBranchStock + 1);

        // Input stock quantities for each branch
        updateBranchStockForModel(modelId);

        // Close the stock update popup
        webUtils.click(loc_dlgCommons_btnUpdate);
        logger.info("Closed Update stock popup.");
    }

    /**
     * Updates stock quantities for each branch for the given model ID, or for a product without variations.
     *
     * @param modelId The ID of the variation model, or null if there are no variations.
     */
    private void updateBranchStockForModel(Integer modelId) {
        IntStream.range(0, activeBranchNames.size()).forEach(branchIndex -> {
            String variationName = newProductInfo.isHasModel() ?
                    "[%s]".formatted(APIGetProductDetail.getVariationValue(newProductInfo, defaultLanguage, modelId)) : "";
            int branchId = activeBranchIds.get(branchIndex);
            int stock = APIGetProductDetail.getStockByModelAndBranch(newProductInfo,
                    newProductInfo.isHasModel() ? modelId : null, branchId);

            // Update or add stock for each branch
            if (!webUtils.getListElement(loc_dlgUpdateStock_txtBranchStock(activeBranchNames.get(branchIndex))).isEmpty()) {
                webUtils.sendKeys(loc_dlgUpdateStock_txtBranchStock(activeBranchNames.get(branchIndex)), stock);
                logger.info("{}[{}] Updated stock: {}", variationName, activeBranchNames.get(branchIndex), stock);
            } else {
                logger.info("{}[{}] Added stock: {}", variationName, activeBranchNames.get(branchIndex), stock);
            }
        });
    }

    /**
     * Adds stock for a product based on its variation, inventory management type, and update state.
     * <p>
     * This method determines whether the product is managed by variations or IMEI, and then handles stock input or update accordingly.
     * </p>
     *
     * @param isUpdated Boolean flag indicating whether this is an update (true) or a creation (false).
     */
    public void handleStockManagement(boolean isUpdated) {
        // If the product is managed by lot-date, handle it early and return
        if (newProductInfo.isLotAvailable()) {
            logLotManagedProduct();
            return;
        }

        // Handle stock for products with variations
        if (newProductInfo.isHasModel()) {
            handleVariationStock();
            return;
        }

        // Check if stock has changed; if not, exit the method
        if (!hasStockChangedForModel(null)) return;

        // Handle stock for product without variation and managed inventory by IMEI/Serial number
        if (newProductInfo.getInventoryManageType().equals("IMEI_SERIAL_NUMBER")) {
            handleIMEIStock();
            return;
        }

        // Otherwise, handle stock for product without variation and managed inventory by Product
        handleNormalStock(isUpdated);
    }

    /**
     * Manages stock input or updates for products that have variations.
     * <p>
     * This method iterates through each variation of the product and opens the appropriate popup for stock input or update.
     * If the product is managed by IMEI, it adds IMEI numbers for each variation; otherwise, it updates normal stock quantities for each branch.
     * </p>
     */
    private void handleVariationStock() {
        IntStream.range(0, APIGetProductDetail.getVariationValues(newProductInfo, defaultLanguage).size())
                .filter(varIndex -> hasStockChangedForModel(APIGetProductDetail.getVariationModelId(newProductInfo, varIndex))) // Filter for variations with stock changes
                .forEach(varIndex -> {
                    // Click the stock input field for the current variation
                    webUtils.clickJS(loc_tblVariation_txtStock, varIndex);

                    // Determine if the product is managed by IMEI and handle accordingly
                    if (newProductInfo.getInventoryManageType().equals("IMEI_SERIAL_NUMBER")) {
                        addIMEIForEachBranch(varIndex);  // Add IMEI stock for the variation
                    } else {
                        int modelId = APIGetProductDetail.getVariationModelId(newProductInfo, varIndex);
                        addOrUpdateStockForBranches(modelId);  // Add normal stock for the variation
                    }
                });
    }

    /**
     * Checks if there is any stock present for the given model ID.
     *
     * @param modelId The ID of the variation model, or null if there are no variations.
     * @return true if there is stock present for the specified model, false otherwise.
     */
    private boolean hasStockChangedForModel(Integer modelId) {
        // Check if there is any stock present in the arranged product info
        return APIGetProductDetail.getMaximumBranchStockForModel(newProductInfo, modelId) > 0;
    }

    /**
     * Handles stock input or update for products management inventory by IMEI.
     * <p>
     * This method opens the IMEI management popup and adds IMEI numbers for the first branch of the product.
     * </p>
     */
    private void handleIMEIStock() {
        // Open the IMEI popup for the first branch
        webUtils.click(loc_txtWithoutVariationBranchStock(activeBranchNames.getFirst()));
        logger.info("Open Add IMEI popup without variation product.");

        // Add IMEI numbers for the first branch
        addIMEIForEachBranch(0);
        logger.info("Complete add stock for IMEI product.");
    }

    /**
     * Handles stock input or update for normal products.
     * <p>
     * If the operation is an update, it opens the stock update popup. Then it iterates through all branches
     * and updates the stock quantities for each branch.
     * </p>
     *
     * @param isUpdated Boolean flag indicating whether this is an update (true) or a creation (false).
     */
    private void handleNormalStock(boolean isUpdated) {
        if (isUpdated) {
            // Open the stock update popup for the first branch
            webUtils.click(loc_txtWithoutVariationBranchStock(activeBranchNames.getFirst()));
            logger.info("Open Update stock popup.");

            // Update normal stock for without variation product
            addOrUpdateStockForBranches(null);

            return;
        }

        // Iterate through each branch and input stock
        IntStream.range(0, activeBranchNames.size()).forEach(brIndex -> {
            // Get stock for the current branch and input it
            int stock = APIGetProductDetail.getStockByModelAndBranch(newProductInfo, null, activeBranchIds.get(brIndex));
            webUtils.sendKeys(loc_txtWithoutVariationBranchStock(activeBranchNames.get(brIndex)), stock);
            logger.info("[Create][{}] Input stock: {}", activeBranchNames.get(brIndex), stock);
        });
        logger.info("[Create]Complete stock creation for Normal product.");
    }

    /**
     * Logs a message when a product is managed by lot-date, indicating that stock updates cannot be made
     * on the product detail page and must be done on the Lot detail page.
     */
    private void logLotManagedProduct() {
        logger.info("Product is managed by lot-date.");
        logger.info("Stock updates are not allowed on the product detail page.");
        logger.info("Please update stock on the Lot detail page.");
    }


    /**
     * Adds variations for a product by generating random variations, deleting any existing ones,
     * and inputting the new variation names and values.
     */
    public void addVariations() {
        if (currentProductInfo != null && currentProductInfo.isLotAvailable()) {
            // Log a message and return early if the product is managed by lot date,
            // indicating that variations cannot be updated under this management type.
            logger.info("Product managed stock by lot-date, not allowed to update variations.");
            return;
        }

        // Remove old variations from the system.
        deleteOldVariations();

        if (!newProductInfo.isHasModel()) {
            // If the product does not have any models (without variation),
            // return early as there's nothing to update.
            return;
        }

        // Generate variation names and values
        var variationGroupName = APIGetProductDetail.getVariationName(newProductInfo, defaultLanguage);
        var variationValues = APIGetProductDetail.getVariationValues(newProductInfo, defaultLanguage);

        // Generate and log variation map
        var variationMap = VariationHelper.getVariationMap(variationGroupName, variationValues);
        logger.info("Variation map: {}", variationMap);

        // Log variation list
        logger.info("Variation list: {}", variationValues);

        // Add new variations
        addNewVariationGroups(variationMap);

        // Click on variations label to save
        webUtils.click(loc_lblVariations);
    }


    /**
     * Deletes all existing variations by clicking the delete icons in reverse order.
     * <p>
     * If the product has no variations or the current product information is unavailable, no action is taken.
     * Variations are deleted from the last one to the first one, ensuring proper deletion.
     * </p>
     */
    private void deleteOldVariations() {
        // Check if current product information is available and has variations
        if (currentProductInfo == null || !currentProductInfo.isHasModel()) {
            return; // Exit if there are no variations to delete
        }

        int numberOfVariations = webUtils.getListElement(loc_btnDeleteVariation).size();
        // Iterate through the variations in reverse order and click the delete button for each
        IntStream.iterate(numberOfVariations - 1, index -> index >= 0, index -> index - 1)
                .forEach(index -> webUtils.clickJS(loc_btnDeleteVariation, index));

        logger.info("Removed old variations."); // Log the removal action
    }

    /**
     * Adds new variation groups based on the generated variation map.
     *
     * @param variationMap The map containing variation names as keys and their corresponding values as a list
     */
    private void addNewVariationGroups(Map<String, List<String>> variationMap) {
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
        new Actions(driver).moveToElement(webUtils.getElement(loc_txtVariationName)).perform();
        webUtils.sendKeys(loc_txtVariationName, groupIndex, varName);
    }


    /**
     * Inputs the variation values for a given variation group.
     *
     * @param varValues  The list of values to be input for the variation group
     * @param groupIndex The index of the variation group
     */
    private void inputVariationValues(List<String> varValues, int groupIndex) {
        varValues.forEach(varValue -> {
            webUtils.getElement(loc_txtVariationValue, groupIndex).sendKeys(varValue);
            // Wait for suggestion to appear
            WebUtils.sleep(1000);

            // Complete the input of variation value by pressing Enter
            webUtils.getElement(loc_txtVariationValue, groupIndex).sendKeys(Keys.chord(Keys.ENTER));
            logger.info("Input variation value: {}", varValue);
        });
    }

    /**
     * Changes the status of a product.
     * <p>
     * This method retrieves the current product information, navigates to the product detail page, and updates the
     * product status if it differs from the specified status. Logs the status change for verification.
     * </p>
     */
    private void changeProductStatus() {
        // Log the status change
        logger.info("Change product status, id: {}", newProductInfo.getId());

        // Change status
        webUtils.click(loc_btnDeactivate);

        logger.info("Change product status to {}", newProductInfo.getBhStatus());
    }

    /**
     * Deletes a product.
     * <p>
     * This method retrieves the current product information, navigates to the product detail page, and deletes the product
     * if it is not already marked as deleted. Logs the deletion for verification.
     * </p>
     */
    public void deleteProduct() {
        if (currentProductInfo.isDeleted()) {
            return;
        }

        // Log the deletion
        logger.info("Delete product id: {}", currentProductInfo.getId());

        // Open the confirmation delete popup
        webUtils.click(loc_btnDelete);

        // Confirm and close the deleted popup
        webUtils.click(loc_dlgConfirmDelete_btnOK);
    }


    /**
     * Saves the changes made to the product (either create or update).
     * <p>
     * This method clicks the "Save" button, waits for a success notification, and closes the notification popup.
     * If the operation is an update, it returns immediately. For product creation, it waits for the API response
     * to retrieve the product ID, logs the completion of the process, and stores the product ID.
     * </p>
     *
     * @param isUpdate Indicates if the operation is an update (true) or a product creation (false).
     */
    void saveChanges(boolean isUpdate) {
        // Ensure the Save button is enabled; if not, deactivate the current state
        if (webUtils.isDisabledJS(loc_btnSave)) {
            webUtils.click(loc_btnDeactivate); // Click the Deactivate button if the Save button is disabled
        }

        // Save the product by clicking the "Save" button
        webUtils.click(loc_btnSave);

        // Ensure the success notification popup appears and close it
        Assert.assertFalse(webUtils.getListElement(loc_dlgSuccessNotification, 60_000).isEmpty(), isUpdate ? "[Update product] Cannot update product." : "[Create product] Cannot create product.");
        webUtils.click(loc_dlgNotification_btnClose);

        // Log that the process is waiting for the save action to complete
        logger.info("Wait save changes.");

        // Return immediately if it's an update operation
        if (isUpdate) return;

        // Wait product creation
        WebUtils.sleep(3000);

        // Wait for the API response to retrieve the new product's ID
        int productId = new APIGetProductList(credentials).searchProductIdByName(newProductInfo.getName());

        // Log the completion of product creation with the new product ID
        logger.info("Complete create product, id: {}", productId);

        // Store the product ID in utilsProductInfo
        newProductInfo.setId(productId);
    }

    /**
     * Removes old wholesale product configurations if they exist.
     */
    private void removeOldWholesaleConfig() {
        if (!webUtils.isCheckedJS(loc_chkAddWholesalePricing)) {
            return;
        }

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

        // Navigate to create wholesale pricing by URL
        driver.get("%s/product/wholesale-price/create/%d".formatted(PropertiesUtils.getDomain(), productInfo.getId()));

        // Return a new instance of WholesaleProductPage for further actions
        return new WholesaleProductPage(driver, productInfo, defaultLanguage);
    }

    /**
     * Configures wholesale pricing for a product based on its information.
     */
    public void configWholesaleProduct() {
        // Get latest product information
        fetchProductInformation(currentProductInfo.getId());

        // Navigate to the wholesale product page and obtain the page object
        WholesaleProductPage wholesalePage = navigateToWholesaleProductPage(driver, currentProductInfo);

        // Configure the product's wholesale pricing based on its model presence
        if (currentProductInfo.isHasModel()) {
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
        if (!webUtils.isCheckedJS(loc_chkAddConversionUnit)) {
            return;
        }

        webUtils.uncheckCheckbox(loc_chkAddConversionUnit); // Uncheck to remove old config
        webUtils.click(loc_btnSave); // Save changes

        // Verify removal was successful
        boolean isSuccessNotificationPresent = !webUtils.getListElement(loc_dlgSuccessNotification).isEmpty();
        Assert.assertTrue(isSuccessNotificationPresent, "Failed to remove old conversion unit configuration.");
        logger.info("Old conversion unit configuration removed successfully.");

        // Refresh page
        driver.navigate().refresh();
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
        // Handle conversion unit configuration based on inventory management type
        if (currentProductInfo.getInventoryManageType().equals("IMEI_SERIAL_NUMBER")) {
            logger.info("Conversion units are not supported for products managed by IMEI/Serial at this time.");
            return new ConversionUnitPage(driver, credentials, productInfo, defaultLanguage);
        }
        // Remove old conversion unit configuration if present
        removeOldConversionUnit();

        // Check the 'Add Conversion Unit' checkbox to enable configuration
        webUtils.checkCheckbox(loc_chkAddConversionUnit);

        // Click the 'Configure Add Conversion Unit' button
        webUtils.click(loc_btnConfigureAddConversionUnit);


        // Return a new instance of ConversionUnitPage for further actions
        return new ConversionUnitPage(driver, credentials, productInfo, defaultLanguage);
    }

    /**
     * Configures the conversion unit for a product based on whether it has variations.
     */
    public void configConversionUnit() {
        // Navigate to the Conversion Unit page and obtain the page object
        ConversionUnitPage conversionUnitPage = navigateToConversionUnitPage(driver, credentials, currentProductInfo);

        // Configure the conversion unit based on whether the product has variations
        conversionUnitPage.addConversionUnitConfiguration();
    }

    /**
     * Creates or updates a product based on the provided parameters.
     * <p>
     * This method generates the product information, handles variations (if applicable), initializes basic product details,
     * uploads images, manages pricing, stock, and SKU details, and completes the product creation or update process.
     * The process is logged for tracking.
     * </p>
     *
     * @param hasModel      Indicates if the product has variations (models).
     * @param isIMEIProduct Indicates if the product uses IMEI numbers.
     * @param branchStock   Stock quantities for each branch.
     */
    private void manageProduct(boolean hasModel, boolean isIMEIProduct, int... branchStock) {
        // Generate product information
        fetchProductInformation(isIMEIProduct, hasModel, branchStock);

        // Determine if the product is being updated.
        // If newProductInfo.getId() is not null, the product already exists and is being updated.
        boolean isUpdate = (newProductInfo.getId() != null);


        // Log the start of product management
        logger.info("Start the process of creating/updating product");

        // Add variations first to input prices, stock, and upload images related to variations
        addVariations();

        // Initialize basic product details
        inputProductName();
        inputProductDescription();

        // Upload product images
        uploadProductImage("images.png");

        // Configure product tax and inventory settings
        selectVAT();
        setManageInventory(isUpdate);
        managedStockByLot();

        // Configure storefront display settings
        configStorefrontDisplay();

        // Set additional product attributes
        setPriority();
        setProductDimension();
        configSellingPlatform();
        addProductAttribution();

        // Input SEO information
        inputProductSEO();

        // Handle pricing, stock management, and SKU for the product
        handleProductPrice();
        handleStockManagement(isUpdate);
        addProductSKU(isUpdate);

        // Complete the product creation or update process
        saveChanges(isUpdate);

        // Reset local variables for the next test
        resetAllVariables();
    }

    /**
     * Creates a product with or without variations based on the provided parameters.
     * <p>
     * This method delegates the product creation process to the {@link #manageProduct(boolean, boolean, int...)}
     * method, specifying that the product is being created (not updated).
     * It handles the entire product creation lifecycle, including setting stock quantities per branch.
     * </p>
     *
     * @param hasModel                 Indicates whether the product has variations (models) or not.
     * @param isManagedInventoryByIMEI Indicates whether the product uses IMEI numbers for inventory management.
     * @param branchStock              Stock quantities for each branch.
     * @return The current {@code BaseProductPage} instance for method chaining.
     */
    public BaseProductPage createProduct(boolean hasModel, boolean isManagedInventoryByIMEI, int... branchStock) {
        manageProduct(hasModel, isManagedInventoryByIMEI, branchStock);
        return this;
    }

    /**
     * Updates an existing product with or without variations based on the provided parameters.
     * <p>
     * This method delegates the product update process to the {@link #manageProduct(boolean, boolean, int...)}
     * method, specifying that the product is being updated (not created).
     * It allows updating stock quantities per branch and other product details.
     * </p>
     *
     * @param hasModel    Indicates whether the product has variations (models) or not.
     * @param branchStock Stock quantities for each branch.
     * @return The current {@code BaseProductPage} instance for method chaining.
     */
    public BaseProductPage updateProduct(boolean hasModel, int... branchStock) {
        manageProduct(hasModel, currentProductInfo.getInventoryManageType().equals("IMEI_SERIAL_NUMBER"), branchStock);
        return this;
    }

    /**
     * Verifies the product information by comparing the arranged product information
     * with the expected product information retrieved from the API. It checks various
     * attributes of the product to ensure they match the expected values.
     */
    public void verifyProductInformation() {
        // Log the start of the product information verification process
        logger.info("Start the process of creating/updating the product");

        // Determine if the product needs to be created based on the API response
        boolean isCreate = currentProductInfo == null;

        // Get the product ID from either the arranged product info or the API response
        int productId = isCreate ? newProductInfo.getId() : currentProductInfo.getId();

        // Retrieve expected product information from the API
        var actualProductInfo = new APIGetProductDetail(credentials).getProductInformation(productId);

        // Check product name
        Assert.assertEquals(actualProductInfo.getName(), newProductInfo.getName(),
                String.format("Product name must be '%s', but found '%s'", newProductInfo.getName(), actualProductInfo.getName()));

        // Check product description, stripping HTML tags
        Assert.assertEquals(actualProductInfo.getDescription().replaceAll("<.*?>", ""), newProductInfo.getDescription(),
                String.format("Product description must be '%s', but found '%s'", newProductInfo.getDescription(), actualProductInfo.getDescription()));

        // Check VAT ID
        int expectedVatId = newProductInfo.getTaxId();
        int actualVatId = actualProductInfo.getTaxId();
        Assert.assertEquals(actualVatId, expectedVatId,
                "Product VAT ID must be '%d', but found '%d'".formatted(expectedVatId, actualVatId));

        // Check VAT name
        String expectedVatName = newProductInfo.getTaxName();
        String actualVatName = actualProductInfo.getTaxName();
        Assert.assertEquals(actualVatName, expectedVatName,
                "Product VAT name must be '%s', but found '%s'".formatted(expectedVatName, actualVatName));

        // Check listing price
        var expectedOrgPrice = newProductInfo.isHasModel() ? APIGetProductDetail.getVariationListingPrice(newProductInfo) : newProductInfo.getOrgPrice();
        var actualOrgPrice = newProductInfo.isHasModel() ? APIGetProductDetail.getVariationListingPrice(actualProductInfo) : actualProductInfo.getOrgPrice();
        Assert.assertEquals(actualOrgPrice, expectedOrgPrice,
                "Listing price must be '%s', but found '%s'".formatted(expectedOrgPrice, actualOrgPrice));

        // Check selling price
        var expectedNewPrice = newProductInfo.isHasModel() ? APIGetProductDetail.getVariationSellingPrice(newProductInfo) : newProductInfo.getNewPrice();
        var actualNewPrice = newProductInfo.isHasModel() ? APIGetProductDetail.getVariationSellingPrice(actualProductInfo) : actualProductInfo.getNewPrice();
        Assert.assertEquals(actualNewPrice, expectedNewPrice,
                "Selling price must be '%s', but found '%s'".formatted(expectedNewPrice, actualNewPrice));

        // Check cost price
        var expectedCostPrice = newProductInfo.isHasModel() ? APIGetProductDetail.getVariationCostPrice(newProductInfo) : newProductInfo.getCostPrice();
        var actualCostPrice = newProductInfo.isHasModel() ? APIGetProductDetail.getVariationCostPrice(actualProductInfo) : actualProductInfo.getCostPrice();
        Assert.assertEquals(actualCostPrice, expectedCostPrice,
                "Cost price must be '%s', but found '%s'".formatted(expectedCostPrice, actualCostPrice));

        // Check online shop display settings
        Assert.assertEquals(actualProductInfo.isShowOutOfStock(), newProductInfo.isShowOutOfStock(),
                "Display out of stock must be '%s', but found '%s'".formatted(newProductInfo.isShowOutOfStock(), actualProductInfo.isShowOutOfStock()));
        Assert.assertEquals(actualProductInfo.getIsHideStock(), newProductInfo.getIsHideStock(),
                "Hide remaining stock must be '%s', but found '%s'".formatted(newProductInfo.getIsHideStock(), actualProductInfo.getIsHideStock()));

        // Retrieve expected and actual stock maps
        var expectedStockMap = new ArrayList<>(APIGetProductDetail.getModelBranchStockMap(newProductInfo).values());
        var actualStockMap = new ArrayList<>(APIGetProductDetail.getModelBranchStockMap(actualProductInfo).values());

        // Iterate through both expected stock map and active branch IDs
        IntStream.range(0, expectedStockMap.size()).forEach(modelIndex -> {
            // Get the expected and actual stock maps for the current model index
            Map<Integer, Integer> expectedStock = expectedStockMap.get(modelIndex);
            Map<Integer, Integer> actualStock = actualStockMap.get(modelIndex);

            // Iterate through all branch IDs
            allBranchesIds.forEach(branchId -> {
                // Safely get actual and expected stock values
                int actualStockValue = actualStock.getOrDefault(branchId, 0);
                int expectedStockValue = expectedStock.getOrDefault(branchId, 0);

                // Assert the actual and expected stocks are equal
                Assert.assertEquals(actualStockValue, expectedStockValue,
                        "[ModelIndex: %d, BranchId: %d] Branch stock must be '%s', but found '%s'".formatted(modelIndex, branchId, expectedStockValue, actualStockValue));
            });
        });

        // Check inventory management type
        String expectedManagedInventoryType = newProductInfo.getInventoryManageType();
        String actualManagedInventoryType = actualProductInfo.getInventoryManageType();
        Assert.assertEquals(actualManagedInventoryType, expectedManagedInventoryType,
                "Product inventory must be managed by '%s', but found '%s'".formatted(expectedManagedInventoryType, actualManagedInventoryType));

        // Check lot availability
        boolean expectedLotAvailable = newProductInfo.isLotAvailable();
        boolean actualLotAvailable = actualProductInfo.isLotAvailable();
        Assert.assertEquals(actualLotAvailable, expectedLotAvailable,
                "Product stock must be managed by lot '%s', but found '%s'".formatted(expectedLotAvailable, actualLotAvailable));

        // Check expired quality exclusion
        boolean expectedExcludeExpired = newProductInfo.isExpiredQuality();
        boolean actualExcludeExpired = actualProductInfo.isExpiredQuality();
        Assert.assertEquals(actualExcludeExpired, expectedExcludeExpired,
                "Exclude expired quality must be '%s', but found '%s'".formatted(expectedExcludeExpired, actualExcludeExpired));

        // Retrieve expected and actual SKU maps
        var expectedSKUMap = new ArrayList<>(APIGetProductDetail.getModelBranchSKUMap(newProductInfo).values());
        var actualSKUMap = new ArrayList<>(APIGetProductDetail.getModelBranchSKUMap(actualProductInfo).values());

        // Iterate through both expected SKU map and all branch IDs
        IntStream.range(0, expectedSKUMap.size()).forEach(modelIndex -> {
            // Get the expected and actual SKU maps for the current model index
            Map<Integer, String> expectedSKU = expectedSKUMap.get(modelIndex);
            Map<Integer, String> actualSKU = actualSKUMap.get(modelIndex);

            // Iterate through all branch IDs
            allBranchesIds.forEach(branchId -> {
                // If creating a product without variations, only the first branch allows SKU input.
                // Skip SKU checks for branches other than the first one.
                if (currentProductInfo == null && !newProductInfo.isHasModel()
                    && !Objects.equals(branchId, allBranchesIds.getFirst())) {
                    return;
                }

                // Safely retrieve actual and expected SKU values
                String actualSKUValue = actualSKU.getOrDefault(branchId, "");
                String expectedSKUValue = expectedSKU.getOrDefault(branchId, "");

                // Assert that the actual and expected SKU values are equal
                Assert.assertEquals(actualSKUValue, expectedSKUValue,
                        "[ModelIndex: %d, BranchId: %d] Branch SKU must be '%s', but found '%s'"
                                .formatted(modelIndex, branchId, expectedSKUValue, actualSKUValue));
            });
        });

        // Check SEO attributes
        String expectedSEOTitle = APIGetProductDetail.retrieveSEOTitle(newProductInfo, defaultLanguage);
        String actualSEOTitle = APIGetProductDetail.retrieveSEOTitle(actualProductInfo, defaultLanguage);
        Assert.assertEquals(actualSEOTitle, expectedSEOTitle,
                "Product SEO title must be '%s', but found '%s'".formatted(expectedSEOTitle, actualSEOTitle));

        String expectedSEODescription = APIGetProductDetail.retrieveSEODescription(newProductInfo, defaultLanguage);
        String actualSEODescription = APIGetProductDetail.retrieveSEODescription(actualProductInfo, defaultLanguage);
        Assert.assertEquals(actualSEODescription, expectedSEODescription,
                "Product SEO description must be '%s', but found '%s'".formatted(expectedSEODescription, actualSEODescription));

        String expectedSEOKeyword = APIGetProductDetail.retrieveSEOKeywords(newProductInfo, defaultLanguage);
        String actualSEOKeyword = APIGetProductDetail.retrieveSEOKeywords(actualProductInfo, defaultLanguage);
        Assert.assertEquals(actualSEOKeyword, expectedSEOKeyword,
                "Product SEO keyword must be '%s', but found '%s'".formatted(expectedSEOKeyword, actualSEOKeyword));

        String expectedSEOUrl = APIGetProductDetail.retrieveSEOUrl(newProductInfo, defaultLanguage);
        String actualSEOUrl = APIGetProductDetail.retrieveSEOUrl(actualProductInfo, defaultLanguage);
        Assert.assertEquals(actualSEOUrl, expectedSEOUrl,
                "Product SEO URL must be '%s', but found '%s'".formatted(expectedSEOUrl, actualSEOUrl));

        // Check shipping information
        var expectedShippingInfo = newProductInfo.getShippingInfo();
        var actualShippingInfo = actualProductInfo.getShippingInfo();
        Assert.assertEquals(actualShippingInfo, expectedShippingInfo,
                "Product shipping info must be '%s' but found '%s'".formatted(expectedShippingInfo, actualShippingInfo));

        // Check selling platform availability
        Assert.assertEquals(actualProductInfo.isOnWeb(), newProductInfo.isOnWeb(),
                "Web platform config must be '%s', but found '%s'".formatted(newProductInfo.isOnWeb(), actualProductInfo.isOnWeb()));
        Assert.assertEquals(actualProductInfo.isOnApp(), newProductInfo.isOnApp(),
                "App platform config must be '%s', but found '%s'".formatted(newProductInfo.isOnApp(), actualProductInfo.isOnApp()));
        Assert.assertEquals(actualProductInfo.isInStore(), newProductInfo.isInStore(),
                "InStore platform config must be '%s', but found '%s'".formatted(newProductInfo.isInStore(), actualProductInfo.isInStore()));
        Assert.assertEquals(actualProductInfo.isInGosocial(), newProductInfo.isInGosocial(),
                "GoSOCIAL platform config must be '%s', but found '%s'".formatted(newProductInfo.isInGosocial(), actualProductInfo.isInGosocial()));

        // Check product attribution
        var expectedAttribution = newProductInfo.getItemAttributes();
        var actualAttribution = actualProductInfo.getItemAttributes();

        Assert.assertEquals(actualAttribution, expectedAttribution,
                "Product attribution must be '%s', but found '%s'".formatted(expectedAttribution, actualAttribution));

        // Check variations if the product has models
        if (newProductInfo.isHasModel()) {
            var expectedVariationName = APIGetProductDetail.getVariationName(newProductInfo, defaultLanguage);
            var actualVariationName = APIGetProductDetail.getVariationName(actualProductInfo, defaultLanguage);
            Assert.assertEquals(actualVariationName, expectedVariationName,
                    "Variation name must be '%s', but found '%s'".formatted(expectedVariationName, actualVariationName));

            var expectedVariationValues = APIGetProductDetail.getVariationValues(newProductInfo, defaultLanguage);
            var actualVariationValues = APIGetProductDetail.getVariationValues(actualProductInfo, defaultLanguage);
            Assert.assertEquals(actualVariationValues, expectedVariationValues,
                    "Variation values must be '%s', but found '%s'".formatted(expectedVariationValues, actualVariationValues));
        }
    }


    /**
     * Changes the status of variations for a specific product.
     * <p>
     * This method retrieves the current product information, updates the status of each variation,
     * verifies the status changes, and logs the entire process. The product status is changed last to
     * ensure it accurately reflects the final state after all variations have been processed.
     * </p>
     */
    public void updateProductStatus() {
        // Log the start of changing the product variation statuses
        logger.info("Start the process of updating product statues");

        // Generate new product/variation status
        ProductHelper.InitProductInfo initProductInfo = new ProductHelper.InitProductInfo();
        initProductInfo.setCurrentProductInfo(currentProductInfo);
        initProductInfo.setChangStatus(true);
        newProductInfo = ProductHelper.generateProductInformation(initProductInfo);

        // Change product status
        changeProductStatus();

        // Wait for the product status to change before verification
        // This delay allows the system time to update the product status
        WebUtils.sleep(1000);

        // Verify that the product status has been correctly updated
        var actualProductInfoAfterChangeProductStatus = new APIGetProductDetail(credentials).getProductInformation(newProductInfo.getId());

        // Ensure that the product statuses are equal to the expected status
        Assert.assertEquals(actualProductInfoAfterChangeProductStatus.getBhStatus(), newProductInfo.getBhStatus(),
                "Product status must be '%s', but found '%s'".formatted(newProductInfo.getBhStatus(), actualProductInfoAfterChangeProductStatus.getBhStatus()));

        // Determine the number of variations available for the product
        int numOfVariations = APIGetProductDetail.getVariationModelList(newProductInfo).size();

        // Update the status of each variation using the VariationDetailPage
        IntStream.range(0, numOfVariations).forEach(varIndex -> {
            // Check if the variation status has changed before updating
            if (Objects.equals(APIGetProductDetail.getVariationStatus(currentProductInfo, varIndex),
                    APIGetProductDetail.getVariationStatus(newProductInfo, varIndex))) {
                new VariationDetailPage(driver, varIndex, newProductInfo)
                        .navigateToVariationDetailPage()
                        .changeVariationStatus();
            }
        });

        // Verify that the variation statuses have been correctly updated
        var actualProductInfoAfterChangeVariationStatus = new APIGetProductDetail(credentials).getProductInformation(newProductInfo.getId());

        // Retrieve the expected statuses of the variations
        var expectedStatus = IntStream.range(0, numOfVariations)
                .mapToObj(index -> APIGetProductDetail.getVariationStatus(newProductInfo, index))
                .collect(Collectors.toCollection(ArrayList::new));

        // Retrieve the actual statuses of the variations and assert they match the expected statuses
        var actualStatus = IntStream.range(0, numOfVariations)
                .mapToObj(index -> APIGetProductDetail.getVariationStatus(actualProductInfoAfterChangeVariationStatus, index))
                .collect(Collectors.toCollection(ArrayList::new));

        // Ensure that the actual statuses are equal to the expected statuses
        Assert.assertEquals(actualStatus, expectedStatus,
                "Variation status must be '%s', but found '%s'".formatted(expectedStatus, actualStatus));
    }

    /**
     * Edits the translations for variations of a specific product.
     * <p>
     * This method retrieves the product information, updates the name and description of each variation, and logs the
     * translation process.
     * </p>
     */
    public void updateProductTranslation() {
        // Log the start of adding variation translations
        logger.info("Start the process of adding product translation");

        // Generate product translation
        ProductHelper.InitProductInfo initProductInfo = new ProductHelper.InitProductInfo();
        initProductInfo.setCurrentProductInfo(currentProductInfo);
        initProductInfo.setLangCodes(storeLanguageCodes);
        initProductInfo.setDefaultLangCode(defaultLanguage);
        newProductInfo = ProductHelper.generateProductInformation(initProductInfo);

        // Get list untranslated languages
        List<String> untranslatedLanguageCodes = getUntranslatedLanguageCodes();
        List<String> untranslatedLanguageNames = getUntranslatedLanguageNames();
        logger.info("Languages not yet translated: {}", untranslatedLanguageCodes);

        // Add product translation
        addProductTranslation(untranslatedLanguageCodes, untranslatedLanguageNames);

        // Add variation translation
        IntStream.range(0, APIGetProductDetail.getVariationModelList(newProductInfo).size()).forEach(varIndex ->
                new VariationDetailPage(driver, varIndex, newProductInfo)
                        .navigateToVariationDetailPage()
                        .updateVariationProductNameAndDescription(untranslatedLanguageCodes,
                                untranslatedLanguageNames,
                                defaultLanguage)
        );

        // Get current product information
        var actualProductInfo = new APIGetProductDetail(credentials).getProductInformation(newProductInfo.getId());

        // Verify translation for each language code
        storeLanguageCodes.forEach(languageCode -> {
            // Check main product name
            var actualMainProductName = APIGetProductDetail.getMainProductName(actualProductInfo, languageCode);
            var expectedMainProductName = APIGetProductDetail.getMainProductName(newProductInfo, languageCode);
            // Verify the main product name for the given language
            Assert.assertEquals(actualMainProductName, expectedMainProductName,
                    "[%s] Main product name must be '%s', but found '%s'"
                            .formatted(languageCode, expectedMainProductName, actualMainProductName));

            // Check main product description
            var actualMainProductDescription = APIGetProductDetail.getMainProductDescription(actualProductInfo, languageCode).replaceAll("<.*?>", "");
            var expectedMainProductDescription = APIGetProductDetail.getMainProductDescription(newProductInfo, languageCode);
            // Verify the main product description for the given language
            Assert.assertEquals(actualMainProductDescription, expectedMainProductDescription,
                    "[%s] Main product description must be '%s', but found '%s'"
                            .formatted(languageCode, expectedMainProductDescription, actualMainProductDescription));

            // If the product has models (variations), validate them
            if (newProductInfo.isHasModel()) {
                // Check variation name
                var actualVariationName = APIGetProductDetail.getVariationName(actualProductInfo, languageCode);
                var expectedVariationName = APIGetProductDetail.getVariationName(newProductInfo, languageCode);
                // Verify the variation name for the given language
                Assert.assertEquals(actualVariationName, expectedVariationName,
                        "[%s] Variation name must be '%s', but found '%s'"
                                .formatted(languageCode, expectedVariationName, actualVariationName));

                // Check variation values
                var actualVariationValues = APIGetProductDetail.getVariationValues(actualProductInfo, languageCode);
                var expectedVariationValues = APIGetProductDetail.getVariationValues(newProductInfo, languageCode);
                // Verify the variation values for the given language
                Assert.assertEquals(actualVariationValues, expectedVariationValues,
                        "[%s] Variation values must be '%s', but found '%s'"
                                .formatted(languageCode, expectedVariationValues, actualVariationValues));

                // Check each model's version name and description
                newProductInfo.getModels().forEach(model -> {
                    // Check version product name
                    var actualVersionProductName = APIGetProductDetail.getVersionName(actualProductInfo, model.getId(), languageCode);
                    var expectedVersionProductName = APIGetProductDetail.getVersionName(newProductInfo, model.getId(), languageCode);
                    // Verify the version product name for the given model and language
                    Assert.assertEquals(actualVersionProductName, expectedVersionProductName,
                            "[%s] Version product name for model ID '%s' must be '%s', but found '%s'"
                                    .formatted(languageCode, model.getId(), expectedVersionProductName, actualVersionProductName));

                    // Check version product description
                    var actualVersionProductDescription = APIGetProductDetail.getVersionDescription(actualProductInfo, model.getId(), languageCode).replaceAll("<.*?>", "");
                    var expectedVersionProductDescription = APIGetProductDetail.getVersionDescription(newProductInfo, model.getId(), languageCode);
                    // Verify the version product description for the given model and language
                    Assert.assertEquals(actualVersionProductDescription, expectedVersionProductDescription,
                            "[%s] Version product description for model ID '%s' must be '%s', but found '%s'"
                                    .formatted(languageCode, model.getId(), expectedVersionProductDescription, actualVersionProductDescription));
                });
            }

            // Check SEO title
            var actualSEOTitle = APIGetProductDetail.retrieveSEOTitle(actualProductInfo, languageCode);
            var expectedSEOTitle = APIGetProductDetail.retrieveSEOTitle(newProductInfo, languageCode);
            // Verify the SEO title for the given language
            Assert.assertEquals(actualSEOTitle, expectedSEOTitle,
                    "[%s] SEO title must be '%s', but found '%s'"
                            .formatted(languageCode, expectedSEOTitle, actualSEOTitle));

            // Check SEO description
            var actualSEODescription = APIGetProductDetail.retrieveSEODescription(actualProductInfo, languageCode);
            var expectedSEODescription = APIGetProductDetail.retrieveSEODescription(newProductInfo, languageCode);
            // Verify the SEO description for the given language
            Assert.assertEquals(actualSEODescription, expectedSEODescription,
                    "[%s] SEO description must be '%s', but found '%s'"
                            .formatted(languageCode, expectedSEODescription, actualSEODescription));

            // Check SEO keywords
            var actualSEOKeywords = APIGetProductDetail.retrieveSEOKeywords(actualProductInfo, languageCode);
            var expectedSEOKeywords = APIGetProductDetail.retrieveSEOKeywords(newProductInfo, languageCode);
            // Verify the SEO keywords for the given language
            Assert.assertEquals(actualSEOKeywords, expectedSEOKeywords,
                    "[%s] SEO keywords must be '%s', but found '%s'"
                            .formatted(languageCode, expectedSEOKeywords, actualSEOKeywords));

            // Check SEO URL
            var actualSEOUrl = APIGetProductDetail.retrieveSEOUrl(actualProductInfo, languageCode);
            var expectedSEOUrl = APIGetProductDetail.retrieveSEOUrl(newProductInfo, languageCode);
            // Verify the SEO URL for the given language
            Assert.assertEquals(actualSEOUrl, expectedSEOUrl,
                    "[%s] SEO URL must be '%s', but found '%s'"
                            .formatted(languageCode, expectedSEOUrl, actualSEOUrl));
        });
    }

    /**
     * Adds translations for a specific product for all untranslated languages.
     * <p>
     * This method navigates to the product detail page and opens the edit translation popup.
     * It then retrieves a list of untranslated language codes and corresponding language names,
     * and iterates through each, adding translations for those languages. After all translations
     * are added, it closes the translation popup and saves the updates.
     * </p>
     *
     * @param untranslatedLanguageCodes A list of language codes that have not yet been translated.
     * @param untranslatedLanguageNames A list of language names corresponding to the untranslated language codes.
     */
    private void addProductTranslation(List<String> untranslatedLanguageCodes, List<String> untranslatedLanguageNames) {
        // Update new product name
        inputProductName();

        // Update product description
        inputProductDescription();

        // Update product SEO
        inputProductSEO();

        // Add translations for each untranslated language
        untranslatedLanguageCodes.forEach(languageCode -> {
            // Get language name from language code
            String languageName = untranslatedLanguageNames.get(untranslatedLanguageCodes.indexOf(languageCode));

            // Open edit translation popup
            openEditTranslationPopup();

            // Add translation then close popup
            new EditTranslationPopup(driver).addTranslation(newProductInfo, languageCode, languageName)
                    .closeTranslationPopup();
        });

        // Save translation
        saveChanges(true);
    }

    /**
     * Retrieves the list of store languages that have not been translated yet,
     * excluding the default language.
     *
     * @return a list of untranslated language codes
     */
    private List<String> getUntranslatedLanguageCodes() {
        List<String> languageCodes = new ArrayList<>(storeLanguageCodes);
        languageCodes.remove(defaultLanguage);
        return languageCodes;
    }

    /**
     * Retrieves the list of store language names that have not been translated yet,
     * excluding the default language.
     * <p>
     * This method creates a copy of the available store language names and removes the name corresponding
     * to the default language code from the list.
     * </p>
     *
     * @return A list of untranslated store language names, excluding the default language.
     */
    private List<String> getUntranslatedLanguageNames() {
        // Copy the list of available store language names
        List<String> languageNames = new ArrayList<>(storeLanguageNames);

        // Remove the language name associated with the default language code
        languageNames.remove(storeLanguageCodes.indexOf(defaultLanguage));

        return languageNames;
    }

    /**
     * Opens the edit translation popup for the product.
     */
    private void openEditTranslationPopup() {
        webUtils.click(loc_lblEditTranslation);
    }

    /**
     * Adds or updates attributes for a product.
     * <p>
     * This method retrieves the current product information, updates attributes for each variation, and logs the process.
     * </p>
     */
    public void updateProductAttribution() {
        // Generate product attribution
        ProductHelper.InitProductInfo initProductInfo = new ProductHelper.InitProductInfo();
        initProductInfo.setCurrentProductInfo(currentProductInfo);
        initProductInfo.setHasAttribution(true);
        newProductInfo = ProductHelper.generateProductInformation(initProductInfo);

        // Log the start of adding variation attributes
        logger.info("Start the process of adding product attribution");

        // Add attribution to main product
        addProductAttribution();
        saveChanges(true);

        // Update variation attributes
        IntStream.range(0, APIGetProductDetail.getVariationModelList(newProductInfo).size()).forEach(varIndex ->
                new VariationDetailPage(driver, varIndex, newProductInfo)
                        .navigateToVariationDetailPage()
                        .updateVariationAttribution()
        );

        // Pause briefly to allow the attribution update process to complete
        WebUtils.sleep(1000);

        // Get current product information
        var actualProductInfo = new APIGetProductDetail(credentials).getProductInformation(newProductInfo.getId());

        // Compare item attribution
        var actualItemAttribution = actualProductInfo.getItemAttributes();
        var expectedItemAttribution = newProductInfo.getItemAttributes();
        Assert.assertEquals(actualItemAttribution, expectedItemAttribution, "Item attribution must be '%s', but found '%s'".formatted(expectedItemAttribution, actualItemAttribution));

        // Compare model attribution
        IntStream.range(0, newProductInfo.getModels().size()).forEach(modelIndex -> {
            // Check attribute names
            var actualModelAttributionNames = APIGetProductDetail.getAttributeNames(actualProductInfo, modelIndex);
            var expectedModelAttributionNames = APIGetProductDetail.getAttributeNames(actualProductInfo, modelIndex);
            Assert.assertEquals(actualModelAttributionNames, expectedModelAttributionNames, "[%s] Model attribution names must be '%s', but found '%s'"
                    .formatted(newProductInfo.getModels().get(modelIndex).getName(), expectedModelAttributionNames, actualModelAttributionNames));

            // Check attribute values
            var actualModelAttributionValues = APIGetProductDetail.getAttributeNames(actualProductInfo, modelIndex);
            var expectedModelAttributionValues = APIGetProductDetail.getAttributeNames(actualProductInfo, modelIndex);
            Assert.assertEquals(actualModelAttributionValues, expectedModelAttributionValues, "[%s] Model attribution values must be '%s', but found '%s'"
                    .formatted(newProductInfo.getModels().get(modelIndex).getName(), expectedModelAttributionValues, actualModelAttributionValues));

            // Check attribute displays
            var actualModelAttributionDisplays = APIGetProductDetail.getAttributeNames(actualProductInfo, modelIndex);
            var expectedModelAttributionDisplays = APIGetProductDetail.getAttributeNames(actualProductInfo, modelIndex);
            Assert.assertEquals(actualModelAttributionDisplays, expectedModelAttributionDisplays, "[%s] Model attribution display must be '%s', but found '%s'"
                    .formatted(newProductInfo.getModels().get(modelIndex).getName(), expectedModelAttributionDisplays, actualModelAttributionDisplays));
        });
    }
}
