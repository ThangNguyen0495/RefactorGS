package pages.ios.seller.product;

import api.seller.login.APISellerLogin;
import api.seller.product.APIGetProductDetail;
import api.seller.product.APIGetProductDetail.ProductInformation;
import api.seller.product.APIGetProductList;
import api.seller.setting.APIGetBranchList;
import api.seller.setting.APIGetStoreDefaultLanguage;
import api.seller.setting.APIGetVATList;
import api.seller.user_feature.APIGetUserFeature;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import pages.ios.seller.home.HomeScreen;
import pages.ios.seller.login.LoginScreen;
import pages.ios.seller.product.product_management.IOSProductManagementScreen;
import utility.IOSUtils;
import utility.WebUtils;
import utility.helper.ProductHelper;
import utility.helper.VariationHelper;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

import static api.seller.user_feature.APIGetUserFeature.*;
import static org.apache.commons.lang.math.RandomUtils.nextBoolean;

public class IOSBaseProductScreen extends IOSBaseProductElement {
    // WebDriver and WebUtils
    private final WebDriver driver;
    private final IOSUtils iosUtils;
    String defaultLanguage;
    List<APIGetBranchList.BranchInformation> branchInfos;

    // Logger
    private final Logger logger = LogManager.getLogger();

    private ProductInformation currentProductInfo;
    private ProductInformation newProductInfo;

    public IOSBaseProductScreen(WebDriver driver) {
        // Get driver
        this.driver = driver;

        // Init commons class
        iosUtils = new IOSUtils(driver);
    }

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
     * Sets whether inventory management should be by lot date.
     */
    @Setter
    private boolean manageByLotDate = false;
    private boolean showOnApp;
    private boolean showOnWeb;
    private boolean showInStore;
    private boolean showInGoSocial;

    // Seller credentials
    APISellerLogin.Credentials credentials;

    // Product and Language Information
    private List<Integer> allBranchesIds;
    private List<Integer> activeBranchIds;

    // User features
    private List<UserPackage> userPackages;

    /**
     * Fetches necessary information including login credentials, branch details, and store language details.
     * This method updates the state of the ProductPage with relevant information for branches, languages,
     * and the default language setting based on the seller's information.
     *
     * @return The current instance of ProductPage, with updated information for method chaining.
     */
    public IOSBaseProductScreen fetchInformation() {
        // Get credentials
        this.credentials = LoginScreen.getCredentials();

        // Retrieve and store branch information
        this.branchInfos = new APIGetBranchList(this.credentials).getBranchInformation();

        // Retrieve the default language of the seller
        this.defaultLanguage = new APIGetStoreDefaultLanguage(this.credentials).getDefaultLanguage();

        // Get active branch names and IDs
        this.activeBranchIds = APIGetBranchList.getActiveBranchIds(this.branchInfos);

        // Get all branches IDs
        this.allBranchesIds = APIGetBranchList.getBranchIds(this.branchInfos);

        // Get all user packages
        this.userPackages = new APIGetUserFeature(this.credentials).getUserFeature();

        // Init platform information
        this.showOnApp = hasGoAPP(this.userPackages);
        this.showOnWeb = hasGoWEB(this.userPackages);
        this.showInGoSocial = hasGoSOCIAL(this.userPackages);
        this.showInStore = hasGoPOS(this.userPackages);

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
        var vatInfos = new APIGetVATList(this.credentials).getVATInformation();
        List<Integer> vatIds = APIGetVATList.getVATIds(vatInfos); // List of VAT IDs
        List<String> vatNames = APIGetVATList.getVATNames(vatInfos); // List of VAT names

        // Create an InitProductInfo object with the required parameters
        ProductHelper.InitProductInfo initProductInfo = new ProductHelper.InitProductInfo();
        initProductInfo.setCurrentProductInfo(currentProductInfo);
        initProductInfo.setHasModel(hasModel);
        initProductInfo.setNoCost(noCost);
        initProductInfo.setNoDiscount(noDiscount);
        initProductInfo.setManageByIMEI(isManagedByIMEI);
        initProductInfo.setHasDimension(hasDimension);
        initProductInfo.setHasLot(manageByLotDate || (currentProductInfo != null && currentProductInfo.isLotAvailable()));
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
        newProductInfo = ProductHelper.generateProductInformation(initProductInfo);
    }

    /**
     * Fetches product information from the API.
     *
     * @param productId The ID of the product to fetch information for.
     * @return An instance of APIGetProductDetail containing the product information.
     */
    private ProductInformation fetchProductInformation(int productId) {
        return new APIGetProductDetail(this.credentials).getProductInformation(productId);
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
        this.manageByLotDate = false;
        this.showOnApp = hasGoAPP(userPackages);
        this.showOnWeb = hasGoWEB(userPackages);
        this.showInGoSocial = hasGoSOCIAL(userPackages);
        this.showInStore = hasGoPOS(userPackages);
    }

    public IOSBaseProductScreen navigateToCreateProductScreen() {
        // Relaunch app
        iosUtils.relaunchApp();

        // Navigate to create product screen
        new HomeScreen(driver).navigateToCreateProductScreen();

        return this;
    }

    public void navigateToProductDetailScreen(int productId) {
        // Get product information
        this.currentProductInfo = fetchProductInformation(productId);

        // Get product name
        String productName = currentProductInfo.getName();

        // Navigate to product detail screen
        new IOSProductManagementScreen(driver).navigateToProductManagementScreen()
                .navigateToProductDetailScreen(productName);

        // Log
        logger.info("Navigate to product detail screen");

    }

    private void selectProductImages() {
        // Remove product images
        if (currentProductInfo != null) {
            int size = iosUtils.getListElement(loc_icnDeleteImages).size();
            IntStream.range(0, size).forEach(ignored -> iosUtils.click(loc_icnDeleteImages));
            logger.info("Remove old product images");
        }

        // Open select image popup
        iosUtils.click(loc_icnProductImage);

        // Select images
        new SelectImagePopup(driver).selectImages();

        // Log
        logger.info("Select product images.");
    }

    private void inputProductName() {
        // Input product name
        String name = newProductInfo.getName();
        iosUtils.sendKeys(loc_txtProductName, name);

        // Log
        logger.info("Input product name: {}", name);
    }

    private void inputProductDescription() {
        // Open description popup
        iosUtils.click(loc_btnProductDescription);

        // Input product description
        String description = newProductInfo.getDescription();
        new ProductDescriptionScreen(driver).inputDescription(description);

        // Log
        logger.info("Input product description: {}", description);
    }

    private void inputWithoutVariationPrice() {
        // Input listing price
        long listingPrice = newProductInfo.getOrgPrice();
        iosUtils.sendKeys(loc_txtWithoutVariationListingPrice, listingPrice);
        logger.info("Input without variation listing price: {}", String.format("%,d", listingPrice));

        // Input selling price
        long sellingPrice = newProductInfo.getNewPrice();
        iosUtils.sendKeys(loc_txtWithoutVariationSellingPrice, sellingPrice);
        logger.info("Input without variation selling price: {}", String.format("%,d", sellingPrice));

        // Input cost price
        long costPrice = newProductInfo.getCostPrice();
        iosUtils.sendKeys(loc_txtWithoutVariationCostPrice, costPrice);
        logger.info("Input without variation cost price: {}", String.format("%,d", costPrice));
    }

