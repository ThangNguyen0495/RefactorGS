package api.seller.setting;

import api.seller.login.APISellerLogin;
import utility.APIUtils;

public class APIGetStoreURL {

    private final APISellerLogin.LoginInformation loginInfo;

    public APIGetStoreURL(APISellerLogin.Credentials credentials) {
        loginInfo = new APISellerLogin().getSellerInformation(credentials);
    }

    public String getStoreURL() {
        String path = "/storeservice/api/store-urls/stores/%d/domains".formatted(loginInfo.getStore().getId());
        return new APIUtils().get(path, loginInfo.getAccessToken())
                .then().statusCode(200)
                .extract().asPrettyString();
    }
}
