import api.seller.product.APICreateProduct;
import api.seller.product.APIGetProductDetail;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import static api.seller.login.APIDashboardLogin.Credentials;

public class CheckTest {
    @Test
    void t() throws JsonProcessingException {
        Credentials credentials = new Credentials("stgaboned@nbobd.com", "Abc@12345");
//        new APICreateProduct(credentials).createProduct(false, false, 5);
//        var languageInformation = new APIGetStoreLanguage(credentials).getStoreLanguageInformation();
//        System.out.println(new ObjectMapper().writeValueAsString(languageInformation));
//        System.out.println(APIGetStoreLanguage.getDefaultLanguageCode(languageInformation));

     var info = ProductInformationGenerator.generateProductInformation(credentials,"vi", true, false,false, false, false, false, false, false, false, false, false, false, false,false, 1, 2, 3);
        System.out.println(APIGetProductDetail.getVariationValues(info, "vi"));
        System.out.println(APIGetProductDetail.getProductStockQuantityMap(info));
        System.out.println(APIGetProductDetail.getVariationListingPrice(info));
        System.out.println(new ObjectMapper().writeValueAsString(info));
    }


}
