package api.seller.login;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import utility.APIUtils;

public class APIDashboardLogin {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Credentials {
        private String username;
        private String password;
    }

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


        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Store {
            private long id;
            private String name;
            private String symbol;
        }
    }

    public SellerInformation getSellerInformation(Credentials credentials) {
        return new APIUtils().login("/api/authenticate/store/email/gosell", credentials)
                .then()
                .statusCode(200)
                .extract()
                .as(SellerInformation.class);
    }
}
