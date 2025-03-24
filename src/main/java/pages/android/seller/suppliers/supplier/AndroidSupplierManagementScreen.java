package pages.android.seller.suppliers.supplier;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.android.seller.home.AndroidSellerHomeScreen;
import utility.AndroidUtils;

import static utility.AndroidUtils.getLocatorById;

public class AndroidSupplierManagementScreen {
    private final AndroidUtils androidUtils;
    private final WebDriver driver;

    public AndroidSupplierManagementScreen(WebDriver driver) {
        this.driver = driver;
        this.androidUtils = new AndroidUtils(driver);
    }

    private final By loc_txtSearchBox = getLocatorById("%s:id/edtSupplierSearch");
    private final By loc_btnAddSupplier = getLocatorById("%s:id/ivActionBarIconRight");

    private By loc_lstSupplier(String supplierName) {
        return By.xpath("(//*[contains(@text, '%s')])[last()]".formatted(supplierName));
    }

    public AndroidSupplierManagementScreen navigateToSupplierManagementScreenByActivity() {
        new AndroidSellerHomeScreen(driver).navigateToSupplierManagementScreen();
        return this;
    }

    public void navigateToCreateSupplierScreen() {
        androidUtils.click(loc_btnAddSupplier);
    }

    public void navigateToSupplierDetailScreen(String supplierName) {
        androidUtils.sendKeys(loc_txtSearchBox, supplierName);
        androidUtils.click(loc_lstSupplier(supplierName));
    }
}
