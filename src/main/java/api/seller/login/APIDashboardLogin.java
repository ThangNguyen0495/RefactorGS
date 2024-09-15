package api.seller.login;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import utility.APIUtils;

/**
 * This class handles the login functionality for the dashboard API.
 * It provides methods to authenticate and retrieve seller information.
 */
public class APIDashboardLogin {

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
    public static class SellerInformation {
        private long id;
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
            private long id;
            private String name;
            private String symbol;
        }
    }

    /**
     * Retrieves seller information based on the provided credentials.
     *
     * @param credentials The {@link Credentials} used to authenticate the seller.
     * @return A {@link SellerInformation} object containing details about the authenticated seller.
     */
    public SellerInformation getSellerInformation(Credentials credentials) {
        return new APIUtils().login("/api/authenticate/store/email/gosell", credentials)
                .then()
                .statusCode(200)
                .extract()
                .as(SellerInformation.class);
    }
}
