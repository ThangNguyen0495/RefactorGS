package utility;

import org.apache.logging.log4j.LogManager;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.IntStream;

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
     * Highlights the specified web element by adding a red border around it.
     *
     * @param locator The By locator to find the web element to be highlighted.
     * @param index   The index of the element if multiple elements match the locator.
     */
    private void highlightElement(By locator, int index) {
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;

        // Highlight the element with a red border
        jsExecutor.executeScript("arguments[0].style.border = '2px solid red'", getElement(locator, index));

        // Remove the border after a short delay for visual confirmation
        new WebDriverWait(driver, Duration.ofSeconds(1))
                .until(_ -> {
                    jsExecutor.executeScript("arguments[0].style.border = ''", getElement(locator, index));
                    return true;
                });
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
        retryOnStaleElement(() -> {
            // Highlight the element by adding a red border
            highlightElement(locator, index);

            // Ensure the element is clickable and perform the click
            try {
                elementToBeClickable(locator, index).click();
            } catch (ElementClickInterceptedException e) {
                // Handle cases where the element is intercepted by another element
                clickJS(locator, index);
            }
            return null;
        });

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
        retryOnStaleElement(() -> {
            // Highlight the element
            ((JavascriptExecutor) driver).executeScript("arguments[0].style.border= '1px solid red'", getElement(locator, index));
            ((JavascriptExecutor) driver).executeScript("arguments[0].style.border= ''", getElement(locator, index));
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
    void clickOutOfTextBox(By locator, int index) {
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
        retryOnStaleElement(() -> {
            waitVisibilityOfElementLocated(locator, index);
            clear(locator, index);
            click(locator, index);
            try {
                getElement(locator, index).sendKeys(content);
            } catch (ElementNotInteractableException ex) {
                new Actions(driver).moveToElement(getElement(locator, index))
                        .click()
                        .sendKeys(content)
                        .build()
                        .perform();
            }
            clickOutOfTextBox(locator, index);
            return null;
        });
    }

    /**
     * Uploads a file using the specified locator.
     *
     * @param locator The By locator.
     * @param content The file path to be uploaded.
     */
    public void uploads(By locator, CharSequence content) {
        retryOnStaleElement(() -> {
            getElement(locator).sendKeys(content);
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
     * This method sends a sequence of DELETE and BACK_SPACE keys to ensure the field is cleared.
     * It retries up to 5 times if the element is stale or not interactable.
     * If the field is not cleared after 5 attempts, an exception is thrown.
     *
     * @param locator the {@link By} locator used to find the web element
     * @param index   the index of the element to interact with, if multiple elements are matched
     * @throws IllegalStateException if the element cannot be cleared after 5 attempts
     */
    public void clear(By locator, int index) {
        // Generate an array of CharSequence consisting of DELETE and BACK_SPACE keys repeated 100 times
        CharSequence[] clearChars = IntStream.range(0, 100)
                .mapToObj(_ -> List.of(Keys.DELETE, Keys.BACK_SPACE))
                .flatMap(Collection::stream).toArray(CharSequence[]::new);

        // Retry up to 5 times to clear the field
        for (int retriesIndex = 0; retriesIndex < 5; retriesIndex++) {
            retryOnStaleElement(() -> {
                // Check if the element is already empty
                if (getElement(locator, index).getText().isEmpty() &&
                    (getValue(locator, index) == null || getValue(locator, index).isEmpty())) {
                    return null; // Field is already clear, exit
                }

                // Attempt to clear the field by sending keystrokes
                getElement(locator, index).sendKeys(clearChars);
                return null;
            });

            // Check if the field is cleared after sending the keys
            if (getElement(locator, index).getText().isEmpty() &&
                (getValue(locator, index) == null || getValue(locator, index).isEmpty())) {
                return; // Successfully cleared the field, exit
            }
        }

        // After 5 attempts, if the field is still not cleared, throw an exception
        throw new IllegalStateException("Failed to clear the text field after 5 attempts.");
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
    public boolean isDisabledJS(By locator) {
        return retryOnStaleElement(() ->
                (boolean) ((JavascriptExecutor) driver).executeScript("return arguments[0].disabled", getElement(locator))
        );
    }

    /**
     * Checks if the element identified by the locator and index is disabled using JavaScript.
     *
     * @param locator The locator of the element.
     * @param index   The index of the element if there are multiple matching elements.
     * @return True if the element is disabled, false otherwise.
     */
    public boolean isDisabledJS(By locator, int index) {
        return retryOnStaleElement(() ->
                (boolean) ((JavascriptExecutor) driver).executeScript("return arguments[0].disabled", getElement(locator, index))
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
     * Refreshes the page and retries up to 5 times if the value is null.
     *
     * @param key The key to retrieve from localStorage.
     * @return The value of the specified key from localStorage.
     * @throws IllegalStateException if the value is still null after 5 attempts.
     */
    public String getLocalStorageValue(String key) {
        int retriesRemaining = 5;
        while (retriesRemaining > 0) {
            Object value = ((JavascriptExecutor) driver).executeScript("return localStorage.getItem(arguments[0])", key);

            if (value != null) {
                return value.toString(); // Return the retrieved value as a string
            }

            retriesRemaining--;

            if (retriesRemaining > 0) {
                driver.navigate().refresh(); // Refresh the page before retrying
                try {
                    Thread.sleep(1000); // Wait 1 second before retrying
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Restore the interrupted status
                    throw new RuntimeException("Thread interrupted during localStorage retrieval retry", e);
                }
            }
        }

        throw new IllegalStateException("Failed to retrieve '" + key + "' from localStorage after 5 attempts.");
    }


    /**
     * Retrieves the value of a specific cookie by its key, retrying up to 5 times if the cookie is not found.
     *
     * @param key the name of the cookie to retrieve
     * @return the value of the cookie associated with the specified key
     * @throws NoSuchElementException if the cookie is not found after 5 attempts
     */
    public String getCookieValue(String key) {
        int attempts = 0;
        while (attempts < 5) {
            try {
                Cookie cookie = driver.manage().getCookieNamed(key);
                if (cookie != null) {
                    return cookie.getValue();
                }
            } catch (NullPointerException e) {
                // Log the failure and retry
                LogManager.getLogger().warn("Attempt {} failed to retrieve cookie '{}'. Retrying...", attempts + 1, key);
            }

            attempts++;
            try {
                Thread.sleep(1000); // Wait 1 second before retrying
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore the interrupted status
                throw new RuntimeException("Thread interrupted during cookie retrieval retry", e);
            }
        }

        throw new NoSuchElementException("Cookie '" + key + "' not found after 5 attempts");
    }

    /**
     * Waits for the element identified by the locator to become visible.
     *
     * @param locator The locator of the element.
     */
    public void waitVisibilityOfElementLocated(By locator) {
        retryOnStaleElement(() -> {
            wait.until(visibilityOfElementLocated(locator));
            return null;
        });
    }

    /**
     * Waits for the element identified by the locator and index to become visible.
     *
     * @param locator The locator of the element.
     * @param index   The index of the element if there are multiple matching elements.
     */
    public void waitVisibilityOfElementLocated(By locator, int index) {
        retryOnStaleElement(() -> {
            wait.until(visibilityOf(getElement(locator, index)));
            return null;
        });
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
     *
     * @param locator The locator of the element.
     * @param index   The index of the element if there are multiple matching elements.
     * @return The clickable WebElement.
     */
    public WebElement elementToBeClickable(By locator, int index) {
        return retryOnStaleElement(() -> wait.until(ExpectedConditions.elementToBeClickable(getElement(locator, index))));
    }

    /**
     * Waits for the current URL to contain a specified path.
     *
     * @param path         The path to check for in the URL.
     * @param milliseconds Optional timeout in milliseconds. Defaults to 15 seconds if not specified.
     */
    public void waitURLShouldBeContains(String path, int... milliseconds) {
        WebDriverWait customWait = (milliseconds.length == 0) ? wait : new WebDriverWait(driver, Duration.ofMillis(milliseconds[0]));
        customWait.until((ExpectedCondition<Boolean>) driver -> {
            assert driver != null;
            return driver.getCurrentUrl().contains(path);
        });
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
        for (int retriesIndex = 0; retriesIndex < 5; retriesIndex++) {
            if (!isCheckedJS(locator, index)) {
                clickJS(locator, index); // Attempts to check the checkbox using JS
            } else {
                return; // Checkbox is checked, exit method
            }
        }
        // After 5 attempts, if the checkbox is still unchecked, throw an error
        if (!isCheckedJS(locator, index)) {
            throw new IllegalStateException("Failed to check the checkbox after 5 attempts.");
        }
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
        for (int retriesIndex = 0; retriesIndex < 5; retriesIndex++) {
            if (isCheckedJS(locator, index)) {
                clickJS(locator, index); // Attempts to uncheck the checkbox using JS
            } else {
                return; // Checkbox is unchecked, exit method
            }
        }
        // After 5 attempts, if the checkbox is still checked, throw an error
        if (isCheckedJS(locator, index)) {
            throw new IllegalStateException("Failed to uncheck the checkbox after 5 attempts.");
        }
    }


}