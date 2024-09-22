package api.seller.promotion;

import api.seller.login.APISellerLogin;
import utility.APIUtils;

/**
 * Class to handle deletion of flash sales through the API.
 */
public class APIDeleteFlashSale {
    private final APISellerLogin.LoginInformation loginInfo;

    /**
     * Constructor to initialize the API with seller credentials.
     *
     * @param credentials the seller's credentials.
     */
    public APIDeleteFlashSale(APISellerLogin.Credentials credentials) {
        this.loginInfo = new APISellerLogin().getSellerInformation(credentials);
    }

    /**
     * Deletes a scheduled flash sale with the specified ID.
     *
     * @param saleId the ID of the flash sale to be deleted.
     * @throws RuntimeException if the deletion fails or does not return a success status code.
     */
    public void deleteScheduledFlashSale(int saleId) {
        String url = String.format("/itemservice/api/campaigns/delete/%d?storeId=%d", saleId, loginInfo.getStore().getId());
        new APIUtils().delete(url, loginInfo.getAccessToken()).then().statusCode(200);
    }
}
