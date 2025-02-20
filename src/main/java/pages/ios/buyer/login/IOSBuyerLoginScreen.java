package pages.ios.buyer.login;

import api.seller.login.APISellerLogin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.ios.buyer.account.IOSBuyerAccountScreen;
import pages.ios.seller.login.LoginScreen;
import utility.IOSUtils;

import java.time.Duration;

public class IOSBuyerLoginScreen {
    final static Logger logger = LogManager.getLogger(LoginScreen.class);

    WebDriver driver;
    WebDriverWait wait;
    IOSUtils iosUtils;

    By loc_txtUsername = By.xpath("//XCUIElementTypeTextField[@value=\"Email\"]");
    By loc_txtPassword = By.xpath("//XCUIElementTypeSecureTextField[@value=\"Mật khẩu\" or @value=\"Password\"]");
    By loc_btnLogin = By.xpath("//XCUIElementTypeButton[@name=\"Đăng nhập\" or @name=\"Login\"]");


    public IOSBuyerLoginScreen(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        iosUtils = new IOSUtils(driver);
    }



    private void inputUsername(String username) {
        iosUtils.sendKeys(loc_txtUsername, username);
        logger.info("Input '*****' into Username field.");
    }

    private void inputPassword(String password) {
        iosUtils.sendKeys(loc_txtPassword, password);
        logger.info("Input '*****' into Password field.");
    }

    private void clickLoginBtn() {
        iosUtils.click(loc_btnLogin);
        logger.info("Clicked on Login button.");
    }

    public IOSBuyerLoginScreen performLogin(APISellerLogin.Credentials credentials) {
        new IOSBuyerAccountScreen(driver).navigateToLoginScreen();
        inputUsername(credentials.getUsername());
        inputPassword(credentials.getPassword());
        clickLoginBtn();
        return this;
    }
}
