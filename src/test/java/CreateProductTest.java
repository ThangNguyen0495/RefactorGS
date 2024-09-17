import api.seller.login.APIDashboardLogin;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import pages.web.seller.login.LoginPage;
import pages.web.seller.product.all_products.BaseProductPage;
import utility.PropertiesUtils;
import utility.WebDriverManager;

import static org.apache.commons.lang.math.RandomUtils.nextBoolean;

@Listeners(utility.ExtendReportListener.class)
public class CreateProductTest {
    WebDriver driver;
    APIDashboardLogin.Credentials credentials;
    BaseProductPage productPage;

    @BeforeClass
    void setup() {
        // init WebDriver
        driver = new WebDriverManager().getWebDriver();

        // init login information
        credentials = new APIDashboardLogin.Credentials(PropertiesUtils.getSellerAccount(), PropertiesUtils.getSellerPassword());

        // init product page POM
        productPage = new BaseProductPage(driver).fetchInformation(credentials);

        // login to dashboard with login information
        new LoginPage(driver).loginDashboardByJs(credentials);
    }

    //G1: Normal product without variation
    @Test
    void CR_PRODUCT_G1_01_CreateProductWithoutDimension() {
        productPage.setHasDimension(false)
                .navigateToCreateProductPage()
                .createWithoutVariationProduct(false, 5);
    }

    @Test
    void CR_PRODUCT_G1_02_CreateProductWitDimension() {
        productPage.setHasDimension(true)
                .navigateToCreateProductPage()
                .createWithoutVariationProduct(false, 5);
    }

    @Test
    void CR_PRODUCT_G1_03_CreateProductWithInStock() {
        productPage.navigateToCreateProductPage()
                .createWithoutVariationProduct(false, 5);
    }

    @Test
    void CR_PRODUCT_G1_04_CreateProductWithOutOfStock() {
        productPage.setHasDimension(true)
                .navigateToCreateProductPage()
                .createWithoutVariationProduct(false, 0);
    }

    @Test
    void CR_PRODUCT_G1_05_CreateProductWithDiscountPrice() {
        productPage.setNoDiscount(false)
                .navigateToCreateProductPage()
                .createWithoutVariationProduct(false, 5);
    }

    @Test
    void CR_PRODUCT_G1_06_CreateProductWithoutDiscountPrice() {
        productPage.setNoDiscount(true)
                .navigateToCreateProductPage()
                .createWithoutVariationProduct(false, 5);
    }

    @Test
    void CR_PRODUCT_G1_07_CreateProductWithoutCostPrice() {
        productPage.setNoCost(true)
                .navigateToCreateProductPage()
                .createWithoutVariationProduct(false, 5);
    }

    @Test
    void CR_PRODUCT_G1_08_CreateProductWithCostPrice() {
        productPage.setNoCost(false)
                .navigateToCreateProductPage()
                .createWithoutVariationProduct(false, 5);
    }

    @Test
    void CR_PRODUCT_G1_09_CreateProductWithNonePlatform() {
        productPage.setSellingPlatform(false, false, false, false)
                .navigateToCreateProductPage()
                .createWithoutVariationProduct(false, 5);
    }

    @Test
    void CR_PRODUCT_G1_10_CreateProductWithAnyPlatform() {
        productPage.setSellingPlatform(nextBoolean(), nextBoolean(), nextBoolean(), nextBoolean())
                .navigateToCreateProductPage()
                .createWithoutVariationProduct(false, 5);
    }

    @Test
    void CR_PRODUCT_G1_11_CreateProductWithAttribution() {
        productPage.setHasAttribution(true)
                .navigateToCreateProductPage()
                .createWithoutVariationProduct(false, 5);
    }

    @Test
    void CR_PRODUCT_G1_12_CreateProductWithoutAttribution() {
        productPage.setHasAttribution(false)
                .navigateToCreateProductPage()
                .createWithoutVariationProduct(false, 5);
    }

