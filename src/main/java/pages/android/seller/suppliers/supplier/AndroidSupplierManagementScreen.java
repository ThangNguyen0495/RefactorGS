package pages.android.seller.suppliers.supplier;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import utility.AndroidUtils;

import static utility.AndroidUtils.getLocatorByResourceId;
import static utility.helper.ActivityHelper.sellerSupplierMgmtActivity;

public class AndroidSupplierManagementScreen {
    private final AndroidUtils androidUtils;

    public AndroidSupplierManagementScreen(WebDriver driver) {
        this.androidUtils = new AndroidUtils(driver);
    }

    private final By loc_txtSearchBox = getLocatorByResourceId("%s:id/edtSupplierSearch");

    private By loc_lstSupplier(String supplierName) {
        return By.xpath("(//*[contains(@text, '%s')])[last()]".formatted(supplierName));
    }

    public AndroidSupplierManagementScreen navigateToSupplierManagementScreenByActivity() {
        androidUtils.navigateToScreenUsingScreenActivity(sellerSupplierMgmtActivity);
        return this;
    }

    public void navigateToSupplierDetailScreen(String supplierName) {
        androidUtils.sendKeys(loc_txtSearchBox, supplierName);
        androidUtils.click(loc_lstSupplier(supplierName));
    }
}
