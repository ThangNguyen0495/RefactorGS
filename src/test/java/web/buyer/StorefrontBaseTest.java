package web.buyer;

import api.seller.login.APISellerLogin;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;
import utility.ListenerUtils;
import utility.PropertiesUtils;

@Listeners(ListenerUtils.class)
public class StorefrontBaseTest {

    public WebDriver driver;
    public APISellerLogin.Credentials sellerCredentials;
    public APISellerLogin.Credentials buyerCredentials;

    /**
     * Initializes test configurations, such as retrieving seller credentials.
     * This method runs before all tests in the suite.
     */
    @BeforeSuite
    public void generateStorefrontTestConfigs() {
        this.sellerCredentials = PropertiesUtils.getSellerCredentials();
        this.buyerCredentials = PropertiesUtils.getBuyerCredentials();
    }

    /**
     * Cleans up after the test suite by quitting the WebDriver instance.
     * This method runs after all tests in the suite are completed.
     */
    @AfterSuite
    void tearDown() {
        if (driver != null) driver.quit();
    }
}
