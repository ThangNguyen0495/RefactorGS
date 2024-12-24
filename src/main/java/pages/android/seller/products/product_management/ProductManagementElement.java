package pages.android.seller.products.product_management;

import org.openqa.selenium.By;

import static io.appium.java_client.AppiumBy.androidUIAutomator;
import static utility.AndroidUtils.uiScrollResourceId;
import static utility.AndroidUtils.uiScrollResourceIdInstance;
import static utility.helper.ActivityHelper.sellerBundleId;

public class ProductManagementElement {
    By loc_txtSearchBox = androidUIAutomator(uiScrollResourceId.formatted("%s:id/edtProductSearch".formatted(sellerBundleId)));
    By loc_btnSort = androidUIAutomator(uiScrollResourceId.formatted("%s:id/ivSortType".formatted(sellerBundleId)));

    By loc_lstSortOptions(int index) {
        return androidUIAutomator(uiScrollResourceIdInstance.formatted("%s:id/tvStatus".formatted(sellerBundleId), index));
    }

    By loc_btnFilter = androidUIAutomator(uiScrollResourceId.formatted("%s:id/btnFilterProduct".formatted(sellerBundleId)));

    By loc_lblProductName(String productName) {
        return By.xpath("//android.widget.TextView[@* = '%s']".formatted(productName));
    }

    By loc_lblProductName = By.xpath("//*[@* ='%s:id/tvProductName']".formatted(sellerBundleId));

}
