package pages.android.seller.products;


import api.seller.product.APIGetProductCollections;
import api.seller.product.APIGetProductList;
import api.seller.setting.APIGetBranchList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import pages.android.seller.home.AndroidSellerHomeScreen;
import pages.android.seller.login.AndroidSellerLoginScreen;
import utility.AndroidUtils;

import java.util.List;

import static api.seller.product.APIGetProductList.ProductFilterType.*;
import static utility.AndroidUtils.*;
import static utility.WebDriverManager.appBundleId;


public class AndroidProductManagementScreen {
    WebDriver driver;
    AndroidUtils androidUtils;
    Logger logger = LogManager.getLogger();

    public AndroidProductManagementScreen(WebDriver driver) {
        // Get driver
        this.driver = driver;

        // Init commons class
        androidUtils = new AndroidUtils(driver);
    }

    By loc_txtSearchBox = getLocatorByResourceId("%s:id/edtProductSearch");
    By loc_btnSort = getLocatorByResourceId("%s:id/ivSortType");

    By loc_lstSortOptions(int index) {
        return getLocatorByResourceIdAndInstance("%s:id/tvStatus", index);
    }

    By loc_btnFilter = getLocatorByResourceId("%s:id/btnFilterProduct");

    By loc_lblProductName(String productName) {
        return By.xpath("//android.widget.TextView[@* = '%s']".formatted(productName));
    }

    By loc_lblProductName = getLocatorById("%s:id/tvProductName");

    public AndroidProductManagementScreen navigateToProductManagementScreen() {
        // Navigate to product management screen
        new AndroidSellerHomeScreen(driver).navigateToProductManagementScreen();

        // Log
        logger.info("Navigate to product management screen.");

        return this;
    }

    public void navigateToProductDetailScreen(String productName) {
        // Search product by name
        androidUtils.sendKeys(loc_txtSearchBox, productName);

        // Log
        logger.info("Search product by name: {}", productName);

        // Navigate to product detail screen
        if (!androidUtils.getListElement(loc_lblProductName(productName)).isEmpty()) {
            // Click into first result
            androidUtils.click(loc_lblProductName(productName));
        } else throw new NoSuchElementException("No result with keyword: %s".formatted(productName));
    }

    /**
     * Sorts the product list based on the specified sorting option.
     *
     * @param sortOption The sorting criteria (e.g., Stock High to Low, Recent Updated, etc.).
     */
    private void sortListProduct(APIGetProductList.ProductFilterType sortOption) {
        androidUtils.click(loc_btnSort);

        switch (sortOption) {
            case STOCK_HIGH_TO_LOW -> androidUtils.click(loc_lstSortOptions(1));
            case STOCK_LOW_TO_HIGH -> androidUtils.click(loc_lstSortOptions(2));
            case PRIORITY_HIGH_TO_LOW -> androidUtils.click(loc_lstSortOptions(3));
            case PRIORITY_LOW_TO_HIGH -> androidUtils.click(loc_lstSortOptions(4));
            default -> androidUtils.click(loc_lstSortOptions(0));
        }

        logger.info("Sorted product list by {}", sortOption);
    }

    /**
     * Retrieves the list of product names currently displayed on the first screen.
     *
     * @return A list of product names on the first screen.
     */
    private List<String> getListProductOnFirstScreen() {
        return androidUtils.getListElement(loc_lblProductName).stream()
                .limit(5)
                .map(WebElement::getText)
                .toList();
    }

    /**
     * Fetches the expected list of product names after sorting or filtering.
     *
     * @param sortBy      Sorting criteria.
     * @param filterValue Value used for filtering.
     * @return A list of expected product names.
     */
    private List<String> getExpectedProductNames(APIGetProductList.ProductFilterType sortBy, String filterValue) {
        return new APIGetProductList(AndroidSellerLoginScreen.getCredentials())
                .getProductInformationInFirstPage(sortBy, filterValue)
                .stream()
                .map(APIGetProductList.Product::getName)
                .toList();
    }

    /**
     * Verifies that the displayed product list matches the expected list after sorting or filtering.
     *
     * @param filterBy    Filtering category.
     * @param filterValue Filtering value.
     */
    private void verifyProductList(APIGetProductList.ProductFilterType filterBy, String filterValue) {
        List<String> firstScreenProductNames = getListProductOnFirstScreen();
        List<String> expectedProductNames = getExpectedProductNames(filterBy, filterValue);

        Assert.assertEquals(firstScreenProductNames, expectedProductNames.subList(0, firstScreenProductNames.size()),
                "List product after sorted/filtered on first screen must be %s, but found %s"
                        .formatted(expectedProductNames.subList(0, firstScreenProductNames.size()), firstScreenProductNames));
    }

    /**
     * Tests sorting the product list by "Recent updated".
     */
    public void checkSortByRecentUpdated() {
        sortListProduct(RECENT_UPDATED);
        verifyProductList(RECENT_UPDATED, "");
    }

