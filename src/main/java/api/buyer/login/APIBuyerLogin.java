package api.buyer.login;

import api.seller.login.APISellerLogin;
import utility.APIUtils;

/**
 * Handles the login process for buyers by extending the seller login functionality.
 */
public class APIBuyerLogin extends APISellerLogin {

    /**
     * Retrieves the login information for a buyer.
     *
     * @param credentials the buyer's login credentials
     * @return the buyer's LoginInformation object
     */
    public LoginInformation getBuyerInformation(Credentials credentials) {
        // Perform buyer login using the API and return the login information
        return new APIUtils().post("/api/authenticate/mobile", null, credentials)
                .then().statusCode(200)
                .extract().as(LoginInformation.class);
    }
}
