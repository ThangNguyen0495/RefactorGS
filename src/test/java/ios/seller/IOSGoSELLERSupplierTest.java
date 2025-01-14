package ios.seller;

import api.seller.supplier.APICreateSupplier;
import baseTest.BaseTest;
import org.apache.logging.log4j.LogManager;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.ios.seller.home.HomeScreen;
import pages.ios.seller.login.LoginScreen;
import pages.ios.seller.supplier.IOSBaseSupplierScreen;
import utility.PropertiesUtils;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Test class for managing supplier operations on the dashboard.
 */
public class IOSGoSELLERSupplierTest extends BaseTest {
    private IOSBaseSupplierScreen baseSupplierScreen;

    /**
     * Sets up the test environment, including initializing the WebDriver and logging into the dashboard.
     *
     * @throws IOException        if an I/O exception occurs during setup.
     * @throws URISyntaxException if the URI syntax for configuration is invalid.
     */
    @BeforeClass
    private void setup() throws IOException, URISyntaxException {
        // Initialize WebDriver for the SELLER WEB environment
        initDriver("SELLER", "IOS");

        // Log into the goSELLER
        new LoginScreen(driver).performLogin(sellerCredentials);

        // Change application language
        new HomeScreen(driver).changeApplicationLanguage();

        // Initialize the BaseSupplierScreen instance
        baseSupplierScreen = new IOSBaseSupplierScreen(driver);
    }

    /**
     * Cleans up after the test suite by quitting the WebDriver instance.
     * This method runs after all tests in the suite are completed.
     */
    @AfterClass
    void tearDown() {
        if (driver != null) driver.quit();
    }

    /**
     * Provides test cases for creating and updating suppliers.
     *
     * @return An array of test case data, including supplier type, action type, and test name.
     */
    @DataProvider(name = "supplierTestCases")
    public Object[][] supplierTestCases() {
        return new Object[][]{
                {"Create Vietnam Supplier", true, false},
                {"Create Foreign Supplier", false, false},
                {"Update Vietnam Supplier", true, true},
                {"Update Foreign Supplier", false, true}
        };
    }

    /**
     * Runs test cases for managing suppliers, including creation and updates.
     *
     * @param isVietnamSupplier Indicates whether the supplier is a Vietnam-based supplier.
     * @param isUpdate          Indicates whether the test case is for updating a supplier.
     * @param testName          A descriptive name for the test case.
     */
    @Test(dataProvider = "supplierTestCases")
    void testSupplierManagement(String testName, boolean isVietnamSupplier, boolean isUpdate) {
        LogManager.getLogger().info("Running test: {}", testName);

        // Handle supplier creation or update based on the test parameters
        handleSupplier(isVietnamSupplier, isUpdate);
    }

    /**
     * Handles the logic for creating or updating a supplier.
     *
     * @param isVietnamSupplier Indicates whether the supplier is a Vietnam-based supplier.
     * @param isUpdate          Indicates whether the supplier is being updated.
     */
    private void handleSupplier(boolean isVietnamSupplier, boolean isUpdate) {
        // Navigate to the appropriate supplier page based on the action type
        if (isUpdate) {
            int supplierId = new APICreateSupplier(PropertiesUtils.getSellerCredentials()).createThenGetSupplierId();
            baseSupplierScreen.navigateToSupplierDetailScreenByItsId(supplierId);
        } else {
            baseSupplierScreen.navigateToCreateSupplierPage();
        }

        // Fetch supplier information, create a new supplier, and verify its details
        baseSupplierScreen
                .fetchSupplierInformation(PropertiesUtils.getSellerCredentials(), isVietnamSupplier)
                .createNewSupplier()
                .verifySupplierInformation();
    }
}