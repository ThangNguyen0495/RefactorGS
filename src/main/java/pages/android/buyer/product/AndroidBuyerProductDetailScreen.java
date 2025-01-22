package pages.android.buyer.product;

import api.buyer.product.APIGetCampaignInformation;
import api.buyer.product.APIGetFlashSaleInformation;
import api.buyer.product.APIGetWholesaleInformation;
import api.seller.login.APISellerLogin;
import api.seller.product.APIGetProductDetail;
import api.seller.sale_channel.APIGetPreferences;
import api.seller.setting.APIGetBranchList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import pages.android.buyer.home.AndroidBuyerHomeScreen;
import utility.AndroidUtils;
import utility.PropertiesUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static api.seller.setting.APIGetBranchList.*;
import static utility.AndroidUtils.*;

public class AndroidBuyerProductDetailScreen {
    // WebDriver instance for interacting with the browser
    private final WebDriver driver;

    // Logger instance for logging relevant information and events
    private final Logger logger = LogManager.getLogger();

    // Utility class instance for common WebDriver actions
    private final AndroidUtils androidUtils;

    // Information about the product retrieved via the API
    private APIGetProductDetail.ProductInformation productInfo;

    // List containing branch information fetched from the API
    private List<APIGetBranchList.BranchInformation> branchInfos;

    // Credentials used for API authentication
    private APISellerLogin.Credentials credentials;

    /**
     * Constructor for ProductDetailPage.
     * Initializes the WebDriver instance and common utilities required for page interactions.
     *
     * @param driver the WebDriver instance for interacting with the browser
     */
    public AndroidBuyerProductDetailScreen(WebDriver driver) {
        // Assign the WebDriver instance passed to the class
        this.driver = driver;

        // Initialize WebUtils for common WebDriver interactions
        androidUtils = new AndroidUtils(driver);
    }

    // Locators
    private final By loc_lblProductName = getBuyerLocatorByResourceId("%s:id/item_market_product_detail_desc_title");
    private final By loc_lblSellingPrice = getBuyerLocatorByResourceId("%s:id/item_market_product_detail_desc_promotion_price");
    private final By loc_lblListingPrice = getBuyerLocatorByResourceId("%s:id/item_market_product_detail_desc_original_price");

    private By loc_ddvVariationName(int variationGroupIndex) {
        return getBuyerLocatorByResourceId("%s" + ":id/item_market_product_detail_desc_tv_variation_%s_label".formatted(variationGroupIndex));
    }

    private By loc_ddvVariationValue(String variationValue) {
        return getLocatorByText(variationValue);
    }

    private By loc_lblBranchStock(String branchName) {
        return getLocatorByPartialText(branchName);
    }

    // Using this to scroll to description section
    private final By loc_sctDescription = getBuyerLocatorByResourceId("%s:id/llWebViewContainer");
    private final By loc_cntDescription = By.xpath("//android.widget.TextView");
    private final By loc_lblSoldOut = getBuyerLocatorByResourceId("%s:id/activity_item_details_tv_not_available");
    private final By loc_txtQuantity = getBuyerLocatorByResourceId("%s:id/product_detail_content_popup_variation_edt_quantity");
    private final By loc_lblFlashSale = getBuyerLocatorByResourceId("%s:id/rlFlashSaleContainer");
    private final By loc_chkBuyInBulk = getBuyerLocatorByResourceId("%s:id/product_detail_content_popup_variation_iv_check_buy_in_bulk");
    private final By loc_pnlWholesalePricing = getBuyerLocatorByResourceId("%s:id/item_market_product_detail_desc_group_wholesale_pricing");
    private final By loc_btnBuyNow = getBuyerLocatorByResourceId("%s:id/tvBuyNow");
    private final By loc_btnAddToCart = getBuyerLocatorByResourceId("%s:id/ivIconAddToCart");
    private final By loc_icnFilterBranch = getBuyerLocatorByResourceId("%s:id/iv_select_branch_filter");
    private final By loc_icnSearchBranch = getBuyerLocatorByResourceId("%s:id/iv_show_search_branch");

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

        // Retrieve the product name from the storefront
        String actualProductName = androidUtils.getText(loc_lblProductName);

