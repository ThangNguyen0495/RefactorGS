import api.seller.login.APIDashboardLogin;
import api.seller.product.APICreateProduct;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import pages.web.seller.login.LoginPage;
import pages.web.seller.product.all_products.BaseProductPage;
import utility.PropertiesUtils;
import utility.WebDriverManager;

import static org.apache.commons.lang.math.RandomUtils.nextBoolean;

@Listeners(utility.ExtendReportListener.class)
public class UpdateProductTest {
    APIDashboardLogin.Credentials credentials;
    BaseProductPage productPage;
    int productId;
    WebDriver driver;
    APICreateProduct apiCreateProduct;

    @BeforeClass
    void setup() {
        // init WebDriver
        driver = new WebDriverManager().getWebDriver();

        // init login information
        credentials = new APIDashboardLogin.Credentials(PropertiesUtils.getSellerAccount(), PropertiesUtils.getSellerPassword());

        // login to dashboard with login information
        new LoginPage(driver).loginDashboardByJs(credentials);

        // init POM
        productPage = new BaseProductPage(driver)
                .fetchInformation(credentials);

        // init API
        apiCreateProduct = new api.seller.product.APICreateProduct(credentials);
    }

    @BeforeGroups(groups = "[WEB][UPDATE] Normal product - Without variation")
    void preCondition_G1() {
        // get product ID
        productId = apiCreateProduct.createProduct(false, false, 5);
    }

    @BeforeGroups(groups = "[WEB][UPDATE] IMEI product - Without variation")
    void preCondition_G2() {
        // get product ID
        productId = apiCreateProduct.createProduct(true, false, 5);
    }

    @BeforeGroups(groups = "[WEB][UPDATE] Normal product - Variation")
    void preCondition_G3() {
        productId = apiCreateProduct.createProduct(false, true, 5);
    }

    @BeforeGroups(groups = "[WEB][UPDATE] IMEI product - Variation")
    void preCondition_G4() {
        productId = apiCreateProduct.createProduct(true, true, 5);
    }

    //G1: Normal product without variation
    @Test(groups = "[WEB][UPDATE] Normal product - Without variation")
    void UP_PRODUCT_G1_01_UpdateProductWithoutDimension() {
        productPage.setHasDimension(false)
                .navigateToUpdateProductPage(productId)
                .updateWithoutVariationProduct(5);

    }

    @Test(groups = "[WEB][UPDATE] Normal product - Without variation")
    void UP_PRODUCT_G1_02_UpdateProductWitDimension() {
        productPage.setHasDimension(true)
                .navigateToUpdateProductPage(productId)
                .updateWithoutVariationProduct(5);

    }

    @Test(groups = "[WEB][UPDATE] Normal product - Without variation")
    void UP_PRODUCT_G1_03_UpdateProductWithInStock() {
        productPage.navigateToUpdateProductPage(productId)
                .updateWithoutVariationProduct(5);

    }

    @Test(groups = "[WEB][UPDATE] Normal product - Without variation")
    void UP_PRODUCT_G1_04_UpdateProductWithOutOfStock() {
        productPage.setHasDimension(true)
                .navigateToUpdateProductPage(productId)
                .updateWithoutVariationProduct(0);
    }

    @Test(groups = "[WEB][UPDATE] Normal product - Without variation")
    void UP_PRODUCT_G1_05_UpdateProductWithDiscountPrice() {
        productPage.setNoDiscount(false)
                .navigateToUpdateProductPage(productId)
                .updateWithoutVariationProduct(5);

    }

    @Test(groups = "[WEB][UPDATE] Normal product - Without variation")
    void UP_PRODUCT_G1_06_UpdateProductWithoutDiscountPrice() {
        productPage.setNoDiscount(true)
                .navigateToUpdateProductPage(productId)
                .updateWithoutVariationProduct(5);

    }

    @Test(groups = "[WEB][UPDATE] Normal product - Without variation")
    void UP_PRODUCT_G1_07_UpdateProductWithoutCostPrice() {
        productPage.setNoCost(true)
                .navigateToUpdateProductPage(productId)
                .updateWithoutVariationProduct(5);

    }

    @Test(groups = "[WEB][UPDATE] Normal product - Without variation")
    void UP_PRODUCT_G1_08_UpdateProductWithCostPrice() {
        productPage.setNoCost(false)
                .navigateToUpdateProductPage(productId)
                .updateWithoutVariationProduct(5);

    }

    @Test(groups = "[WEB][UPDATE] Normal product - Without variation")
    void UP_PRODUCT_G1_09_UpdateProductWithNonePlatform() {
        productPage.setSellingPlatform(false, false, false, false)
                .navigateToUpdateProductPage(productId)
                .updateWithoutVariationProduct(5);

    }

