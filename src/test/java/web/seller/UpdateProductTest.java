package web.seller;

import api.seller.product.APICreateProduct;
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
 * Test class for updating products on the seller dashboard.
 * This class tests various scenarios for product updates using different configurations of product attributes.
 */
public class UpdateProductTest extends DashboardBaseTest {
    private BaseProductPage productPage;
    private APICreateProduct apiCreateProduct;

    /**
     * Sets up the test environment before the class runs.
     * This includes initializing the WebDriver, logging into the dashboard,
     * and creating an instance of the API for product creation.
     */
    @BeforeClass
    void setup() {
        driver = new WebDriverManager().getWebDriver();
        productPage = new BaseProductPage(driver).fetchInformation(credentials);
        new DashboardLoginPage(driver).loginDashboardByJs(credentials);
        apiCreateProduct = new APICreateProduct(credentials);
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
                flags[0], productId, flags[1], flags[2], flags[3], flags[4],
                flags[5], flags[6], flags[7], flags[8], flags[9],
                flags[10], flags[11], flags[12], flags[13], testName
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
        int wpProdId = apiCreateProduct.createProduct(false, false, 0); // without variation, managed by Product
        int wpIMEIProdId = apiCreateProduct.createProduct(true, false, 0); // without variation, managed by IMEI
        int wvProdId = apiCreateProduct.createProduct(false, true, 0); // with variation, managed by Product
        int wvIMEIProdId = apiCreateProduct.createProduct(true, true, 0); // with variation, managed by IMEI

        return new Object[][]{
                // G1: Without variation, managed by Product
                generateTestObject(false, wpProdId, "noDiscount", "G1_01: Update product without variation, managed by Product and no discount"),
                generateTestObject(false, wpProdId, "noCost", "G1_02: Update product without variation, managed by Product and no cost"),
                generateTestObject(false, wpProdId, "hasDimension", "G1_03: Update product without variation, managed by Product and with dimension"),
                generateTestObject(false, wpProdId, "hasSEO", "G1_04: Update product without variation, managed by Product and with SEO"),
                generateTestObject(false, wpProdId, "managedByLot", "G1_05: Update product without variation, managed by Product and manage by lot"),
                generateTestObject(false, wpProdId, "hasAttribution", "G1_06: Add attribution to product without variation, managed by Product"),
                generateTestObject(false, wpProdId, "randomPlatforms", "G1_07: Update product without variation, managed by Product with random platforms"),
                generateTestObject(false, wpProdId, "inStock", "G1_08: Update product without variation, managed by Product and in stock"),
                generateTestObject(false, wpProdId, "addWholesalePrice", "G1_09: Add wholesale pricing to product without variation, managed by Product"),
                generateTestObject(false, wpProdId, "addConversionUnit", "G1_10: Add conversion unit to product without variation, managed by Product"),
                generateTestObject(false, wpProdId, "changeStatus", "G1_11: Change product's status without variation, managed by Product"),
                generateTestObject(false, wpProdId, "editTranslation", "G1_12: Add translation to product without variation, managed by Product"),
                generateTestObject(false, wpProdId, "isDelete", "G1_13: Delete product without variation, managed by Product"),

                // G2: Without variation, managed by IMEI
                generateTestObject(false, wpIMEIProdId, "noDiscount", "G2_01: Update product without variation, managed by IMEI and no discount"),
                generateTestObject(false, wpIMEIProdId, "noCost", "G2_02: Update product without variation, managed by IMEI and no cost"),
                generateTestObject(false, wpIMEIProdId, "hasDimension", "G2_03: Update product without variation, managed by IMEI and with dimension"),
                generateTestObject(false, wpIMEIProdId, "hasSEO", "G2_04: Update product without variation, managed by IMEI and with SEO"),
                generateTestObject(false, wpIMEIProdId, "randomPlatforms", "G2_05: Update product without variation, managed by IMEI with random platforms"),
                generateTestObject(false, wpIMEIProdId, "hasAttribution", "G2_06: Add attribution to product without variation, managed by IMEI"),
                generateTestObject(false, wpIMEIProdId, "inStock", "G2_07: Update product without variation, managed by IMEI and in stock"),
                generateTestObject(false, wpIMEIProdId, "addWholesalePrice", "G2_08: Add wholesale pricing to product without variation, managed by IMEI"),
                generateTestObject(false, wpIMEIProdId, "addConversionUnit", "G2_09: Add conversion unit to product without variation, managed by IMEI"),
                generateTestObject(false, wpIMEIProdId, "changeStatus", "G2_10: Change product's status without variation, managed by IMEI"),
                generateTestObject(false, wpIMEIProdId, "editTranslation", "G2_11: Add translation to product without variation, managed by IMEI"),
                generateTestObject(false, wpIMEIProdId, "isDelete", "G2_12: Delete product without variation, managed by IMEI"),

                // G3: With variation, managed by Product
                generateTestObject(true, wvProdId, "noDiscount", "G3_01: Update product with variation, managed by Product and no discount"),
                generateTestObject(true, wvProdId, "noCost", "G3_02: Update product with variation, managed by Product and no cost"),
                generateTestObject(true, wvProdId, "hasDimension", "G3_03: Update product with variation, managed by Product and with dimension"),
                generateTestObject(true, wvProdId, "hasSEO", "G3_04: Update product with variation, managed by Product and with SEO"),
                generateTestObject(true, wvProdId, "managedByLot", "G3_05: Update product with variation, managed by Product and manage by lot"),
                generateTestObject(true, wvProdId, "hasAttribution", "G3_06: Add attribution to product with variation, managed by Product"),
                generateTestObject(true, wvProdId, "randomPlatforms", "G3_07: Update product with variation, managed by Product and set random platform"),
                generateTestObject(true, wvProdId, "inStock", "G3_08: Update product with variation, managed by Product and in stock"),
                generateTestObject(true, wvProdId, "addWholesalePrice", "G3_09: Add wholesale pricing to product with variation, managed by Product"),
                generateTestObject(true, wvProdId, "addConversionUnit", "G3_10: Add conversion unit to product with variation, managed by Product"),
                generateTestObject(true, wvProdId, "changeStatus", "G3_11: Change product's status with variation, managed by Product"),
                generateTestObject(true, wvProdId, "editTranslation", "G3_12: Add translation to product with variation, managed by Product"),
                generateTestObject(true, wvProdId, "isDelete", "G3_13: Delete product with variation, managed by Product"),

                // G4: With variation, managed by IMEI
                generateTestObject(true, wvIMEIProdId, "noDiscount", "G4_01: Update product with variation, managed by IMEI and no discount"),
                generateTestObject(true, wvIMEIProdId, "noCost", "G4_02: Update product with variation, managed by IMEI and no cost"),
                generateTestObject(true, wvIMEIProdId, "hasDimension", "G4_03: Update product with variation, managed by IMEI and with dimension"),
                generateTestObject(true, wvIMEIProdId, "hasSEO", "G4_04: Update product with variation, managed by IMEI and with SEO"),
                generateTestObject(true, wvIMEIProdId, "randomPlatforms", "G4_05: Update product with variation, managed by IMEI and set random platform"),
                generateTestObject(true, wvIMEIProdId, "hasAttribution", "G4_06: Add attribution to product with variation, managed by IMEI"),
                generateTestObject(true, wvIMEIProdId, "inStock", "G4_07: Update product with variation, managed by IMEI and in stock"),
                generateTestObject(true, wvIMEIProdId, "addWholesalePrice", "G4_08: Add wholesale pricing to product with variation, managed by IMEI"),
                generateTestObject(true, wvIMEIProdId, "addConversionUnit", "G4_09: Add conversion unit to product with variation, managed by IMEI"),
                generateTestObject(true, wvIMEIProdId, "changeStatus", "G4_10: Change product's status with variation, managed by IMEI"),
                generateTestObject(true, wvIMEIProdId, "editTranslation", "G4_11: Add translation to product with variation, managed by IMEI"),
                generateTestObject(true, wvIMEIProdId, "isDelete", "G4_12: Delete product with variation, managed by IMEI")
        };
    }

