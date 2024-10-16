package web.buyer;

import api.buyer.login.APIBuyerLogin;
import api.seller.product.APIAddWholesaleProduct;
import api.seller.product.APICreateProduct;
import api.seller.product.APIGetProductDetail;
import api.seller.promotion.APICreateFlashSale;
import api.seller.promotion.APICreateProductDiscountCampaign;
import api.seller.setting.APIUpdateBranchInformation;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.web.buyer.detail_product.ProductDetailPage;
import pages.web.buyer.login.StorefrontLoginPage;
import utility.WebDriverManager;

import java.util.Arrays;
import java.util.List;

/**
 * This class contains test cases for verifying product details on the storefront,
 * including various scenarios for products managed by Product or IMEI.
 */
public class ProductDetailTest extends StorefrontBaseTest {
    private int customerId;
    private APICreateProduct apiCreateProduct;
    private APICreateFlashSale apiCreateFlashSale;
    private APICreateProductDiscountCampaign apiCreateDiscountCampaign;
    private APIAddWholesaleProduct apiAddWholesaleProduct;
    private APIUpdateBranchInformation apiUpdateBranch;

    /**
     * Sets up the necessary APIs and logs into the storefront before running tests.
     */
    @BeforeClass
    void setup() {
        this.driver = new WebDriverManager().getWebDriver();
        new StorefrontLoginPage(driver).loginStorefrontByJS(buyerCredentials);
        this.customerId = new APIBuyerLogin().getBuyerInformation(buyerCredentials).getId();
        this.apiCreateProduct = new APICreateProduct(sellerCredentials);
        this.apiCreateFlashSale = new APICreateFlashSale(sellerCredentials);
        this.apiCreateDiscountCampaign = new APICreateProductDiscountCampaign(sellerCredentials);
        this.apiAddWholesaleProduct = new APIAddWholesaleProduct(sellerCredentials);
        this.apiUpdateBranch = new APIUpdateBranchInformation(sellerCredentials);
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
    private Object[] generateTestObject(boolean hasModel, boolean isManagedByIMEI, String setterKey1, String setterKey2, String testName) {
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
                flags[0], flags[1], flags[2], flags[3], flags[4], flags[5],
                flags[6], flags[7], flags[8], flags[9], flags[10], testName
        };
    }

    /**
     * Data provider for product test scenarios.
     *
     * @return A 2D array of test data for product detail tests.
     */
    @DataProvider(name = "productTestData")
    public Object[][] productTestData() {
        return new Object[][]{
//                // G1: Without variation, manage inventory by Product
//                generateTestObject(false, false, "hasFlashSale", "inStock", "G1: Check product information with flash sale."),
//                generateTestObject(false, false, "hasDiscountCampaign", "inStock", "G1: Check product information with discount campaign"),
//                generateTestObject(false, false, "hasWholesalePricing", "inStock", "G1: Check product information with wholesale price"),
                generateTestObject(false, false, "hideRemaining", "inStock", "G1: Check product information when hiding remaining stock, product is in stock"),
//                generateTestObject(false, false, "notDisplayOutOfStock", "", "G1: Check product information when hiding out of stock, product is out of stock"),
//                generateTestObject(false, false, "notDisplayOutOfStock", "inStock", "G1: Check product information when hiding out of stock, product is in stock"),
//                generateTestObject(false, false, "hideFreeBranch", "inStock", "G1: Check product information when free branch is active but hidden"),
//                generateTestObject(false, false, "hidePaidBranch", "inStock", "G1: Check product information when paid branch is active but hidden"),
//                generateTestObject(false, false, "deactivatePaidBranch", "inStock", "G1: Check product information when all paid branches are deactivated"),
//
//                // G2: Without variation, manage inventory by IMEI/Serial number
//                generateTestObject(false, true, "hasFlashSale", "inStock", "G2: Check product information with flash sale."),
//                generateTestObject(false, true, "hasDiscountCampaign", "inStock", "G2: Check product information with discount campaign"),
//                generateTestObject(false, true, "hasWholesalePricing", "inStock", "G2: Check product information with wholesale price"),
//                generateTestObject(false, true, "hideRemaining", "inStock", "G2: Check product information when hiding remaining stock, product is in stock"),
//                generateTestObject(false, true, "notDisplayOutOfStock", "", "G2: Check product information when hiding out of stock, product is out of stock"),
//                generateTestObject(false, true, "notDisplayOutOfStock", "inStock", "G2: Check product information when hiding out of stock, product is in stock"),
//                generateTestObject(false, true, "hideFreeBranch", "inStock", "G2: Check product information when free branch is active but hidden"),
//                generateTestObject(false, true, "hidePaidBranch", "inStock", "G2: Check product information when paid branch is active but hidden"),
//                generateTestObject(false, true, "deactivatePaidBranch", "inStock", "G2: Check product information when all paid branches are deactivated"),
//
//                // G3: With variation, manage inventory by Product
//                generateTestObject(true, false, "hasFlashSale", "inStock", "G3: Check product information with flash sale."),
//                generateTestObject(true, false, "hasDiscountCampaign", "inStock", "G3: Check product information with discount campaign"),
//                generateTestObject(true, false, "hasWholesalePricing", "inStock", "G3: Check product information with wholesale price"),
//                generateTestObject(true, false, "hideRemaining", "inStock", "G3: Check product information when hiding remaining stock, product is in stock"),
//                generateTestObject(true, false, "notDisplayOutOfStock", "", "G3: Check product information when hiding out of stock, product is out of stock"),
//                generateTestObject(true, false, "notDisplayOutOfStock", "inStock", "G3: Check product information when hiding out of stock, product is in stock"),
//                generateTestObject(true, false, "hideFreeBranch", "inStock", "G3: Check product information when free branch is active but hidden"),
//                generateTestObject(true, false, "hidePaidBranch", "inStock", "G3: Check product information when paid branch is active but hidden"),
//                generateTestObject(true, false, "deactivatePaidBranch", "inStock", "G3: Check product information when all paid branches are deactivated"),
//
//                // G4: With variation, manage inventory by IMEI / Serial number
//                generateTestObject(true, true, "hasFlashSale", "inStock", "G4: Check product information with flash sale."),
//                generateTestObject(true, true, "hasDiscountCampaign", "inStock", "G4: Check product information with discount campaign"),
//                generateTestObject(true, true, "hasWholesalePricing", "inStock", "G4: Check product information with wholesale price"),
//                generateTestObject(true, true, "hideRemaining", "inStock", "G4: Check product information when hiding remaining stock, product is in stock"),
//                generateTestObject(true, true, "notDisplayOutOfStock", "", "G4: Check product information when hiding out of stock, product is out of stock"),
//                generateTestObject(true, true, "notDisplayOutOfStock", "inStock", "G4: Check product information when hiding out of stock, product is in stock"),
//                generateTestObject(true, true, "hideFreeBranch", "inStock", "G4: Check product information when free branch is active but hidden"),
//                generateTestObject(true, true, "hidePaidBranch", "inStock", "G4: Check product information when paid branch is active but hidden"),
//                generateTestObject(true, true, "deactivatePaidBranch", "inStock", "G4: Check product information when all paid branches are deactivated")
        };
    }

