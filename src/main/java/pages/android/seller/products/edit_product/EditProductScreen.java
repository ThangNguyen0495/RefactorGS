package pages.android.seller.products.edit_product;


import api.seller.product.APIGetProductDetail;
import api.seller.setting.APIGetBranchList;
import api.seller.setting.APIGetStoreDefaultLanguage;
import io.appium.java_client.android.AndroidDriver;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pages.android.seller.login.LoginScreen;
import pages.android.seller.products.*;
import pages.android.seller.products.product_management.ProductManagementScreen;
import utility.helper.ActivityHelper;
import utility.AndroidUtils;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import static org.apache.commons.lang.math.JVMRandom.nextLong;
import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static utility.helper.ProductHelper.MAX_PRICE;


public class EditProductScreen extends EditProductElement {
    AndroidDriver driver;
    AndroidUtils androidUtils;
    Logger logger = LogManager.getLogger();
    private static String defaultLanguage;
    private static List<APIGetBranchList.BranchInformation> branchInfos;
    private APIGetProductDetail.ProductInformation productInfo;
    ProductManagementScreen productManagementScreen;

    public EditProductScreen(AndroidDriver driver) {
        // Get driver
        this.driver = driver;

        // Init commons class
        androidUtils = new AndroidUtils(driver);

        // Init product management POM
        productManagementScreen = new ProductManagementScreen(driver);

        // Get store default language
        defaultLanguage = new APIGetStoreDefaultLanguage(LoginScreen.getCredentials())
                .getDefaultLanguage();

        // Get branch information
        branchInfos = new APIGetBranchList(LoginScreen.getCredentials()).getBranchInformation();
    }

    private boolean hideRemainingStock = false;
    private boolean showOutOfStock = true;
    private boolean manageByIMEI;
    private boolean manageByLot = false;
    private boolean hasDiscount = true;
    private boolean hasCostPrice = true;
    private boolean hasDimension = false;
    private boolean showOnWeb = true;
    private boolean showOnApp = true;
    private boolean showInStore = true;
    private boolean showInGoSocial = true;
    private boolean hasPriority = false;

    public EditProductScreen getHideRemainingStock(boolean hideRemainingStock) {
        this.hideRemainingStock = hideRemainingStock;
        return this;
    }

    public EditProductScreen isShowOutOfStock(boolean showOutOfStock) {
        this.showOutOfStock = showOutOfStock;
        return this;
    }

    public EditProductScreen getManageByLotDate(boolean manageByLot) {
        this.manageByLot = manageByLot;
        return this;
    }

    public EditProductScreen getHasDiscount(boolean hasDiscount) {
        this.hasDiscount = hasDiscount;
        return this;
    }

    public EditProductScreen getHasCostPrice(boolean hasCostPrice) {
        this.hasCostPrice = hasCostPrice;
        return this;
    }

    public EditProductScreen getHasDimension(boolean hasDimension) {
        this.hasDimension = hasDimension;
        return this;
    }

    public EditProductScreen getProductSellingPlatform(boolean showOnWeb, boolean showOnApp, boolean showInStore, boolean showInGoSocial) {
        this.showOnWeb = showOnWeb;
        this.showOnApp = showOnApp;
        this.showInStore = showInStore;
        this.showInGoSocial = showInGoSocial;
        return this;
    }

    public EditProductScreen getHasPriority(boolean hasPriority) {
        this.hasPriority = hasPriority;
        return this;
    }

    private static long getCurrentEpoch() {
        return Instant.now().toEpochMilli();
    }

    private boolean hasLot;
    private APIGetProductDetail apiProductDetail;

