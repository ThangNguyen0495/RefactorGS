package api.seller.setting;

import api.seller.login.APISellerLogin;
import utility.APIUtils;

public class APIGetStoreDefaultLanguage {
    /**
     * Seller information retrieved using the provided credentials.
     */
    private final APISellerLogin.LoginInformation loginInfo;

    /**
     * Constructs an instance of {@link APIGetStoreDefaultLanguage} with the specified credentials.
     *
     * @param credentials The credentials used to authenticate and access the store API.
     */
    public APIGetStoreDefaultLanguage(APISellerLogin.Credentials credentials) {
        loginInfo = new APISellerLogin().getSellerInformation(credentials);
    }

    /**
     * Retrieves the default language of the store using the seller's authenticated information.
     *
     * @return The default language code of the store.
     */
    public String getDefaultLanguage() {
        return new APIUtils().get("/storeservice/api/store-language/store/%d?hasInitial=true".formatted(loginInfo.getStore().getId()), loginInfo.getAccessToken())
                .then().statusCode(200)
                .extract().jsonPath()
                .getList("langCode", String.class)
                .getFirst();
    }
}
