package pages.ios.buyer.product;

import api.buyer.product.APIGetCampaignInformation;
import api.buyer.product.APIGetFlashSaleInformation;
import api.buyer.product.APIGetWholesaleInformation;
import api.seller.login.APISellerLogin;
import api.seller.product.APIGetProductDetail;
import api.seller.sale_channel.APIGetPreferences;
import api.seller.setting.APIGetBranchList;
import io.appium.java_client.AppiumBy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import pages.ios.buyer.home.IOSBuyerHomeScreen;
import utility.IOSUtils;
import utility.PropertiesUtils;
import utility.WebUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static api.seller.setting.APIGetBranchList.*;

public class IOSBuyerProductDetailScreen {
    // WebDriver instance for interacting with the browser
    private final WebDriver driver;

    // Logger instance for logging relevant information and events
    private final Logger logger = LogManager.getLogger();

    // Utility class instance for common WebDriver actions
    private final IOSUtils iosUtils;

    // Information about the product retrieved via the API
    private APIGetProductDetail.ProductInformation productInfo;

    // List containing branch information fetched from the API
    private List<BranchInformation> branchInfos;

    // Credentials used for API authentication
    private APISellerLogin.Credentials credentials;

    /**
     * Constructor for ProductDetailPage.
     * Initializes the WebDriver instance and common utilities required for page interactions.
     *
     * @param driver the WebDriver instance for interacting with the browser
     */
    public IOSBuyerProductDetailScreen(WebDriver driver) {
        // Assign the WebDriver instance passed to the class
        this.driver = driver;

        // Initialize WebUtils for common WebDriver interactions
        iosUtils = new IOSUtils(driver);
    }

    // Locators
    private By loc_lblProductName(String productName) {
        return By.xpath("(//XCUIElementTypeStaticText[@name=\"%s\"])[1]".formatted(productName));
    }

    private By loc_lblSellingPrice(long sellingPrice) {
        return By.xpath("//*[*/*[@name=\"bg_variation_popup_close_button\"]]//XCUIElementTypeStaticText[contains(@name, \"%,d\")]".formatted(sellingPrice));
    }

    private By loc_ddvVariationName(String variationName) {
        return AppiumBy.iOSClassChain("**/XCUIElementTypeCell/**/XCUIElementTypeStaticText[`name == \"%s\"`]".formatted(variationName));
    }

    private By loc_ddvVariationValue(String variationValue) {
        return AppiumBy.iOSClassChain("**/XCUIElementTypeCell/**/XCUIElementTypeStaticText[`name == \"%s\"`]".formatted(variationValue));
    }

    private By loc_lblBranchStock(String branchName) {
        return AppiumBy.iOSClassChain("**/XCUIElementTypeStaticText[`name BEGINSWITH \"%s\"`]".formatted(branchName));
    }

    // Using this to scroll to description section
    private By loc_cntDescription(String productDescription) {
        return By.xpath("//XCUIElementTypeStaticText[@name=\"%s\"]".formatted(productDescription));
    }

    private final By loc_lblSoldOut = By.xpath("//XCUIElementTypeStaticText[@name=\"Hết Hàng\"]");
    private final By loc_txtQuantity = By.xpath("//XCUIElementTypeImage[@name=\"icon_minus_small\"]//preceding-sibling::XCUIElementTypeTextField");
    private final By loc_lblFlashSale = By.xpath("//XCUIElementTypeStaticText[contains(@name,\" Sold \") or contains(@name, 'Đã bán')]");
    private final By loc_lblDiscountCampaign = By.xpath("//XCUIElementTypeStaticText[@name=\"Bán sỉ\" or @name=\"Wholesale\"]");
    private final By loc_chkBuyInBulk = By.xpath("//*[XCUIElementTypeStaticText[@name=\"Mua số lượng lớn\" or @name=\"Buy in Bulk\"]]/XCUIElementTypeButton");
    private final By loc_pnlWholesalePricing = By.xpath("//XCUIElementTypeStaticText[@name=\"Giá bán sỉ\" or @name=\"Wholesale pricing\"]");
    private final By loc_btnBuyNow = By.xpath("//XCUIElementTypeButton[@name=\"Mua ngay\" or @name=\"Buy now\"]");
    private final By loc_btnAddToCart = By.xpath("//XCUIElementTypeButton[@name=\"  \"]");
    private final By loc_btnCloseCart = By.xpath("//*[XCUIElementTypeImage[@name=\"bg_variation_popup_close_button\"]]/XCUIElementTypeButton");
    private final By loc_icnSearchBranch = AppiumBy.iOSClassChain("**/XCUIElementTypeCell/**/XCUIElementTypeButton[`name == \"ic booking search\"`]");
    private final By loc_lblCartVariations = By.xpath("(//*[*/XCUIElementTypeImage[@name=\"btn_close_small_gray\"]]//XCUIElementTypeStaticText)[last()]");