    public EditProductScreen navigateToProductDetailScreen(int productId) {
        // Get product information
        apiProductDetail = new APIGetProductDetail(LoginScreen.getCredentials());
        this.productInfo = apiProductDetail.getProductInformation(productId);

        // Get lot manage status
        this.hasLot = productInfo.isLotAvailable();

        // Get product name
        String productName = APIGetProductDetail.getMainProductName(productInfo, defaultLanguage);

        // get inventory manage type
        manageByIMEI = productInfo.getInventoryManageType().equals("IMEI_SERIAL_NUMBER");

        // Navigate to product detail screen
        productManagementScreen.navigateToProductManagementScreen()
                .navigateToProductDetailScreen(productName);

        // Log
        logger.info("Navigate to product detail screen");

        return this;
    }

    void removeOldVariations() {
        // Get product name
        String productName = APIGetProductDetail.getMainProductName(productInfo, defaultLanguage);

        // If product has model, remove model and saves changes.
        if (productInfo.isHasModel() && !productInfo.isLotAvailable()) {

            // remove variation
            removeVariation();

            // Navigate to product detail screen
            productManagementScreen.navigateToProductManagementScreen()
                    .navigateToProductDetailScreen(productName);

            // log
            logger.info("Remove old variation and navigate to product detail again");
        }
    }

    void selectProductImages() {
        // Remove product images
        int size = androidUtils.getListElement(loc_icnDeleteImages).size();
        IntStream.range(0, size).forEach(ignored -> androidUtils.click(loc_icnDeleteImages));
        logger.info("Remove old product images");

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
        // Get current checkbox status
        boolean status = productInfo.getIsHideStock();

        // Hide remaining stock on online store config
        if (!Objects.equals(hideRemainingStock, status)) androidUtils.click(loc_chkHideRemainingStock);

        // Log
        logger.info("Hide remaining stock on online store config: {}", hideRemainingStock);
    }

    @SneakyThrows
    void displayIfOutOfStock() {
        // Get current checkbox status
        boolean status = productInfo.isShowOutOfStock();

        // Add display out of stock config
        if (!Objects.equals(showOutOfStock, status)) androidUtils.click(loc_chkDisplayIfOutOfStock);

        // Log
        logger.info("Display out of stock config: {}", showOutOfStock);

        // Get new show out of stock config
        productInfo.setShowOutOfStock(showOutOfStock);
    }

    void manageProductByLot() {
        if (!manageByIMEI) {
            // Get current manage by lot checkbox status
            boolean status = productInfo.isLotAvailable();

            // Manage product by lot
            if (manageByLot && !status) androidUtils.click(loc_chkManageStockByLotDate);

            // Log
            logger.info("Manage product by lot date: {}", manageByLot || status);

            // Get new lot available
            productInfo.setLotAvailable(manageByLot || status);
        } else logger.info("Lot only support for the product has inventory managed by product");
    }

