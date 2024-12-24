package pages.web.buyer.detail_product;


import api.buyer.product.APIGetCampaignInformation;
import api.buyer.product.APIGetCampaignInformation.CampaignInformation;
import api.buyer.product.APIGetFlashSaleInformation;
import api.buyer.product.APIGetFlashSaleInformation.FlashSaleInformation;
import api.buyer.product.APIGetWholesaleInformation;
import api.buyer.product.APIGetWholesaleInformation.WholesaleInformation;
import api.seller.login.APISellerLogin;
import api.seller.product.APIGetProductDetail;
import api.seller.product.APIGetProductDetail.ProductInformation;
import api.seller.sale_channel.APIGetPreferences;
import api.seller.setting.APIGetBranchList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.testng.Assert;
import utility.PropertiesUtils;
import utility.WebUtils;


import java.util.*;
import java.util.stream.IntStream;

import static api.seller.setting.APIGetBranchList.*;

/**
 * Represents the product detail page in the web application.
 * This class provides methods to navigate to the product detail page,
 * interact with product information, and validate product attributes,
 * variations, and related elements.
 */
public class ProductDetailPage {
    // WebDriver instance for interacting with the browser
    private final WebDriver driver;

    // Logger instance for logging relevant information and events
    private final Logger logger = LogManager.getLogger();

    // Utility class instance for common WebDriver actions
    private final WebUtils webUtils;

    // Information about the product retrieved via the API
    private ProductInformation productInfo;

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
    public ProductDetailPage(WebDriver driver) {
        // Assign the WebDriver instance passed to the class
        this.driver = driver;

        // Initialize WebUtils for common WebDriver interactions
        webUtils = new WebUtils(driver);
    }

    // Locators
    private final By loc_lblProductName = By.cssSelector("[rv-text='models.productName']");
    private final By loc_lblSellingPrice = By.cssSelector(".price-disc");
    private final By loc_lblListingPrice = By.cssSelector(".price-org");
    private final By loc_lblVariationName = By.cssSelector("span[rv-text='variation.label']");
    private  By loc_ddvVariationName(int variationGroupIndex) { return By.cssSelector("[aria-owns='bs-select-%s']".formatted(variationGroupIndex)); }
    private By loc_ddvVariationValue(String variationValue) { return By.xpath("//span[contains(text(), '%s ') or text() = '%s']".formatted(variationValue, variationValue)); }
    private By loc_ddvSelectedVariationValue(int variationGroupIndex) { return By.cssSelector("[aria-owns='bs-select-%s']".formatted(variationGroupIndex));}
    private final By loc_lblBranchStock = By.cssSelector("#branch-list .stock");
    private final By loc_pnlDescription = By.cssSelector("#product-description");
    private final By loc_lblSoldOut = By.cssSelector(".sold-out");
    private final By loc_lblBranchName = By.cssSelector(".info .name");
    private final By loc_txtQuantity = By.cssSelector("[name = 'quantity']");
    private final By loc_lblFlashSale = By.cssSelector(".flash-sale");
    private final By loc_chkBuyInBulk = By.cssSelector(".buy-in-bulk__checkbox");
    private final By loc_pnlWholesalePricing = By.cssSelector(".product-wholesale-pricing");
    private final By loc_cntAttributeGroup = By.cssSelector("[rv-text='attribute.attributeName']");
    private final By loc_cntAttributeValue = By.cssSelector("[rv-text='attribute.attributeValue']");
    private final By loc_btnViewMore = By.cssSelector(".btn-view-more");
    private final By loc_btnBuyNow = By.cssSelector("#button-buy-now");
    private final By loc_btnAddToCart = By.cssSelector("#button-add-to-cart");
    private final By loc_spnLoading = By.cssSelector(".loader");
    private final By loc_icnFilterBranch = By.cssSelector("#locationCode");
    private final By loc_icnSearchBranch = By.cssSelector(".input-search-branch");
    private final By loc_seoTitle = By.cssSelector("meta[name='title']");
    private final By loc_seoDescription = By.cssSelector("meta[name='description']");
    private final By loc_seoKeyword = By.cssSelector("meta[name='keywords']");
    private final By loc_seoURL = By.cssSelector("meta[name='og:url']");

