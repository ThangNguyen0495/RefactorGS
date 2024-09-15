package utility;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.ios.IOSDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

/**
 * Provides utility functions for interacting with iOS devices in an Appium-based
 * test automation framework. This class offers methods to manage iOS app interactions,
 * including initializing drivers, handling app lifecycle, and performing common actions
 * such as tapping elements, swiping, and managing permissions.
 */
public class IOSUtils {
    private static final Logger logger = LogManager.getLogger();
    private final WebDriver driver;
    private final WebDriverWait wait;

    /**
     * Constructor for UICommonIOS.
     *
     * @param driver The WebDriver instance.
     */
    public IOSUtils(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    /**
     * Retries an action on StaleElementReferenceException.
     *
     * @param action The action to be retried.
     * @param <T>    The return type of the action.
     * @return The result of the action.
     */
    private <T> T retryOnStaleElement(Supplier<T> action) {
        try {
            return action.get();
        } catch (StaleElementReferenceException ex) {
            return action.get();
        }
    }

    /**
     * Accepts the specified permission by interacting with the alert dialog.
     *
     * @param optionText The text of the button to accept.
     */
    public void allowPermission(String optionText) {
        HashMap<String, Object> args = new HashMap<>();
        args.put("action", "accept");
        args.put("buttonLabel", optionText);
        ((IOSDriver) driver).executeScript("mobile: alert", args);
        logger.info("Allowed permission with option: {}", optionText);
    }

    /**
     * Hides the keyboard if it is visible.
     */
    public void hideKeyboard() {
        By doneButtonLocator = By.xpath("//XCUIElementTypeButton[@name=\"Done\"]");
        if (!driver.findElements(doneButtonLocator).isEmpty()) {
            click(doneButtonLocator);
        }
    }

    /**
     * Creates a WebDriverWait instance with a custom timeout.
     *
     * @param milliseconds Timeout duration in milliseconds.
     * @return A WebDriverWait instance with the specified timeout.
     */
    public WebDriverWait createCustomWait(int milliseconds) {
        return new WebDriverWait(driver, Duration.ofMillis(milliseconds));
    }

    /**
     * Retrieves a list of elements located by the specified locator.
     *
     * @param locator The locator for the elements.
     * @return A list of found WebElements.
     */
    public List<WebElement> getElements(By locator) {
        try {
            createCustomWait(3000).until(ExpectedConditions.presenceOfElementLocated(locator));
        } catch (TimeoutException ignored) {
        }

        return retryOnStaleElement(() -> driver.findElements(locator).isEmpty()
                ? List.of()
                : wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator)));
    }

    /**
     * Retrieves a single element located by the specified locator.
     *
     * @param locator The locator for the element.
     * @return The found WebElement.
     */
    public WebElement getElement(By locator) {
        return retryOnStaleElement(() -> {
            try {
                return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            } catch (TimeoutException ex) {
                logger.error("Element not found: {}", driver.getPageSource());
                throw new TimeoutException("Cannot find element");
            }
        });
    }

    /**
     * Retrieves a single element located by the specified locator and index.
     *
     * @param locator The locator for the elements.
     * @param index   The index of the element in the list.
     * @return The found WebElement.
     */
    public WebElement getElement(By locator, int index) {
        return retryOnStaleElement(() -> wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator)).get(index));
    }

    /**
     * Performs a tap action at the specified coordinates on the screen.
     *
     * @param x The x-coordinate for the tap.
     * @param y The y-coordinate for the tap.
     */
    public void tapAtCoordinates(int x, int y) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence tapSequence = new Sequence(finger, 1);
        tapSequence.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, y))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        ((IOSDriver) driver).perform(List.of(tapSequence));
    }

    /**
     * Performs a double tap action at the specified coordinates on the screen.
     *
     * @param x The x-coordinate for the double tap.
     * @param y The y-coordinate for the double tap.
     */
    public void doubleTapAtCoordinates(int x, int y) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence doubleTapSequence = new Sequence(finger, 1);

        doubleTapSequence.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, y))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(new Pause(finger, Duration.ofMillis(100)))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        ((IOSDriver) driver).perform(List.of(doubleTapSequence));
    }

    /**
     * Performs a tap action at a percentage of the screen's width and height.
     *
     * @param xPercentage The x-coordinate as a percentage of the screen width (0.0 to 1.0).
     * @param yPercentage The y-coordinate as a percentage of the screen height (0.0 to 1.0).
     */
    public void tapAtPercentage(double xPercentage, double yPercentage) {
        Dimension screenSize = driver.manage().window().getSize();
        int x = (int) (screenSize.width * xPercentage);
        int y = (int) (screenSize.height * yPercentage);
        tapAtCoordinates(x, y);
    }

    /**
     * Performs a tap action at the center of the given WebElement.
     *
     * @param element The WebElement to tap on.
     */
    public void tapAtCenter(WebElement element) {
        int centerX = element.getLocation().getX() + element.getSize().getWidth() / 2;
        int centerY = element.getLocation().getY() + element.getSize().getHeight() / 2;
        tapAtCoordinates(centerX, centerY);
    }

    /**
     * Performs a tap action at the right-top corner of the given WebElement.
     *
     * @param element The WebElement to tap on.
     */
    public void tapAtRightTopCorner(WebElement element) {
        int rightTopX = element.getLocation().getX() + element.getSize().getWidth();
        int rightTopY = element.getLocation().getY();
        tapAtCoordinates(rightTopX, rightTopY);
    }

    /**
     * Performs a tap action at the right-top corner of the element located by the specified locator.
     *
     * @param locator The locator for the element.
     */
    public void tapAtRightTopCorner(By locator) {
        tapAtRightTopCorner(getElement(locator));
    }

    /**
     * Performs a tap action at the right-top corner of the element located by the specified locator and index.
     *
     * @param locator The locator for the elements.
     * @param index   The index of the element in the list.
     */
    public void tapAtRightTopCorner(By locator, int index) {
        tapAtRightTopCorner(getElement(locator, index));
    }

    /**
     * Clicks the element located by the specified locator.
     * Uses tapAtCenter if the element's type is "XCUIElementTypeImage" or "XCUIElementTypeOther".
     *
     * @param locator The locator for the element.
     */
    public void click(By locator) {
        WebElement element = getElement(locator);
        String elementType = element.getAttribute("type");
        if ("XCUIElementTypeImage".equals(elementType) || "XCUIElementTypeOther".equals(elementType)) {
            tapAtCenter(element);
        } else {
            element.click();
        }
    }

    /**
     * Clicks the element located by the specified locator and index.
     * Uses tapAtCenter if the element's type is "XCUIElementTypeImage" or "XCUIElementTypeOther".
     *
     * @param locator The locator for the elements.
     * @param index   The index of the element in the list.
     */
    public void click(By locator, int index) {
        WebElement element = getElement(locator, index);
        String elementType = element.getAttribute("type");
        if ("XCUIElementTypeImage".equals(elementType) || "XCUIElementTypeOther".equals(elementType)) {
            tapAtCenter(element);
        } else {
            element.click();
        }
    }

    /**
     * Sends the specified keys to the element located by the specified locator.
     * Clears the element's existing value before sending keys and hides the keyboard.
     *
     * @param locator The locator for the element.
     * @param content The keys to send to the element.
     */
    public void sendKeys(By locator, CharSequence content) {
        WebElement element = getElement(locator);
        clearAndSendKeys(element, content);
    }

    /**
     * Sends the specified keys to the element located by the specified locator and index.
     * Clears the element's existing value before sending keys and hides the keyboard.
     *
     * @param locator The locator for the elements.
     * @param index   The index of the element in the list.
     * @param content The keys to send to the element.
     */
    public void sendKeys(By locator, int index, CharSequence content) {
        WebElement element = getElement(locator, index);
        clearAndSendKeys(element, content);
    }

    private void clearAndSendKeys(WebElement element, CharSequence content) {
        retryOnStaleElement(() -> {
            element.clear();
            element.sendKeys(content);
            hideKeyboard();
            return null; // Returning null as this is a void action.
        });
    }

    /**
     * Retrieves the text of the element located by the specified locator.
     *
     * @param locator The locator for the element.
     * @return The text of the element.
     */
    public String getText(By locator) {
        return retryOnStaleElement(() -> getElement(locator).getText());
    }

    /**
     * Retrieves the text of the element located by the specified locator and index.
     *
     * @param locator The locator for the elements.
     * @param index   The index of the element in the list.
     * @return The text of the element.
     */
    public String getText(By locator, int index) {
        return retryOnStaleElement(() -> getElement(locator, index).getText());
    }

    /**
     * Performs a swipe action from a start to an end point specified as percentages of the screen dimensions.
     *
     * @param startX The start x-coordinate as a percentage of the screen width (0.0 to 1.0).
     * @param startY The start y-coordinate as a percentage of the screen height (0.0 to 1.0).
     * @param endX   The end x-coordinate as a percentage of the screen width (0.0 to 1.0).
     * @param endY   The end y-coordinate as a percentage of the screen height (0.0 to 1.0).
     */
    public void swipeByPercentage(double startX, double startY, double endX, double endY) {
        swipeByPercentage(startX, startY, endX, endY, 200);
    }

    /**
     * Performs a swipe action from a start to an end point specified as percentages of the screen dimensions.
     * Allows specifying the duration of the swipe action.
     *
     * @param startX The start x-coordinate as a percentage of the screen width (0.0 to 1.0).
     * @param startY The start y-coordinate as a percentage of the screen height (0.0 to 1.0).
     * @param endX   The end x-coordinate as a percentage of the screen width (0.0 to 1.0).
     * @param endY   The end y-coordinate as a percentage of the screen height (0.0 to 1.0).
     * @param delay  The duration of the swipe action in milliseconds.
     */
    public void swipeByPercentage(double startX, double startY, double endX, double endY, int delay) {
        Dimension screenSize = driver.manage().window().getSize();
        int startXCoordinate = (int) (screenSize.width * startX);
        int startYCoordinate = (int) (screenSize.height * startY);
        int endXCoordinate = (int) (screenSize.width * endX);
        int endYCoordinate = (int) (screenSize.height * endY);

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipeSequence = new Sequence(finger, 0);
        swipeSequence.addAction(finger.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(), startXCoordinate, startYCoordinate))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(new Pause(finger, Duration.ofMillis(10)))
                .addAction(finger.createPointerMove(Duration.ofMillis(delay), PointerInput.Origin.viewport(), endXCoordinate, endYCoordinate))
                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        ((AppiumDriver) driver).perform(List.of(swipeSequence));
    }

    /**
     * Checks if the specified WebElement is checked based on its type and attributes.
     *
     * @param element The WebElement to check.
     * @return True if the element is checked, false otherwise.
     */
    public boolean isChecked(WebElement element) {
        return retryOnStaleElement(() -> {
            if ("XCUIElementTypeOther".equals(element.getAttribute("type"))) {
                element.click();
                return !element.findElements(By.xpath("//XCUIElementTypeImage[@name=\"icon_checked_white\"]")).isEmpty();
            }
            return "1".equals(element.getAttribute("value"));
        });
    }

    /**
     * Relaunches the app by terminating and then activating it again.
     *
     * @param bundleId The bundle ID of the app.
     */
    public void relaunchApp(String bundleId) {
        ((IOSDriver) driver).terminateApp(bundleId);
        ((IOSDriver) driver).activateApp(bundleId);
    }
}
