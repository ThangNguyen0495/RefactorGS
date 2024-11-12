package android.seller;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.android.seller.login.LoginScreen;
import pages.android.seller.products.create_product.CreateProductScreen;
import utility.WebDriverManager;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

import static org.apache.commons.lang.math.RandomUtils.nextBoolean;

public class CreateProductTest extends AndroidGoSELLERBaseTest {
    CreateProductScreen baseProductScreen;

    @BeforeClass
    void setup() throws MalformedURLException, URISyntaxException {
        // init WebDriver
        driver = new WebDriverManager().getAndroidDriver("emulator-5554", "/Users/nguyenthang/Documents/apk_test.apk");

        // login to dashboard with login information
        new LoginScreen(driver).performLogin(credentials);

        // init product page POM
        baseProductScreen = new CreateProductScreen(driver);
    }

    //G1: Normal product without variation
    @Test
    void CR_PRODUCT_G1_01_CreateProductWithoutDimension() {
        baseProductScreen.getManageByIMEI(false)
                .getHasDimension(false)
                .navigateToCreateProductScreen()
                .createProductWithoutVariation(5);
    }

    @Test
    void CR_PRODUCT_G1_02_CreateProductWithDimension() {
        baseProductScreen.getManageByIMEI(false)
                .getHasDimension(true)
                .navigateToCreateProductScreen()
                .createProductWithoutVariation(5);
    }

    @Test
    void CR_PRODUCT_G1_03_CreateProductWithInStock() {
        baseProductScreen.getManageByIMEI(false)
                .navigateToCreateProductScreen()
                .createProductWithoutVariation(5);
    }

    @Test
    void CR_PRODUCT_G1_04_CreateProductWithOutOfStock() {
        baseProductScreen.getManageByIMEI(false)
                .navigateToCreateProductScreen()
                .createProductWithoutVariation(0);
    }

    @Test
    void CR_PRODUCT_G1_05_CreateProductWithDiscountPrice() {
        baseProductScreen.getManageByIMEI(false)
                .getHasDiscount(true)
                .navigateToCreateProductScreen()
                .createProductWithoutVariation(5);
    }

    @Test
    void CR_PRODUCT_G1_06_CreateProductWithoutDiscountPrice() {
        baseProductScreen.getManageByIMEI(false)
                .getHasDiscount(false)
                .navigateToCreateProductScreen()
                .createProductWithoutVariation(5);
    }

    @Test
    void CR_PRODUCT_G1_07_CreateProductWithoutCostPrice() {
        baseProductScreen.getManageByIMEI(false)
                .getHasCostPrice(false)
                .navigateToCreateProductScreen()
                .createProductWithoutVariation(5);
    }

    @Test
    void CR_PRODUCT_G1_08_CreateProductWithCostPrice() {
        baseProductScreen.getManageByIMEI(false).getHasCostPrice(true)
                .navigateToCreateProductScreen()
                .createProductWithoutVariation(5);
    }

    @Test
    void CR_PRODUCT_G1_09_CreateProductWithNonePlatform() {
        baseProductScreen.getManageByIMEI(false)
                .getProductSellingPlatform(false, false, false, false)
                .navigateToCreateProductScreen()
                .createProductWithoutVariation(5);
    }

    @Test
    void CR_PRODUCT_G1_10_CreateProductWithAnyPlatform() {
        baseProductScreen.getManageByIMEI(false)
                .getProductSellingPlatform(nextBoolean(), nextBoolean(), nextBoolean(), nextBoolean())
                .navigateToCreateProductScreen()
                .createProductWithoutVariation(5);
    }

    @Test
    void CR_PRODUCT_G1_11_CreateProductWithManageByLotDate() {
        baseProductScreen.getManageByIMEI(false)
                .getManageByLotDate(true)
                .navigateToCreateProductScreen()
                .createProductWithoutVariation(5);
    }

    @Test
    void CR_PRODUCT_G1_12_CreateProductWithoutManageByLotDate() {
        baseProductScreen.getManageByIMEI(false)
                .getManageByLotDate(false)
                .navigateToCreateProductScreen()
                .createProductWithoutVariation(5);
    }

    @Test
    void CR_PRODUCT_G1_13_CreateProductWithPriority() {
        baseProductScreen.getManageByIMEI(false)
                .getHasPriority(true)
                .navigateToCreateProductScreen()
                .createProductWithoutVariation(5);
    }

    @Test
    void CR_PRODUCT_G1_14_CreateProductWithoutPriority() {
        baseProductScreen.getManageByIMEI(false)
                .getHasPriority(false)
                .navigateToCreateProductScreen()
                .createProductWithoutVariation(5);
    }


    //G2: IMEI product without variation
    @Test
    void CR_PRODUCT_G2_01_CreateProductWithoutDimension() {
        baseProductScreen.getManageByIMEI(true)
                .getHasDimension(false)
                .navigateToCreateProductScreen()
                .createProductWithoutVariation(5);
    }

