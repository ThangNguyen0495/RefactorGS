package web.seller;

import api.seller.product.APICreateProduct;
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
 * Test class for updating products on the seller dashboard.
 * This class tests various scenarios for product updates using different configurations of product attributes.
 */
public class DashboardUpdateProductTest extends BaseTest {
    private BaseProductPage productPage;
    private APICreateProduct apiCreateProduct;

    /**
     * Sets up the test environment before the class runs.
     * This includes initializing the WebDriver, logging into the dashboard,
     * and creating an instance of the API for product creation.
     */
    @BeforeClass
    void setup() throws IOException, URISyntaxException {
        initDriver("SELLER", "WEB");
        productPage = new BaseProductPage(driver).fetchInformation(sellerCredentials);
        new DashboardLoginPage(driver).loginDashboardByJs(sellerCredentials);
        apiCreateProduct = new APICreateProduct(sellerCredentials);
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
                setterKey.equals("hasSEO"),
                setterKey.equals("managedByLot"),
                setterKey.equals("hasAttribution"),
                setterKey.equals("randomPlatforms"),
                setterKey.equals("inStock"),
                setterKey.equals("addWholesalePrice"),
                setterKey.equals("addConversionUnit"),
                setterKey.equals("changeStatus"),
                setterKey.equals("editTranslation"),
                setterKey.equals("isDelete")
        };

