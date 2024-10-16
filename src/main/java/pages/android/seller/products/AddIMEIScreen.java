package pages.android.seller.products;

import io.appium.java_client.android.AndroidDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import utility.AndroidUtils;

import java.util.stream.IntStream;

import static io.appium.java_client.AppiumBy.androidUIAutomator;
import static utility.helper.ActivityHelper.sellerBundleId;
import static utility.AndroidUtils.uiScrollResourceId;

public class AddIMEIScreen {
    WebDriver driver;
    AndroidUtils androidUtils;
    Logger logger = LogManager.getLogger();
    public AddIMEIScreen(AndroidDriver driver) {
        // Get driver
        this.driver = driver;


        // Init commons class
        androidUtils = new AndroidUtils(driver);
    }

    By loc_icnRemoveIMEI = androidUIAutomator(uiScrollResourceId.formatted("%s:id/ivDeleteIcon".formatted(sellerBundleId)));
    By loc_txtIMEI = androidUIAutomator(uiScrollResourceId.formatted("%s:id/edtInputImeiSerialNumberValue".formatted(sellerBundleId)));
    By loc_btnAdd = androidUIAutomator(uiScrollResourceId.formatted("%s:id/ivAddNewImeiSerialNumber".formatted(sellerBundleId)));
    By loc_btnSave = androidUIAutomator(uiScrollResourceId.formatted("%s:id/tvActionBarIconRight".formatted(sellerBundleId)));

    public void addIMEI(int quantity, String branchName, String variation) {
        // Remove old IMEI
        int size = androidUtils.getListElement(loc_icnRemoveIMEI).size();
        IntStream.range(0, size).forEach(ignored -> androidUtils.click(loc_icnRemoveIMEI));

        // Add imei value for variation
        IntStream.range(0, quantity).forEach(index -> {
            // Input imei value
            String imei = "%s%s_%s".formatted(variation.isEmpty() ? "" : "%s_".formatted(variation), branchName, index);
            androidUtils.sendKeys(loc_txtIMEI, imei);

            // Add
            androidUtils.click(loc_btnAdd);

            // Log
            logger.info("Add imei into branch '{}', value: {}", branchName, imei);
        });

        // Save changes
        androidUtils.click(loc_btnSave);
    }
}
