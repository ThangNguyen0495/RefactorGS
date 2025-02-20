package utility;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.ios.IOSDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        return WebUtils.retryOnStaleElement(() -> {
            try {
                return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            } catch (TimeoutException ex) {
                throw new TimeoutException("Cannot find element");
            }
        });
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

            System.out.println(updatedXPath);
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

        WebUtils.retryOnStaleElement(() -> {
            getElement(locator).clear();

            if (content instanceof CharSequence) {
                getElement(locator).sendKeys((CharSequence) content);
            } else {
                getElement(locator).sendKeys(String.valueOf(content));
            }
            hideKeyboard();
        });
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
     *
     * @param bundleId The bundle ID of the app.
     */
    public void relaunchApp(String bundleId) {
        ((IOSDriver) driver).terminateApp(bundleId);
        ((IOSDriver) driver).activateApp(bundleId);
        logger.info("Relaunch app.");
    }
}
