package pages.ios.seller.product.product_management;

import api.seller.product.APIGetProductCollections;
import api.seller.product.APIGetProductList;
import api.seller.setting.APIGetBranchList;
import io.appium.java_client.AppiumBy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import pages.ios.seller.home.HomeScreen;
import pages.ios.seller.login.LoginScreen;
import utility.IOSUtils;

import java.util.List;

import static api.seller.product.APIGetProductList.Product;
import static api.seller.product.APIGetProductList.ProductFilterType;
import static api.seller.product.APIGetProductList.ProductFilterType.*;

public class IOSProductManagementScreen {
    WebDriver driver;
    IOSUtils iosUtils;
    Logger logger = LogManager.getLogger();

    public IOSProductManagementScreen(WebDriver driver) {
        // Get driver
        this.driver = driver;

        // Init commons class
        iosUtils = new IOSUtils(driver);
    }

    public static final By loc_txtSearchBox = By.xpath("//XCUIElementTypeImage[@name=\"icon_search\"]/preceding-sibling::XCUIElementTypeTextField");

    private By loc_lblProductName(String productName) {
        return By.xpath("//XCUIElementTypeStaticText[@name=\"%s\"]".formatted(productName));
    }

    private final By loc_lblProductNameCell = By.xpath("//XCUIElementTypeCell");
    private final By loc_btnSort = AppiumBy.iOSNsPredicateString("name == \"icon sort priority\"");
    private final By loc_ddvRecentlyUpdated = By.xpath("//XCUIElementTypeStaticText[@name=\"Recently updated\" or @name=\"Cập nhật gần đây\"]");
    private final By loc_ddvStockHighToLow = By.xpath("//XCUIElementTypeStaticText[@name=\"Stock: High to low\" or @name=\"Số lượng giảm dần\"]");
    private final By loc_ddvStockLowToHigh = By.xpath("//XCUIElementTypeStaticText[@name=\"Stock: Low to high\" or @name=\"Số lượng tăng dần\"]");
    private final By loc_ddvPriorityHighToLow = By.xpath("//XCUIElementTypeStaticText[@name=\"Priority: High to low\" or @name=\"Độ ưu tiên giảm dần\"]");
    private final By loc_ddvPriorityLowToHigh = By.xpath("//XCUIElementTypeStaticText[@name=\"Priority: Low to high\" or @name=\"Độ ưu tiên tăng dần\"]");
    private final By loc_btnFilter = By.xpath("(//XCUIElementTypeButton[@name=\"icon sort priority\"]/parent::*/following-sibling::*//XCUIElementTypeButton)[1]");

    public IOSProductManagementScreen navigateToProductManagementScreen() {
        // Relaunch app
        iosUtils.relaunchApp();

        // Navigate to product management screen
        new HomeScreen(driver).navigateToProductManagementScreen();

        return this;
    }

    public void navigateToProductDetailScreen(String productName) {
        // Search product by name
        iosUtils.sendKeys(loc_txtSearchBox, productName);

        // Log
        logger.info("Search product by name: {}", productName);

        // Navigate to product detail screen
        if (!iosUtils.getListElement(loc_lblProductName(productName)).isEmpty()) {
            // Click into first result
            iosUtils.click(loc_lblProductName(productName));
        } else throw new NoSuchElementException("No result with keyword: %s".formatted(productName));
    }

    /**
     * Sorts the product list based on the specified sorting option.
     *
     * @param sortOption The sorting criteria (e.g., Stock High to Low, Recent Updated, etc.).
     */
    private void sortListProduct(ProductFilterType sortOption) {
        iosUtils.click(loc_btnSort);

        switch (sortOption) {
            case STOCK_HIGH_TO_LOW -> iosUtils.click(loc_ddvStockHighToLow);
            case STOCK_LOW_TO_HIGH -> iosUtils.click(loc_ddvStockLowToHigh);
            case PRIORITY_HIGH_TO_LOW -> iosUtils.click(loc_ddvPriorityHighToLow);
            case PRIORITY_LOW_TO_HIGH -> iosUtils.click(loc_ddvPriorityLowToHigh);
            default -> iosUtils.click(loc_ddvRecentlyUpdated);
        }

        logger.info("Sorted product list by {}", sortOption);
    }

