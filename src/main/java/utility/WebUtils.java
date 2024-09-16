package utility;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
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
     * Retrieves a list of web elements identified by the locator.
     * It waits for the elements to be present before retrieving them.
     *
     * @param locator The locator to find the elements.
     * @return A list of web elements.
     */
    public List<WebElement> getListElement(By locator) {
        try {
            getWait(3000).until(ExpectedConditions.presenceOfElementLocated(locator));
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
     * Gets a WebElement located by the first locator and nested by the second locator.
     *
     * @param locator1 The By locator for the parent element.
     * @param locator2 The By locator for the nested element.
     * @return The WebElement.
     */
    public WebElement getElement(By locator1, By locator2) {
        return retryOnStaleElement(() -> wait.until(presenceOfNestedElementLocatedBy(getElement(locator1), locator2)));
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
     * Clicks on the web element located by the specified locator and index.
     * This method is useful when there are multiple matching elements and
     * a specific one needs to be clicked.
     * <p>
     * The method highlights the element by briefly adding a red border around it
     * and ensures the element is clickable before clicking. It also retries fetching
     * the element in case of stale element exceptions.
     *
     * @param locator The By locator used to find the web element on the page.
     * @param index   The index of the element if multiple elements match the locator.
     *                Use 0 to click the first element.
     */
    public void click(By locator, int index) {
        WebElement element = retryOnStaleElement(() -> getElement(locator, index));
        // Highlight the element
        ((JavascriptExecutor) driver).executeScript("arguments[0].style.border= '1px solid red'", element);
        ((JavascriptExecutor) driver).executeScript("arguments[0].style.border= ''", element);
        // Ensure the element is clickable and click it
        elementToBeClickable(locator, index).click();
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
        WebElement element = retryOnStaleElement(() -> getElement(locator, index));
        // Highlight the element
        ((JavascriptExecutor) driver).executeScript("arguments[0].style.border= '1px solid red'", element);
        ((JavascriptExecutor) driver).executeScript("arguments[0].style.border= ''", element);
        // Perform click using JavaScript
        ((JavascriptExecutor) driver).executeScript("arguments[0].click()", element);
    }


    /**
     * Clicks outside of a text box to remove focus.
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
        waitVisibilityOfElementLocated(locator, index);
        clear(locator, index);
        click(locator, index);
        retryOnStaleElement(() -> {
            getElement(locator, index).sendKeys(content);
            return null;
        });
        clickOutOfTextBox(locator, index);
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
        return retryOnStaleElement(() -> getAttribute(locator, "value"));
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
     * Clears the text of a WebElement located by the specified locator.
     *
     * @param locator The By locator.
     */
    public void clear(By locator) {
        clear(locator, 0);
    }

    /**
     * Clears the text from an input field until it is empty.
     *
     * @param locator The locator of the input field.
     * @param index   The index of the element if there are multiple matching elements.
     */
    public void clear(By locator, int index) {
        WebElement element = getElement(locator, index);
        String currentText = element.getText();

        // Clear text until the field is empty or value is cleared
        while (!currentText.isEmpty() || (getValue(locator, index) != null && !getValue(locator, index).isEmpty())) {
            element.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
            currentText = element.getText();
        }
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
        WebElement element = getElement(locator, index);
        if (element != null) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].removeAttribute(arguments[1])", element, attribute);
        }
    }

    /**
     * Removes the element identified by the locator using JavaScript.
     *
     * @param locator The locator of the element.
     */
    public void removeElement(By locator) {
        WebElement element = getElement(locator);
        if (element != null) {
            retryOnStaleElement(() -> {
                ((JavascriptExecutor) driver).executeScript("arguments[0].remove()", element);
                return null;
            });
        }
    }

    /**
     * Removes a specific Facebook bubble element from the page.
     */
    public void removeFbBubble() {
        removeElement(By.cssSelector("#fb-root"));
    }

    /**
     * Retrieves the value of 'langKey' from localStorage using JavaScript.
     * Refreshes the page and retries if the value is null.
     *
     * @return The value of 'langKey' from localStorage.
     */
    public String getLangKey() {
        return retryOnStaleElement(() -> {
            Object langKey = ((JavascriptExecutor) driver).executeScript("return localStorage.getItem('langKey')");
            if (langKey == null) {
                driver.navigate().refresh();
                return getLangKey();
            }
            return langKey.toString();
        });
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
     * Waits for the element identified by the locator to be clickable.
     *
     * @param locator The locator of the element.
     * @return The clickable WebElement.
     */
    public WebElement elementToBeClickable(By locator) {
        return retryOnStaleElement(() -> wait.until(ExpectedConditions.elementToBeClickable(locator)));
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
}