    @Test
    void CR_PRODUCT_G2_02_CreateProductWithDimension() {
        baseProductScreen.getManageByIMEI(true)
                .getHasDimension(true)
                .navigateToCreateProductScreen()
                .createProductWithoutVariation(5);
    }

    @Test
    void CR_PRODUCT_G2_03_CreateProductWithInStock() {
        baseProductScreen.getManageByIMEI(true)
                .navigateToCreateProductScreen()
                .createProductWithoutVariation(5);
    }

    @Test
    void CR_PRODUCT_G2_04_CreateProductWithOutOfStock() {
        baseProductScreen.getManageByIMEI(true)
                .navigateToCreateProductScreen()
                .createProductWithoutVariation(0);
    }

    @Test
    void CR_PRODUCT_G2_05_CreateProductWithDiscountPrice() {
        baseProductScreen.getManageByIMEI(true)
                .getHasDiscount(true)
                .navigateToCreateProductScreen()
                .createProductWithoutVariation(5);
    }

    @Test
    void CR_PRODUCT_G2_06_CreateProductWithoutDiscountPrice() {
        baseProductScreen.getManageByIMEI(true)
                .getHasDiscount(false)
                .navigateToCreateProductScreen()
                .createProductWithoutVariation(5);
    }

    @Test
    void CR_PRODUCT_G2_07_CreateProductWithoutCostPrice() {
        baseProductScreen.getManageByIMEI(true)
                .getHasCostPrice(false)
                .navigateToCreateProductScreen()
                .createProductWithoutVariation(5);
    }

    @Test
    void CR_PRODUCT_G2_08_CreateProductWithCostPrice() {
        baseProductScreen.getManageByIMEI(true)
                .getHasCostPrice(true)
                .navigateToCreateProductScreen()
                .createProductWithoutVariation(5);
    }

    @Test
    void CR_PRODUCT_G2_09_CreateProductWithNonePlatform() {
        baseProductScreen.getManageByIMEI(true)
                .getProductSellingPlatform(false, false, false, false)
                .navigateToCreateProductScreen()
                .createProductWithoutVariation(5);
    }

    @Test
    void CR_PRODUCT_G2_10_CreateProductWithAnyPlatform() {
        baseProductScreen.getManageByIMEI(true)
                .getProductSellingPlatform(nextBoolean(), nextBoolean(), nextBoolean(), nextBoolean())
                .navigateToCreateProductScreen()
                .createProductWithoutVariation(5);
    }

    @Test
    void CR_PRODUCT_G2_11_CreateProductWithPriority() {
        baseProductScreen.getManageByIMEI(true)
                .getHasPriority(true)
                .navigateToCreateProductScreen()
                .createProductWithoutVariation(5);
    }

    @Test
    void CR_PRODUCT_G2_12_CreateProductWithoutPriority() {
        baseProductScreen.getManageByIMEI(true)
                .getHasPriority(false)
                .navigateToCreateProductScreen()
                .createProductWithoutVariation(5);
    }

    //G3: Normal product with variation
    @Test
    void CR_PRODUCT_G3_01_CreateProductWithoutDimension() {
        baseProductScreen.getManageByIMEI(false)
                .getHasDimension(false)
                .navigateToCreateProductScreen()
                .createProductWithVariation(1, 1);
    }

    @Test
    void CR_PRODUCT_G3_02_CreateProductWithDimension() {
        baseProductScreen.getManageByIMEI(false)
                .getHasDimension(true)
                .navigateToCreateProductScreen()
                .createProductWithVariation(1, 1);
    }

    @Test
    void CR_PRODUCT_G3_03_CreateProductWithInStock() {
        baseProductScreen.getManageByIMEI(false)
                .navigateToCreateProductScreen()
                .createProductWithVariation(1, 1);
    }

    @Test
    void CR_PRODUCT_G3_04_CreateProductWithOutOfStock() {
        baseProductScreen.getManageByIMEI(false).getHasDimension(true)
                .navigateToCreateProductScreen()
                .createProductWithVariation(0);
    }

    @Test
    void CR_PRODUCT_G3_05_CreateProductWithDiscountPrice() {
        baseProductScreen.getManageByIMEI(false)
                .getHasDiscount(true)
                .navigateToCreateProductScreen()
                .createProductWithVariation(1, 1);
    }

    @Test
    void CR_PRODUCT_G3_06_CreateProductWithoutDiscountPrice() {
        baseProductScreen.getManageByIMEI(false)
                .getHasDiscount(false)
                .navigateToCreateProductScreen()
                .createProductWithVariation(1, 1);
    }

    @Test
    void CR_PRODUCT_G3_07_CreateProductWithoutCostPrice() {
        baseProductScreen.getManageByIMEI(false)
                .getHasCostPrice(false)
                .navigateToCreateProductScreen()
                .createProductWithVariation(1, 1);
    }

    @Test
    void CR_PRODUCT_G3_08_CreateProductWithCostPrice() {
        baseProductScreen.getManageByIMEI(false)
                .getHasCostPrice(true)
                .navigateToCreateProductScreen()
                .createProductWithVariation(1, 1);
    }

