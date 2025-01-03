package baseTest;

import api.seller.login.APISellerLogin;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Listeners;
import utility.ListenerUtils;
import utility.PropertiesUtils;
import utility.WebDriverManager;
import utility.helper.FileDownloadHelper;

import java.io.IOException;
import java.net.URISyntaxException;

@Listeners(ListenerUtils.class)
public class BaseTest {
    public WebDriver driver;
    public APISellerLogin.Credentials sellerCredentials = PropertiesUtils.getSellerCredentials();
    public APISellerLogin.Credentials buyerCredentials = PropertiesUtils.getBuyerCredentials();

    /**
     * Initializes the driver based on the given test type and platform.
     * It handles Android, iOS, and Web platforms and downloads the appropriate app based on the test type (SELLER/BUYER).
     *
     * @param testType The type of the test (SELLER or BUYER).
     * @param platform The platform to run the test on (WEB, ANDROID, IOS).
     * @throws IOException              If there is an error in downloading the app file.
     * @throws URISyntaxException       If the URL of the app is malformed.
     * @throws IllegalArgumentException If an unsupported platform is provided.
     */
    public void initDriver(String testType, String platform) throws IOException, URISyntaxException {
        switch (platform) {
            case "WEB":
                driver = WebDriverManager.getWebDriver();
                break;

            case "ANDROID":
                String androidAppPath = System.getProperty("user.dir") + "/app/app.apk";
                String androidAppURL = getAndroidAppURL(testType);
                downloadAppFile(androidAppURL, androidAppPath);
                driver = WebDriverManager.getAndroidDriver(PropertiesUtils.getAndroidEmulatorUdid(), androidAppPath);
                break;

            case "IOS":
                String iosAppPath = System.getProperty("user.dir") + "/app/app.zip";
                String iosAppURL = getIosAppURL(testType);
                downloadAppFile(iosAppURL, iosAppPath);
                driver = WebDriverManager.getIOSDriver(PropertiesUtils.getIOSSimulatorUdid(), iosAppPath);
                break;

            default:
                throw new IllegalArgumentException("Unsupported platform: " + platform);
        }
    }

    /**
     * Get the correct app URL based on the test type for Android.
     *
     * @param testType The type of the test (SELLER or BUYER).
     * @return The Android app URL for download.
     */
    private String getAndroidAppURL(String testType) {
        return testType.equals("SELLER") ? PropertiesUtils.getAndroidSellerAppURL() : PropertiesUtils.getAndroidBuyerAppURL();
    }

    /**
     * Get the correct app URL based on the test type for iOS.
     *
     * @param testType The type of the test (SELLER or BUYER).
     * @return The iOS app URL for download.
     */
    private String getIosAppURL(String testType) {
        return testType.equals("SELLER") ? PropertiesUtils.getIOSSellerAppURL() : PropertiesUtils.getIOSBuyerAppURL();
    }

    /**
     * Helper method to download the app file.
     *
     * @param appURL The URL to download the app from.
     * @param appPath The local path to save the app.
     * @throws IOException If there's an issue with downloading the app file.
     */
    private void downloadAppFile(String appURL, String appPath) throws IOException {
        FileDownloadHelper.downloadFile(appURL, appPath);
    }
}