    /**
     * Compares the product name displayed in the storefront with the product name from the dashboard.
     *
     * @param modelId  The model ID of the product.
     * @param language The language to retrieve the product name in.
     */
    private void validateProductName(Integer modelId, String language) {
        // Retrieve the product name from the dashboard
        String expectedProductName = productInfo.isHasModel()
                ? APIGetProductDetail.getVersionName(productInfo, modelId, language)
                : APIGetProductDetail.getMainProductName(productInfo, language);

        // Validate product name
        Assert.assertFalse(iosUtils.getListElement(loc_lblProductName(expectedProductName)).isEmpty(),
                "Product name should be '%s', but it does not match.".formatted(expectedProductName));
        logger.info("[Validate Product Name] Product name is displayed correctly.");
    }

    /**
     * Compares the listing and selling prices for each branch against expected values.
     *
     * @param expectedSellingPrice The expected selling price.
     * @param branchName           The name of the branch.
     */
    private void validateBranchPrices(long expectedSellingPrice, String branchName, Runnable adjustStockAction) {
        String branchInfo = branchName.isEmpty() ? "" : "[Branch name: %s]".formatted(branchName);

        if (!(new APIGetPreferences(credentials).getStoreListingWebInformation().isEnabledProduct() && productInfo.isEnabledListing())) {
            // Open cart popup to verify the product selling price
            iosUtils.click(loc_btnAddToCart);
            logger.info("%s Open cart popup.".formatted(branchInfo));

            // Run pre-action to check selling price if needed
            if (adjustStockAction != null) {
                adjustStockAction.run();
            }

            Assert.assertFalse(iosUtils.getListElement(loc_lblSellingPrice(expectedSellingPrice)).isEmpty(),
                    "%s Selling price must be '%,d', but it does not match.".formatted(branchInfo, expectedSellingPrice));
            logger.info("{} Checked product prices and store currency.", branchInfo);

            // Close cart after verify selling price
            iosUtils.click(loc_btnCloseCart);
            logger.info("%s Close cart popup.".formatted(branchInfo));
        } else {
            logger.info("{} Website listing is enabled, so listing/selling price is hidden.", branchInfo);
        }
    }

    // Check if the flash sale badge is displayed
    private void validateFlashSaleDisplay(String branchName) {
        String branchInfo = "[Branch name: %s]".formatted(branchName);
        Assert.assertFalse(iosUtils.getListElement(loc_lblFlashSale).isEmpty(), "%s Flash sale badge is not displayed.".formatted(branchInfo));
        logger.info("{} Checked flash sale badge display.", branchInfo);
    }

    // Check if the discount campaign is displayed
    private void validateDiscountCampaignDisplay(String branchName) {
        String branchInfo = "[Branch name: %s]".formatted(branchName);

        Assert.assertFalse(iosUtils.getListElement(loc_lblDiscountCampaign).isEmpty(), "%s Discount campaign is not displayed.".formatted(branchInfo));
        logger.info("{} Checked discount campaign display.", branchInfo);
    }

    // Check if wholesale product pricing is displayed
    private void validateWholesalePricingDisplay(String branchName) {
        String branchInfo = "[Branch name: %s]".formatted(branchName);
        Assert.assertFalse(iosUtils.getListElement(loc_pnlWholesalePricing).isEmpty(), "%s Wholesale product information is not displayed.".formatted(branchInfo));
        logger.info("{} Checked wholesale product information display.", branchInfo);
    }

