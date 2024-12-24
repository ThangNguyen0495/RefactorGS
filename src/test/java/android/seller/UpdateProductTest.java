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

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

/**
 * Test class for updating products on the seller dashboard.
 * This class tests various scenarios for product updates using different configurations of product attributes.
 */
public class UpdateProductTest extends BaseTest {
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
    private Object[] generateTestObject(boolean hasModel, int productId, String setterKey, String testName) {
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
                flags[0], productId, flags[1], flags[2], flags[3], flags[4], flags[5], flags[6], testName
        };
    }

    /**
     * Data provider for product update test cases.
     * This method creates various product scenarios for testing.
     *
     * @return A 2D array of test data for product updates.
     */
    @DataProvider(name = "productTestData")
    public Object[][] productTestData() {
        APICreateProduct apiCR = new APICreateProduct(sellerCredentials);
        int wpProdId = apiCR.createProductThenGetId(false, false, 0); // without variation, managed by Product
        int wpIMEIProdId = apiCR.createProductThenGetId(true, false, 0); // without variation, managed by IMEI
        int wvProdId = apiCR.createProductThenGetId(false, true, 0); // with variation, managed by Product
        int wvIMEIProdId = apiCR.createProductThenGetId(true, true, 0); // with variation, managed by IMEI

        return new Object[][]{
                // G1: Without variation, managed by Product
                generateTestObject(false, wpProdId, "noDiscount", "G1_01: Update product without variation, managed by Product and no discount"),
                generateTestObject(false, wpProdId, "noCost", "G1_02: Update product without variation, managed by Product and no cost"),
                generateTestObject(false, wpProdId, "hasDimension", "G1_03: Update product without variation, managed by Product and with dimension"),
                generateTestObject(false, wpProdId, "managedByLot", "G1_04: Update product without variation, managed by Product and manage by lot"),
                generateTestObject(false, wpProdId, "randomPlatforms", "G1_05: Update product without variation, managed by Product with random platforms"),
                generateTestObject(false, wpProdId, "inStock", "G1_06: Update product without variation, managed by Product and in stock"),

                // G2: Without variation, managed by IMEI
                generateTestObject(false, wpIMEIProdId, "noDiscount", "G2_01: Update product without variation, managed by IMEI and no discount"),
                generateTestObject(false, wpIMEIProdId, "noCost", "G2_02: Update product without variation, managed by IMEI and no cost"),
                generateTestObject(false, wpIMEIProdId, "hasDimension", "G2_03: Update product without variation, managed by IMEI and with dimension"),
                generateTestObject(false, wpIMEIProdId, "randomPlatforms", "G2_04: Update product without variation, managed by IMEI with random platforms"),
                generateTestObject(false, wpIMEIProdId, "inStock", "G2_05: Update product without variation, managed by IMEI and in stock"),

                // G3: With variation, managed by Product
                generateTestObject(true, wvProdId, "noDiscount", "G3_01: Update product with variation, managed by Product and no discount"),
                generateTestObject(true, wvProdId, "noCost", "G3_02: Update product with variation, managed by Product and no cost"),
                generateTestObject(true, wvProdId, "hasDimension", "G3_03: Update product with variation, managed by Product and with dimension"),
                generateTestObject(true, wvProdId, "managedByLot", "G3_04: Update product with variation, managed by Product and manage by lot"),
                generateTestObject(true, wvProdId, "randomPlatforms", "G3_05: Update product with variation, managed by Product and set random platform"),
                generateTestObject(true, wvProdId, "inStock", "G3_06: Update product with variation, managed by Product and in stock"),

                // G4: With variation, managed by IMEI
                generateTestObject(true, wvIMEIProdId, "noDiscount", "G4_01: Update product with variation, managed by IMEI and no discount"),
                generateTestObject(true, wvIMEIProdId, "noCost", "G4_02: Update product with variation, managed by IMEI and no cost"),
                generateTestObject(true, wvIMEIProdId, "hasDimension", "G4_03: Update product with variation, managed by IMEI and with dimension"),
                generateTestObject(true, wvIMEIProdId, "randomPlatforms", "G4_04: Update product with variation, managed by IMEI and set random platform"),
                generateTestObject(true, wvIMEIProdId, "inStock", "G4_05: Update product with variation, managed by IMEI and in stock"),
        };
    }

    /**
     * Test method to update products with varying attributes based on the provided test data.
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
    @Test(dataProvider = "productTestData", description = "Update product with varying attributes")
    void updateProductTest(boolean hasModel, int productId, boolean noDiscount, boolean noCostPrice,
                           boolean hasDimension, boolean managedByLot, boolean randomPlatforms,
                           boolean inStock, String testName) {

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