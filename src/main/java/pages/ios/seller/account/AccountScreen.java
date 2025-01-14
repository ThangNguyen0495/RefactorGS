package pages.ios.seller.account;

import io.appium.java_client.ios.IOSDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import utility.IOSUtils;
import utility.PropertiesUtils;

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

    private final By loc_icnLogout = By.xpath("//XCUIElementTypeImage[@name=\"icon_account_tab_logout\"]/preceding-sibling::XCUIElementTypeButton");
    private final By loc_dlgLogout_btnOK = By.xpath("//XCUIElementTypeButton[@name=\"OK\"]");
    private final By loc_btnLanguage = By.xpath("//*[XCUIElementTypeImage[@name=\"icon_account_tab_language\"]]/XCUIElementTypeButton");
    private final By loc_ddvVietNameOptions = By.xpath("//*[XCUIElementTypeImage[@name=\"flag_vietnam\"]]/XCUIElementTypeButton");
    private final By loc_ddvEnglishOptions = By.xpath("//*[XCUIElementTypeImage[@name=\"flag_usa\"]]/XCUIElementTypeButton");

    public void logout() {
        // Open logout popup
        iosUtils.click(loc_icnLogout);

        // Confirm logout
        iosUtils.click(loc_dlgLogout_btnOK);

        // Log
        logger.info("Logout");
    }

    public void selectLanguage() {
        // Open language dropdown
        iosUtils.click(loc_btnLanguage);

        // Select language
        iosUtils.click(PropertiesUtils.getLangKey().equals("vi") ? loc_ddvVietNameOptions : loc_ddvEnglishOptions);
    }
}
