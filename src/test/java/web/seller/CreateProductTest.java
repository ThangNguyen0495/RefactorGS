package web.seller;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.web.seller.login.DashboardLoginPage;
import pages.web.seller.product.all_products.BaseProductPage;
import utility.WebDriverManager;

import java.util.Arrays;

/**
 * Test class for creating products on the seller dashboard.
 * This class tests various scenarios for product creation
 * using different configurations of product attributes.
 */
public class CreateProductTest extends DashboardBaseTest {
    private BaseProductPage productPage;

    /**
     * Sets up the test environment before any tests are run.
     * Initializes the WebDriver and logs into the dashboard.
     */
    @BeforeClass
    void setup() {
        driver = new WebDriverManager().getWebDriver();
        productPage = new BaseProductPage(driver).fetchInformation(credentials);
        new DashboardLoginPage(driver).loginDashboardByJs(credentials);
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
                setterKey.equals("hasDimension"),
                setterKey.equals("hasSEO"),
                setterKey.equals("managedByLot"),
                setterKey.equals("hasAttribution"),
                setterKey.equals("randomPlatforms"),
                setterKey.equals("inStock")
        };

        return new Object[]{
                flags[0], flags[1], flags[2], flags[3], flags[4],
                flags[5], flags[6], flags[7], flags[8], flags[9], testName
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
                generateTestObject(false, false, "noDiscount", "Create product without variation, managed by Product and no discount"),
                generateTestObject(false, false, "noCost", "Create product without variation, managed by Product and no cost"),
                generateTestObject(false, false, "hasDimension", "Create product without variation, managed by Product and with dimension"),
                generateTestObject(false, false, "hasSEO", "Create product without variation, managed by Product and with SEO"),
                generateTestObject(false, false, "managedByLot", "Create product without variation, managed by Product and managed by lot"),
                generateTestObject(false, false, "hasAttribution", "Create product without variation, managed by Product and with attribution"),
                generateTestObject(false, false, "randomPlatforms", "Create product without variation, managed by Product and randomized platforms"),
                generateTestObject(false, false, "inStock", "Create product without variation, managed by Product and out of stock"),

                // G2: Without variation, manage inventory by IMEI/Serial number
                generateTestObject(false, true, "noDiscount", "Create product without variation, managed by IMEI and no discount"),
                generateTestObject(false, true, "noCost", "Create product without variation, managed by IMEI and no cost"),
                generateTestObject(false, true, "hasDimension", "Create product without variation, managed by IMEI and with dimension"),
                generateTestObject(false, true, "hasSEO", "Create product without variation, managed by IMEI and with SEO"),
                generateTestObject(false, true, "hasAttribution", "Create product without variation, managed by IMEI and with attribution"),
                generateTestObject(false, true, "randomPlatforms", "Create product without variation, managed by IMEI and randomized platforms"),
                generateTestObject(false, true, "inStock", "Create product without variation, managed by IMEI and out of stock"),

                // G3: With variation, manage inventory by Product
                generateTestObject(true, false, "noDiscount", "Create product with variation, managed by Product and no discount"),
                generateTestObject(true, false, "noCost", "Create product with variation, managed by Product and no cost"),
                generateTestObject(true, false, "hasDimension", "Create product with variation, managed by Product and with dimension"),
                generateTestObject(true, false, "hasSEO", "Create product with variation, managed by Product and with SEO"),
                generateTestObject(true, false, "managedByLot", "Create product with variation, managed by Product and managed by lot"),
                generateTestObject(true, false, "hasAttribution", "Create product with variation, managed by Product and with attribution"),
                generateTestObject(true, false, "randomPlatforms", "Create product with variation, managed by Product and randomized platforms"),
                generateTestObject(true, false, "inStock", "Create product with variation, managed by Product and out of stock"),

                // G4: With variation, manage inventory by IMEI/Serial number
                generateTestObject(true, true, "noDiscount", "Create product with variation, managed by IMEI and no discount"),
                generateTestObject(true, true, "noCost", "Create product with variation, managed by IMEI and no cost"),
                generateTestObject(true, true, "hasDimension", "Create product with variation, managed by IMEI and with dimension"),
                generateTestObject(true, true, "hasSEO", "Create product with variation, managed by IMEI and with SEO"),
                generateTestObject(true, true, "hasAttribution", "Create product with variation, managed by IMEI and with attribution"),
                generateTestObject(true, true, "randomPlatforms", "Create product with variation, managed by IMEI and randomized platforms"),
                generateTestObject(true, true, "inStock", "Create product with variation, managed by IMEI and out of stock"),
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
     * @param hasSEO         Indicates if SEO information should be included.
     * @param managedByLot   Indicates if inventory is managed by lot date.
     * @param hasAttribution Indicates if attributions should be included.
     * @param randomPlatforms Indicates if platforms should be randomized.
     * @param inStock        Indicates if the product is in stock.
     * @param testName       Descriptive name for the test.
     * @throws Exception If any error occurs during product creation.
     */
    @Test(dataProvider = "productTestData",
            description = "Create product with varying attributes")
    void createProductTest(boolean isVariation, boolean isManagedByIMEI, boolean noDiscount,
                           boolean noCostPrice, boolean hasDimension, boolean hasSEO, boolean managedByLot,
                           boolean hasAttribution, boolean randomPlatforms, boolean inStock, String testName) throws Exception {

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
            productPage.setSellingPlatform(RandomUtils.nextBoolean(),
                    RandomUtils.nextBoolean(),
                    RandomUtils.nextBoolean(),
                    RandomUtils.nextBoolean());
        }

        // Generate stock array based on inStock flag
        int[] stock = new int[RandomUtils.nextInt(10) + 1]; // Random stock size (1 to 10)
        Arrays.fill(stock, inStock ? RandomUtils.nextInt(10) : 0); // Fill with random stock or 0 if out of stock

        // Create product based on whether it has variations or not
        if (isVariation) {
            createVariationProduct(isManagedByIMEI, stock);
        } else {
            createWithoutVariationProduct(isManagedByIMEI, stock);
        }
    }

    /**
     * Helper method to create a product without variations.
     *
     * @param isIMEI Indicates if the inventory is managed by IMEI/Serial number.
     * @param branchStock The stock levels for each branch. Stock values are matched sequentially to branch IDs.
     *                    Branches without provided stock will default to 0.
     */
    private void createWithoutVariationProduct(boolean isIMEI, int... branchStock) {
        productPage.navigateToCreateProductPage()
                .createWithoutVariationProduct(isIMEI, branchStock);
    }

    /**
     * Helper method to create a product with variations.
     *
     * @param isIMEI Indicates if the inventory is managed by IMEI/Serial number.
     * @param branchStock The stock levels for each branch. Stock values are matched sequentially to branch IDs.
     *                    Branches without provided stock will default to 0.
     * @throws Exception If any error occurs during product creation.
     */
    private void createVariationProduct(boolean isIMEI, int... branchStock) throws Exception {
        productPage.navigateToCreateProductPage()
                .createVariationProduct(isIMEI, branchStock);
    }
}