    /**
     * Validates the variation names displayed in the storefront against the database values.
     *
     * @param language The language to check the variation names in.
     */
    private void validateVariationNames(String language) {
        // Retrieve the variation name list from the dashboard
        List<String> expectedVariationNames = Arrays.stream(APIGetProductDetail.getVariationName(productInfo, language).split("\\|")).toList();
        expectedVariationNames.forEach(variationName ->
                Assert.assertFalse(iosUtils.getListElement(loc_ddvVariationName(variationName)).isEmpty(),
                        "Can not find variation name '%s'".formatted(variationName)));

        // Log variation name check
        logger.info("Checked variation names.");
    }

    /**
     * Verifies that the filter and search elements for branches are either displayed or hidden.
     *
     * @param variationName   the name of the variation being checked
     * @param shouldBeVisible true if the elements should be visible, false if they should be hidden
     */
    private void verifyFilterAndSearchBranchesVisibility(String variationName, boolean shouldBeVisible) {
        String varName = !variationName.isEmpty() ? "[Variation: %s]".formatted(variationName) : "";

        // Define expected state as visible or hidden
        String visibilityCheck = shouldBeVisible ? "shown" : "hidden";

        // Assert search branch visibility
        boolean isSearchVisible = !iosUtils.getListElement(loc_icnSearchBranch).isEmpty();
        Assert.assertEquals(isSearchVisible, shouldBeVisible,
                "%s 'Search box' should be %s but it is %s.".formatted(varName, visibilityCheck, isSearchVisible ? "shown" : "hidden"));
        logger.info("{} Checking if 'Search box' is {}.", varName, visibilityCheck);
    }


    /**
     * Validates that the branch is displayed correctly based on stock and status.
     *
     * @param branchName    the name of the branch
     * @param isVisible     the visibility status of the branch
     * @param stockCount    the stock count of the branch
     * @param variationName the name of the variation being checked
     */
    private void validateBranchName(String branchName, boolean isVisible, int stockCount, String variationName) {
        String varName = !variationName.isEmpty() ? "[Variation: %s]".formatted(variationName) : "";

        // Assert branch visibility based on stock
        Assert.assertTrue(getBranchNames(branchInfos).contains(branchName) && isVisible && (stockCount > 0),
                "[Branch name: %s] Branch in-stock but is not shown.".formatted(branchName));
        logger.info("{} Validating visibility for branch '{}'", varName, branchName);
    }

    /**
     * Compares the stock quantity of a branch between the storefront and the dashboard.
     *
     * @param branchName    the name of the branch
     * @param isVisible     the visibility status of the branch
     * @param expectedStock the expected stock count of the branch
     * @param variationName the name of the variation being checked
     */
    private void validateBranchStock(String branchName, boolean isVisible, int expectedStock, String variationName) {
        String varName = !variationName.isEmpty() ? "[Variation: %s]".formatted(variationName) : "";
        if (!productInfo.getIsHideStock() && isVisible) {
            String actualStockText = iosUtils.getText(loc_lblBranchStock(branchName));
            logger.info("Branch and stock text: '{}'", actualStockText);
            int actualStock = Integer.parseInt(actualStockText.split("-")[1].split(",")[0].replaceAll("\\D+", ""));

            // Assert stock quantities match
            Assert.assertEquals(actualStock, expectedStock,
                    "%s[Branch name: %s] Stock quantity should be %s, but found %s".formatted(varName, branchName, expectedStock, actualStock));
        } else {
            logger.info("Stock visibility is hidden for branch '{}'.", branchName);
        }
    }

    /**
     * Compares the product description between the storefront and the dashboard.
     *
     * @param modelId  the model ID of the product
     * @param language the language for the description
     */
    private void validateProductDescription(Integer modelId, String language) {
        // Retrieve dashboard product description
        String expectedDescription = productInfo.isHasModel()
                ? APIGetProductDetail.getVersionDescription(productInfo, modelId, language)
                : APIGetProductDetail.getMainProductDescription(productInfo, language);
        expectedDescription = expectedDescription.replaceAll("<.*?>", "").replaceAll("amp;", "");

        // Assert descriptions match
        Assert.assertFalse(iosUtils.getListElement(loc_cntDescription(expectedDescription)).isEmpty(),
                "[Check description] Product description should be '%s', but it does not match'".formatted(expectedDescription));
        logger.info("[Check description] Product description is shown correctly.");
    }

