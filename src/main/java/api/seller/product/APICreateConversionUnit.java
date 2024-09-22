package api.seller.product;

import api.seller.login.APISellerLogin;
import utility.APIUtils;

import java.time.Instant;

/**
 * Handles the creation of conversion units via the API.
 */
public class APICreateConversionUnit {

    private final APISellerLogin.LoginInformation loginInfo;

    /**
     * Constructs an instance of APICreateConversionUnit with the provided login credentials.
     *
     * @param loginInformation The login credentials for accessing the API.
     */
    public APICreateConversionUnit(APISellerLogin.Credentials loginInformation) {
        this.loginInfo = new APISellerLogin().getSellerInformation(loginInformation);
    }

    /**
     * Creates a new conversion unit and returns its name.
     * The name is generated dynamically using the current timestamp to ensure uniqueness.
     *
     * @return The name of the created conversion unit.
     */
    public String createConversionUnitAndGetName() {
        // Generate a unique name for the conversion unit using the current timestamp
        String name = "unitName" + Instant.now().toEpochMilli();

        // Define the request body with the generated name
        String body = String.format("""
                {
                    "name": "%s"
                }""", name);

        // Send a POST request to create the conversion unit
        new APIUtils().post("/itemservice/api/item/conversion-units", loginInfo.getAccessToken(), body)
                .then()
                .statusCode(200); // Verify the creation was successful with a 200 status code

        // Return the name of the created conversion unit
        return name;
    }
}
