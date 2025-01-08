package pages.android.seller.products.product_management;

import org.openqa.selenium.By;

import static utility.AndroidUtils.getSellerLocatorByResourceId;
import static utility.AndroidUtils.getSellerLocatorByResourceIdAndInstance;
import static utility.helper.ActivityHelper.sellerBundleId;

public class ProductManagementElement {
    By loc_txtSearchBox = getSellerLocatorByResourceId("%s:id/edtProductSearch");
    By loc_btnSort = getSellerLocatorByResourceId("%s:id/ivSortType");

    By loc_lstSortOptions(int index) {
        return getSellerLocatorByResourceIdAndInstance("%s:id/tvStatus", index);
    }

    By loc_btnFilter = getSellerLocatorByResourceId("%s:id/btnFilterProduct");

    By loc_lblProductName(String productName) {
        return By.xpath("//android.widget.TextView[@* = '%s']".formatted(productName));
    }

    By loc_lblProductName = By.xpath("//*[@* ='%s:id/tvProductName']");

}