    private void inputWithoutVariationBarcode() {
        // Input without variation barcode
        long barcode = Instant.now().toEpochMilli();
        iosUtils.sendKeys(loc_txtWithoutVariationBarcode, barcode);

        // Log
        logger.info("Input without variation barcode: {}", barcode);
    }

    private void hideRemainingStockOnOnlineStore() {
        // Get current checkbox status
        boolean status = (currentProductInfo != null) && currentProductInfo.getIsHideStock();

        // Hide remaining stock on online store config
        if (!Objects.equals(newProductInfo.getIsHideStock(), status))
            iosUtils.toggleCheckbox(loc_chkHideRemainingStock);

        // Log
        logger.info("Hide remaining stock on online store config: {}", newProductInfo.getIsHideStock());
    }

    private void displayIfOutOfStock() {
        // Add display out of stock config
        boolean status = iosUtils.isChecked(loc_chkDisplayIfOutOfStock);
        if (!Objects.equals(newProductInfo.isShowOutOfStock(), status)) {
            iosUtils.toggleCheckbox(loc_chkDisplayIfOutOfStock);
        }

        // Log
        logger.info("Display out of stock config: {}", newProductInfo.isShowOutOfStock());
    }

    private void selectManageInventory() {
        // If product is managed by IMEI/Serial number
        if (newProductInfo.getInventoryManageType().equals("IMEI_SERIAL_NUMBER")) {
            // Open manage inventory dropdown
            iosUtils.click(loc_ddvSelectedManageInventoryType);

            // Select manage inventory type
            iosUtils.click(loc_ddvManageInventoryByIMEI);
        }

        // Log
        logger.info("Manage inventory by: {}", newProductInfo.getInventoryManageType().equals("IMEI_SERIAL_NUMBER") ? "IMEI/Serial number" : "Product");
    }

    private void manageProductByLot() {
        if (newProductInfo.getInventoryManageType().equals("IMEI_SERIAL_NUMBER")) {
            logger.info("Lot only support for the product has inventory managed by product");
            return;
        }

        // Manage product by lot
        boolean status = iosUtils.isChecked(loc_chkManageStockByLotDate);
        if (!Objects.equals(status, newProductInfo.isLotAvailable()))
            iosUtils.toggleCheckbox(loc_chkManageStockByLotDate);

        // Log
        logger.info("Manage product by lot date: {}", newProductInfo.isLotAvailable());
    }

    private void addWithoutVariationStock(boolean isCreate) {
        // Check product is managed by lot or not
        if (newProductInfo.isLotAvailable() && !newProductInfo.getInventoryManageType().equals("IMEI_SERIAL_NUMBER")) {
            logger.info("Product is managed by lot, requiring stock updates in the lot screen.");
            return;
        }

        // Navigate to inventory screen
        WebUtils.retryUntil(5, 1000, "Can not navigate to Inventory screen",
                () -> iosUtils.getListElement(loc_btnInventory).isEmpty(),
                () -> iosUtils.click(loc_btnInventory));


        // Add without variation stock
        new InventoryScreen(driver).manageStock(isCreate, newProductInfo.getInventoryManageType().equals("IMEI_SERIAL_NUMBER"),
                branchInfos, "", newProductInfo.getBranches());
    }

    private void modifyShippingInformation() {
        // Enables the shipping switch
        iosUtils.swipeToElement(loc_swShipping);
        iosUtils.toggleCheckbox(loc_swShipping);
        logger.info("Enables shipping switch");

        // Add product weight
        int weight = newProductInfo.getShippingInfo().getWeight();
        iosUtils.sendKeys(loc_txtWeight, weight);
        logger.info("Add product weight: %,dg".formatted(weight));

        // Add product length
        int length = newProductInfo.getShippingInfo().getLength();
        iosUtils.sendKeys(loc_txtLength, length);
        logger.info("Add product length: %,dcm".formatted(length));

        // Add product width
        int width = newProductInfo.getShippingInfo().getWidth();
        iosUtils.sendKeys(loc_txtWidth, width);
        logger.info("Add product width: %,dcm".formatted(width));

        // Add product height
        int height = newProductInfo.getShippingInfo().getHeight();
        iosUtils.sendKeys(loc_txtHeight, height);
        logger.info("Add product height: %,dcm".formatted(height));
    }

    private void modifyProductSellingPlatform() {
        /* WEB PLATFORM */
        // Get current show on web status
        boolean webStatus = iosUtils.isChecked(loc_swWeb);

        // Modify show on web config
        if (!Objects.equals(showOnWeb, webStatus)) iosUtils.click(loc_swWeb);

        // Log
        logger.info("On web configure: {}", showOnWeb);

        /* APP PLATFORM */
        // Get current show on app status
        boolean appStatus = iosUtils.isChecked(loc_swApp);

        // Modify show on app config
        if (!Objects.equals(showOnApp, appStatus)) iosUtils.click(loc_swApp);

        // Log
        logger.info("On app configure: {}", showOnApp);

        /* IN-STORE PLATFORM */
        // Get current show in-store status
        boolean inStoreStatus = iosUtils.isChecked(loc_swInStore);

        // Modify show in-store config
        if (!Objects.equals(showInStore, inStoreStatus)) iosUtils.click(loc_swInStore);

        // Log
        logger.info("In store configure: {}", showInStore);

        /* GO SOCIAL PLATFORM */
        // Get current show in goSocial status
        boolean goSocialStatus = iosUtils.isChecked(loc_swGoSocial);

        // Modify show in goSocial config
        if (!Objects.equals(showInGoSocial, goSocialStatus)) iosUtils.click(loc_swGoSocial);

        // Log
        logger.info("In goSOCIAL configure: {}", showInGoSocial);
    }

    private void modifyPriority() {
        // Update priority config
        iosUtils.click(loc_swPriority);
        if (iosUtils.getListElement(loc_txtPriority).isEmpty()) {
            iosUtils.click(loc_swPriority);
        }

        // If product has priority, add priority
        // Input priority
        int priority = newProductInfo.getPriority();
        iosUtils.sendKeys(loc_txtPriority, priority);

        // Log
        logger.info("Product priority: {}", priority);

    }

    private void addVariations() {
        if ((currentProductInfo != null) && currentProductInfo.isLotAvailable()) {
            logger.info("Product that is managed by Lot, do not allow add variation");
            return;
        }

        // Navigate to Add/Edit variation
        WebUtils.retryUntil(5, 1000, "Can not change 'Variation' switch status",
                () -> !iosUtils.getListElement(loc_btnAddVariation).isEmpty(),
                () -> iosUtils.click(loc_swVariation));

        iosUtils.click(loc_btnAddVariation);

        // Add/Edit variation
        var variationMap = VariationHelper.getVariationMap(APIGetProductDetail.getVariationName(newProductInfo, defaultLanguage),
                APIGetProductDetail.getVariationValues(newProductInfo, defaultLanguage));

        int numberOfVariations = (currentProductInfo != null) ? currentProductInfo.getModels().getFirst()
                .getLabel().split("\\|").length : 1;
        new VariationScreen(driver).addVariation(variationMap, numberOfVariations);
    }


