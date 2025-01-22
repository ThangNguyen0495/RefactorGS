package pages.android.seller.home;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.android.seller.account.AndroidSellerAccountScreen;
import utility.AndroidUtils;

import static utility.AndroidUtils.*;

public class AndroidSellerHomeScreen {
    private final AndroidUtils androidUtils;
    private final WebDriver driver;
    public AndroidSellerHomeScreen(WebDriver driver) {
        this.driver = driver;
        androidUtils = new AndroidUtils(driver);
    }

    private final By loc_icnAccount = getSellerLocatorByResourceId("%s:id/bottom_navigation_tab_account");

    public void changeApplicationLanguage() {
        // Navigate to Account screen
        androidUtils.click(loc_icnAccount);

        // Change language
        new AndroidSellerAccountScreen(driver).selectLanguage();
    }
}