    /**
     * Checks if the 'Buy Now' and 'Add to Cart' buttons are either visible or hidden.
     *
     * @param variationName the name of the variation being checked
     */
    private void verifyBuyNowAndAddToCartButtonsVisibility(String variationName) {
        String varName = !variationName.isEmpty() ? "[Variation: %s]".formatted(variationName) : "";

        boolean shouldBeVisible = !(new APIGetPreferences(credentials).getStoreListingWebInformation().isEnabledProduct() && productInfo.isEnabledListing());

        // Define expected state as visible or hidden
        String visibilityCheck = shouldBeVisible ? "shown" : "hidden";

        // Assert 'Buy Now' button visibility/invisibility
        boolean isBuyNowVisible = !iosUtils.getListElement(loc_btnBuyNow).isEmpty();
        Assert.assertTrue(isBuyNowVisible, "%s 'Buy now' button should be %s but it is %s.".formatted(varName, visibilityCheck, isBuyNowVisible ? "shown" : "hidden"));
        logger.info("{} Checking if 'Buy Now' button is {}.", varName, visibilityCheck);

        // Assert 'Add to Cart' button visibility/invisibility
        boolean isAddToCartVisible = !iosUtils.getListElement(loc_btnAddToCart).isEmpty();
        Assert.assertTrue(isAddToCartVisible, "%s 'Add to cart' button should be %s but it is %s.".formatted(varName, visibilityCheck, isAddToCartVisible ? "shown" : "hidden"));
        logger.info("{} Checking if 'Add to Cart' button is {}.", varName, visibilityCheck);
    }

    /**
     * Checks if the 'Sold Out' mark is displayed correctly.
     *
     * @param variationName the name of the variation being checked
     */
    private void verifySoldOutMarkDisplayed(String variationName) {
        String varName = !variationName.isEmpty() ? "[Variation: %s]".formatted(variationName) : "";
        boolean isSoldOut = !iosUtils.getListElement(loc_lblSoldOut).isEmpty();

        // Assert sold out mark is visible
        Assert.assertTrue(isSoldOut, "%s Sold out mark does not show".formatted(varName));
        logger.info("{} Checking if 'SOLD OUT' mark is shown", varName);
    }

    /**
     * Verifies the pricing and discounts for a given variation based on available campaigns, flash sales, or wholesale information.
     * Prioritizes flash sales over campaigns and wholesale discounts, and displays the appropriate pricing for the variation.
     *
     * @param itemId         the ID of the item
     * @param variationIndex the index of the variation model
     * @param branchId       the ID of the branch
     * @param customerId     the ID of the customer
     * @param branchName     the name of the branch
     */
    private void verifyVariationPriceAndDiscount(int itemId, int variationIndex, int branchId, int customerId, String branchName) {
        // Retrieve pricing and discount information from external sources
        APIGetCampaignInformation.CampaignInformation campaignInfo = fetchCampaignInfo(itemId, branchId, customerId);
        APIGetWholesaleInformation.WholesaleInformation wholesaleInfo = fetchWholesaleInfo(itemId, customerId, variationIndex);
        APIGetFlashSaleInformation.FlashSaleInformation flashSaleInfo = fetchFlashSaleInfo(itemId, variationIndex);

        // Determine the selling price for the variation
        long sellingPrice = getSellingPrice(variationIndex);

        // Display appropriate pricing and discount information based on priority
        if (isFlashSaleActive(flashSaleInfo)) {
            displayFlashSaleInfo(flashSaleInfo, branchName);
        } else if (campaignInfo != null) {
            displayCampaignInfo(campaignInfo, sellingPrice, branchName);
        } else if (wholesaleInfo != null) {
            displayWholesaleInfo(wholesaleInfo, branchName);
        } else {
            displayRegularPrice(sellingPrice, branchName);
        }
    }

    /**
     * Fetches discount campaign information for the specified item, branch, and customer.
     *
     * @param itemId     the ID of the item
     * @param branchId   the ID of the branch
     * @param customerId the ID of the customer
     * @return the campaign information, or null if no campaign is available
     */
    private APIGetCampaignInformation.CampaignInformation fetchCampaignInfo(int itemId, int branchId, int customerId) {
        return new APIGetCampaignInformation(credentials).getDiscountCampaignInformation(itemId, branchId, customerId);
    }

