package pages.web.buyer.login;

import api.buyer.login.APIBuyerLogin;
import api.seller.login.APISellerLogin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import utility.PropertiesUtils;

public class StorefrontLoginPage {

    private static final Logger logger = LogManager.getLogger();
    private final WebDriver driver;

    public StorefrontLoginPage(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * Logs in to the Storefront using JavaScript to set the authorization token.
     *
     * @param credentials The credentials of the buyer to authenticate.
     */
    public void loginStorefrontByJS(APISellerLogin.Credentials credentials) {
        // Log the start of the login process
        logger.info("Initiating login to Storefront using JavaScript");

        // Retrieve the buyer's access token using API
        String buyerAccessToken = new APIBuyerLogin().getBuyerInformation(credentials).getAccessToken();
        logger.info("Access token retrieved successfully");

        // Navigate to the Storefront URL
        String storeURL = PropertiesUtils.getStoreURL();
        driver.get(storeURL);
        logger.info("Navigated to Storefront URL: {}", storeURL);

        // Update the Authorization token in the cookie using JavaScript
        String script = "document.cookie = 'Authorization=\"Bearer %s\"'".formatted(buyerAccessToken);
        ((JavascriptExecutor) driver).executeScript(script);
        logger.info("Authorization token updated in cookie");

        // Refresh the page to load the new configuration
        driver.navigate().refresh();
        logger.info("Page refreshed to apply new session");

        // Log the completion of the login process
        logger.info("Successfully logged in to the Storefront via JavaScript");
    }
}
