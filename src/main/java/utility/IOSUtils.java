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
        try {
            createCustomWait(3000).until(ExpectedConditions.alertIsPresent());
            HashMap<String, Object> args = new HashMap<>();
            args.put("action", "accept");
            args.put("buttonLabel", optionText);
            ((IOSDriver) driver).executeScript("mobile: alert", args);
            logger.info("Allowed permission with option: {}", optionText);
        } catch (TimeoutException | NoAlertPresentException ignored) {
        }
    }

    /**
     * Hides the keyboard if it is visible.
     */
    public void hideKeyboard() {
        By doneButtonLocator = By.xpath("//XCUIElementTypeButton[@name=\"Done\"]");
        WebUtils.retryUntil(5, 1000, "Can not hide keyboard",
                () -> driver.findElements(doneButtonLocator).isEmpty(),
                () -> click(doneButtonLocator));
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
     * @param locator    The locator for the elements.
     * @param waitMillis Optional wait time in milliseconds before attempting to find the elements. Default is 3000 ms.
     * @return A list of found WebElements, or an empty list if none are found.
     */
    public List<WebElement> getListElement(By locator, int... waitMillis) {
        int waitTime = (waitMillis.length > 0) ? waitMillis[0] : 3000;

        try {
            // Wait for the presence of at least one element matching the locator
            createCustomWait(waitTime).until(ExpectedConditions.presenceOfElementLocated(locator));
        } catch (TimeoutException ignored) {
            // Timeout ignored; proceed to return an empty list if no elements are found
        }

        return retryOnStaleElement(() -> {
            // Retrieve and return elements, or an empty list if none are found
            List<WebElement> elements = driver.findElements(locator);
            return elements.isEmpty() ? List.of() : wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
        });
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
     * Toggles the status of a checkbox. If the checkbox cannot be clicked directly due to its type,
     * the method attempts to interact with an alternative element (e.g., a button).
     *
     * @param locator The {@link By} locator used to identify the checkbox element.
     */
    public void toggleCheckbox(By locator) {
        // Retrieve the initial status of the checkbox
        boolean isInitiallyChecked = isChecked(locator);

        // Attempt to click the checkbox element
        getElement(locator).click();

        // Verify if the status has been toggled successfully
        if (isChecked(locator) != isInitiallyChecked) {
            return; // Status toggled successfully, no further action needed
        }

        // If the status did not change and the type is 'XCUIElementTypeOther', try an alternate approach
        if (getElement(locator).getAttribute("type").equals("XCUIElementTypeOther")) {
            String updatedXPath = locator.toString()
                    .replaceAll("By.xpath: +", "") // Extract XPath string
                    .replaceAll("XCUIElementTypeOther$", "XCUIElementTypeButton"); // Replace last element type

            // Attempt to click the alternative element (e.g., a button)
            getElement(By.xpath(updatedXPath)).click();
        }
    }

    /**
     * Clicks the first element located by the specified locator.
     *
     * @param locator The {@link By} locator of the target element.
     */
    public void click(By locator) {
        click(locator, 0);
    }

    /**
     * Clicks the element at the specified index located by the given locator.
     *
     * @param locator The {@link By} locator of the target elements.
     * @param index   The zero-based index of the element to click.
     * @throws IndexOutOfBoundsException If no element exists at the given index.
     */
    public void click(By locator, int index) {
        getElement(locator, index).click();
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
        sendKeys(locator, 0, content);
    }

    public void sendKeys(By locator, int index, Object content) {
        if (content == null) {
            throw new IllegalArgumentException("Content to send cannot be null.");
        }

        WebUtils.retryOnStaleElement(driver, () -> {
            getElement(locator, index).clear();

            if (content instanceof CharSequence) {
                getElement(locator, index).sendKeys((CharSequence) content);
            } else {
                getElement(locator, index).sendKeys(String.valueOf(content));
            }
            hideKeyboard();
        });
    }

//    /**
//     * Sends the specified keys to the element located by the specified locator and index.
//     * Clears the element's existing value before sending keys and hides the keyboard.
//     *
//     * @param locator The locator for the elements.
//     * @param index   The index of the element in the list.
//     * @param content The keys to send to the element.
//     */
//    public void sendKeys(By locator, int index, CharSequence content) {
//        WebElement element = getElement(locator, index);
//        clearAndSendKeys(element, content);
//    }
//
//    private void clearAndSendKeys(WebElement element, CharSequence content) {
//        retryOnStaleElement(() -> {
//            element.clear();
//            element.sendKeys(content);
//            hideKeyboard();
//            return null; // Returning null as this is a void action.
//        });
//    }

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
     * @param locator The locator to check.
     * @return True if the element is checked, false otherwise.
     */
    public boolean isChecked(By locator) {
        return retryOnStaleElement(() -> {
            // Get the WebElement
            WebElement element = getElement(locator);

            // Check the "name" attribute
            String name = element.getAttribute("name");
            if (name != null) {
                if (name.equals("ic_green_rectangle_unselected")) return false;
                if (name.equals("ic_green_rectangle_selected")) return true;
            }

            // Check the "type" attribute for specific handling
            String type = element.getAttribute("type");
            if (type.equals("XCUIElementTypeOther")) {
                return !element.findElements(By.xpath("//XCUIElementTypeImage[@name='icon_checked_white']")).isEmpty();
            }

            // Fallback: Check the "value" attribute
            return element.getAttribute("value") != null && element.getAttribute("value").equals("1");
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
