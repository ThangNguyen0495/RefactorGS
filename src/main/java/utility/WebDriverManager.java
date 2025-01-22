package utility;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.safari.SafariDriver;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * WebDriverManager is a utility class for setting up and managing different types of WebDriver instances
 * for browser and mobile testing.
 */
public class WebDriverManager {
    private static final String url = "http://127.0.0.1:4723/wd/hub";

    /**
     * Initializes and returns an AndroidDriver instance.
     *
     * @param udid    The unique device identifier.
     * @param appPath The path to the app to be tested.
     * @return An instance of AndroidDriver.
     * @throws MalformedURLException If the URL is malformed.
     * @throws URISyntaxException    If the URI syntax is incorrect.
     */
    public static AndroidDriver getAndroidDriver(String udid, String appPath) throws MalformedURLException, URISyntaxException {
        UiAutomator2Options options = new UiAutomator2Options();
        options.setUdid(udid);
        options.setCapability("platformName", "Android");
        options.setCapability("appium:automationName", "uiautomator2");
        options.setCapability("appium:autoGrantPermissions", "true");
        options.setCapability("appium:appWaitActivity", "*");
        options.setCapability("appium:resetOnSessionStartOnly", "true");
        options.setCapability("appium:appWaitForLaunch", "false");
        options.setCapability("appium:fastReset", "true");
        options.setCapability("appium:noReset", "false");
        options.setCapability("appium:newCommandTimeout", 300_000);
        options.setCapability("appium:adbExecTimeout", 300_000);
        options.setCapability("appium:app", appPath);

        return new AndroidDriver(new URI(url).toURL(), options);
    }

    /**
     * Initializes and returns an IOSDriver instance.
     *
     * @param udid    The unique device identifier.
     * @param appPath The path to the app to be tested.
     * @return An instance of IOSDriver.
     * @throws MalformedURLException If the URL is malformed.
     * @throws URISyntaxException    If the URI syntax is incorrect.
     */
    public static IOSDriver getIOSDriver(String udid, String appPath) throws MalformedURLException, URISyntaxException {
        XCUITestOptions options = new XCUITestOptions();
        options.setCapability("appium:udid", udid);
        options.setCapability("platformName", "iOS");
        options.setCapability("appium:newCommandTimeout", 300_000);
        options.setCapability("appium:wdaLaunchTimeout", 300_000);
        options.setCapability("appium:wdaConnectionTimeout", 300_000);
        options.setCapability("appium:automationName", "XCUITest");
        options.setCapability("appium:app", appPath);

        return new IOSDriver(new URI(url).toURL(), options);
    }

    /**
     * Initializes and returns a WebDriver instance for the specified browser.
     *
     * @return An instance of WebDriver for the specified browser.
     */
    public static WebDriver getWebDriver() {
        WebDriver driver;
        boolean headless = System.getProperty("os.name").equals("Linux") || PropertiesUtils.getHeadless();
        switch (PropertiesUtils.getBrowser()) {
            case "firefox" -> {
                io.github.bonigarcia.wdm.WebDriverManager.firefoxdriver().setup();
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                if (headless) firefoxOptions.addArguments("--headless");
                firefoxOptions.addArguments("--no-sandbox");
                driver = new FirefoxDriver(firefoxOptions);
            }
            case "edge" -> {
                io.github.bonigarcia.wdm.WebDriverManager.edgedriver().setup();
                EdgeOptions edgeOptions = new EdgeOptions();
                if (headless) edgeOptions.addArguments("--headless");
                edgeOptions.addArguments("--no-sandbox");
                driver = new EdgeDriver(edgeOptions);
            }
            case "safari" -> {
                io.github.bonigarcia.wdm.WebDriverManager.safaridriver().setup();
                driver = new SafariDriver();
            }
            default -> {
                io.github.bonigarcia.wdm.WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                if (headless) chromeOptions.addArguments("--headless");
                chromeOptions.addArguments("--disable-site-isolation-trials");
                chromeOptions.addArguments("--disable-dev-shm-usage");
                chromeOptions.addArguments("--remote-allow-origins=*");
                chromeOptions.addArguments("--no-sandbox");
                driver = new ChromeDriver(chromeOptions);
            }
        }

        driver.manage().window().maximize();
        return driver;
    }
}