    private void bulkUpdateVariations(boolean isCreate) {
        // Update variation information at product variation screen
        if ((newProductInfo.getModels().size() == 1)) {
            updateVariationInformation(isCreate);
        } else { // Update variation information at edit multiple screen
            // Navigate to edit multiple screen
            iosUtils.click(loc_btnEditMultiple);

            // Init edit multiple model
            EditMultipleScreen editMultipleScreen = new EditMultipleScreen(driver);

            // Set other variation price to first variation's price
            var models = newProductInfo.getModels();
            var newModels = models.stream().peek(model -> {
                model.setOrgPrice(models.getFirst().getOrgPrice());
                model.setNewPrice(models.getFirst().getNewPrice());
                model.setCostPrice(0);

                // If product is not allow bull update stock (set stock = 0)
                if (newProductInfo.getInventoryManageType().equals("IMEI_SERIAL_NUMBER") || newProductInfo.isLotAvailable()) {
                    var branches = model.getBranches();
                    branches.forEach(branch -> branch.setTotalItem(0));
                    model.setBranches(branches);
                }
            }).toList();
            newProductInfo.setModels(newModels);

            // Bulk update price
            long listingPrice = newProductInfo.getModels().getFirst().getOrgPrice();
            long sellingPrice = newProductInfo.getModels().getFirst().getNewPrice();
            editMultipleScreen.bulkUpdatePrice(listingPrice, sellingPrice);

            // Bulk update stock
            editMultipleScreen.bulkUpdateStock(newProductInfo.getInventoryManageType().equals("IMEI_SERIAL_NUMBER"),
                    newProductInfo.isLotAvailable(), branchInfos, newProductInfo.getModels().getFirst().getBranches());
        }
    }

    private void saveChanges() {
        // Save all product information
        iosUtils.click(loc_btnSave);

        // Allow managing stock by lot-date
        if ((currentProductInfo != null) // Check if the current product info is available (indicating it's an update)
            && !currentProductInfo.isLotAvailable() // Before the update, stock was not managed by lot
            && newProductInfo.isLotAvailable()) { // After the update, stock is now managed by lot, so confirmation is needed
            // Click the confirmation button to acknowledge the change in stock management by lot
            iosUtils.click(loc_dlgWarningManagedByLot_btnOK);

            // Log the confirmation of the stock management change
            logger.info("Confirmed management by lot.");
        }

        if (iosUtils.getListElement(IOSProductManagementScreen.loc_txtSearchBox, 30_000).isEmpty()) {
            throw new RuntimeException("Can not create/update product.");
        }

        // Logger
        LogManager.getLogger().info("Waiting for product is created/updated successfully.");

        // If product are updated, check information after updating
        // Get product ID
        int productId = new APIGetProductList(this.credentials)
                .searchProductIdByName(this.newProductInfo.getName());
        this.newProductInfo.setId(productId);

        // Validate after create
        if (productId == 0) Assert.fail("Can not find product after created");
    }

