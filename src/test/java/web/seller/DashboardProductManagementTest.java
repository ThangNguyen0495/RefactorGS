package web.seller;

import baseTest.BaseTest;
import org.apache.logging.log4j.LogManager;
import org.testng.annotations.*;
import pages.web.seller.login.DashboardLoginPage;
import pages.web.seller.product.all_products.ProductManagementPage;
import utility.ListenerUtils;

import java.io.IOException;
import java.net.URISyntaxException;

public class DashboardProductManagementTest extends BaseTest {
    private ProductManagementPage productManagementPage;

    @BeforeClass
    void setup() throws IOException, URISyntaxException {
        initDriver("SELLER", "WEB");
        new DashboardLoginPage(driver).loginDashboardByJs(sellerCredentials);
        productManagementPage = new ProductManagementPage(driver).fetchInformation(sellerCredentials);
    }

    /**
     * Cleans up after the test suite by quitting the WebDriver instance.
     * This method runs after all tests in the suite are completed.
     */
    @AfterClass
    void tearDown() {
        if (driver != null) driver.quit();
    }

    @DataProvider(name = "bulkUpdateActions")
    public Object[][] bulkUpdateActions() {
        return new Object[][]{
                {"Bulk Update: Clear Stock", 0},
                {"Bulk Update: Delete Products", 1},
                {"Bulk Update: Deactivate Product", 2},
                {"Bulk Update: Activate Product", 3},
                {"Bulk Update: Update Stock", 4},
                {"Bulk Update: Update Tax", 5},
                {"Bulk Update: Display Out Of Stock Products", 6},
                {"Bulk Update: Update Selling Platform", 7},
                {"Bulk Update: Update Price", 8},
                {"Bulk Update: Set Stock Alert", 9},
                {"Bulk Update: Manage Stock By Lot Date", 10}
        };

    }

    @Test(dataProvider = "bulkUpdateActions")
    void checkBulkUpdateActions(String testName, int actionIndex) {
        LogManager.getLogger().info("Running test: {}", testName);
        productManagementPage.bulkUpdateAndVerifyProducts(actionIndex);
    }
}
