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
 * The {@code ExtendReportListener} class is a TestNG listener that generates an Extent report for
 * test execution and captures screenshots on test failures.
 * It implements the {@code ITestListener}, {@code ISuiteListener}, and {@code IInvokedMethodListener}
 * interfaces to handle events at the suite, test, and method levels.
 */
public class ExtendReportListener implements ITestListener, ISuiteListener, IInvokedMethodListener {
    private ExtentReports extent;
    private ExtentTest test;

    // Logger instance for logging to the console
    private static final Logger logger = LogManager.getLogger();

    /**
     * Retrieves the WebDriver instance used in the current test method.
     * <p>
     * This method uses reflection to access the "driver" field from the test class.
     * If the WebDriver instance cannot be retrieved due to access restrictions or the absence of the field,
     * the method returns {@code null}.
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
        extent.setSystemInfo("Author", "Automation Tester");
    }

    /**
     * Logs the start of the test suite.
     *
     * @param context The {@code ITestContext} object representing the context of the suite.
     */
    @Override
    public void onStart(ITestContext context) {
        if (test == null) {
            test = extent.createTest("Before Suite: " + context.getSuite().getName());
            test.info("Starting Suite: " + context.getSuite().getName());
        }
    }

    /**
     * Flushes the ExtentReports to ensure all logs and results are written to the report file after the suite finishes.
     *
     * @param suite The {@code ISuite} object representing the suite of tests.
     */
    @Override
    public void onFinish(ISuite suite) {
        extent.flush();
    }

    /**
     * Logs the start of a test method invocation and method configuration (BeforeGroup, BeforeClass, etc.).
     *
     * @param method The {@code IInvokedMethod} object representing the invoked method.
     * @param result The {@code ITestResult} object containing information about the executed test.
     */
    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult result) {
        String methodName = method.getTestMethod().getMethodName();
        if (method.isTestMethod()) {
            logger.info("Method {} is starting.", methodName);
            test.info("Method " + methodName + " is starting.");
        }

        if (method.isConfigurationMethod()) {
            if (test == null) {
                test = extent.createTest("Configuration: " + methodName);
            }
            if (method.getTestMethod().isBeforeGroupsConfiguration()) {
                test.info("Before Group: " + result.getTestClass().getRealClass().getSimpleName());
            } else if (method.getTestMethod().isBeforeClassConfiguration()) {
                test.info("Before Class: " + result.getTestClass().getRealClass().getSimpleName());
            } else if (method.getTestMethod().isBeforeSuiteConfiguration()) {
                test.info("Before Suite: " + result.getTestContext().getSuite().getName());
            } else if (method.getTestMethod().isBeforeMethodConfiguration()) {
                test.info("Before Method: " + methodName);
            }
        }
    }

    /**
     * Logs the end of a test method invocation and method configuration (AfterGroup, AfterClass, etc.).
     *
     * @param method The {@code IInvokedMethod} object representing the invoked method.
     * @param result The {@code ITestResult} object containing information about the executed test.
     */
    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult result) {
        String methodName = method.getTestMethod().getMethodName();
        if (method.isTestMethod()) {
            logger.info("Method {} has ended.", methodName);
            test.info("Method " + methodName + " has ended.");
        }

        if (method.isConfigurationMethod()) {
            if (test == null) {
                test = extent.createTest("Configuration: " + methodName);
            }
            if (method.getTestMethod().isAfterGroupsConfiguration()) {
                test.info("After Group: " + result.getTestClass().getRealClass().getSimpleName());
            } else if (method.getTestMethod().isAfterClassConfiguration()) {
                test.info("After Class: " + result.getTestClass().getRealClass().getSimpleName());
            } else if (method.getTestMethod().isAfterSuiteConfiguration()) {
                test.info("After Suite: " + result.getTestContext().getSuite().getName());
            } else if (method.getTestMethod().isAfterMethodConfiguration()) {
                test.info("After Method: " + methodName);
            }
        }
    }

    /**
     * Logs the start of a test method.
     *
     * @param result The {@code ITestResult} object containing information about the executed test.
     */
    @Override
    public void onTestStart(ITestResult result) {
        test = extent.createTest(result.getMethod().getMethodName());
        String testName = (result.getParameters().length > 0)
                ? result.getParameters()[result.getParameters().length - 1].toString()
                : result.getMethod().getMethodName();

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
        test.pass("Test Passed: " + result.getMethod().getMethodName());
    }

    /**
     * Logs a failed test method execution and captures a screenshot if WebDriver is available.
     *
     * @param result The {@code ITestResult} object containing information about the executed test.
     */
    @Override
    public void onTestFailure(ITestResult result) {
        test.log(Status.FAIL, result.getThrowable());

        // Capture screenshot on test failure
        if (getDriver(result) != null) {
            WebDriver driver = getDriver(result);
            String base64Image = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
            test.addScreenCaptureFromBase64String(base64Image);
        }
    }

    /**
     * Logs a skipped test method execution.
     *
     * @param result The {@code ITestResult} object containing information about the executed test.
     */
    @Override
    public void onTestSkipped(ITestResult result) {
        test.skip("Test Skipped: " + result.getMethod().getMethodName());
    }
}