    /**
     * Verifies the product information by comparing the arranged product information
     * with the expected product information retrieved from the API. It checks various
     * attributes of the product to ensure they match the expected values.
     */
    public void verifyProductInformation() {
        // Log the start of the product information verification process
        logger.info("Verify product information");

        // Get productID
        int productId = (this.currentProductInfo == null) ? this.newProductInfo.getId() : this.currentProductInfo.getId();

        // Retrieve expected product information from the API
        var actualProductInfo = new APIGetProductDetail(LoginScreen.getCredentials())
                .getProductInformation(productId);

        // Check product name
        Assert.assertEquals(actualProductInfo.getName(), this.newProductInfo.getName(),
                String.format("Product name must be '%s', but found '%s'", this.newProductInfo.getName(), actualProductInfo.getName()));

        // Check product description, stripping HTML tags
        Assert.assertEquals(actualProductInfo.getDescription().replaceAll("<.*?>", ""), this.newProductInfo.getDescription(),
                String.format("Product description must be '%s', but found '%s'", this.newProductInfo.getDescription(), actualProductInfo.getDescription()));

        // Check listing price
        var expectedOrgPrice = this.newProductInfo.isHasModel()
                ? APIGetProductDetail.getVariationListingPrice(this.newProductInfo)
                : this.newProductInfo.getOrgPrice();
        var actualOrgPrice = this.newProductInfo.isHasModel()
                ? APIGetProductDetail.getVariationListingPrice(actualProductInfo)
                : actualProductInfo.getOrgPrice();
        Assert.assertEquals(actualOrgPrice, expectedOrgPrice,
                "Listing price must be '%s', but found '%s'".formatted(expectedOrgPrice, actualOrgPrice));

        // Check selling price
        var expectedNewPrice = this.newProductInfo.isHasModel()
                ? APIGetProductDetail.getVariationSellingPrice(this.newProductInfo)
                : this.newProductInfo.getNewPrice();
        var actualNewPrice = this.newProductInfo.isHasModel()
                ? APIGetProductDetail.getVariationSellingPrice(actualProductInfo)
                : actualProductInfo.getNewPrice();
        Assert.assertEquals(actualNewPrice, expectedNewPrice,
                "Selling price must be '%s', but found '%s'".formatted(expectedNewPrice, actualNewPrice));

        // Check cost price
        var expectedCostPrice = this.newProductInfo.isHasModel()
                ? APIGetProductDetail.getVariationCostPrice(this.newProductInfo)
                : this.newProductInfo.getCostPrice();
        var actualCostPrice = this.newProductInfo.isHasModel()
                ? APIGetProductDetail.getVariationCostPrice(actualProductInfo)
                : actualProductInfo.getCostPrice();
        Assert.assertEquals(actualCostPrice, expectedCostPrice,
                "Cost price must be '%s', but found '%s'".formatted(expectedCostPrice, actualCostPrice));

        // Check online shop display settings
        Assert.assertEquals(actualProductInfo.isShowOutOfStock(), this.newProductInfo.isShowOutOfStock(),
                "Display out of stock must be '%s', but found '%s'".formatted(this.newProductInfo.isShowOutOfStock(), actualProductInfo.isShowOutOfStock()));
        Assert.assertEquals(actualProductInfo.getIsHideStock(), this.newProductInfo.getIsHideStock(),
                "Hide remaining stock must be '%s', but found '%s'".formatted(this.newProductInfo.getIsHideStock(), actualProductInfo.getIsHideStock()));

        // Retrieve expected and actual stock maps
        var expectedStockMap = new ArrayList<>(APIGetProductDetail.getModelBranchStockMap(this.newProductInfo).values());
        var actualStockMap = new ArrayList<>(APIGetProductDetail.getModelBranchStockMap(actualProductInfo).values());

        // Iterate through both expected stock map and active branch IDs
        IntStream.range(0, expectedStockMap.size()).forEach(modelIndex -> {
            // Get the expected and actual stock maps for the current model index
            Map<Integer, Integer> expectedStock = expectedStockMap.get(modelIndex);
            Map<Integer, Integer> actualStock = actualStockMap.get(modelIndex);

            // Iterate through all branch IDs
            this.allBranchesIds.forEach(branchId -> {
                // Safely get actual and expected stock values
                int actualStockValue = actualStock.getOrDefault(branchId, 0);
                int expectedStockValue = expectedStock.getOrDefault(branchId, 0);

                // Assert the actual and expected stocks are equal
                Assert.assertEquals(actualStockValue, expectedStockValue,
                        "[ModelIndex: %d, BranchId: %d] Branch stock must be '%s', but found '%s'".formatted(modelIndex, branchId, expectedStockValue, actualStockValue));
            });
        });

        // Check inventory management type
        String expectedManagedInventoryType = this.newProductInfo.getInventoryManageType();
        String actualManagedInventoryType = actualProductInfo.getInventoryManageType();
        Assert.assertEquals(actualManagedInventoryType, expectedManagedInventoryType,
                "Product inventory must be managed by '%s', but found '%s'".formatted(expectedManagedInventoryType, actualManagedInventoryType));

        // Check lot availability
        boolean expectedLotAvailable = this.newProductInfo.isLotAvailable();
        boolean actualLotAvailable = actualProductInfo.isLotAvailable();
        Assert.assertEquals(actualLotAvailable, expectedLotAvailable,
                "Product stock must be managed by lot '%s', but found '%s'".formatted(expectedLotAvailable, actualLotAvailable));

        // Check expired quality exclusion
//        boolean expectedExcludeExpired = newProductInfo.isExpiredQuality();
//        boolean actualExcludeExpired = actualProductInfo.isExpiredQuality();
//        Assert.assertEquals(actualExcludeExpired, expectedExcludeExpired,
//                "Exclude expired quality must be '%s', but found '%s'".formatted(expectedExcludeExpired, actualExcludeExpired));

        // Check SEO attributes
        String expectedSEOTitle = APIGetProductDetail.retrieveSEOTitle(this.newProductInfo, this.defaultLanguage);
        String actualSEOTitle = APIGetProductDetail.retrieveSEOTitle(actualProductInfo, this.defaultLanguage);
        Assert.assertEquals(actualSEOTitle, expectedSEOTitle,
                "Product SEO title must be '%s', but found '%s'".formatted(expectedSEOTitle, actualSEOTitle));

        String expectedSEODescription = APIGetProductDetail.retrieveSEODescription(this.newProductInfo, this.defaultLanguage);
        String actualSEODescription = APIGetProductDetail.retrieveSEODescription(actualProductInfo, this.defaultLanguage);
        Assert.assertEquals(actualSEODescription, expectedSEODescription,
                "Product SEO description must be '%s', but found '%s'".formatted(expectedSEODescription, actualSEODescription));

        String expectedSEOKeyword = APIGetProductDetail.retrieveSEOKeywords(this.newProductInfo, this.defaultLanguage);
        String actualSEOKeyword = APIGetProductDetail.retrieveSEOKeywords(actualProductInfo, this.defaultLanguage);
        Assert.assertEquals(actualSEOKeyword, expectedSEOKeyword,
                "Product SEO keyword must be '%s', but found '%s'".formatted(expectedSEOKeyword, actualSEOKeyword));

        String expectedSEOUrl = APIGetProductDetail.retrieveSEOUrl(this.newProductInfo, this.defaultLanguage);
        String actualSEOUrl = APIGetProductDetail.retrieveSEOUrl(actualProductInfo, this.defaultLanguage);
        Assert.assertEquals(actualSEOUrl, expectedSEOUrl,
                "Product SEO URL must be '%s', but found '%s'".formatted(expectedSEOUrl, actualSEOUrl));

        // Check shipping information
        var expectedShippingInfo = this.newProductInfo.getShippingInfo();
        var actualShippingInfo = actualProductInfo.getShippingInfo();
        Assert.assertEquals(actualShippingInfo, expectedShippingInfo,
                "Product shipping info must be '%s' but found '%s'".formatted(expectedShippingInfo, actualShippingInfo));

        // Check selling platform availability
//        Assert.assertEquals(actualProductInfo.isOnWeb(), this.newProductInfo.isOnWeb(),
//                "Web platform config must be '%s', but found '%s'".formatted(this.newProductInfo.isOnWeb(), actualProductInfo.isOnWeb()));
//        Assert.assertEquals(actualProductInfo.isOnApp(), this.newProductInfo.isOnApp(),
//                "App platform config must be '%s', but found '%s'".formatted(this.newProductInfo.isOnApp(), actualProductInfo.isOnApp()));
//        Assert.assertEquals(actualProductInfo.isInStore(), this.newProductInfo.isInStore(),
//                "InStore platform config must be '%s', but found '%s'".formatted(newProductInfo.isInStore(), actualProductInfo.isInStore()));
//        Assert.assertEquals(actualProductInfo.isInGosocial(), this.newProductInfo.isInGosocial(),
//                "GoSOCIAL platform config must be '%s', but found '%s'".formatted(this.newProductInfo.isInGosocial(), actualProductInfo.isInGosocial()));

        // Check product attribution
        var expectedAttribution = this.newProductInfo.getItemAttributes();
        var actualAttribution = actualProductInfo.getItemAttributes();

        Assert.assertEquals(actualAttribution, expectedAttribution,
                "Product attribution must be '%s', but found '%s'".formatted(expectedAttribution, actualAttribution));

        // Check variations if the product has models
        if (this.newProductInfo.isHasModel()) {
            var expectedVariationName = APIGetProductDetail.getVariationName(this.newProductInfo, this.defaultLanguage);
            var actualVariationName = APIGetProductDetail.getVariationName(actualProductInfo, this.defaultLanguage);
            Assert.assertEquals(actualVariationName, expectedVariationName,
                    "Variation name must be '%s', but found '%s'".formatted(expectedVariationName, actualVariationName));

            var expectedVariationValues = APIGetProductDetail.getVariationValues(this.newProductInfo, this.defaultLanguage);
            var actualVariationValues = APIGetProductDetail.getVariationValues(actualProductInfo, this.defaultLanguage);
            Assert.assertEquals(actualVariationValues, expectedVariationValues,
                    "Variation values must be '%s', but found '%s'".formatted(expectedVariationValues, actualVariationValues));
        }

        // Reset default flags
        resetAllVariables();
    }

