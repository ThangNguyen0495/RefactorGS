package android.seller;

import api.seller.product.APICreateProduct;
import baseTest.BaseTest;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.android.seller.login.LoginScreen;
import pages.android.seller.products.AndroidBaseProductScreen;
import pages.android.seller.home.HomeScreen;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

/**
 * Test class for updating products on the seller dashboard.
 * This class tests various scenarios for product updates using different configurations of product attributes.
 */
public class AndroidGoSELLERUpdateProductTest extends BaseTest {
    private AndroidBaseProductScreen productScreen;

    /**
     * Sets up the test environment before the class runs.
     * This includes initializing the WebDriver, logging into the dashboard,
     * and creating an instance of the API for product creation.
     */
    @BeforeClass
    void setup() throws IOException, URISyntaxException {
        initDriver("SELLER", "ANDROID");

        new LoginScreen(driver).performLogin(sellerCredentials);

        // Change application language
        new HomeScreen(driver).changeApplicationLanguage();
        productScreen = new AndroidBaseProductScreen(driver).fetchInformation();
    }

    /**
     * Generates a test object array for product update scenarios.
     *
     * @param hasModel  Indicates if the product has variations.
     * @param productId The ID of the product to update.
     * @param setterKey The attribute to set (e.g., "noDiscount").
     * @param testName  A descriptive name for the test case.
     * @return An array of objects representing the test case.
     */
    private Object[] generateTestObject(String testName, boolean hasModel, int productId, String setterKey) {
        boolean[] flags = new boolean[]{
                hasModel,
                setterKey.equals("noDiscount"),
                setterKey.equals("noCost"),
                setterKey.equals("hasDimension"),
                setterKey.equals("managedByLot"),
                setterKey.equals("randomPlatforms"),
                setterKey.equals("inStock"),
        };

        return new Object[]{
                testName, flags[0], productId, flags[1], flags[2], flags[3], flags[4], flags[5], flags[6]
        };
    }

