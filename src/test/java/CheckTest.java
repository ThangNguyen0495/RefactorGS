import api.seller.supplier.APICreateSupplier;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import pages.ios.seller.login.LoginScreen;
import pages.web.seller.login.DashboardLoginPage;
import pages.web.seller.suppliers.all_suppliers.BaseSupplierPage;
import utility.ListenerUtils;
import utility.PropertiesUtils;
import utility.WebDriverManager;


@Listeners(ListenerUtils.class)
public class CheckTest {

    @Test
    void t() {
//        new DashboardLoginPage(driver).loginDashboardByJs(PropertiesUtils.getSellerCredentials());
//        new BaseSupplierPage(driver)
//                .fetchSupplierInformation(PropertiesUtils.getSellerCredentials(), true)
//                .navigateToSupplierDetailPageByItsId(902)
//                .createNewSupplier()
//                .verifySupplierInformation();
        new APICreateSupplier(PropertiesUtils.getSellerCredentials()).createThenGetSupplierId();
    }

    WebDriver driver;
//
//    @BeforeMethod
//    void set() {
//        driver = WebDriverManager.getWebDriver();
//    }
//
//    @AfterMethod
//    void clear() {
//        driver.quit();
//    }

}
