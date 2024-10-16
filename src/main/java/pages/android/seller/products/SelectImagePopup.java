package pages.android.seller.products;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import utility.AndroidUtils;

import static io.appium.java_client.AppiumBy.androidUIAutomator;
import static utility.helper.ActivityHelper.sellerBundleId;

/**
 * This class represents the popup screen for selecting images in the Seller app.
 * It provides methods to interact with the popup, such as selecting images and saving the selection.
 */
public class SelectImagePopup {

    private final AndroidUtils androidUtils;

    // Locator for the image list, used to find and select images on the popup.
    private final By loc_lstImages = androidUIAutomator(
            String.format(AndroidUtils.uiScrollResourceId, String.format("%s:id/tvSelectIndex", sellerBundleId))
    );

    // Locator for the save button, used to confirm and save the image selection.
    private final By loc_btnSave = androidUIAutomator(
            String.format(AndroidUtils.uiScrollResourceId, String.format("%s:id/fragment_choose_photo_dialog_btn_choose", sellerBundleId))
    );

    /**
     * Constructor for the SelectImagePopup class.
     * Initializes the AndroidUtils object with the provided AndroidDriver.
     *
     * @param driver the AndroidDriver instance used to interact with the Android UI elements.
     */
    public SelectImagePopup(AndroidDriver driver) {
        this.androidUtils = new AndroidUtils(driver);
    }

    /**
     * Selects images from the list and saves the selection.
     * This method clicks on the image list to select an image and then clicks the save button to confirm.
     */
    public void selectImages() {
        androidUtils.click(loc_lstImages);  // Selects images from the list.
        androidUtils.click(loc_btnSave);    // Clicks the save button to confirm the selection.
    }
}