    /**
     * Fetches wholesale pricing information for the specified item, customer, and variation model.
     *
     * @param itemId         the ID of the item
     * @param customerId     the ID of the customer
     * @param variationIndex the index of the variation model
     * @return the wholesale information, or null if no wholesale information is available
     */
    private APIGetWholesaleInformation.WholesaleInformation fetchWholesaleInfo(int itemId, int customerId, int variationIndex) {
        return new APIGetWholesaleInformation(credentials).getWholesaleInformation(itemId, customerId, fetchModelId(variationIndex));
    }

    /**
     * Fetches flash sale information for the specified item and variation model.
     *
     * @param itemId         the ID of the item
     * @param variationIndex the index of the variation model
     * @return the flash sale information, or null if no flash sale is active
     */
    private APIGetFlashSaleInformation.FlashSaleInformation fetchFlashSaleInfo(int itemId, int variationIndex) {
        return new APIGetFlashSaleInformation(credentials).getFlashSaleInformation(itemId, fetchModelId(variationIndex));
    }

    /**
     * Retrieves the selling price for the variation model. If the product has no variation model,
     * the new price of the product is returned.
     *
     * @param variationIndex the index of the variation model
     * @return the selling price of the variation or the new product price if no variation exists
     */
    private long getSellingPrice(int variationIndex) {
        return productInfo.isHasModel()
                ? APIGetProductDetail.getVariationSellingPrice(productInfo, variationIndex)
                : productInfo.getNewPrice();
    }

    /**
     * Determines whether a flash sale is active for the variation.
     *
     * @param flashSaleInfo the flash sale information
     * @return true if a flash sale is active and not scheduled, false otherwise
     */
    private boolean isFlashSaleActive(APIGetFlashSaleInformation.FlashSaleInformation flashSaleInfo) {
        return flashSaleInfo != null && flashSaleInfo.getStatus().equals("IN_PROGRESS");
    }


    /**
     * Displays flash sale information and verifies the prices.
     */
    private void displayFlashSaleInfo(APIGetFlashSaleInformation.FlashSaleInformation flashSaleInfo, String brName) {
        validateFlashSaleDisplay(brName);
        logger.info("PRICE: FLASH SALE");
        validateBranchPrices(flashSaleInfo.getItems().getFirst().getNewPrice(), brName, null);
    }

    /**
     * Displays campaign information and verifies the prices.
     */
    private void displayCampaignInfo(APIGetCampaignInformation.CampaignInformation campaignInfo, long sellingPrice, String brName) {
        validateDiscountCampaignDisplay(brName);
        logger.info("PRICE: DISCOUNT CAMPAIGN");
        long newPrice = calculateCampaignPrice(campaignInfo, sellingPrice);

        validateBranchPrices(newPrice, brName,
                () -> iosUtils.click(loc_chkBuyInBulk));
    }

    /**
     * Displays wholesale information and verifies the prices.
     */
    private void displayWholesaleInfo(APIGetWholesaleInformation.WholesaleInformation wholesaleInfo, String brName) {
        validateWholesalePricingDisplay(brName);
        logger.info("PRICE: WHOLESALE PRODUCT");
        validateBranchPrices(wholesaleInfo.getPrice().longValue(), brName,
                () -> adjustQuantityForWholesale(wholesaleInfo));
    }

    /**
     * Displays the regular selling price.
     */
    private void displayRegularPrice(long sellingPrice, String brName) {
        logger.info("PRICE: SELLING PRICE");
        validateBranchPrices(sellingPrice, brName, null);
    }

    /**
     * Calculates the campaign price based on the discount information.
     */
    private long calculateCampaignPrice(APIGetCampaignInformation.CampaignInformation campaignInfo, long sellingPrice) {
        String discountType = campaignInfo.getWholesales().getFirst().getType();
        long discountValue = campaignInfo.getWholesales().getFirst().getWholesaleValue();
        return discountType.equals("FIXED_AMOUNT")
                ? Math.max(sellingPrice - discountValue, 0)
                : (sellingPrice * (100 - discountValue)) / 100;
    }

    /**
     * Adjusts the quantity input field based on the wholesale minimum quantity.
     */
    private void adjustQuantityForWholesale(APIGetWholesaleInformation.WholesaleInformation wholesaleInfo) {
        int minQuantity = wholesaleInfo.getMinQuatity();
        setQuantityWithRetries(minQuantity);
    }

