package pages.ios.seller.home;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import pages.ios.seller.account.AccountScreen;
import utility.IOSUtils;

public class HomeScreen extends HomeElement{
    WebDriver driver;
    IOSUtils iosUtils;
    Logger logger = LogManager.getLogger();
    public HomeScreen(WebDriver driver) {
        // Get driver
        this.driver = driver;

        // Init commons class
        iosUtils = new IOSUtils(driver);
    }

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
        iosUtils.click(loc_icnAccount);

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
        iosUtils.click(loc_icnSupplierManagement);

        // Log
        logger.info("Navigate to supplier management screen");
    }
}