    /**
     * Test method to update products with varying attributes based on the provided test data.
     *
     * @param hasModel          Indicates if the product has variations.
     * @param productId         The ID of the product to update.
     * @param noDiscount        Flag for no discount on the product.
     * @param noCostPrice       Flag for no cost price on the product.
     * @param hasDimension      Flag for having dimensions.
     * @param hasSEO            Flag for having SEO attributes.
     * @param managedByLot      Flag for managing by lot.
     * @param hasAttribution    Flag for having attribution.
     * @param randomPlatforms   Flag for using random platforms.
     * @param inStock           Flag for stock availability.
     * @param addWholesalePrice Flag for adding wholesale pricing.
     * @param addConversionUnit Flag for adding conversion units.
     * @param changeStatus      Flag for changing product status.
     * @param editTranslation   Flag for editing product translation.
     * @param isDelete          Flag for deleting the product.
     * @param testName          A descriptive name for the test case.
     */
    @Test(dataProvider = "productTestData", description = "Update product with varying attributes")
    void updateProductTest(boolean hasModel, int productId, boolean noDiscount,
                           boolean noCostPrice, boolean hasDimension, boolean hasSEO, boolean managedByLot,
                           boolean hasAttribution, boolean randomPlatforms, boolean inStock,
                           boolean addWholesalePrice, boolean addConversionUnit,
                           boolean changeStatus, boolean editTranslation, boolean isDelete, String testName) {

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
            productPage.setSellingPlatform(RandomUtils.nextBoolean(),
                    RandomUtils.nextBoolean(),
                    RandomUtils.nextBoolean(),
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
     * @param stock     The stock levels for the product.
     */
    private void updateProduct(boolean hasModel, int... stock) {
        productPage.updateProduct(hasModel, stock)
                .verifyProductInformation();
    }

    /**
     * Adds variation attribution to a product if it has a model.
     *
     */
    private void addVariationAttribution() {
        productPage.updateProductAttribution();
    }

    /**
     * Edits the translation for a given product.
     *
     */
    private void updateProductTranslation() {
        productPage.updateProductTranslation();
    }

    /**
     * Changes the status of a product.
     *
     */
    private void changeProductStatus() {
        productPage.updateProductStatus();
    }

    /**
     * Adds a conversion unit to a product.
     *
     */
    private void addConversionUnit() {
        productPage.configConversionUnit();
    }

    /**
     * Adds wholesale pricing to a product.
     *
     */
    private void addWholesaleProduct() {
        productPage.configWholesaleProduct();
    }

    /**
     * Deletes a product from the system.
     *
     */
    private void deleteProduct() {
        productPage.deleteProduct();
    }
}