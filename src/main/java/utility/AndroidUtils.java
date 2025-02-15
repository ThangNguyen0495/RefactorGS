package utility;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.Activity;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

import static io.appium.java_client.AppiumBy.androidUIAutomator;
import static utility.helper.ActivityHelper.buyerBundleId;
import static utility.helper.ActivityHelper.sellerBundleId;

/**
 * Provides utility functions for interacting with Android devices in an Appium-based
 * test automation framework. This class offers methods to manage Android app interactions,
 * including initializing drivers, handling app states, and performing common actions.
 */

public class AndroidUtils {

    private static final Logger logger = LogManager.getLogger(AndroidUtils.class);
    public static By getSellerLocatorByResourceId(String resourceId) {
        return AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView(new UiSelector().resourceId(\"%s\"))".formatted(resourceId.formatted(sellerBundleId)));
    }

    public static By getBuyerLocatorByResourceId(String resourceId) {
        return AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView(new UiSelector().resourceId(\"%s\"))".formatted(resourceId.formatted(buyerBundleId)));
    }

    public static By getSellerLocatorByResourceIdAndInstance(String resourceId, int index) {
        return AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView(new UiSelector().resourceId(\"%s\").instance(%d))".formatted(resourceId.formatted(sellerBundleId), index));
    }

    public static By getLocatorByText(String text) {
        return AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView(new UiSelector().text(\"%s\"))".formatted(text));
    }

    public static By getLocatorByPartialText(String partialText) {
        return  AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector().scrollable(true)).scrollForward().scrollIntoView(new UiSelector().textContains(\"%s\"))".formatted(partialText));
    }


    private final WebDriver driver;
    private final WebDriverWait wait;

    /**
     * Constructor for AndroidUtils.
     *
     * @param driver The AndroidDriver instance.
     */
    public AndroidUtils(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    /**
     * Creates a WebDriverWait instance with a custom timeout.
     * Defaults to 3000 milliseconds if not provided.
     *
     * @param milliseconds Optional timeout duration in milliseconds. Defaults to 3000 if not provided.
     * @return A WebDriverWait instance with the specified or default timeout.
     */
    public WebDriverWait customWait(int... milliseconds) {
        int timeout = (milliseconds.length == 0) ? 3000 : milliseconds[0];
        return new WebDriverWait(driver, Duration.ofMillis(timeout));
    }

    /**
     * Scrolls to the top of the screen using UiScrollable.
     */
    public void scrollToTopOfScreen() {
        try {
            driver.findElement(androidUIAutomator(
                    "new UiScrollable(new UiSelector().scrollable(true)).scrollBackward().scrollToBeginning(1000)"));
            logger.info("Scrolled to the top of the screen.");
        } catch (NoSuchElementException e) {
            logger.warn("Failed to scroll to the top of the screen: {}", e.getMessage());
        }
    }

    /**
     * Scrolls to the end of the screen using UiScrollable.
     */
    public void scrollToEndOfScreen() {
        try {
            driver.findElement(androidUIAutomator(
                    "new UiScrollable(new UiSelector().scrollable(true)).scrollForward().scrollToEnd(1000)"));
            logger.info("Scrolled to the end of the screen.");
        } catch (NoSuchElementException e) {
            logger.warn("Failed to scroll to the end of the screen: {}", e.getMessage());
        }
    }

    /**
     * Closes the notification screen if it is visible.
     */
    private void closeNotificationScreen() {
        if (driver.getPageSource().contains("Appium Settings")) {
            ((AndroidDriver) driver).pressKey(new KeyEvent(AndroidKey.BACK));
            logger.info("Closed the notification screen.");
        }
    }

    /**
     * Retrieves a list of web elements identified by the locator.
     * It waits for the elements to be present before retrieving them.
     *
     * @param locator The locator to find the elements.
     * @return A list of web elements.
     */
    public List<WebElement> getListElement(By locator, int... milliseconds) {
        // Determine the wait time, using the provided timeout or defaulting to 3000 ms
        int waitTime = (milliseconds.length != 0) ? milliseconds[0] : 3000;
        try {
            closeNotificationScreen();
            customWait(waitTime).until(ExpectedConditions.presenceOfElementLocated(locator));
        } catch (TimeoutException ignored) {
        }

        closeNotificationScreen();
        return driver.findElements(locator);
    }

    /**
     * Attempts to locate and retrieve a single element based on the specified locator.
     * Ensures the element is fully visible before returning it.
     *
     * @param locator The {@link By} locator used to identify the element.
     * @return The fully visible {@link WebElement}.
     * @throws RuntimeException If the element cannot be made fully visible after 5 retries.
     */
    public WebElement getElement(By locator) {
        return WebUtils.retryOnStaleElement(() -> {
            closeNotificationScreen(); // Close notification screen
            return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        });
    }

    /**
     * Clicks the element located by the specified locator.
     *
     * @param locator The locator for the element.
     */
    public void click(By locator) {
        getElement(locator).click();
    }

    /**
     * Sends the specified keys to the element located by the given locator.
     * Clears the element's existing value before sending keys.
     *
     * @param locator The locator for the element.
     * @param content The keys or content to send to the element.
     *                Non-CharSequence objects will be converted to strings.
     * @throws IllegalArgumentException if content is null.
     */
    public void sendKeys(By locator, Object content) {
        if (content == null) {
            throw new IllegalArgumentException("Content to send cannot be null.");
        }

        getElement(locator).clear();

        if (content instanceof CharSequence) {
            getElement(locator).sendKeys((CharSequence) content);
            return; // Early return for CharSequence
        }

        getElement(locator).sendKeys(String.valueOf(content));
    }

    /**
     * Sends the specified keys to the given WebElement using Actions.
     * This method first clears the element's existing value before sending the specified keys.
     *
     * @param locator The locator for the WebElement to which keys will be sent.
     * @param content The keys to send to the WebElement, which can include text and special characters.
     * @throws IllegalArgumentException if the specified locator does not correspond to a valid WebElement.
     */
    public void sendKeysActions(By locator, CharSequence content) {
        WebElement element = getElement(locator);
        element.clear();
        element.click();
        new Actions(driver).sendKeys(content).perform();
    }

    /**
     * Retrieves the text of the element located by the specified locator.
     *
     * @param locator The locator for the element.
     * @return The text of the element.
     */
    public String getText(By locator) {
        return getElement(locator).getText();
    }

    /**
     * Waits until the specified screen activity is loaded.
     *
     * @param screenActivity The activity name of the screen to wait for.
     */
    public void waitUntilScreenLoaded(String screenActivity) {
        customWait(60_000).until((ExpectedCondition<Boolean>) driver -> {
            AndroidDriver androidDriver = (AndroidDriver) driver;
            assert androidDriver != null;
            assert androidDriver.currentActivity() != null;
            return androidDriver.currentActivity().equals(screenActivity);
        });
    }

    /**
     * Checks if the element located by the specified locator is checked.
     *
     * @param locator The locator for the element.
     * @return True if the element is checked, false otherwise.
     */
    public boolean isChecked(By locator) {
        // Check if the element is an ImageView and compare images if so
        if (getElement(locator).getAttribute("class").equals("android.widget.ImageView")) {
            String checkboxImagePath = "./src/main/resources/files/element_image/el_image.png";

            try {
                ScreenshotUtils screenshotUtils = new ScreenshotUtils();
                screenshotUtils.takeElementScreenShot(checkboxImagePath, getElement(locator));
                return screenshotUtils.compareImages(checkboxImagePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // Check if the element is marked as checked
        return getElement(locator).getAttribute("checked").equals("true");
    }

    /**
     * Relaunches the app by terminating and then activating it again.
     *
     * @param appPackage The package name of the app.
     */
    public void relaunchApp(String appPackage) {
        ((AndroidDriver) driver).terminateApp(appPackage);
        ((AndroidDriver) driver).activateApp(appPackage);
        logger.info("Relaunched app with package: {}", appPackage);
    }

    /**
     * Navigates to a specific screen using the provided app package and activity.
     *
     * @param appPackage  The package name of the app.
     * @param appActivity The activity name of the screen to navigate to.
     */
    public void navigateToScreenUsingScreenActivity(String appPackage, String appActivity) {
        // Return early if the current activity is already the desired activity
        if (Objects.equals(((AndroidDriver) driver).currentActivity(), appActivity)) {
            return; // Early exit if the current activity matches the target activity
        }

        // Navigate to screen by activity
        Activity activity = new Activity(appPackage, appActivity);
        activity.setStopApp(false);
        ((AndroidDriver) driver).startActivity(activity);
        logger.info("Navigated to screen activity: {}", appActivity);
    }

    /**
     * Pushes a file to the mobile device's download directory.
     * <p>
     * This method uploads a specified file to the mobile device's download directory.
     * The file path provided should be the full path to the file on the local machine,
     * not just the file name in the resources directory.
     *
     * @param filePath The full path of the file to be uploaded. It can be located anywhere on the local machine.
     * @throws IllegalArgumentException if the specified file does not exist.
     * @throws RuntimeException         if there is an error during the file upload process,
     *                                  such as an IOException when accessing the file.
     */
    public void pushFileToMobileDevices(String filePath) {
        // Create a File object from the provided file path
        File file = new File(filePath);

        // Check if the file exists before attempting to push it
        if (!file.exists()) {
            // Throw an exception if the file is not found
            throw new IllegalArgumentException("File does not exist: " + file.getAbsolutePath());
        }

        try {
            // Push the file to the mobile device's download directory
            ((AndroidDriver) driver).pushFile(String.format("/sdcard/Download/%s", file.getName()), file);

            // Log the successful file upload
            logger.info("Pushed file to mobile device: {}", filePath);
        } catch (IOException e) {
            // If an IOException occurs, wrap it in a RuntimeException and throw
            throw new RuntimeException("Failed to push file to mobile device: " + filePath, e);
        }
    }
}
