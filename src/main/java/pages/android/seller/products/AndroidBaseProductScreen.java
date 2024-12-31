package pages.android.seller.products;

import api.seller.login.APISellerLogin;
import api.seller.product.APIGetProductDetail;
import api.seller.product.APIGetProductDetail.ProductInformation;
import api.seller.product.APIGetProductList;
import api.seller.setting.APIGetBranchList;
import api.seller.setting.APIGetStoreDefaultLanguage;
import api.seller.setting.APIGetVATList;
import api.seller.user_feature.APIGetUserFeature;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import pages.android.seller.login.LoginScreen;
import pages.android.seller.products.product_management.ProductManagementScreen;
import utility.AndroidUtils;
import utility.PropertiesUtils;
import utility.WebUtils;
import utility.helper.ActivityHelper;
import utility.helper.ProductHelper;
import utility.helper.VariationHelper;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

import static api.seller.user_feature.APIGetUserFeature.*;
import static io.appium.java_client.AppiumBy.androidUIAutomator;
import static org.apache.commons.lang.math.RandomUtils.nextBoolean;
import static org.openqa.selenium.By.xpath;
import static utility.AndroidUtils.uiScrollResourceId;
import static utility.AndroidUtils.uiScrollResourceIdInstance;
import static utility.helper.ActivityHelper.*;


public class AndroidBaseProductScreen extends BaseProductElement {
    // WebDriver and WebUtils
    private final WebDriver driver;
    private final AndroidUtils androidUtils;
    String defaultLanguage;
    List<APIGetBranchList.BranchInformation> branchInfos;

    // Logger
    private final Logger logger = LogManager.getLogger();

    private ProductInformation currentProductInfo;
    private ProductInformation newProductInfo;

