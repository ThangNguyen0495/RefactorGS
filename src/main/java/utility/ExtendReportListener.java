package utility;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.*;

import java.io.File;

public class ExtendReportListener implements ITestListener, ISuiteListener, IInvokedMethodListener {
    private ExtentReports extent;
    private ExtentTest test;

    public WebDriver getDriver(ITestResult iTestResult) {
        try {
            return (WebDriver) iTestResult.getTestClass().getRealClass().getField("driver").get(iTestResult.getInstance());
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    // Initialize ExtentReports before the suite starts
    @Override
    public void onStart(ISuite suite) {
        String workingDir = System.getProperty("user.dir");
        ExtentSparkReporter reporter = new ExtentSparkReporter(workingDir + File.separator + "target" + File.separator + "ExtentReport.html");
        reporter.config().setReportName("Extent Report");
        extent = new ExtentReports();
        extent.attachReporter(reporter);
        extent.setSystemInfo("Author", "Automation Tester");
    }

    // Before each suite
    @Override
    public void onStart(ITestContext context) {
        // Create a test for the suite start
        if (test == null) {
            test = extent.createTest("Before Suite: " + context.getSuite().getName());
            test.info("Starting Suite: " + context.getSuite().getName());
        }
    }

    // After the suite finishes
    @Override
    public void onFinish(ISuite suite) {
        extent.flush();
    }

    // For each test method
    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult result) {
        if (method.isConfigurationMethod()) {
            if (test == null) {
                // Initialize `test` if it's not already set
                test = extent.createTest("Configuration: " + result.getMethod().getMethodName());
            }
            // Handle BeforeGroup, BeforeClass, BeforeMethod, etc.
            if (method.getTestMethod().isBeforeGroupsConfiguration()) {
                test.info("Before Group: " + result.getTestClass().getRealClass().getSimpleName());
            } else if (method.getTestMethod().isBeforeClassConfiguration()) {
                test.info("Before Class: " + result.getTestClass().getRealClass().getSimpleName());
            } else if (method.getTestMethod().isBeforeSuiteConfiguration()) {
                test.info("Before Suite: " + result.getTestContext().getSuite().getName());
            } else if (method.getTestMethod().isBeforeMethodConfiguration()) {
                test.info("Before Method: " + method.getTestMethod().getMethodName());
            }
        }
    }

    // After each test method
    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult result) {
        if (method.isConfigurationMethod()) {
            // Handle AfterGroup, AfterClass, etc.
            if (test == null) {
                test = extent.createTest("Configuration: " + result.getMethod().getMethodName());
            }
            if (method.getTestMethod().isAfterGroupsConfiguration()) {
                test.info("After Group: " + result.getTestClass().getRealClass().getSimpleName());
            } else if (method.getTestMethod().isAfterClassConfiguration()) {
                test.info("After Class: " + result.getTestClass().getRealClass().getSimpleName());
            } else if (method.getTestMethod().isAfterSuiteConfiguration()) {
                test.info("After Suite: " + result.getTestContext().getSuite().getName());
            } else if (method.getTestMethod().isAfterMethodConfiguration()) {
                test.info("After Method: " + method.getTestMethod().getMethodName());
            }
        }
    }

    // Track the actual test method execution
    @Override
    public void onTestStart(ITestResult result) {
        test = extent.createTest(result.getMethod().getMethodName());
        test.info("Test Started: " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        test.pass("Test Passed: " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        test.fail("Test Failed: " + result.getThrowable());

        // Capture screenshot on test failure
        if (getDriver(result) != null) {
            WebDriver driver = getDriver(result);
            String base64Image = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
            test.addScreenCaptureFromBase64String(base64Image, "Failed Test Screenshot");
        }

    }

    @Override
    public void onTestSkipped(ITestResult result) {
        test.skip("Test Skipped: " + result.getMethod().getMethodName());
    }
}
