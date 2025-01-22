import api.seller.login.APISellerLogin;
import api.seller.product.APIGetInventoryHistory;
import baseTest.BaseTest;
import io.appium.java_client.android.AndroidDriver;
import io.restassured.RestAssured;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.SystemDefaultDnsResolver;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import utility.AndroidUtils;
import utility.ListenerUtils;
import utility.PropertiesUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import static utility.helper.ActivityHelper.buyerBundleId;

@Listeners(ListenerUtils.class)
public class CheckTest extends BaseTest {

    @Test
    void loc_txt() throws IOException, URISyntaxException {
      initDriver("BUYER", "ANDROID");
      driver.quit();
    }
}