    public AndroidBaseProductScreen(WebDriver driver) {
        // Get driver
        this.driver = driver;

        // Init commons class
        androidUtils = new AndroidUtils(driver);
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
    public AndroidBaseProductScreen fetchInformation() {
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

    public AndroidBaseProductScreen navigateToCreateProductScreen() {
        // Navigate to create product screen
        androidUtils.navigateToScreenUsingScreenActivity(PropertiesUtils.getSellerBundleId(), ActivityHelper.sellerCreateProductActivity);

        // Log
        logger.info("Navigate to create product screen.");

        return this;
    }

    public void navigateToProductDetailScreen(int productId) {
        // Get product information
        this.currentProductInfo = fetchProductInformation(productId);

        // Get product name
        String productName = APIGetProductDetail.getMainProductName(currentProductInfo, defaultLanguage);

        // Navigate to product detail screen
        new ProductManagementScreen(driver).navigateToProductManagementScreen()
                .navigateToProductDetailScreen(productName);

        // Log
        logger.info("Navigate to product detail screen");

    }

    private void selectProductImages() {
        // Remove product images
        if (currentProductInfo != null) {
            int size = androidUtils.getListElement(loc_icnDeleteImages).size();
            IntStream.range(0, size).forEach(ignored -> androidUtils.click(loc_icnDeleteImages));
            logger.info("Remove old product images");
        }

        // Get list images
        String imagePath = System.getProperty("user.dir") + "/src/main/resources/files/images/images.png";

        // Sent list images to mobile device
        androidUtils.pushFileToMobileDevices(imagePath);

        // Open select image popup
        androidUtils.click(loc_icnUploadImages);

        // Select images
        new SelectImagePopup(driver).selectImages();

        // Log
        logger.info("Select product images.");
    }

    private void inputProductName() {
        // Input product name
        String name = newProductInfo.getName();
        androidUtils.sendKeys(loc_txtProductName, name);

        // Log
        logger.info("Input product name: {}", name);
    }

    private void inputProductDescription() {
        // Open description popup
        androidUtils.click(loc_btnProductDescription);

        // Input product description
        String description = newProductInfo.getDescription();
        new ProductDescriptionScreen(driver).inputDescription(description);

        // Log
        logger.info("Input product description: {}", description);
    }

    private void inputWithoutVariationPrice() {
        // Input listing price
        long listingPrice = newProductInfo.getOrgPrice();
        androidUtils.sendKeys(loc_txtWithoutVariationListingPrice, listingPrice);
        logger.info("Input without variation listing price: {}", String.format("%,d", listingPrice));

        // Input selling price
        long sellingPrice = newProductInfo.getNewPrice();
        androidUtils.sendKeys(loc_txtWithoutVariationSellingPrice, sellingPrice);
        logger.info("Input without variation selling price: {}", String.format("%,d", sellingPrice));

        // Input cost price
        long costPrice = newProductInfo.getCostPrice();
        androidUtils.sendKeys(loc_txtWithoutVariationCostPrice, costPrice);
        logger.info("Input without variation cost price: {}", String.format("%,d", costPrice));
    }

    private void inputWithoutVariationSKU() {
        // Input without variation SKU
        String sku = newProductInfo.getBranches().getFirst().getSku();
        androidUtils.sendKeys(loc_txtWithoutVariationSKU, sku);

        // Log
        logger.info("Input without variation SKU: {}", sku);
    }

    private void inputWithoutVariationBarcode() {
        // Input without variation barcode
        long barcode = Instant.now().toEpochMilli();
        androidUtils.sendKeys(loc_txtWithoutVariationBarcode, barcode);

        // Log
        logger.info("Input without variation barcode: {}", barcode);
    }

    private void hideRemainingStockOnOnlineStore() {
        // Get current checkbox status
//        boolean status = (currentProductInfo != null) && currentProductInfo.getIsHideStock();
//
//        // Hide remaining stock on online store config
//        if (!Objects.equals(newProductInfo.getIsHideStock(), status)) androidUtils.click(loc_chkHideRemainingStock);

        WebUtils.retryUntil(5, 1000, "Can not change hide remaining stock config.",
                () -> {
                    // Get current checkbox status
                    boolean status = androidUtils.isChecked(loc_chkHideRemainingStock);//(currentProductInfo == null) || currentProductInfo.isShowOutOfStock();
                    return Objects.equals(newProductInfo.getIsHideStock(), status);
                },
                () -> androidUtils.click(loc_chkHideRemainingStock));
        // Log
        logger.info("Hide remaining stock on online store config: {}", newProductInfo.getIsHideStock());
    }

    private void displayIfOutOfStock() {
        // Add display out of stock config
        WebUtils.retryUntil(5, 1000, "Can not change display when ou of stock config.",
                () -> {
                    // Get current checkbox status
                    boolean status = androidUtils.isChecked(loc_chkDisplayIfOutOfStock);//(currentProductInfo == null) || currentProductInfo.isShowOutOfStock();
                    return Objects.equals(newProductInfo.isShowOutOfStock(), status);
                },
                () -> androidUtils.click(loc_chkDisplayIfOutOfStock));

        // Log
        logger.info("Display out of stock config: {}", newProductInfo.isShowOutOfStock());
    }

    private void selectManageInventory() {
        // If product is managed by IMEI/Serial number
        if (newProductInfo.getInventoryManageType().equals("IMEI_SERIAL_NUMBER")) {
            // Open manage inventory dropdown
            androidUtils.click(loc_lblSelectedManageInventoryType);

            // Select manage inventory type
            androidUtils.click(loc_lblManageInventoryByIMEI);
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
        if (newProductInfo.isLotAvailable()) androidUtils.click(loc_chkManageStockByLotDate);

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
        androidUtils.click(loc_lblInventory);

        // Add without variation stock
        new InventoryScreen(driver).manageStock(isCreate, newProductInfo.getInventoryManageType().equals("IMEI_SERIAL_NUMBER"),
                branchInfos, "", newProductInfo.getBranches());
    }

    private void modifyShippingInformation() {
        // Update shipping status
        WebUtils.retryUntil(5, 1000, "Can not change Shipping switch status",
                () -> androidUtils.isChecked(loc_swShipping),
                () -> androidUtils.click(loc_swShipping));

        // Add product weight
        int weight = newProductInfo.getShippingInfo().getWeight();
        androidUtils.sendKeys(loc_txtShippingWeight, weight);
        logger.info("Add product weight: %,dg".formatted(weight));

        // Add product length
        int length = newProductInfo.getShippingInfo().getLength();
        androidUtils.sendKeys(loc_txtShippingLength, length);
        logger.info("Add product length: %,dcm".formatted(length));

        // Add product width
        int width = newProductInfo.getShippingInfo().getWidth();
        androidUtils.sendKeys(loc_txtShippingWidth, width);
        logger.info("Add product width: %,dcm".formatted(width));

        // Add product height
        int height = newProductInfo.getShippingInfo().getHeight();
        androidUtils.sendKeys(loc_txtShippingHeight, height);
        logger.info("Add product height: %,dcm".formatted(height));
    }

    private void modifyProductSellingPlatform() {
        /* WEB PLATFORM */
        // Get current show on web status
        boolean webStatus = androidUtils.isChecked(loc_swWeb);

        // Modify show on web config
        if (!Objects.equals(showOnWeb, webStatus)) androidUtils.click(loc_swWeb);

        // Log
        logger.info("On web configure: {}", showOnWeb);

        /* APP PLATFORM */
        // Get current show on app status
        boolean appStatus = androidUtils.isChecked(loc_swApp);

        // Modify show on app config
        if (!Objects.equals(showOnApp, appStatus)) androidUtils.click(loc_swApp);

        // Log
        logger.info("On app configure: {}", showOnApp);

        /* IN-STORE PLATFORM */
        // Get current show in-store status
        boolean inStoreStatus = androidUtils.isChecked(loc_swInStore);

        // Modify show in-store config
        if (!Objects.equals(showInStore, inStoreStatus)) androidUtils.click(loc_swInStore);

        // Log
        logger.info("In store configure: {}", showInStore);

        /* GO SOCIAL PLATFORM */
        // Get current show in goSocial status
        boolean goSocialStatus = androidUtils.isChecked(loc_swGoSocial);

        // Modify show in goSocial config
        if (!Objects.equals(showInGoSocial, goSocialStatus)) androidUtils.click(loc_swGoSocial);

        // Log
        logger.info("In goSOCIAL configure: {}", showInGoSocial);
    }

    private void modifyPriority() {
        // Get current priority config status
        boolean status = androidUtils.isChecked(loc_swPriority);

        // Update priority config
        if (!Objects.equals(newProductInfo.getPriority() > 0, status)) androidUtils.click(loc_swPriority);

        // If product has priority, add priority
        if (newProductInfo.getPriority() > 0) {
            // Input priority
            int priority = newProductInfo.getPriority();
            androidUtils.sendKeys(loc_txtPriorityValue, priority);

            // Log
            logger.info("Product priority: {}", priority);
            return;
        }

        logger.info("Product do not have priority configure");
    }

    private void removeOldVariations() {
        // If product without model, skip
        if (!currentProductInfo.isHasModel() || currentProductInfo.isLotAvailable()) {
            return;
        }

        // remove variation
        removeVariation();

        // Navigate to product detail screen
        navigateToProductDetailScreen(currentProductInfo.getId());

        // log
        logger.info("Remove old variation and navigate to product detail again");
    }

    private void removeVariation() {
        // If product is managed by Lot, that is not allow to remove variation
        if (currentProductInfo.isLotAvailable()) {
            logger.info("Product that is managed by Lot, do not allow remove variation");
            return;
        }

        // If product has variation, remove old variation
        if (!currentProductInfo.getModels().isEmpty()) {
            // Navigate to Add/Edit variation
            androidUtils.click(loc_btnAddVariation);

            // Get number of variation groups
            int numberOfVariationGroups = currentProductInfo.getModels().getFirst()
                    .getLabel().split("\\|").length;

            // Remove all variations and save changes
            new VariationScreen(driver).removeOldVariation(numberOfVariationGroups)
                    .saveChanges();

            // Save changes
            androidUtils.click(loc_btnSave);
        }
    }

    private void addVariations() {
        if ((currentProductInfo != null) && currentProductInfo.isLotAvailable()) {
            logger.info("Product that is managed by Lot, do not allow add variation");
            return;
        }

        // Navigate to Add/Edit variation
        WebUtils.retryUntil(5, 1000, "Can not change 'Variation' switch status",
                () -> androidUtils.isChecked(loc_swVariations),
                () -> androidUtils.click(loc_swVariations));

        androidUtils.click(loc_btnAddVariation);

        // Add/Edit variation
        var variationMap = VariationHelper.getVariationMap(APIGetProductDetail.getVariationName(newProductInfo, defaultLanguage),
                APIGetProductDetail.getVariationValues(newProductInfo, defaultLanguage));
        new VariationScreen(driver).addVariation(variationMap);
    }


    private void bulkUpdateVariations(boolean isCreate) {
        // Update variation information at product variation screen
        if ((newProductInfo.getModels().size() == 1)) {
            updateVariationInformation(isCreate);
        } else { // Update variation information at edit multiple screen
            // Navigate to edit multiple screen
            androidUtils.click(loc_btnEditMultiple);

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
        androidUtils.click(loc_btnSave);

        // Allow managing stock by lot-date
        if ((currentProductInfo != null) // Check if the current product info is available (indicating it's an update)
            && !currentProductInfo.isLotAvailable() // Before the update, stock was not managed by lot
            && newProductInfo.isLotAvailable()) { // After the update, stock is now managed by lot, so confirmation is needed
            // Click the confirmation button to acknowledge the change in stock management by lot
            androidUtils.click(loc_dlgWarningManageByLot_btnOK);

            // Log the confirmation of the stock management change
            logger.info("Confirmed management by lot.");
        }

        // Wait product management screen loaded
        androidUtils.waitUntilScreenLoaded(ActivityHelper.sellerProductMgmtActivity);

        // Logger
        LogManager.getLogger().info("===== STEP =====> [VerifyProductInformation] START... ");

        // If product are updated, check information after updating
        // Get product ID
        int productId = new APIGetProductList(this.credentials)
                .searchProductIdByName(this.newProductInfo.getName());
        this.newProductInfo.setId(productId);

        // Validate after create
        if (productId == 0) Assert.fail("Can not find product after created");

        // Logger
        LogManager.getLogger().info("===== STEP =====> [VerifyProductInformation] DONE!!! ");
    }

    /**
     * Verifies the product information by comparing the arranged product information
     * with the expected product information retrieved from the API. It checks various
     * attributes of the product to ensure they match the expected values.
     */
    public void verifyProductInformation() {
        // Log the start of the product information verification process
        logger.info("===== STEP =====> [VerifyProductInfo] START... ");

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
        Assert.assertEquals(actualProductInfo.isOnWeb(), this.newProductInfo.isOnWeb(),
                "Web platform config must be '%s', but found '%s'".formatted(this.newProductInfo.isOnWeb(), actualProductInfo.isOnWeb()));
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

        // Log the completion of the product information verification process
        logger.info("===== STEP =====> [VerifyProductInfo] END!!! ");

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
        // Select product images
        selectProductImages();

        // Determine operation type
        String operationType = isCreate ? "Create" : "Update";
        String productType = hasVariation ? "VariationProduct" : "WithoutVariationProduct";
        LogManager.getLogger().info("===== STEP =====> [{}{}] START... ", operationType, productType);

        // Fetch product information
        fetchProductInformation(isManagedByIMEI, hasVariation, branchStock);

        // For update operations, remove old variations
        if (!isCreate) {
            removeOldVariations();
        } else {
            selectManageInventory();
        }

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
        modifyProductSellingPlatform();
        modifyPriority();

        // Logger
        LogManager.getLogger().info("===== STEP =====> [{}{}] DONE!!! ", operationType, productType);

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
    public AndroidBaseProductScreen createProduct(boolean hasModel, boolean isManagedInventoryByIMEI, int... branchStock) {
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
    public AndroidBaseProductScreen updateProduct(boolean hasModel, int... branchStock) {
        manageProduct(false, this.currentProductInfo.getInventoryManageType().equals("IMEI_SERIAL_NUMBER"), hasModel, branchStock);
        return this;
    }


    public void updateEachVariationInformation(int... branchStock) {
        // Update variation information
        updateVariationInformation(true);

        // Save changes
        saveChanges();
    }

    private void updateVariationInformation(boolean isCreate) {
        // Init variation POM
        ProductVariationScreen productVariationScreen = new ProductVariationScreen(driver);

        // Update variation information
        IntStream.range(0, this.newProductInfo.getModels().size()).forEach(variationIndex -> {
            // Navigate to variation detail screen
            androidUtils.click(loc_lstVariations(variationIndex));

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

        private final AndroidUtils androidUtils;

        // Locator for the image list, used to find and select images on the popup.
        private final By loc_lstImages = androidUIAutomator(
                String.format(AndroidUtils.uiScrollResourceId, String.format("%s:id/tvSelectIndex", sellerBundleId))
        );

        // Locator for the save button, used to confirm and save the image selection.
        private final By loc_btnSave = androidUIAutomator(
                String.format(AndroidUtils.uiScrollResourceId, String.format("%s:id/fragment_choose_photo_dialog_btn_choose", sellerBundleId))
        );

        /**
         * Constructor for the SelectImagePopup class.
         * Initializes the AndroidUtils object with the provided AndroidDriver.
         *
         * @param driver the AndroidDriver instance used to interact with the Android UI elements.
         */
        public SelectImagePopup(WebDriver driver) {
            this.androidUtils = new AndroidUtils(driver);
        }

        /**
         * Selects images from the list and saves the selection.
         * This method clicks on the image list to select an image and then clicks the save button to confirm.
         */
        public void selectImages() {
            androidUtils.click(loc_lstImages);  // Selects images from the list.
            androidUtils.click(loc_btnSave);    // Clicks the save button to confirm the selection.
        }
    }

    private static class ProductVariationScreen {
        private final WebDriver driver;
        private final AndroidUtils androidUtils;
        private final Logger logger = LogManager.getLogger();
        private APIGetProductDetail.ProductInformation productInfo;
        private ProductInformation.Model model;
        private int variationIndex;
        private String variationValue;
        private List<APIGetBranchList.BranchInformation> branchInfos;

        public ProductVariationScreen(WebDriver driver) {
            // Get driver
            this.driver = driver;

            // Init commons class
            androidUtils = new AndroidUtils(driver);
        }

        By loc_btnSave = androidUIAutomator(uiScrollResourceId.formatted("%s:id/tvActionBarIconRight".formatted(sellerBundleId)));
        By loc_btnSelectImage = xpath("//android.widget.FrameLayout[*[@resource-id = '%s:id/rlSelectImages']]".formatted(sellerBundleId));
        By loc_txtVariationName = androidUIAutomator(uiScrollResourceId.formatted("%s:id/edtVersionName".formatted(sellerBundleId)));
        By loc_chkReuseProductDescription = androidUIAutomator(uiScrollResourceId.formatted("%s:id/ivUseProductDescription".formatted(sellerBundleId)));
        By loc_btnVariationDescription = androidUIAutomator(uiScrollResourceId.formatted("%s:id/tvVariationDescription".formatted(sellerBundleId)));
        By loc_txtVariationListingPrice = By.xpath("//*[@*= '%s:id/edtVariationOrgPrice']//*[@* = '%s:id/edtPriceCustom']".formatted(sellerBundleId, sellerBundleId));
        By loc_txtVariationSellingPrice = By.xpath("//*[@*= '%s:id/edtVariationNewPrice']//*[@* = '%s:id/edtPriceCustom']".formatted(sellerBundleId, sellerBundleId));
        By loc_txtVariationCostPrice = By.xpath("//*[@*= '%s:id/edtVariationCostPrice']//*[@* = '%s:id/edtPriceCustom']".formatted(sellerBundleId, sellerBundleId));
        By loc_txtVariationSKU = androidUIAutomator(uiScrollResourceId.formatted("%s:id/edtSKU".formatted(sellerBundleId)));
        By loc_txtVariationBarcode = androidUIAutomator(uiScrollResourceId.formatted("%s:id/edtBarcode".formatted(sellerBundleId)));
        By loc_btnInventory = androidUIAutomator(uiScrollResourceId.formatted("%s:id/clInventoryContainer".formatted(sellerBundleId)));
        By loc_btnDeactivate = androidUIAutomator(uiScrollResourceId.formatted("%s:id/tvActiveDeactive".formatted(sellerBundleId)));

        public ProductVariationScreen getVariationInformation(List<APIGetBranchList.BranchInformation> branchInfos, int variationIndex, APIGetProductDetail.ProductInformation productInfo) {
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
            // Get image path
            String imagePath = System.getProperty("user.dir") + "/src/main/resources/files/images/images.png";

            // Sent image to mobile device
            androidUtils.pushFileToMobileDevices(imagePath);

            // Open select image popup
            androidUtils.click(loc_btnSelectImage);

            // Select images
            new AndroidBaseProductScreen.SelectImagePopup(driver).selectImages();

            // Log
            logger.info("Select variation images.");
        }

        void updateVariationName() {
            // Input variation name
            String name = model.getVersionName();
            androidUtils.sendKeys(loc_txtVariationName, name);

            // Log
            logger.info("Input variation name: {}", name);
        }

        void updateVariationDescription() {
            // Get reuse description checkbox status
            boolean reuseParentDescription = model.isUseProductDescription();

            // Get current reuse description checkbox status
            boolean status = androidUtils.isChecked(loc_chkReuseProductDescription);

            // Uncheck reuse description checkbox
            if (!Objects.equals(reuseParentDescription, status)) androidUtils.click(loc_chkReuseProductDescription);

            if (reuseParentDescription) {
                // Log
                logger.info("Reuse parent description");
                return;
            }

            // Open description popup
            androidUtils.click(loc_btnVariationDescription);

            // Input product description
            String description = model.getDescription();
            new ProductDescriptionScreen(driver).inputDescription(description);

            // Log
            logger.info("Input variation description: {}", description);
        }

        void updateVariationPrice() {
            // Input listing price
            long listingPrice = model.getOrgPrice();
            androidUtils.sendKeys(loc_txtVariationListingPrice, String.valueOf(listingPrice));
            logger.info("Input variation listing price: {}", String.format("%,d", listingPrice));

            // Input selling price
            long sellingPrice = model.getNewPrice();
            androidUtils.sendKeys(loc_txtVariationSellingPrice, String.valueOf(sellingPrice));
            logger.info("Input variation selling price: {}", String.format("%,d", sellingPrice));

            // Input cost price
            long costPrice = model.getCostPrice();
            androidUtils.sendKeys(loc_txtVariationCostPrice, String.valueOf(costPrice));
            logger.info("Input variation cost price: {}", String.format("%,d", costPrice));
        }

        void updateVariationSKU() {
            // Input variation SKU
            String sku = model.getSku();
            androidUtils.sendKeys(loc_txtVariationSKU, sku);

            // Log
            logger.info("Input variation SKU: {}", sku);
        }

        void updateVariationBarcode() {
            // Input variation barcode
            String barcode = "";
            androidUtils.sendKeys(loc_txtVariationBarcode, barcode);

            // Log
            logger.info("Input variation barcode: {}", barcode);
        }

        void manageVariationStock(boolean isCreate) {
            // Check product is managed by lot or not
            if (!productInfo.isLotAvailable() || productInfo.getInventoryManageType().equals("IMEI_SERIAL_NUMBER")) {
                // Navigate to inventory screen
                WebUtils.retryUntil(5, 1000, "Can not navigate to Inventory screen",
                        () -> {
                            System.out.println(((AndroidDriver) driver).currentActivity());
                            return ((AndroidDriver) driver).currentActivity().equals(sellerProductBranchInventoryActivity);
                        },
                        () -> androidUtils.click(loc_btnInventory));

                // Add variation stock
                new InventoryScreen(driver).manageStock(isCreate, productInfo.getInventoryManageType().equals("IMEI_SERIAL_NUMBER"), branchInfos, variationValue, model.getBranches());
            } else logger.info("Product is managed by lot, requiring stock updates in the lot screen.");
        }

        void updateVariationStatus() {
            if (!androidUtils.getListElement(loc_btnDeactivate).isEmpty()) {
                // Get new variation status
                String newStatus = model.getStatus();

                // Get current variation status
                String currentStatus = APIGetProductDetail.getVariationStatus(productInfo, variationIndex);

                // Update variation status
                if (!currentStatus.equals(newStatus)) {
                    androidUtils.click(loc_btnDeactivate);
                }

                // Log
                logger.info("New variation's status: {}", newStatus);
            }
        }

        void completeUpdateVariation() {
            // Save all product information
            androidUtils.click(loc_btnSave);

            // Wait product detail screen loaded
            androidUtils.waitUntilScreenLoaded(sellerProductDetailActivity);
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
        AndroidUtils androidUtils;
        Logger logger = LogManager.getLogger();

        public VariationScreen(WebDriver driver) {
            this.driver = driver;
            androidUtils = new AndroidUtils(driver);
        }

        public VariationScreen removeOldVariation(int numOfVariationGroup) {
            // Check number of variation groups
            if (numOfVariationGroup <= 0) return this;

            // Remove old variation
            IntStream.range(0, numOfVariationGroup).forEach(ignored -> androidUtils.click(loc_btnRemoveVariationGroup));
            return this;
        }

        By loc_btnRemoveVariationGroup = androidUIAutomator(uiScrollResourceId.formatted("%s:id/ivDeleteVariation1".formatted(sellerBundleId)));
        By loc_btnAddVariation = androidUIAutomator(uiScrollResourceId.formatted("%s:id/tvAddVariation".formatted(sellerBundleId)));
        By loc_txtVariationName1 = androidUIAutomator(uiScrollResourceId.formatted("%s:id/edtVariation1Name".formatted(sellerBundleId)));
        By loc_txtVariationValue1 = androidUIAutomator(uiScrollResourceId.formatted("%s:id/edtInputImeiSerialNumberValue".formatted(sellerBundleId)));
        By loc_btnAddVariationValue1 = androidUIAutomator(uiScrollResourceId.formatted("%s:id/ivAddNewImeiSerialNumber".formatted(sellerBundleId)));
        By loc_txtVariationName2 = androidUIAutomator(uiScrollResourceId.formatted("%s:id/edtVariation2Name".formatted(sellerBundleId)));
        By loc_txtVariationValue2 = androidUIAutomator(uiScrollResourceId.formatted("%s:id/edtVariation2Value".formatted(sellerBundleId)));
        By loc_btnAddVariationValue2 = androidUIAutomator(uiScrollResourceId.formatted("%s:id/ivAddValueForVariation2".formatted(sellerBundleId)));
        By loc_btnSave = androidUIAutomator(uiScrollResourceId.formatted("%s:id/ivActionBarIconRight".formatted(sellerBundleId)));

        public void addVariation(Map<String, List<String>> variationMap) {
            // Remove old variation
            removeOldVariation(1);

            // Add variation
            IntStream.range(0, variationMap.keySet().size()).forEachOrdered(groupIndex -> {
                // Get variation group
                String variationGroup = variationMap.keySet().stream().toList().get(groupIndex);

                // Get variation value
                List<String> variationValue = variationMap.get(variationGroup);

                // Add new variation group
                androidUtils.click(loc_btnAddVariation);

                // Input variation group
                androidUtils.sendKeys(groupIndex == 0 ? loc_txtVariationName1 : loc_txtVariationName2, variationGroup);
                logger.info("Add variation group {}, group: {}", groupIndex + 1, variationGroup);

                // Input variation value
                for (String value : variationValue) {
                    androidUtils.sendKeys(groupIndex == 0 ? loc_txtVariationValue1 : loc_txtVariationValue2, value);
                    androidUtils.click(groupIndex == 0 ? loc_btnAddVariationValue1 : loc_btnAddVariationValue2);
                    logger.info("Add variation value for group {}, value: {}", groupIndex + 1, value);
                }
            });

            // Save changes
            saveChanges();

            // Log
            logger.info("Complete add variations");
        }

        public void saveChanges() {
            // Save changes
            androidUtils.click(loc_btnSave);
        }
    }


    /**
     * This class represents the product description screen in the Seller app.
     * It provides methods to input and save the product description.
     */
    public static class ProductDescriptionScreen {

        private final AndroidUtils androidUtils;

        // Locator for the description input field
        private final By locTxtContent = By.xpath("//android.widget.EditText");

        // Locator for the save button
        private final By locBtnSave = AppiumBy.androidUIAutomator(
                AndroidUtils.uiScrollResourceId.formatted("%s:id/ivActionBarIconRight".formatted(ActivityHelper.sellerBundleId))
        );

        /**
         * Constructor for the ProductDescriptionScreen class.
         * Initializes the AndroidUtils object with the provided AndroidDriver.
         *
         * @param driver the AndroidDriver instance used to interact with the Android UI elements.
         */
        public ProductDescriptionScreen(WebDriver driver) {
            this.androidUtils = new AndroidUtils(driver);
        }

        /**
         * Inputs the product description and saves the changes.
         *
         * @param description the description text to be entered.
         */
        public void inputDescription(String description) {
            // Input product description
            androidUtils.sendKeysActions(locTxtContent, description);

            // Save changes
            androidUtils.click(locBtnSave);
        }
    }

    public static class InventoryScreen {
        WebDriver driver;
        AndroidUtils androidUtils;
        Logger logger = LogManager.getLogger();

        public InventoryScreen(WebDriver driver) {
            this.driver = driver;
            androidUtils = new AndroidUtils(driver);
        }

        By loc_txtBranchStock(int branchIndex) {
            return androidUIAutomator(uiScrollResourceIdInstance.formatted("%s:id/edtStock".formatted(sellerBundleId), branchIndex));
        }

        By loc_btnSave = androidUIAutomator(uiScrollResourceId.formatted("%s:id/tvActionBarIconRight".formatted(sellerBundleId)));
        By loc_dlgUpdateStock = androidUIAutomator(uiScrollResourceId.formatted("%s:id/tabLayoutUpdateStockType".formatted(sellerBundleId)));
        By loc_dlgUpdateStock_tabChange = By.xpath("(//*[@* = '%s:id/tabLayoutUpdateStockType']//android.widget.TextView)[2]".formatted(sellerBundleId));
        By loc_dlgUpdateStock_txtQuantity = androidUIAutomator(uiScrollResourceId.formatted("%s:id/edtStock".formatted(sellerBundleId)));
        By loc_dlgUpdateStock_btnOK = androidUIAutomator(uiScrollResourceId.formatted("%s:id/tvUpdateStock".formatted(sellerBundleId)));

        /**
         * Manages stock for branches by either adding or updating based on the operation.
         *
         * @param isCreate       Indicates whether the operation is an update (false) or add (true).
         * @param manageByIMEI   Indicates if the stock management uses IMEI.
         * @param branchInfos    List of branch information from the API.
         * @param variation      Variation information for IMEI management.
         * @param branches       List of branches with their respective stock details.
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
                String currentStockText = androidUtils.getText(loc_txtBranchStock(branchIndex)).replaceAll("\\D+", "");
                int currentStock = currentStockText.isEmpty() ? 0 : Integer.parseInt(currentStockText);

                // Only update when the stock needs to be changed
                if (branchQuantity != currentStock) {
                    if (manageByIMEI) {
                        // Navigate to add IMEI screen
                        androidUtils.click(loc_txtBranchStock(branchIndex));

                        // Add IMEI
                        new AddIMEIScreen(driver).addIMEI(branchQuantity, branchName, variation);
                    } else {
                        // Handle stock based on operation type
                        androidUtils.click(loc_txtBranchStock(branchIndex));
                        if (!isCreate && !androidUtils.getListElement(loc_dlgUpdateStock).isEmpty()) {
                            // Update stock via popup for updates
                            androidUtils.click(loc_dlgUpdateStock_tabChange);
                            androidUtils.sendKeys(loc_dlgUpdateStock_txtQuantity, String.valueOf(branchQuantity));
                            androidUtils.click(loc_dlgUpdateStock_btnOK);
                        } else {
                            // Add or directly input stock
                            androidUtils.sendKeys(loc_txtBranchStock(branchIndex), String.valueOf(branchQuantity));
                        }
                    }

                    // Log the operation
                    String action = isCreate ? "Add" : "Update";
                    logger.info("{} stock for branch '{}', quantity: {}", action, branchName, branchQuantity);
                }
            });

            // Save changes
            androidUtils.click(loc_btnSave);
        }
    }


    public static class EditMultipleScreen {
        WebDriver driver;
        AndroidUtils androidUtils;
        Logger logger = LogManager.getLogger();

        public EditMultipleScreen(WebDriver driver) {
            this.driver = driver;
            androidUtils = new AndroidUtils(driver);
        }

        By loc_btnSave = androidUIAutomator(uiScrollResourceId.formatted("%s:id/tvActionBarIconRight".formatted(sellerBundleId)));
        By loc_ddvSelectedBranch = androidUIAutomator(uiScrollResourceId.formatted("%s:id/tvFilterBranches".formatted(sellerBundleId)));
        By loc_lstBranches(int branchIndex) {
            return androidUIAutomator(uiScrollResourceIdInstance.formatted("%s:id/ivUnChecked".formatted(sellerBundleId), branchIndex));
        }
        By loc_lblActions = androidUIAutomator(uiScrollResourceId.formatted("%s:id/ivAction".formatted(sellerBundleId)));
        By loc_lblUpdatePriceActions = By.xpath("(//*[@* = '%s:id/title'])[1]".formatted(sellerBundleId));
        By loc_lblUpdateStockActions = By.xpath("(//*[@* = '%s:id/title'])[2]".formatted(sellerBundleId));
        By loc_dlgUpdatePrice_txtListingPrice = By.xpath("//*[@* = '%s:id/edtOrgPrice']//*[@* = '%s:id/edtPriceCustom']".formatted(sellerBundleId, sellerBundleId));
        By loc_dlgUpdatePrice_txtSellingPrice = By.xpath("//*[@* = '%s:id/edtNewPrice']//*[@* = '%s:id/edtPriceCustom']".formatted(sellerBundleId, sellerBundleId));
        By loc_dlgUpdatePrice_btnOK = androidUIAutomator(uiScrollResourceId.formatted("%s:id/tvOK".formatted(sellerBundleId)));
        By loc_dlgUpdateStock_tabChange = By.xpath("(//*[@* = '%s:id/tabLayoutUpdateStockType']//android.widget.TextView)[2]".formatted(sellerBundleId));
        By loc_dlgUpdateStock_txtQuantity = androidUIAutomator(uiScrollResourceId.formatted("%s:id/edtStock".formatted(sellerBundleId)));
        By loc_dlgUpdateStock_btnOK = androidUIAutomator(uiScrollResourceId.formatted("%s:id/tvUpdateStock".formatted(sellerBundleId)));

        public void bulkUpdatePrice(long listingPrice, long sellingPrice) {
            // Open list actions
            androidUtils.click(loc_lblActions);

            // Select bulk update price actions
            androidUtils.click(loc_lblUpdatePriceActions);

            // Input listing price
            androidUtils.sendKeys(loc_dlgUpdatePrice_txtListingPrice, String.valueOf(listingPrice));
            logger.info("Bulk listing price: {}", String.format("%,d", listingPrice));

            // Input selling price
            androidUtils.sendKeys(loc_dlgUpdatePrice_txtSellingPrice, String.valueOf(sellingPrice));
            logger.info("Bulk selling price: {}", String.format("%,d", sellingPrice));

            // Save changes
            androidUtils.click(loc_dlgUpdatePrice_btnOK);
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
                    androidUtils.click(loc_ddvSelectedBranch);

                    // Switch branch
                    androidUtils.click(loc_lstBranches(branchIndex));

                    // Open list actions
                    androidUtils.click(loc_lblActions);

                    // Select bulk update stock actions
                    androidUtils.click(loc_lblUpdateStockActions);

                    // Switch to change tab
                    androidUtils.click(loc_dlgUpdateStock_tabChange);

                    // Input quantity
                    androidUtils.sendKeys(loc_dlgUpdateStock_txtQuantity, String.valueOf(branchQuantity));

                    // Save changes
                    androidUtils.click(loc_dlgUpdateStock_btnOK);

                    // Log
                    logger.info("Bulk update stock for branch '{}', quantity: {}", branchName, branchQuantity);
                });
            }

            // Save changes
            androidUtils.click(loc_btnSave);
        }
    }


    private static class AddIMEIScreen {
        WebDriver driver;
        AndroidUtils androidUtils;
        Logger logger = LogManager.getLogger();
        public AddIMEIScreen(WebDriver driver) {
            // Get driver
            this.driver = driver;


            // Init commons class
            androidUtils = new AndroidUtils(driver);
        }

        By loc_icnRemoveIMEI = androidUIAutomator(uiScrollResourceId.formatted("%s:id/ivDeleteIcon".formatted(sellerBundleId)));
        By loc_txtIMEI = androidUIAutomator(uiScrollResourceId.formatted("%s:id/edtInputImeiSerialNumberValue".formatted(sellerBundleId)));
        By loc_btnAdd = androidUIAutomator(uiScrollResourceId.formatted("%s:id/ivAddNewImeiSerialNumber".formatted(sellerBundleId)));
        By loc_btnSave = androidUIAutomator(uiScrollResourceId.formatted("%s:id/tvActionBarIconRight".formatted(sellerBundleId)));

        public void addIMEI(int quantity, String branchName, String variation) {
            // Remove old IMEI
            int size = androidUtils.getListElement(loc_icnRemoveIMEI).size();
            IntStream.range(0, size).forEach(ignored -> androidUtils.click(loc_icnRemoveIMEI));

            // Add imei value for variation
            IntStream.range(0, quantity).forEach(index -> {
                // Input imei value
                String imei = "%s%s_%s".formatted(variation.isEmpty() ? "" : "%s_".formatted(variation), branchName, index);
                androidUtils.sendKeys(loc_txtIMEI, imei);

                // Add
                androidUtils.click(loc_btnAdd);

                // Log
                logger.info("Add imei into branch '{}', value: {}", branchName, imei);
            });

            // Save changes
            androidUtils.click(loc_btnSave);
        }
    }
}
