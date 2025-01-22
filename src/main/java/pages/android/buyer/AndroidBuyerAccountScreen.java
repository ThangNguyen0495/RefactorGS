package pages.android.buyer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.android.buyer.home.AndroidBuyerHomeScreen;
import utility.AndroidUtils;

public class AndroidBuyerAccountScreen {
    private final WebDriver driver;
    private final AndroidUtils androidUtils;
    private final Logger logger = LogManager.getLogger();
    public AndroidBuyerAccountScreen(WebDriver driver) {
        this.driver = driver;
        this.androidUtils = new AndroidUtils(driver);
    }

    private final By loc_btnLogin = AndroidUtils.getBuyerLocatorByResourceId("%s:id/fragment_tab_account_user_profile_tv_sign_in");

    public void navigateToLoginScreen() {
        new AndroidBuyerHomeScreen(driver).navigateToAccountScreen();
        androidUtils.click(loc_btnLogin);
        logger.info("Navigate to login screen.");
    }
}
