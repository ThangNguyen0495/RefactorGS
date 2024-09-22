package api.seller.promotion;

import api.seller.login.APISellerLogin;
import utility.APIUtils;

/**
 * Class to handle ending flash sales through the API.
 */
public class APIEndFlashSale {
    private final APISellerLogin.LoginInformation loginInfo;

    /**
     * Constructor to initialize the API with seller credentials.
     *
     * @param credentials the seller's credentials.
     */
    public APIEndFlashSale(APISellerLogin.Credentials credentials) {
        this.loginInfo = new APISellerLogin().getSellerInformation(credentials);
    }

    /**
     * Ends an in-progress flash sale early.
     *
     * @param saleId the ID of the flash sale to be ended.
     * @throws RuntimeException if the request fails or does not return a success status code.
     */
    public void endInProgressFlashSale(int saleId) {
        String url = String.format("/itemservice/api/campaigns/end-early/%d?storeId=%d", saleId, loginInfo.getStore().getId());
        new APIUtils().post(url, loginInfo.getAccessToken(), null).then().statusCode(200);
    }
}