    /**
     * Creates or updates a product with or without variations based on the parameters.
     *
     * @param isCreate        Indicates if the operation is to create a product.
     * @param isManagedByIMEI Indicates if the product is managed by IMEI.
     * @param hasVariation    Indicates if the product has variations.
     * @param branchStock     Branch stock quantities.
     */
    public void manageProduct(boolean isCreate, boolean isManagedByIMEI, boolean hasVariation, int... branchStock) {
        // Log
        logger.info("Start the process of creating/updating product");

        // Fetch product information
        fetchProductInformation(isManagedByIMEI, hasVariation, branchStock);

        // For update operations, remove old variations
        if (isCreate) {
            selectManageInventory();
        }

        // Select product images
        selectProductImages();

        // Common steps for both create and update operations
        inputProductName();
        inputProductDescription();

        if (!hasVariation) {
            // Steps for products without variations
            inputWithoutVariationPrice();
            inputWithoutVariationBarcode();
            addWithoutVariationStock(isCreate);
        } else {
            // Steps for products with variations
            addVariations();
            bulkUpdateVariations(isCreate);
        }

        // Common modifications for both types
        hideRemainingStockOnOnlineStore();
        displayIfOutOfStock();
        manageProductByLot();
        modifyShippingInformation();
        modifyPriority();
        modifyProductSellingPlatform();

        // Save changes
        saveChanges();
    }

    /**
     * Creates a product with or without variations based on the provided parameters.
     * <p>
     * This method delegates the product creation process to the {@link #manageProduct(boolean, boolean, boolean, int...)} (boolean, boolean, int...)}
     * method, specifying that the product is being created (not updated).
     * It handles the entire product creation lifecycle, including setting stock quantities per branch.
     * </p>
     *
     * @param hasModel                 Indicates whether the product has variations (models) or not.
     * @param isManagedInventoryByIMEI Indicates whether the product uses IMEI numbers for inventory management.
     * @param branchStock              Stock quantities for each branch.
     * @return The current {@code BaseProductPage} instance for method chaining.
     */
    public IOSBaseProductScreen createProduct(boolean hasModel, boolean isManagedInventoryByIMEI, int... branchStock) {
        manageProduct(true, isManagedInventoryByIMEI, hasModel, branchStock);
        return this;
    }

    /**
     * Updates an existing product with or without variations based on the provided parameters.
     * <p>
     * This method delegates the product update process to the {@link #manageProduct(boolean, boolean, boolean, int...)} (boolean, boolean, int...)}
     * method, specifying that the product is being updated (not created).
     * It allows updating stock quantities per branch and other product details.
     * </p>
     *
     * @param hasModel    Indicates whether the product has variations (models) or not.
     * @param branchStock Stock quantities for each branch.
     * @return The current {@code BaseProductPage} instance for method chaining.
     */
    public IOSBaseProductScreen updateProduct(boolean hasModel, int... branchStock) {
        manageProduct(false, this.currentProductInfo.getInventoryManageType().equals("IMEI_SERIAL_NUMBER"), hasModel, branchStock);
        return this;
    }
    private void updateVariationInformation(boolean isCreate) {
        // Init variation POM
        ProductVariationScreen productVariationScreen = new ProductVariationScreen(driver);

        // Update variation information
        IntStream.range(0, this.newProductInfo.getModels().size()).forEach(variationIndex -> {
            // Navigate to variation detail screen
            iosUtils.click(loc_lstVariations, variationIndex);

            // Update variation information
            productVariationScreen.getVariationInformation(this.branchInfos, variationIndex, this.newProductInfo)
                    .updateVariationInformation(isCreate);
        });
    }

    /**
     * This class represents the popup screen for selecting images in the Seller app.
     * It provides methods to interact with the popup, such as selecting images and saving the selection.
     */
    private static class SelectImagePopup {

        private final IOSUtils iosUtils;

        By loc_lstImages = By.xpath("//XCUIElementTypeCollectionView//XCUIElementTypeButton");
        By loc_btnSave = By.xpath("//XCUIElementTypeImage[@name=\"ic_DownArrow\"]//preceding-sibling::XCUIElementTypeButton[1]");

        /**
         * Constructor for the SelectImagePopup class.
         * Initializes the IOSUtils object with the provided AndroidDriver.
         *
         * @param driver the AndroidDriver instance used to interact with the Android UI elements.
         */
        public SelectImagePopup(WebDriver driver) {
            this.iosUtils = new IOSUtils(driver);
        }

        /**
         * Selects images from the list and saves the selection.
         * This method clicks on the image list to select an image and then clicks the save button to confirm.
         */
        public void selectImages() {
            iosUtils.allowPermission("Allow Full Access");// Allow access all photo library
            iosUtils.click(loc_lstImages);  // Selects images from the list.
            iosUtils.click(loc_btnSave);    // Clicks the save button to confirm the selection.
        }
    }

    private static class ProductVariationScreen {
        private final WebDriver driver;
        private final IOSUtils iosUtils;
        private final Logger logger = LogManager.getLogger();
        private ProductInformation productInfo;
        private ProductInformation.Model model;
        private int variationIndex;
        private String variationValue;
        private List<APIGetBranchList.BranchInformation> branchInfos;

        public ProductVariationScreen(WebDriver driver) {
            // Get driver
            this.driver = driver;

            // Init commons class
            iosUtils = new IOSUtils(driver);
        }

        By loc_btnSave = By.xpath("//XCUIElementTypeButton[@name=\"icon checked white\"]");
        By loc_icnVariationImage = By.xpath("//*[XCUIElementTypeImage[@name=\"icon_selected_image_default\"]]/XCUIElementTypeButton");
        By loc_txtVariationName = By.xpath("//XCUIElementTypeStaticText[@name=\"Product version name *\" or @name=\"Tên mẫu mã sản phẩm *\"]//following-sibling::XCUIElementTypeTextField");
        By loc_chkReuseProductDescription = By.xpath("//XCUIElementTypeStaticText[@name=\"Reuse the product description\" or @name=\"Nội dung giống mô tả sản phẩm\"]//preceding-sibling::XCUIElementTypeOther");
        By loc_btnVariationDescription = By.xpath("//XCUIElementTypeStaticText[@name=\"Input product description\" or @name=\"Thay đổi mô tả sản phẩm\"]/preceding-sibling::XCUIElementTypeButton");
        By loc_txtVariationListingPrice = By.xpath("(//XCUIElementTypeStaticText[@name=\"Selling price\" or @name=\"Giá bán\"]//following-sibling::*//XCUIElementTypeTextField)[1]");
        By loc_txtVariationSellingPrice = By.xpath("(//XCUIElementTypeStaticText[@name=\"Selling price\" or @name=\"Giá bán\"]//following-sibling::*//XCUIElementTypeTextField)[2]");
        By loc_txtVariationCostPrice = By.xpath("//XCUIElementTypeStaticText[@name=\"Cost price\" or @name=\"Giá gốc\"]//following-sibling::*//XCUIElementTypeTextField");
        By loc_txtVariationSKU = By.xpath("//XCUIElementTypeStaticText[@name=\"SKU\" or @name=\"Mã SKU\"]//following-sibling::XCUIElementTypeTextField");
        By loc_txtVariationBarcode = By.xpath("//XCUIElementTypeStaticText[@name=\"Barcode\" or @name=\"Mã vạch\"]//following-sibling::XCUIElementTypeTextField");
        By loc_icnInventory = By.xpath("//XCUIElementTypeImage[@name=\"icon_inventory\"]/preceding-sibling::XCUIElementTypeButton");
        By loc_btnDeactivate = By.xpath("//XCUIElementTypeButton[contains(@name, \"ctivate\") or @name=\"Ngừng bán\"] or @name=\"Bán ngay\"]");

