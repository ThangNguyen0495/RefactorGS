package pages.android.seller.products;


import api.seller.product.APIGetProductDetail;
import api.seller.setting.APIGetBranchList;
import io.appium.java_client.android.AndroidDriver;
import lombok.Data;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import utility.AndroidUtils;

import java.time.Instant;
import java.util.List;
import java.util.stream.IntStream;

import static io.appium.java_client.AppiumBy.androidUIAutomator;
import static org.apache.commons.lang.math.JVMRandom.nextLong;
import static org.apache.commons.lang.math.RandomUtils.nextBoolean;
import static org.openqa.selenium.By.xpath;
import static utility.helper.ActivityHelper.sellerBundleId;
import static utility.helper.ActivityHelper.sellerProductDetailActivity;
import static utility.AndroidUtils.uiScrollResourceId;
import static utility.helper.ProductHelper.MAX_PRICE;

public class ProductVariationScreen {
    AndroidDriver driver;
    AndroidUtils androidUtils;
    Logger logger = LogManager.getLogger();
    String defaultLanguage;
    boolean hasDiscount;
    boolean hasCostPrice;
    APIGetProductDetail.ProductInformation productInfo;
    int variationIndex;
    String variationValue;
    List<APIGetBranchList.BranchInformation> branchInfos;

