package utility;

import io.appium.java_client.android.Activity;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

import static io.appium.java_client.AppiumBy.androidUIAutomator;

/**
 * Provides utility functions for interacting with Android devices in an Appium-based
 * test automation framework. This class offers methods to manage Android app interactions,
 * including initializing drivers, handling app states, and performing common actions.
 */

public class AndroidUtils {

    private static final Logger logger = LogManager.getLogger(AndroidUtils.class);

    public static final String uiScrollResourceId =
            "new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView(new UiSelector().resourceId(\"%s\"))";
    public static final String uiScrollResourceIdInstance =
            "new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView(new UiSelector().resourceId(\"%s\").instance(%d))";
    public static final String uiScrollText =
            "new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView(new UiSelector().text(\"%s\"))";
    public static final String uiScrollPartText =
            "new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView(new UiSelector().textContains(\"%s\"))";

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

    public void scrollUp() {
        try {
            driver.findElement(androidUIAutomator(
                    "new UiScrollable(new UiSelector().scrollable(true)).scrollBackward()"));
            logger.info("Scrolled up once on the screen.");
        } catch (NoSuchElementException e) {
            logger.warn("Failed to scroll up: {}", e.getMessage());
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

    public void scrollDown() {
        try {
            driver.findElement(androidUIAutomator(
                    "new UiScrollable(new UiSelector().scrollable(true)).scrollForward()"));
            logger.info("Scrolled down once on the screen.");
        } catch (NoSuchElementException e) {
            logger.warn("Failed to scroll down: {}", e.getMessage());
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
     * Retrieves a list of elements located by the specified locator.
     * Closes the notification screen before and after finding elements.
     *
     * @param locator The locator for the elements.
     * @return A list of found WebElements.
     */
    public List<WebElement> getListElement(By locator) {
        try {
            closeNotificationScreen();
            customWait(3000).until(ExpectedConditions.presenceOfElementLocated(locator));
        } catch (TimeoutException ignored) {
        }

        closeNotificationScreen();
        return driver.findElements(locator);
    }

    /**
     * Retrieves a single element located by the specified locator.
     * Closes the notification screen before and after finding the element.
     * If the element is not fully visible, retries up to 5 times to locate it again.
     *
     * @param locator The locator for the element (e.g., UiAutomator locator).
     * @return The fully visible WebElement.
     * @throws RuntimeException If the element cannot be made fully visible after 5 retries.
     */
    private WebElement getElement(By locator) {
        int retries = 0;

        // Retry until the element is fully visible or the retry limit is reached
        while (retries < 5) {
            WebElement element = WebUtils.retryOnStaleElement(driver, () -> {
                closeNotificationScreen();
                return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            });

            // If the element is fully visible, return it
            if (isElementFullyVisible(element)) {
                return element;
            }

            retries++;
        }

        // Throw an exception if the element is still not fully visible
        throw new RuntimeException("Failed to make element fully visible after 5 retries.");
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
     * Checks if the element located by the specified locator is enabled.
     *
     * @param locator The locator for the element.
     * @return True if the element is enabled, false otherwise.
     */
    public boolean isEnabled(By locator) {
        return getElement(locator).isEnabled();
    }

    /**
     * Performs a tap action at the specified coordinates on the screen.
     *
     * @param x The x-coordinate for the tap.
     * @param y The y-coordinate for the tap.
     */
    public void tapByCoordinates(int x, int y) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence tapSequence = new Sequence(finger, 1)
                .addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, y))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        ((AndroidDriver) driver).perform(List.of(tapSequence));
    }

    /**
     * Performs a tap action at a percentage of the screen's width and height.
     *
     * @param x The x-coordinate as a percentage of the screen width (0.0 to 1.0).
     * @param y The y-coordinate as a percentage of the screen height (0.0 to 1.0).
     */
    public void tapByCoordinatesInPercent(double x, double y) {
        Dimension size = driver.manage().window().getSize();
        tapByCoordinates((int) (size.width * x), (int) (size.height * y));
    }

    /**
     * Performs a swipe action from a start to an end point, specified as percentages of the screen dimensions.
     *
     * @param startX The start x-coordinate as a percentage of the screen width (0.0 to 1.0).
     * @param startY The start y-coordinate as a percentage of the screen height (0.0 to 1.0).
     * @param endX   The end x-coordinate as a percentage of the screen width (0.0 to 1.0).
     * @param endY   The end y-coordinate as a percentage of the screen height (0.0 to 1.0).
     * @param delay  The duration of the swipe in milliseconds.
     */
    public void swipeByCoordinatesInPercent(double startX, double startY, double endX, double endY, int delay) {
        Dimension size = driver.manage().window().getSize();

        int startXCoordinate = (int) (size.width * startX);
        int startYCoordinate = (int) (size.height * startY);
        int endXCoordinate = (int) (size.width * endX);
        int endYCoordinate = (int) (size.height * endY);

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipeSequence = new Sequence(finger, 0)
                .addAction(finger.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(), startXCoordinate, startYCoordinate))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(new Pause(finger, Duration.ofMillis(10)))
                .addAction(finger.createPointerMove(Duration.ofMillis(delay), PointerInput.Origin.viewport(), endXCoordinate, endYCoordinate))
                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        ((AndroidDriver) driver).perform(List.of(swipeSequence));
    }

    /**
     * Gets the vertical location of an element as a percentage of the screen height.
     *
     * @param locator The locator for the element.
     * @return The vertical location of the element as a percentage of the screen height.
     */
    public double getElementLocationYPercent(By locator) {
        int y = getElement(locator).getLocation().getY();
        Dimension size = driver.manage().window().getSize();
        return (double) y / size.height;
    }

    /**
     * Swipes horizontally across the screen from a start to an end point, specified as percentages of the screen width.
     *
     * @param locator The locator of the element to use for determining the vertical position.
     * @param startX  The start x-coordinate as a percentage of the screen width (0.0 to 1.0).
     * @param endX    The end x-coordinate as a percentage of the screen width (0.0 to 1.0).
     */
    public void swipeHorizontalInPercent(By locator, double startX, double endX) {
        double y = getElementLocationYPercent(locator);
        swipeByCoordinatesInPercent(startX, y, endX, y, 200);
    }

    /**
     * Waits until the specified screen activity is loaded.
     *
     * @param screenActivity The activity name of the screen to wait for.
     */
    public void waitUntilScreenLoaded(String screenActivity) {
        customWait(30000).until((ExpectedCondition<Boolean>) driver -> {
            AndroidDriver androidDriver = (AndroidDriver) driver;
            assert androidDriver != null;
            return Objects.requireNonNull(androidDriver.currentActivity()).equals(screenActivity);
        });
    }

    /**
     * Checks if the element located by the specified locator is displayed on the screen.
     *
     * @param locator The locator for the element.
     * @return True if the element is displayed, false otherwise.
     */
    public boolean isDisplayed(By locator) {
        return getElement(locator).isDisplayed();
    }

    private boolean isElementFullyVisible(WebElement element) {
        Rectangle rect = element.getRect();
        int screenHeight = driver.manage().window().getSize().getHeight();

        // Check if the element is fully visible within the screen's height
        return rect.y >= 0 && (rect.y + rect.height) <= screenHeight;
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
            String checkedImagePath = "./src/main/resources/files/checkbox_images/checked.png";
            String checkboxImagePath = "./src/main/resources/files/element_image/el_image.png";

            try {
                ScreenshotUtils screenshotUtils = new ScreenshotUtils();
                screenshotUtils.takeElementScreenShot(checkboxImagePath, getElement(locator));
                return screenshotUtils.compareImages(checkedImagePath, checkboxImagePath);
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

    /**
     * Retrieves the text of all elements located by the specified locator on the first screen.
     * Scrolls to the top of the screen before retrieving the elements.
     *
     * @param locator The locator for the elements.
     * @return A list of text from the elements found on the first screen.
     */
    public List<String> getListElementTextOnFirstScreen(By locator) {
        scrollToTopOfScreen();
        List<WebElement> elements = getListElement(locator);
        return elements.isEmpty() ? List.of() : elements.stream().map(WebElement::getText).toList();
    }
}