        return new Object[]{
                testName, flags[0], productId, flags[1], flags[2], flags[3],
                flags[4], flags[5], flags[6], flags[7], flags[8], flags[9],
                flags[10], flags[11], flags[12], flags[13]
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
        int wpProdId = apiCreateProduct.createProductThenGetId(false, false, 0); // without variation, managed by Product
        int wpIMEIProdId = apiCreateProduct.createProductThenGetId(true, false, 0); // without variation, managed by IMEI
        int wvProdId = apiCreateProduct.createProductThenGetId(false, true, 0); // with variation, managed by Product
        int wvIMEIProdId = apiCreateProduct.createProductThenGetId(true, true, 0); // with variation, managed by IMEI

        return new Object[][]{
                // Group 1: Without variation, managed by Product
                generateTestObject("G1_01: Update product without variation, managed by Product and no discount", false, wpProdId, "noDiscount"),
                generateTestObject("G1_02: Update product without variation, managed by Product and no cost", false, wpProdId, "noCost"),
                generateTestObject("G1_03: Update product without variation, managed by Product and with dimension", false, wpProdId, "hasDimension"),
                generateTestObject("G1_04: Update product without variation, managed by Product and with SEO", false, wpProdId, "hasSEO"),
                generateTestObject("G1_05: Update product without variation, managed by Product and manage by lot", false, wpProdId, "managedByLot"),
                generateTestObject("G1_06: Add attribution to product without variation, managed by Product", false, wpProdId, "hasAttribution"),
                generateTestObject("G1_07: Update product without variation, managed by Product with random platforms", false, wpProdId, "randomPlatforms"),
                generateTestObject("G1_08: Update product without variation, managed by Product and in stock", false, wpProdId, "inStock"),
                generateTestObject("G1_09: Add wholesale pricing to product without variation, managed by Product", false, wpProdId, "addWholesalePrice"),
                generateTestObject("G1_10: Add conversion unit to product without variation, managed by Product", false, wpProdId, "addConversionUnit"),
                generateTestObject("G1_11: Change product's status without variation, managed by Product", false, wpProdId, "changeStatus"),
                generateTestObject("G1_12: Add translation to product without variation, managed by Product", false, wpProdId, "editTranslation"),
                generateTestObject("G1_13: Delete product without variation, managed by Product", false, wpProdId, "isDelete"),

                // Group 2: Without variation, managed by IMEI
                generateTestObject("G2_01: Update product without variation, managed by IMEI and no discount", false, wpIMEIProdId, "noDiscount"),
                generateTestObject("G2_02: Update product without variation, managed by IMEI and no cost", false, wpIMEIProdId, "noCost"),
                generateTestObject("G2_03: Update product without variation, managed by IMEI and with dimension", false, wpIMEIProdId, "hasDimension"),
                generateTestObject("G2_04: Update product without variation, managed by IMEI and with SEO", false, wpIMEIProdId, "hasSEO"),
                generateTestObject("G2_05: Update product without variation, managed by IMEI with random platforms", false, wpIMEIProdId, "randomPlatforms"),
                generateTestObject("G2_06: Add attribution to product without variation, managed by IMEI", false, wpIMEIProdId, "hasAttribution"),
                generateTestObject("G2_07: Update product without variation, managed by IMEI and in stock", false, wpIMEIProdId, "inStock"),
                generateTestObject("G2_08: Add wholesale pricing to product without variation, managed by IMEI", false, wpIMEIProdId, "addWholesalePrice"),
                generateTestObject("G2_09: Add conversion unit to product without variation, managed by IMEI", false, wpIMEIProdId, "addConversionUnit"),
                generateTestObject("G2_10: Change product's status without variation, managed by IMEI", false, wpIMEIProdId, "changeStatus"),
                generateTestObject("G2_11: Add translation to product without variation, managed by IMEI", false, wpIMEIProdId, "editTranslation"),
                generateTestObject("G2_12: Delete product without variation, managed by IMEI", false, wpIMEIProdId, "isDelete"),

                // Group 3: With variation, managed by Product
                generateTestObject("G3_01: Update product with variation, managed by Product and no discount", true, wvProdId, "noDiscount"),
                generateTestObject("G3_02: Update product with variation, managed by Product and no cost", true, wvProdId, "noCost"),
                generateTestObject("G3_03: Update product with variation, managed by Product and with dimension", true, wvProdId, "hasDimension"),
                generateTestObject("G3_04: Update product with variation, managed by Product and with SEO", true, wvProdId, "hasSEO"),
                generateTestObject("G3_05: Update product with variation, managed by Product and manage by lot", true, wvProdId, "managedByLot"),
                generateTestObject("G3_06: Add attribution to product with variation, managed by Product", true, wvProdId, "hasAttribution"),
                generateTestObject("G3_07: Update product with variation, managed by Product and set random platform", true, wvProdId, "randomPlatforms"),
                generateTestObject("G3_08: Update product with variation, managed by Product and in stock", true, wvProdId, "inStock"),
                generateTestObject("G3_09: Add wholesale pricing to product with variation, managed by Product", true, wvProdId, "addWholesalePrice"),
                generateTestObject("G3_10: Add conversion unit to product with variation, managed by Product", true, wvProdId, "addConversionUnit"),
                generateTestObject("G3_11: Change product's status with variation, managed by Product", true, wvProdId, "changeStatus"),
                generateTestObject("G3_12: Add translation to product with variation, managed by Product", true, wvProdId, "editTranslation"),
                generateTestObject("G3_13: Delete product with variation, managed by Product", true, wvProdId, "isDelete"),

                // Group 4: With variation, managed by IMEI
                generateTestObject("G4_01: Update product with variation, managed by IMEI and no discount", true, wvIMEIProdId, "noDiscount"),
                generateTestObject("G4_02: Update product with variation, managed by IMEI and no cost", true, wvIMEIProdId, "noCost"),
                generateTestObject("G4_03: Update product with variation, managed by IMEI and with dimension", true, wvIMEIProdId, "hasDimension"),
                generateTestObject("G4_04: Update product with variation, managed by IMEI and with SEO", true, wvIMEIProdId, "hasSEO"),
                generateTestObject("G4_05: Update product with variation, managed by IMEI and set random platform", true, wvIMEIProdId, "randomPlatforms"),
                generateTestObject("G4_06: Add attribution to product with variation, managed by IMEI", true, wvIMEIProdId, "hasAttribution"),
                generateTestObject("G4_07: Update product with variation, managed by IMEI and in stock", true, wvIMEIProdId, "inStock"),
                generateTestObject("G4_08: Add wholesale pricing to product with variation, managed by IMEI", true, wvIMEIProdId, "addWholesalePrice"),
                generateTestObject("G4_09: Add conversion unit to product with variation, managed by IMEI", true, wvIMEIProdId, "addConversionUnit"),
                generateTestObject("G4_10: Change product's status with variation, managed by IMEI", true, wvIMEIProdId, "changeStatus"),
                generateTestObject("G4_11: Add translation to product with variation, managed by IMEI", true, wvIMEIProdId, "editTranslation"),
                generateTestObject("G4_12: Delete product with variation, managed by IMEI", true, wvIMEIProdId, "isDelete")};
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
                "G1_05: Update product without variation, managed by Product and manage by lot",
                "G1_09: Add wholesale pricing to product without variation, managed by Product",
                "G2_01: Update product without variation, managed by IMEI and no discount",
                "G2_08: Add wholesale pricing to product without variation, managed by IMEI",
                "G3_01: Update product with variation, managed by Product and no discount",
                "G3_05: Update product with variation, managed by Product and manage by lot",
                "G3_09: Add wholesale pricing to product with variation, managed by Product",
                "G4_01: Update product with variation, managed by IMEI and no discount",
                "G4_08: Add wholesale pricing to product with variation, managed by IMEI");

