package pages.android.seller.account;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import utility.AndroidUtils;
import utility.PropertiesUtils;

import static utility.AndroidUtils.getSellerLocatorByResourceId;
import static utility.helper.ActivityHelper.sellerBundleId;

public class AccountScreen {
    private final WebDriver driver;
    private final AndroidUtils androidUtils;

    public AccountScreen(WebDriver driver) {
        this.driver = driver;
        androidUtils = new AndroidUtils(driver);
    }

    private final By loc_btnLanguage = getSellerLocatorByResourceId("%s:id/llLanguage");
    private final By loc_ddvVietnamese = getSellerLocatorByResourceId("%s:id/llVietnamese");
    private final By loc_ddvEnglish = getSellerLocatorByResourceId("%s:id/llEnglish");

    public void selectLanguage() {
        androidUtils.click(loc_btnLanguage);
        if (PropertiesUtils.getLangKey().equals("vi")) {
            androidUtils.click(loc_ddvVietnamese);
        } else {
            androidUtils.click(loc_ddvEnglish);
        }

        androidUtils.relaunchApp(sellerBundleId);
    }
}
