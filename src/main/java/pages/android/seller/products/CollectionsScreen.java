package pages.android.seller.products;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import utility.AndroidUtils;

import static utility.WebDriverManager.appBundleId;

public class CollectionsScreen {
    WebDriver driver;
    AndroidUtils androidUtils;
    Logger logger = LogManager.getLogger();

    public CollectionsScreen(WebDriver driver) {
        // Get driver
        this.driver = driver;

        // Init commons class
        androidUtils = new AndroidUtils(driver);
    }

    By loc_btnAllCollections = By.xpath("(//*[@* = '%s:id/htvFullCollections'] // *[@* = '%s:id/tag_container'])[1]".formatted(appBundleId, appBundleId));
    By loc_btnCollection(String collectionName) {
        return By.xpath("//*[@text = '%s']".formatted(collectionName));
    }

    public void selectCollection(String collectionName) {
        // Select collection
        androidUtils.click(collectionName.equals("ALL") ? loc_btnAllCollections : loc_btnCollection(collectionName));

        // Log
        logger.info("Select collection: {}", collectionName);
    }

}
