package pages.android.seller.products.create_product;


import api.seller.login.APISellerLogin;
import api.seller.product.APIGetProductDetail;
import api.seller.product.APIGetProductList;
import api.seller.setting.APIGetBranchList;
import api.seller.setting.APIGetStoreDefaultLanguage;
import api.seller.setting.APIGetStoreLanguage;
import io.appium.java_client.android.AndroidDriver;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import pages.android.seller.login.LoginScreen;
import pages.android.seller.products.*;
import utility.helper.ActivityHelper;
import utility.AndroidUtils;
import utility.PropertiesUtils;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

import static org.apache.commons.lang.math.JVMRandom.nextLong;
import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static utility.helper.ProductHelper.MAX_PRICE;

public class CreateProductScreen extends CreateProductElement {
    AndroidDriver driver;
    AndroidUtils androidUtils;
    Logger logger = LogManager.getLogger();
    private static String defaultLanguage;
    private static List<APIGetBranchList.BranchInformation> branchInfos;
    private static APIGetProductDetail.ProductInformation productInfo;
    private static List<APIGetStoreLanguage.LanguageInformation> languageInfos;
    private static APISellerLogin.Credentials credentials;

    public CreateProductScreen(AndroidDriver driver) {
        // Get driver
        this.driver = driver;

        // Init commons class
        androidUtils = new AndroidUtils(driver);

        // Get store information
        languageInfos = new APIGetStoreLanguage(LoginScreen.getCredentials()).getStoreLanguageInformation();

        // Get store default language
        defaultLanguage = new APIGetStoreDefaultLanguage(LoginScreen.getCredentials()).getDefaultLanguage();

        // Get branch information
        branchInfos = new APIGetBranchList(LoginScreen.getCredentials()).getBranchInformation();

        // Init product information model
        productInfo = new APIGetProductDetail.ProductInformation();
    }

    private boolean hideRemainingStock = false;
    private boolean showOutOfStock = true;
    private boolean manageByIMEI = false;
    private boolean manageByLot = false;
    private boolean hasDiscount = true;
    private boolean hasCostPrice = true;
    private boolean hasDimension = false;
    private boolean showOnWeb = true;
    private boolean showOnApp = true;
    private boolean showInStore = true;
    private boolean showInGoSocial = true;
    private boolean hasPriority = false;

    public CreateProductScreen getHideRemainingStock(boolean hideRemainingStock) {
        this.hideRemainingStock = hideRemainingStock;
        return this;
    }

    public CreateProductScreen getShowOutOfStock(boolean showOutOfStock) {
        this.showOutOfStock = showOutOfStock;
        return this;
    }

    public CreateProductScreen getManageByIMEI(boolean manageByIMEI) {
        this.manageByIMEI = manageByIMEI;
        return this;
    }

    public CreateProductScreen getManageByLotDate(boolean manageByLot) {
        this.manageByLot = manageByLot;
        return this;
    }

    public CreateProductScreen getHasDiscount(boolean hasDiscount) {
        this.hasDiscount = hasDiscount;
        return this;
    }

    public CreateProductScreen getHasCostPrice(boolean hasCostPrice) {
        this.hasCostPrice = hasCostPrice;
        return this;
    }

    public CreateProductScreen getHasDimension(boolean hasDimension) {
        this.hasDimension = hasDimension;
        return this;
    }

    public CreateProductScreen getProductSellingPlatform(boolean showOnWeb, boolean showOnApp, boolean showInStore, boolean showInGoSocial) {
        this.showOnWeb = showOnWeb;
        this.showOnApp = showOnApp;
        this.showInStore = showInStore;
        this.showInGoSocial = showInGoSocial;
        return this;
    }

    public CreateProductScreen getHasPriority(boolean hasPriority) {
        this.hasPriority = hasPriority;
        return this;
    }

    private static long getCurrentEpoch() {
        return Instant.now().toEpochMilli();
    }

    public CreateProductScreen navigateToCreateProductScreen() {
        // Navigate to create product screen
        androidUtils.navigateToScreenUsingScreenActivity(PropertiesUtils.getSellerBundleId(), ActivityHelper.sellerCreateProductActivity);

        // Log
        logger.info("Navigate to create product screen.");

        return this;
    }

