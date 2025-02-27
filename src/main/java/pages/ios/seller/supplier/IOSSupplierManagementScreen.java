package pages.ios.seller.supplier;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.ios.seller.home.HomeScreen;
import utility.IOSUtils;

public class IOSSupplierManagementScreen {
    private final WebDriver driver;
    private final IOSUtils iosUtils;

    public IOSSupplierManagementScreen(WebDriver driver) {
        this.driver = driver;
        this.iosUtils = new IOSUtils(driver);
    }

    private final By loc_icnCreateSupplier = By.xpath("//XCUIElementTypeButton[@name=\"ic plus border\"]");
    private final By loc_txtSearchBox = By.xpath("//*[XCUIElementTypeImage[@name=\"icon_search\"]]/XCUIElementTypeTextField");

    private By loc_lstSupplier(String supplierName) {
        return By.xpath("(//*[contains(@name, '%s')])[last()]".formatted(supplierName));
    }

    public IOSSupplierManagementScreen navigateToSupplierManagementScreen() {
        // Relaunch app
        iosUtils.relaunchApp();

        new HomeScreen(driver).navigateToSupplierManagementScreen();
        return this;
    }

    public void navigateToCreateSupplierScreen() {
        iosUtils.click(loc_icnCreateSupplier);
    }

    public void navigateToSupplierDetailScreen(String supplierName) {
        iosUtils.sendKeys(loc_txtSearchBox, supplierName);
        iosUtils.click(loc_lstSupplier(supplierName));
    }
}
