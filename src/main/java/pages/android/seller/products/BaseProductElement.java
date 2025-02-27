package pages.android.seller.products;

import org.openqa.selenium.By;

import static org.openqa.selenium.By.xpath;
import static utility.AndroidUtils.getLocatorByResourceId;
import static utility.AndroidUtils.getLocatorByResourceIdAndInstance;
import static utility.WebDriverManager.appBundleId;

public class BaseProductElement {
    By loc_btnSave = getLocatorByResourceId("%s:id/ivActionBarIconRight");
    By loc_icnDeleteImages = getLocatorByResourceId("%s:id/ivDelete");
    By loc_icnUploadImages = xpath("//*[contains(@resource-id, ':id/rlSelectImages')]");
    By loc_txtProductName = getLocatorByResourceId("%s:id/edtProductName");
    By loc_btnProductDescription = getLocatorByResourceId("%s:id/tvProductDescription");
    By loc_txtWithoutVariationListingPrice = By.xpath("//*[@resource-id = '%s:id/edtProductOrgPrice']//android.widget.EditText".formatted(appBundleId));
    By loc_txtWithoutVariationSellingPrice = By.xpath("//*[@resource-id = '%s:id/edtProductNewPrice']//android.widget.EditText".formatted(appBundleId));
    By loc_txtWithoutVariationCostPrice =  By.xpath("//*[@resource-id = '%s:id/edtProductCostPrice']//android.widget.EditText".formatted(appBundleId));
    By loc_txtWithoutVariationBarcode = getLocatorByResourceId("%s:id/edtProductBarcode");
    By loc_chkHideRemainingStock = getLocatorByResourceId("%s:id/ivHideStockOnOnlineStore");
    By loc_chkDisplayIfOutOfStock = getLocatorByResourceId("%s:id/ivDisplayIfOutOfStock");
    By loc_lblSelectedManageInventoryType = getLocatorByResourceId("%s:id/btnSwitchManageInventoryType");
    By loc_lblManageInventoryByIMEI = xpath("//*[@* = '%s:id/llManageInventoryByImeiSerial']".formatted(appBundleId));
    By loc_chkManageStockByLotDate = getLocatorByResourceId("%s:id/ivManageStockByLotDate");
    By loc_lblInventory = getLocatorByResourceId("%s:id/clInventoryContainer");
    By loc_swShipping = getLocatorByResourceId("%s:id/swShipping");
    By loc_txtShippingWeight = getLocatorByResourceId("%s:id/edtShippingWeight");
    By loc_txtShippingLength = getLocatorByResourceId("%s:id/edtShippingLength");
    By loc_txtShippingWidth = getLocatorByResourceId("%s:id/edtShippingWidth");
    By loc_txtShippingHeight = getLocatorByResourceId("%s:id/edtShippingHeight");
    By loc_swWeb = getLocatorByResourceId("%s:id/swPlatformWeb");
    By loc_swApp = getLocatorByResourceId("%s:id/swPlatformApp");
    By loc_swInStore = getLocatorByResourceId("%s:id/swPlatformInstore");
    By loc_swGoSocial = getLocatorByResourceId("%s:id/swPlatformGoSocial");
    By loc_swPriority = getLocatorByResourceId("%s:id/swPriority");
    By loc_txtPriorityValue = getLocatorByResourceId("%s:id/edtPriority");
    By loc_swVariations = getLocatorByResourceId("%s:id/swVariation");
    By loc_btnAddVariation = getLocatorByResourceId("%s:id/tvAddVariation");
    By loc_lstVariations(int variationIndex)  {
        return getLocatorByResourceIdAndInstance("%s:id/llVariationContainer", variationIndex);
    }
    By loc_btnEditMultiple = getLocatorByResourceId("%s:id/clEditMultiple");
    By loc_dlgWarningManageByLot_btnOK = getLocatorByResourceId("%s:id/tvRightButton");
}