    /**
     * Sends the specified quantity to the quantity input field with retry logic.
     *
     * @param minQuantity the minimum quantity to send
     * @throws IllegalStateException if unable to set the quantity after maximum retries
     */
    private void setQuantityWithRetries(int minQuantity) {
        for (int attempt = 1; attempt <= 5; attempt++) {
            iosUtils.sendKeys(loc_txtQuantity, minQuantity);

            if (iosUtils.getText(loc_txtQuantity).equals(String.valueOf(minQuantity))) {
                return;
            }

            logger.warn("Attempt {} to set quantity failed. Retrying...", attempt);
        }
        throw new IllegalStateException("Failed to set quantity after 5 attempts.");
    }

    /**
     * Checks and validates all variation information, including attributes, stock, pricing, and discounts
     * for a product across different branches. This method ensures that product variations are displayed
     * and configured correctly, validates stock availability across branches, and handles UI elements
     * like Buy Now and Add to Cart buttons based on stock status.
     *
     * @param customerId    the ID of the customer to fetch pricing and discounts based on their profile
     * @param variationName the name of the variation being validated
     */
    private void validateVariationInformation(int variationIndex, int customerId, String variationName) {
        // Retrieve the model ID for the variation. If the product has no model, this will return null.
        Integer modelId = fetchModelId(variationIndex);

        // Validate product name
        validateProductName(modelId, langKey);

        // Validate variation names if the product has multiple models
        if (productInfo.isHasModel()) {
            validateVariationNames(langKey);
        }

        // Retrieve the list of branch IDs for the product.
        List<Integer> branchIds = getBranchIds(branchInfos);

        // Count the number of branches that have stock and are visible on the storefront.
        var visibleBranches = getVisibleBranchesWithStock(branchInfos, productInfo, modelId);

        // If there are branches with stock and visible on the storefront, validate each branch's information.
        if (visibleBranches.isEmpty()) {
            // Handle cases where no branches have stock by verifying the Sold Out status and updating UI elements.
            handleOutOfStockBranches(variationName);
        } else {
            // Validate branch stock, pricing, and other details for branches with stock.
            verifyBranchStockNameAndPrice(variationName, visibleBranches, branchIds, variationIndex, customerId);
        }

        // Validate product description
        validateProductDescription(modelId, langKey);
    }

    /**
     * Retrieves the model ID for the variation. If the product has no model, returns null.
     *
     * @param variationIndex the index of the variation being validated
     * @return the model ID or null if there is no variation model
     */
    private Integer fetchModelId(int variationIndex) {
        return productInfo.isHasModel() ? APIGetProductDetail.getVariationModelId(productInfo, variationIndex) : null;
    }

    /**
     * Validates the branches that have stock available and are visible on the storefront.
     * This includes verifying the visibility of filters, branch names, and pricing information.
     *
     * @param variationName   the name of the product variation being validated
     * @param visibleBranches the list of branches with visible stock and their details
     * @param branchIds       the list of branch IDs corresponding to the branches
     * @param variationIndex  the index of the variation being validated, used for reference
     * @param customerId      the unique ID of the customer, used for retrieving pricing and discount details
     */
    private void verifyBranchStockNameAndPrice(String variationName, List<BranchInformation> visibleBranches,
                                               List<Integer> branchIds, int variationIndex, int customerId) {
        // Verify branch filter and "Buy Now"/"Add to Cart" button visibility
        verifyFilterAndSearchBranchesVisibility(variationName, visibleBranches.size() >= 6);
        verifyBuyNowAndAddToCartButtonsVisibility(variationName);

        // Validate stock and pricing for each visible branch
        IntStream.range(0, visibleBranches.size()).forEach(brElementIndex -> {
            // Retrieve branch name
            String branchName = visibleBranches.get(brElementIndex).getName();

            // Get branch ID and corresponding index
            int branchIndex = getBranchIndexByName(branchName);
            int branchId = branchIds.get(branchIndex);

            // Validate branch details, stock, and pricing
            validateBranchInfo(branchName, branchIndex, branchId, variationIndex, customerId, variationName);
        });
    }

    /**
     * Retrieves the index of a branch based on its name.
     *
     * @param branchName the name of the branch
     * @return the index of the branch
     */
    private int getBranchIndexByName(String branchName) {
        return getBranchNames(branchInfos).indexOf(branchName);
    }

