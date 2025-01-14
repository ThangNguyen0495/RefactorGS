package pages.android.seller.home;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.android.seller.account.AccountScreen;
import utility.AndroidUtils;

import static utility.AndroidUtils.*;

public class HomeScreen {
    private final AndroidUtils androidUtils;
    private final WebDriver driver;
    public HomeScreen(WebDriver driver) {
        this.driver = driver;
        androidUtils = new AndroidUtils(driver);
    }

    private final By loc_icnAccount = getSellerLocatorByResourceId("%s:id/bottom_navigation_tab_account");

    public void changeApplicationLanguage() {
        // Navigate to Account screen
        androidUtils.click(loc_icnAccount);

        // Change language
        new AccountScreen(driver).selectLanguage();
    }
}
