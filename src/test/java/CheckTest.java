import api.seller.login.APISellerLogin;
import api.seller.product.APIGetInventoryHistory;
import baseTest.BaseTest;
import io.restassured.RestAssured;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.SystemDefaultDnsResolver;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import utility.ListenerUtils;
import utility.PropertiesUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Listeners(ListenerUtils.class)
public class CheckTest extends BaseTest {

    @Test
    void loc_txt() {
       var s = new APIGetInventoryHistory(PropertiesUtils.getSellerCredentials()).checkProductCanBeDeleted(1224983);
        System.out.println(s);
    }
}
