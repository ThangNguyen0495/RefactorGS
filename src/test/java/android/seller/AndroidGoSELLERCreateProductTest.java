package android.seller;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.android.seller.login.LoginScreen;
import pages.android.seller.products.AndroidBaseProductScreen;
import baseTest.BaseTest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

/**
 * Test class for creating products on the seller dashboard.
 * This class tests various scenarios for product creation
 * using different configurations of product attributes.
 */
public class AndroidGoSELLERCreateProductTest extends BaseTest {
    private AndroidBaseProductScreen productScreen;

    /**
     * Sets up the test environment before any tests are run.
     * Initializes the WebDriver and logs into the dashboard.
     */
    @BeforeClass
    void setup() throws IOException, URISyntaxException {
        initDriver("SELLER", "ANDROID");
        new LoginScreen(driver).performLogin(sellerCredentials);
        productScreen = new AndroidBaseProductScreen(driver).fetchInformation();
    }

    /**
     * Generates a test data object containing flags and a test name.
     *
     * @param hasModel        Whether the product has model variations.
     * @param isManagedByIMEI Whether the inventory is managed by IMEI/Serial number.
     * @param setterKey       String that determines which product attribute is being set.
     * @param testName        Descriptive name for the test.
     * @return An array representing the product configuration flags and test name.
     */
    private Object[] generateTestObject(boolean hasModel, boolean isManagedByIMEI, String setterKey, String testName) {
        boolean[] flags = new boolean[]{
                hasModel,
                isManagedByIMEI,
                setterKey.equals("noDiscount"),
                setterKey.equals("noCost"),
                setterKey.equals("hasSEO"),
                setterKey.equals("managedByLot"),
                setterKey.equals("randomPlatforms"),
                setterKey.equals("outOfStock")
        };

        return new Object[]{
                flags[0], flags[1], flags[2], flags[3], flags[4],
                flags[5], flags[6], flags[7], testName
        };
    }

    /**
     * Data provider for product test data.
     * Provides various combinations of product attributes to test.
     *
     * @return A 2D array of test data objects representing different product configurations.
     */
    @DataProvider(name = "productTestData")
    public Object[][] productTestData() {
        return new Object[][]{
                // G1: Without variation, manage inventory by Product
                generateTestObject(false, false, "noDiscount", "G1_01: Create product without variation, managed by Product and no discount"),
                generateTestObject(false, false, "noCost", "G1_02: Create product without variation, managed by Product and no cost"),
                generateTestObject(false, false, "hasDimension", "G1_03: Create product without variation, managed by Product and with dimension"),
                generateTestObject(false, false, "managedByLot", "G1_04: Create product without variation, managed by Product and managed by lot"),
                generateTestObject(false, false, "randomPlatforms", "G1_05: Create product without variation, managed by Product and randomized platforms"),
                generateTestObject(false, false, "outOfStock", "G1_06: Create product without variation, managed by Product and out of stock"),

                // G2: Without variation, manage inventory by IMEI/Serial number
                generateTestObject(false, true, "noDiscount", "G2_01: Create product without variation, managed by IMEI and no discount"),
                generateTestObject(false, true, "noCost", "G2_02: Create product without variation, managed by IMEI and no cost"),
                generateTestObject(false, true, "hasDimension", "G2_03: Create product without variation, managed by IMEI and with dimension"),
                generateTestObject(false, true, "randomPlatforms", "G2_04: Create product without variation, managed by IMEI and randomized platforms"),
                generateTestObject(false, true, "outOfStock", "G2_05: Create product without variation, managed by IMEI and out of stock"),

                // G3: With variation, manage inventory by Product
                generateTestObject(true, false, "noDiscount", "G3_01: Create product with variation, managed by Product and no discount"),
                generateTestObject(true, false, "noCost", "G3_02: Create product with variation, managed by Product and no cost"),
                generateTestObject(true, false, "hasDimension", "G3_03: Create product with variation, managed by Product and with dimension"),
                generateTestObject(true, false, "managedByLot", "G3_04: Create product with variation, managed by Product and managed by lot"),
                generateTestObject(true, false, "randomPlatforms", "G3_05: Create product with variation, managed by Product and randomized platforms"),
                generateTestObject(true, false, "outOfStock", "G3_06: Create product with variation, managed by Product and out of stock"),

                // G4: With variation, manage inventory by IMEI/Serial number
                generateTestObject(true, true, "noDiscount", "G4_01: Create product with variation, managed by IMEI and no discount"),
                generateTestObject(true, true, "noCost", "G4_02: Create product with variation, managed by IMEI and no cost"),
                generateTestObject(true, true, "hasDimension", "G4_03: Create product with variation, managed by IMEI and with dimension"),
                generateTestObject(true, true, "randomPlatforms", "G4_05: Create product with variation, managed by IMEI and randomized platforms"),
                generateTestObject(true, true, "outOfStock", "G4_06: Create product with variation, managed by IMEI and out of stock")
        };
    }

    /**
     * Test method to create a product with various configurations.
     * Uses the data provider to test different product attributes and behaviors.
     *
     * @param isVariation    Indicates if the product has variations.
     * @param isManagedByIMEI Indicates if the inventory is managed by IMEI/Serial number.
     * @param noDiscount     Indicates if no discount is applicable.
     * @param noCostPrice    Indicates if no cost price is applicable.
     * @param hasDimension   Indicates if the product has dimensions.
     * @param managedByLot   Indicates if inventory is managed by lot date.
     * @param randomPlatforms Indicates if platforms should be randomized.
     * @param outOfStock        Indicates if the product is out of stock.
     * @param testName       Descriptive name for the test.
     */
    @Test(dataProvider = "productTestData",
            description = "Create product with varying attributes")
    void createProductTest(boolean isVariation, boolean isManagedByIMEI, boolean noDiscount,
                           boolean noCostPrice, boolean hasDimension, boolean managedByLot,
                           boolean randomPlatforms, boolean outOfStock, String testName) {

        LogManager.getLogger().info("Running test: {}", testName);

        // Set product attributes based on test data
        productScreen.setNoDiscount(noDiscount);
        productScreen.setNoCost(noCostPrice);
        productScreen.setHasDimension(hasDimension);
        productScreen.setManageByLotDate(managedByLot);

        // Set platforms based on the randomPlatforms flag
        if (randomPlatforms) {
            productScreen.setSellingPlatform(false,
                    RandomUtils.nextBoolean(),
                    false,
                    RandomUtils.nextBoolean());
        }

        // Generate stock array based on inStock flag
        int[] stock = new int[RandomUtils.nextInt(10) + 1]; // Random stock size (1 to 10)
        Arrays.fill(stock, outOfStock ? 0 : RandomUtils.nextInt(10)); // Fill with random stock or 0 if out of stock

        // Create product based on whether it has variations or not
        createProduct(isVariation, isManagedByIMEI, stock);

    }

    /**
     * Helper method to create a product with variations.
     *
     * @param isIMEI Indicates if the inventory is managed by IMEI/Serial number.
     * @param branchStock The stock levels for each branch. Stock values are matched sequentially to branch IDs.
     *                    Branches without provided stock will default to 0.
     */
    private void createProduct(boolean hasModel, boolean isIMEI, int... branchStock) {
        productScreen.navigateToCreateProductScreen()
                .createProduct(hasModel, isIMEI, branchStock)
                .verifyProductInformation();
    }
}