package utility;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.ios.IOSDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static utility.WebDriverManager.appBundleId;

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

    private static final List<String> acceptedPermission = new ArrayList<>();

    /**
     * Accepts the specified permission by interacting with the alert dialog.
     *
     * @param optionText The text of the button to accept.
     */
    public void allowPermission(String optionText) {
        if (acceptedPermission.contains(optionText)) {
            return;
        }

        try {
            logger.info("Waiting for the permission alert to show.");
            wait.until(ExpectedConditions.alertIsPresent());

            HashMap<String, Object> args = new HashMap<>();
            args.put("action", "accept");
            args.put("buttonLabel", optionText);

            ((IOSDriver) driver).executeScript("mobile: alert", args);
            logger.info("Allowed permission with option: {}", optionText);

            acceptedPermission.add(optionText);
        } catch (TimeoutException | NoAlertPresentException e) {
            logger.warn("No permission alert appeared or timed out: {}", e.getMessage());
        }
    }


    /**
     * Hides the keyboard if it is visible.
     */
    public void hideKeyboard() {
        By doneButtonLocator = AppiumBy.iOSNsPredicateString("name==\"Done\"");
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

        return WebUtils.retryOnStaleElement(() -> {
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
        return WebUtils.retryOnStaleElement(() -> wait.until(ExpectedConditions.presenceOfElementLocated(locator)));
    }

    /**
     * Retrieves a single element located by the specified locator and index.
     *
     * @param locator The locator for the elements.
     * @param index   The index of the element in the list.
     * @return The found WebElement.
     */
    public WebElement getElement(By locator, int index) {
        return WebUtils.retryOnStaleElement(() -> wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator)).get(index));
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
        getElement(locator).click();
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
     * Sends the specified content to the element located by the given locator.
     * This method defaults to using the first matching element (index 0).
     *
     * @param locator The {@link By} locator used to find the element.
     * @param content The content to send to the element. Must not be {@code null}.
     * @throws IllegalArgumentException if {@code content} is {@code null}.
     */
    public void sendKeys(By locator, Object content) {
        sendKeys(locator, 0, content);
    }

    /**
     * Sends the specified content to the element located by the given locator at the specified index.
     * This method clears the existing content in the element before sending the new content.
     * It supports both {@link CharSequence} and other object types, converting non-CharSequence content to a string.
     * If a stale element exception occurs, it retries automatically.
     * Finally, it attempts to hide the keyboard after sending keys.
     *
     * @param locator The {@link By} locator used to find the element.
     * @param index   The index of the matching element to interact with (starting from 0).
     * @param content The content to send to the element. Must not be {@code null}.
     * @throws IllegalArgumentException if {@code content} is {@code null}.
     */
    public void sendKeys(By locator, int index, Object content) {
        if (content == null) {
            throw new IllegalArgumentException("Content to send cannot be null.");
        }

        WebUtils.retryOnStaleElement(() -> {
            getElement(locator, index).clear();

            if (content instanceof CharSequence) {
                getElement(locator, index).sendKeys((CharSequence) content);
            } else {
                getElement(locator, index).sendKeys(String.valueOf(content));
            }
            hideKeyboard();
        });
    }

    /**
     * Retrieves the text of the element located by the specified locator and index.
     *
     * @param locator The locator for the elements.
     * @param index   The index of the element in the list.
     * @return The text of the element.
     */
    public String getText(By locator, int index) {
        return WebUtils.retryOnStaleElement(() -> getElement(locator, index).getText());
    }

    /**
     * Retrieves the text of the element located by the specified locator.
     *
     * @param locator The locator for the element.
     * @return The text of the element.
     */
    public String getText(By locator) {
        return WebUtils.retryOnStaleElement(() -> getElement(locator).getText());
    }

    /**
     * Checks if the specified WebElement is checked based on its type and attributes.
     *
     * @param locator The locator to check.
     * @return True if the element is checked, false otherwise.
     */
    public boolean isChecked(By locator) {
        return WebUtils.retryOnStaleElement(() -> {
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
     */
    public void relaunchApp() {
        ((IOSDriver) driver).terminateApp(appBundleId);
        ((IOSDriver) driver).activateApp(appBundleId);
        logger.info("Relaunch app.");

        WebUtils.sleep(3_000);
        logger.info("Waiting 3 seconds for application to launch");
    }

    public void swipeToElement(By locator) {
        WebElement element = getElement(locator);
        Dimension screenSize = driver.manage().window().getSize();

        int screenHeight = screenSize.getHeight();
        int minY = (int) (screenHeight * 0.2); // 10% of screen height
        int maxY = (int) (screenHeight * 0.8); // 100% of screen height

        int elementY = element.getLocation().getY();

        while (elementY < minY || elementY > maxY) {
            if (elementY < minY) {
                swipeDown();
            } else {
                swipeUp();
            }
            elementY = driver.findElement(locator).getLocation().getY();
        }
    }

    private void swipeUp() {
        Dimension screenSize = driver.manage().window().getSize();
        int startX = screenSize.getWidth() / 2;
        int startY = (int) (screenSize.getHeight() * 0.7); // Start at 70%
        int endY = (int) (screenSize.getHeight() * 0.3);   // Move to 30%

        performSwipe(startX, startY, startX, endY);
    }

    private void swipeDown() {
        Dimension screenSize = driver.manage().window().getSize();
        int startX = screenSize.getWidth() / 2;
        int startY = (int) (screenSize.getHeight() * 0.3); // Start at 30%
        int endY = (int) (screenSize.getHeight() * 0.7);   // Move to 70%

        performSwipe(startX, startY, startX, endY);
    }

    private void performSwipe(int startX, int startY, int endX, int endY) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 1)
                .addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger.createPointerMove(Duration.ofMillis(500), PointerInput.Origin.viewport(), endX, endY))
                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        ((IOSDriver) driver).perform(Collections.singletonList(swipe));
    }
}
