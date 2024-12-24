package pages.ios.seller.account;

import io.appium.java_client.ios.IOSDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import utility.IOSUtils;

public class AccountScreen {
    WebDriver driver;
    IOSUtils iosUtils;
    Logger logger = LogManager.getLogger();

    public AccountScreen(WebDriver driver) {
        // Get driver
        this.driver = driver;

        // Init commons class
        iosUtils = new IOSUtils(driver);
    }

    By loc_icnLogout = By.xpath("//XCUIElementTypeImage[@name=\"icon_account_tab_logout\"]/preceding-sibling::XCUIElementTypeButton");
    By loc_dlgLogout_btnOK = By.xpath("//XCUIElementTypeButton[@name=\"OK\"]");

    public void logout() {
        // Open logout popup
        iosUtils.click(loc_icnLogout);

        // Confirm logout
        iosUtils.click(loc_dlgLogout_btnOK);

        // Log
        logger.info("Logout");
    }
}
