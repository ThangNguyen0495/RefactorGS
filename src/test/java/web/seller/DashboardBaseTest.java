package web.seller;

import api.seller.login.APISellerLogin;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;
import utility.ExtendReportListener;
import utility.PropertiesUtils;

@Listeners(ExtendReportListener.class)
public class DashboardBaseTest {

    public WebDriver driver;
    public APISellerLogin.Credentials credentials;

    /**
     * Initializes test configurations, such as retrieving seller credentials.
     * This method runs before all tests in the suite.
     */
    @BeforeSuite
    public void generateDashboardTestConfigs() {
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