        public ProductVariationScreen getVariationInformation(List<APIGetBranchList.BranchInformation> branchInfos, int variationIndex, ProductInformation productInfo) {
            // Get branch information
            this.branchInfos = branchInfos;

            // Get product information
            this.productInfo = productInfo;

            // Get variation index
            this.variationIndex = variationIndex;

            // Get model
            this.model = productInfo.getModels().get(variationIndex);

            // Get variation value
            this.variationValue = model.getName();

            // Log
            logger.info("Update information of '{}' variation", variationValue);

            return this;
        }


        void selectVariationImages() {
            // Open select image popup
            iosUtils.click(loc_icnVariationImage);

            // Select images
            new SelectImagePopup(driver).selectImages();

            // Log
            logger.info("Select variation images.");
        }

        void updateVariationName() {
            // Input variation name
            String name = model.getVersionName();
            iosUtils.sendKeys(loc_txtVariationName, name);

            // Log
            logger.info("Input variation name: {}", name);
        }

        void updateVariationDescription() {
            // Get reuse description checkbox status
            boolean reuseParentDescription = model.isUseProductDescription();

            // Get current reuse description checkbox status
            boolean status = iosUtils.isChecked(loc_chkReuseProductDescription);

            // Uncheck reuse description checkbox
            if (!Objects.equals(reuseParentDescription, status))
                iosUtils.toggleCheckbox(loc_chkReuseProductDescription);

            if (reuseParentDescription) {
                // Log
                logger.info("Reuse parent description");
                return;
            }

            // Open description popup
            iosUtils.click(loc_btnVariationDescription);

            // Input product description
            String description = model.getDescription();
            new ProductDescriptionScreen(driver).inputDescription(description);

            // Log
            logger.info("Input variation description: {}", description);
        }

        void updateVariationPrice() {
            // Input listing price
            long listingPrice = model.getOrgPrice();
            iosUtils.sendKeys(loc_txtVariationListingPrice, String.valueOf(listingPrice));
            logger.info("Input variation listing price: {}", String.format("%,d", listingPrice));

            // Input selling price
            long sellingPrice = model.getNewPrice();
            iosUtils.sendKeys(loc_txtVariationSellingPrice, String.valueOf(sellingPrice));
            logger.info("Input variation selling price: {}", String.format("%,d", sellingPrice));

            // Input cost price
            long costPrice = model.getCostPrice();
            iosUtils.sendKeys(loc_txtVariationCostPrice, String.valueOf(costPrice));
            logger.info("Input variation cost price: {}", String.format("%,d", costPrice));
        }

        void updateVariationSKU() {
            // Input variation SKU
            String sku = model.getSku();

            // Click to check it's add new SKU or update SKU cases
            iosUtils.click(loc_txtVariationSKU);

            // In case, the current screen is update sku screen
            // Add SKU for each branch
            if (iosUtils.getListElement(loc_txtVariationSKU).isEmpty()) {
                new VariationSKUScreen(driver).inputVariationSKU(APIGetBranchList.getActiveBranchNames(branchInfos), sku);
            } else {
                // Update SKU for the current variation
                iosUtils.sendKeys(loc_txtVariationSKU, sku);
            }

            // Log
            logger.info("Input variation SKU: {}", sku);
        }

        void updateVariationBarcode() {
            // Input variation barcode
            String barcode = "";
            iosUtils.sendKeys(loc_txtVariationBarcode, barcode);

            // Log
            logger.info("Input variation barcode: {}", barcode);
        }

        void manageVariationStock(boolean isCreate) {
            // Check product is managed by lot or not
            if (!productInfo.isLotAvailable() || productInfo.getInventoryManageType().equals("IMEI_SERIAL_NUMBER")) {
                // Navigate to inventory screen
                iosUtils.click(loc_icnInventory);

                // Add variation stock
                new InventoryScreen(driver).manageStock(isCreate, productInfo.getInventoryManageType().equals("IMEI_SERIAL_NUMBER"), branchInfos, variationValue, model.getBranches());
            } else logger.info("Product is managed by lot, requiring stock updates in the lot screen.");
        }

        void updateVariationStatus() {
            if (!iosUtils.getListElement(loc_btnDeactivate).isEmpty()) {
                // Get new variation status
                String newStatus = model.getStatus();

                // Get current variation status
                String currentStatus = APIGetProductDetail.getVariationStatus(productInfo, variationIndex);

                // Update variation status
                if (!currentStatus.equals(newStatus)) {
                    iosUtils.click(loc_btnDeactivate);
                }

                // Log
                logger.info("New variation's status: {}", newStatus);
            }
        }

        void completeUpdateVariation() {
            // Save all product information
            iosUtils.click(loc_btnSave);
        }

        public void updateVariationInformation(boolean isCreate) {
            selectVariationImages();
            updateVariationName();
            updateVariationDescription();
            updateVariationPrice();
            updateVariationSKU();
            updateVariationBarcode();
            manageVariationStock(isCreate);
            if (!isCreate) updateVariationStatus();
            completeUpdateVariation();
        }
    }

    private static class VariationScreen {
        WebDriver driver;
        IOSUtils iosUtils;
        Logger logger = LogManager.getLogger();

        public VariationScreen(WebDriver driver) {
            this.driver = driver;
            iosUtils = new IOSUtils(driver);
        }

        By loc_btnRemoveVariationGroup = By.xpath("//*[*/*/XCUIElementTypeImage[@name=\"ic-minus_circle\"]]/preceding-sibling::XCUIElementTypeButton");

        By loc_txtVariationName = By.xpath("//*[XCUIElementTypeImage[@name=\"ic-minus_circle\"]]/XCUIElementTypeTextField");

        By loc_txtVariationValue = By.xpath("//*[XCUIElementTypeImage[@name=\"ic-plus\"]]/*/XCUIElementTypeTextField");

        By loc_icnAddVariationValue = By.xpath("//*[*/XCUIElementTypeImage[@name=\"ic-plus\"]]/following-sibling:: XCUIElementTypeButton");

        By loc_btnAddVariation = By.xpath("//*[XCUIElementTypeImage[@name=\"ic-plus\"]]/XCUIElementTypeButton");
        By loc_btnSave = By.xpath("//XCUIElementTypeButton[@name=\"icon checked white\"]");

        private void removeOldVariation(int numOfVariationGroup) {
            // Check number of variation groups
            if (numOfVariationGroup <= 0) return;

            // Remove old variation
            IntStream.range(0, numOfVariationGroup).forEach(ignored -> iosUtils.click(loc_btnRemoveVariationGroup));
        }

