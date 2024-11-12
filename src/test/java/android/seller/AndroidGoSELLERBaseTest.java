package android.seller;

import api.seller.login.APISellerLogin;
import io.appium.java_client.android.AndroidDriver;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;
import utility.ListenerUtils;
import utility.PropertiesUtils;

@Listeners(ListenerUtils.class)
public class AndroidGoSELLERBaseTest {
    public AndroidDriver driver;
    public APISellerLogin.Credentials credentials;

    /**
     * Initializes test configurations, such as retrieving seller credentials.
     * This method runs before all tests in the suite.
     */
    @BeforeSuite
    public void generateGoSELLERTestConfigs() {
        this.credentials = PropertiesUtils.getSellerCredentials();
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
