package pages.ios.buyer.account;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.ios.buyer.home.IOSBuyerHomeScreen;
import utility.IOSUtils;

public class IOSBuyerAccountScreen {
    private final WebDriver driver;
    private final IOSUtils androidUtils;
    private final Logger logger = LogManager.getLogger();
    public IOSBuyerAccountScreen(WebDriver driver) {
        this.driver = driver;
        this.androidUtils = new IOSUtils(driver);
    }

    private final By loc_btnLogin = By.xpath("//XCUIElementTypeButton[@name=\"Đăng nhập\" or @name=\"Login\"]");

    public void navigateToLoginScreen() {
        new IOSBuyerHomeScreen(driver).navigateToAccountScreen();
        androidUtils.click(loc_btnLogin);
        logger.info("Navigate to login screen.");
    }
}
