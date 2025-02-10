package pages.android.seller.products;

import org.openqa.selenium.By;

import static org.openqa.selenium.By.xpath;
import static utility.AndroidUtils.getSellerLocatorByResourceId;
import static utility.AndroidUtils.getSellerLocatorByResourceIdAndInstance;
import static utility.helper.ActivityHelper.sellerBundleId;


public class BaseProductElement {
    By loc_btnSave = getSellerLocatorByResourceId("%s:id/ivActionBarIconRight");
    By loc_icnDeleteImages = getSellerLocatorByResourceId("%s:id/ivDelete");
    By loc_icnUploadImages = xpath("//*[contains(@resource-id, ':id/rlSelectImages')]");
    By loc_txtProductName = getSellerLocatorByResourceId("%s:id/edtProductName");
    By loc_btnProductDescription = getSellerLocatorByResourceId("%s:id/tvProductDescription");
    By loc_txtWithoutVariationListingPrice = By.xpath("//*[@resource-id = '%s:id/edtProductOrgPrice']//android.widget.EditText".formatted(sellerBundleId));
    By loc_txtWithoutVariationSellingPrice = By.xpath("//*[@resource-id = '%s:id/edtProductNewPrice']//android.widget.EditText".formatted(sellerBundleId));
    By loc_txtWithoutVariationCostPrice =  By.xpath("//*[@resource-id = '%s:id/edtProductCostPrice']//android.widget.EditText".formatted(sellerBundleId));
    By loc_txtWithoutVariationBarcode = getSellerLocatorByResourceId("%s:id/edtProductBarcode");
    By loc_chkHideRemainingStock = getSellerLocatorByResourceId("%s:id/ivHideStockOnOnlineStore");
    By loc_chkDisplayIfOutOfStock = getSellerLocatorByResourceId("%s:id/ivDisplayIfOutOfStock");
    By loc_lblSelectedManageInventoryType = getSellerLocatorByResourceId("%s:id/btnSwitchManageInventoryType");
    By loc_lblManageInventoryByIMEI = xpath("//*[@* = '%s:id/llManageInventoryByImeiSerial']".formatted(sellerBundleId));
    By loc_chkManageStockByLotDate = getSellerLocatorByResourceId("%s:id/ivManageStockByLotDate");
    By loc_lblInventory = getSellerLocatorByResourceId("%s:id/clInventoryContainer");
    By loc_swShipping = getSellerLocatorByResourceId("%s:id/swShipping");
    By loc_txtShippingWeight = getSellerLocatorByResourceId("%s:id/edtShippingWeight");
    By loc_txtShippingLength = getSellerLocatorByResourceId("%s:id/edtShippingLength");
    By loc_txtShippingWidth = getSellerLocatorByResourceId("%s:id/edtShippingWidth");
    By loc_txtShippingHeight = getSellerLocatorByResourceId("%s:id/edtShippingHeight");
    By loc_swWeb = getSellerLocatorByResourceId("%s:id/swPlatformWeb");
    By loc_swApp = getSellerLocatorByResourceId("%s:id/swPlatformApp");
    By loc_swInStore = getSellerLocatorByResourceId("%s:id/swPlatformInstore");
    By loc_swGoSocial = getSellerLocatorByResourceId("%s:id/swPlatformGoSocial");
    By loc_swPriority = getSellerLocatorByResourceId("%s:id/swPriority");
    By loc_txtPriorityValue = getSellerLocatorByResourceId("%s:id/edtPriority");
    By loc_swVariations = getSellerLocatorByResourceId("%s:id/swVariation");
    By loc_btnAddVariation = getSellerLocatorByResourceId("%s:id/tvAddVariation");
    By loc_lstVariations(int variationIndex)  {
        return getSellerLocatorByResourceIdAndInstance("%s:id/llVariationContainer", variationIndex);
    }
    By loc_btnEditMultiple = getSellerLocatorByResourceId("%s:id/clEditMultiple");
    By loc_dlgWarningManageByLot_btnOK = getSellerLocatorByResourceId("%s:id/tvRightButton");
}