    @Test(groups = "[WEB][UPDATE] Normal product - Without variation")
    void UP_PRODUCT_G1_10_UpdateProductWithAnyPlatform() {
        productPage.setSellingPlatform(nextBoolean(), nextBoolean(), nextBoolean(), nextBoolean())
                .navigateToUpdateProductPage(productId)
                .updateWithoutVariationProduct(5);

    }

    @Test(groups = "[WEB][UPDATE] Normal product - Without variation")
    void UP_PRODUCT_G1_11_UpdateProductWithAttribution() {
        productPage.setHasAttribution(true)
                .navigateToUpdateProductPage(productId)
                .updateWithoutVariationProduct(5);

    }

    @Test(groups = "[WEB][UPDATE] Normal product - Without variation")
    void UP_PRODUCT_G1_12_UpdateProductWithoutAttribution() {
        productPage.setHasAttribution(false)
                .navigateToUpdateProductPage(productId)
                .updateWithoutVariationProduct(5);

    }

    @Test(groups = "[WEB][UPDATE] Normal product - Without variation")
    void UP_PRODUCT_G1_13_UpdateProductWithSEO() {
        productPage.setHasSEO(true)
                .navigateToUpdateProductPage(productId)
                .updateWithoutVariationProduct(5);

    }

    @Test(groups = "[WEB][UPDATE] Normal product - Without variation")
    void UP_PRODUCT_G1_14_UpdateProductWithoutSEO() {
        productPage.setHasSEO(false)
                .navigateToUpdateProductPage(productId)
                .updateWithoutVariationProduct(5);

    }

    @Test(groups = "[WEB][UPDATE] Normal product - Without variation")
    void UP_PRODUCT_G1_15_UpdateProductWithoutManageByLotDate() {
        productPage.setManageByLotDate(false)
                .navigateToUpdateProductPage(productId)
                .updateWithoutVariationProduct(5);

    }

    @Test(groups = "[WEB][UPDATE] Normal product - Without variation")
    void UP_PRODUCT_G1_16_UpdateProductWithManageByLotDate() {
        productPage.setManageByLotDate(true)
                .navigateToUpdateProductPage(productId)
                .updateWithoutVariationProduct(5);

    }

    @Test(groups = "[WEB][UPDATE] Normal product - Without variation")
    void UP_PRODUCT_G1_17_EditTranslation() {
        productPage.editTranslation(productId);
    }

    @Test(groups = "[WEB][UPDATE] Normal product - Without variation")
    void UP_PRODUCT_G1_18_ChangeProductStatus() {
        productPage.changeProductStatus("INACTIVE", productId);
    }

    @Test(groups = "[WEB][UPDATE] Normal product - Without variation")
    void UP_PRODUCT_G1_19_AddWholesaleProduct() {
        productPage.configWholesaleProduct(productId);

    }

    @Test(groups = "[WEB][UPDATE] Normal product - Without variation")
    void UP_PRODUCT_G1_20_AddConversionUnit() {
        productPage.configConversionUnit(productId);

    }

    @Test(groups = "[WEB][UPDATE] Normal product - Without variation")
    void UP_PRODUCT_G1_21_DeleteProduct() {
        productPage.deleteProduct(productId);
    }