        // Get all test cases from the regression data provider and filter based on testNames
        Object[][] allData = regressionTestData();

        return Arrays.stream(allData)
                .filter(data -> testNames.stream().anyMatch(testName -> ((String) data[0]).contains(testName)))
                .toArray(Object[][]::new);
    }

    /**
     * Regression test to validate the functionality of updating a product with various attributes.
     * This test ensures that existing features and workflows perform as expected under different scenarios.
     * It uses data-driven testing with the specified data provider.
     *
     * @param testName          A descriptive name for the test case being executed.
     * @param hasModel          Indicates whether the product has variations or models.
     * @param productId         The unique identifier of the product to be updated.
     * @param noDiscount        Specifies whether the product should have no discount applied.
     * @param noCostPrice       Specifies whether the product should have no cost price set.
     * @param hasDimension      Indicates whether the product includes dimensional attributes.
     * @param hasSEO            Indicates whether SEO attributes are configured for the product.
     * @param managedByLot      Specifies whether the product is managed by lot.
     * @param hasAttribution    Specifies whether the product includes attribution details.
     * @param randomPlatforms   Specifies whether random selling platforms should be assigned.
     * @param inStock           Indicates whether the product should be marked as in stock.
     * @param addWholesalePrice Specifies whether wholesale pricing should be added to the product.
     * @param addConversionUnit Specifies whether a conversion unit should be added to the product.
     * @param changeStatus      Specifies whether the status of the product should be changed.
     * @param editTranslation   Specifies whether translations for the product should be edited.
     * @param isDelete          Specifies whether the product should be deleted.
     */
    @Test(dataProvider = "regressionTestData", description = "Update product with varying attributes")
    void regressionTest(String testName, boolean hasModel, int productId, boolean noDiscount,
                        boolean noCostPrice, boolean hasDimension, boolean hasSEO, boolean managedByLot,
                        boolean hasAttribution, boolean randomPlatforms, boolean inStock,
                        boolean addWholesalePrice, boolean addConversionUnit,
                        boolean changeStatus, boolean editTranslation, boolean isDelete) {
        updateProductTest( testName, hasModel,  productId,  noDiscount,
         noCostPrice,  hasDimension,  hasSEO,  managedByLot,
         hasAttribution,  randomPlatforms,  inStock,
         addWholesalePrice,  addConversionUnit,
         changeStatus,  editTranslation,  isDelete);
    }

    /**
     * Smoke test to verify the functionality of updating a product with various attributes.
     * This test uses data-driven testing with the specified data provider.
     *
     * @param testName          A descriptive name for the test case being executed.
     * @param hasModel          Indicates whether the product has variations or models.
     * @param productId         The unique identifier of the product to be updated.
     * @param noDiscount        Specifies whether the product should have no discount applied.
     * @param noCostPrice       Specifies whether the product should have no cost price set.
     * @param hasDimension      Indicates whether the product includes dimensional attributes.
     * @param hasSEO            Indicates whether SEO attributes are configured for the product.
     * @param managedByLot      Specifies whether the product is managed by lot.
     * @param hasAttribution    Specifies whether the product includes attribution details.
     * @param randomPlatforms   Specifies whether random selling platforms should be assigned.
     * @param inStock           Indicates whether the product should be marked as in stock.
     * @param addWholesalePrice Specifies whether wholesale pricing should be added to the product.
     * @param addConversionUnit Specifies whether a conversion unit should be added to the product.
     * @param changeStatus      Specifies whether the status of the product should be changed.
     * @param editTranslation   Specifies whether translations for the product should be edited.
     * @param isDelete          Specifies whether the product should be deleted.
     */
    @Test(dataProvider = "smokeTestData", description = "Update product with varying attributes")
    void smokeTest(String testName, boolean hasModel, int productId, boolean noDiscount,
                        boolean noCostPrice, boolean hasDimension, boolean hasSEO, boolean managedByLot,
                        boolean hasAttribution, boolean randomPlatforms, boolean inStock,
                        boolean addWholesalePrice, boolean addConversionUnit,
                        boolean changeStatus, boolean editTranslation, boolean isDelete) {
        updateProductTest( testName, hasModel,  productId,  noDiscount,
                noCostPrice,  hasDimension,  hasSEO,  managedByLot,
                hasAttribution,  randomPlatforms,  inStock,
                addWholesalePrice,  addConversionUnit,
                changeStatus,  editTranslation,  isDelete);
    }

    /**
     * Helper method to update a product with various attributes based on the provided test data.
     *
     * @param testName          A descriptive name for the test case being executed.
     * @param hasModel          Indicates whether the product has variations or models.
     * @param productId         The unique identifier of the product to update.
     * @param noDiscount        Specifies whether the product should have no discount applied.
     * @param noCostPrice       Specifies whether the product should have no cost price set.
     * @param hasDimension      Indicates whether the product includes dimensional attributes.
     * @param hasSEO            Indicates whether SEO attributes are configured for the product.
     * @param managedByLot      Specifies whether the product is managed by lot.
     * @param hasAttribution    Specifies whether the product includes attribution details.
     * @param randomPlatforms   Specifies whether random selling platforms should be assigned.
     * @param inStock           Indicates whether the product should be marked as in stock.
     * @param addWholesalePrice Specifies whether wholesale pricing should be added to the product.
     * @param addConversionUnit Specifies whether a conversion unit should be added to the product.
     * @param changeStatus      Specifies whether the status of the product should be changed.
     * @param editTranslation   Specifies whether translations for the product should be edited.
     * @param isDelete          Specifies whether the product should be deleted.
     */
    void updateProductTest(String testName, boolean hasModel, int productId, boolean noDiscount,
                           boolean noCostPrice, boolean hasDimension, boolean hasSEO, boolean managedByLot,
                           boolean hasAttribution, boolean randomPlatforms, boolean inStock,
                           boolean addWholesalePrice, boolean addConversionUnit,
                           boolean changeStatus, boolean editTranslation, boolean isDelete) {

        // Log the test case being executed
        LogManager.getLogger().info("Running test: {}", testName);

        // Navigate to the product detail page
        productPage.navigateToUpdateProductPage(productId);

        // Perform the appropriate action based on the flags
        // Execute the corresponding action for the first true flag found
        // Add wholesale pricing to the product
        if (addWholesalePrice) {
            addWholesaleProduct();
            return;
        }

        // Add attribution details for the product and variations (if any)
        if (hasAttribution) {
            addVariationAttribution();
            return;
        }

        // Add a conversion unit for the product
        if (addConversionUnit) {
            addConversionUnit();
            return;
        }

        // Change the status of the product and variations (if any)
        if (changeStatus) {
            changeProductStatus();
            return;
        }

        // Add translation to the product and variations (if any)
        if (editTranslation) {
            updateProductTranslation();
            return;
        }

        // Delete the specified product from the system
        if (isDelete) {
            deleteProduct();
            return;
        }

        // Set product attributes based on the provided flags
        productPage.setNoDiscount(noDiscount);
        productPage.setNoCost(noCostPrice);
        productPage.setHasDimension(hasDimension);
        productPage.setHasSEO(hasSEO);
        productPage.setManageByLotDate(managedByLot);

        // Set platforms based on the randomPlatforms flag
        if (randomPlatforms) {
            productPage.setSellingPlatform(false,
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
        productPage.updateProduct(hasModel, stock)
                .verifyProductInformation();
    }

    /**
     * Adds variation attribution to a product if it has a model.
     */
    private void addVariationAttribution() {
        productPage.updateProductAttribution();
    }

    /**
     * Edits the translation for a given product.
     */
    private void updateProductTranslation() {
        productPage.updateProductTranslation();
    }

    /**
     * Changes the status of a product.
     */
    private void changeProductStatus() {
        productPage.updateProductStatus();
    }

    /**
     * Adds a conversion unit to a product.
     */
    private void addConversionUnit() {
        productPage.configConversionUnit();
    }

    /**
     * Adds wholesale pricing to a product.
     */
    private void addWholesaleProduct() {
        productPage.configWholesaleProduct();
    }

    /**
     * Deletes a product from the system.
     */
    private void deleteProduct() {
        productPage.deleteProduct();
    }
}