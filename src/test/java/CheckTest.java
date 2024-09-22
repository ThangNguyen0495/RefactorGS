import api.buyer.login.APIBuyerLogin;
import api.seller.login.APISellerLogin;
import api.seller.product.APIGetProductDetail;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import utility.ExtendReportListener;
import utility.PropertiesUtils;
import utility.WebDriverManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static api.seller.login.APISellerLogin.Credentials;

@Listeners(ExtendReportListener.class)
public class CheckTest {
    @Test
    void t() throws JsonProcessingException {
        Credentials sellerCredentials =PropertiesUtils.getSellerCredentials();
        APISellerLogin.Credentials buyerCredentials = PropertiesUtils.getBuyerCredentials();
////        new APICreateProduct(credentials).createProduct(false, false, 5);
////        var languageInformation = new APIGetStoreLanguage(credentials).getStoreLanguageInformation();
////        System.out.println(new ObjectMapper().writeValueAsString(languageInformation));
////        System.out.println(APIGetStoreLanguage.getDefaultLanguageCode(languageInformation));
//
////        var productInfo = ProductUtils.generateProductInformation(credentials, "vi", true, true, true, true, true, true, true, true, true, true, true, true, true, true, 1, 1, 1,2);
////        System.out.println(new APIGetStoreDefaultLanguage(credentials).getDefaultLanguage());
//        System.out.println(PropertiesUtils.getDomain());
        System.out.println(new ObjectMapper().writeValueAsString(new APIGetProductDetail(sellerCredentials).getProductInformation(1298602)));
    }

    @Test
    void v() {
        var driver = new WebDriverManager().getWebDriver();
        driver.get(PropertiesUtils.getStoreURL());
      var s = driver.manage().getCookieNamed("langKey");
        System.out.println(s.getValue());
        driver.quit();
    }
}
