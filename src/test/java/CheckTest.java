import api.seller.product.APIGetProductDetail;
import api.seller.setting.APIGetStoreDefaultLanguage;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import utility.ExtendReportListener;
import utility.ProductUtils;
import utility.PropertiesUtils;

import static api.seller.login.APIDashboardLogin.Credentials;

@Listeners(ExtendReportListener.class)
public class CheckTest {
    @Test
    void t() throws JsonProcessingException {
        Credentials credentials = new Credentials(PropertiesUtils.getSellerAccount(), PropertiesUtils.getSellerPassword());
////        new APICreateProduct(credentials).createProduct(false, false, 5);
////        var languageInformation = new APIGetStoreLanguage(credentials).getStoreLanguageInformation();
////        System.out.println(new ObjectMapper().writeValueAsString(languageInformation));
////        System.out.println(APIGetStoreLanguage.getDefaultLanguageCode(languageInformation));
//
////        var productInfo = ProductUtils.generateProductInformation(credentials, "vi", true, true, true, true, true, true, true, true, true, true, true, true, true, true, 1, 1, 1,2);
////        System.out.println(new APIGetStoreDefaultLanguage(credentials).getDefaultLanguage());
//        System.out.println(PropertiesUtils.getDomain());
        Assert.assertTrue(false);
    }

    @Test
    void v() {
        Assert.assertFalse(true);
    }
}
