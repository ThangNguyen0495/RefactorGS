package android.buyer;

import api.buyer.login.APIBuyerLogin;
import api.seller.product.APIAddWholesaleProduct;
import api.seller.product.APICreateProduct;
import api.seller.product.APIGetProductDetail;
import api.seller.promotion.APICreateFlashSale;
import api.seller.promotion.APICreateProductDiscountCampaign;
import api.seller.setting.APIUpdateBranchInformation;
import baseTest.BaseTest;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.testng.annotations.*;
import pages.android.buyer.login.AndroidBuyerLoginScreen;
import pages.android.buyer.product.AndroidBuyerProductDetailScreen;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

/**
 * This class contains test cases for verifying product details on the storefront,
 * including various scenarios for products managed by Product or IMEI.
 */
public class AndroidGoBUYERProductDetailTest extends BaseTest {
    private int customerId;
    private APICreateProduct apiCreateProduct;
    private APICreateFlashSale apiCreateFlashSale;
    private APICreateProductDiscountCampaign apiCreateDiscountCampaign;
    private APIAddWholesaleProduct apiAddWholesaleProduct;
    private APIUpdateBranchInformation apiUpdateBranch;

    /**
     * Sets up the necessary APIs and logs into the storefront before running tests.
     */
    @BeforeMethod
    void setup() throws IOException, URISyntaxException {
        initDriver("BUYER", "ANDROID");
        new AndroidBuyerLoginScreen(driver).performLogin(buyerCredentials);
        this.customerId = new APIBuyerLogin().getBuyerInformation(buyerCredentials).getId();
        this.apiCreateProduct = new APICreateProduct(sellerCredentials);
        this.apiCreateFlashSale = new APICreateFlashSale(sellerCredentials);
        this.apiCreateDiscountCampaign = new APICreateProductDiscountCampaign(sellerCredentials);
        this.apiAddWholesaleProduct = new APIAddWholesaleProduct(sellerCredentials);
        this.apiUpdateBranch = new APIUpdateBranchInformation(sellerCredentials);
    }

    @AfterMethod
    void tearDown() {
        if (driver != null)   driver.quit();
    }

    /**
     * Generates a test data object containing flags and a test name.
     *
     * @param hasModel        Whether the product has model variations.
     * @param isManagedByIMEI Whether the inventory is managed by IMEI/Serial number.
     * @param setterKey1      First key to set product attributes.
     * @param setterKey2      Second key to set product attributes.
     * @param testName        Descriptive name for the test.
     * @return An array representing the product configuration flags and test name.
     */
    private Object[] generateTestObject(String testName, boolean hasModel, boolean isManagedByIMEI, String setterKey1, String setterKey2) {
        List<String> setterList = List.of(setterKey1, setterKey2);
        boolean[] flags = new boolean[]{
                hasModel,
                isManagedByIMEI,
                setterList.contains("hasFlashSale"),
                setterList.contains("hasDiscountCampaign"),
                setterList.contains("hasWholesalePricing"),
                setterList.contains("hideRemaining"),
                setterList.contains("notDisplayOutOfStock"),
                setterList.contains("inStock"),
                setterList.contains("hideFreeBranch"),
                setterList.contains("hidePaidBranch"),
                setterList.contains("deactivatePaidBranch"),
        };

        return new Object[]{
                testName, flags[0], flags[1], flags[2], flags[3], flags[4],
                flags[5], flags[6], flags[7], flags[8], flags[9], flags[10]
        };
    }