    //G2: IMEI product without variation
    @Test(groups = "[WEB][UPDATE] IMEI product - Without variation")
    void UP_PRODUCT_G2_01_UpdateProductWithoutDimension() {
        productPage.setHasDimension(false)
                .navigateToUpdateProductPage(productId)
                .updateWithoutVariationProduct(5);

    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Without variation")
    void UP_PRODUCT_G2_02_UpdateProductWitDimension() {
        productPage.setHasDimension(true)
                .navigateToUpdateProductPage(productId)
                .updateWithoutVariationProduct(5);

    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Without variation")
    void UP_PRODUCT_G2_03_UpdateProductWithInStock() {
        productPage.navigateToUpdateProductPage(productId)
                .updateWithoutVariationProduct(5);

    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Without variation")
    void UP_PRODUCT_G2_04_UpdateProductWithOutOfStock() {
        productPage.setHasDimension(true)
                .navigateToUpdateProductPage(productId)
                .updateWithoutVariationProduct(0);


    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Without variation")
    void UP_PRODUCT_G2_05_UpdateProductWithDiscountPrice() {
        productPage.setNoDiscount(false)
                .navigateToUpdateProductPage(productId)
                .updateWithoutVariationProduct(5);

    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Without variation")
    void UP_PRODUCT_G2_06_UpdateProductWithoutDiscountPrice() {
        productPage.setNoDiscount(true)
                .navigateToUpdateProductPage(productId)
                .updateWithoutVariationProduct(5);

    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Without variation")
    void UP_PRODUCT_G2_07_UpdateProductWithoutCostPrice() {
        productPage.setNoCost(true)
                .navigateToUpdateProductPage(productId)
                .updateWithoutVariationProduct(5);

    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Without variation")
    void UP_PRODUCT_G2_08_UpdateProductWithCostPrice() {
        productPage.setNoCost(false)
                .navigateToUpdateProductPage(productId)
                .updateWithoutVariationProduct(5);

    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Without variation")
    void UP_PRODUCT_G2_09_UpdateProductWithNonePlatform() {
        productPage.setSellingPlatform(false, false, false, false)
                .navigateToUpdateProductPage(productId)
                .updateWithoutVariationProduct(5);

    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Without variation")
    void UP_PRODUCT_G2_10_UpdateProductWithAnyPlatform() {
        productPage.setSellingPlatform(nextBoolean(), nextBoolean(), nextBoolean(), nextBoolean())
                .navigateToUpdateProductPage(productId)
                .updateWithoutVariationProduct(5);

    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Without variation")
    void UP_PRODUCT_G2_11_UpdateProductWithAttribution() {
        productPage.setHasAttribution(true)
                .navigateToUpdateProductPage(productId)
                .updateWithoutVariationProduct(5);

    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Without variation")
    void UP_PRODUCT_G2_12_UpdateProductWithoutAttribution() {
        productPage.setHasAttribution(false)
                .navigateToUpdateProductPage(productId)
                .updateWithoutVariationProduct(5);

    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Without variation")
    void UP_PRODUCT_G2_13_UpdateProductWithSEO() {
        productPage.setHasSEO(true)
                .navigateToUpdateProductPage(productId)
                .updateWithoutVariationProduct(5);

    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Without variation")
    void UP_PRODUCT_G2_14_UpdateProductWithoutSEO() {
        productPage.setHasSEO(false)
                .navigateToUpdateProductPage(productId)
                .updateWithoutVariationProduct(5);

    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Without variation")
    void UP_PRODUCT_G2_15_EditTranslation() {
        productPage.editTranslation(productId);
    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Without variation")
    void UP_PRODUCT_G2_16_ChangeProductStatus() {
        productPage.changeProductStatus("INACTIVE", productId);
    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Without variation")
    void UP_PRODUCT_G2_17_AddWholesaleProduct() {

        productPage.configWholesaleProduct(productId);

    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Without variation")
    void UP_PRODUCT_G2_18_DeleteProduct() {
        productPage.deleteProduct(productId);

    }

    //G3: Normal product with variation
    @Test(groups = "[WEB][UPDATE] Normal product - Variation")
    void UP_PRODUCT_G3_01_UpdateProductWithoutDimension() throws InterruptedException {
        productPage.setHasDimension(false)
                .navigateToUpdateProductPage(productId)
                .updateVariationProduct(1);

    }

    @Test(groups = "[WEB][UPDATE] Normal product - Variation")
    void UP_PRODUCT_G3_02_UpdateProductWitDimension() throws InterruptedException {
        productPage.setHasDimension(true)
                .navigateToUpdateProductPage(productId)
                .updateVariationProduct(1);

    }

    @Test(groups = "[WEB][UPDATE] Normal product - Variation")
    void UP_PRODUCT_G3_03_UpdateProductWithInStock() throws InterruptedException {
        productPage.navigateToUpdateProductPage(productId)
                .updateVariationProduct(1);

    }

    @Test(groups = "[WEB][UPDATE] Normal product - Variation")
    void UP_PRODUCT_G3_04_UpdateProductWithOutOfStock() throws InterruptedException {
        productPage.setHasDimension(true)
                .navigateToUpdateProductPage(productId)
                .updateVariationProduct(0);


    }

    @Test(groups = "[WEB][UPDATE] Normal product - Variation")
    void UP_PRODUCT_G3_05_UpdateProductWithDiscountPrice() throws InterruptedException {
        productPage.setNoDiscount(false)
                .navigateToUpdateProductPage(productId)
                .updateVariationProduct(1);

    }

    @Test(groups = "[WEB][UPDATE] Normal product - Variation")
    void UP_PRODUCT_G3_06_UpdateProductWithoutDiscountPrice() throws InterruptedException {
        productPage.setNoDiscount(true)
                .navigateToUpdateProductPage(productId)
                .updateVariationProduct(1);

    }

    @Test(groups = "[WEB][UPDATE] Normal product - Variation")
    void UP_PRODUCT_G3_07_UpdateProductWithoutCostPrice() throws InterruptedException {
        productPage.setNoCost(true)
                .navigateToUpdateProductPage(productId)
                .updateVariationProduct(1);

    }

    @Test(groups = "[WEB][UPDATE] Normal product - Variation")
    void UP_PRODUCT_G3_08_UpdateProductWithCostPrice() throws InterruptedException {
        productPage.setNoCost(false)
                .navigateToUpdateProductPage(productId)
                .updateVariationProduct(1);

    }

    @Test(groups = "[WEB][UPDATE] Normal product - Variation")
    void UP_PRODUCT_G3_09_UpdateProductWithNonePlatform() throws InterruptedException {
        productPage.setSellingPlatform(false, false, false, false)
                .navigateToUpdateProductPage(productId)
                .updateVariationProduct(1);

    }

    @Test(groups = "[WEB][UPDATE] Normal product - Variation")
    void UP_PRODUCT_G3_10_UpdateProductWithAnyPlatform() throws InterruptedException {
        productPage.setSellingPlatform(nextBoolean(), nextBoolean(), nextBoolean(), nextBoolean())
                .navigateToUpdateProductPage(productId)
                .updateVariationProduct(1);

    }

    @Test(groups = "[WEB][UPDATE] Normal product - Variation")
    void UP_PRODUCT_G3_11_UpdateProductWithAttribution() throws InterruptedException {
        productPage.setHasAttribution(true)
                .navigateToUpdateProductPage(productId)
                .updateVariationProduct(1)
                .addVariationAttribution();

    }

    @Test(groups = "[WEB][UPDATE] Normal product - Variation")
    void UP_PRODUCT_G3_12_UpdateProductWithoutAttribution() throws InterruptedException {
        productPage.setHasAttribution(false)
                .navigateToUpdateProductPage(productId)
                .updateVariationProduct(1);

    }

    @Test(groups = "[WEB][UPDATE] Normal product - Variation")
    void UP_PRODUCT_G3_13_UpdateProductWithSEO() throws InterruptedException {
        productPage.setHasSEO(true)
                .navigateToUpdateProductPage(productId)
                .updateVariationProduct(1);

    }

    @Test(groups = "[WEB][UPDATE] Normal product - Variation")
    void UP_PRODUCT_G3_14_UpdateProductWithoutSEO() throws InterruptedException {
        productPage.setHasSEO(false)
                .navigateToUpdateProductPage(productId)
                .updateVariationProduct(1);

    }

    @Test(groups = "[WEB][UPDATE] Normal product - Variation")
    void UP_PRODUCT_G3_15_UpdateProductWithoutManageByLotDate() throws InterruptedException {
        productPage.setManageByLotDate(false)
                .navigateToUpdateProductPage(productId)
                .updateVariationProduct(1);

    }

    @Test(groups = "[WEB][UPDATE] Normal product - Variation")
    void UP_PRODUCT_G3_16_UpdateProductWithManageByLotDate() throws InterruptedException {
        productPage.setManageByLotDate(true)
                .navigateToUpdateProductPage(productId)
                .updateVariationProduct(1);

    }

    @Test(groups = "[WEB][UPDATE] Normal product - Variation")
    void UP_PRODUCT_G3_17_EditTranslationForMainProduct() {
        productPage.editTranslation(productId);
    }

    @Test(groups = "[WEB][UPDATE] Normal product - Variation")
    void UP_PRODUCT_G3_18_EditTranslationForEachVariation() {
        productPage.editVariationTranslation(productId);
    }

    @Test(groups = "[WEB][UPDATE] Normal product - Variation")
    void UP_PRODUCT_G3_19_ChangeProductStatus() {
        productPage.changeProductStatus("INACTIVE", productId);
    }

    @Test(groups = "[WEB][UPDATE] Normal product - Variation")
    void UP_PRODUCT_G3_20_ChangeVariationStatus() {
        productPage.changeProductStatus("ACTIVE", productId).changeVariationStatus(productId);
    }

    @Test(groups = "[WEB][UPDATE] Normal product - Variation")
    void UP_PRODUCT_G3_21_AddWholesaleProduct() {

        productPage.configWholesaleProduct(productId);

    }

    @Test(groups = "[WEB][UPDATE] Normal product - Variation")
    void UP_PRODUCT_G3_22_AddConversionUnit() {

        productPage.configWholesaleProduct(productId);

    }

    @Test(groups = "[WEB][UPDATE] Normal product - Variation")
    void UP_PRODUCT_G3_23_DeleteProduct() {
        productPage.deleteProduct(productId);

    }

    //G4: IMEI product with variation
    @Test(groups = "[WEB][UPDATE] IMEI product - Variation")
    void UP_PRODUCT_G4_01_UpdateProductWithoutDimension() throws InterruptedException {
        productPage.setHasDimension(false)
                .navigateToUpdateProductPage(productId)
                .updateVariationProduct(1);

    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Variation")
    void UP_PRODUCT_G4_02_UpdateProductWitDimension() throws InterruptedException {
        productPage.setHasDimension(true)
                .navigateToUpdateProductPage(productId)
                .updateVariationProduct(1);

    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Variation")
    void UP_PRODUCT_G4_03_UpdateProductWithInStock() throws InterruptedException {
        productPage.navigateToUpdateProductPage(productId)
                .updateVariationProduct(1);

    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Variation")
    void UP_PRODUCT_G4_04_UpdateProductWithOutOfStock() throws InterruptedException {
        productPage.setHasDimension(true)
                .navigateToUpdateProductPage(productId)
                .updateVariationProduct(0);


    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Variation")
    void UP_PRODUCT_G4_05_UpdateProductWithDiscountPrice() throws InterruptedException {
        productPage.setNoDiscount(false)
                .navigateToUpdateProductPage(productId)
                .updateVariationProduct(1);

    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Variation")
    void UP_PRODUCT_G4_06_UpdateProductWithoutDiscountPrice() throws InterruptedException {
        productPage.setNoDiscount(true)
                .navigateToUpdateProductPage(productId)
                .updateVariationProduct(1);

    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Variation")
    void UP_PRODUCT_G4_07_UpdateProductWithoutCostPrice() throws InterruptedException {
        productPage.setNoCost(true)
                .navigateToUpdateProductPage(productId)
                .updateVariationProduct(1);

    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Variation")
    void UP_PRODUCT_G4_08_UpdateProductWithCostPrice() throws InterruptedException {
        productPage.setNoCost(false)
                .navigateToUpdateProductPage(productId)
                .updateVariationProduct(1);

    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Variation")
    void UP_PRODUCT_G4_09_UpdateProductWithNonePlatform() throws InterruptedException {
        productPage.setSellingPlatform(false, false, false, false)
                .navigateToUpdateProductPage(productId)
                .updateVariationProduct(1);

    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Variation")
    void UP_PRODUCT_G4_10_UpdateProductWithAnyPlatform() throws InterruptedException {
        productPage.setSellingPlatform(nextBoolean(), nextBoolean(), nextBoolean(), nextBoolean())
                .navigateToUpdateProductPage(productId)
                .updateVariationProduct(1);

    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Variation")
    void UP_PRODUCT_G4_11_UpdateProductWithAttribution() throws InterruptedException {
        productPage.setHasAttribution(true)
                .navigateToUpdateProductPage(productId)
                .updateVariationProduct(1)
                .addVariationAttribution();

    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Variation")
    void UP_PRODUCT_G4_12_UpdateProductWithoutAttribution() throws InterruptedException {
        productPage.setHasAttribution(false)
                .navigateToUpdateProductPage(productId)
                .updateVariationProduct(1);

    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Variation")
    void UP_PRODUCT_G4_13_UpdateProductWithSEO() throws InterruptedException {
        productPage.setHasSEO(true)
                .navigateToUpdateProductPage(productId)
                .updateVariationProduct(1);

    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Variation")
    void UP_PRODUCT_G4_14_UpdateProductWithoutSEO() throws InterruptedException {
        productPage.setHasSEO(false)
                .navigateToUpdateProductPage(productId)
                .updateVariationProduct(1);

    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Variation")
    void UP_PRODUCT_G4_15_EditTranslationForMainProduct() {
        productPage.editTranslation(productId);
    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Variation")
    void UP_PRODUCT_G4_16_EditTranslationForEachVariation() {
        productPage.editVariationTranslation(productId);
    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Variation")
    void UP_PRODUCT_G4_17_ChangeProductStatus() {
        productPage.changeProductStatus("INACTIVE", productId);
    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Variation")
    void UP_PRODUCT_G4_18_ChangeVariationStatus() {
        productPage.changeProductStatus("ACTIVE", productId).changeVariationStatus(productId);
    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Variation")
    void UP_PRODUCT_G4_19_AddWholesaleProduct() {

        productPage.configWholesaleProduct(productId);

    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Variation")
    void UP_PRODUCT_G4_20_DeleteProduct() {
        productPage.deleteProduct(productId);

    }
}