    void selectProductImages() {
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

    void inputProductName() {
        // Input product name
        String name = "[%s][%s] Product name %s".formatted(defaultLanguage, manageByIMEI ? "IMEI" : "NORMAL", getCurrentEpoch());
        androidUtils.sendKeys(loc_txtProductName, name);

        // Log
        logger.info("Input product name: {}", name);
    }

    void inputProductDescription() {
        // Open description popup
        androidUtils.click(loc_btnProductDescription);

        // Input product description
        String description = "[%s] Product description %s".formatted(defaultLanguage, getCurrentEpoch());
        new ProductDescriptionScreen(driver).inputDescription(description);

        // Log
        logger.info("Input product description: {}", description);
    }

    void inputWithoutVariationPrice() {
        // Input listing price
        long listingPrice = nextLong(MAX_PRICE);
        androidUtils.sendKeys(loc_txtWithoutVariationListingPrice, String.valueOf(listingPrice));
        logger.info("Input without variation listing price: {}", String.format("%,d", listingPrice));

        // Input selling price
        long sellingPrice = hasDiscount ? nextLong(Math.max(listingPrice, 1)) : listingPrice;
        androidUtils.sendKeys(loc_txtWithoutVariationSellingPrice, String.valueOf(sellingPrice));
        logger.info("Input without variation selling price: {}", String.format("%,d", sellingPrice));

        // Input cost price
        long costPrice = hasCostPrice ? nextLong(Math.max(sellingPrice, 1)) : 0;
        androidUtils.sendKeys(loc_txtWithoutVariationCostPrice, String.valueOf(costPrice));
        logger.info("Input without variation cost price: {}", String.format("%,d", costPrice));
    }

    void inputWithoutVariationSKU() {
        // Input without variation SKU
        String sku = "SKU%s".formatted(getCurrentEpoch());
        androidUtils.sendKeys(loc_txtWithoutVariationSKU, sku);

        // Log
        logger.info("Input without variation SKU: {}", sku);
    }

    void inputWithoutVariationBarcode() {
        // Input without variation barcode
        String barcode = "Barcode%s".formatted(getCurrentEpoch());
        androidUtils.sendKeys(loc_txtWithoutVariationBarcode, barcode);

        // Log
        logger.info("Input without variation barcode: {}", barcode);
    }

    @SneakyThrows
    void hideRemainingStockOnOnlineStore() {
        // Hide remaining stock on online store config
        if (hideRemainingStock) androidUtils.click(loc_chkHideRemainingStock);

        // Log
        logger.info("Hide remaining stock on online store config: {}", hideRemainingStock);
    }

    @SneakyThrows
    void displayIfOutOfStock() {
        // Add display out of stock config
        if (!showOutOfStock) androidUtils.click(loc_chkDisplayIfOutOfStock);

        // Log
        logger.info("Display out of stock config: {}", showOutOfStock);

        // Get show out of stock config
        productInfo.setShowOutOfStock(showOutOfStock);
    }

    void selectManageInventory() {
        // If product is managed by IMEI/Serial number
        if (manageByIMEI) {
            // Open manage inventory dropdown
            androidUtils.click(loc_lblSelectedManageInventoryType);

            // Select manage inventory type
            androidUtils.click(loc_lblManageInventoryByIMEI);
        }

        // Log
        logger.info("Manage inventory by: {}", manageByIMEI ? "IMEI/Serial number" : "Product");

        // Get manage inventory type
        productInfo.setInventoryManageType(manageByIMEI ? "IMEI_SERIAL_NUMBER" : "PRODUCT");
    }

    void manageProductByLot() {
        if (!manageByIMEI) {
            // Manage product by lot
            if (manageByLot) androidUtils.click(loc_chkManageStockByLotDate);

            // Log
            logger.info("Manage product by lot date: {}", manageByLot);
        } else logger.info("Lot only support for the product has inventory managed by product");

        // Get lot available
        productInfo.setLotAvailable(manageByLot && !manageByIMEI);
    }

    void addWithoutVariationStock(int... branchStock) {
        // Check product is managed by lot or not
        if (!manageByLot || manageByIMEI) {
            // Navigate to inventory screen
            androidUtils.click(loc_lblInventory);

            // Add without variation stock
            new InventoryScreen(driver).addStock(manageByIMEI, branchInfos, "", branchStock);
        } else logger.info("Product is managed by lot, requiring stock updates in the lot screen.");
    }

    void modifyShippingInformation() {
        // Get current shipping config status
        boolean status = androidUtils.isChecked(loc_swShipping);

        // Update shipping status
        if (!Objects.equals(hasDimension, status)) androidUtils.click(loc_swShipping);

        // If product has dimension, add shipping configuration
        // Add product weight
        if (hasDimension) {
            androidUtils.sendKeys(loc_txtShippingWeight, "10");
            logger.info("Add product weight: 10g");

            // Add product length
            androidUtils.sendKeys(loc_txtShippingLength, "10");
            logger.info("Add product length: 10cm");

            // Add product width
            androidUtils.sendKeys(loc_txtShippingWidth, "10");
            logger.info("Add product width: 10cm");

            // Add product height
            androidUtils.sendKeys(loc_txtShippingHeight, "10");
            logger.info("Add product height: 10cm");
        } else logger.info("Product do not have shipping information.");
    }

    void modifyProductSellingPlatform() {
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

        // Get platform config
        productInfo.setOnApp(showOnApp);
        productInfo.setOnWeb(showOnWeb);
        productInfo.setInGosocial(showInGoSocial);
        productInfo.setInStore(showInStore);
    }

    void modifyPriority() {
        // Get current priority config status
        boolean status = androidUtils.isChecked(loc_swPriority);

        // Update priority config
        if (!Objects.equals(hasPriority, status)) androidUtils.click(loc_swPriority);

        // If product has priority, add priority
        if (hasPriority) {
            // Input priority
            int priority = nextInt(100);
            androidUtils.sendKeys(loc_txtPriorityValue, String.valueOf(priority));

            // Log
            logger.info("Product priority: {}", priority);
        } else logger.info("Product do not have priority configure");
    }

    void addVariations() {
        // Navigate to Add/Edit variation
        for (int retriesIndex = 0; retriesIndex < 5; retriesIndex++) {
            androidUtils.click(loc_swVariations);
            if (androidUtils.isChecked(loc_swVariations)) {
                break;
            }
        }
        androidUtils.click(loc_btnAddVariation);

        // Add/Edit variation
        new CRUDVariationScreen(driver).addVariation(defaultLanguage);
    }

    boolean updateEachVariationInformation = false;

    void bulkUpdateVariations(int increaseNum, int... branchStock) {
        // Get total variations
        int totalVariations = CRUDVariationScreen.getVariationMap().values().stream().mapToInt(List::size).reduce(1, (a, b) -> a * b);

        // Set update variation information flag
        updateEachVariationInformation = (totalVariations == 1);

        // Update variation information at product variation screen
        if (updateEachVariationInformation) {
            // Init variation POM
            ProductVariationScreen productVariationScreen = new ProductVariationScreen(driver);

            // Navigate to variation detail screen to update variation information
            androidUtils.click(loc_lstVariations);

            // Update variation information
            productVariationScreen.getVariationInformation(defaultLanguage, branchInfos, hasDiscount, hasCostPrice, 0, productInfo)
                    .addVariationInformation(branchStock);
        } else { // Update variation information at edit multiple screen
            // Navigate to edit multiple screen
            androidUtils.click(loc_btnEditMultiple);

            // Init edit multiple model
            EditMultipleScreen editMultipleScreen = new EditMultipleScreen(driver);

            // Bulk update price
            long listingPrice = nextLong(MAX_PRICE);
            long sellingPrice = hasDiscount ? nextLong(Math.max(listingPrice, 1)) : listingPrice;
            editMultipleScreen.bulkUpdatePrice(listingPrice, sellingPrice);

            // Bulk update stock
            editMultipleScreen.bulkUpdateStock(manageByIMEI, manageByLot, branchInfos, increaseNum, branchStock);
        }
    }

    void completeCreateProduct() {
        // Save all product information
        androidUtils.click(loc_btnSave);

        // Wait product management screen loaded
        androidUtils.waitUntilScreenLoaded(ActivityHelper.sellerProductMgmtActivity);

        // Logger
        LogManager.getLogger().info("===== STEP =====> [VerifyProductInformation] START... ");

        // If product are updated, check information after updating
        // Get product ID
        int productId = new APIGetProductList(LoginScreen.getCredentials()).searchProductIdByName(APIGetProductDetail.getMainProductName(productInfo, defaultLanguage));

        // Validate after create
        if (productId == 0) Assert.fail("Can not find product after created");

        // Logger
        LogManager.getLogger().info("===== STEP =====> [VerifyProductInformation] DONE!!! ");
    }

    public void createProductWithoutVariation(int... branchStock) {
        // Logger
        LogManager.getLogger().info("===== STEP =====> [CreateWithoutVariationProduct] START... ");

        // Add product information
        selectProductImages();
        inputProductName();
        inputProductDescription();
        inputWithoutVariationPrice();
        inputWithoutVariationSKU();
        inputWithoutVariationBarcode();
        hideRemainingStockOnOnlineStore();
        displayIfOutOfStock();
        selectManageInventory();
        manageProductByLot();
        addWithoutVariationStock(branchStock);
        modifyShippingInformation();
        modifyProductSellingPlatform();
        modifyPriority();

        // Logger
        LogManager.getLogger().info("===== STEP =====> [CreateWithoutVariationProduct] DONE!!! ");

        // Complete and check information
        completeCreateProduct();
    }

    public void createProductWithVariation(int increaseNum, int... branchStock) {
        // Logger
        LogManager.getLogger().info("===== STEP =====> [CreateVariationProduct] START... ");

        // Add product information
        selectProductImages();
        inputProductName();
        inputProductDescription();
        hideRemainingStockOnOnlineStore();
        displayIfOutOfStock();
        selectManageInventory();
        manageProductByLot();
        modifyShippingInformation();
        modifyProductSellingPlatform();
        modifyPriority();
        addVariations();
        bulkUpdateVariations(increaseNum, branchStock);

        // Logger
        LogManager.getLogger().info("===== STEP =====> [CreateVariationProduct] DONE!!! ");

        // Complete and check information
        completeCreateProduct();
    }
}
