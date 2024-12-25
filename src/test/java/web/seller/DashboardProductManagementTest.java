package web.seller;

import baseTest.BaseTest;
import org.apache.logging.log4j.LogManager;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.web.seller.login.DashboardLoginPage;
import pages.web.seller.product.all_products.ProductManagementPage;

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
                {0, "testBulkUpdate_ClearStock"},
                {1, "testBulkUpdate_DeleteProducts"},
                {2, "testBulkUpdate_DeactivateProduct"},
                {3, "testBulkUpdate_ActivateProduct"},
                {4, "testBulkUpdate_UpdateStock"},
                {5, "testBulkUpdate_UpdateTax"},
                {6, "testBulkUpdate_DisplayOutOfStockProducts"},
                {7, "testBulkUpdate_UpdateSellingPlatform"},
                {8, "testBulkUpdate_UpdatePrice"},
                {9, "testBulkUpdate_SetStockAlert"},
                {10, "testBulkUpdate_ManageStockByLotDate"}
        };
    }

    @Test(dataProvider = "bulkUpdateActions")
    void checkBulkUpdateActions(int actionIndex, String testName) {
        LogManager.getLogger().info("Running test: {}", testName);
        productManagementPage.bulkUpdateAndVerifyProducts(actionIndex);
    }
}