    /**
     * Tests sorting the product list by "Stock high to low".
     */
    public void checkSortByStockHighToLow() {
        sortListProduct(STOCK_HIGH_TO_LOW);
        verifyProductList(STOCK_HIGH_TO_LOW, "");
    }

    /**
     * Tests sorting the product list by "Stock low to high".
     */
    public void checkSortByStockLowToHigh() {
        sortListProduct(STOCK_LOW_TO_HIGH);
        verifyProductList(STOCK_LOW_TO_HIGH, "");
    }

    /**
     * Tests sorting the product list by "Priority high to low".
     */
    public void checkSortByPriorityHighToLow() {
        sortListProduct(PRIORITY_HIGH_TO_LOW);
        verifyProductList(PRIORITY_HIGH_TO_LOW, "");
    }

    /**
     * Tests sorting the product list by "Priority low to high".
     */
    public void checkSortByPriorityLowToHigh() {
        sortListProduct(PRIORITY_LOW_TO_HIGH);
        verifyProductList(PRIORITY_LOW_TO_HIGH, "");
    }

    /**
     * Tests filtering products by status.
     *
     * @param status The status filter value (e.g., Active, Inactive).
     */
    public void checkFilterByStatus(String status) {
        androidUtils.click(loc_btnFilter);
        new FilterScreen(driver).filterByStatus(status);
        verifyProductList(STATUS, status);
    }

    /**
     * Tests filtering products by sales channel.
     *
     * @param channel The channel filter value (e.g., Lazada, Shopee).
     */
    public void checkFilterByChannel(String channel) {
        androidUtils.click(loc_btnFilter);
        new FilterScreen(driver).filterByChannel(channel);
        verifyProductList(CHANNEL, channel);
    }

    /**
     * Tests filtering products by platform.
     *
     * @param platform The platform filter value (e.g., Web, App).
     */
    public void checkFilterByPlatform(String platform) {
        androidUtils.click(loc_btnFilter);
        new FilterScreen(driver).filterByPlatform(platform);
        verifyProductList(PLATFORM, platform);
    }

    /**
     * Tests filtering products by branch.
     */
    public void checkFilterByBranch() {
        androidUtils.click(loc_btnFilter);

        List<APIGetBranchList.BranchInformation> branchInfos = new APIGetBranchList(AndroidSellerLoginScreen.getCredentials()).getBranchInformation();
        String branchName = branchInfos.getFirst().getName();
        int branchId = branchInfos.getFirst().getId();

        new FilterScreen(driver).filterByBranch(branchName);
        verifyProductList(BRANCH, String.valueOf(branchId));
    }

    /**
     * Tests filtering products by collection.
     */
    public void checkFilterByCollections() {
        androidUtils.click(loc_btnFilter);

        List<APIGetProductCollections.CollectionItem> collectionInfos = new APIGetProductCollections(AndroidSellerLoginScreen.getCredentials()).getListProductCollection();
        String collectionName = collectionInfos.isEmpty() ? "ALL" : collectionInfos.getFirst().getName();
        String collectionId = collectionName.equals("ALL") ? "" : String.valueOf(collectionInfos.getFirst().getId());

        new FilterScreen(driver).filterByCollections(collectionName);
        verifyProductList(COLLECTION, collectionId);
    }

    // ============================ INNER CLASSES ============================
    public static class FilterScreen {
        private final WebDriver driver;
        private final AndroidUtils androidUtils;
        private final Logger logger = LogManager.getLogger();

        public FilterScreen(WebDriver driver) {
            this.driver = driver;
            this.androidUtils = new AndroidUtils(driver);
        }

        // Locators
        private final By loc_btnReset = getLocatorByResourceId("%s:id/btnReset");
        private final By loc_btnApply = getLocatorByResourceId("%s:id/btnApply");
        private final By loc_btnSeeAllBranches = getLocatorByResourceId("%s:id/btnSeeAllBranches");
        private final By loc_btnSeeAllCollections = getLocatorByResourceId("%s:id/btnSeeAllCollections");

        // Status locators
        private By loc_btnFilterByStatus(int actionsIndex) {
            return getLocatorByResourceIdAndInstance("%s:id/tag_container", actionsIndex);
        }

        // Channel locators
        By loc_btnFilterByChannel(int actionsIndex) {
            return getLocatorByResourceIdAndInstance("%s:id/tag_container", actionsIndex + 4);
        }

        // Platform locators
        private By loc_btnFilterByPlatform(int actionsIndex) {
            return getLocatorByResourceIdAndInstance("%s:id/tag_container", actionsIndex + 7);
        }

        /**
         * Resets all applied filters.
         */
        private void resetFilters() {
            androidUtils.click(loc_btnReset);
            logger.info("Reset all filters");
        }