    /**
     * Retrieves the list of product names currently displayed on the first screen.
     *
     * @return A list of product names on the first screen.
     */
    private List<String> getListProductOnFirstScreen() {
        return iosUtils.getListElement(loc_lblProductNameCell).stream()
                .limit(5)
                .map(webElement -> {
                    List<WebElement> buttons = webElement.findElements(By.xpath(".//XCUIElementTypeButton"));
                    List<WebElement> textElements = webElement.findElements(By.xpath(".//XCUIElementTypeStaticText"));

                    if (textElements.isEmpty()) {
                        throw new RuntimeException("Can not get product name because it is not presented.");
                    }

                    return buttons.isEmpty() ? textElements.getLast().getText()
                            : textElements.getFirst().getText();
                })
                .toList();
    }

    /**
     * Fetches the expected list of product names after sorting or filtering.
     *
     * @param sortBy      Sorting criteria.
     * @param filterValue Value used for filtering.
     * @return A list of expected product names.
     */
    private List<String> getExpectedProductNames(ProductFilterType sortBy, String filterValue) {
        return new APIGetProductList(LoginScreen.getCredentials())
                .getProductInformationInFirstPage(sortBy, filterValue)
                .stream()
                .map(Product::getName)
                .toList();
    }

    /**
     * Verifies that the displayed product list matches the expected list after sorting or filtering.
     *
     * @param filterBy    Filtering category.
     * @param filterValue Filtering value.
     */
    private void verifyProductList(ProductFilterType filterBy, String filterValue) {
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
        iosUtils.click(loc_btnFilter);
        new FilterScreen(driver).filterByStatus(status);
        verifyProductList(STATUS, status);
    }

    /**
     * Tests filtering products by sales channel.
     *
     * @param channel The channel filter value (e.g., Lazada, Shopee).
     */
    public void checkFilterByChannel(String channel) {
        iosUtils.click(loc_btnFilter);
        new FilterScreen(driver).filterByChannel(channel);
        verifyProductList(CHANNEL, channel);
    }

    /**
     * Tests filtering products by platform.
     *
     * @param platform The platform filter value (e.g., Web, App).
     */
    public void checkFilterByPlatform(String platform) {
        iosUtils.click(loc_btnFilter);
        new FilterScreen(driver).filterByPlatform(platform);
        verifyProductList(PLATFORM, platform);
    }

    /**
     * Tests filtering products by branch.
     */
    public void checkFilterByBranch() {
        iosUtils.click(loc_btnFilter);

        List<APIGetBranchList.BranchInformation> branchInfos = new APIGetBranchList(LoginScreen.getCredentials()).getBranchInformation();
        String branchName = branchInfos.getFirst().getName();
        int branchId = branchInfos.getFirst().getId();

        new FilterScreen(driver).filterByBranch(branchName);
        verifyProductList(BRANCH, String.valueOf(branchId));
    }

    /**
     * Tests filtering products by collection.
     */
    public void checkFilterByCollections() {
        iosUtils.click(loc_btnFilter);

        List<APIGetProductCollections.CollectionItem> collectionInfos = new APIGetProductCollections(LoginScreen.getCredentials()).getListProductCollection();
        String collectionName = collectionInfos.isEmpty() ? "ALL" : collectionInfos.getFirst().getName();
        String collectionId = collectionName.equals("ALL") ? "" : String.valueOf(collectionInfos.getFirst().getId());

        new FilterScreen(driver).filterByCollections(collectionName);
        verifyProductList(COLLECTION, collectionId);
    }

    // ============================ INNER CLASSES ============================
    public static class FilterScreen {
        private final WebDriver driver;
        private final IOSUtils iosUtils;
        private final Logger logger = LogManager.getLogger();

        public FilterScreen(WebDriver driver) {
            this.driver = driver;
            this.iosUtils = new IOSUtils(driver);
        }

        // Locators
        private final By loc_btnReset = By.xpath("//XCUIElementTypeButton[@name=\"Reset\" or @name=\"Đặt lại\"]");
        private final By loc_btnApply = By.xpath("//XCUIElementTypeButton[@name=\"Apply\" or @name=\"Áp dụng\"]");
        private final By loc_btnSeeAllBranches = By.xpath("(//XCUIElementTypeButton[@name=\"See all\" or @name=\"Xem tất cả\"])[1]");
        private final By loc_btnSeeAllCollections = By.xpath("(//XCUIElementTypeButton[@name=\"See all\" or @name=\"Xem tất cả\"])[2]");

        // Status locators
        private final By loc_btnActiveStatus = By.xpath("//XCUIElementTypeButton[@name=\"Active\" or @name=\"Đang bán\"]");
        private final By loc_btnInActiveStatus = By.xpath("//XCUIElementTypeButton[@name=\"Inactive\" or @name=\"Đã ngừng bán\"]");
        private final By loc_btnErrorStatus = By.xpath("//XCUIElementTypeButton[@name=\"Error\" or @name=\"Đang lỗi\"]");