        public void addVariation(Map<String, List<String>> variationMap, int numberOfVariations) {
            // Remove old variation
            removeOldVariation(numberOfVariations);

            // Add variation
            IntStream.range(0, variationMap.keySet().size()).forEachOrdered(groupIndex -> {
                // Get variation group
                String variationGroup = variationMap.keySet().stream().toList().get(groupIndex);

                // Get variation value
                List<String> variationValue = variationMap.get(variationGroup);

                // Add new variation group
                iosUtils.click(loc_btnAddVariation);

                // Input variation group
                iosUtils.sendKeys(loc_txtVariationName, groupIndex, variationGroup);
                logger.info("Add variation group {}, group: {}", groupIndex + 1, variationGroup);

                // Input variation value
                variationValue.forEach(value -> {
                    iosUtils.sendKeys(loc_txtVariationValue, groupIndex, value);
                    iosUtils.click(loc_icnAddVariationValue, groupIndex);
                    logger.info("Add variation value for group {}, value: {}", groupIndex + 1, value);
                });
            });

            // Save changes
            saveChanges();

            // Log
            logger.info("Complete add variations");
        }

        public void saveChanges() {
            // Save changes
            iosUtils.click(loc_btnSave);
        }
    }


    /**
     * This class represents the product description screen in the Seller app.
     * It provides methods to input and save the product description.
     */
    public static class ProductDescriptionScreen {

        private final IOSUtils iosUtils;

        // Locator for the description input field
        By loc_rtfDescription = By.xpath("//XCUIElementTypeTextField");
        By loc_btnSave = By.xpath("//XCUIElementTypeButton[@name=\"icon checked white\"]");

        /**
         * Constructor for the ProductDescriptionScreen class.
         * Initializes the IOSUtils object with the provided AndroidDriver.
         *
         * @param driver the IOSDriver instance used to interact with the Android UI elements.
         */
        public ProductDescriptionScreen(WebDriver driver) {
            this.iosUtils = new IOSUtils(driver);
        }

        /**
         * Inputs the product description and saves the changes.
         *
         * @param description the description text to be entered.
         */
        public void inputDescription(String description) {
            // Input product description
            iosUtils.sendKeys(loc_rtfDescription, description);

            // Save changes
            iosUtils.click(loc_btnSave);
        }
    }

    public static class InventoryScreen {
        WebDriver driver;
        IOSUtils iosUtils;
        Logger logger = LogManager.getLogger();

        public InventoryScreen(WebDriver driver) {
            this.driver = driver;
            iosUtils = new IOSUtils(driver);
        }

        By loc_btnSave = By.xpath("//XCUIElementTypeButton[@name=\"icon checked white\"]");

        By loc_txtBranchStock(String branchName) {
            return By.xpath("//*[XCUIElementTypeStaticText[@name=\"%s\"]]/XCUIElementTypeTextField | //XCUIElementTypeStaticText[@name=\"%s\"]/preceding-sibling::XCUIElementTypeTextField".formatted(branchName, branchName));
        }

        By loc_dlgUpdateStock_tabChange = By.xpath("//XCUIElementTypeStaticText[@name=\"CHANGE\"]");
        By loc_dlgUpdateStock_txtQuantity = By.xpath("//XCUIElementTypeStaticText[@name=\"Input quantity\" or @name=\"Nhập số lượng\"]/preceding-sibling::*/XCUIElementTypeTextField");
        By loc_dlgUpdateStock_btnOK = By.xpath("//XCUIElementTypeButton[@name=\"OK\"]");

        /**
         * Manages stock for branches by either adding or updating based on the operation.
         *
         * @param isCreate     Indicates whether the operation is an update (false) or add (true).
         * @param manageByIMEI Indicates if the stock management uses IMEI.
         * @param branchInfos  List of branch information from the API.
         * @param variation    Variation information for IMEI management.
         * @param branches     List of branches with their respective stock details.
         */
        public void manageStock(boolean isCreate, boolean manageByIMEI, List<APIGetBranchList.BranchInformation> branchInfos, String variation, List<ProductInformation.Branch> branches) {
            // Manage stock for each branch
            IntStream.range(0, APIGetBranchList.getActiveBranchNames(branchInfos).size()).forEach(branchIndex -> {
                // Get current branch
                String branchName = APIGetBranchList.getActiveBranchNames(branchInfos).get(branchIndex);

                // Get branch quantity
                int branchQuantity = branches.stream()
                        .filter(branch -> branch.getBranchId() == APIGetBranchList.getActiveBranchIds(branchInfos).get(branchIndex))
                        .findFirst()
                        .map(ProductInformation.Branch::getTotalItem)
                        .orElse(0);

                // Get current branch stock
                String currentStockText = iosUtils.getText(loc_txtBranchStock(branchName)).replaceAll("\\D+", "");
                int currentStock = currentStockText.isEmpty() ? 0 : Integer.parseInt(currentStockText);

                // Only update when the stock needs to be changed
                if (branchQuantity != currentStock) {
                    if (manageByIMEI) {
                        // Navigate to add IMEI screen
                        iosUtils.click(loc_txtBranchStock(branchName));

                        // Add IMEI
                        new AddIMEIScreen(driver).addIMEI(branchQuantity, branchName, variation);
                    } else {
                        // Handle stock based on operation type
                        iosUtils.click(loc_txtBranchStock(branchName));
                        if (!isCreate && !iosUtils.getListElement(loc_dlgUpdateStock_tabChange).isEmpty()) {
                            // Update stock via popup for updates
                            iosUtils.click(loc_dlgUpdateStock_tabChange);
                            iosUtils.sendKeys(loc_dlgUpdateStock_txtQuantity, String.valueOf(branchQuantity));
                            iosUtils.click(loc_dlgUpdateStock_btnOK);
                        } else {
                            // Add or directly input stock
                            iosUtils.sendKeys(loc_txtBranchStock(branchName), String.valueOf(branchQuantity));
                        }
                    }

                    // Log the operation
                    String action = isCreate ? "Add" : "Update";
                    logger.info("{} stock for branch '{}', quantity: {}", action, branchName, branchQuantity);
                }
            });

            // Save changes
            iosUtils.click(loc_btnSave);
        }
    }


    public static class EditMultipleScreen {
        WebDriver driver;
        IOSUtils iosUtils;
        Logger logger = LogManager.getLogger();

        public EditMultipleScreen(WebDriver driver) {
            this.driver = driver;
            iosUtils = new IOSUtils(driver);
        }

        By loc_btnSave = By.xpath("//XCUIElementTypeButton[@name=\"icon checked white\"]");
        By loc_icnStoreBranch = By.xpath("//XCUIElementTypeImage[@name=\"icon_store_branch\"]/preceding-sibling::XCUIElementTypeButton");

        By loc_ddvBranch(String branchName) {
            return By.xpath("//XCUIElementTypeStaticText[@name=\"%s\"]/preceding-sibling::XCUIElementTypeOther".formatted(branchName));
        }

