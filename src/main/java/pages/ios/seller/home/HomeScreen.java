package pages.ios.seller.home;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import pages.ios.seller.account.AccountScreen;
import utility.IOSUtils;
import utility.WebUtils;

public class HomeScreen {
    WebDriver driver;
    IOSUtils iosUtils;
    Logger logger = LogManager.getLogger();
    public HomeScreen(WebDriver driver) {
        // Get driver
        this.driver = driver;

        // Init commons class
        iosUtils = new IOSUtils(driver);
    }

    By loc_icnAccount = By.xpath("//XCUIElementTypeTabBar[@name=\"Tab Bar\"]//XCUIElementTypeButton[3]");
    By loc_icnCreateProduct = By.xpath("//XCUIElementTypeStaticText[@name=\"Add new product\" or @name=\"Thêm sản phẩm mới\"]");
    By loc_icnProductManagement =  By.xpath("//XCUIElementTypeStaticText[@name=\"Product\" or @name=\"Sản phẩm\"]");
    By loc_icnSupplierManagement = By.xpath("//XCUIElementTypeStaticText[@name=\"Supplier\" or @name=\"Nhà cung cấp\"]");

    public void logout() {
        if (!iosUtils.getListElement(loc_icnAccount).isEmpty()) {
            // Navigate to Account screen
            iosUtils.click(loc_icnAccount);

            // Logout
            new AccountScreen(driver).logout();
        }
    }

    public void changeApplicationLanguage() {
        // Navigate to Account screen
        try {
            iosUtils.click(loc_icnAccount);
        } catch (TimeoutException ex) {
            WebUtils.sleep(3000);
            System.out.println(driver.getPageSource());
            iosUtils.click(loc_icnAccount);
        }

        // Change language
        new AccountScreen(driver).selectLanguage();
    }

    public void navigateToCreateProductScreen() {
        // Click create product icon
        iosUtils.click(loc_icnCreateProduct);

        // Log
        logger.info("Navigate create product screen");
    }

    public void navigateToProductManagementScreen() {
        // Click product management icon
        iosUtils.click(loc_icnProductManagement);

        // Log
        logger.info("Navigate product management screen");
    }

    public void navigateToSupplierManagementScreen() {
        // Click supplier management icon
        iosUtils.swipeToElement(loc_icnSupplierManagement);
        iosUtils.click(loc_icnSupplierManagement);

        // Log
        logger.info("Navigate to supplier management screen");
    }
}
