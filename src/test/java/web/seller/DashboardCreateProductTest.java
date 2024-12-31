package web.seller;

import baseTest.BaseTest;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.web.seller.login.DashboardLoginPage;
import pages.web.seller.product.all_products.BaseProductPage;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

/**
 * Test class for creating products on the seller dashboard.
 * This class tests various scenarios for product creation
 * using different configurations of product attributes.
 */
public class DashboardCreateProductTest extends BaseTest {
    private BaseProductPage productPage;

    /**
     * Sets up the test environment before any tests are run.
     * Initializes the WebDriver and logs into the dashboard.
     *
     * @throws IOException       if an error occurs while initializing the WebDriver or loading credentials.
     * @throws URISyntaxException if there is an error in parsing the URI for the resources.
     */
    @BeforeClass
    void setup() throws IOException, URISyntaxException {
        initDriver("SELLER", "WEB");
        productPage = new BaseProductPage(driver).fetchInformation(sellerCredentials);
        new DashboardLoginPage(driver).loginDashboardByJs(sellerCredentials);
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
     * Generates a test data object containing flags and a test name.
     * The test name describes the scenario for product creation with varying attributes.
     *
     * @param testName        Descriptive name for the test case.
     * @param hasModel        Whether the product has model variations.
     * @param isManagedByIMEI Whether the inventory is managed by IMEI/Serial number.
     * @param setterKey       String that determines which product attribute is being set.
     * @return An array representing the product configuration flags and test name.
     */
    private Object[] generateTestObject(String testName, boolean hasModel, boolean isManagedByIMEI, String setterKey) {
        boolean[] flags = new boolean[]{
                hasModel,
                isManagedByIMEI,
                setterKey.equals("noDiscount"),
                setterKey.equals("noCost"),
                setterKey.equals("hasDimension"),
                setterKey.equals("hasSEO"),
                setterKey.equals("managedByLot"),
                setterKey.equals("hasAttribution"),
                setterKey.equals("randomPlatforms"),
                setterKey.equals("outOfStock")
        };

        return new Object[]{
                testName, flags[0], flags[1], flags[2], flags[3], flags[4],
                flags[5], flags[6], flags[7], flags[8], flags[9]
        };
    }

    /**
     * Data provider for product test data.
     * Provides various combinations of product attributes to test different configurations.
     *
     * @return A 2D array of test data objects representing different product configurations.
     */
    @DataProvider(name = "regressionTestData")
    public Object[][] regressionTestData() {
        return new Object[][]{
                // G1: Without variation, manage inventory by Product
                generateTestObject("G1_01: Create product without variation, managed by Product and no discount", false, false, "noDiscount"),
                generateTestObject("G1_02: Create product without variation, managed by Product and no cost", false, false, "noCost"),
                generateTestObject("G1_03: Create product without variation, managed by Product and with dimension", false, false, "hasDimension"),
                generateTestObject("G1_04: Create product without variation, managed by Product and with SEO", false, false, "hasSEO"),
                generateTestObject("G1_05: Create product without variation, managed by Product and managed by lot", false, false, "managedByLot"),
                generateTestObject("G1_06: Create product without variation, managed by Product and with attribution", false, false, "hasAttribution"),
                generateTestObject("G1_07: Create product without variation, managed by Product and randomized platforms", false, false, "randomPlatforms"),
                generateTestObject("G1_08: Create product without variation, managed by Product and out of stock", false, false, "outOfStock"),

                // G2: Without variation, manage inventory by IMEI/Serial number
                generateTestObject("G2_01: Create product without variation, managed by IMEI and no discount", false, true, "noDiscount"),
                generateTestObject("G2_02: Create product without variation, managed by IMEI and no cost", false, true, "noCost"),
                generateTestObject("G2_03: Create product without variation, managed by IMEI and with dimension", false, true, "hasDimension"),
                generateTestObject("G2_04: Create product without variation, managed by IMEI and with SEO", false, true, "hasSEO"),
                generateTestObject("G2_05: Create product without variation, managed by IMEI and with attribution", false, true, "hasAttribution"),
                generateTestObject("G2_06: Create product without variation, managed by IMEI and randomized platforms", false, true, "randomPlatforms"),
                generateTestObject("G2_07: Create product without variation, managed by IMEI and out of stock", false, true, "outOfStock"),

                // G3: With variation, manage inventory by Product
                generateTestObject("G3_01: Create product with variation, managed by Product and no discount", true, false, "noDiscount"),
                generateTestObject("G3_02: Create product with variation, managed by Product and no cost", true, false, "noCost"),
                generateTestObject("G3_03: Create product with variation, managed by Product and with dimension", true, false, "hasDimension"),
                generateTestObject("G3_04: Create product with variation, managed by Product and with SEO", true, false, "hasSEO"),
                generateTestObject("G3_05: Create product with variation, managed by Product and managed by lot", true, false, "managedByLot"),
                generateTestObject("G3_06: Create product with variation, managed by Product and with attribution", true, false, "hasAttribution"),
                generateTestObject("G3_07: Create product with variation, managed by Product and randomized platforms", true, false, "randomPlatforms"),
                generateTestObject("G3_08: Create product with variation, managed by Product and out of stock", true, false, "outOfStock"),

                // G4: With variation, manage inventory by IMEI/Serial number
                generateTestObject("G4_01: Create product with variation, managed by IMEI and no discount", true, true, "noDiscount"),
                generateTestObject("G4_02: Create product with variation, managed by IMEI and no cost", true, true, "noCost"),
                generateTestObject("G4_03: Create product with variation, managed by IMEI and with dimension", true, true, "hasDimension"),
                generateTestObject("G4_04: Create product with variation, managed by IMEI and with SEO", true, true, "hasSEO"),
                generateTestObject("G4_05: Create product with variation, managed by IMEI and with attribution", true, true, "hasAttribution"),
                generateTestObject("G4_06: Create product with variation, managed by IMEI and randomized platforms", true, true, "randomPlatforms"),
                generateTestObject("G4_07: Create product with variation, managed by IMEI and out of stock", true, true, "outOfStock")
        };
    }

    /**
     * Data provider for smoke test data.
     * Provides a subset of tests for a quicker, high-level validation.
     *
     * @return A 2D array of test data objects for smoke tests.
     */
    @DataProvider(name = "smokeTestData")
    Object[][] smokeTestData() {
        List<String> testNames = List.of("G1_01: Create product without variation, managed by Product and no discount",
                "G1_05: Create product without variation, managed by Product and managed by lot",
                "G2_01: Create product without variation, managed by IMEI and no discount",
                "G3_01: Create product with variation, managed by Product and no discount",
                "G3_05: Create product with variation, managed by Product and managed by lot",
                "G4_01: Create product with variation, managed by IMEI and no discount");

        // Get all test cases from the regression data provider and filter based on testNames
        Object[][] allData = regressionTestData();

        return Arrays.stream(allData)
                .filter(data -> testNames.stream().anyMatch(testName -> ((String) data[0]).contains(testName)))
                .toArray(Object[][]::new);
    }

    /**
     * Regression test for creating products with varying attributes.
     * Uses the data from the regressionTestData provider to run multiple product creation tests.
     *
     * @param testName        Descriptive name for the test case.
     * @param isVariation     Indicates if the product has variations.
     * @param isManagedByIMEI Indicates if the inventory is managed by IMEI/Serial number.
     * @param noDiscount      Indicates if no discount is applicable.
     * @param noCostPrice     Indicates if no cost price is applicable.
     * @param hasDimension    Indicates if the product has dimensions.
     * @param hasSEO          Indicates if SEO information should be included.
     * @param managedByLot    Indicates if inventory is managed by lot date.
     * @param hasAttribution  Indicates if attributions should be included.
     * @param randomPlatforms Indicates if platforms should be randomized.
     * @param outOfStock      Indicates if the product is out of stock.
     */
    @Test(dataProvider = "regressionTestData",
            description = "Create product with varying attributes")
    void regressionTest(String testName, boolean isVariation, boolean isManagedByIMEI, boolean noDiscount,
                        boolean noCostPrice, boolean hasDimension, boolean hasSEO, boolean managedByLot,
                        boolean hasAttribution, boolean randomPlatforms, boolean outOfStock) {
        createProductTest(testName, isVariation, isManagedByIMEI, noDiscount,
                noCostPrice, hasDimension, hasSEO, managedByLot,
                hasAttribution, randomPlatforms, outOfStock);
    }

    /**
     * Smoke test for creating products with varying attributes.
     * A subset of the regression tests to verify critical workflows.
     *
     * @param testName        Descriptive name for the test case.
     * @param isVariation     Indicates if the product has variations.
     * @param isManagedByIMEI Indicates if the inventory is managed by IMEI/Serial number.
     * @param noDiscount      Indicates if no discount is applicable.
     * @param noCostPrice     Indicates if no cost price is applicable.
     * @param hasDimension    Indicates if the product has dimensions.
     * @param hasSEO          Indicates if SEO information should be included.
     * @param managedByLot    Indicates if inventory is managed by lot date.
     * @param hasAttribution  Indicates if attributions should be included.
     * @param randomPlatforms Indicates if platforms should be randomized.
     * @param outOfStock      Indicates if the product is out of stock.
     */
    @Test(dataProvider = "smokeTestData",
            description = "Create product with varying attributes")
    void smokeTest(String testName, boolean isVariation, boolean isManagedByIMEI, boolean noDiscount,
                   boolean noCostPrice, boolean hasDimension, boolean hasSEO, boolean managedByLot,
                   boolean hasAttribution, boolean randomPlatforms, boolean outOfStock) {
        createProductTest(testName, isVariation, isManagedByIMEI, noDiscount,
                noCostPrice, hasDimension, hasSEO, managedByLot,
                hasAttribution, randomPlatforms, outOfStock);
    }

    /**
     * Test method to create a product with various configurations.
     * Uses the data provider to test different product attributes and behaviors.
     *
     * @param testName        Descriptive name for the test case.
     * @param isVariation     Indicates if the product has variations.
     * @param isManagedByIMEI Indicates if the inventory is managed by IMEI/Serial number.
     * @param noDiscount      Indicates if no discount is applicable.
     * @param noCostPrice     Indicates if no cost price is applicable.
     * @param hasDimension    Indicates if the product has dimensions.
     * @param hasSEO          Indicates if SEO information should be included.
     * @param managedByLot    Indicates if inventory is managed by lot date.
     * @param hasAttribution  Indicates if attributions should be included.
     * @param randomPlatforms Indicates if platforms should be randomized.
     * @param outOfStock      Indicates if the product is out of stock.
     */
    void createProductTest(String testName, boolean isVariation, boolean isManagedByIMEI, boolean noDiscount,
                           boolean noCostPrice, boolean hasDimension, boolean hasSEO, boolean managedByLot,
                           boolean hasAttribution, boolean randomPlatforms, boolean outOfStock) {

        LogManager.getLogger().info("Running test: {}", testName);

        // Set product attributes based on test data
        productPage.setNoDiscount(noDiscount);
        productPage.setNoCost(noCostPrice);
        productPage.setHasDimension(hasDimension);
        productPage.setHasSEO(hasSEO);
        productPage.setManageByLotDate(managedByLot);
        productPage.setHasAttribution(hasAttribution);

        // Set platforms based on the randomPlatforms flag
        if (randomPlatforms) {
            productPage.setSellingPlatform(false,
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
     * @param isIMEI      Indicates if the inventory is managed by IMEI/Serial number.
     * @param branchStock The stock levels for each branch. Stock values are matched sequentially to branch IDs.
     *                    Branches without provided stock will default to 0.
     */
    private void createProduct(boolean hasModel, boolean isIMEI, int... branchStock) {
        productPage.navigateToCreateProductPage()
                .createProduct(hasModel, isIMEI, branchStock)
                .verifyProductInformation();
    }
}