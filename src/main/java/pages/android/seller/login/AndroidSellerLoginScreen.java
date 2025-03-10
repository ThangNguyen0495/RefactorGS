package pages.android.seller.login;

import api.seller.login.APISellerLogin;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import utility.AndroidUtils;

import static utility.AndroidUtils.getLocatorById;

/**
 * Represents the login screen for the seller application.
 * Provides methods to interact with the login fields and perform login actions.
 */
public class AndroidSellerLoginScreen {

    final static Logger logger = LogManager.getLogger(AndroidSellerLoginScreen.class);
    private final AndroidUtils androidUtils;

    @Getter
    private static APISellerLogin.Credentials credentials;

    /**
     * Constructor for LoginScreen.
     *
     * @param driver the AndroidDriver instance used for interacting with the app.
     */
    public AndroidSellerLoginScreen(WebDriver driver) {
        this.androidUtils = new AndroidUtils(driver);
    }

    // Locators for UI elements
    private final By loc_txtUsername = getLocatorById("%s:id/edtUserName");
    private final By loc_txtPassword = getLocatorById("%s:id/edtPassword");
    private final By loc_chkTermOfUse = getLocatorById("%s:id/cbxTermAndPrivacy");
    private final By loc_btnLogin = getLocatorById("%s:id/tvLogin");

    /**
     * Inputs the username into the username field.
     *
     * @param username the username to input.
     */
    public void inputUsername(String username) {
        androidUtils.sendKeys(loc_txtUsername, username);
        logger.info("Input '****' into Username field.");
    }

    /**
     * Inputs the password into the password field.
     *
     * @param password the password to input.
     */
    public void inputPassword(String password) {
        androidUtils.sendKeys(loc_txtPassword, password);
        logger.info("Input '****' into Password field.");
    }

    /**
     * Checks if the Terms of Use agreement checkbox is checked.
     *
     * @return true if the checkbox is checked, false otherwise.
     */
    public boolean isTermAgreementChecked() {
        boolean isChecked = androidUtils.isChecked(loc_chkTermOfUse);
        logger.info("Is Term Agreement checkbox checked: {}", isChecked);
        return isChecked;
    }

    /**
     * Clicks the Terms of Use agreement checkbox if it is not already checked.
     */
    public void clickAgreeTerm() {
        if (isTermAgreementChecked()) {
            logger.info("Term Agreement checkbox is already checked.");
            return;
        }
        androidUtils.click(loc_chkTermOfUse);
        logger.info("Clicked on Term Agreement checkbox.");
    }

    /**
     * Clicks the login button to initiate the login process.
     */
    public void clickLoginBtn() {
        androidUtils.click(loc_btnLogin);
        logger.info("Clicked on Login button.");
    }

    /**
     * Performs the login action using the provided credentials.
     *
     * @param loginInformation the credentials to use for login.
     */
    public void performLogin(APISellerLogin.Credentials loginInformation) {
        // If app crashed, restart app then actions
        androidUtils.relaunchAppIfAppCrashed();

        // Set login information for later use
        AndroidSellerLoginScreen.credentials = loginInformation;

        // Perform login steps
        inputUsername(loginInformation.getUsername());
        inputPassword(loginInformation.getPassword());
        clickAgreeTerm();
        clickLoginBtn();

        // Accept saving password in Google Password Manager if prompted
        androidUtils.acceptSavePasswordToGooglePasswordManager();
    }
}