    /**
     * Validates SEO metadata against the product information for the specified language.
     *
     * @param language The language to check the SEO metadata against.
     */
    private void validateSeoMetadata(String language) {
        // Retrieve the main language from product info
        var mainLanguage = productInfo.getLanguages()
                .parallelStream()
                .filter(lang -> lang.getLanguage().equals(language))
                .findAny()
                .orElse(null);

        // Validate SEO title
        if (mainLanguage != null && mainLanguage.getSeoTitle() != null) {
            String actualSeoTitle = webUtils.getAttribute(loc_seoTitle, "content");
            String expectedSeoTitle = mainLanguage.getSeoTitle();
            Assert.assertEquals(actualSeoTitle, expectedSeoTitle, "SEO title should be '%s', but found '%s'.".formatted(expectedSeoTitle, actualSeoTitle));
            logger.info("[{}] Checked SEO title", language);
        }

        // Validate SEO description
        if (mainLanguage != null && mainLanguage.getSeoDescription() != null) {
            String actualSeoDescription = webUtils.getAttribute(loc_seoDescription, "content");
            String expectedSeoDescription = mainLanguage.getSeoDescription();
            Assert.assertEquals(actualSeoDescription, expectedSeoDescription, "SEO description should be '%s', but found '%s'.".formatted(expectedSeoDescription, actualSeoDescription));
            logger.info("[{}] Checked SEO description", language);
        }

        // Validate SEO keywords
        if (mainLanguage != null && mainLanguage.getSeoKeywords() != null) {
            String actualSeoKeywords = webUtils.getAttribute(loc_seoKeyword, "content");
            String expectedSeoKeywords = mainLanguage.getSeoKeywords();
            Assert.assertEquals(actualSeoKeywords, expectedSeoKeywords, "SEO keywords should be '%s', but found '%s'.".formatted(expectedSeoKeywords, actualSeoKeywords));
            logger.info("[{}] Checked SEO keywords", language);
        }

        // Validate SEO URL
        if (mainLanguage != null && mainLanguage.getSeoUrl() != null) {
            String actualSeoUrl = webUtils.getAttribute(loc_seoURL, "content");
            String expectedSeoUrl = mainLanguage.getSeoUrl();
            Assert.assertTrue(actualSeoUrl.contains(expectedSeoUrl), "SEO URL should contain '%s', but found '%s'.".formatted(expectedSeoUrl, actualSeoUrl));
            logger.info("[{}] Checked SEO URL", language);
        }
    }

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
        String actualProductName = webUtils.getText(loc_lblProductName);

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
                long actualListingPrice = Long.parseLong(webUtils.getText(loc_lblListingPrice).replaceAll("\\D+", ""));
                Assert.assertEquals(actualListingPrice, expectedListingPrice, "%s Listing price should be %,d, but found %,d.".formatted(branchInfo, expectedListingPrice, actualListingPrice));
            } else {
                logger.info("No discount product (listing price = selling price)");
            }

            long actualSellingPrice = Long.parseLong(webUtils.getText(loc_lblSellingPrice).replaceAll("\\D+", ""));
            Assert.assertTrue(Math.abs(actualSellingPrice - expectedSellingPrice) <= 1, "%s Selling price should be approximately %,d ±1, but found %,d.".formatted(branchInfo, expectedSellingPrice, actualSellingPrice));
            logger.info("{} Checked product prices and store currency.", branchInfo);
        } else {
            logger.info("{} Website listing is enabled, so listing/selling price is hidden.", branchInfo);
        }
    }

    // Check if the flash sale badge is displayed
    private void validateFlashSaleDisplay(String branchName) {
        String branchInfo = "[Branch name: %s]".formatted(branchName);
        Assert.assertFalse(webUtils.getListElement(loc_lblFlashSale).isEmpty(), "%s Flash sale badge is not displayed.".formatted(branchInfo));
        logger.info("{} Checked flash sale badge display.", branchInfo);
    }

    // Check if the discount campaign is displayed
    private void validateDiscountCampaignDisplay(String branchName) {
        String branchInfo = "[Branch name: %s]".formatted(branchName);

        if (!webUtils.getListElement(loc_chkBuyInBulk).isEmpty()) {
            // Check if the buy in bulk checkbox is unchecked and click if so
            if (webUtils.getAttribute(loc_chkBuyInBulk, "class").contains("unchecked")) {
                webUtils.clickJS(loc_chkBuyInBulk);
            }

            // Wait for the page to load
            try {
                webUtils.waitInvisibilityOfElementLocated(loc_spnLoading);
                logger.info("Waited for the page to load after applying the discount campaign.");
            } catch (TimeoutException ex) {
                logger.info(ex);
                webUtils.waitInvisibilityOfElementLocated(loc_spnLoading);
                logger.info("Waited again for the page to load after applying the discount campaign.");
            }
        }

        Assert.assertFalse(webUtils.getListElement(loc_chkBuyInBulk).isEmpty(), "%s Discount campaign is not displayed.".formatted(branchInfo));
        logger.info("{} Checked discount campaign display.", branchInfo);
    }

    // Check if wholesale product pricing is displayed
    private void validateWholesalePricingDisplay(String branchName) {
        String branchInfo = "[Branch name: %s]".formatted(branchName);
        Assert.assertFalse(webUtils.getListElement(loc_pnlWholesalePricing).isEmpty(), "%s Wholesale product information is not displayed.".formatted(branchInfo));
        logger.info("{} Checked wholesale product information display.", branchInfo);
    }

    // Validate product attributes
    private void validateProductAttributes(List<Boolean> displayAttributes, List<String> attributeGroups, List<String> attributeValues, String variationName) {
        String variationInfo = !variationName.isEmpty() ? "[Variation: %s]".formatted(variationName) : "";

        if (displayAttributes.stream().anyMatch(Boolean::booleanValue)) {
            // If the product has more than 3 attributes, click to view more
            if (Collections.frequency(displayAttributes, true) > 3) {
                webUtils.clickJS(loc_btnViewMore);
            }

            // Validate each attribute
            int storefrontAttributeIndex = 0;
            for (int attributeIndex = 0; attributeIndex < attributeGroups.size(); attributeIndex++) {
                if (displayAttributes.get(attributeIndex)) {
                    // Validate attribute name
                    String actualAttributeName = webUtils.getText(loc_cntAttributeGroup);
                    Assert.assertEquals(actualAttributeName, attributeGroups.get(attributeIndex),
                            "Attribute name must be '%s', but found '%s'.".formatted(attributeGroups.get(attributeIndex), actualAttributeName));

                    // Validate attribute value
                    String actualAttributeValue = webUtils.getText(loc_cntAttributeValue);
                    Assert.assertEquals(actualAttributeValue, attributeValues.get(attributeIndex),
                            "Attribute value must be '%s', but found '%s'.".formatted(attributeValues.get(attributeIndex), actualAttributeValue));

                    storefrontAttributeIndex++;
                }
            }
        }

        // Log attribute check
        logger.info("{} Checked product attributes.", variationInfo);
    }

    /**
     * Validates the variation names displayed in the storefront against the database values.
     *
     * @param language The language to check the variation names in.
     */
    private void validateVariationNames(String language) {
        // Retrieve the variation name list from the dashboard
        List<String> expectedVariationNames = Arrays.stream(APIGetProductDetail.getVariationName(productInfo, language).split("\\|")).toList();
        List<WebElement> variationElements = webUtils.getListElement(loc_lblVariationName);
        List<String> actualVariationNames = variationElements.stream().map(WebElement::getText).toList();

        Assert.assertEquals(expectedVariationNames.size(), actualVariationNames.size(),
                "Variation name count does not match. Expected: %d, Found: %d.".formatted(expectedVariationNames.size(), actualVariationNames.size()));

        for (int index = 0; index < expectedVariationNames.size(); index++) {
            Assert.assertEquals(expectedVariationNames.get(index).toLowerCase(),
                    actualVariationNames.get(index).toLowerCase(),
                    "Variation name at index %d should be '%s', but found '%s'.".formatted(index, expectedVariationNames.get(index), actualVariationNames.get(index)));
        }

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

        // Assert filter branch visibility
        boolean isFilterVisible = !webUtils.getListElement(loc_icnFilterBranch).isEmpty();
        Assert.assertEquals(isFilterVisible, shouldBeVisible,
                "%s 'Filter dropdown' should be %s but it is %s.".formatted(varName, visibilityCheck, isFilterVisible ? "shown" : "hidden"));
        logger.info("{} Checking if 'Filter dropdown' is {}.", varName, visibilityCheck);

        // Assert search branch visibility
        boolean isSearchVisible = !webUtils.getListElement(loc_icnSearchBranch).isEmpty();
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
     * @param branchIndex   the index of the branch in the list
     * @param isVisible     the visibility status of the branch
     * @param expectedStock the expected stock count of the branch
     * @param variationName the name of the variation being checked
     */
    private void validateBranchStock(String branchName, int branchIndex, boolean isVisible, int expectedStock, String variationName) {
        String varName = !variationName.isEmpty() ? "[Variation: %s]".formatted(variationName) : "";
        if (!productInfo.getIsHideStock() && isVisible) {
            String actualStockText = webUtils.getText(loc_lblBranchStock);
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

        // Retrieve storefront product description
        String sfDescription = webUtils.getText(loc_pnlDescription).replaceAll("\n", "");

        // Assert descriptions match
        Assert.assertEquals(dbDescription, sfDescription,
                "[Check description] Product description should be '%s', but found '%s'".formatted(dbDescription, sfDescription));
        logger.info("[Check description] Product description is shown correctly.");
    }

    /**
     * Checks if the 'Buy Now' and 'Add to Cart' buttons are either visible or hidden.
     *
     * @param variationName   the name of the variation being checked
     * @param shouldBeVisible true if the buttons should be visible, false if they should be hidden
     */
    private void verifyBuyNowAndAddToCartButtonsVisibility(String variationName, boolean shouldBeVisible) {
        String varName = !variationName.isEmpty() ? "[Variation: %s]".formatted(variationName) : "";

        // Define expected state as visible or hidden
        String visibilityCheck = shouldBeVisible ? "shown" : "hidden";

        // If the product or store listing is not enabled, check for button visibility
        if (!(new APIGetPreferences(credentials).getStoreListingWebInformation().isEnabledProduct() && productInfo.isEnabledListing())) {
            // Assert 'Buy Now' button visibility
            boolean isBuyNowVisible = !webUtils.getListElement(loc_btnBuyNow).isEmpty();
            Assert.assertEquals(isBuyNowVisible, shouldBeVisible,
                    "%s 'Buy now' button should be %s but it is %s.".formatted(varName, visibilityCheck, isBuyNowVisible ? "shown" : "hidden"));
            logger.info("{} Checking if 'Buy Now' button is {}.", varName, visibilityCheck);

            // Assert 'Add to Cart' button visibility
            boolean isAddToCartVisible = !webUtils.getListElement(loc_btnAddToCart).isEmpty();
            Assert.assertEquals(isAddToCartVisible, shouldBeVisible,
                    "%s 'Add to cart' button should be %s but it is %s.".formatted(varName, visibilityCheck, isAddToCartVisible ? "shown" : "hidden"));
            logger.info("{} Checking if 'Add to Cart' button is {}.", varName, visibilityCheck);
        }
    }

    /**
     * Checks if the 'Sold Out' mark is displayed correctly.
     *
     * @param variationName the name of the variation being checked
     */
    private void verifySoldOutMarkDisplayed(String variationName) {
        String varName = !variationName.isEmpty() ? "[Variation: %s]".formatted(variationName) : "";
        boolean isSoldOut = webUtils.getText(loc_lblSoldOut).equals("Hết hàng") || webUtils.getText(loc_lblSoldOut).equals("Out of stock");

        // Assert sold out mark is visible
        Assert.assertTrue(isSoldOut, "%s Sold out mark does not show".formatted(varName));
        logger.info("{} Checking if 'SOLD OUT' mark is shown", varName);
    }

    /**
     * Verifies that a 404 page is displayed when the product is out of stock.
     */
    private void verify404PageDisplayed() {
        Assert.assertTrue(driver.getCurrentUrl().contains("404"), "404 is not shown although product is out of stock.");
        logger.info("Checking if 404 page is shown when product is out of stock.");
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
        CampaignInformation campaignInfo = fetchCampaignInfo(itemId, branchId, customerId);
        WholesaleInformation wholesaleInfo = fetchWholesaleInfo(itemId, customerId, variationIndex);
        FlashSaleInformation flashSaleInfo = fetchFlashSaleInfo(itemId, variationIndex);

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
    private CampaignInformation fetchCampaignInfo(int itemId, int branchId, int customerId) {
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
    private WholesaleInformation fetchWholesaleInfo(int itemId, int customerId, int variationIndex) {
        return new APIGetWholesaleInformation(credentials).getWholesaleInformation(itemId, customerId, fetchModelId(variationIndex));
    }

    /**
     * Fetches flash sale information for the specified item and variation model.
     *
     * @param itemId         the ID of the item
     * @param variationIndex the index of the variation model
     * @return the flash sale information, or null if no flash sale is active
     */
    private FlashSaleInformation fetchFlashSaleInfo(int itemId, int variationIndex) {
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
    private boolean isFlashSaleActive(FlashSaleInformation flashSaleInfo) {
        return flashSaleInfo != null && flashSaleInfo.getStatus().equals("IN_PROGRESS");
    }


    /**
     * Displays flash sale information and verifies the prices.
     */
    private void displayFlashSaleInfo(FlashSaleInformation flashSaleInfo, long listingPrice, String brName) {
        validateFlashSaleDisplay(brName);
        logger.info("PRICE: FLASH SALE");
        validateBranchPrices(listingPrice, flashSaleInfo.getItems().getFirst().getNewPrice(), brName);
    }

    /**
     * Displays campaign information and verifies the prices.
     */
    private void displayCampaignInfo(CampaignInformation campaignInfo, long listingPrice, long sellingPrice, String brName) {
        validateDiscountCampaignDisplay(brName);
        logger.info("PRICE: DISCOUNT CAMPAIGN");
        long newPrice = calculateCampaignPrice(campaignInfo, sellingPrice);
        validateBranchPrices(listingPrice, newPrice, brName);
    }

    /**
     * Displays wholesale information and verifies the prices.
     */
    private void displayWholesaleInfo(WholesaleInformation wholesaleInfo, long listingPrice, String brName) {
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
    private long calculateCampaignPrice(CampaignInformation campaignInfo, long sellingPrice) {
        String discountType = campaignInfo.getWholesales().getFirst().getType();
        long discountValue = campaignInfo.getWholesales().getFirst().getWholesaleValue();
        return discountType.equals("FIXED_AMOUNT")
                ? Math.max(sellingPrice - discountValue, 0)
                : (sellingPrice * (100 - discountValue)) / 100;
    }

    /**
     * Adjusts the quantity input field based on the wholesale minimum quantity.
     */
    private void adjustQuantityForWholesale(WholesaleInformation wholesaleInfo) {
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
            webUtils.sendKeys(loc_txtQuantity, String.valueOf(minQuantity));

            if (webUtils.getValue(loc_txtQuantity).equals(String.valueOf(minQuantity))) {
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
            webUtils.waitInvisibilityOfElementLocated(loc_spnLoading);
            logger.info("Wait for the page to load after applying wholesale discount.");
        } catch (TimeoutException ex) {
            logger.warn("Timeout while waiting for the page to load: {}", ex.getMessage());
            webUtils.waitInvisibilityOfElementLocated(loc_spnLoading);
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
        validateBasicInformation(modelId, variationIndex, variationName);

        // Retrieve the list of branch IDs for the product.
        List<Integer> branchIds = getBranchIds(branchInfos);

        // Count the number of branches that have stock and are visible on the storefront.
        int numberOfDisplayBranches = countVisibleBranchesWithStock(branchInfos, productInfo, modelId);

        // If there are branches with stock and visible on the storefront, validate each branch's information.
        if (numberOfDisplayBranches > 0) {
            // Validate branch stock, pricing, and other details for branches with stock.
            verifyBranchStockNameAndPrice(variationName, numberOfDisplayBranches, branchIds, variationIndex, customerId);
        } else {
            // Handle cases where no branches have stock by verifying the Sold Out status and updating UI elements.
            handleOutOfStockBranches(variationName);
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
     * @param modelId        the model ID of the variation
     * @param variationIndex the index of the variation being validated
     * @param variationName  the name of the variation being validated
     */
    private void validateBasicInformation(Integer modelId, int variationIndex, String variationName) {
        // Validate product name
        validateProductName(modelId, langKey);

        // Validate product attributes
        List<Boolean> isDisplayAttribute = APIGetProductDetail.getDisplayAttributesStatus(productInfo, variationIndex);
        List<String> attributeNames = APIGetProductDetail.getAttributeNames(productInfo, variationIndex);
        List<String> attributeValues = APIGetProductDetail.getAttributeValues(productInfo, variationIndex);
        validateProductAttributes(isDisplayAttribute, attributeNames, attributeValues, variationName);

        // Validate variation names if the product has multiple models
        if (productInfo.isHasModel()) {
            validateVariationNames(langKey);
        }

        // Validate product description
        validateProductDescription(modelId, langKey);
    }

    /**
     * Validates the branches that have stock available and are visible on the storefront.
     *
     * @param variationName         the name of the variation being validated
     * @param numberOfDisplayBranches the number of branches with visible stock
     * @param branchIds             the list of branch IDs
     * @param variationIndex        the index of the variation being validated
     * @param customerId            the ID of the customer for pricing and discounts
     */
    private void verifyBranchStockNameAndPrice(String variationName, int numberOfDisplayBranches, List<Integer> branchIds, int variationIndex, int customerId) {
        // Verify branch filter and Buy Now/Add to Cart button visibility
        verifyFilterAndSearchBranchesVisibility(variationName, numberOfDisplayBranches >= 6);
        verifyBuyNowAndAddToCartButtonsVisibility(variationName, true);

        // Validate stock and pricing for each visible branch
        IntStream.range(0, numberOfDisplayBranches).forEach(brElementIndex -> {
            String branchName = switchBranchAndGetBranchName(brElementIndex);
            int branchIndex = getBranchIndexByName(branchName);
            int branchId = branchIds.get(branchIndex);

            validateBranchInfo(branchName, brElementIndex, branchIndex, branchId, variationIndex, customerId, variationName);
        });
    }

    /**
     * Switches the storefront branch and retrieves its name.
     *
     * @param brElementIndex the index of the branch element to switch to
     * @return the name of the switched branch
     */
    private String switchBranchAndGetBranchName(int brElementIndex) {
        webUtils.clickJS(loc_lblBranchName, brElementIndex);
        return webUtils.getText(loc_lblBranchName);
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
     * @param branchName    the name of the branch
     * @param brElementIndex the index of the branch element on the storefront
     * @param branchIndex   the index of the branch in the API data
     * @param branchId      the ID of the branch
     * @param variationIndex the index of the variation being validated
     * @param customerId    the ID of the customer
     * @param variationName the name of the variation
     */
    private void validateBranchInfo(String branchName, int brElementIndex, int branchIndex, int branchId, int variationIndex, int customerId, String variationName) {
        boolean isBranchShown = isBranchShownOnStorefront(branchInfos, branchIndex);
        int expectedStock = APIGetProductDetail.getStockByModelAndBranch(productInfo, fetchModelId(variationIndex), branchId);

        validateBranchStock(branchName, brElementIndex, isBranchShown, expectedStock, variationName);
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
        verifyBuyNowAndAddToCartButtonsVisibility(variationName, false);
    }

    /**
     * Counts the number of branches that are both visible on the storefront
     * and have stock available for a given product model.
     *
     * @param branchInfos A list of branch information used to determine visibility and stock.
     * @param productInfo The product information, including stock availability per model and branch.
     * @param modelId The identifier for the product model whose stock is being checked.
     * @return The number of branches with stock and visible on the storefront.
     */
    private int countVisibleBranchesWithStock(List<BranchInformation> branchInfos, ProductInformation productInfo, Integer modelId) {
        // Get a list of branch IDs from the branch information
        List<Integer> branchIds = getBranchIds(branchInfos);

        // Count branches that are visible and have stock
        return (int) IntStream.range(0, branchIds.size())
                .filter(branchIndex -> isBranchVisibleWithStock(branchInfos, branchIndex, productInfo, modelId, branchIds.get(branchIndex)))
                .count();
    }

    /**
     * Helper method to check if a branch is visible on the storefront and has stock available.
     *
     * @param branchInfos A list of branch information used to determine visibility.
     * @param branchIndex The index of the branch to check.
     * @param productInfo The product information, including stock details.
     * @param modelId The identifier for the product model.
     * @param branchId The identifier for the branch whose stock is being checked.
     * @return true if the branch is visible and has stock, false otherwise.
     */
    private boolean isBranchVisibleWithStock(List<BranchInformation> branchInfos, int branchIndex, ProductInformation productInfo, Integer modelId, int branchId) {
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
                    varNames.forEach(var -> selectVariation(var, varNames.indexOf(var)));

                    // Wait for the page to load after selecting the variation
                    waitForPageLoad();
                }

                // Validate the variation's information
                validateVariationInformation(variationIndex, customerId , variationValue);
            }

            // Refresh the page before moving to the next variation
            driver.navigate().refresh();
        }
    }

    /**
     * Selects the specified variation from the dropdown based on the variation name and index.
     * <p>
     * This method ensures that the correct variation is chosen from the UI dropdown for further validation.
     *
     * @param variation the name of the variation to select
     * @param index     the index of the variation in the dropdown list
     */
    private void selectVariation(String variation, int index) {
        // Click the dropdown corresponding to the variation's index
        webUtils.clickJS(loc_ddvVariationName(index + 1));
        logger.info("Opened variation dropdown {}.", index);

        // Select the variation value from the dropdown options
        webUtils.clickJS(loc_ddvVariationValue(variation));
        logger.info("Selected variation: {}.", variation);

        // Verify that the variation was correctly selected by comparing the title attribute
        Assert.assertEquals(webUtils.getAttribute(loc_ddvSelectedVariationValue(index + 1), "title"),
                variation, "Cannot select variation: %s.".formatted(variation));
    }

    private String langKey;
    /**
     * Navigates to the product detail page by constructing the SEO or default URL based on the language
     * and product information. The page is then refreshed to ensure all content is loaded properly.
     *
     * @param credentials the credentials required to authenticate API calls
     * @param productId   the ID of the product for which the detail page is accessed
     * @return ProductDetailPage instance to allow method chaining
     */
    public ProductDetailPage navigateProductDetailPage(APISellerLogin.Credentials credentials, int productId) {
        logger.info("===== STEP =====> [CheckProductDetail] START...");

        // Store the credentials for API requests
        this.credentials = credentials;

        // Fetch product information using the product ID
        this.productInfo = new APIGetProductDetail(credentials).getProductInformation(productId);

        // Get the latest branch information from the API
        branchInfos = new APIGetBranchList(credentials).getBranchInformation();

        // Navigate to the product detail page using the constructed URL
        driver.get("%s/product/%d".formatted(PropertiesUtils.getStoreURL(), productInfo.getId()));

        // Refresh the page to ensure the latest content is loaded
        driver.navigate().refresh();

        // SKIP SEO URL ISSUE FOR TEST
        this.langKey = webUtils.getCookieValue("langKey");

        logger.info("Navigate to Product detail page by URL, id: {}", productId);

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
        boolean isProductInStock = APIGetProductDetail.isProductInStock(productInfo);

        if (shouldAccessProductDetail(productInfo, isProductInStock)) {
            verifyProductDetailsOnPage(langKey, customerId);
        } else {
            verify404PageDisplayed();
        }

        logger.info("===== STEP =====> [CheckProductDetail] DONE!!!");
    }

    /**
     * Verify the product information on the page.
     */
    private void verifyProductDetailsOnPage(String languageCode, int customerId) {
        validateSeoMetadata(languageCode);
        validateAllVariationsInformation(languageCode, customerId);
    }

    /**
     * Determine if the product detail should be accessed.
     */
    private boolean shouldAccessProductDetail(ProductInformation productInfo, boolean isProductInStock) {
        return !productInfo.isDeleted()
               && productInfo.isOnWeb()
               && productInfo.getBhStatus().equals("ACTIVE")
               && (isProductInStock || productInfo.isShowOutOfStock());
    }
}