        // Validate product name
        Assert.assertEquals(expectedProductName, actualProductName, "Product name should be '%s', but found '%s'.".formatted(expectedProductName, actualProductName));
        logger.info("[Validate Product Name] Product name is displayed correctly.");
    }

    /**
     * Compares the listing and selling prices for each branch against expected values.
     *
     * @param expectedListingPrice The expected listing price.
     * @param expectedSellingPrice The expected selling price.
     * @param branchName           The name of the branch.
     */
    private void validateBranchPrices(long expectedListingPrice, long expectedSellingPrice, String branchName) {
        String branchInfo = branchName.isEmpty() ? "" : "[Branch name: %s]".formatted(branchName);

        if (!(new APIGetPreferences(credentials).getStoreListingWebInformation().isEnabledProduct() && productInfo.isEnabledListing())) {
            if (expectedListingPrice != expectedSellingPrice) {
                long actualListingPrice = Long.parseLong(androidUtils.getText(loc_lblListingPrice).replaceAll("\\D+", ""));
                Assert.assertEquals(actualListingPrice, expectedListingPrice, "%s Listing price should be %,d, but found %,d.".formatted(branchInfo, expectedListingPrice, actualListingPrice));
            } else {
                logger.info("No discount product (listing price = selling price)");
            }

            long actualSellingPrice = Long.parseLong(androidUtils.getText(loc_lblSellingPrice).replaceAll("\\D+", ""));
            Assert.assertTrue(Math.abs(actualSellingPrice - expectedSellingPrice) <= 1, "%s Selling price should be approximately %,d ±1, but found %,d.".formatted(branchInfo, expectedSellingPrice, actualSellingPrice));
            logger.info("{} Checked product prices and store currency.", branchInfo);
        } else {
            logger.info("{} Website listing is enabled, so listing/selling price is hidden.", branchInfo);
        }
    }

    // Check if the flash sale badge is displayed
    private void validateFlashSaleDisplay(String branchName) {
        String branchInfo = "[Branch name: %s]".formatted(branchName);
        Assert.assertFalse(androidUtils.getListElement(loc_lblFlashSale).isEmpty(), "%s Flash sale badge is not displayed.".formatted(branchInfo));
        logger.info("{} Checked flash sale badge display.", branchInfo);
    }

    // Check if the discount campaign is displayed
    private void validateDiscountCampaignDisplay(String branchName) {
        String branchInfo = "[Branch name: %s]".formatted(branchName);

        if (!androidUtils.getListElement(loc_chkBuyInBulk).isEmpty()) {
            // Check if the buy in bulk checkbox is unchecked and click if so
            if (!androidUtils.isChecked(loc_chkBuyInBulk)) {
                androidUtils.click(loc_chkBuyInBulk);
            }
        }

        Assert.assertFalse(androidUtils.getListElement(loc_chkBuyInBulk).isEmpty(), "%s Discount campaign is not displayed.".formatted(branchInfo));
        logger.info("{} Checked discount campaign display.", branchInfo);
    }

    // Check if wholesale product pricing is displayed
    private void validateWholesalePricingDisplay(String branchName) {
        String branchInfo = "[Branch name: %s]".formatted(branchName);
        Assert.assertFalse(androidUtils.getListElement(loc_pnlWholesalePricing).isEmpty(), "%s Wholesale product information is not displayed.".formatted(branchInfo));
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
        List<String> actualVariationNames = IntStream.range(0, expectedVariationNames.size())
                .mapToObj(variationNameIndex -> androidUtils.getText(loc_ddvVariationName(variationNameIndex)))
                .toList();

        IntStream.range(0, expectedVariationNames.size())
                .forEach(index -> Assert.assertEquals(expectedVariationNames.get(index).toLowerCase(),
                        actualVariationNames.get(index).toLowerCase(),
                        "Variation name at index %d should be '%s', but found '%s'.".formatted(index, expectedVariationNames.get(index), actualVariationNames.get(index))));

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
        boolean isSearchVisible = !androidUtils.getListElement(loc_icnSearchBranch).isEmpty();
        Assert.assertEquals(isSearchVisible, shouldBeVisible,
                "%s 'Search box' should be %s but it is %s.".formatted(varName, visibilityCheck, isSearchVisible ? "shown" : "hidden"));
        logger.info("{} Checking if 'Search box' is {}.", varName, visibilityCheck);

        // Assert filter branch visibility
        if (isSearchVisible) {
            androidUtils.click(loc_icnSearchBranch);
            boolean isFilterVisible = !androidUtils.getListElement(loc_icnFilterBranch).isEmpty();
            Assert.assertEquals(isFilterVisible, shouldBeVisible,
                    "%s 'Filter dropdown' should be %s but it is %s.".formatted(varName, visibilityCheck, isFilterVisible ? "shown" : "hidden"));
            logger.info("{} Checking if 'Filter dropdown' is {}.", varName, visibilityCheck);
        }
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
            String actualStockText = androidUtils.getText(loc_lblBranchStock(branchName));
            int actualStock = Integer.parseInt(actualStockText.replaceAll("\\D+", ""));

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
        String dbDescription = productInfo.isHasModel()
                ? APIGetProductDetail.getVersionDescription(productInfo, modelId, language)
                : APIGetProductDetail.getMainProductDescription(productInfo, language);
        dbDescription = dbDescription.replaceAll("<.*?>", "").replaceAll("amp;", "");

        // Scroll to description section
        // Then retrieve storefront product description
        WebElement descriptionSection = androidUtils.getElement(loc_sctDescription);
        String sfDescription = descriptionSection.findElement(loc_cntDescription).getText().replaceAll("\n", "");

        // Assert descriptions match
        Assert.assertEquals(dbDescription, sfDescription,
                "[Check description] Product description should be '%s', but found '%s'".formatted(dbDescription, sfDescription));
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
        boolean isBuyNowVisible = !androidUtils.getListElement(loc_btnBuyNow).isEmpty();
        Assert.assertTrue(isBuyNowVisible, "%s 'Buy now' button should be %s but it is %s.".formatted(varName, visibilityCheck, isBuyNowVisible ? "shown" : "hidden"));
        logger.info("{} Checking if 'Buy Now' button is {}.", varName, visibilityCheck);

        // Assert 'Add to Cart' button visibility/invisibility
        boolean isAddToCartVisible = !androidUtils.getListElement(loc_btnAddToCart).isEmpty();
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
        boolean isSoldOut = androidUtils.getText(loc_lblSoldOut).equals("Hết hàng") || androidUtils.getText(loc_lblSoldOut).equals("Out of stock");

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

        // Determine the listing and selling price for the variation
        long listingPrice = getListingPrice(variationIndex);
        long sellingPrice = getSellingPrice(variationIndex);

        // Display appropriate pricing and discount information based on priority
        if (isFlashSaleActive(flashSaleInfo)) {
            displayFlashSaleInfo(flashSaleInfo, listingPrice, branchName);
        } else if (campaignInfo != null) {
            displayCampaignInfo(campaignInfo, listingPrice, sellingPrice, branchName);
        } else if (wholesaleInfo != null) {
            displayWholesaleInfo(wholesaleInfo, listingPrice, branchName);
        } else {
            displayRegularPrice(listingPrice, sellingPrice, branchName);
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
     * Retrieves the listing price for the variation model. If the product has no variation model,
     * the original price of the product is returned.
     *
     * @param variationIndex the index of the variation model
     * @return the listing price of the variation or the original product price if no variation exists
     */
    private long getListingPrice(int variationIndex) {
        return productInfo.isHasModel()
                ? APIGetProductDetail.getVariationListingPrice(productInfo, variationIndex)
                : productInfo.getOrgPrice();
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
    private void displayFlashSaleInfo(APIGetFlashSaleInformation.FlashSaleInformation flashSaleInfo, long listingPrice, String brName) {
        validateFlashSaleDisplay(brName);
        logger.info("PRICE: FLASH SALE");
        validateBranchPrices(listingPrice, flashSaleInfo.getItems().getFirst().getNewPrice(), brName);
    }

    /**
     * Displays campaign information and verifies the prices.
     */
    private void displayCampaignInfo(APIGetCampaignInformation.CampaignInformation campaignInfo, long listingPrice, long sellingPrice, String brName) {
        validateDiscountCampaignDisplay(brName);
        logger.info("PRICE: DISCOUNT CAMPAIGN");
        long newPrice = calculateCampaignPrice(campaignInfo, sellingPrice);
        validateBranchPrices(listingPrice, newPrice, brName);
    }

    /**
     * Displays wholesale information and verifies the prices.
     */
    private void displayWholesaleInfo(APIGetWholesaleInformation.WholesaleInformation wholesaleInfo, long listingPrice, String brName) {
        validateWholesalePricingDisplay(brName);
        logger.info("PRICE: WHOLESALE PRODUCT");
        adjustQuantityForWholesale(wholesaleInfo);
        validateBranchPrices(listingPrice, wholesaleInfo.getPrice().longValue(), brName);
    }

    /**
     * Displays the regular selling price.
     */
    private void displayRegularPrice(long listingPrice, long sellingPrice, String brName) {
        logger.info("PRICE: SELLING PRICE");
        validateBranchPrices(listingPrice, sellingPrice, brName);
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
        waitForPageLoad();
    }

    /**
     * Sends the specified quantity to the quantity input field with retry logic.
     *
     * @param minQuantity the minimum quantity to send
     * @throws IllegalStateException if unable to set the quantity after maximum retries
     */
    private void setQuantityWithRetries(int minQuantity) {
        for (int attempt = 1; attempt <= 5; attempt++) {
            androidUtils.click(loc_btnAddToCart);
            androidUtils.sendKeys(loc_txtQuantity, minQuantity);

            if (androidUtils.getText(loc_txtQuantity).equals(String.valueOf(minQuantity))) {
                return;
            }

            logger.warn("Attempt {} to set quantity failed. Retrying...", attempt);

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Thread interrupted while waiting to retry setting quantity", e);
            }
        }
        throw new IllegalStateException("Failed to set quantity after 5 attempts.");
    }

    /**
     * Waits for the page to load after applying changes.
     */
    private void waitForPageLoad() {
        try {

            logger.info("Wait for the page to load after applying wholesale discount.");
        } catch (TimeoutException ex) {
            logger.warn("Timeout while waiting for the page to load: {}", ex.getMessage());

            logger.info("Retrying to wait for page load after applying wholesale discount.");
        }
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

        // Validate basic information such as product name, attributes, and description for the variation.
        validateBasicInformation(modelId);

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
     * Validates the basic information for the variation such as product name, attributes, and description.
     *
     * @param modelId the model ID of the variation
     */
    private void validateBasicInformation(Integer modelId) {
        // Validate product name
        validateProductName(modelId, langKey);

        // Validate variation names if the product has multiple models
        if (productInfo.isHasModel()) {
            validateVariationNames(langKey);
        }

        // Validate product description
        validateProductDescription(modelId, langKey);
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

            // Switch to the branch using its name
            androidUtils.click(loc_lblBranchStock(branchName));

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
                    variationValue = APIGetProductDetail.getVariationValue(productInfo, language, variationIndex);
                    List<String> varNames = Arrays.stream(variationValue.split("\\|")).toList();

                    // Log the variation value if it's not empty
                    if (!variationValue.isEmpty()) {
                        logger.info("*** var: {} ***", variationValue);
                    }

                    // Loop through each variation and select it from the dropdown
                    varNames.forEach(this::selectVariation);

                    // Wait for the page to load after selecting the variation
                    waitForPageLoad();
                }

                // Validate the variation's information
                validateVariationInformation(variationIndex, customerId, variationValue);
            }
        }
    }

    /**
     * Selects the specified variation from the dropdown based on the variation name and index.
     * <p>
     * This method ensures that the correct variation is chosen from the UI dropdown for further validation.
     *
     * @param variation the name of the variation to select
     */
    private void selectVariation(String variation) {
        // Select the variation value from the dropdown options
        androidUtils.click(loc_ddvVariationValue(variation));
        logger.info("Selected variation: {}.", variation);
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
    public AndroidBuyerProductDetailScreen navigateProductDetailPage(APISellerLogin.Credentials credentials, int productId) {
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
        new AndroidBuyerHomeScreen(driver).navigateToProductDetailPage(productInfo);

        logger.info("Navigate to Product detail screen by URL, id: {}", productId);

        try {
            // Wait for 1 second to allow the page to fully load
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // Handle any interruptions that occur during the sleep period
            throw new RuntimeException(e);
        }

        // Return the current instance of ProductDetailPage for method chaining
        return this;
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
