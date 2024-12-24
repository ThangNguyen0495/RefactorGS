package pages.android.seller.login;

import api.seller.login.APISellerLogin;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import utility.AndroidUtils;
import utility.helper.ActivityHelper;

/**
 * Represents the login screen for the seller application.
 * Provides methods to interact with the login fields and perform login actions.
 */
public class LoginScreen {

    final static Logger logger = LogManager.getLogger(LoginScreen.class);
    private final AndroidUtils androidUtils;

    @Getter
    private static APISellerLogin.Credentials credentials;

    /**
     * Constructor for LoginScreen.
     *
     * @param driver the AndroidDriver instance used for interacting with the app.
     */
    public LoginScreen(WebDriver driver) {
        this.androidUtils = new AndroidUtils(driver);
    }

    // Locators for UI elements
    private final By loc_txtUsername = By.xpath("//*[ends-with(@resource-id,'edtUserName')]");
    private final By loc_txtPassword = By.xpath("//*[ends-with(@resource-id,'edtPassword')]");
    private final By loc_chkTermOfUse = By.xpath("//*[ends-with(@resource-id,'cbxTermAndPrivacy')]");
    private final By loc_btnLogin = By.xpath("//*[ends-with(@resource-id,'tvLogin')]");
    private final By loc_btnAcceptSavePassword = By.xpath("//android.widget.Button[@resource-id=\"android:id/autofill_save_yes\"]");

    /**
     * Inputs the username into the username field.
     *
     * @param username the username to input.
     */
    public void inputUsername(String username) {
        androidUtils.sendKeys(loc_txtUsername, username);
        logger.info("Input '{}' into Username field.", username);
    }

    /**
     * Inputs the password into the password field.
     *
     * @param password the password to input.
     */
    public void inputPassword(String password) {
        androidUtils.sendKeys(loc_txtPassword, password);
        logger.info("Input '{}' into Password field.", password);
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
        // Set login information for later use
        LoginScreen.credentials = loginInformation;

        // Perform login steps
        inputUsername(loginInformation.getUsername());
        inputPassword(loginInformation.getPassword());
        clickAgreeTerm();
        clickLoginBtn();

        // Wait for the home screen to load after login
        androidUtils.waitUntilScreenLoaded(ActivityHelper.sellerHomeActivity);

        // Accept saving password in Google Password Manager if prompted
        if (!androidUtils.getListElement(loc_btnAcceptSavePassword).isEmpty()) {
            androidUtils.click(loc_btnAcceptSavePassword);
            logger.info("Accepted saving password in Google Password Manager.");
        }
    }
}