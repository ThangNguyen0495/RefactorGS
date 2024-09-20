import api.seller.login.APIDashboardLogin;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import pages.web.seller.login.LoginPage;
import pages.web.seller.product.all_products.ProductManagementPage;
import utility.ExtendReportListener;
import utility.PropertiesUtils;
import utility.WebDriverManager;

@Listeners(ExtendReportListener.class)
public class ProductManagementTest {
    public WebDriver driver;
    APIDashboardLogin.Credentials loginInformation;
    ProductManagementPage productManagementPage;

    @BeforeClass
    void setup() {
        driver = new WebDriverManager().getWebDriver();
        loginInformation = new APIDashboardLogin.Credentials(PropertiesUtils.getSellerAccount(), PropertiesUtils.getSellerPassword());
        new LoginPage(driver).loginDashboardByJs(loginInformation);
        productManagementPage = new ProductManagementPage(driver).fetchInformation(loginInformation);
    }

    @AfterClass
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void MN_PRODUCT_01_CheckBulkActionClearStock() {
        productManagementPage.bulkClearStock();
    }

    @Test
    void MN_PRODUCT_02_CheckBulkActionDelete() {
        productManagementPage.bulkDeleteProducts();
    }

    @Test
    void MN_PRODUCT_03_CheckBulkActionDeactivate() {
        productManagementPage.bulkDeactivateProduct();
    }

    @Test
    void MN_PRODUCT_04_CheckBulkActionActivate() {
        productManagementPage.bulkActivateProduct();
    }

    @Test
    void MN_PRODUCT_05_CheckBulkActionUpdateStock() {
        productManagementPage.bulkUpdateStock();
    }

    @Test
    void MN_PRODUCT_06_CheckBulkActionUpdateTax() {
        productManagementPage.bulkUpdateTax();
    }

    @Test
    void MN_PRODUCT_07_CheckBulkActionDisplayOutOfStockProduct() {
        productManagementPage.bulkDisplayOutOfStockProduct();
    }

    @Test
    void MN_PRODUCT_08_CheckBulkActionUpdateSellingPlatform() {
        productManagementPage.bulkUpdateSellingPlatform();
    }

    @Test
    void MN_PRODUCT_09_CheckBulkActionUpdatePrice() {
        productManagementPage.bulkUpdatePrice();
    }

    @Test
    void MN_PRODUCT_10_CheckBulkActionSetStockAlert() {
        productManagementPage.bulkSetStockAlert();
    }

    @Test
    void MN_PRODUCT_11_CheckBulkActionManageStockByLotDate() {
        productManagementPage.bulkManageStockByLotDate();
    }

}
