package pages.android.seller.products;

import io.appium.java_client.android.AndroidDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import utility.AndroidUtils;

import static io.appium.java_client.AppiumBy.androidUIAutomator;
import static utility.helper.ActivityHelper.sellerBundleId;
import static utility.AndroidUtils.uiScrollResourceId;
import static utility.AndroidUtils.uiScrollResourceIdInstance;

public class FilterScreen {
    AndroidDriver driver;
    AndroidUtils androidUtils;
    Logger logger = LogManager.getLogger();

    public FilterScreen(AndroidDriver driver) {
        // Get driver
        this.driver = driver;

        // Init commons class
        androidUtils = new AndroidUtils(driver);
    }

    By loc_btnReset = androidUIAutomator(uiScrollResourceId.formatted("%s:id/btnReset".formatted(sellerBundleId)));
    By loc_btnFilterByStatus(int actionsIndex) {
        return androidUIAutomator(uiScrollResourceIdInstance.formatted("%s:id/tag_container".formatted(sellerBundleId), actionsIndex));
    }
    By loc_btnFilterByChannel(int actionsIndex) {
        return androidUIAutomator(uiScrollResourceIdInstance.formatted("%s:id/tag_container".formatted(sellerBundleId), actionsIndex + 4));
    }
    By loc_btnFilterByPlatform(int actionsIndex) {
        return androidUIAutomator(uiScrollResourceIdInstance.formatted("%s:id/tag_container".formatted(sellerBundleId), actionsIndex + 7));
    }
    By loc_btnSeeAllBranches = androidUIAutomator(uiScrollResourceId.formatted("%s:id/btnSeeAllBranches".formatted(sellerBundleId)));
    By loc_btnSeeAllCollections = androidUIAutomator(uiScrollResourceId.formatted("%s:id/btnSeeAllCollections".formatted(sellerBundleId)));
    By loc_btnApply = androidUIAutomator(uiScrollResourceId.formatted("%s:id/btnApply".formatted(sellerBundleId)));

    public void filterByStatus(String status) {
        // Reset all filters
        androidUtils.click(loc_btnReset);

        // Select status
        switch (status) {
            case "ACTIVE" -> androidUtils.click(loc_btnFilterByStatus(1));
            case "INACTIVE" -> androidUtils.click(loc_btnFilterByStatus(2));
            case "ERROR" -> androidUtils.click(loc_btnFilterByStatus(3));
            default -> androidUtils.click(loc_btnFilterByStatus(0));
        }

        // Apply filter
        androidUtils.click(loc_btnApply);

        // Log
        logger.info("Filter list product by status: {}", status);
    }

    public void filterByChannel(String channel) {
        // Reset all filters
        androidUtils.click(loc_btnReset);

        // Select channel
        switch (channel) {
            case "LAZADA" -> androidUtils.click( loc_btnFilterByChannel( 1));
            case "SHOPEE" -> androidUtils.click( loc_btnFilterByChannel( 2));
            default -> androidUtils.click( loc_btnFilterByChannel( 0));
        }

        // Apply filter
        androidUtils.click(loc_btnApply);

        // Log
        logger.info("Filter list product by channel, channelName: {}", channel);
    }

    public void filterByPlatform(String platform) {
        // Reset all filters
        androidUtils.click(loc_btnReset);

        // Select platform
        switch (platform) {
            case "WEB" -> androidUtils.click(loc_btnFilterByPlatform(1));
            case "APP" -> androidUtils.click(loc_btnFilterByPlatform( 2));
            case "IN_STORE" -> androidUtils.click(loc_btnFilterByPlatform( 3));
            case "NONE" -> androidUtils.click(loc_btnFilterByPlatform(4));
            default -> androidUtils.click(loc_btnFilterByPlatform(0));
        }

        // Apply filter
        androidUtils.click(loc_btnApply);

        // Log
        logger.info("Filter list product platform, platformName: {}", platform);
    }

    public void filterByBranch(String branchName) {
        // Reset all filters
        androidUtils.click(loc_btnReset);

        // Navigate to branch screen
        androidUtils.click(loc_btnSeeAllBranches);

        // Select branch
        new BranchScreen(driver).selectBranch(branchName);

        // Apply filter
        androidUtils.click(loc_btnApply);

        // Log
        logger.info("Filter list product by branch, branchName: {}", branchName);
    }

    public void filterByCollections(String collectionName) {
        // Reset all filters
        androidUtils.click(loc_btnReset);

        // Navigate to collections screen
        androidUtils.click(loc_btnSeeAllCollections);

        // Select collection
        new CollectionsScreen(driver).selectCollection(collectionName);

        // Apply filter
        androidUtils.click(loc_btnApply);

        // Log
        logger.info("Filter list product by collection, collectionName: {}", collectionName);
    }
}
