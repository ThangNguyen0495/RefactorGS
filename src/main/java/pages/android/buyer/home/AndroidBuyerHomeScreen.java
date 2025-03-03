package pages.android.buyer.home;

import api.seller.product.APIGetProductDetail;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import utility.AndroidUtils;
import utility.PropertiesUtils;

import java.time.Duration;

public class AndroidBuyerHomeScreen {
    final static Logger logger = LogManager.getLogger(AndroidBuyerHomeScreen.class);

    WebDriver driver;
    WebDriverWait wait;
    AndroidUtils androidUtils;

    public AndroidBuyerHomeScreen(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        androidUtils = new AndroidUtils(driver);
    }

    private final By loc_icnSearch = By.xpath("//*[contains(@resource-id, ':id/bottom_navigation_tab_product')]");
    private final By loc_btnSearch = By.xpath("//*[contains(@resource-id, ':id/btn_action_bar_search')]");
    private final By loc_txtSearchBox = By.xpath("//*[contains(@resource-id, ':id/search_src_text')]");

    private By loc_lblSearchResult(String productName) {
        return By.xpath("//android.widget.TextView[@text = '%s']".formatted(productName));
    }

    private final By loc_icnAccount = By.xpath("//*[contains(@resource-id, ':id/bottom_navigation_tab_me')]");


    private void searchProductByName(String productName) {
        // click Search icon
        androidUtils.click(loc_icnSearch);
        androidUtils.click(loc_btnSearch);
        logger.info("Open search screen");

        // input search keywords
        androidUtils.sendKeys(loc_txtSearchBox, productName);
        logger.info("Search with keywords: %s".formatted(productName));

    }

    public void navigateToProductDetailPage(APIGetProductDetail.ProductInformation productInfo) {
        String productName = productInfo.getLanguages().stream()
                .filter(language -> language.getLanguage().equals(PropertiesUtils.getLangKey()))
                .findFirst()
                .map(APIGetProductDetail.ProductInformation.MainLanguage::getName)
                .orElse("");

        searchProductByName(productName);

        androidUtils.click(loc_lblSearchResult(productName));

        logger.info("Navigate to product detail screen, product name: {}", productName);
    }

    public void navigateToAccountScreen() {
        androidUtils.click(loc_icnAccount);

        logger.info("Navigate to Account screen.");
    }
}