    /**
     * Tests product detail information based on various product configurations.
     *
     * @param hasModel                Whether the product has model variations.
     * @param isManagedByIMEI         Whether the inventory is managed by IMEI/Serial number.
     * @param createFlashSale         Flag indicating if a flash sale should be created.
     * @param createDiscountCampaign   Flag indicating if a discount campaign should be created.
     * @param addWholesalePrice        Flag indicating if wholesale pricing should be added.
     * @param hideStock                Flag indicating if stock should be hidden.
     * @param hideOutOfStock           Flag indicating if out-of-stock items should be hidden.
     * @param inStock                  Flag indicating if the product is in stock (true if in stock, false otherwise).
     * @param hideFreeBranch           Flag indicating if the free branch should be hidden.
     * @param hidePaidBranch           Flag indicating if paid branches should be hidden.
     * @param deactivateAllPaidBranch  Flag indicating if all paid branches should be deactivated.
     */
    @Test(dataProvider = "productTestData")
    void productDetailTest(boolean hasModel, boolean isManagedByIMEI, boolean createFlashSale, boolean createDiscountCampaign, boolean addWholesalePrice, boolean hideStock, boolean hideOutOfStock, boolean inStock, boolean hideFreeBranch, boolean hidePaidBranch, boolean deactivateAllPaidBranch, String testDescription) {
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
     * @param withVariation       Whether the product has variations.
     * @param isManagedByIMEI     Whether the product is managed by IMEI.
     * @param hideStock           Whether to hide stock.
     * @param hideOutOfStock      Whether to hide out-of-stock items.
     * @param inStock             Whether the product is in stock (true if in stock, false otherwise).
     * @return The ID of the created product.
     */
    private int createProduct(boolean withVariation, boolean isManagedByIMEI, boolean hideStock, boolean hideOutOfStock, boolean inStock) {
        apiCreateProduct.setHideStock(hideStock);
        apiCreateProduct.setShowOutOfStock(!hideOutOfStock);

        int[] stock = new int[RandomUtils.nextInt(10)];
        Arrays.fill(stock, inStock ? RandomUtils.nextInt(10) : 0);

        // Create the product using the API
        return apiCreateProduct.createProduct(isManagedByIMEI, withVariation, stock);
    }

    /**
     * Updates the visibility of branches based on test configurations.
     *
     * @param hideFreeBranch         Whether to hide the free branch.
     * @param hidePaidBranch         Whether to hide paid branches.
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
     * @param productInfo The product information to use for the flash sale.
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
     * @param productInfo The product information to use for the discount campaign.
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
     * @param productInfo The product information to use for adding wholesale pricing.
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
        new ProductDetailPage(driver).navigateProductDetailPage(sellerCredentials, productId)
                .verifyProductInformation(customerId);
    }
}