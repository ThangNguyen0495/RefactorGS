package utility;

import org.apache.logging.log4j.LogManager;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.function.Supplier;

import static org.apache.commons.lang.StringUtils.trim;
import static org.openqa.selenium.support.ui.ExpectedConditions.*;

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

    /**
     * Retries an operation until a specified condition is met or the maximum number of retries is reached.
     * The method performs the action and checks the condition after each attempt. If the condition is met,
     * the operation succeeds and the result is returned. If the condition is not met after the maximum
     * retries, an exception is thrown.
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
    public static  <T> T retryUntil(int maxRetries, int delayMillis, String exceptionMsg, Supplier<Boolean> condition, Supplier<T> action) {
        for (int attempt = 0; attempt < maxRetries; attempt++) {
            // Perform the action
            T result = action.get();

            // If the condition is met, return the result
            if (condition.get()) {
                return result;
            }

            // Sleep before retrying
            WebUtils.sleep(delayMillis);

            // If it's the last attempt and condition still not met, throw an exception
            if (attempt == maxRetries - 1) {
                throw new IllegalArgumentException(exceptionMsg);
            }
        }

        // Safeguard: this should never be reached
        throw new IllegalStateException(exceptionMsg);
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
    public <T> T retryOnStaleElement(Supplier<T> action) {
        while (true) {
            try {
                return action.get();
            } catch (StaleElementReferenceException ignored) {
            }
        }
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
    private <T> T retryOnClickIntercepted(By locator, int index) {
        try {
            // Attempt to perform a regular click on the element
            getElement(locator, index).click();
        } catch (ElementClickInterceptedException ex) {
            // If click is intercepted, perform the click using JavaScript
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", getElement(locator, index));
        }

        return null;
    }

    /**
     * Attempts to send keys to a web element located by the specified locator and index.
     * <p>
     * If the element is not interactable (throws an ElementNotInteractableException), this method
     * retries the action by moving to the element, clicking it, and then sending the keys using Actions class.
     * </p>
     *
     * @param locator The By locator used to find the web element on the page.
     * @param index   The index of the element if multiple elements match the locator.
     *                Use 0 to interact with the first element.
     * @param content The content (keys) to send to the element.
     * @return T      Returns null since sendKeys actions do not return a value.
     */
    private <T> T retrySendKeysOnElementNotInteractable(By locator, int index, CharSequence content) {
        try {
            // Attempt to send keys to the element normally
            getElement(locator, index).sendKeys(content);
        } catch (ElementNotInteractableException ex) {
            // If element is not interactable, retry using Actions class
            new Actions(driver).moveToElement(getElement(locator, index)) // Move to the element
                    .click() // Click on the element to ensure it's focused
                    .sendKeys(content) // Send the keys
                    .build()
                    .perform(); // Execute the chain of actions
        }
        return null; // Return null as sendKeys does not return a value
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
        retryOnStaleElement(() -> {
            jsExecutor.executeScript("arguments[0].style.border = '2px solid red'", getElement(locator, index));
            return null;
        });

        // Remove the border after a short delay for visual confirmation
        new WebDriverWait(driver, Duration.ofSeconds(1))
                .until(_ -> retryOnStaleElement(() -> {
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
        int waitTime = (milliseconds.length != 0) ? milliseconds[0] : 3000;
        try {
            getWait(waitTime).until(ExpectedConditions.presenceOfElementLocated(locator));
        } catch (TimeoutException ignore) {
        }
        return driver.findElements(locator).isEmpty()
                ? driver.findElements(locator)
                : wait.until(presenceOfAllElementsLocatedBy(locator));
    }

    /**
     * Gets a WebElement located by the specified locator.
     *
     * @param locator The By locator.
     * @return The WebElement.
     */
    public WebElement getElement(By locator) {
        return retryOnStaleElement(() -> wait.until(presenceOfElementLocated(locator)));
    }

    /**
     * Gets a WebElement from a list located by the specified locator and index.
     *
     * @param locator The By locator.
     * @param index   The index of the element in the list.
     * @return The WebElement.
     */
    public WebElement getElement(By locator, int index) {
        return retryOnStaleElement(() -> wait.until(presenceOfAllElementsLocatedBy(locator)).get(index));
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
     * and retries fetching the element if a stale element exception occurs.
     * </p>
     *
     * @param locator The By locator used to find the web element on the page.
     * @param index   The index of the element to be clicked if multiple elements match the locator.
     *                Use 0 to click the first element.
     */
    public void click(By locator, int index) {
        // Highlight the element by adding a red border
        highlightElement(locator, index);

        // Wait for the element to be clickable
        elementToBeClickable(locator, index);

        // Retry to click the element
        retryOnStaleElement(() -> retryOnClickIntercepted(locator, index));
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
            return null;
        });
    }


    /**
     * Clicks outside a text box to remove focus.
     *
     * @param locator The By locator.
     * @param index   The index of the element in the list.
     */
    private void clickOutOfTextBox(By locator, int index) {
        retryOnStaleElement(() -> {
            ((JavascriptExecutor) driver).executeScript("arguments[0].blur();", getElement(locator, index));
            return null;
        });
    }

    /**
     * Sends keys to a WebElement located by the specified locator.
     *
     * @param locator The By locator.
     * @param content The content to be sent.
     */
    public void sendKeys(By locator, CharSequence content) {
        sendKeys(locator, 0, content);
    }

    /**
     * Sends keys to a WebElement located by the specified locator and index.
     *
     * @param locator The By locator.
     * @param index   The index of the element in the list.
     * @param content The content to be sent.
     */
    public void sendKeys(By locator, int index, CharSequence content) {
        clear(locator, index);
        click(locator, index);
        retryOnStaleElement(() -> retrySendKeysOnElementNotInteractable(locator, index, content));
        clickOutOfTextBox(locator, index);
    }

    /**
     * Uploads a file using the specified locator.
     *
     * @param locator  The By locator.
     * @param filePath The file path to be uploaded.
     */
    public void uploads(By locator, String filePath) {
        retryOnStaleElement(() -> {
            getElement(locator).sendKeys(filePath);
            return null;
        });
    }

    /**
     * Uploads a file using the specified locator and index.
     *
     * @param locator The By locator.
     * @param index   The index of the element in the list.
     * @param content The file path to be uploaded.
     */
    public void uploads(By locator, int index, CharSequence content) {
        retryOnStaleElement(() -> {
            getElement(locator, index).sendKeys(content);
            return null;
        });
    }

    /**
     * Gets the text of a WebElement located by the specified locator.
     *
     * @param locator The By locator.
     * @return The text of the WebElement.
     */
    public String getText(By locator) {
        return retryOnStaleElement(() -> trim(getAttribute(locator, "innerText")));
    }

    /**
     * Gets the text of a WebElement located by the specified locator and index.
     *
     * @param locator The By locator.
     * @param index   The index of the element in the list.
     * @return The text of the WebElement.
     */
    public String getText(By locator, int index) {
        return retryOnStaleElement(() -> trim(getAttribute(locator, index, "innerText")));
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
        return retryOnStaleElement(() -> getElement(locator, index).getAttribute(attribute));
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
        retryUntil(5, 1000, "Cannot clear field after 5 attempts", () -> {
            // Check if field is already clear
            String value = getValue(locator, index);
            return getText(locator, index).isEmpty() && (value == null || value.isEmpty()); // Field is already clear
        }, () -> {
            retryOnStaleElement(() -> {
                getElement(locator, index).sendKeys(Keys.HOME, Keys.chord(Keys.SHIFT, Keys.END), Keys.DELETE);
                return null;
            });
            return null;
        });
    }

    /**
     * Checks if the checkbox or radio button identified by the locator is selected using JavaScript.
     *
     * @param locator The locator of the checkbox or radio button.
     * @return True if the element is selected, false otherwise.
     */
    public boolean isCheckedJS(By locator) {
        return retryOnStaleElement(() ->
                (boolean) ((JavascriptExecutor) driver).executeScript("return arguments[0].checked", getElement(locator))
        );
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
            retryOnStaleElement(() -> {
                ((JavascriptExecutor) driver).executeScript("arguments[0].remove()", getElement(locator));
                return null;
            });
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
        wait.until(visibilityOfElementLocated(locator));
    }

    /**
     * Waits for the element identified by the locator to become invisible.
     *
     * @param locator The locator of the element.
     */
    public void waitInvisibilityOfElementLocated(By locator) {
        retryOnStaleElement(() -> {
            wait.until(invisibilityOfElementLocated(locator));
            return null;
        });
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

        // Retry checking the checkbox up to 5 times
        retryUntil(5, 1000, errorMessage,
                () -> isCheckedJS(locator, index), // Condition to stop retrying: checkbox is checked
                () -> {
                    // Attempt to check the checkbox using JavaScript if it's still unchecked
                    if (!isCheckedJS(locator, index)) {
                        clickJS(locator, index); // Click the checkbox using JavaScript
                    }
                    return null; // Return null since the Supplier expects a return value
                }
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

        // Retry unchecking the checkbox up to 5 times
        retryUntil(5, 1000, errorMessage,
                () -> !isCheckedJS(locator, index), // Condition to stop retrying: checkbox is unchecked
                () -> {
                    // Attempt to uncheck the checkbox using JavaScript if it's still checked
                    if (isCheckedJS(locator, index)) {
                        clickJS(locator, index); // Click the checkbox to uncheck it using JavaScript
                    }

                    return null; // Return null since the Supplier expects a return value
                }
        );
    }

}