        By loc_icnActions = By.xpath("//XCUIElementTypeStaticText[@name=\"Action\" or @name=\"Thao tác\"]//preceding-sibling::XCUIElementTypeButton");
        By loc_ddvUpdatePriceActions = By.xpath("//XCUIElementTypeStaticText[@name=\"Update price\" or @name = \"Cập nhật giá bán\"]//preceding-sibling::XCUIElementTypeButton");
        By loc_ddvUpdateStockActions = By.xpath("//XCUIElementTypeStaticText[@name=\"Update stock\" or @name=\"Cập nhật kho hàng\"]//preceding-sibling::XCUIElementTypeButton");

        By loc_dlgUpdatePrice_txtListingPrice = By.xpath("//XCUIElementTypeStaticText[@name=\"Selling price\" or @name=\"Giá bán\"]//following-sibling::XCUIElementTypeOther[1]//XCUIElementTypeTextField");
        By loc_dlgUpdatePrice_txtSellingPrice = By.xpath("//XCUIElementTypeStaticText[@name=\"Selling price\" or @name=\"Giá bán\"]//following-sibling::XCUIElementTypeOther[2]//XCUIElementTypeTextField");
        By loc_dlgUpdatePrice_btnOK = By.xpath("//XCUIElementTypeButton[@name=\"OK\"]");

        By loc_dlgUpdateStock_tabChange = By.xpath("//XCUIElementTypeButton[@name=\"CHANGE\" or @name=\"THAY ĐỔI\"]");
        By loc_dlgUpdateStock_txtQuantity = By.xpath("//XCUIElementTypeStaticText[@name=\"Input quantity\" or @name=\"Nhập số lượng\"]/preceding-sibling::*/XCUIElementTypeTextField");
        By loc_dlgUpdateStock_btnOK = By.xpath("//XCUIElementTypeButton[@name=\"OK\"]");

        public void bulkUpdatePrice(long listingPrice, long sellingPrice) {
            // Open list actions
            iosUtils.click(loc_icnActions);

            // Select bulk update price actions
            iosUtils.click(loc_ddvUpdatePriceActions);

            // Input listing price
            iosUtils.sendKeys(loc_dlgUpdatePrice_txtListingPrice, String.valueOf(listingPrice));
            logger.info("Bulk listing price: {}", String.format("%,d", listingPrice));

            // Input selling price
            iosUtils.sendKeys(loc_dlgUpdatePrice_txtSellingPrice, String.valueOf(sellingPrice));
            logger.info("Bulk selling price: {}", String.format("%,d", sellingPrice));

            // Save changes
            iosUtils.click(loc_dlgUpdatePrice_btnOK);
        }

        public void bulkUpdateStock(boolean manageByIMEI, boolean manageByLot, List<APIGetBranchList.BranchInformation> branchInfos, List<ProductInformation.Branch> branches) {
            // Not supported for product managed by IMEI/Serial number
            if (manageByIMEI) {
                logger.info("Can not bulk update stock with product that is managed by IMEI/Serial number.");
            } else if (manageByLot) {
                logger.info("Product is managed by lot, requiring stock updates in the lot-date screen.");
            } else {
                // Update stock for each branch
                IntStream.range(0, APIGetBranchList.getActiveBranchNames(branchInfos).size()).forEach(branchIndex -> {
                    // Get branch name
                    String branchName = APIGetBranchList.getActiveBranchNames(branchInfos).get(branchIndex);

                    // Get branch quantity
                    int branchQuantity = branches.stream()
                            .filter(branch -> branch.getBranchId() == APIGetBranchList.getActiveBranchIds(branchInfos).get(branchIndex))
                            .findFirst()
                            .map(ProductInformation.Branch::getTotalItem)
                            .orElse(0);

                    // Open list branches
                    iosUtils.click(loc_icnStoreBranch);

                    // Switch branch
                    iosUtils.click(loc_ddvBranch(branchName));

                    // Open list actions
                    iosUtils.click(loc_icnActions);

                    // Select bulk update stock actions
                    iosUtils.click(loc_ddvUpdateStockActions);

                    // Switch to change tab
                    iosUtils.click(loc_dlgUpdateStock_tabChange);

                    // Input quantity
                    iosUtils.sendKeys(loc_dlgUpdateStock_txtQuantity, String.valueOf(branchQuantity));

                    // Save changes
                    iosUtils.click(loc_dlgUpdateStock_btnOK);

                    // Log
                    logger.info("Bulk update stock for branch '{}', quantity: {}", branchName, branchQuantity);
                });
            }

            // Save changes
            iosUtils.click(loc_btnSave);
        }
    }


    private static class AddIMEIScreen {
        WebDriver driver;
        IOSUtils iosUtils;
        Logger logger = LogManager.getLogger();

        public AddIMEIScreen(WebDriver driver) {
            // Get driver
            this.driver = driver;


            // Init commons class
            iosUtils = new IOSUtils(driver);
        }

        By loc_btnSave = By.xpath("//XCUIElementTypeButton[@name=\"icon checked white\"]");
        By loc_icnRemoveIMEI = By.xpath("//XCUIElementTypeScrollView/XCUIElementTypeOther[1]/XCUIElementTypeOther[2]//XCUIElementTypeButton/XCUIElementTypeStaticText");
        By loc_txtIMEI = By.xpath("//XCUIElementTypeTextField[@value=\"Input IMEI/Serial number\" or @value=\"Nhập số IMEI/Serial\"]");
        By loc_btnAdd = By.xpath("//XCUIElementTypeImage[@name = \"icon_plus-white\"]");

        private void addIMEI(int quantity, String branchName, String variation) {
            // Remove old IMEI
            int size = iosUtils.getListElement(loc_icnRemoveIMEI).size();
            IntStream.range(0, size).forEach(ignored -> iosUtils.click(loc_icnRemoveIMEI));

            // Add imei value for variation
            IntStream.range(0, quantity).forEach(index -> {
                // Input imei value
                String imei = "%s%s_%s".formatted(variation.isEmpty() ? "" : "%s_".formatted(variation), branchName, index);
                iosUtils.sendKeys(loc_txtIMEI, imei);

                // Add
                iosUtils.click(loc_btnAdd);

                // Log
                logger.info("Add imei into branch '{}', value: {}", branchName, imei);
            });

            // Save changes
            iosUtils.click(loc_btnSave);
        }
    }

    private static class VariationSKUScreen {
        private final IOSUtils iosUtils;
        private final Logger logger = LogManager.getLogger();

        private By loc_txtBranchSKU(String branchName) {
            return By.xpath("//*[XCUIElementTypeStaticText[@name=\"%s\"]]/XCUIElementTypeTextField".formatted(branchName));
        }

        private final By loc_btnSave = By.xpath("//XCUIElementTypeButton[@name=\"icon checked white\"]");

        public VariationSKUScreen(WebDriver driver) {
            this.iosUtils = new IOSUtils(driver);
        }

        private void inputVariationSKU(List<String> branchNames, String SKU) {
            branchNames.forEach(branchName -> iosUtils.sendKeys(loc_txtBranchSKU(branchName), SKU));
            logger.info("Input variation SKU.");

            iosUtils.click(loc_btnSave);
            logger.info("Save the SKU changes.");
        }
    }
}
