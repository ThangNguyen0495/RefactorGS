package pages.web.seller.login;

import api.seller.login.APIDashboardLogin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import utility.PropertiesUtils;

/**
 * Represents the login page of the seller's dashboard.
 * <p>
 * This class provides functionality to log in to the dashboard by setting the necessary authentication tokens and
 * user information in local storage using JavaScript.
 * </p>
 */
public class LoginPage {

    private static final Logger logger = LogManager.getLogger(LoginPage.class);

    private final WebDriver driver;

    /**
     * Constructs a new instance of `LoginPage`.
     *
     * @param driver The WebDriver instance used to interact with the browser.
     */
    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * Logs in to the dashboard by setting local storage items using JavaScript.
     * <p>
     * This method accesses the dashboard URL, retrieves login credentials and user information, and then uses JavaScript
     * to set local storage items such as access token, refresh token, store ID, user ID, and store owner ID. Finally, it
     * refreshes the page to apply the login session.
     * </p>
     *
     * @param credentials The credentials used to log in and retrieve user information.
     */
    public void loginDashboardByJs(APIDashboardLogin.Credentials credentials) {
        // Access the dashboard to set the cookie
        driver.get(PropertiesUtils.getDomain());

        // Initialize login information model
        APIDashboardLogin.SellerInformation loginInfo = new APIDashboardLogin().getSellerInformation(credentials);

        // Set local storage items using JavaScript
        ((JavascriptExecutor) driver).executeScript("localStorage.setItem('accessToken', '%s')".formatted(loginInfo.getAccessToken()));
        ((JavascriptExecutor) driver).executeScript("localStorage.setItem('refreshToken', '%s')".formatted(loginInfo.getRefreshToken()));
        ((JavascriptExecutor) driver).executeScript("localStorage.setItem('storeId', %s)".formatted(loginInfo.getStore().getId()));
        ((JavascriptExecutor) driver).executeScript("localStorage.setItem('userId', %s)".formatted(loginInfo.getId()));
        ((JavascriptExecutor) driver).executeScript("localStorage.setItem('storeOwnerId', %s)".formatted(loginInfo.getId()));
        ((JavascriptExecutor) driver).executeScript("localStorage.setItem('storeFull', 'storeFull')");

        logger.info("Set local storage successfully");

        // Refresh the page to apply the login session
        driver.navigate().refresh();
    }
}
