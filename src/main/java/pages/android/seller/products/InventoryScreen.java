package pages.android.seller.products;

import api.seller.setting.APIGetBranchList;
import io.appium.java_client.android.AndroidDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.openqa.selenium.By;
import utility.AndroidUtils;

import java.util.List;
import java.util.stream.IntStream;

import static io.appium.java_client.AppiumBy.androidUIAutomator;
import static utility.helper.ActivityHelper.sellerBundleId;
import static utility.AndroidUtils.uiScrollResourceId;
import static utility.AndroidUtils.uiScrollResourceIdInstance;

public class InventoryScreen {
    AndroidDriver driver;
    AndroidUtils androidUtils;
    Logger logger = LogManager.getLogger();

    public InventoryScreen(AndroidDriver driver) {
        this.driver = driver;
        androidUtils = new AndroidUtils(driver);
    }

    By loc_txtBranchStock(int branchIndex) {
        return androidUIAutomator(uiScrollResourceIdInstance.formatted("%s:id/edtStock".formatted(sellerBundleId), branchIndex));
    }
    By loc_btnSave = androidUIAutomator(uiScrollResourceId.formatted("%s:id/tvActionBarIconRight".formatted(sellerBundleId)));
    By loc_dlgUpdateStock = androidUIAutomator(uiScrollResourceId.formatted("%s:id/tabLayoutUpdateStockType".formatted(sellerBundleId)));
    By loc_dlgUpdateStock_tabChange = By.xpath("(//*[@* = '%s:id/tabLayoutUpdateStockType']//android.widget.TextView)[2]".formatted(sellerBundleId));
    By loc_dlgUpdateStock_txtQuantity = androidUIAutomator(uiScrollResourceId.formatted("%s:id/edtStock".formatted(sellerBundleId)));
    By loc_dlgUpdateStock_btnOK = androidUIAutomator(uiScrollResourceId.formatted("%s:id/tvUpdateStock".formatted(sellerBundleId)));

    public void addStock(boolean manageByIMEI, List<APIGetBranchList.BranchInformation> branchInfos, String variation, int... branchStock) {
        // Add stock for each branch
        IntStream.range(0, APIGetBranchList.getActiveBranchNames(branchInfos).size()).forEach(branchIndex -> {
            // Get current branch
            String branchName = APIGetBranchList.getActiveBranchNames(branchInfos).get(branchIndex);

            // Get branch quantity
            int branchQuantity = (branchIndex >= branchStock.length) ? 0 : branchStock[branchIndex];

            // Get current branch quantity
            String currentStock = androidUtils.getText(loc_txtBranchStock(branchIndex)).replaceAll("\\D+", "");

            // Check current branch stock, only update when stock is changed
            if ((!currentStock.isEmpty() || branchQuantity != 0) && Integer.parseInt(currentStock.isEmpty() ? "0" : currentStock) != branchQuantity) {
                // Add branch stock
                if (manageByIMEI) {
                    // Navigate to add imei screen
                    androidUtils.click(loc_txtBranchStock(branchIndex));

                    // Add imei
                    new AddIMEIScreen(driver).addIMEI(branchQuantity, branchName, variation);
                } else {
                    // Input branch stock
                    androidUtils.sendKeys(loc_txtBranchStock(branchIndex), String.valueOf(branchQuantity));
                }
            }

            // Log
            logger.info("Add stock for branch '{}', quantity: {}", branchName, branchQuantity);
        });

        // Save changes
        androidUtils.click(loc_btnSave);
    }

    public void updateStock(boolean manageByIMEI, List<APIGetBranchList.BranchInformation> branchInfos, String variation, int... branchStock) {
        // Add stock for each branch
        IntStream.range(0, APIGetBranchList.getActiveBranchNames(branchInfos).size()).forEach(branchIndex -> {
            // Get current branch
            String branchName =  APIGetBranchList.getActiveBranchNames(branchInfos).get(branchIndex);

            // Get branch quantity
            int branchQuantity = (branchIndex >= branchStock.length) ? 0 : branchStock[branchIndex];

            // Get current quantity
            String value = androidUtils.getText(loc_txtBranchStock(branchIndex)).replaceAll("\\D+", "");
            int currentBranchQuantity = value.isEmpty() ? 0 : Integer.parseInt(value);

            // Only update stock when stock is changed
            if (branchQuantity != currentBranchQuantity) {
                // Add branch stock
                if (manageByIMEI) {
                    // Navigate to add imei screen
                    androidUtils.click(loc_txtBranchStock(branchIndex));

                    // Add imei
                    new AddIMEIScreen(driver).addIMEI(branchQuantity, branchName, variation);
                } else {
                    // Click into branch stock textbox
                    androidUtils.click(loc_txtBranchStock(branchIndex));

                    // If update stock popup shows, update stock on popup
                    if (!androidUtils.getListElement(loc_dlgUpdateStock).isEmpty()) {
                        // Switch to change tab
                        androidUtils.click(loc_dlgUpdateStock_tabChange);

                        // Input quantity
                        androidUtils.sendKeys(loc_dlgUpdateStock_txtQuantity, String.valueOf(branchQuantity));

                        // Save changes
                        androidUtils.click(loc_dlgUpdateStock_btnOK);
                    } else {
                        // Input into branch stock textbox
                        androidUtils.sendKeys(loc_txtBranchStock(branchIndex), String.valueOf(branchQuantity));
                    }
                }
            }

            // Log
            logger.info("Update stock for branch '{}', quantity: {}", branchName, branchQuantity);
        });

        // Save changes
        androidUtils.click(loc_btnSave);
    }
}