    /**
     * Validates the stock, pricing, and other information for a branch.
     *
     * @param branchName     the name of the branch
     * @param branchIndex    the index of the branch in the API data
     * @param branchId       the ID of the branch
     * @param variationIndex the index of the variation being validated
     * @param customerId     the ID of the customer
     * @param variationName  the name of the variation
     */
    private void validateBranchInfo(String branchName, int branchIndex, int branchId, int variationIndex, int customerId, String variationName) {
        boolean isBranchShown = isBranchShownOnStorefront(branchInfos, branchIndex);
        int expectedStock = APIGetProductDetail.getStockByModelAndBranch(productInfo, fetchModelId(variationIndex), branchId);

        // Switch to the branch using its name
        iosUtils.click(loc_lblBranchStock(branchName));

        validateBranchStock(branchName, isBranchShown, expectedStock, variationName);
        validateBranchName(branchName, isBranchShown, expectedStock, variationName);
        verifyVariationPriceAndDiscount(productInfo.getId(), variationIndex, branchId, customerId, branchName);
    }

    /**
     * Handles the scenario where no branches have stock by verifying the Sold Out mark is displayed
     * and hiding the Buy Now and Add to Cart buttons.
     *
     * @param variationName the name of the variation being validated
     */
    private void handleOutOfStockBranches(String variationName) {
        verifySoldOutMarkDisplayed(variationName);
        verifyBuyNowAndAddToCartButtonsVisibility(variationName);
    }

    /**
     * Counts the number of branches that are both visible on the storefront
     * and have stock available for a given product model.
     *
     * @param branchInfos A list of branch information used to determine visibility and stock.
     * @param productInfo The product information, including stock availability per model and branch.
     * @param modelId     The identifier for the product model whose stock is being checked.
     * @return The number of branches with stock and visible on the storefront.
     */
    private List<BranchInformation> getVisibleBranchesWithStock(List<BranchInformation> branchInfos, APIGetProductDetail.ProductInformation productInfo, Integer modelId) {
        // Get a list of branch IDs from the branch information
        List<Integer> branchIds = getBranchIds(branchInfos);

        // Count branches that are visible and have stock
        return IntStream.range(0, branchIds.size())
                .filter(branchIndex -> isBranchVisibleWithStock(branchInfos, branchIndex, productInfo, modelId, branchIds.get(branchIndex)))
                .mapToObj(branchInfos::get)
                .toList();
    }

    /**
     * Helper method to check if a branch is visible on the storefront and has stock available.
     *
     * @param branchInfos A list of branch information used to determine visibility.
     * @param branchIndex The index of the branch to check.
     * @param productInfo The product information, including stock details.
     * @param modelId     The identifier for the product model.
     * @param branchId    The identifier for the branch whose stock is being checked.
     * @return true if the branch is visible and has stock, false otherwise.
     */
    private boolean isBranchVisibleWithStock(List<BranchInformation> branchInfos, int branchIndex, APIGetProductDetail.ProductInformation productInfo, Integer modelId, int branchId) {
        // Check if the branch is shown on the storefront
        boolean isShownOnStorefront = isBranchShownOnStorefront(branchInfos, branchIndex);

        // Check if the branch has stock for the given model
        boolean hasStock = APIGetProductDetail.getStockByModelAndBranch(productInfo, modelId, branchId) > 0;

        return isShownOnStorefront && hasStock;
    }

