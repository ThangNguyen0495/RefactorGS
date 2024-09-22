package web.buyer;

import api.seller.login.APISellerLogin;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.*;
import utility.ExtendReportListener;
import utility.PropertiesUtils;

import java.lang.reflect.Method;

@Listeners(ExtendReportListener.class)
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
     * Begins recording the test run for the specified method.
     * This method runs before each test method.
     *
     * @param method The test method that is about to be executed.
     */
    @BeforeMethod
    void beginTestRecording(Method method) {
        // Uncomment when SeleniumRecording is implemented
        // SeleniumRecording.startRecord(driver, method);
    }

    /**
     * Stops the recording of the test run after the test method execution.
     * This method runs after each test method.
     *
     * @param result The result of the test method execution.
     */
    @AfterMethod
    public void endTestRecording(ITestResult result) {
        // Uncomment when SeleniumRecording is implemented
        // SeleniumRecording.stopRecord(result);
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