    /**
     * Data provider for product update test cases.
     * This method creates various product scenarios for testing.
     *
     * @return A 2D array of test data for product updates.
     */
    @DataProvider(name = "regressionTestData")
    public Object[][] regressionTestData() {
        APICreateProduct apiCR = new APICreateProduct(sellerCredentials);
        int wpProdId = apiCR.createProductThenGetId(false, false, 0); // without variation, managed by Product
        int wpIMEIProdId = apiCR.createProductThenGetId(true, false, 0); // without variation, managed by IMEI
        int wvProdId = apiCR.createProductThenGetId(false, true, 0); // with variation, managed by Product
        int wvIMEIProdId = apiCR.createProductThenGetId(true, true, 0); // with variation, managed by IMEI

        return new Object[][]{
                // G1: Without variation, managed by Product
                generateTestObject("G1_01: Update product without variation, managed by Product and no discount", false, wpProdId, "noDiscount"),
                generateTestObject("G1_02: Update product without variation, managed by Product and no cost", false, wpProdId, "noCost"),
                generateTestObject("G1_03: Update product without variation, managed by Product and with dimension", false, wpProdId, "hasDimension"),
                generateTestObject("G1_04: Update product without variation, managed by Product and manage by lot", false, wpProdId, "managedByLot"),
                generateTestObject("G1_05: Update product without variation, managed by Product with random platforms", false, wpProdId, "randomPlatforms"),
                generateTestObject("G1_06: Update product without variation, managed by Product and in stock", false, wpProdId, "inStock"),

                // G2: Without variation, managed by IMEI
                generateTestObject("G2_01: Update product without variation, managed by IMEI and no discount", false, wpIMEIProdId, "noDiscount"),
                generateTestObject("G2_02: Update product without variation, managed by IMEI and no cost", false, wpIMEIProdId, "noCost"),
                generateTestObject("G2_03: Update product without variation, managed by IMEI and with dimension", false, wpIMEIProdId, "hasDimension"),
                generateTestObject("G2_04: Update product without variation, managed by IMEI with random platforms", false, wpIMEIProdId, "randomPlatforms"),
                generateTestObject("G2_05: Update product without variation, managed by IMEI and in stock", false, wpIMEIProdId, "inStock"),

                // G3: With variation, managed by Product
                generateTestObject("G3_01: Update product with variation, managed by Product and no discount", true, wvProdId, "noDiscount"),
                generateTestObject("G3_02: Update product with variation, managed by Product and no cost", true, wvProdId, "noCost"),
                generateTestObject("G3_03: Update product with variation, managed by Product and with dimension", true, wvProdId, "hasDimension"),
                generateTestObject("G3_04: Update product with variation, managed by Product and manage by lot", true, wvProdId, "managedByLot"),
                generateTestObject("G3_05: Update product with variation, managed by Product and set random platform", true, wvProdId, "randomPlatforms"),
                generateTestObject("G3_06: Update product with variation, managed by Product and in stock", true, wvProdId, "inStock"),

                // G4: With variation, managed by IMEI
                generateTestObject("G4_01: Update product with variation, managed by IMEI and no discount", true, wvIMEIProdId, "noDiscount"),
                generateTestObject("G4_02: Update product with variation, managed by IMEI and no cost", true, wvIMEIProdId, "noCost"),
                generateTestObject("G4_03: Update product with variation, managed by IMEI and with dimension", true, wvIMEIProdId, "hasDimension"),
                generateTestObject("G4_04: Update product with variation, managed by IMEI and set random platform", true, wvIMEIProdId, "randomPlatforms"),
                generateTestObject("G4_05: Update product with variation, managed by IMEI and in stock", true, wvIMEIProdId, "inStock")
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
        List<String> testNames = List.of("G1_01: Update product without variation, managed by Product and no discount",
                "G1_04: Update product without variation, managed by Product and manage by lot",
                "G2_01: Update product without variation, managed by IMEI and no discount",
                "G3_01: Update product with variation, managed by Product and no discount",
                "G3_04: Update product with variation, managed by Product and manage by lot",
                "G4_01: Update product with variation, managed by IMEI and no discount");

        // Get all test cases from the regression data provider and filter based on testNames
        Object[][] allData = regressionTestData();

        return Arrays.stream(allData)
                .filter(data -> testNames.stream().anyMatch(testName -> ((String) data[0]).contains(testName)))
                .toArray(Object[][]::new);
    }

    @Test(dataProvider = "regressionTestData", description = "Update product with varying attributes")
    void regressionTest(String testName, boolean hasModel, int productId, boolean noDiscount,
                        boolean noCostPrice, boolean hasDimension, boolean managedByLot,
                        boolean randomPlatforms, boolean inStock) {
        updateProductTest(testName, hasModel, productId, noDiscount, noCostPrice,
                hasDimension, managedByLot, randomPlatforms, inStock);
    }

    @Test(dataProvider = "smokeTestData", description = "Update product with varying attributes")
    void smokeTest(String testName, boolean hasModel, int productId, boolean noDiscount,
                   boolean noCostPrice, boolean hasDimension, boolean managedByLot,
                   boolean randomPlatforms, boolean inStock) {
        updateProductTest(testName, hasModel, productId, noDiscount, noCostPrice, hasDimension,
                managedByLot, randomPlatforms, inStock);
    }

    /**
     * Helper method to update products with varying attributes based on the provided test data.
     *
     * @param hasModel        Indicates if the product has variations.
     * @param productId       The ID of the product to update.
     * @param noDiscount      Flag for no discount on the product.
     * @param noCostPrice     Flag for no cost price on the product.
     * @param hasDimension    Flag for having dimensions.
     * @param managedByLot    Flag for managing by lot.
     * @param randomPlatforms Flag for using random platforms.
     * @param inStock         Flag for stock availability.
     * @param testName        A descriptive name for the test case.
     */
    void updateProductTest(String testName, boolean hasModel, int productId, boolean noDiscount, boolean noCostPrice,
                           boolean hasDimension, boolean managedByLot, boolean randomPlatforms,
                           boolean inStock) {

        // Log the test case being executed
        LogManager.getLogger().info("Running test: {}", testName);

        // Navigate to the product detail page
        productScreen.navigateToProductDetailScreen(productId);

        // Set product attributes based on the provided flags
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

        // Generate stock array based on the inStock flag
        int[] stock = new int[RandomUtils.nextInt(10) + 1]; // Random stock size (1 to 10)
        Arrays.fill(stock, inStock ? RandomUtils.nextInt(10) : 0); // Fill with random stock or 0 if out of stock

        // This handles cases where no specific action was required,
        // Updating basic product information instead
        updateProduct(hasModel, stock);
    }

    /**
     * Updates a product
     *
     * @param stock The stock levels for the product.
     */
    private void updateProduct(boolean hasModel, int... stock) {
        productScreen.updateProduct(hasModel, stock)
                .verifyProductInformation();
    }
}