    @Test
    void CR_PRODUCT_G1_13_CreateProductWithSEO() {
        productPage.setHasSEO(true)
                .navigateToCreateProductPage()
                .createWithoutVariationProduct(false, 5);
    }

    @Test
    void CR_PRODUCT_G1_14_CreateProductWithoutSEO() {
        productPage.setHasSEO(false)
                .navigateToCreateProductPage()
                .createWithoutVariationProduct(false, 5);
    }


    @Test
    void CR_PRODUCT_G1_15_CreateProductWithManageByLotDate() {
        productPage.setManageByLotDate(true)
                .navigateToCreateProductPage()
                .createWithoutVariationProduct(false, 5);
    }

    @Test
    void CR_PRODUCT_G1_16_CreateProductWithoutManageByLotDate() {
        productPage.setManageByLotDate(false)
                .navigateToCreateProductPage()
                .createWithoutVariationProduct(false, 5);
    }

    //G2: IMEI product without variation
    @Test
    void CR_PRODUCT_G2_01_CreateProductWithoutDimension() {
        productPage.setHasDimension(false)
                .navigateToCreateProductPage()
                .createWithoutVariationProduct(true, 5);
    }

    @Test
    void CR_PRODUCT_G2_02_CreateProductWitDimension() {
        productPage.setHasDimension(true)
                .navigateToCreateProductPage()
                .createWithoutVariationProduct(true, 5);
    }

    @Test
    void CR_PRODUCT_G2_03_CreateProductWithInStock() {
        productPage.navigateToCreateProductPage()
                .createWithoutVariationProduct(true, 5);
    }

    @Test
    void CR_PRODUCT_G2_04_CreateProductWithOutOfStock() {
        productPage.setHasDimension(true)
                .navigateToCreateProductPage()
                .createWithoutVariationProduct(false, 0);
    }

    @Test
    void CR_PRODUCT_G2_05_CreateProductWithDiscountPrice() {
        productPage.setNoDiscount(false)
                .navigateToCreateProductPage()
                .createWithoutVariationProduct(true, 5);
    }

    @Test
    void CR_PRODUCT_G2_06_CreateProductWithoutDiscountPrice() {
        productPage.setNoDiscount(true)
                .navigateToCreateProductPage()
                .createWithoutVariationProduct(true, 5);
    }

    @Test
    void CR_PRODUCT_G2_07_CreateProductWithoutCostPrice() {
        productPage.setNoCost(true)
                .navigateToCreateProductPage()
                .createWithoutVariationProduct(true, 5);
    }

    @Test
    void CR_PRODUCT_G2_08_CreateProductWithCostPrice() {
        productPage.setNoCost(false)
                .navigateToCreateProductPage()
                .createWithoutVariationProduct(true, 5);
    }

    @Test
    void CR_PRODUCT_G2_09_CreateProductWithNonePlatform() {
        productPage.setSellingPlatform(false, false, false, false)
                .navigateToCreateProductPage()
                .createWithoutVariationProduct(true, 5);
    }

    @Test
    void CR_PRODUCT_G2_10_CreateProductWithAnyPlatform() {
        productPage.setSellingPlatform(nextBoolean(), nextBoolean(), nextBoolean(), nextBoolean())
                .navigateToCreateProductPage()
                .createWithoutVariationProduct(true, 5);
    }

    @Test
    void CR_PRODUCT_G2_11_CreateProductWithAttribution() {
        productPage.setHasAttribution(true)
                .navigateToCreateProductPage()
                .createWithoutVariationProduct(true, 5);
    }

    @Test
    void CR_PRODUCT_G2_12_CreateProductWithoutAttribution() {
        productPage.setHasAttribution(false)
                .navigateToCreateProductPage()
                .createWithoutVariationProduct(true, 5);
    }

