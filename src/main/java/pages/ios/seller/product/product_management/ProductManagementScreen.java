package pages.ios.seller.product.product_management;

import io.appium.java_client.ios.IOSDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import pages.ios.seller.home.HomeScreen;
import utility.IOSUtils;

import static pages.ios.seller.product.IOSBaseProductElement.loc_chkDisplayIfOutOfStock;
import static utility.helper.ActivityHelper.sellerBundleId;

public class ProductManagementScreen extends ProductManagementElement {
    WebDriver driver;
    IOSUtils iosUtils;
    Logger logger = LogManager.getLogger();

    public ProductManagementScreen(WebDriver driver) {
        // Get driver
        this.driver = driver;

        // Init commons class
        iosUtils = new IOSUtils(driver);
    }

    public ProductManagementScreen navigateToProductManagementScreen() {
        // Relaunch app
        iosUtils.relaunchApp(sellerBundleId);

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

            // Wait product detail screen loaded
            iosUtils.getElement(loc_chkDisplayIfOutOfStock);
        } else throw new NoSuchElementException("No result with keyword: %s".formatted(productName));
    }

//    void sortListProduct(String sortOption) {
//        // Open list of sort options
//        iosUtils.click(loc_btnSort);
//
//        // Sort product
//        switch (sortOption) {
//            case "Stock high to low" -> iosUtils.click(loc_ddvStockHighToLow);
//            case "Stock low to high" -> iosUtils.click(loc_ddvStockLowToHigh);
//            case "Priority high to low" -> iosUtils.click(loc_ddvPriorityHighToLow);
//            case "Priority low to high" -> iosUtils.click(loc_ddvPriorityLowToHigh);
//            default ->  iosUtils.click(loc_ddvRecentlyUpdated);
//        }
//
//        // Log
//        logger.info("Sort list product by {}", sortOption);
//    }
//
//    private List<String> getListProductOnFirstScreen() {
//        // Get all products on first screen after sort/filter
//        return IntStream.range(0, iosUtils.getListElement(loc_lblProductName).size())
//                .mapToObj(index -> iosUtils.getText(loc_lblProductName, index)).toList();
//    }
//
//    public void checkSortByRecentUpdated() {
//        // Sort list product by recent updated
//        sortListProduct("Recent updated");
//
//        // Get list product name after sort
//        List<String> firstScreenProductNames = getListProductOnFirstScreen();
//
//        // Get list product nam by API
//        List<String> expectedProductNames = new APIAllProductsForCheckSortAndFilter(LoginScreen.getLoginInformation()).getListProductNameAfterSortByRecentUpdated();
//
//        // Verify list product are sorted correctly
//        assertCustomize.assertEquals(firstScreenProductNames, expectedProductNames.subList(0, firstScreenProductNames.size()), "List product after sorted on first screen must be %s, but found %s"
//                .formatted(expectedProductNames.subList(0, firstScreenProductNames.size()), firstScreenProductNames));
//
//        // Verify test
//        AssertCustomize.verifyTest();
//    }
//
//    public void checkSortByStockHighToLow() {
//        // Sort list product by stock high to low
//        sortListProduct("Stock high to low");
//
//        // Get list product name after sort
//        List<String> firstScreenProductNames = getListProductOnFirstScreen();
//
//        // Get list product nam by API
//        List<String> expectedProductNames = new APIAllProductsForCheckSortAndFilter(LoginScreen.getLoginInformation()).getListProductNameAfterSortByStockHighToLow();
//
//        // Verify list product are sorted correctly
//        assertCustomize.assertEquals(firstScreenProductNames, expectedProductNames.subList(0, firstScreenProductNames.size()), "List product after sorted on first screen must be %s, but found %s"
//                .formatted(expectedProductNames.subList(0, firstScreenProductNames.size()), firstScreenProductNames));
//
//        // Verify test
//        AssertCustomize.verifyTest();
//    }
//
//    public void checkSortByStockLowToHigh() {
//        // Sort list product by stock low to high
//        sortListProduct("Stock low to high");
//
//        // Get list product name after sort
//        List<String> firstScreenProductNames = getListProductOnFirstScreen();
//
//        // Get list product nam by API
//        List<String> expectedProductNames = new APIAllProductsForCheckSortAndFilter(LoginScreen.getLoginInformation()).getListProductNameAfterSortByStockLowToHigh();
//
//        // Verify list product are sorted correctly
//        assertCustomize.assertEquals(firstScreenProductNames, expectedProductNames.subList(0, firstScreenProductNames.size()), "List product after sorted on first screen must be %s, but found %s"
//                .formatted(expectedProductNames.subList(0, firstScreenProductNames.size()), firstScreenProductNames));
//
//        // Verify test
//        AssertCustomize.verifyTest();
//    }
//
//    public void checkSortByPriorityHighToLow() {
//        // Sort list product by priority high to low
//        sortListProduct("Priority high to low");
//
//        // Get list product name after sort
//        List<String> firstScreenProductNames = getListProductOnFirstScreen();
//
//        // Get list product nam by API
//        List<String> expectedProductNames = new APIAllProductsForCheckSortAndFilter(LoginScreen.getLoginInformation()).getListProductNameAfterSortByPriorityHighToLow();
//
//        // Verify list product are sorted correctly
//        assertCustomize.assertEquals(firstScreenProductNames, expectedProductNames.subList(0, firstScreenProductNames.size()), "List product after sorted on first screen must be %s, but found %s"
//                .formatted(expectedProductNames.subList(0, firstScreenProductNames.size()), firstScreenProductNames));
//
//        // Verify test
//        AssertCustomize.verifyTest();
//    }
//
//    public void checkSortByPriorityLowToHigh() {
//        // Sort list product by priority low to high
//        sortListProduct("Priority low to high");
//
//        // Get list product name after sort
//        List<String> firstScreenProductNames = getListProductOnFirstScreen();
//
//        // Get list product nam by API
//        List<String> expectedProductNames = new APIAllProductsForCheckSortAndFilter(LoginScreen.getLoginInformation()).getListProductNameAfterSortByPriorityLowToHigh();
//
//        // Verify list product are sorted correctly
//        assertCustomize.assertEquals(firstScreenProductNames, expectedProductNames.subList(0, firstScreenProductNames.size()), "List product after sorted on first screen must be %s, but found %s"
//                .formatted(expectedProductNames.subList(0, firstScreenProductNames.size()), firstScreenProductNames));
//
//        // Verify test
//        AssertCustomize.verifyTest();
//    }
//
//    public void checkFilterByActiveStatus() {
//        // Sort list product by recent updated
//        sortListProduct("Recent updated");
//
//        // Navigate to Filter screen
//        iosUtils.click(loc_btnFilter);
//
//        // Filter list product by Activate status
//        new FilterScreen(driver).filterByStatus("ACTIVE");
//
//        // Get list product name after filter
//        List<String> firstScreenProductNames = getListProductOnFirstScreen();
//
//        // Get list product nam by API
//        List<String> expectedProductNames = new APIAllProductsForCheckSortAndFilter(LoginScreen.getLoginInformation()).getListProductAfterFilterByStatus("ACTIVE");
//
//        // Verify list product are filtered correctly
//        assertCustomize.assertEquals(firstScreenProductNames, expectedProductNames.subList(0, firstScreenProductNames.size()), "List product after filtered first screen must be %s, but found %s"
//                .formatted(expectedProductNames.subList(0, firstScreenProductNames.size()), firstScreenProductNames));
//
//        // Verify test
//        AssertCustomize.verifyTest();
//    }
//
//    public void checkFilterByInactiveStatus() {
//        // Sort list product by recent updated
//        sortListProduct("Recent updated");
//
//        // Navigate to Filter screen
//        iosUtils.click(loc_btnFilter);
//
//        // Filter list product by Inactive status
//        new FilterScreen(driver).filterByStatus("INACTIVE");
//
//        // Get list product name after filter
//        List<String> firstScreenProductNames = getListProductOnFirstScreen();
//
//        // Get list product nam by API
//        List<String> expectedProductNames = new APIAllProductsForCheckSortAndFilter(LoginScreen.getLoginInformation()).getListProductAfterFilterByStatus("INACTIVE");
//
//        // Verify list product are filtered correctly
//        assertCustomize.assertEquals(firstScreenProductNames, expectedProductNames.subList(0, firstScreenProductNames.size()), "List product after filtered first screen must be %s, but found %s"
//                .formatted(expectedProductNames.subList(0, firstScreenProductNames.size()), firstScreenProductNames));
//
//        // Verify test
//        AssertCustomize.verifyTest();
//    }
//
//    public void checkFilterByErrorStatus() {
//        // Sort list product by recent updated
//        sortListProduct("Recent updated");
//
//        // Navigate to Filter screen
//        iosUtils.click(loc_btnFilter);
//
//        // Filter list product by Error status
//        new FilterScreen(driver).filterByStatus("ERROR");
//
//        // Get list product name after filter
//        List<String> firstScreenProductNames = getListProductOnFirstScreen();
//
//        // Get list product nam by API
//        List<String> expectedProductNames = new APIAllProductsForCheckSortAndFilter(LoginScreen.getLoginInformation()).getListProductAfterFilterByStatus("ERROR");
//
//        // Verify list product are filtered correctly
//        assertCustomize.assertEquals(firstScreenProductNames, expectedProductNames.subList(0, firstScreenProductNames.size()), "List product after filtered first screen must be %s, but found %s"
//                .formatted(expectedProductNames.subList(0, firstScreenProductNames.size()), firstScreenProductNames));
//
//        // Verify test
//        AssertCustomize.verifyTest();
//    }
//
//
//    public void checkFilterByLazadaChannel() {
//        // Sort list product by recent updated
//        sortListProduct("Recent updated");
//
//        // Navigate to Filter screen
//        iosUtils.click(loc_btnFilter);
//
//        // Filter list product by Lazada channel
//        new FilterScreen(driver).filterByChannel("LAZADA");
//
//        // Get list product name after filter
//        List<String> firstScreenProductNames = getListProductOnFirstScreen();
//
//        // Get list product nam by API
//        List<String> expectedProductNames = new APIAllProductsForCheckSortAndFilter(LoginScreen.getLoginInformation()).getListProductAfterFilterByChannel("LAZADA");
//
//        // Verify list product are filtered correctly
//        assertCustomize.assertEquals(firstScreenProductNames, expectedProductNames.subList(0, firstScreenProductNames.size()), "List product after filtered first screen must be %s, but found %s"
//                .formatted(expectedProductNames.subList(0, firstScreenProductNames.size()), firstScreenProductNames));
//
//        // Verify test
//        AssertCustomize.verifyTest();
//    }
//
//    public void checkFilterByShopeeChannel() {
//        // Sort list product by recent updated
//        sortListProduct("Recent updated");
//
//        // Navigate to Filter screen
//        iosUtils.click(loc_btnFilter);
//
//        // Filter list product by Shopee channel
//        new FilterScreen(driver).filterByChannel("SHOPEE");
//
//        // Get list product name after filter
//        List<String> firstScreenProductNames = getListProductOnFirstScreen();
//
//        // Get list product nam by API
//        List<String> expectedProductNames = new APIAllProductsForCheckSortAndFilter(LoginScreen.getLoginInformation()).getListProductAfterFilterByChannel("SHOPEE");
//
//        // Verify list product are filtered correctly
//        assertCustomize.assertEquals(firstScreenProductNames, expectedProductNames.subList(0, firstScreenProductNames.size()), "List product after filtered first screen must be %s, but found %s"
//                .formatted(expectedProductNames.subList(0, firstScreenProductNames.size()), firstScreenProductNames));
//
//        // Verify test
//        AssertCustomize.verifyTest();
//    }
//
//    public void checkFilterByWebPlatform() {
//        // Sort list product by recent updated
//        sortListProduct("Recent updated");
//
//        // Navigate to Filter screen
//        iosUtils.click(loc_btnFilter);
//
//        // Filter list product by Web platform
//        new FilterScreen(driver).filterByPlatform("WEB");
//
//        // Get list product name after filter
//        List<String> firstScreenProductNames = getListProductOnFirstScreen();
//
//        // Get list product nam by API
//        List<String> expectedProductNames = new APIAllProductsForCheckSortAndFilter(LoginScreen.getLoginInformation()).getListProductAfterFilterByPlatform("WEB");
//
//        // Verify list product are filtered correctly
//        assertCustomize.assertEquals(firstScreenProductNames, expectedProductNames.subList(0, firstScreenProductNames.size()), "List product after filtered first screen must be %s, but found %s"
//                .formatted(expectedProductNames.subList(0, firstScreenProductNames.size()), firstScreenProductNames));
//
//        // Verify test
//        AssertCustomize.verifyTest();
//    }
//
//    public void checkFilterByAppPlatform() {
//        // Sort list product by recent updated
//        sortListProduct("Recent updated");
//
//        // Navigate to Filter screen
//        iosUtils.click(loc_btnFilter);
//
//        // Filter list product by App platform
//        new FilterScreen(driver).filterByPlatform("APP");
//
//        // Get list product name after filter
//        List<String> firstScreenProductNames = getListProductOnFirstScreen();
//
//        // Get list product nam by API
//        List<String> expectedProductNames = new APIAllProductsForCheckSortAndFilter(LoginScreen.getLoginInformation()).getListProductAfterFilterByPlatform("APP");
//
//        // Verify list product are filtered correctly
//        assertCustomize.assertEquals(firstScreenProductNames, expectedProductNames.subList(0, firstScreenProductNames.size()), "List product after filtered first screen must be %s, but found %s"
//                .formatted(expectedProductNames.subList(0, firstScreenProductNames.size()), firstScreenProductNames));
//
//        // Verify test
//        AssertCustomize.verifyTest();
//
//    }
//
//    public void checkFilterByPOSPlatform() {
//        // Sort list product by recent updated
//        sortListProduct("Recent updated");
//
//        // Navigate to Filter screen
//        iosUtils.click(loc_btnFilter);
//
//        // Filter list product by in-store platform
//        new FilterScreen(driver).filterByPlatform("IN_STORE");
//
//        // Get list product name after filter
//        List<String> firstScreenProductNames = getListProductOnFirstScreen();
//
//        // Get list product nam by API
//        List<String> expectedProductNames = new APIAllProductsForCheckSortAndFilter(LoginScreen.getLoginInformation()).getListProductAfterFilterByPlatform("IN_STORE");
//
//        // Verify list product are filtered correctly
//        assertCustomize.assertEquals(firstScreenProductNames, expectedProductNames.subList(0, firstScreenProductNames.size()), "List product after filtered first screen must be %s, but found %s"
//                .formatted(expectedProductNames.subList(0, firstScreenProductNames.size()), firstScreenProductNames));
//
//        // Verify test
//        AssertCustomize.verifyTest();
//    }
//
//    public void checkFilterByNonePlatform() {
//        // Sort list product by recent updated
//        sortListProduct("Recent updated");
//
//        // Navigate to Filter screen
//        iosUtils.click(loc_btnFilter);
//
//        // Filter list product by in-store platform
//        new FilterScreen(driver).filterByPlatform("NONE");
//
//        // Get list product name after filter
//        List<String> firstScreenProductNames = getListProductOnFirstScreen();
//
//        // Get list product nam by API
//        List<String> expectedProductNames = new APIAllProductsForCheckSortAndFilter(LoginScreen.getLoginInformation()).getListProductAfterFilterByPlatform("NONE");
//
//        // Verify list product are filtered correctly
//        assertCustomize.assertEquals(firstScreenProductNames, expectedProductNames.subList(0, firstScreenProductNames.size()), "List product after filtered first screen must be %s, but found %s"
//                .formatted(expectedProductNames.subList(0, firstScreenProductNames.size()), firstScreenProductNames));
//
//        // Verify test
//        AssertCustomize.verifyTest();
//    }
//
//    public void checkFilterByBranch() {
//        // Sort list product by recent updated
//        sortListProduct("Recent updated");
//
//        // Navigate to Filter screen
//        iosUtils.click(loc_btnFilter);
//
//        // Get branch info
//        BranchInfo branchInfo = new BranchManagement(LoginScreen.getLoginInformation()).getInfo();
//        String branchName = branchInfo.getBranchName().get(nextInt(branchInfo.getBranchName().size()));
//        int branchId = branchInfo.getBranchID().get(branchInfo.getBranchName().indexOf(branchName));
//
//        // Filter list product by in-store platform
//        new FilterScreen(driver).filterByBranch(branchName);
//
//        // Get list product name after filter
//        List<String> firstScreenProductNames = getListProductOnFirstScreen();
//
//        // Get list product nam by API
//        List<String> expectedProductNames = new APIAllProductsForCheckSortAndFilter(LoginScreen.getLoginInformation()).getListProductAfterFilterByBranch(branchId);
//
//        // Verify list product are filtered correctly
//        assertCustomize.assertEquals(firstScreenProductNames, expectedProductNames.subList(0, firstScreenProductNames.size()), "List product after filtered first screen must be %s, but found %s"
//                .formatted(expectedProductNames.subList(0, firstScreenProductNames.size()), firstScreenProductNames));
//
//        // Verify test
//        AssertCustomize.verifyTest();
//    }
//
//    public void checkFilterByCollections() {
//        // Sort list product by recent updated
//        sortListProduct("Recent updated");
//
//        // Navigate to Filter screen
//        iosUtils.click(loc_btnFilter);
//
//        // Get collection info
//        CollectionInfo collectionInfo= new APIProductCollection(LoginScreen.getLoginInformation()).getManualCollection();
//        String collectionName = collectionInfo.getCollectionNames().isEmpty() ? "ALL" : collectionInfo.getCollectionNames().get(0);
//        String collectionId = collectionName.equals("ALL") ? "" : collectionInfo.getCollectionIds().get(0).toString();
//
//        // Filter list product by in-store platform
//        new FilterScreen(driver).filterByCollections(collectionName);
//
//        // Get list product name after filter
//        List<String> firstScreenProductNames = getListProductOnFirstScreen();
//
//        // Get list product nam by API
//        List<String> expectedProductNames = new APIAllProductsForCheckSortAndFilter(LoginScreen.getLoginInformation()).getListProductAfterFilterByCollection(collectionId);
//
//        // Verify list product are filtered correctly
//        assertCustomize.assertEquals(firstScreenProductNames, expectedProductNames.subList(0, firstScreenProductNames.size()), "List product after filtered first screen must be %s, but found %s"
//                .formatted(expectedProductNames.subList(0, firstScreenProductNames.size()), firstScreenProductNames));
//
//        // Verify test
//        AssertCustomize.verifyTest();
//    }
}