    /**
     * Validates the information for all product variations, including price, discounts, and attributes.
     * The function loops through each variation and ensures the product is in 'ACTIVE' status, validates the
     * variation and branch information, and checks for appropriate discounts and pricing.
     *
     * @param language   the language for the product details
     * @param customerId the ID of the customer
     */
    private void validateAllVariationsInformation(String language, int customerId) {
        // Retrieve the list of model IDs for the variations, or null if the product has no variations
        List<Integer> modelIds = productInfo.isHasModel()
                ? APIGetProductDetail.getVariationModelList(productInfo)
                : Collections.singletonList(null);

        // Iterate through all model IDs (or a single null value if no variations exist)
        for (Integer modelId : modelIds) {
            // Get the index of the current variation model
            int variationIndex = modelIds.indexOf(modelId);

            // Check if the product or variation is in 'ACTIVE' status
            if ((productInfo.isHasModel() && APIGetProductDetail.getVariationStatus(productInfo, variationIndex).equals("ACTIVE"))
                || productInfo.getBhStatus().equals("ACTIVE")) {

                String variationValue = "";

                // If the product has variations, select the variation and display its value
                if (productInfo.isHasModel()) {
                    variationIndex = 2;
                    variationValue = APIGetProductDetail.getVariationValue(productInfo, language, variationIndex);
                    var varNames = variationValue.split("\\|");

                    // Log the variation value
                    logger.info("*** var: {} ***", variationValue);

                    // Select the variation value from the dropdown options
                    selectVariation(varNames);
                }

                // Validate the variation's information
                validateVariationInformation(variationIndex, customerId, variationValue);
            }
        }
    }

    private String getSelectedVariation() {
        // Open cart popup to verify the variation is selected
        iosUtils.click(loc_btnAddToCart);
        String selectedVariation = iosUtils.getText(loc_lblCartVariations);
        logger.info("Selected variation: {}", selectedVariation);
        iosUtils.click(loc_btnCloseCart);

        return selectedVariation;
    }

    /**
     * Selects the specified variation from the dropdown based on the variation name and index.
     * <p>
     * This method ensures that the correct variation is chosen from the UI dropdown for further validation.
     *
     * @param variationNames the name of the variation to select
     */
    private void selectVariation(String[] variationNames) {
        // Loop through each variation and select it if not already selected
        for (String varName : variationNames) {
            logger.info("Selected variation: {}.", varName);

            WebUtils.retryUntil(5, 3000, "Can not select '%s' variation.".formatted(varName),
                    () -> getSelectedVariation().contains(varName),
                    () -> {
                        iosUtils.swipeToElement(loc_ddvVariationValue(varName));
                        iosUtils.click(loc_ddvVariationValue(varName));
                    });
        }
    }

    private final String langKey = PropertiesUtils.getLangKey();

    /**
     * Navigates to the product detail page by constructing the SEO or default URL based on the language
     * and product information. The page is then refreshed to ensure all content is loaded properly.
     *
     * @param credentials the credentials required to authenticate API calls
     * @param productId   the ID of the product for which the detail page is accessed
     * @return ProductDetailPage instance to allow method chaining
     */
    public IOSBuyerProductDetailScreen navigateProductDetailPage(APISellerLogin.Credentials credentials, int productId) {
        // Relaunch app to load new product information
        iosUtils.relaunchApp();
        logger.info("Start the process of checking product information");

        // Store the credentials for API requests
        this.credentials = credentials;

        // Fetch product information using the product ID
        this.productInfo = new APIGetProductDetail(credentials).getProductInformation(productId);

        // Get the latest branch information from the API
        branchInfos = new APIGetBranchList(credentials).getBranchInformation();

        boolean shouldAccessProductDetail = shouldAccessProductDetail(productInfo);

        if (!shouldAccessProductDetail) {
            logger.info("Product must hide on buyer app");
            return this;
        }

        // Navigate to the product detail page using the constructed URL
        new IOSBuyerHomeScreen(driver).navigateToProductDetailPage(productInfo);

        logger.info("Navigate to Product detail screen by URL, id: {}", productId);

        WebUtils.sleep(1000);

        // Return the current instance of ProductDetailPage for method chaining
        return this;
    }

    public void test() {
        iosUtils.getElement(loc_lblProductName(productInfo.getName()));
    }

    /**
     * Access product detail on SF by URL and check product information.
     */
    public void verifyProductInformation(int customerId) {
        if (!shouldAccessProductDetail(productInfo)) {
            logger.info("Product is not shown on buyer app, so SKIP verify that information.");
            return;
        }

        validateAllVariationsInformation(langKey, customerId);
    }

    /**
     * Determine if the product detail should be accessed.
     */
    private boolean shouldAccessProductDetail(APIGetProductDetail.ProductInformation productInfo) {
        return !productInfo.isDeleted()
               && productInfo.isOnApp()
               && productInfo.getBhStatus().equals("ACTIVE")
               && (APIGetProductDetail.isProductInStock(productInfo) || productInfo.isShowOutOfStock());
    }
}
