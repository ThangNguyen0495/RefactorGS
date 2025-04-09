package android.seller;

import baseTest.BaseTest;
import org.apache.logging.log4j.LogManager;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.android.seller.home.AndroidSellerHomeScreen;
import pages.android.seller.login.AndroidSellerLoginScreen;
import pages.android.seller.products.AndroidProductManagementScreen;

import java.io.IOException;
import java.net.URISyntaxException;

public class AndroidGoSELLERProductManagementTest extends BaseTest {
    AndroidProductManagementScreen productManagementScreen;

    @BeforeClass
    void setup() throws URISyntaxException, IOException {
        initDriver("SELLER", "ANDROID");
        new AndroidSellerLoginScreen(driver).performLogin(sellerCredentials);
        // Change application language
        new AndroidSellerHomeScreen(driver).changeApplicationLanguage();

        // init product page POM
        productManagementScreen = new AndroidProductManagementScreen(driver);
    }

    /**
     * Data Provider for product sorting and filtering test cases.
     *
     * @return A 2D array containing test case names, sort/filter options, and filter values.
     */
    @DataProvider(name = "sortAndFilterOptions")
    public Object[][] provideSortAndFilterOptions() {
        return new Object[][]{
                {"MN01: Sort product by recent updated", "RECENT_UPDATED", ""},
                {"MN02: Sort product by stock high to low", "STOCK_HIGH_TO_LOW", ""},
                {"MN03: Sort product by stock low to high", "STOCK_LOW_TO_HIGH", ""},
                {"MN04: Sort product by priority high to low", "PRIORITY_HIGH_TO_LOW", ""},
                {"MN05: Sort product by priority low to high", "PRIORITY_LOW_TO_HIGH", ""},
                {"MN06: Filter product by active status", "STATUS", "ACTIVE"},
                {"MN07: Filter product by inactive status", "STATUS", "INACTIVE"},
                {"MN08: Filter product by error status", "STATUS", "ERROR"},
                {"MN09: Filter product by Lazada channel", "CHANNEL", "LAZADA"},
                {"MN10: Filter product by Shopee channel", "CHANNEL", "SHOPEE"},
                {"MN11: Filter product by web platform", "PLATFORM", "WEB"},
                {"MN12: Filter product by app platform", "PLATFORM", "APP"},
                {"MN13: Filter product by in-store platform", "PLATFORM", "IN_STORE"},
                {"MN14: Filter product by none platform", "PLATFORM", "NONE"},
                {"MN15: Filter product by branch", "BRANCH", ""},
                {"MN16: Filter product by collections", "COLLECTION", ""}
        };
    }


    /**
     * Executes product sorting and filtering test cases using the Data Provider.
     *
     * @param testDescription The description of the test case.
     * @param sortFilterOption The sort or filter option to apply.
     * @param filterValue The value to filter by (if applicable).
     */
    @Test(dataProvider = "sortAndFilterOptions")
    public void testProductManagement(String testDescription, String sortFilterOption, String filterValue) {
        LogManager.getLogger().info("Running test: {}", testDescription);

        productManagementScreen.navigateToProductManagementScreen();

        switch (sortFilterOption) {
            case "RECENT_UPDATED" -> productManagementScreen.checkSortByRecentUpdated();
            case "STOCK_HIGH_TO_LOW" -> productManagementScreen.checkSortByStockHighToLow();
            case "STOCK_LOW_TO_HIGH" -> productManagementScreen.checkSortByStockLowToHigh();
            case "PRIORITY_HIGH_TO_LOW" -> productManagementScreen.checkSortByPriorityHighToLow();
            case "PRIORITY_LOW_TO_HIGH" -> productManagementScreen.checkSortByPriorityLowToHigh();
            case "STATUS" -> productManagementScreen.checkFilterByStatus(filterValue);
            case "CHANNEL" -> productManagementScreen.checkFilterByChannel(filterValue);
            case "PLATFORM" -> productManagementScreen.checkFilterByPlatform(filterValue);
            case "BRANCH" -> productManagementScreen.checkFilterByBranch();
            case "COLLECTION" -> productManagementScreen.checkFilterByCollections();
            default -> throw new IllegalArgumentException("Invalid filter type: " + sortFilterOption);
        }
    }
}
