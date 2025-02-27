package pages.android.seller.products.product_management;

import org.openqa.selenium.By;

import static utility.AndroidUtils.getLocatorByResourceId;
import static utility.AndroidUtils.getLocatorByResourceIdAndInstance;

public class ProductManagementElement {
    By loc_txtSearchBox = getLocatorByResourceId("%s:id/edtProductSearch");
    By loc_btnSort = getLocatorByResourceId("%s:id/ivSortType");

    By loc_lstSortOptions(int index) {
        return getLocatorByResourceIdAndInstance("%s:id/tvStatus", index);
    }

    By loc_btnFilter = getLocatorByResourceId("%s:id/btnFilterProduct");

    By loc_lblProductName(String productName) {
        return By.xpath("//android.widget.TextView[@* = '%s']".formatted(productName));
    }

    By loc_lblProductName = By.xpath("//*[@* ='%s:id/tvProductName']");

}
