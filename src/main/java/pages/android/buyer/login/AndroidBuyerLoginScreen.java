package pages.android.buyer.login;

import api.seller.login.APISellerLogin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.android.buyer.account.AndroidBuyerAccountScreen;
import pages.ios.seller.login.LoginScreen;
import utility.AndroidUtils;

import java.time.Duration;

public class AndroidBuyerLoginScreen {
    final static Logger logger = LogManager.getLogger(LoginScreen.class);

    WebDriver driver;
    WebDriverWait wait;
    AndroidUtils androidUtils;

    By loc_txtUsername = By.xpath("//*[ends-with(@resource-id,'field') and not (contains(@resource-id,'password'))]//*[ends-with(@resource-id,'edittext')]");
    By loc_txtPassword = By.xpath("//*[ends-with(@resource-id,'field') and contains(@resource-id,'password')]//*[ends-with(@resource-id,'edittext')]");
    By loc_btnLogin = By.xpath("//*[ends-with(@resource-id,'submit') or ends-with(@resource-id,'check_email')]");


    public AndroidBuyerLoginScreen(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        androidUtils = new AndroidUtils(driver);
    }



    private void inputUsername(String username) {
        androidUtils.sendKeys(loc_txtUsername, username);
        logger.info("Input '*****' into Username field.");
    }

    private void inputPassword(String password) {
        androidUtils.sendKeys(loc_txtPassword, password);
        logger.info("Input '*****' into Password field.");
    }

    private void clickLoginBtn() {
        androidUtils.click(loc_btnLogin);
        logger.info("Clicked on Login button.");
    }

    public AndroidBuyerLoginScreen performLogin(APISellerLogin.Credentials credentials) {
        new AndroidBuyerAccountScreen(driver).navigateToLoginScreen();
        inputUsername(credentials.getUsername());
        inputPassword(credentials.getPassword());
        clickLoginBtn();

        // Accept saving password in Google Password Manager if prompted
        androidUtils.acceptSavePasswordToGooglePasswordManager();

        // Relaunch app if it's crashed
        androidUtils.relaunchAppIfAppCrashed();
        return this;
    }
}