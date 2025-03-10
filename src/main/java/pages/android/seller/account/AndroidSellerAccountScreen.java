package pages.android.seller.account;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import utility.AndroidUtils;
import utility.PropertiesUtils;

import static utility.AndroidUtils.getLocatorById;

public class AndroidSellerAccountScreen {
    private final AndroidUtils androidUtils;

    public AndroidSellerAccountScreen(WebDriver driver) {
        androidUtils = new AndroidUtils(driver);
    }

    private final By loc_btnLanguage = getLocatorById("%s:id/llLanguage");
    private final By loc_ddvVietnamese = getLocatorById("%s:id/llVietnamese");
    private final By loc_ddvEnglish = getLocatorById("%s:id/llEnglish");

    public void selectLanguage() {
        androidUtils.click(loc_btnLanguage);
        if (PropertiesUtils.getLangKey().equals("vi")) {
            androidUtils.click(loc_ddvVietnamese);
        } else {
            androidUtils.click(loc_ddvEnglish);
        }

        androidUtils.relaunchApp();
    }
}