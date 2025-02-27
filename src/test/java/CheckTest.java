import baseTest.BaseTest;
import io.appium.java_client.android.AndroidDriver;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import utility.ListenerUtils;

import java.io.IOException;
import java.net.URISyntaxException;

@Listeners(ListenerUtils.class)
public class CheckTest extends BaseTest {

    @Test
    void loc_txt() throws IOException, URISyntaxException {
      initDriver("BUYER", "ANDROID");
      driver.quit();
    }
}