    @Test
    void CR_PRODUCT_G2_13_CreateProductWithSEO() {
        productPage.setHasSEO(true)
                .navigateToCreateProductPage()
                .createWithoutVariationProduct(true, 5);
    }

    @Test
    void CR_PRODUCT_G2_14_CreateProductWithoutSEO() {
        productPage.setHasSEO(false)
                .navigateToCreateProductPage()
                .createWithoutVariationProduct(true, 5);
    }

    //G3: Normal product with variation
    @Test
    void CR_PRODUCT_G3_01_CreateProductWithoutDimension() throws Exception {
        productPage.setHasDimension(false)
                .navigateToCreateProductPage()
                .createVariationProduct(false, 1, 1);
    }

    @Test
    void CR_PRODUCT_G3_02_CreateProductWitDimension() throws Exception {
        productPage.setHasDimension(true)
                .navigateToCreateProductPage()
                .createVariationProduct(false, 1, 1);
    }

    @Test
    void CR_PRODUCT_G3_03_CreateProductWithInStock() throws Exception {
        productPage.navigateToCreateProductPage()
                .createVariationProduct(false, 1, 1);
    }

    @Test
    void CR_PRODUCT_G3_04_CreateProductWithOutOfStock() {
        productPage.setHasDimension(true)
                .navigateToCreateProductPage()
                .createWithoutVariationProduct(false, 0);
    }

    @Test
    void CR_PRODUCT_G3_05_CreateProductWithDiscountPrice() throws Exception {
        productPage.setNoDiscount(false)
                .navigateToCreateProductPage()
                .createVariationProduct(false, 1, 1);
    }

    @Test
    void CR_PRODUCT_G3_06_CreateProductWithoutDiscountPrice() throws Exception {
        productPage.setNoDiscount(true)
                .navigateToCreateProductPage()
                .createVariationProduct(false, 1, 1);
    }

    @Test
    void CR_PRODUCT_G3_07_CreateProductWithoutCostPrice() throws Exception {
        productPage.setNoCost(true)
                .navigateToCreateProductPage()
                .createVariationProduct(false, 1, 1);
    }

    @Test
    void CR_PRODUCT_G3_08_CreateProductWithCostPrice() throws Exception {
        productPage.setNoCost(false)
                .navigateToCreateProductPage()
                .createVariationProduct(false, 1, 1);
    }

    @Test
    void CR_PRODUCT_G3_09_CreateProductWithNonePlatform() throws Exception {
        productPage.setSellingPlatform(false, false, false, false)
                .navigateToCreateProductPage()
                .createVariationProduct(false, 1, 1);
    }

    @Test
    void CR_PRODUCT_G3_10_CreateProductWithAnyPlatform() throws Exception {
        productPage.setSellingPlatform(nextBoolean(), nextBoolean(), nextBoolean(), nextBoolean())
                .navigateToCreateProductPage()
                .createVariationProduct(false, 1, 1);
    }

    @Test
    void CR_PRODUCT_G3_11_CreateProductWithAttribution() throws Exception {
        productPage.setHasAttribution(true)
                .navigateToCreateProductPage()
                .createVariationProduct(false, 1, 1);
    }

    @Test
    void CR_PRODUCT_G3_12_CreateProductWithoutAttribution() throws Exception {
        productPage.setHasAttribution(false)
                .navigateToCreateProductPage()
                .createVariationProduct(false, 1, 1);
    }

    @Test
    void CR_PRODUCT_G3_13_CreateProductWithSEO() throws Exception {
        productPage.setHasSEO(true)
                .navigateToCreateProductPage()
                .createVariationProduct(false, 1, 1);
    }

    @Test
    void CR_PRODUCT_G3_14_CreateProductWithoutSEO() throws Exception {
        productPage.setHasSEO(false)
                .navigateToCreateProductPage()
                .createVariationProduct(false, 1, 1);
    }