        // Channel locators
        private final By loc_btnLazadaChannel = By.xpath("//XCUIElementTypeButton[@name=\"Lazada\"]");
        private final By loc_btnShopeeChannel = By.xpath("//XCUIElementTypeButton[@name=\"Shopee\"]");

        // Platform locators
        private final By loc_btnWebPlatform = By.xpath("//XCUIElementTypeButton[@name=\"Web\"]");
        private final By loc_btnAppPlatform = By.xpath("//XCUIElementTypeButton[@name=\"App\" or @name=\"Ứng dụng\"]");
        private final By loc_btnInStorePlatform = By.xpath("//XCUIElementTypeButton[@name=\"In-store\" or @name=\"Tại cửa hàng\"]");
        private final By loc_btnNonePlatform = By.xpath("//XCUIElementTypeButton[@name=\"None Platform\" or @name=\"Chưa xác định\"]");

        /**
         * Resets all applied filters.
         */
        private void resetFilters() {
            iosUtils.click(loc_btnReset);
            logger.info("Reset all filters");
        }

        /**
         * Applies the selected filter.
         */
        private void applyFilters() {
            iosUtils.click(loc_btnApply);
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
                case "ACTIVE" -> iosUtils.click(loc_btnActiveStatus);
                case "INACTIVE" -> iosUtils.click(loc_btnInActiveStatus);
                case "ERROR" -> iosUtils.click(loc_btnErrorStatus);
                default -> throw new IllegalArgumentException("Invalid status: " + status);
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
                case "LAZADA" -> iosUtils.click(loc_btnLazadaChannel);
                case "SHOPEE" -> iosUtils.click(loc_btnShopeeChannel);
                default -> throw new IllegalArgumentException("Invalid channel: " + channel);
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
                case "WEB" -> iosUtils.click(loc_btnWebPlatform);
                case "APP" -> iosUtils.click(loc_btnAppPlatform);
                case "IN_STORE" -> iosUtils.click(loc_btnInStorePlatform);
                case "NONE" -> iosUtils.click(loc_btnNonePlatform);
                default -> throw new IllegalArgumentException("Invalid platform: " + platform);
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
            iosUtils.click(loc_btnSeeAllBranches);
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
            iosUtils.click(loc_btnSeeAllCollections);
            new FilterScreen.CollectionsScreen(driver).selectCollection(collectionName);
            applyFilters();
            logger.info("Filtered products by collection: {}", collectionName);
        }

        // ============================ INNER CLASSES ============================

        /**
         * Handles branch selection.
         */
        public static class BranchScreen {
            private final IOSUtils iosUtils;
            private final Logger logger = LogManager.getLogger();

            public BranchScreen(WebDriver driver) {
                this.iosUtils = new IOSUtils(driver);
            }

            private final By loc_btnAllBranches = By.xpath("//XCUIElementTypeStaticText[@name=\"All branches\" or @name=\"Tất cả chi nhánh\"]");

            private By loc_btnBranch(String branchName) {
                return By.xpath("//XCUIElementTypeStaticText[@name=\"%s\"]".formatted(branchName));
            }

            /**
             * Selects a branch based on the given name.
             *
             * @param branchName The name of the branch to select.
             */
            public void selectBranch(String branchName) {
                iosUtils.click(branchName.equals("ALL") ? loc_btnAllBranches : loc_btnBranch(branchName));
                logger.info("Selected branch: {}", branchName);
            }
        }

        /**
         * Handles collection selection.
         */
        public static class CollectionsScreen {
            private final IOSUtils iosUtils;
            private final Logger logger = LogManager.getLogger();

            public CollectionsScreen(WebDriver driver) {
                this.iosUtils = new IOSUtils(driver);
            }

            private final By loc_btnAllCollections = By.xpath("//XCUIElementTypeStaticText[@name=\"All collections\" or @name=\"Tất cả bộ sưu tập\"]");

            private By loc_btnCollection(String collectionName) {
                return By.xpath("//XCUIElementTypeStaticText[@name=\"%s\"]".formatted(collectionName));
            }

            /**
             * Selects a collection based on the given name.
             *
             * @param collectionName The name of the collection to select.
             */
            public void selectCollection(String collectionName) {
                iosUtils.click(collectionName.equals("ALL") ? loc_btnAllCollections : loc_btnCollection(collectionName));
                logger.info("Selected collection: {}", collectionName);
            }
        }
    }
}
