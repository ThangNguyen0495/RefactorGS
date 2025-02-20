package pages.ios.buyer.home;

import api.seller.product.APIGetProductDetail;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import utility.IOSUtils;
import utility.PropertiesUtils;

import java.time.Duration;

public class IOSBuyerHomeScreen {
    final static Logger logger = LogManager.getLogger(IOSBuyerHomeScreen.class);

    WebDriver driver;
    WebDriverWait wait;
    IOSUtils iosUtils;

    public IOSBuyerHomeScreen(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        iosUtils = new IOSUtils(driver);
    }

    private final By loc_icnSearch = By.xpath("//XCUIElementTypeButton[@name=\"productTabBarItem\"]");
    private final By loc_btnSearch = By.xpath("//XCUIElementTypeButton[@name=\"Tìm kiếm\" or @name=\"Search\"]");
    private final By loc_txtSearchBox = By.xpath("//XCUIElementTypeTextField[@value=\"Nhập từ khóa\" or @value=\"Type a keyword\"]");
    private By loc_lblSearchResult(String productName) {
        return By.xpath("//XCUIElementTypeStaticText[@name=\"%s\"]".formatted(productName));
    }

    private final By loc_icnAccount = By.xpath("//XCUIElementTypeButton[@name=\"accountTabBarItem\"]");


    private void searchProductByName(String productName) {
        // click Search icon
        iosUtils.click(loc_icnSearch);
        iosUtils.click(loc_btnSearch);
        logger.info("Open search screen");

        // input search keywords
        iosUtils.sendKeys(loc_txtSearchBox, productName);
        logger.info("Search with keywords: %s".formatted(productName));

    }

    public void navigateToProductDetailPage(APIGetProductDetail.ProductInformation productInfo) {
        String productName = productInfo.getLanguages().stream()
                .filter(language -> language.getLanguage().equals(PropertiesUtils.getLangKey()))
                .findFirst()
                .map(APIGetProductDetail.ProductInformation.MainLanguage::getName)
                .orElse("");

        searchProductByName(productName);

        iosUtils.click(loc_lblSearchResult(productName));

        logger.info("Navigate to product detail screen, product name: {}", productName);
    }

    public void navigateToAccountScreen() {
        iosUtils.allowPermission("Allow");
        iosUtils.click(loc_icnAccount);
        logger.info("Navigate to Account screen.");
    }
}
