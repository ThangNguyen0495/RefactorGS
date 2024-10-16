package pages.android.seller.products;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import utility.helper.ActivityHelper;
import utility.AndroidUtils;

/**
 * This class represents the product description screen in the Seller app.
 * It provides methods to input and save the product description.
 */
public class ProductDescriptionScreen {

    private final AndroidUtils androidUtils;

    // Locator for the description input field
    private final By locTxtContent = By.xpath("//android.widget.EditText");

    // Locator for the save button
    private final By locBtnSave = AppiumBy.androidUIAutomator(
            AndroidUtils.uiScrollResourceId.formatted("%s:id/ivActionBarIconRight".formatted(ActivityHelper.sellerBundleId))
    );

    /**
     * Constructor for the ProductDescriptionScreen class.
     * Initializes the AndroidUtils object with the provided AndroidDriver.
     *
     * @param driver the AndroidDriver instance used to interact with the Android UI elements.
     */
    public ProductDescriptionScreen(AndroidDriver driver) {
        this.androidUtils = new AndroidUtils(driver);
    }

    /**
     * Inputs the product description and saves the changes.
     *
     * @param description the description text to be entered.
     */
    public void inputDescription(String description) {
        // Input product description
        androidUtils.sendKeysActions(androidUtils.getElement(locTxtContent), description);

        // Save changes
        androidUtils.click(locBtnSave);
    }
}
