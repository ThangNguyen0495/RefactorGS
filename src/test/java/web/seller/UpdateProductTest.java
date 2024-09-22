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
     * @param hasModel         Indicates if the product has variations.
     * @param isManagedByIMEI  Indicates if the inventory is managed by IMEI.
     * @param productId        The ID of the product to update.
     * @param setterKey        The attribute to set (e.g., "noDiscount").
     * @param testName         A descriptive name for the test case.
     * @return An array of objects representing the test case.
     */
    private Object[] generateTestObject(boolean hasModel, boolean isManagedByIMEI, int productId, String setterKey, String testName) {
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
                setterKey.equals("inStock"),
                setterKey.equals("addWholesalePrice"),
                setterKey.equals("addConversionUnit"),
                setterKey.equals("changeStatus"),
                setterKey.equals("editTranslation"),
                setterKey.equals("isDelete")
        };

        return new Object[]{
                flags[0], flags[1], productId, flags[2], flags[3], flags[4],
                flags[5], flags[6], flags[7], flags[8], flags[9],
                flags[10], flags[11], flags[12], flags[13], flags[14], testName
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
                generateTestObject(false, false, wpProdId, "noDiscount", "Update normal product without variation and no discount"),
                generateTestObject(false, false, wpProdId, "noCost", "Update normal product without variation and no cost"),
                generateTestObject(false, false, wpProdId, "hasDimension", "Update normal product without variation and with dimension"),
                generateTestObject(false, false, wpProdId, "hasSEO", "Update normal product without variation and with SEO"),
                generateTestObject(false, false, wpProdId, "managedByLot", "Update normal product without variation and manage by lot"),
                generateTestObject(false, false, wpProdId, "hasAttribution", "Update normal product without variation and with attribution"),
                generateTestObject(false, false, wpProdId, "randomPlatforms", "Update normal product without variation with random platforms"),
                generateTestObject(false, false, wpProdId, "inStock", "Update normal product without variation and set out of stock"),
                generateTestObject(false, false, wpProdId, "addWholesalePrice", "Update normal product without variation and add wholesale pricing"),
                generateTestObject(false, false, wpProdId, "addConversionUnit", "Update normal product without variation and add conversion unit"),
                generateTestObject(false, false, wpProdId, "changeStatus", "Update normal product without variation and change status"),
                generateTestObject(false, false, wpProdId, "editTranslation", "Update normal product without variation and add translation"),
                generateTestObject(false, false, wpProdId, "isDelete", "Delete normal product without variation"),

                // G2: Without variation, managed by IMEI
                generateTestObject(false, true, wpIMEIProdId, "noDiscount", "Update normal product without variation, managed by IMEI and no discount"),
                generateTestObject(false, true, wpIMEIProdId, "noCost", "Update normal product without variation, managed by IMEI and no cost"),
                generateTestObject(false, true, wpIMEIProdId, "hasDimension", "Update normal product without variation, managed by IMEI and with dimension"),
                generateTestObject(false, true, wpIMEIProdId, "hasSEO", "Update normal product without variation, managed by IMEI and with SEO"),
                generateTestObject(false, true, wpIMEIProdId, "randomPlatforms", "Update normal product without variation, managed by IMEI with random platforms"),
                generateTestObject(false, true, wpIMEIProdId, "inStock", "Update normal product without variation, managed by IMEI and set out of stock"),
                generateTestObject(false, true, wpIMEIProdId, "addWholesalePrice", "Update normal product without variation, managed by IMEI and add wholesale pricing"),
                generateTestObject(false, true, wpIMEIProdId, "addConversionUnit", "Update normal product without variation, managed by IMEI and add conversion unit"),
                generateTestObject(false, true, wpIMEIProdId, "changeStatus", "Update normal product without variation, managed by IMEI and change status"),
                generateTestObject(false, true, wpIMEIProdId, "editTranslation", "Update normal product without variation, managed by IMEI and add translation"),
                generateTestObject(false, true, wpIMEIProdId, "isDelete", "Delete normal product without variation, managed by IMEI"),

                // G3: With variation, managed by Product
                generateTestObject(true, false, wvProdId, "noDiscount", "Update product with variation and no discount"),
                generateTestObject(true, false, wvProdId, "noCost", "Update product with variation and no cost"),
                generateTestObject(true, false, wvProdId, "hasDimension", "Update product with variation and with dimension"),
                generateTestObject(true, false, wvProdId, "hasSEO", "Update product with variation and with SEO"),
                generateTestObject(true, false, wvProdId, "managedByLot", "Update product with variation and manage by lot"),
                generateTestObject(true, false, wvProdId, "hasAttribution", "Update product with variation and with attribution"),
                generateTestObject(true, false, wvProdId, "randomPlatforms", "Update product with variation and set random platform"),
                generateTestObject(true, false, wvProdId, "inStock", "Update product with variation and set out of stock"),
                generateTestObject(true, false, wvProdId, "addWholesalePrice", "Update product with variation and add wholesale pricing"),
                generateTestObject(true, false, wvProdId, "addConversionUnit", "Update product with variation and add conversion unit"),
                generateTestObject(true, false, wvProdId, "changeStatus", "Update product with variation and change status"),
                generateTestObject(true, false, wvProdId, "editTranslation", "Update product with variation and add translation"),
                generateTestObject(true, false, wvProdId, "isDelete", "Delete product with variation"),

                // G4: With variation, managed by IMEI
                generateTestObject(true, true, wvIMEIProdId, "noDiscount", "Update product with variation, managed by IMEI and no discount"),
                generateTestObject(true, true, wvIMEIProdId, "noCost", "Update product with variation, managed by IMEI and no cost"),
                generateTestObject(true, true, wvIMEIProdId, "hasDimension", "Update product with variation, managed by IMEI and with dimension"),
                generateTestObject(true, true, wvIMEIProdId, "hasSEO", "Update product with variation, managed by IMEI and with SEO"),
                generateTestObject(true, true, wvIMEIProdId, "randomPlatforms", "Update product with variation, managed by IMEI and set random platform"),
                generateTestObject(true, true, wvIMEIProdId, "inStock", "Update product with variation, managed by IMEI and set out of stock"),
                generateTestObject(true, true, wvIMEIProdId, "addWholesalePrice", "Update product with variation, managed by IMEI and add wholesale pricing"),
                generateTestObject(true, true, wvIMEIProdId, "addConversionUnit", "Update product with variation, managed by IMEI and add conversion unit"),
                generateTestObject(true, true, wvIMEIProdId, "changeStatus", "Update product with variation, managed by IMEI and change status"),
                generateTestObject(true, true, wvIMEIProdId, "editTranslation", "Update product with variation, managed by IMEI and add translation"),
                generateTestObject(true, true, wvIMEIProdId, "isDelete", "Delete product with variation, managed by IMEI")
        };
    }

    /**
     * Test method to update products with varying attributes based on the provided test data.
     *
     * @param isVariation       Indicates if the product has variations.
     * @param isManagedByIMEI   Indicates if the inventory is managed by IMEI.
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
     * @throws Exception if an error occurs during the test.
     */
    @Test(dataProvider = "productTestData", description = "Update product with varying attributes")
    void updateProductTest(boolean isVariation, boolean isManagedByIMEI, int productId, boolean noDiscount,
                           boolean noCostPrice, boolean hasDimension, boolean hasSEO, boolean managedByLot,
                           boolean hasAttribution, boolean randomPlatforms, boolean inStock,
                           boolean addWholesalePrice, boolean addConversionUnit,
                           boolean changeStatus, boolean editTranslation, boolean isDelete, String testName) throws Exception {

        // Log the test case being executed
        LogManager.getLogger().info("Running test: {}", testName);

        // Set product attributes based on the provided flags
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

        // Generate stock array based on the inStock flag
        int[] stock = new int[RandomUtils.nextInt(10) + 1]; // Random stock size (1 to 10)
        Arrays.fill(stock, inStock ? RandomUtils.nextInt(10) : 0); // Fill with random stock or 0 if out of stock

        // Perform the appropriate action based on the flags
        if (addWholesalePrice) {
            addWholesaleProduct(productId);
        } else if (addConversionUnit) {
            addConversionUnit(productId);
        } else if (changeStatus) {
            changeProductStatus(productId, isVariation);
        } else if (editTranslation) {
            editTranslation(productId, isVariation);
        } else if (isDelete) {
            deleteProduct(productId);
        } else if (isVariation) {
            updateVariationProduct(productId, stock);
        } else {
            updateWithoutVariationProduct(productId, stock);
        }
    }

    /**
     * Updates a product without variations.
     *
     * @param productId The ID of the product to update.
     * @param stock     The stock levels for the product.
     */
    private void updateWithoutVariationProduct(int productId, int... stock) {
        productPage.navigateToUpdateProductPage(productId)
                .updateWithoutVariationProduct(stock);
    }

    /**
     * Updates a product with variations.
     *
     * @param productId The ID of the product to update.
     * @param stock     The stock levels for the product.
     * @throws Exception If any error occurs during product update.
     */
    private void updateVariationProduct(int productId, int... stock) throws Exception {
        productPage.navigateToUpdateProductPage(productId)
                .updateVariationProduct(stock);
    }

    /**
     * Edits the translation for a given product.
     *
     * @param productId The ID of the product to edit.
     * @param hasModel  Indicates if the product has variations.
     */
    void editTranslation(int productId, boolean hasModel) {
        productPage.navigateToUpdateProductPage(productId)
                .editTranslation();

        // If the product has variations, edit the translation for variations
        if (hasModel) {
            productPage.editVariationTranslation(productId);
        }
    }

    /**
     * Changes the status of a product.
     *
     * @param productId The ID of the product to change status.
     * @param hasModel  Indicates if the product has variations.
     */
    void changeProductStatus(int productId, boolean hasModel) {
        productPage.navigateToUpdateProductPage(productId)
                .changeProductStatus("INACTIVE", productId);

        // If the product has variations, change the status for variations
        if (hasModel) {
            productPage.changeVariationStatus(productId);
        }
    }

    /**
     * Adds a conversion unit to a product.
     *
     * @param productId The ID of the product to add conversion unit to.
     */
    void addConversionUnit(int productId) {
        productPage.navigateToUpdateProductPage(productId)
                .configConversionUnit();
    }

    /**
     * Adds wholesale pricing to a product.
     *
     * @param productId The ID of the product to add wholesale pricing to.
     */
    void addWholesaleProduct(int productId) {
        productPage.navigateToUpdateProductPage(productId)
                .configWholesaleProduct();
    }

    /**
     * Deletes a product from the system.
     *
     * @param productId The ID of the product to delete.
     */
    void deleteProduct(int productId) {
        productPage.navigateToUpdateProductPage(productId)
                .deleteProduct();
    }
}