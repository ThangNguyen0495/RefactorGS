package api.seller.product;

import api.seller.login.APISellerLogin;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import utility.APIUtils;

import java.util.List;

public class APIGetProductCollection {
    private final APISellerLogin.LoginInformation loginInfo;

    public APIGetProductCollection(APISellerLogin.Credentials credentials) {
        this.loginInfo = new APISellerLogin().getSellerInformation(credentials);
    }


    /**
     * Class representing a product collection.
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ProductCollection {
        private int id;
        private String collectionName;
        private String collectionType;
        private int sellerId;
        private String itemType;
    }

    public List<ProductCollection> getProductCollections(int productId){
        return new APIUtils().get("/itemservice/api/collections/products/%d".formatted(productId), loginInfo.getAccessToken())
                .then().statusCode(200)
                .extract().jsonPath()
                .getList(".", ProductCollection.class);
    }
}