    void addWithoutVariationStock(int... branchStock) {
        // Check product is managed by lot or not
        if (!manageByLot || manageByIMEI) {
            // Navigate to inventory screen
            androidUtils.click(loc_lblInventory);

            // Add without variation stock
            new InventoryScreen(driver).updateStock(manageByIMEI, branchInfos, "", branchStock);
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

        // Get new platform config
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
        // If product is managed by Lot, that is not allow to remove variation
        if (this.hasLot) {
            logger.info("Product that is managed by Lot, do not allow add variation");
        } else {
            // Else navigate to Add/Edit variation screen to add new variation
            androidUtils.click(loc_swVariations);
            androidUtils.click(loc_btnAddVariation);

            // Add/Edit variation
            new CRUDVariationScreen(driver).addVariation(defaultLanguage);
        }
    }

    void removeVariation() {
        // If product is managed by Lot, that is not allow to remove variation
        if (this.hasLot) {
            logger.info("Product that is managed by Lot, do not allow remove variation");
        }
        // If product has variation, remove old variation
        else if (!androidUtils.getListElement(loc_lstVariations(0)).isEmpty()) {
            // Navigate to Add/Edit variation
            androidUtils.click(loc_btnAddVariation);

            // Remove all variations and save changes
            new CRUDVariationScreen(driver).removeOldVariation()
                    .saveChanges();

            // Save changes
            androidUtils.click(loc_btnSave);
        }
    }

    void bulkUpdateVariations(int increaseNum, int... branchStock) {
        // Get total variations
        int totalVariations = this.hasLot
                ? APIGetProductDetail.getVariationModelList(productInfo).size()
                : CRUDVariationScreen.getVariationMap()
                .values()
                .stream()
                .mapToInt(List::size)
                .reduce(1, (a, b) -> a * b);

        // Navigate to edit multiple screen
        if (totalVariations > 1) {
            androidUtils.click(loc_btnEditMultiple);

            // Init edit multiple model
            EditMultipleScreen editMultipleScreen = new EditMultipleScreen(driver);

            // Bulk update price
            long listingPrice = nextLong(MAX_PRICE);
            long sellingPrice = hasDiscount ? nextLong(Math.max(listingPrice, 1)) : listingPrice;
            editMultipleScreen.bulkUpdatePrice(listingPrice, sellingPrice);

            // Bulk update stock
            editMultipleScreen.bulkUpdateStock(manageByIMEI, manageByLot, branchInfos, increaseNum, branchStock);
        } else {
            // Can not bulk actions when total of variations is 1
            // So we must be updated variation information at that's detail screen
            updateVariationInformation(branchStock);
        }

    }

    void completeUpdateProduct() {
        // Save all product information
        androidUtils.click(loc_btnSave);

        // If product are managed by lot, accept when warning shows
        if (!androidUtils.getListElement(loc_dlgWarningManageByLot_btnOK).isEmpty()) {
            androidUtils.click(loc_dlgWarningManageByLot_btnOK);

            // Log
            logger.info("Confirm managed by lot");
        }

        // Wait product management screen loaded
        androidUtils.waitUntilScreenLoaded(ActivityHelper.sellerProductMgmtActivity);

        // Log
        logger.info("Product update successfully");
    }

    public void updateProductWithoutVariation(int... branchStock) {

        removeOldVariations();
        selectProductImages();
        inputProductName();
        inputProductDescription();
        inputWithoutVariationPrice();
//        inputWithoutVariationSKU();
        inputWithoutVariationBarcode();
        hideRemainingStockOnOnlineStore();
        displayIfOutOfStock();
        manageProductByLot();
        addWithoutVariationStock(branchStock);
        modifyShippingInformation();
        modifyProductSellingPlatform();
        modifyPriority();
        completeUpdateProduct();
    }

    public void updateProductWithVariation(int increaseNum, int... branchStock) {
        removeOldVariations();
        selectProductImages();
        inputProductName();
        inputProductDescription();
        hideRemainingStockOnOnlineStore();
        displayIfOutOfStock();
        manageProductByLot();
        modifyShippingInformation();
        modifyProductSellingPlatform();
        modifyPriority();
        addVariations();
        bulkUpdateVariations(increaseNum, branchStock);
        completeUpdateProduct();
    }

    public void updateEachVariationInformation(int... branchStock) {
        // Update variation information
        updateVariationInformation(branchStock);

        // Save changes
        completeUpdateProduct();
    }

    boolean updateVariationInformation = false;

    void updateVariationInformation(int... branchStock) {
        // Set update variation information flag
        updateVariationInformation = true;

        // Init variation POM
        ProductVariationScreen productVariationScreen = new ProductVariationScreen(driver);

        // Update variation information
        IntStream.range(0, APIGetProductDetail.getVariationValues(productInfo, defaultLanguage).size()).forEach(variationIndex -> {
            // Navigate to variation detail screen
            androidUtils.click(loc_lstVariations(variationIndex));

            // Update variation information
            productVariationScreen.getVariationInformation(defaultLanguage, branchInfos, hasDiscount, hasCostPrice, variationIndex, productInfo)
                    .updateVariationInformation(branchStock);
        });
    }
}