    @Test
    void CR_PRODUCT_G3_15_CreateProductWithManageByLotDate() throws Exception {
        productPage.setManageByLotDate(true)
                .navigateToCreateProductPage()
                .createVariationProduct(false, 1, 1);
    }

    @Test
    void CR_PRODUCT_G3_16_CreateProductWithoutManageByLotDate() throws Exception {
        productPage.setManageByLotDate(false)
                .navigateToCreateProductPage()
                .createVariationProduct(false, 1, 1);
    }

    //G4: IMEI product with variation
    @Test
    void CR_PRODUCT_G4_01_CreateProductWithoutDimension() throws Exception {
        productPage.setHasDimension(false)
                .navigateToCreateProductPage()
                .createVariationProduct(false, 1, 1);
    }

    @Test
    void CR_PRODUCT_G4_02_CreateProductWitDimension() throws Exception {
        productPage.setHasDimension(true)
                .navigateToCreateProductPage()
                .createVariationProduct(false, 1, 1);
    }

    @Test
    void CR_PRODUCT_G4_03_CreateProductWithInStock() throws Exception {
        productPage.navigateToCreateProductPage()
                .createVariationProduct(false, 1, 1);
    }

    @Test
    void CR_PRODUCT_G4_04_CreateProductWithOutOfStock() {
        productPage.setHasDimension(true)
                .navigateToCreateProductPage()
                .createWithoutVariationProduct(false, 0);
    }

    @Test
    void CR_PRODUCT_G4_05_CreateProductWithDiscountPrice() throws Exception {
        productPage.setNoDiscount(false)
                .navigateToCreateProductPage()
                .createVariationProduct(false, 1, 1);
    }

    @Test
    void CR_PRODUCT_G4_06_CreateProductWithoutDiscountPrice() throws Exception {
        productPage.setNoDiscount(true)
                .navigateToCreateProductPage()
                .createVariationProduct(false, 1, 1);
    }

    @Test
    void CR_PRODUCT_G4_07_CreateProductWithoutCostPrice() throws Exception {
        productPage.setNoCost(true)
                .navigateToCreateProductPage()
                .createVariationProduct(false, 1, 1);
    }

    @Test
    void CR_PRODUCT_G4_08_CreateProductWithCostPrice() throws Exception {
        productPage.setNoCost(false)
                .navigateToCreateProductPage()
                .createVariationProduct(false, 1, 1);
    }

    @Test
    void CR_PRODUCT_G4_09_CreateProductWithNonePlatform() throws Exception {
        productPage.setSellingPlatform(false, false, false, false)
                .navigateToCreateProductPage()
                .createVariationProduct(false, 1, 1);
    }

    @Test
    void CR_PRODUCT_G4_10_CreateProductWithAnyPlatform() throws Exception {
        productPage.setSellingPlatform(nextBoolean(), nextBoolean(), nextBoolean(), nextBoolean())
                .navigateToCreateProductPage()
                .createVariationProduct(false, 1, 1);
    }

    @Test
    void CR_PRODUCT_G4_11_CreateProductWithAttribution() throws Exception {
        productPage.setHasAttribution(true)
                .navigateToCreateProductPage()
                .createVariationProduct(true, 1, 1);
    }

    @Test
    void CR_PRODUCT_G4_12_CreateProductWithoutAttribution() throws Exception {
        productPage.setHasAttribution(false)
                .navigateToCreateProductPage()
                .createVariationProduct(true, 1, 1);
    }

    @Test
    void CR_PRODUCT_G4_13_CreateProductWithSEO() throws Exception {
        productPage.setHasSEO(true)
                .navigateToCreateProductPage()
                .createVariationProduct(false, 1, 1);
    }

    @Test
    void CR_PRODUCT_G4_14_CreateProductWithoutSEO() throws Exception {
        productPage.setHasSEO(false)
                .navigateToCreateProductPage()
                .createVariationProduct(false, 1, 1);
    }
}
