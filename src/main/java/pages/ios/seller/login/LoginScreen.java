package pages.ios.seller.login;

import api.seller.login.APISellerLogin.Credentials;
import io.appium.java_client.ios.IOSDriver;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.ios.seller.home.HomeScreen;
import utility.IOSUtils;

import static io.appium.java_client.AppiumBy.iOSNsPredicateString;

public class LoginScreen {
    WebDriver driver;
    IOSUtils iosUtils;
    Logger logger = LogManager.getLogger();
    @Getter
    private static Credentials credentials;

    By loc_txtUsername = iOSNsPredicateString("type == \"XCUIElementTypeTextField\"");
    By loc_txtPassword = iOSNsPredicateString("type == \"XCUIElementTypeSecureTextField\"");
    By loc_chkTermOfUse = By.xpath("(//XCUIElementTypeTextView[@value]//preceding-sibling::*)[1]//XCUIElementTypeImage");
    By loc_btnLogin = By.xpath("//XCUIElementTypeButton[@name=\"LOGIN\"]");

    public LoginScreen(WebDriver driver) {
        // Get driver
        this.driver = driver;

        // Init commons class
        iosUtils = new IOSUtils(driver);
    }

    void allowNotificationPermission() {
        // Switch to notification permission and accept
        iosUtils.allowPermission("Allow");
    }

    void inputUsername(String username) {
        // Input username
        iosUtils.sendKeys(loc_txtUsername, username);

        // Log
        logger.info("Input username: ********");
    }

    void inputPassword(String password) {
        // Input password
        iosUtils.sendKeys(loc_txtPassword, password);

        // Log
        logger.info("Input password: ********");
    }

    void agreeTermOfUse() {
        // Agree term of use
        iosUtils.toggleCheckbox(loc_chkTermOfUse);

        // Log
        logger.info("Agree term of use");
    }

    void clickLoginBtn() {
        // Tap login button
        iosUtils.click(loc_btnLogin);

        // Log
        logger.info("Tap login button");
    }

    public void performLogin(Credentials credentials) {
        // Get login information
        LoginScreen.credentials = credentials;

        // Check if user are logged, logout and re-login with new account
        new HomeScreen(driver).logout();

        // Login with new account
        allowNotificationPermission();
        inputUsername(credentials.getUsername());
        inputPassword(credentials.getPassword());
        agreeTermOfUse();
        clickLoginBtn();
    }
}