        /**
         * Applies the selected filter.
         */
        private void applyFilters() {
            androidUtils.click(loc_btnApply);
            logger.info("Applied selected filters");
        }

        /**
         * Filters products by status.
         *
         * @param status The status to filter (ACTIVE, INACTIVE, ERROR)
         */
        public void filterByStatus(String status) {
            resetFilters();

            switch (status) {
                case "ACTIVE" -> androidUtils.click(loc_btnFilterByStatus(1));
                case "INACTIVE" -> androidUtils.click(loc_btnFilterByStatus(2));
                case "ERROR" -> androidUtils.click(loc_btnFilterByStatus(3));
                default -> androidUtils.click(loc_btnFilterByStatus(0));
            }

            logger.info("Filtered products by status: {}", status);
            applyFilters();
        }

        /**
         * Filters products by sales channel.
         *
         * @param channel The channel to filter (LAZADA, SHOPEE)
         */
        public void filterByChannel(String channel) {
            resetFilters();

            switch (channel) {
                case "LAZADA" -> androidUtils.click(loc_btnFilterByChannel(1));
                case "SHOPEE" -> androidUtils.click(loc_btnFilterByChannel(2));
                default -> androidUtils.click(loc_btnFilterByChannel(0));
            }

            logger.info("Filtered products by channel: {}", channel);
            applyFilters();
        }

        /**
         * Filters products by platform.
         *
         * @param platform The platform to filter (WEB, APP, IN_STORE, NONE)
         */
        public void filterByPlatform(String platform) {
            resetFilters();

            switch (platform) {
                case "WEB" -> androidUtils.click(loc_btnFilterByPlatform(1));
                case "APP" -> androidUtils.click(loc_btnFilterByPlatform(2));
                case "IN_STORE" -> androidUtils.click(loc_btnFilterByPlatform(3));
                case "NONE" -> androidUtils.click(loc_btnFilterByPlatform(4));
                default -> androidUtils.click(loc_btnFilterByPlatform(0));
            }

            logger.info("Filtered products by platform: {}", platform);
            applyFilters();
        }

        /**
         * Filters products by branch.
         *
         * @param branchName The name of the branch to select.
         */
        public void filterByBranch(String branchName) {
            resetFilters();
            androidUtils.click(loc_btnSeeAllBranches);
            new FilterScreen.BranchScreen(driver).selectBranch(branchName);
            applyFilters();
            logger.info("Filtered products by branch: {}", branchName);
        }

        /**
         * Filters products by collection.
         *
         * @param collectionName The name of the collection to select.
         */
        public void filterByCollections(String collectionName) {
            resetFilters();
            androidUtils.click(loc_btnSeeAllCollections);
            new FilterScreen.CollectionsScreen(driver).selectCollection(collectionName);
            applyFilters();
            logger.info("Filtered products by collection: {}", collectionName);
        }

        // ============================ INNER CLASSES ============================

        /**
         * Handles branch selection.
         */
        public static class BranchScreen {
            private final AndroidUtils androidUtils;
            private final Logger logger = LogManager.getLogger();

            public BranchScreen(WebDriver driver) {
                this.androidUtils = new AndroidUtils(driver);
            }

            private final By loc_btnAllBranches = By.xpath("(//*[@* = '%s:id/htvFullBranches'] // *[@* = '%s:id/tag_container'])[1]".formatted(appBundleId, appBundleId));

            private By loc_btnBranch(String branchName) {
                return By.xpath("//*[@text = '%s']".formatted(branchName));
            }

            /**
             * Selects a branch based on the given name.
             *
             * @param branchName The name of the branch to select.
             */
            public void selectBranch(String branchName) {
                androidUtils.click(branchName.equals("ALL") ? loc_btnAllBranches : loc_btnBranch(branchName));
                logger.info("Selected branch: {}", branchName);
            }
        }

        /**
         * Handles collection selection.
         */
        public static class CollectionsScreen {
            private final AndroidUtils androidUtils;
            private final Logger logger = LogManager.getLogger();

            public CollectionsScreen(WebDriver driver) {
                this.androidUtils = new AndroidUtils(driver);
            }

            private final By loc_btnAllCollections = By.xpath("(//*[@* = '%s:id/htvFullCollections'] // *[@* = '%s:id/tag_container'])[1]".formatted(appBundleId, appBundleId));

            private By loc_btnCollection(String collectionName)  {
                return By.xpath("//*[@text = '%s']".formatted(collectionName));
            }

            /**
             * Selects a collection based on the given name.
             *
             * @param collectionName The name of the collection to select.
             */
            public void selectCollection(String collectionName) {
                androidUtils.click(collectionName.equals("ALL") ? loc_btnAllCollections : loc_btnCollection(collectionName));
                logger.info("Selected collection: {}", collectionName);
            }
        }
    }
}
