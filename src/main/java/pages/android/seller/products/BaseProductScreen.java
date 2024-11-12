package pages.android.seller.products;

import api.seller.product.APIGetProductDetail;
import io.appium.java_client.android.AndroidDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utility.AndroidUtils;


public class BaseProductScreen extends BaseProductElement {
    // WebDriver and WebUtils
    private final AndroidDriver driver;
    private final AndroidUtils androidUtils;

    // Logger
    private final Logger logger = LogManager.getLogger();

    private APIGetProductDetail.ProductInformation productInfo;
    private APIGetProductDetail.ProductInformation currentProductInfo;

    public BaseProductScreen(AndroidDriver driver) {
        this.driver = driver;
        this.androidUtils = new AndroidUtils(driver);
    }

    private void uploadProductImages() {
        //TODO remove old images
        androidUtils.click(loc_icnUploadImages);

        new SelectImagePopup(driver).selectImages();
    }

    private void inputProductName() {
        androidUtils.sendKeys(loc_txtProductName, productInfo.getName());
    }

    private void inputProductDescription() {
        androidUtils.click(loc_btnProductDescription);

        new ProductDescriptionScreen(driver).inputDescription(productInfo.getDescription());
    }

    private void handlesWithoutVariationPrices() {
        androidUtils.sendKeys(loc_txtWithoutVariationListingPrice, String.valueOf(productInfo.getOrgPrice()));
        androidUtils.sendKeys(loc_txtWithoutVariationSellingPrice, String.valueOf(productInfo.getNewPrice()));
        androidUtils.sendKeys(loc_txtWithoutVariationCostPrice, String.valueOf(productInfo.getCostPrice()));
    }

    private void selectVAT() {
        // TODO do it later
    }

    private void inputWithoutVariationSKU() {
        androidUtils.sendKeys(loc_txtWithoutVariationSKU, productInfo.getBranches().getFirst().getSku());
    }

    private void inputWithoutVariationBarcode() {
        androidUtils.sendKeys(loc_txtWithoutVariationBarcode, productInfo.getBarcode());
    }

    private void configHideRemainingStockOnOnlineStore() {
        boolean currentChkState = currentProductInfo != null && currentProductInfo.getIsHideStock();
        if (currentChkState == productInfo.getIsHideStock()) return;
        androidUtils.click(loc_chkHideRemainingStock);
    }

    private void configDisplayIfOutOfStock() {
        boolean currentChkState = currentProductInfo == null || currentProductInfo.isShowOutOfStock();
        if (currentChkState == productInfo.isShowOutOfStock()) return;
        androidUtils.click(loc_chkDisplayIfOutOfStock);
    }

    private void manageInventory() {
        if (currentProductInfo != null) return;
        if (!productInfo.getInventoryManageType().equals("IMEI_SERIAL_NUMBER")) return;
        androidUtils.click(loc_lblSelectedManageInventoryType);
        androidUtils.click(loc_lblManageInventoryByIMEI);
    }

    private void handlesStock() {
        // TODO do it later
    }

//    private void
}
