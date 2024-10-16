import api.seller.login.APISellerLogin;
import api.seller.product.APIGetProductDetail;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import pages.web.seller.product.all_products.BaseProductPage;
import utility.ListenerUtils;
import utility.PropertiesUtils;

import static api.seller.login.APISellerLogin.Credentials;

@Listeners(ListenerUtils.class)
public class CheckTest {
    @Test
    void t() throws JsonProcessingException {
//        Credentials sellerCredentials =PropertiesUtils.getSellerCredentials();
//        APISellerLogin.Credentials buyerCredentials = PropertiesUtils.getBuyerCredentials();
////        new APICreateProduct(credentials).createProduct(false, false, 5);
////        var languageInformation = new APIGetStoreLanguage(credentials).getStoreLanguageInformation();
////        System.out.println(new ObjectMapper().writeValueAsString(languageInformation));
////        System.out.println(APIGetStoreLanguage.getDefaultLanguageCode(languageInformation));
//
////        var productInfo = ProductUtils.generateProductInformation(credentials, "vi", true, true, true, true, true, true, true, true, true, true, true, true, true, true, 1, 1, 1,2);
////        System.out.println(new APIGetStoreDefaultLanguage(credentials).getDefaultLanguage());
//        System.out.println(PropertiesUtils.getDomain());
//       new BaseProductPage(null).fetchInformation(sellerCredentials).getTranslate(1298602);
        String s = null;
        String x = String.valueOf(s);
        System.out.println(x);
    }


}
