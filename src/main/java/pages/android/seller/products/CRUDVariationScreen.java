package pages.android.seller.products;

import io.appium.java_client.android.AndroidDriver;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import utility.AndroidUtils;
import utility.helper.VariationHelper;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static io.appium.java_client.AppiumBy.androidUIAutomator;
import static utility.helper.ActivityHelper.sellerBundleId;
import static utility.AndroidUtils.uiScrollResourceId;

public class CRUDVariationScreen {
    WebDriver driver;
    AndroidUtils androidUtils;
    Logger logger = LogManager.getLogger();
    @Getter
    private static Map<String, List<String>> variationMap;

    public CRUDVariationScreen(AndroidDriver driver) {
        this.driver = driver;
        androidUtils = new AndroidUtils(driver);
    }

    public CRUDVariationScreen removeOldVariation() {
        // Remove old variation
        int size = androidUtils.getListElement(loc_btnRemoveVariationGroup).size();
        IntStream.range(0, size).forEach(ignored -> androidUtils.click(loc_btnRemoveVariationGroup));
        return this;
    }

    By loc_btnRemoveVariationGroup = androidUIAutomator(uiScrollResourceId.formatted("%s:id/ivDeleteVariation1".formatted(sellerBundleId)));
    By loc_btnAddVariation = androidUIAutomator(uiScrollResourceId.formatted("%s:id/tvAddVariation".formatted(sellerBundleId)));
    By loc_txtVariationName1 = androidUIAutomator(uiScrollResourceId.formatted("%s:id/edtVariation1Name".formatted(sellerBundleId)));
    By loc_txtVariationValue1 = androidUIAutomator(uiScrollResourceId.formatted("%s:id/edtInputImeiSerialNumberValue".formatted(sellerBundleId)));
    By loc_btnAddVariationValue1 = androidUIAutomator(uiScrollResourceId.formatted("%s:id/ivAddNewImeiSerialNumber".formatted(sellerBundleId)));
    By loc_txtVariationName2 = androidUIAutomator(uiScrollResourceId.formatted("%s:id/edtVariation2Name".formatted(sellerBundleId)));
    By loc_txtVariationValue2 = androidUIAutomator(uiScrollResourceId.formatted("%s:id/edtVariation2Value".formatted(sellerBundleId)));
    By loc_btnAddVariationValue2 = androidUIAutomator(uiScrollResourceId.formatted("%s:id/ivAddValueForVariation2".formatted(sellerBundleId)));
    By loc_btnSave = androidUIAutomator(uiScrollResourceId.formatted("%s:id/ivActionBarIconRight".formatted(sellerBundleId)));

    public void addVariation(String defaultLanguage) {
        // Init variation map
        variationMap = VariationHelper.randomVariationMap(defaultLanguage);

        // Remove old variation
        removeOldVariation();

        // Add variation
        IntStream.range(0, variationMap.keySet().size()).forEachOrdered(groupIndex -> {
            // Get variation group
            String variationGroup = variationMap.keySet().stream().toList().get(groupIndex);

            // Get variation value
            List<String> variationValue = variationMap.get(variationGroup);

            // Add new variation group
            androidUtils.click(loc_btnAddVariation);

            // Input variation group
            androidUtils.sendKeys(groupIndex == 0 ? loc_txtVariationName1 : loc_txtVariationName2, variationGroup);
            logger.info("Add variation group {}, group: {}", groupIndex + 1, variationGroup);

            // Input variation value
            for (String value : variationValue) {
                androidUtils.sendKeys(groupIndex == 0 ? loc_txtVariationValue1 : loc_txtVariationValue2, value);
                androidUtils.click(groupIndex == 0 ? loc_btnAddVariationValue1 : loc_btnAddVariationValue2);
                logger.info("Add variation value for group {}, value: {}", groupIndex + 1, value);
            }
        });

        // Save changes
        saveChanges();

        // Log
        logger.info("Complete add variations");
    }

    public void saveChanges() {
        // Save changes
        androidUtils.click(loc_btnSave);
    }
}
