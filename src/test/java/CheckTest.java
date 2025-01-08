import baseTest.BaseTest;
import io.appium.java_client.ios.IOSDriver;
import org.testng.Assert;
import org.testng.annotations.*;
import utility.ListenerUtils;
import utility.ScreenshotUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

@Listeners(ListenerUtils.class)
public class CheckTest extends BaseTest {

   @Test
    void t() throws IOException, URISyntaxException {
//       initDriver("SELLER", "IOS");
//       // Assuming you have already initialized the Appium driver
//       String sessionId = ((IOSDriver) driver).getSessionId().toString();
//
//       // Print or log the current session ID
//       System.out.println("Current Appium Session ID: " + sessionId);
      new ScreenshotUtils().compareImages("/Users/nguyenthang/IdeaProjects/LocalProject1/src/main/resources/files/checkbox_images/checked.png", "/Users/nguyenthang/IdeaProjects/LocalProject1/src/main/resources/files/element_image/el_image.png");

   }
}