    @Test
    void CR_PRODUCT_G3_09_CreateProductWithNonePlatform() {
        baseProductScreen.getManageByIMEI(false)
                .getProductSellingPlatform(false, false, false, false)
                .navigateToCreateProductScreen()
                .createProductWithVariation(1, 1);
    }

    @Test
    void CR_PRODUCT_G3_10_CreateProductWithAnyPlatform() {
        baseProductScreen.getManageByIMEI(false)
                .getProductSellingPlatform(nextBoolean(), nextBoolean(), nextBoolean(), nextBoolean())
                .navigateToCreateProductScreen()
                .createProductWithVariation(1, 1);
    }

    @Test
    void CR_PRODUCT_G3_11_CreateProductWithManageByLotDate() {
        baseProductScreen.getManageByIMEI(false)
                .getManageByLotDate(true)
                .navigateToCreateProductScreen()
                .createProductWithVariation(1, 1);
    }

    @Test
    void CR_PRODUCT_G3_12_CreateProductWithoutManageByLotDate() {
        baseProductScreen.getManageByIMEI(false)
                .getManageByLotDate(false)
                .navigateToCreateProductScreen()
                .createProductWithVariation(1, 1);
    }

    @Test
    void CR_PRODUCT_G3_13_CreateProductWithPriority() {
        baseProductScreen.getManageByIMEI(false)
                .getHasPriority(true)
                .navigateToCreateProductScreen()
                .createProductWithVariation(1, 1);
    }

    @Test
    void CR_PRODUCT_G3_14_CreateProductWithoutPriority() {
        baseProductScreen.getManageByIMEI(false)
                .getHasPriority(false)
                .navigateToCreateProductScreen()
                .createProductWithVariation(1, 1);
    }

    //G4: IMEI product with variation
    @Test
    void CR_PRODUCT_G4_01_CreateProductWithoutDimension() {
        baseProductScreen.getManageByIMEI(true)
                .getHasDimension(false)
                .navigateToCreateProductScreen()
                .createProductWithVariation(1, 1);
    }

    @Test
    void CR_PRODUCT_G4_02_CreateProductWithDimension() {
        baseProductScreen.getManageByIMEI(true)
                .getHasDimension(true)
                .navigateToCreateProductScreen()
                .createProductWithVariation(1, 1);
    }

    @Test
    void CR_PRODUCT_G4_03_CreateProductWithInStock() {
        baseProductScreen.getManageByIMEI(true)
                .navigateToCreateProductScreen()
                .createProductWithVariation(1, 1);
    }

    @Test
    void CR_PRODUCT_G4_04_CreateProductWithOutOfStock() {
        baseProductScreen.getManageByIMEI(true)
                .getHasDimension(true)
                .navigateToCreateProductScreen()
                .createProductWithVariation(0);
    }

    @Test
    void CR_PRODUCT_G4_05_CreateProductWithDiscountPrice() {
        baseProductScreen.getManageByIMEI(true)
                .getHasDiscount(true)
                .navigateToCreateProductScreen()
                .createProductWithVariation(1, 1);
    }

    @Test
    void CR_PRODUCT_G4_06_CreateProductWithoutDiscountPrice() {
        baseProductScreen.getManageByIMEI(true)
                .getHasDiscount(false)
                .navigateToCreateProductScreen()
                .createProductWithVariation(1, 1);
    }

    @Test
    void CR_PRODUCT_G4_07_CreateProductWithoutCostPrice() {
        baseProductScreen.getManageByIMEI(true)
                .getHasCostPrice(false)
                .navigateToCreateProductScreen()
                .createProductWithVariation(1, 1);
    }

    @Test
    void CR_PRODUCT_G4_08_CreateProductWithCostPrice() {
        baseProductScreen.getManageByIMEI(true)
                .getHasCostPrice(true)
                .navigateToCreateProductScreen()
                .createProductWithVariation(1, 1);
    }

    @Test
    void CR_PRODUCT_G4_09_CreateProductWithNonePlatform() {
        baseProductScreen.getManageByIMEI(true)
                .getProductSellingPlatform(false, false, false, false)
                .navigateToCreateProductScreen()
                .createProductWithVariation(1, 1);
    }

    @Test
    void CR_PRODUCT_G4_10_CreateProductWithAnyPlatform() {
        baseProductScreen.getManageByIMEI(true)
                .getProductSellingPlatform(nextBoolean(), nextBoolean(), nextBoolean(), nextBoolean())
                .navigateToCreateProductScreen()
                .createProductWithVariation(1, 1);
    }

    @Test
    void CR_PRODUCT_G4_11_CreateProductWithPriority() {
        baseProductScreen.getManageByIMEI(true)
                .getHasPriority(true)
                .navigateToCreateProductScreen()
                .createProductWithVariation(1, 1);
    }

    @Test
    void CR_PRODUCT_G4_12_CreateProductWithoutPriority() {
        baseProductScreen.getManageByIMEI(true)
                .getHasPriority(false)
                .navigateToCreateProductScreen()
                .createProductWithVariation(1, 1);
    }
}
