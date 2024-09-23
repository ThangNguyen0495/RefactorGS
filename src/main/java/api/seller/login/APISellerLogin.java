package api.seller.login;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import utility.APIUtils;

import java.util.Objects;

/**
 * This class handles the login functionality for the dashboard API.
 * It provides methods to authenticate and retrieve seller information.
 */
public class APISellerLogin {

    private static Credentials cachedCredentials;
    private static LoginInformation cachedSellerInfo;

    /**
     * Represents the credentials used for logging into the dashboard.
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Credentials {
        private String username;
        private String password;
    }

    /**
     * Represents information about a seller retrieved after authentication.
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LoginInformation {
        private int id;
        private String login;
        private String displayName;
        private String email;
        private String accountType;
        private boolean activated;
        private String status;
        private String langKey;
        private String[] authorities;
        private Store store;
        private String refreshToken;
        private String accessToken;

        /**
         * Represents information about the store associated with the seller.
         */
        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Store {
            private int id;
            private String name;
            private String symbol;
        }
    }

    /**
     * Retrieves seller information based on the provided credentials.
     * If the same credentials are used, cached information is returned.
     *
     * @param credentials The {@link Credentials} used to authenticate the seller.
     * @return A {@link LoginInformation} object containing details about the authenticated seller.
     */
    public LoginInformation getSellerInformation(Credentials credentials) {
        // If cached credentials match, return cached seller information
        if (Objects.equals(credentials, cachedCredentials)) {
            return cachedSellerInfo;
        }

        // Perform login and update cached credentials and seller information
        cachedSellerInfo = authenticateSeller(credentials);
        cachedCredentials = credentials;

        return cachedSellerInfo;
    }

    /**
     * Authenticates the seller and retrieves seller information by making a login API call.
     *
     * @param credentials The credentials to authenticate with.
     * @return A {@link LoginInformation} object with the seller's details.
     */
    private LoginInformation authenticateSeller(Credentials credentials) {
        return new APIUtils().post("/api/authenticate/store/email/gosell", null, credentials)
                .then()
                .statusCode(200)
                .extract()
                .as(LoginInformation.class);
    }
}