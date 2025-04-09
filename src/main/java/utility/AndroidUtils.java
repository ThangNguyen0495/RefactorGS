package utility;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
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

import static io.appium.java_client.AppiumBy.androidUIAutomator;
import static utility.WebDriverManager.appBundleId;

/**
 * Provides utility functions for interacting with Android devices in an Appium-based
 * test automation framework. This class offers methods to manage Android app interactions,
 * including initializing drivers, handling app states, and performing common actions.
 */

public class AndroidUtils {

    private static final Logger logger = LogManager.getLogger(AndroidUtils.class);

    public static By getLocatorById(String id) {
        return By.id(id.formatted(appBundleId));
    }

    public static By getLocatorByResourceId(String resourceId) {
        return AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView(new UiSelector().resourceId(\"%s\"))".formatted(resourceId.formatted(appBundleId)));
    }

    public static By getLocatorByResourceIdAndInstance(String resourceId, int index) {
        return AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView(new UiSelector().resourceId(\"%s\").instance(%d))".formatted(resourceId.formatted(appBundleId), index));
    }

    public static By getLocatorByText(String text) {
        return AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView(new UiSelector().text(\"%s\"))".formatted(text));
    }

    public static By getLocatorByPartialText(String partialText) {
        return AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView(new UiSelector().textStartsWith(\"%s\"))".formatted(partialText));
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

    public void scrollDown() {
        try {
            driver.findElement(androidUIAutomator(
                    "new UiScrollable(new UiSelector().scrollable(true)).scrollForward()"));
            logger.info("Scrolled down.");
        } catch (NoSuchElementException e) {
            logger.warn("Failed to scrolled down: {}", e.getMessage());
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

    public void scrollUp() {
        try {
            driver.findElement(androidUIAutomator(
                    "new UiScrollable(new UiSelector().scrollable(true)).scrollBackward()"));
            logger.info("Scrolled up.");
        } catch (NoSuchElementException e) {
            logger.warn("Failed to scrolled up: {}", e.getMessage());
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
            customWait(waitTime).until(ExpectedConditions.presenceOfElementLocated(locator));
        } catch (TimeoutException ignored) {
        }

        return driver.findElements(locator);
    }

    /**
     * Attempts to locate and retrieve a single element based on the specified locator.
     * Ensures the element is fully visible before returning it.
     *
     * @param locator The {@link By} locator used to identify the element.
     * @return The fully visible {@link WebElement}.
     * @throws RuntimeException If the element cannot be made fully visible after retries.
     */
    public WebElement getElement(By locator) {
        if (locator instanceof AppiumBy) {
            return findElementWithScroll(locator);
        }

        return WebUtils.retryOnStaleElement(() ->
                wait.until(ExpectedConditions.presenceOfElementLocated(locator))
        );
    }

    private WebElement findElementWithScroll(By locator) {
        List<WebElement> elements = getListElement(locator);

        if (!elements.isEmpty()) {
            return elements.getFirst(); // Return first found element
        }

        String keyword = extractKeywordFromLocator(locator);
        String xpathString = constructXPathString(locator, keyword);

        // Try scrolling down
        for (int index = 0; index < 2; index++) {
            scrollDown();
            elements = driver.findElements(By.xpath(xpathString));
            if (!elements.isEmpty()) {
                return elements.getFirst();
            }
        }

        // Try scrolling up
        for (int index = 0; index < 4; index++) {
            scrollUp();
            elements = driver.findElements(By.xpath(xpathString));
            if (!elements.isEmpty()) {
                return elements.getFirst();
            }
        }

        throw new RuntimeException("Element not found after scrolling attempts: " + locator);
    }

    private String extractKeywordFromLocator(By locator) {
        // Extract the keyword from the locator in a more robust way
        return locator.toString().split("\"")[1];
    }

    private String constructXPathString(By locator, String keyword) {
        if (locator.toString().contains("resourceId")) {
            return "//*[contains(@resourceId, '%s')]".formatted(keyword);
        } else {
            return "//*[contains(@text, '%s')]".formatted(keyword);
        }
    }


    /**
     * Clicks the element located by the specified locator.
     *
     * @param locator The locator for the element.
     */
    public void click(By locator) {
        WebUtils.retryOnStaleElement(() -> getElement(locator).click());
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
            // Hide keyboard
            hideKeyboard();
            return; // Early return for CharSequence
        }

        getElement(locator).sendKeys(String.valueOf(content));

        // Hide keyboard
        hideKeyboard();
    }

    /**
     * Hides the Android soft keyboard if it is currently displayed.
     * <p>
     * This method checks whether the keyboard is shown using {@code isKeyboardShown()}.
     * If the keyboard is visible, it attempts to hide it using {@code hideKeyboard()}.
     * This is useful to prevent the keyboard from overlapping UI elements during testing.
     * </p>
     */
    private void hideKeyboard() {
        // Check if the keyboard is currently displayed
        if (((AndroidDriver) driver).isKeyboardShown()) {
            // Hide the keyboard to avoid UI obstruction
            ((AndroidDriver) driver).hideKeyboard();
        }
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
            return screenActivity.contains(androidDriver.currentActivity());
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
     */
    public void relaunchApp() {
        ((AndroidDriver) driver).terminateApp(appBundleId);
        ((AndroidDriver) driver).activateApp(appBundleId);
        logger.info("Relaunched app with package: {}", appBundleId);
    }

    /**
     * Pushes a file to the mobile device's download directory.
     * <p>
     * This method uploads a specified file to the mobile device's download directory.
     * The file path provided should be the full path to the file on the local machine,
     * not just the file name in the resource's directory.
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

    /**
     * Accepts the prompt to save the password in Google Password Manager if it is displayed.
     * This method checks if the "Save Password" prompt appears on the screen and clicks
     * the accept button if it is present.
     */
    public void acceptSavePasswordToGooglePasswordManager() {
        By loc_btnAcceptSavePassword = By.xpath("//android.widget.Button[@resource-id=\"android:id/autofill_save_yes\"]");
        if (!getListElement(loc_btnAcceptSavePassword).isEmpty()) {
            click(loc_btnAcceptSavePassword);
            logger.info("Accepted saving password in Google Password Manager.");
        }
    }

    public void relaunchAppIfAppCrashed() {
        By loc_btnCloseCrashPopup = By.id("android:id/aerr_close");
        if (!getListElement(loc_btnCloseCrashPopup).isEmpty()) {
            click(loc_btnCloseCrashPopup);
            logger.info("Close crash popup");
        }
    }
}