    public ProductVariationScreen(AndroidDriver driver) {
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

    @Data
    public static class VariationInfo {
        private String variation;
        private String name;
        private String description;
        private String barcode;
        private long listingPrice;
        private long sellingPrice;
        private long costPrice;
        private List<Integer> stockQuantity;
        private String status = "ACTIVE";
    }

    private static long getCurrentEpoch() {
        return Instant.now().toEpochMilli();
    }

    @Getter
    public static VariationInfo variationInfo;

    public ProductVariationScreen getVariationInformation(String defaultLanguage, List<APIGetBranchList.BranchInformation> branchInfos, boolean hasDiscount, boolean hasCostPrice, int variationIndex, APIGetProductDetail.ProductInformation productInfo) {
        // Get default language
        this.defaultLanguage = defaultLanguage;

        // Get branch information
        this.branchInfos = branchInfos;

        // Get price condition
        this.hasDiscount = hasDiscount;

        // Get cost price condition
        this.hasCostPrice = hasCostPrice;

        // Get product information
        this.productInfo = productInfo;

        // Get variation index
        this.variationIndex = variationIndex;

        // Get variation value
        this.variationValue = APIGetProductDetail.getVariationValue(productInfo, this.defaultLanguage, this.variationIndex);

        // Log
        logger.info("Update information of '{}' variation", variationValue);

        //Init variation information model
        variationInfo = new VariationInfo();

        // Get variation model code
        variationInfo.setVariation(variationValue);

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
        new SelectImagePopup(driver).selectImages();

        // Log
        logger.info("Select variation images.");
    }

    void updateVariationName() {
        // Input variation name
        String name = "[%s][%s] Variation name %s".formatted(defaultLanguage, variationValue, getCurrentEpoch());
        androidUtils.sendKeys(loc_txtVariationName, name);

        // Get variation name
        variationInfo.setName(name);

        // Log
        logger.info("Input variation name: {}", name);
    }

    void updateVariationDescription() {
        // Get reuse description checkbox status
        boolean reuseParentDescription = nextBoolean();

        if (reuseParentDescription) {
            // Log
            logger.info("Reuse parent description");
        } else {
            // Get current reuse description checkbox status
            boolean status = androidUtils.isChecked(loc_chkReuseProductDescription);

            // Uncheck reuse description checkbox
            if (status) androidUtils.click(loc_chkReuseProductDescription);

            // Open description popup
            androidUtils.click(loc_btnVariationDescription);

            // Input product description
            String description = "[%s][%s] Variation description %s".formatted(defaultLanguage, variationValue, getCurrentEpoch());
            new ProductDescriptionScreen(driver).inputDescription(description);

            // Get variation description
            variationInfo.setDescription(description);

            // Log
            logger.info("Input variation description: {}", description);
        }
    }

    void updateVariationPrice() {
        // Input listing price
        long listingPrice = nextLong(MAX_PRICE);
        androidUtils.sendKeys(loc_txtVariationListingPrice, String.valueOf(listingPrice));
        logger.info("Input variation listing price: {}", String.format("%,d", listingPrice));

        // Input selling price
        long sellingPrice = hasDiscount ? nextLong(Math.max(listingPrice, 1)) : listingPrice;
        androidUtils.sendKeys(loc_txtVariationSellingPrice, String.valueOf(sellingPrice));
        logger.info("Input variation selling price: {}", String.format("%,d", sellingPrice));

        // Input cost price
        long costPrice = hasCostPrice ? nextLong(Math.max(sellingPrice, 1)) : 0;
        androidUtils.sendKeys(loc_txtVariationCostPrice, String.valueOf(costPrice));
        logger.info("Input variation cost price: {}", String.format("%,d", costPrice));

        // Get variation price
        variationInfo.setListingPrice(listingPrice);
        variationInfo.setSellingPrice(sellingPrice);
        variationInfo.setCostPrice(costPrice);
    }

    void updateVariationSKU() {
        // Input variation SKU
        String sku = "SKU%s".formatted(getCurrentEpoch());
        androidUtils.sendKeys(loc_txtVariationSKU, sku);

        // Log
        logger.info("Input variation SKU: {}", sku);
    }

    void updateVariationBarcode() {
        // Input variation barcode
        String barcode = "Barcode%s".formatted(getCurrentEpoch());
        androidUtils.sendKeys(loc_txtVariationBarcode, barcode);

        // Log
        logger.info("Input variation barcode: {}", barcode);

        // Get variation barcode
        variationInfo.setBarcode(barcode);
    }

    void addVariationStock(int... branchStock) {
        // Check product is managed by lot or not
        if (!productInfo.isLotAvailable() || productInfo.getInventoryManageType().equals("IMEI_SERIAL_NUMBER")) {
            // Navigate to inventory screen
            androidUtils.click(loc_btnInventory);

            // Add variation stock
            new InventoryScreen(driver).addStock(productInfo.getInventoryManageType().equals("IMEI_SERIAL_NUMBER"), branchInfos, variationValue, branchStock);
        } else logger.info("Product is managed by lot, requiring add stocks in the lot screen.");

        // Get new stock quantity
        List<Integer> stockQuantity = IntStream.range(0, APIGetBranchList.getActiveBranchIds(branchInfos).size())
                .mapToObj(branchIndex -> productInfo.isLotAvailable() ? 0 : (branchIndex >= branchStock.length) ? 0 : branchStock[branchIndex])
                .toList();
        variationInfo.setStockQuantity(stockQuantity);
    }

    void updateVariationStock(int... branchStock) {
        // Check product is managed by lot or not
        if (!productInfo.isLotAvailable() || productInfo.getInventoryManageType().equals("IMEI_SERIAL_NUMBER")) {
            // Navigate to inventory screen
            androidUtils.click(loc_btnInventory);

            // Add variation stock
            new InventoryScreen(driver).updateStock(productInfo.getInventoryManageType().equals("IMEI_SERIAL_NUMBER"), branchInfos, variationValue, branchStock);
        } else logger.info("Product is managed by lot, requiring stock updates in the lot screen.");

        // Get new stock quantity
        List<Integer> stockQuantity = IntStream.range(0, APIGetBranchList.getActiveBranchIds(branchInfos).size())
                .mapToObj(branchIndex -> productInfo.isLotAvailable() ? 0 : ((branchIndex >= branchStock.length) ? 0 : branchStock[branchIndex]))
                .toList();
        variationInfo.setStockQuantity(stockQuantity);
    }

    void updateVariationStatus() {
        if (!androidUtils.getListElement(loc_btnDeactivate).isEmpty()) {
            // Get new variation status
            String newStatus = nextBoolean() ? "ACTIVE" : "DEACTIVE";

            // Get current variation status
            String currentStatus = APIGetProductDetail.getVariationStatus(productInfo, variationIndex);

            // Update variation status
            if (!currentStatus.equals(newStatus)) {
                androidUtils.click(loc_btnDeactivate);
            }

            // Get variation status
            variationInfo.setStatus(newStatus);

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

    public void addVariationInformation(int... branchStock) {
        selectVariationImages();
        updateVariationName();
        updateVariationDescription();
        updateVariationPrice();
//        updateVariationSKU();
        updateVariationBarcode();
        addVariationStock(branchStock);
        completeUpdateVariation();
    }

    public void updateVariationInformation(int... branchStock) {
        selectVariationImages();
        updateVariationName();
        updateVariationDescription();
        updateVariationPrice();
//        updateVariationSKU();
        updateVariationBarcode();
        updateVariationStock(branchStock);
        updateVariationStatus();
        completeUpdateVariation();
    }
}
