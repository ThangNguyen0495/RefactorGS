package pages.android.seller.products.create_product;

import org.openqa.selenium.By;
import org.openqa.selenium.support.pagefactory.ByChained;

import static io.appium.java_client.AppiumBy.androidUIAutomator;
import static org.openqa.selenium.By.xpath;
import static utility.helper.ActivityHelper.sellerBundleId;
import static utility.AndroidUtils.uiScrollResourceId;


public class CreateProductElement {
    By loc_btnSave = androidUIAutomator(uiScrollResourceId.formatted("%s:id/ivActionBarIconRight".formatted(sellerBundleId)));
    By loc_icnUploadImages = androidUIAutomator(uiScrollResourceId.formatted("%s:id/rlSelectImages".formatted(sellerBundleId)));
    By loc_txtProductName = androidUIAutomator(uiScrollResourceId.formatted("%s:id/edtProductName".formatted(sellerBundleId)));
    By loc_btnProductDescription = androidUIAutomator(uiScrollResourceId.formatted("%s:id/tvProductDescription".formatted(sellerBundleId)));
    By loc_txtWithoutVariationListingPrice = new ByChained(androidUIAutomator(uiScrollResourceId.formatted("%s:id/edtProductOrgPrice".formatted(sellerBundleId))), By.id("%s:id/edtPriceCustom".formatted(sellerBundleId)));
    By loc_txtWithoutVariationSellingPrice = new ByChained(androidUIAutomator(uiScrollResourceId.formatted("%s:id/edtProductNewPrice".formatted(sellerBundleId))), By.id("%s:id/edtPriceCustom".formatted(sellerBundleId)));
    By loc_txtWithoutVariationCostPrice = new ByChained(androidUIAutomator(uiScrollResourceId.formatted("%s:id/edtProductCostPrice".formatted(sellerBundleId))), By.id("%s:id/edtPriceCustom".formatted(sellerBundleId)));
    By loc_txtWithoutVariationSKU = androidUIAutomator(uiScrollResourceId.formatted("%s:id/edtSKU".formatted(sellerBundleId)));
    By loc_txtWithoutVariationBarcode = androidUIAutomator(uiScrollResourceId.formatted("%s:id/edtProductBarcode".formatted(sellerBundleId)));
    By loc_chkHideRemainingStock = androidUIAutomator(uiScrollResourceId.formatted("%s:id/ivHideStockOnOnlineStore".formatted(sellerBundleId)));
    By loc_chkDisplayIfOutOfStock = androidUIAutomator(uiScrollResourceId.formatted("%s:id/ivDisplayIfOutOfStock".formatted(sellerBundleId)));
    By loc_lblSelectedManageInventoryType = androidUIAutomator(uiScrollResourceId.formatted("%s:id/btnSwitchManageInventoryType".formatted(sellerBundleId)));
    By loc_lblManageInventoryByIMEI = xpath("//*[@* = '%s']".formatted("%s:id/llManageInventoryByImeiSerial".formatted(sellerBundleId)));
    By loc_chkManageStockByLotDate = androidUIAutomator(uiScrollResourceId.formatted("%s:id/ivManageStockByLotDate".formatted(sellerBundleId)));
    By loc_lblInventory = androidUIAutomator(uiScrollResourceId.formatted("%s:id/clInventoryContainer".formatted(sellerBundleId)));
    By loc_swShipping = androidUIAutomator(uiScrollResourceId.formatted("%s:id/swShipping".formatted(sellerBundleId)));
    By loc_txtShippingWeight = androidUIAutomator(uiScrollResourceId.formatted("%s:id/edtShippingWeight".formatted(sellerBundleId)));
    By loc_txtShippingLength = androidUIAutomator(uiScrollResourceId.formatted("%s:id/edtShippingLength".formatted(sellerBundleId)));
    By loc_txtShippingWidth = androidUIAutomator(uiScrollResourceId.formatted("%s:id/edtShippingWidth".formatted(sellerBundleId)));
    By loc_txtShippingHeight = androidUIAutomator(uiScrollResourceId.formatted("%s:id/edtShippingHeight".formatted(sellerBundleId)));
    By loc_swWeb = androidUIAutomator(uiScrollResourceId.formatted("%s:id/swPlatformWeb".formatted(sellerBundleId)));
    By loc_swApp = androidUIAutomator(uiScrollResourceId.formatted("%s:id/swPlatformApp".formatted(sellerBundleId)));
    By loc_swInStore = androidUIAutomator(uiScrollResourceId.formatted("%s:id/swPlatformInstore".formatted(sellerBundleId)));
    By loc_swGoSocial = androidUIAutomator(uiScrollResourceId.formatted("%s:id/swPlatformGoSocial".formatted(sellerBundleId)));
    By loc_swPriority = androidUIAutomator(uiScrollResourceId.formatted("%s:id/swPriority".formatted(sellerBundleId)));
    By loc_txtPriorityValue = androidUIAutomator(uiScrollResourceId.formatted("%s:id/edtPriority".formatted(sellerBundleId)));
    By loc_swVariations = androidUIAutomator(uiScrollResourceId.formatted("%s:id/swVariation".formatted(sellerBundleId)));
    By loc_btnAddVariation = androidUIAutomator(uiScrollResourceId.formatted("%s:id/tvAddVariation".formatted(sellerBundleId)));
    By loc_lstVariations = androidUIAutomator(uiScrollResourceId.formatted("%s:id/llVariationContainer".formatted(sellerBundleId)));
    By loc_btnEditMultiple = androidUIAutomator(uiScrollResourceId.formatted("%s:id/clEditMultiple".formatted(sellerBundleId)));
}
