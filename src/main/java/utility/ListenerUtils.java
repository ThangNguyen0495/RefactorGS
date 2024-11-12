package utility;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.*;

import java.io.File;

/**
 * The {@code ListenerUtils} class is a TestNG listener that generates an Extent report for
 * test execution and captures screenshots on test failures.
 */
public class ListenerUtils implements ITestListener, ISuiteListener {
    private ExtentReports extent;
    private ExtentTest test;
    private String testName;  // Global variable to store the test name

    // Logger instance for logging to the console
    private static final Logger logger = LogManager.getLogger();

    /**
     * Retrieves the WebDriver instance used in the current test method.
     *
     * @param iTestResult The {@code ITestResult} object containing information about the executed test method.
     * @return The {@code WebDriver} instance used in the test, or {@code null} if the driver cannot be accessed.
     */
    public WebDriver getDriver(ITestResult iTestResult) {
        try {
            return (WebDriver) iTestResult.getTestClass().getRealClass().getField("driver").get(iTestResult.getInstance());
        } catch (IllegalAccessException | NoSuchFieldException e) {
            return null;
        }
    }

    /**
     * Initializes ExtentReports and attaches the ExtentSparkReporter to generate reports before the suite starts.
     *
     * @param suite The {@code ISuite} object representing the suite of tests.
     */
    @Override
    public void onStart(ISuite suite) {
        String workingDir = System.getProperty("user.dir");
        ExtentSparkReporter reporter = new ExtentSparkReporter(workingDir + File.separator + "target" + File.separator + "ExtentReport.html");
        reporter.config().setReportName("Extent Report");
        extent = new ExtentReports();
        extent.attachReporter(reporter);
        extent.setSystemInfo("Environment", PropertiesUtils.getEnv());
        extent.setSystemInfo("Author", "Automation Tester");
    }

    /**
     * Flushes the ExtentReports to ensure all logs and results are written to the report file after the suite finishes.
     *
     * @param suite The {@code ISuite} object representing the suite of tests.
     */
    @Override
    public void onFinish(ISuite suite) {
        if (extent == null) return;
        extent.flush();
    }

    /**
     * Logs the start of a test method.
     *
     * @param result The {@code ITestResult} object containing information about the executed test.
     */
    @Override
    public void onTestStart(ITestResult result) {
        if (result == null || extent == null) return;

        // Store testName globally
        testName = (result.getParameters().length > 0)
                ? result.getParameters()[result.getParameters().length - 1].toString()
                : result.getMethod().getMethodName();

        test = extent.createTest(testName);
        test.info("Test Started: " + testName);
        logger.info("Test Started: {}", testName);
    }

    /**
     * Logs a successful test method execution.
     *
     * @param result The {@code ITestResult} object containing information about the executed test.
     */
    @Override
    public void onTestSuccess(ITestResult result) {
        if (test == null || result == null) return;
        test.pass("Test Passed: " + testName);
    }

    /**
     * Logs a failed test method execution and captures a screenshot if WebDriver is available.
     *
     * @param result The {@code ITestResult} object containing information about the executed test.
     */
    @Override
    public void onTestFailure(ITestResult result) {
        if (test == null || result == null) return;

        test.log(Status.FAIL, "Test Failed: " + testName);
        test.log(Status.FAIL, result.getThrowable());

        // Capture screenshot on test failure and add it to Extent report
        WebDriver driver = getDriver(result);
        if (driver == null) return;

        try {
            // Capture screenshot and save it as Base64 string
            String base64Image = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
            test.addScreenCaptureFromBase64String(base64Image, "Failure Screenshot");
            logger.error("Screenshot captured for failed test: {}", testName);
        } catch (Exception e) {
            logger.error("Failed to capture screenshot for {}", testName, e);
        }
    }

    /**
     * Logs a skipped test method execution.
     *
     * @param result The {@code ITestResult} object containing information about the executed test.
     */
    @Override
    public void onTestSkipped(ITestResult result) {
        if (test == null || result == null) return;
        test.skip("Test Skipped: " + testName);
    }
}