package utility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 * Utility class providing common methods for interacting with web elements
 * in a Selenium-based testing environment. It facilitates actions such as
 * waiting for elements to be present, interacting with web elements,
 * and handling common scenarios encountered during browser automation.
 */
public class WebUtils {

    protected static final int DEFAULT_TIMEOUT_SECONDS = 10;
    protected static final int SHORT_TIMEOUT_MS = 3000;

    protected WebDriver driver;
    protected WebDriverWait wait;

    /**
     * Constructs a WebUtils object with the specified WebDriver.
     *
     * @param driver The WebDriver instance to be used.
     */
    public WebUtils(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT_SECONDS));
    }


    private static final Logger logger = LogManager.getLogger();

    /**
     * Performs a given action with optional logging.
     *
     * @param action the action to be performed, represented as a {@link Runnable}.
     * @throws IllegalArgumentException if the action is null.
     */
    public static void performAction(Runnable action) {
        performAction(null, action, null);
    }

    /**
     * Performs a given action with optional logging.
     *
     * @param logMessage a message to be logged before performing the action; can be null or empty.
     * @param action     the action to be performed, represented as a {@link Runnable}.
     * @throws IllegalArgumentException if the action is null.
     */
    public static void performAction(String logMessage, Runnable action) {
        performAction(logMessage, action, null);
    }

    /**
     * Performs a given action with optional logging and verification.
     *
     * @param logMessage a message to be logged before performing the action; can be null or empty.
     * @param action     the action to be performed, represented as a {@link Runnable}.
     * @param verifier   an optional action to verify the primary action; can be null.
     * @throws IllegalArgumentException if the action is null.
     */
    public static void performAction(String logMessage, Runnable action, Runnable verifier) {
        if (logMessage != null && !logMessage.isEmpty()) logger.info(logMessage);

        if (action == null) {
            throw new IllegalArgumentException("Action must be provided.");
        }
        action.run();

        if (verifier != null) {
            verifier.run();
        }
    }

    /**
     * Performs a given action that returns a result, without logging.
     *
     * @param <T>    the type of the result produced by the action.
     * @param action the action to be performed, represented as a {@link Supplier}.
     * @return the result of the action.
     * @throws IllegalArgumentException if the action is null.
     */
    public static <T> T performAction(Supplier<T> action) {
        return performAction(null, action);
    }

    /**
     * Performs a given action that returns a result, with optional logging.
     *
     * @param <T>        the type of the result produced by the action.
     * @param logMessage a message to be logged before performing the action; can be null or empty.
     * @param action     the action to be performed, represented as a {@link Supplier}.
     * @return the result of the action.
     * @throws IllegalArgumentException if the action is null.
     */
    public static <T> T performAction(String logMessage, Supplier<T> action) {
        if (logMessage != null && !logMessage.isEmpty()) logger.info(logMessage);

        if (action == null) {
            throw new IllegalArgumentException("Action must be provided.");
        }
        return action.get();
    }

    /**
     * Retries an operation until a specified condition is met or the maximum number of retries is reached.
     * The method performs the action and checks the condition after each attempt. If the condition is met,
     * the operation succeeds and the result is returned. If the condition not met after the maximum
     * retries, an exception thrown.
     *
     * @param <T>          The return type of the operation.
     * @param maxRetries   The maximum number of retry attempts before throwing an exception.
     * @param delayMillis  The delay in milliseconds between retry attempts.
     * @param exceptionMsg The message included in the exception if the maximum number of retries is reached.
     * @param condition    A lambda that returns a boolean indicating if the retry should stop (true to stop).
     * @param action       The action to be performed and retried, which returns a value.
     * @return The result of the action if the condition is met within the allowed retry attempts.
     * @throws IllegalArgumentException if the operation fails after the maximum number of retries.
     */
    public static <T> T retryUntil(int maxRetries, int delayMillis, String exceptionMsg, Supplier<Boolean> condition, Supplier<T> action) {
        for (int attempt = 0; attempt < maxRetries; attempt++) {
            if (condition.get()) {
                return action.get();
            }
            sleep(delayMillis);
        }

        // Safeguard: this should never be reached
        throw new IllegalStateException(exceptionMsg);
    }

    /**
     * Retries an operation until a specified condition is met or the maximum number of retries is reached.
     * This method performs the action and checks the condition after each attempt. If the condition is met,
     * the operation succeeds. If the condition is not met after the maximum retries, an exception thrown.
     * <p>
     * This version used for actions that do not return a result.
     *
     * @param maxRetries   The maximum number of retry attempts before throwing an exception.
     * @param delayMillis  The delay in milliseconds between retry attempts.
     * @param exceptionMsg The message included in the exception if the maximum number of retries is reached.
     * @param condition    A lambda function that returns {@code true} to stop retrying or {@code false} to continue.
     * @param action       The action to be performed and retried, which does not return a result.
     * @throws IllegalArgumentException if the operation fails after the maximum number of retries.
     */
    public static void retryUntil(int maxRetries, int delayMillis, String exceptionMsg, Supplier<Boolean> condition, Runnable action) {
        for (int attempt = 0; attempt < maxRetries; attempt++) {
            // Check if the condition is already met
            if (condition.get()) {
                return; // Exit early if the condition is satisfied
            }

            // Perform the action if the condition was not met
            action.run();

            // Pause between retry attempts if more retries are available
            if (attempt < maxRetries - 1) {
                sleep(delayMillis); // Sleep for the specified delay
            }
        }

        // If the loop exits without meeting the condition, throw an exception
        throw new IllegalArgumentException(exceptionMsg);
    }


    /**
     * Creates a WebDriverWait instance with a custom timeout.
     * If no timeout is provided, it defaults to 3000 milliseconds.
     *
     * @param milliseconds Optional timeout duration in milliseconds. Defaults to 3000 milliseconds if not provided.
     * @return A WebDriverWait instance with the specified or default timeout.
     */
    public WebDriverWait getWait(int... milliseconds) {
        int timeout = (milliseconds.length == 0) ? SHORT_TIMEOUT_MS : milliseconds[0];
        return new WebDriverWait(driver, Duration.ofMillis(timeout));
    }

    /**
     * Waits for a specific condition to be met within the given timeout period.
     *
     * @param <T>             The type of the result returned by the condition.
     * @param condition       The {@link ExpectedCondition} to wait for. Must not be null.
     * @param timeoutInMillis The maximum time to wait for the condition, in milliseconds. Must be greater than 0.
     * @return The result of the condition if met within the timeout, or {@code null} if the timeout occurs.
     * @throws IllegalArgumentException If the condition is null or timeout is not valid.
     */
    public <T> T waitForCondition(ExpectedCondition<T> condition, int... timeoutInMillis) {
        try {
            // Wait for the condition to be met
            return getWait(timeoutInMillis).until(condition);
        } catch (TimeoutException ignored) {
        }
        return null;
    }

    /**
     * Pauses the current thread for the specified duration.
     * <p>
     * This method handles InterruptedException by restoring the interrupted status of the thread.
     * </p>
     *
     * @param milliseconds The duration to sleep in milliseconds.
     */
    public static void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore the interrupted status
            throw new RuntimeException("Thread interrupted during sleep", e);
        }
    }

    /**
     * Retries an action when a StaleElementReferenceException is thrown.
     *
     * @param action The action to be retried.
     * @param <T>    The return type of the action.
     * @return The result of the action.
     */
    public static <T> T retryOnStaleElement(Supplier<T> action) {
        try {
            return action.get();
        } catch (StaleElementReferenceException ignored) {
            return action.get();
        }
    }

    /**
     * Retries an action when a StaleElementReferenceException is thrown.
     * <p>
     * This version is used for actions that do not return a value. The action will
     * be retried up to a certain number of attempts if a StaleElementReferenceException
     * is encountered.
     *
     * @param action The action to be executed and retried if needed.
     * @throws RuntimeException if the action repeatedly fails due to a StaleElementReferenceException.
     */
    public static void retryOnStaleElement(Runnable action) {
        retryOnStaleElement(() -> {
            action.run(); // Execute the Runnable action
            return null;  // Return null as Runnable has no return type
        });
    }

    /**
     * Highlights the specified web element by adding a red border around it.
     *
     * @param locator The By locator to find the web element to be highlighted.
     * @param index   The index of the element if multiple elements match the locator.
     */
    private void highlightElement(By locator, int index) {
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;

        // Highlight the element with a red border
        retryOnStaleElement(() -> jsExecutor.executeScript("arguments[0].style.border = '2px solid red'", getElement(locator, index)));

        // Remove the border after a short delay for visual confirmation
        getWait(1000).until(_ -> retryOnStaleElement(() -> {
            jsExecutor.executeScript("arguments[0].style.border = ''", getElement(locator, index));
            return true;
        }));
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

        ((JavascriptExecutor) driver).executeScript("localStorage.setItem('awareMode', '0')");

        // Wait for the presence of at least one element matching the locator
        var result = waitForCondition(ExpectedConditions.presenceOfElementLocated(locator), waitTime);
        if (result == null) {
            // Return an empty list if the condition was not met
            return List.of();
        }

        // Retrieve all elements matching the locator
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
    }

    /**
     * Gets a WebElement located by the specified locator.
     *
     * @param locator The By locator.
     * @return The WebElement.
     */
    public WebElement getElement(By locator) {
        return getElement(locator, 0);
    }

    /**
     * Gets a WebElement from a list located by the specified locator and index.
     *
     * @param locator The By locator.
     * @param index   The index of the element in the list.
     * @return The WebElement.
     */
    public WebElement getElement(By locator, int index) {
        ((JavascriptExecutor) driver).executeScript("localStorage.setItem('awareMode', '0')");
        return retryOnStaleElement(() -> wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator)).get(index));
    }

    /**
     * Clicks on the web element located by the specified locator.
     * The default behavior is to click the first element found.
     * <p>
     * This method highlights the element briefly by adding a red border around it,
     * ensuring that it is visible and clickable before performing the click action.
     * It handles stale element exceptions by retrying the element fetching process.
     *
     * @param locator The By locator used to find the web element on the page.
     */
    public void click(By locator) {
        click(locator, 0);
    }

    /**
     * Clicks on a web element located by the specified locator and index.
     * <p>
     * This method is designed to handle cases where multiple elements match the locator by specifying an index.
     * It briefly highlights the element by adding a red border to make it visible, ensures the element is clickable,
     * and retries fetching the element if a stale element exception occurs. If the specified index is out of bounds,
     * an exception will be thrown.
     * </p>
     *
     * @param locator The By locator used to find the web element on the page.
     * @param index   The index of the element to be clicked if multiple elements match the locator.
     *                Use 0 to click the first element. The index must be a non-negative integer that
     *                is less than the number of elements found by the locator.
     * @throws AssertionError                 If no elements match the locator.
     * @throws IndexOutOfBoundsException      If the specified index is out of range (index < 0 or index >= number of elements).
     * @throws StaleElementReferenceException If the element is no longer attached to the DOM when trying to click.
     */
    public void click(By locator, int index) {
        // Ensure that at least one element is found
        elementToBeClickable(locator, index);

        // Highlight the element by adding a red border
        highlightElement(locator, index);

        // Wait for the element to be clickable
        waitElementVisible(locator, index);

        // Retry to click the element
        retryOnStaleElement(() -> retryOnClickIntercepted(locator, index));
    }

    /**
     * Attempts to click on a web element located by the specified locator and index.
     * <p>
     * If a regular click is intercepted (e.g., by another element), this method falls back
     * to clicking the element using JavaScript.
     * </p>
     *
     * @param locator The By locator used to find the web element on the page.
     * @param index   The index of the element to be clicked if multiple elements match the locator.
     *                Use 0 to click the first element.
     */
    private void retryOnClickIntercepted(By locator, int index) {
        try {
            // Attempt to perform a regular click on the element
            getElement(locator, index).click();
        } catch (ElementClickInterceptedException ex) {
            // If click is intercepted, perform the click using JavaScript
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", getElement(locator, index));
        }
    }

    /**
     * Clicks on the web element located by the specified locator using JavaScript execution.
     * The default behavior is to click the first element found.
     * <p>
     * This method highlights the element by briefly adding a red border around it,
     * then performs the click action via JavaScript.
     *
     * @param locator The By locator used to find the web element on the page.
     */
    public void clickJS(By locator) {
        clickJS(locator, 0);
    }

    /**
     * Clicks on the web element located by the specified locator and index using JavaScript execution.
     * This method is useful when there are multiple matching elements and a specific one needs to be clicked.
     * <p>
     * The method highlights the element by briefly adding a red border around it
     * and then performs the click action using JavaScript. This can be useful in scenarios
     * where traditional Selenium click actions may not work due to element overlays or other issues.
     * It handles stale element exceptions by retrying the element fetching process.
     *
     * @param locator The By locator used to find the web element on the page.
     * @param index   The index of the element if multiple elements match the locator.
     *                Use 0 to click the first element.
     */
    public void clickJS(By locator, int index) {
        // Highlight the element
        highlightElement(locator, index);

        // Retry click element by JavaScripts
        retryOnStaleElement(() -> {
            // Perform click using JavaScript
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", getElement(locator, index));
        });
    }


    /**
     * Clicks outside a text box to remove focus.
     *
     * @param locator The By locator.
     * @param index   The index of the element in the list.
     */
    private void clickOutOfTextBox(By locator, int index) {
        retryOnStaleElement(() -> ((JavascriptExecutor) driver).executeScript("arguments[0].blur();", getElement(locator, index)));
    }

    /**
     * Sends keys to a WebElement located by the specified locator.
     * <p>
     * This method is intended for use with basic input fields where the entered
     * value remains visible in the input field. It retries the operation up to 5 times
     * if the element is stale or not interactable.
     *
     * @param locator The {@link By} locator of the input field.
     * @param content The content to be sent. Can be a {@link String} or any object
     *                that can be converted to a string.
     */
    public void sendKeys(By locator, Object content) {
        sendKeys(locator, 0, content);
    }

    /**
     * Sends keys to a WebElement located by the specified locator and index.
     * <p>
     * This method is intended for use with basic input fields where the entered
     * value remains visible in the input field. It retries the operation up to 5 times
     * if the element is stale or not interactable.
     *
     * @param locator The {@link By} locator of the input field.
     * @param index   The index of the element in the list of elements matching the locator.
     * @param content The content to be sent. Can be a {@link String} or any object
     *                that can be converted to a string.
     */
    public void sendKeys(By locator, int index, Object content) {
        retryUntil(5, 0, "Cannot input to field after 5 attempts",
                () -> elementTextMatches(locator, index, getContent(content)),
                () -> {
                    clear(locator, index);
                    click(locator, index);
                    retryOnStaleElement(() -> retrySendKeysOnElementNotInteractable(locator, index, getContent(content)));
                    clickOutOfTextBox(locator, index);
                });
    }

    /**
     * Sends keys to a tag input field located by the specified locator and handles tag creation.
     * <p>
     *
     * @param locator The By locator.
     * @param content The content to be sent.
     */
    public void sendKeysToTagInput(By locator, Object content) {
        sendKeysToTagInput(locator, 0, content);
    }

    /**
     * Sends keys to a tag input field located by the specified locator and handles tag creation.
     * <p>
     * This method is specifically for tag input fields, where each entered value is
     * converted into a tag or chip upon pressing Enter. It directly performs the input
     * without retry mechanisms.
     *
     * @param locator The {@link By} locator of the tag input field.
     * @param index   The index of the element in case multiple elements match the locator.
     * @param content The content to be added as a tag. Can be a {@link String} or any object
     *                that can be converted to a string.
     */
    public void sendKeysToTagInput(By locator, int index, Object content) {
        clear(locator, index);
        click(locator, index);
        retryOnStaleElement(() -> retrySendKeysOnElementNotInteractable(locator, index, getContent(content)));
        clickOutOfTextBox(locator, index);
    }

    /**
     * Converts the given content into a CharSequence.
     *
     * @param content The input content, which could be a CharSequence or any other Object.
     * @return The content as a CharSequence. If the content is null, returns an empty string.
     */
    private CharSequence getContent(Object content) {
        if (content == null) {
            return ""; // Return an empty string if content is null
        }
        if (content instanceof CharSequence) {
            return (CharSequence) content; // Directly return if already a CharSequence
        }
        return content.toString(); // Convert other objects to String
    }

    /**
     * Attempts to send keys to a web element located by the specified locator and index.
     * <p>
     * If the element is not interactable (throws an ElementNotInteractableException), this method
     * retries the action by moving to the element, clicking it, and then sending the keys using the Actions class.
     * </p>
     *
     * @param locator The By locator used to find the web element on the page.
     * @param index   The index of the element if multiple elements match the locator.
     *                Use 0 to interact with the first element.
     * @param content The content (keys) to send to the element.
     */
    private void retrySendKeysOnElementNotInteractable(By locator, int index, CharSequence content) {

        try {
            // Attempt to send keys to the element normally
            getElement(locator, index).sendKeys(content);
        } catch (ElementNotInteractableException ex) {
            // Log the exception for debugging purposes
            LogManager.getLogger().warn("Element not interactable, retrying with Actions", ex);

            // Retry using Actions to ensure the element is focused and interactable
            new Actions(driver)
                    .moveToElement(getElement(locator, index)) // Move to the element
                    .click() // Click on the element to ensure it has focus
                    .sendKeys(content) // Send the keys
                    .build()
                    .perform(); // Execute the action chain
        }
    }

    /**
     * Checks if the element's text or value matches the provided content.
     *
     * @param locator The By locator of the element.
     * @param index   The index of the element if multiple elements match the locator.
     * @param content The content to compare with the element's text or value.
     * @return True if the element's text or value matches the content; otherwise, false.
     */
    private boolean elementTextMatches(By locator, int index, CharSequence content) {
        // Only compare if the content is a String (not Keys or other CharSequence types)
        if (content instanceof String) {
            String contentStr = content.toString();
            // Check if the element's text or value matches the content
            return compareStringsIgnoreCase(contentStr, getElementValue(locator, index));
        }

        // If content is not a String (e.g., Keys), return true as no comparison is needed
        return true;
    }

    private String getElementValue(By locator, int index) {
        waitElementVisible(locator, index);
        var text = getText(locator, index);
        if (!text.isEmpty()) {
            return text; // Return early if text is not empty
        }

        text = getValue(locator, index);
        if (text != null && !text.isEmpty()) {
            return text; // Return early if value is not null and not empty
        }

        return ""; // Return empty string if neither condition is met
    }

    /**
     * Compares two strings in a case-insensitive manner, removing commas from numbers before comparison.
     *
     * @param firstString  The first string to compare.
     * @param secondString The second string to compare.
     * @return True if the two strings are equal after case-insensitive comparison and number formatting;
     * otherwise, false.
     */
    private boolean compareStringsIgnoreCase(String firstString, String secondString) {
        // Check if either of the strings is null
        if (firstString == null || secondString == null) {
            return false;
        }

        // Remove commas from both strings to handle number formatting
        String firstStr = firstString.replace(",", "").trim();
        String secondStr = secondString.replace(",", "").trim();

        // Try to compare the two strings as numbers
        try {
            // Parse both strings as numbers
            double firstNum = Double.parseDouble(firstStr);
            double secondNum = Double.parseDouble(secondStr);
            return firstNum == secondNum;
        } catch (NumberFormatException e) {
            // If parsing fails (not a valid number), compare the strings case-insensitively
            return firstStr.equalsIgnoreCase(secondStr);
        }
    }

    /**
     * Uploads a file using the specified locator.
     *
     * @param locator  The By locator.
     * @param filePath The file path to be uploaded.
     */
    public void uploads(By locator, String filePath) {
        uploads(locator, 0, filePath);
    }

    /**
     * Uploads a file using the specified locator and index.
     *
     * @param locator The By locator.
     * @param index   The index of the element in the list.
     * @param content The file path to be uploaded.
     */
    public void uploads(By locator, int index, CharSequence content) {
        retryOnStaleElement(() -> getElement(locator, index).sendKeys(content));
    }

    /**
     * Gets the text of a WebElement located by the specified locator.
     *
     * @param locator The By locator.
     * @return The text of the WebElement.
     */
    public String getText(By locator) {
        return getText(locator, 0);
    }

    /**
     * Gets the text of a WebElement located by the specified locator and index.
     *
     * @param locator The By locator.
     * @return The text of the WebElement.
     */
    public String getText(By locator, int index) {
        return retryOnStaleElement(() -> {
            waitVisibilityOfElementLocated(locator);
            return getElement(locator, index).getText();
        });
    }

    /**
     * Gets the value attribute of a WebElement located by the specified locator.
     *
     * @param locator The By locator.
     * @return The value of the WebElement.
     */
    public String getValue(By locator) {
        return getValue(locator, 0);
    }

    /**
     * Gets the value attribute of a WebElement located by the specified locator and index.
     *
     * @param locator The By locator.
     * @param index   The index of the element in the list.
     * @return The value of the WebElement.
     */
    public String getValue(By locator, int index) {
        return retryOnStaleElement(() -> getAttribute(locator, index, "value"));
    }

    /**
     * Gets the attribute value of a WebElement located by the specified locator and index.
     *
     * @param locator   The By locator.
     * @param index     The index of the element in the list.
     * @param attribute The attribute name.
     * @return The attribute value.
     */
    public String getAttribute(By locator, int index, String attribute) {
        return retryOnStaleElement(() -> {
            waitVisibilityOfElementLocated(locator);
            return getElement(locator, index).getAttribute(attribute);
        });
    }

    /**
     * Gets the attribute value of a WebElement located by the specified locator.
     *
     * @param locator   The By locator.
     * @param attribute The attribute name.
     * @return The attribute value.
     */
    public String getAttribute(By locator, String attribute) {
        return getAttribute(locator, 0, attribute);
    }

    /**
     * Clears the text from a web element specified by the given locator and index.
     * This method attempts to clear the field using keyboard events (DELETE/END).
     * It retries up to 5 times if the element is stale or not interactable.
     * If the field is not cleared after 5 attempts, an exception is thrown.
     *
     * @param locator the {@link By} locator used to find the web element
     * @param index   the index of the element to interact with, if multiple elements are matched
     * @throws IllegalStateException if the element cannot be cleared after 5 attempts
     */
    private void clear(By locator, int index) {
        retryUntil(5, 1000, "Cannot clear field after 5 attempts",
                () -> elementTextMatches(locator, index, ""),
                () -> {
                    String elementText = getElementValue(locator, index);
                    CharSequence[] clearChars = new CharSequence[elementText.length()];
                    Arrays.fill(clearChars, Keys.BACK_SPACE);
                    retryOnStaleElement(() -> getElement(locator, index).sendKeys(clearChars));
                    Arrays.fill(clearChars, Keys.DELETE);
                    retryOnStaleElement(() -> getElement(locator, index).sendKeys(clearChars));
                });
    }

    /**
     * Checks if the checkbox or radio button identified by the locator is selected using JavaScript.
     *
     * @param locator The locator of the checkbox or radio button.
     * @return True if the element is selected, false otherwise.
     */
    public boolean isCheckedJS(By locator) {
        return isCheckedJS(locator, 0);
    }

    /**
     * Checks if the checkbox or radio button identified by the locator and index is selected using JavaScript.
     *
     * @param locator The locator of the checkbox or radio button.
     * @param index   The index of the element if there are multiple matching elements.
     * @return True if the element is selected, false otherwise.
     */
    public boolean isCheckedJS(By locator, int index) {
        return retryOnStaleElement(() ->
                (boolean) ((JavascriptExecutor) driver).executeScript("return arguments[0].checked", getElement(locator, index))
        );
    }

    /**
     * Checks if the element identified by the locator is disabled using JavaScript.
     *
     * @param locator The locator of the element.
     * @return True if the element is disabled, false otherwise.
     */
    public Boolean isDisabledJS(By locator) {
        return isDisabledJS(locator, 0);
    }

    /**
     * Checks if the element identified by the locator and index is disabled using JavaScript.
     *
     * @param locator The locator of the element.
     * @param index   The index of the element if there are multiple matching elements.
     * @return True if the element is disabled, false otherwise.
     */
    public Boolean isDisabledJS(By locator, int index) {
        return retryOnStaleElement(() ->
                (Boolean) ((JavascriptExecutor) driver).executeScript("return arguments[0].disabled", getElement(locator, index))
        );
    }

    /**
     * Removes a specified attribute from the element identified by the locator and index using JavaScript.
     *
     * @param locator   The locator of the element.
     * @param index     The index of the element if there are multiple matching elements.
     * @param attribute The name of the attribute to remove.
     */
    public void removeAttribute(By locator, int index, String attribute) {
        if (getElement(locator, index) != null) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].removeAttribute(arguments[1])", getElement(locator, index), attribute);
        }
    }

    /**
     * Removes the element identified by the locator using JavaScript.
     *
     * @param locator The locator of the element.
     */
    public void removeElement(By locator) {
        if (getElement(locator) != null) {
            retryOnStaleElement(() -> ((JavascriptExecutor) driver).executeScript("arguments[0].remove()", getElement(locator)));
        }
    }

    /**
     * Retrieves the value of a specified key from localStorage using JavaScript.
     * If the value is null, it refreshes the page and retries up to 5 times.
     *
     * @param key The key to retrieve from localStorage.
     * @return The value of the specified key from localStorage.
     * @throws IllegalStateException if the value is still null after 5 attempts.
     */
    public String getLocalStorageValue(String key) {
        String errorMessage = "Failed to retrieve '" + key + "' from localStorage after 5 attempts";

        // Retry retrieving the value from localStorage
        return retryUntil(5, 3000, errorMessage, () -> {
            // Check if the value exists in localStorage
            Object value = ((JavascriptExecutor) driver).executeScript("return localStorage.getItem(arguments[0])", key);
            return value != null; // Condition to stop retrying if value is found
        }, () -> {
            // Retrieve the value from localStorage
            Object value = ((JavascriptExecutor) driver).executeScript("return localStorage.getItem(arguments[0])", key);

            if (value != null) {
                return value.toString(); // Successfully retrieved value, return it as a string
            }

            // Refresh the page before the next attempt if value is null
            driver.navigate().refresh();
            return null; // Indicate that the value was not retrieved yet
        });
    }

    /**
     * Retrieves the value of a specific cookie by its key, refreshing the page and retrying up to 5 times if the cookie is not found.
     *
     * @param key The name of the cookie to retrieve.
     * @return The value of the cookie associated with the specified key.
     * @throws NoSuchElementException if the cookie is not found after 5 attempts.
     */
    public String getCookieValue(String key) {
        String errorMessage = "Cookie '" + key + "' not found after 5 attempts";

        // Retry retrieving the cookie value
        return retryUntil(5, 3000, errorMessage, () -> {
            // Check if the cookie exists
            Cookie cookie = driver.manage().getCookieNamed(key);
            return cookie != null; // Condition to stop retrying if cookie is found
        }, () -> {
            // Retrieve the cookie
            Cookie cookie = driver.manage().getCookieNamed(key);

            if (cookie != null) {
                return cookie.getValue(); // Successfully retrieved value, return it as a string
            }

            // Refresh the page before the next attempt if cookie is null
            driver.navigate().refresh();
            return null; // Indicate that the cookie was not retrieved yet
        });
    }

    /**
     * Waits for the element identified by the locator to become visible.
     *
     * @param locator The locator of the element.
     */
    public void waitVisibilityOfElementLocated(By locator) {
        try {
            retryOnStaleElement(() -> wait.until(ExpectedConditions.visibilityOfElementLocated(locator)));
        } catch (TimeoutException ignored) {
        }
    }

    /**
     * Waits for the element identified by the locator to become invisible.
     *
     * @param locator The locator of the element.
     */
    public void waitInvisibilityOfElementLocated(By locator) {
        retryOnStaleElement(() -> wait.until(ExpectedConditions.invisibilityOfElementLocated(locator)));
    }

    /**
     * Waits for the element identified by the locator and index to be clickable.
     * If the element is disabled, it skips the wait.
     *
     * @param locator The locator of the element.
     * @param index   The index of the element if there are multiple matching elements.
     */
    private void elementToBeClickable(By locator, int index) {
        Boolean isDisabled = isDisabledJS(locator, index);

        // Skip the wait if the element is null or disabled
        if (isDisabled == null || isDisabled) {
            return;
        }

        // Wait until the element becomes clickable
        retryOnStaleElement(() -> wait.until(ExpectedConditions.elementToBeClickable(getElement(locator, index))));
    }

    /**
     * Waits for the element identified by the locator and index to be clickable.
     * If the element is disabled, it skips the wait.
     *
     * @param locator The locator of the element.
     * @param index   The index of the element if there are multiple matching elements.
     */
    private void waitElementVisible(By locator, int index) {
        Boolean isDisabled = isDisabledJS(locator, index);

        // Skip the wait if the element is null or disabled
        if (isDisabled == null || isDisabled) {
            return;
        }

        // Wait until the element becomes clickable
        retryOnStaleElement(() -> wait.until(ExpectedConditions.visibilityOf(getElement(locator, index))));
    }

    /**
     * Waits for the current URL to contain a specified path.
     *
     * @param path         The path to check for in the URL.
     * @param milliseconds Optional timeout in milliseconds. Defaults to 3 seconds if not specified.
     */
    public void waitURLShouldBeContains(String path, int... milliseconds) {
        getWait(milliseconds).until(ExpectedConditions.urlContains(path));
    }

    /**
     * Attempts to check a checkbox by selecting it, retrying up to 5 times if necessary.
     * This method uses JavaScript to check the checkbox if it is not already selected.
     * If the checkbox is still unchecked after 5 attempts, an exception is thrown.
     *
     * @param locator The locator for the checkbox element.
     */
    public void checkCheckbox(By locator) {
        checkCheckbox(locator, 0);
    }

    /**
     * Attempts to check a checkbox by selecting it, retrying up to 5 times if necessary.
     * This method uses JavaScript to check the checkbox at a specified index if there are multiple checkboxes.
     * If the checkbox is still unchecked after 5 attempts, an exception is thrown.
     *
     * @param locator The locator for the checkbox element.
     * @param index   The index of the checkbox if multiple checkboxes match the locator.
     * @throws IllegalStateException if the checkbox is still unchecked after 5 attempts.
     */
    public void checkCheckbox(By locator, int index) {
        String errorMessage = "Failed to check the checkbox after 5 attempts.";

        if (isDisabledJS(locator, index)) {
            throw new RuntimeException("Can not check the checkbox because the checkbox is disabled.");
        }

        // Retry checking the checkbox up to 5 times
        retryUntil(5, 1000, errorMessage,
                () -> isCheckedJS(locator, index), // Condition to stop retrying: checkbox is checked
                () -> clickJS(locator, index)
        );
    }

    /**
     * Attempts to uncheck a checkbox, retrying up to 5 times if necessary.
     * This method uses JavaScript to uncheck the checkbox if it is currently selected.
     * If the checkbox is still checked after 5 attempts, an exception is thrown.
     *
     * @param locator The locator for the checkbox element.
     */
    public void uncheckCheckbox(By locator) {
        uncheckCheckbox(locator, 0);
    }

    /**
     * Attempts to uncheck a checkbox, retrying up to 5 times if necessary.
     * This method uses JavaScript to uncheck the checkbox at a specified index if there are multiple checkboxes.
     * If the checkbox is still checked after 5 attempts, an exception is thrown.
     *
     * @param locator The locator for the checkbox element.
     * @param index   The index of the checkbox if multiple checkboxes match the locator.
     * @throws IllegalStateException if the checkbox is still checked after 5 attempts.
     */
    public void uncheckCheckbox(By locator, int index) {
        String errorMessage = "Failed to uncheck the checkbox after 5 attempts.";

        if (isDisabledJS(locator, index)) {
            return;
        }

        // Retry unchecking the checkbox up to 5 times
        retryUntil(5, 1000, errorMessage,
                () -> !isCheckedJS(locator, index), // Condition to stop retrying: checkbox is unchecked
                () -> clickJS(locator, index)
        );
    }

    private Select getDropdownSelect(By locator) {
        return retryOnStaleElement(() -> new Select(getElement(locator)));
    }

    /**
     * Waits until a dropdown contains a specific option value.
     *
     * @param value The option value to wait for in the dropdown.
     * @throws TimeoutException If the dropdown does not contain the specified value within the wait time.
     */
    private void waitUntilDropdownContainsValue(String value) {
        String optionXpath = "//option[@value = '%s']".formatted(value);
        getElement(By.xpath(optionXpath));
    }

    /**
     * Selects an option in a dropdown menu based on its value and logs the selected option's text.
     * This method waits for the dropdown to contain the specified value, selects the option with the given value,
     * and then logs the option's text. If the value is not found, an exception is thrown.
     *
     * @param locator     The {@link By} locator to identify the dropdown element.
     * @param optionValue The value of the option to be selected.
     * @throws NoSuchElementException If the specified option value does not exist in the dropdown.
     * @throws TimeoutException       If the dropdown does not contain the specified value within the wait time.
     */
    public void selectDropdownOptionByValue(By locator, String optionValue) {
        // Wait for the dropdown to contain the option value and retrieve its text
        waitUntilDropdownContainsValue(optionValue);

        retryOnStaleElement(() -> {
            try {
                // Select the option by value
                retryUntil(5, 1000, "Can not select value '%s'".formatted(optionValue),
                        () -> new Select(getElement(locator)).getFirstSelectedOption().getAttribute("value").equals(optionValue),
                        () -> new Select(getElement(locator)).selectByValue(optionValue));
            } catch (NoSuchElementException e) {
                // If the value is not found, throw an exception with a descriptive message
                throw new NoSuchElementException("Option with value '" + optionValue + "' not found in dropdown.", e);
            }

        });
    }
}