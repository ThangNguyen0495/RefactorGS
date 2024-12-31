package utility;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

/**
 * The {@code ListenerUtils} class is a TestNG listener that generates an Extent report for
 * test execution and captures screenshots on test failures.
 */
public class ListenerUtils implements ITestListener, ISuiteListener {
    private ExtentReports extent;
    private ExtentTest test;
    private String testName;  // Global variable to store the test name
    private String reportFilePath;

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
        reportFilePath = workingDir + File.separator + "target" + File.separator + "ExtentReport.html";
        ExtentSparkReporter reporter = new ExtentSparkReporter(reportFilePath);
        reporter.config().setReportName("Extent Report");
        extent = new ExtentReports();
        extent.attachReporter(reporter);
        extent.setSystemInfo("Environment", PropertiesUtils.getEnv());
        extent.setSystemInfo("Author", "Automation Tester");
    }

    /**
     * Flushes the ExtentReports to ensure all logs and results are written to the report file after the suite finishes.
     * Then, it updates the HTML report by extracting the "Tests Skipped" count and adding a new column for it.
     *
     * @param suite The {@code ISuite} object representing the suite of tests.
     *
     * @throws IndexOutOfBoundsException If there are not enough `<b>` elements in the HTML to retrieve the skipped value.
     */
    @Override
    public void onFinish(ISuite suite) {
        if (extent == null) return;
        // Log the summary at the end of the test execution

        // Flushes the ExtentReports
        extent.flush();

        // Added the "Tests Skipped" column
        try {
            // Load the HTML file
            File inputFile = new File(reportFilePath);
            if (!inputFile.exists()) {
                throw new FileNotFoundException("File not found at specified path: " + reportFilePath);
            }
            Document doc = Jsoup.parse(inputFile, "UTF-8");

            // Find the "Tests Passed" and "Tests Failed" columns and update their classes to "col-md-2"
            Elements testPassedColumn = doc.selectXpath("//div[@class = 'col-md-3' and *//p[text() = 'Tests Passed']]");
            Elements testFailedColumn = doc.selectXpath("//div[@class = 'col-md-3' and *//p[text() = 'Tests Failed']]");

            // Change class from col-md-3 to col-md-2
            testPassedColumn.attr("class", "col-md-2");
            testFailedColumn.attr("class", "col-md-2");

            // Select the <small> element containing the text with "skipped" information
            Element smallElement = doc.selectXpath("//small[contains(text(), ' tests failed,')]").first();

            // Select the second <b> element within this <small> element for the "skipped" value
            Elements boldElements = Objects.requireNonNull(smallElement).select("b");
            if (boldElements.size() < 2) {
                throw new IndexOutOfBoundsException("The <small> element does not contain enough <b> elements to retrieve skipped value.");
            }
            String skippedValue = boldElements.get(1).text();

            // Clone the testFailedColumn element for the "Tests Skipped" column
            Element testSkippedColumn = Objects.requireNonNull(testFailedColumn.first()).clone();
            testSkippedColumn.attr("class", "col-md-2");

            // Update the content inside the cloned element
            Objects.requireNonNull(testSkippedColumn.selectFirst("p")).text("Tests Skipped").attr("class", "m-b-0 text-skip");
            Objects.requireNonNull(testSkippedColumn.selectFirst("h3")).text(skippedValue);

            // Append the new element to the parent container
            Objects.requireNonNull(Objects.requireNonNull(testFailedColumn.first()).parent())
                    .appendChild(testSkippedColumn);

            // Save the updated HTML directly to the original file
            try (FileWriter writer = new FileWriter(inputFile)) {
                writer.write(doc.outerHtml());
            }
        } catch (IOException e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }

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
        testName = result.getParameters().length > 0 ? result.getParameters()[0].toString() : result.getName();

        test = extent.createTest(testName);

        // Logger
        logger.info("===== STEP =====> Test Started: {} ", testName);
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

        test.log(Status.FAIL, result.getThrowable());

        // Capture screenshot on test failure and add it to ExtentReport
        WebDriver driver = getDriver(result);
        if (driver == null) return;

        try {
            // Capture screenshot and save it as Base64 string
            String base64Image = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
            test.addScreenCaptureFromBase64String(base64Image);
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