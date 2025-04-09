package pages.android.seller.home;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.android.seller.account.AndroidSellerAccountScreen;
import utility.AndroidUtils;
import utility.PropertiesUtils;

import static utility.AndroidUtils.*;

public class AndroidSellerHomeScreen {
    private final AndroidUtils androidUtils;
    private final WebDriver driver;
    private final String langKey = PropertiesUtils.getLangKey();
    public AndroidSellerHomeScreen(WebDriver driver) {
        this.driver = driver;
        androidUtils = new AndroidUtils(driver);
    }

    private final By loc_icnAccount = getLocatorByResourceId("%s:id/bottom_navigation_tab_account");
    private final By loc_icnSupplier = getLocatorByText(langKey.equals("vi") ? "Nhà cung cấp" : "Supplier");
    private final By loc_icnCreateProduct = getLocatorByText(langKey.equals("vi") ? "Thêm sản phẩm" : "Add product");
    private final By loc_icnProduct = getLocatorByText(langKey.equals("vi") ? "Sản phẩm" : "Products");

    public void changeApplicationLanguage() {
        // Navigate to Account screen
        androidUtils.click(loc_icnAccount);

        // Change language
        new AndroidSellerAccountScreen(driver).selectLanguage();
    }

    public void navigateToCreateProductScreen() {
        // Relaunch app to access the Home screen
        androidUtils.relaunchApp();

        // Navigate to create product screen
        androidUtils.click(loc_icnCreateProduct);
    }

    public void navigateToProductManagementScreen() {
        // Relaunch app to access the Home screen
        androidUtils.relaunchApp();

        // Navigate to product management screen
        androidUtils.click(loc_icnProduct);
    }

    public void navigateToSupplierManagementScreen() {
        // Relaunch app to access the Home screen
        androidUtils.relaunchApp();

        // Navigate to supplier management screen
        androidUtils.click(loc_icnSupplier);
    }
}
