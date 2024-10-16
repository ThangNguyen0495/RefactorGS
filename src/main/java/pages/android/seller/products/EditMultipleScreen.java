package pages.android.seller.products;

import api.seller.setting.APIGetBranchList;
import io.appium.java_client.android.AndroidDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import utility.AndroidUtils;

import java.util.List;
import java.util.stream.IntStream;

import static io.appium.java_client.AppiumBy.androidUIAutomator;
import static utility.helper.ActivityHelper.sellerBundleId;
import static utility.AndroidUtils.uiScrollResourceId;
import static utility.AndroidUtils.uiScrollResourceIdInstance;

public class EditMultipleScreen {
    WebDriver driver;
    AndroidUtils androidUtils;
    Logger logger = LogManager.getLogger();

    public EditMultipleScreen(AndroidDriver driver) {
        this.driver = driver;
        androidUtils = new AndroidUtils(driver);
    }

    By loc_btnSave = androidUIAutomator(uiScrollResourceId.formatted("%s:id/tvActionBarIconRight".formatted(sellerBundleId)));
    By loc_ddvSelectedBranch = androidUIAutomator(uiScrollResourceId.formatted("%s:id/tvFilterBranches".formatted(sellerBundleId)));
    By loc_lstBranches(int branchIndex) {
        return androidUIAutomator(uiScrollResourceIdInstance.formatted("%s:id/ivUnChecked".formatted(sellerBundleId), branchIndex));
    }
    By loc_lblActions = androidUIAutomator(uiScrollResourceId.formatted("%s:id/ivAction".formatted(sellerBundleId)));
    By loc_lblUpdatePriceActions = By.xpath("(//*[@* = '%s:id/title'])[1]".formatted(sellerBundleId));
    By loc_lblUpdateStockActions = By.xpath("(//*[@* = '%s:id/title'])[2]".formatted(sellerBundleId));
    By loc_dlgUpdatePrice_txtListingPrice = By.xpath("//*[@* = '%s:id/edtOrgPrice']//*[@* = '%s:id/edtPriceCustom']".formatted(sellerBundleId, sellerBundleId));
    By loc_dlgUpdatePrice_txtSellingPrice = By.xpath("//*[@* = '%s:id/edtNewPrice']//*[@* = '%s:id/edtPriceCustom']".formatted(sellerBundleId, sellerBundleId));
    By loc_dlgUpdatePrice_btnOK = androidUIAutomator(uiScrollResourceId.formatted("%s:id/tvOK".formatted(sellerBundleId)));
    By loc_dlgUpdateStock_tabChange = By.xpath("(//*[@* = '%s:id/tabLayoutUpdateStockType']//android.widget.TextView)[2]".formatted(sellerBundleId));
    By loc_dlgUpdateStock_txtQuantity = androidUIAutomator(uiScrollResourceId.formatted("%s:id/edtStock".formatted(sellerBundleId)));
    By loc_dlgUpdateStock_btnOK = androidUIAutomator(uiScrollResourceId.formatted("%s:id/tvUpdateStock".formatted(sellerBundleId)));

    public void bulkUpdatePrice(long listingPrice, long sellingPrice) {
        // Open list actions
        androidUtils.click(loc_lblActions);

        // Select bulk update price actions
        androidUtils.click(loc_lblUpdatePriceActions);

        // Input listing price
        androidUtils.sendKeys(loc_dlgUpdatePrice_txtListingPrice, String.valueOf(listingPrice));
        logger.info("Bulk listing price: {}", String.format("%,d", listingPrice));

        // Input selling price
        androidUtils.sendKeys(loc_dlgUpdatePrice_txtSellingPrice, String.valueOf(sellingPrice));
        logger.info("Bulk selling price: {}", String.format("%,d", sellingPrice));

        // Save changes
        androidUtils.click(loc_dlgUpdatePrice_btnOK);
    }

    public void bulkUpdateStock(boolean manageByIMEI, boolean manageByLot, List<APIGetBranchList.BranchInformation> branchInfos, int increaseNum, int... branchStock) {
        // Not supported for product managed by IMEI/Serial number
        if (manageByIMEI) logger.info("Can not bulk update stock with product that is managed by IMEI/Serial number.");
        else if (manageByLot) logger.info("Product is managed by lot, requiring stock updates in the lot-date screen.");
        else {
            // Update stock for each branch
            IntStream.range(0, APIGetBranchList.getActiveBranchNames(branchInfos).size()).forEach(branchIndex -> {
                // Get branch name
                String branchName = APIGetBranchList.getActiveBranchNames(branchInfos).get(branchIndex);

                // Get branch quantity
                int branchQuantity = ((branchIndex >= branchStock.length) ? 0 : branchStock[branchIndex]) + branchIndex * increaseNum;

                // Open list branches
                androidUtils.click(loc_ddvSelectedBranch);

                // Switch branch
                androidUtils.click(loc_lstBranches(branchIndex));

                // Open list actions
                androidUtils.click(loc_lblActions);

                // Select bulk update stock actions
                androidUtils.click(loc_lblUpdateStockActions);

                // Switch to change tab
                androidUtils.click(loc_dlgUpdateStock_tabChange);

                // Input quantity
                androidUtils.sendKeys(loc_dlgUpdateStock_txtQuantity, String.valueOf(branchQuantity));

                // Save changes
                androidUtils.click(loc_dlgUpdateStock_btnOK);

                // Log
                logger.info("Bulk update stock for branch '{}', quantity: {}", branchName, branchQuantity);
            });
        }

        // Save changes
        androidUtils.click(loc_btnSave);
    }
}
