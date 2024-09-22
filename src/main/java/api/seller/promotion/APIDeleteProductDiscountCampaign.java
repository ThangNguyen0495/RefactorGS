package api.seller.promotion;

import api.seller.login.APISellerLogin;
import utility.APIUtils;

/**
 * This class handles the deletion of product discount campaigns for a seller.
 */
public class APIDeleteProductDiscountCampaign {
    private final APISellerLogin.LoginInformation loginInfo;

    /**
     * Constructs an APIDeleteProductDiscountCampaign instance with the given seller credentials.
     *
     * @param credentials the credentials of the seller to retrieve information
     */
    public APIDeleteProductDiscountCampaign(APISellerLogin.Credentials credentials) {
        this.loginInfo = new APISellerLogin().getSellerInformation(credentials);
    }

    /**
     * Deletes a discount campaign identified by the specified campaign ID.
     *
     * @param campaignId the ID of the discount campaign to be deleted
     * @throws RuntimeException if the delete operation fails (i.e., the status code is not 200)
     */
    public void deleteDiscountCampaign(int campaignId) {
        // Sending a delete request for the specified campaign ID
        new APIUtils().delete("/orderservices2/api/gs-discount-campaigns/%d".formatted(campaignId), loginInfo.getAccessToken())
                .then()
                .statusCode(200); // Checking if the delete operation was successful
    }
}