    /**
     * Data provider for product test scenarios.
     *
     * @return A 2D array of test data for product detail tests.
     */
    @DataProvider(name = "regressionTestData")
    public Object[][] regressionTestData() {
        return new Object[][]{
                // G1: Without variation, manage inventory by Product
                generateTestObject("G1_01: Check product information with flash sale.", false, false, "hasFlashSale", "inStock"),
                generateTestObject("G1_02: Check product information with discount campaign", false, false, "hasDiscountCampaign", "inStock"),
                generateTestObject("G1_03: Check product information with wholesale price", false, false, "hasWholesalePricing", "inStock"),
                generateTestObject("G1_04: Check product information when hiding remaining stock, product is in stock", false, false, "hideRemaining", "inStock"),
                generateTestObject("G1_05: Check product information when hiding out of stock, product is out of stock", false, false, "notDisplayOutOfStock", ""),
                generateTestObject("G1_06: Check product information when hiding out of stock, product is in stock", false, false, "notDisplayOutOfStock", "inStock"),
                generateTestObject("G1_07: Check product information when free branch is active but hidden", false, false, "hideFreeBranch", "inStock"),
                generateTestObject("G1_08: Check product information when paid branch is active but hidden", false, false, "hidePaidBranch", "inStock"),
                generateTestObject("G1_09: Check product information when all paid branches are deactivated", false, false, "deactivatePaidBranch", "inStock"),

                // G2: Without variation, manage inventory by IMEI/Serial number
                generateTestObject("G2_01: Check product information with flash sale.", false, true, "hasFlashSale", "inStock"),
                generateTestObject("G2_02: Check product information with discount campaign", false, true, "hasDiscountCampaign", "inStock"),
                generateTestObject("G2_03: Check product information with wholesale price", false, true, "hasWholesalePricing", "inStock"),
                generateTestObject("G2_04: Check product information when hiding remaining stock, product is in stock", false, true, "hideRemaining", "inStock"),
                generateTestObject("G2_05: Check product information when hiding out of stock, product is out of stock", false, true, "notDisplayOutOfStock", ""),
                generateTestObject("G2_06: Check product information when hiding out of stock, product is in stock", false, true, "notDisplayOutOfStock", "inStock"),
                generateTestObject("G2_07: Check product information when free branch is active but hidden", false, true, "hideFreeBranch", "inStock"),
                generateTestObject("G2_08: Check product information when paid branch is active but hidden", false, true, "hidePaidBranch", "inStock"),
                generateTestObject("G2_09: Check product information when all paid branches are deactivated", false, true, "deactivatePaidBranch", "inStock"),

                // G3: With variation, manage inventory by Product
                generateTestObject("G3_01: Check product information with flash sale.", true, false, "hasFlashSale", "inStock"),
                generateTestObject("G3_02: Check product information with discount campaign", true, false, "hasDiscountCampaign", "inStock"),
                generateTestObject("G3_03: Check product information with wholesale price", true, false, "hasWholesalePricing", "inStock"),
                generateTestObject("G3_04: Check product information when hiding remaining stock, product is in stock", true, false, "hideRemaining", "inStock"),
                generateTestObject("G3_05: Check product information when hiding out of stock, product is out of stock", true, false, "notDisplayOutOfStock", ""),
                generateTestObject("G3_06: Check product information when hiding out of stock, product is in stock", true, false, "notDisplayOutOfStock", "inStock"),
                generateTestObject("G3_07: Check product information when free branch is active but hidden", true, false, "hideFreeBranch", "inStock"),
                generateTestObject("G3_08: Check product information when paid branch is active but hidden", true, false, "hidePaidBranch", "inStock"),
                generateTestObject("G3_09: Check product information when all paid branches are deactivated", true, false, "deactivatePaidBranch", "inStock"),

                // G4: With variation, manage inventory by IMEI / Serial number
                generateTestObject("G4_01: Check product information with flash sale.", true, true, "hasFlashSale", "inStock"),
                generateTestObject("G4_02: Check product information with discount campaign", true, true, "hasDiscountCampaign", "inStock"),
                generateTestObject("G4_03: Check product information with wholesale price", true, true, "hasWholesalePricing", "inStock"),
                generateTestObject("G4_04: Check product information when hiding remaining stock, product is in stock", true, true, "hideRemaining", "inStock"),
                generateTestObject("G4_05: Check product information when hiding out of stock, product is out of stock", true, true, "notDisplayOutOfStock", ""),
                generateTestObject("G4_06: Check product information when hiding out of stock, product is in stock", true, true, "notDisplayOutOfStock", "inStock"),
                generateTestObject("G4_07: Check product information when free branch is active but hidden", true, true, "hideFreeBranch", "inStock"),
                generateTestObject("G4_08: Check product information when paid branch is active but hidden", true, true, "hidePaidBranch", "inStock"),
                generateTestObject("G4_09: Check product information when all paid branches are deactivated", true, true, "deactivatePaidBranch", "inStock")
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
        List<String> testNames = List.of(
                "G1_01: Check product information with flash sale.",
                "G1_02: Check product information with discount campaign",
                "G1_03: Check product information with wholesale price",
                "G2_01: Check product information with flash sale.",
                "G2_02: Check product information with discount campaign",
                "G2_03: Check product information with wholesale price",
                "G3_01: Check product information with flash sale.",
                "G3_02: Check product information with discount campaign",
                "G3_03: Check product information with wholesale price",
                "G4_01: Check product information with flash sale.",
                "G4_02: Check product information with discount campaign",
                "G4_03: Check product information with wholesale price"
        );

        // Get all test cases from the regression data provider and filter based on testNames
        Object[][] allData = regressionTestData();

        return Arrays.stream(allData)
                .filter(data -> testNames.stream().anyMatch(testName -> ((String) data[0]).contains(testName)))
                .toArray(Object[][]::new);
    }


    /**
     * Tests product detail information based on various product configurations.
     *
     * @param hasModel                Whether the product has model variations.
     * @param isManagedByIMEI         Whether the inventory is managed by IMEI/Serial number.
     * @param createFlashSale         Flag indicating if a flash sale should be created.
     * @param createDiscountCampaign  Flag indicating if a discount campaign should be created.
     * @param addWholesalePrice       Flag indicating if wholesale pricing should be added.
     * @param hideStock               Flag indicating if stock should be hidden.
     * @param hideOutOfStock          Flag indicating if out-of-stock items should be hidden.
     * @param inStock                 Flag indicating if the product is in stock (true if in stock, false otherwise).
     * @param hideFreeBranch          Flag indicating if the free branch should be hidden.
     * @param hidePaidBranch          Flag indicating if paid branches should be hidden.
     * @param deactivateAllPaidBranch Flag indicating if all paid branches should be deactivated.
     */
    @Test(dataProvider = "regressionTestData")
    void regressionTest(String testDescription, boolean hasModel, boolean isManagedByIMEI, boolean createFlashSale,
                        boolean createDiscountCampaign, boolean addWholesalePrice, boolean hideStock, boolean hideOutOfStock,
                        boolean inStock, boolean hideFreeBranch, boolean hidePaidBranch, boolean deactivateAllPaidBranch) {
        productDetailTest(testDescription, hasModel, isManagedByIMEI, createFlashSale, createDiscountCampaign,
                addWholesalePrice, hideStock, hideOutOfStock, inStock, hideFreeBranch, hidePaidBranch,
                deactivateAllPaidBranch);
    }

    @Test(dataProvider = "smokeTestData")
    void smokeTest(String testDescription, boolean hasModel, boolean isManagedByIMEI, boolean createFlashSale,
                   boolean createDiscountCampaign, boolean addWholesalePrice, boolean hideStock, boolean hideOutOfStock,
                   boolean inStock, boolean hideFreeBranch, boolean hidePaidBranch, boolean deactivateAllPaidBranch) {
        productDetailTest(testDescription, hasModel, isManagedByIMEI, createFlashSale, createDiscountCampaign,
                addWholesalePrice, hideStock, hideOutOfStock, inStock, hideFreeBranch, hidePaidBranch,
                deactivateAllPaidBranch);
    }

    void productDetailTest(String testDescription, boolean hasModel, boolean isManagedByIMEI, boolean createFlashSale, boolean createDiscountCampaign, boolean addWholesalePrice, boolean hideStock, boolean hideOutOfStock, boolean inStock, boolean hideFreeBranch, boolean hidePaidBranch, boolean deactivateAllPaidBranch) {
        LogManager.getLogger().info("Running test: {}", testDescription);

        // Create a product based on the provided flags
        int productId = createProduct(hasModel, isManagedByIMEI, hideStock, hideOutOfStock, inStock);

        // Update branch visibility based on the test configuration
        updateBranchVisibility(hideFreeBranch, hidePaidBranch, deactivateAllPaidBranch);

        // Get product information to verify
        APIGetProductDetail.ProductInformation productInfo = getProductInfo(productId);

        // Handle flash sale if applicable
        handleFlashSaleIfRequired(productInfo, createFlashSale);

        // Handle discount campaign if applicable
        handleDiscountCampaignIfRequired(productInfo, createDiscountCampaign);

        // Handle wholesale pricing if applicable
        handleWholesalePricingIfRequired(productInfo, addWholesalePrice);

        // Verify the product information against the expected results
        verifyProductInformation(productId);
    }

    /**
     * Creates a product based on the given parameters.
     *
     * @param withVariation   Whether the product has variations.
     * @param isManagedByIMEI Whether the product is managed by IMEI.
     * @param hideStock       Whether to hide stock.
     * @param hideOutOfStock  Whether to hide out-of-stock items.
     * @param inStock         Whether the product is in stock (true if in stock, false otherwise).
     * @return The ID of the created product.
     */
    private int createProduct(boolean withVariation, boolean isManagedByIMEI, boolean hideStock, boolean hideOutOfStock, boolean inStock) {
        apiCreateProduct.setHideStock(hideStock);
        apiCreateProduct.setShowOutOfStock(!hideOutOfStock);

        int[] stock = new int[RandomUtils.nextInt(10)];
        int branchStock = inStock ? RandomUtils.nextInt(10) + 1 : 0;
        Arrays.fill(stock, branchStock);
        System.out.println(Arrays.toString(Arrays.stream(stock).toArray()));
        LogManager.getLogger().info("Product stock: {}", branchStock);

        // Create the product using the API
        return apiCreateProduct.createProductThenGetId(isManagedByIMEI, withVariation, stock);
    }

    /**
     * Updates the visibility of branches based on test configurations.
     *
     * @param hideFreeBranch            Whether to hide the free branch.
     * @param hidePaidBranch            Whether to hide paid branches.
     * @param deactivateAllPaidBranches Whether to deactivate all paid branches.
     */
    private void updateBranchVisibility(boolean hideFreeBranch, boolean hidePaidBranch, boolean deactivateAllPaidBranches) {
        apiUpdateBranch.setFreeBranchVisibilityOnShopOnline(hideFreeBranch);
        apiUpdateBranch.updateAllPaidBranches(hidePaidBranch, !deactivateAllPaidBranches);
    }

    /**
     * Retrieves product information for a given product ID.
     *
     * @param productId The ID of the product to retrieve information for.
     * @return The product information.
     */
    private APIGetProductDetail.ProductInformation getProductInfo(int productId) {
        return new APIGetProductDetail(sellerCredentials).getProductInformation(productId);
    }

    /**
     * Handles the creation of a flash sale if required.
     *
     * @param productInfo     The product information to use for the flash sale.
     * @param createFlashSale Flag indicating if a flash sale should be created.
     */
    private void handleFlashSaleIfRequired(APIGetProductDetail.ProductInformation productInfo, boolean createFlashSale) {
        if (createFlashSale) {
            apiCreateFlashSale.createFlashSale(productInfo, 1, 30);
            APICreateFlashSale.waitForFlashSaleStart();
        }
    }

    /**
     * Handles the creation of a discount campaign if required.
     *
     * @param productInfo            The product information to use for the discount campaign.
     * @param createDiscountCampaign Flag indicating if a discount campaign should be created.
     */
    private void handleDiscountCampaignIfRequired(APIGetProductDetail.ProductInformation productInfo, boolean createDiscountCampaign) {
        if (createDiscountCampaign) {
            apiCreateDiscountCampaign.createProductDiscountCampaign(productInfo, 0);
        }
    }

    /**
     * Handles the addition of wholesale pricing if required.
     *
     * @param productInfo       The product information to use for adding wholesale pricing.
     * @param addWholesalePrice Flag indicating if wholesale pricing should be added.
     */
    private void handleWholesalePricingIfRequired(APIGetProductDetail.ProductInformation productInfo, boolean addWholesalePrice) {
        if (addWholesalePrice) {
            apiAddWholesaleProduct.addWholesalePriceProduct(productInfo);
        }
    }

    /**
     * Verifies the product information on the product detail page against expected values.
     *
     * @param productId The ID of the product to verify.
     */
    private void verifyProductInformation(int productId) {
        new AndroidBuyerProductDetailScreen(driver).navigateProductDetailPage(sellerCredentials, productId)
                .verifyProductInformation(customerId);
    }
}