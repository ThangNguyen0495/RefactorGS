package pages.android.seller.products;

import io.appium.java_client.android.AndroidDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import utility.AndroidUtils;

import static utility.helper.ActivityHelper.sellerBundleId;

public class BranchScreen {
    WebDriver driver;
    AndroidUtils androidUtils;
    Logger logger = LogManager.getLogger();
    public BranchScreen(AndroidDriver driver) {
        // Get driver
        this.driver = driver;

        // Init commons class
        androidUtils = new AndroidUtils(driver);
    }

    By loc_btnAllBranches = By.xpath("(//*[@* = '%s:id/htvFullBranches'] // *[@* = '%s:id/tag_container'])[1]".formatted(sellerBundleId, sellerBundleId));
    By loc_btnBranch(String branchName) {
        return By.xpath("//*[@text = '%s']".formatted(branchName));
    }

    public void selectBranch(String branchName) {
        // Select branch
        androidUtils.click(branchName.equals("ALL") ? loc_btnAllBranches : loc_btnBranch(branchName));

        // Log
        logger.info("Select branch: {}", branchName);
    }
}
