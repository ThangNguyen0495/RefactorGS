package api.seller.setting;

import api.seller.login.APISellerLogin;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import utility.APIUtils;

import java.util.List;

/**
 * This class provides functionality to retrieve store information from the API.
 * It uses credentials from {@link APISellerLogin} to fetch details about the store.
 */
public class APIGetStoreInformation {

    /**
     * Represents detailed information about a store.
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StoreInformation {
        private long id;
        private String name;
        private String url;
        private long ownerId;
        private double rating;
        private int totalRating;
        private int totalReview;
        private String city;
        private String storeType;
        private int follower;
        private List<Integer> categoryIds;
        private String contactNumber;
        private String email;
        private String ward;
        private String address;
        private boolean deleted;
        private boolean hideChat;
        private boolean guestCheckout;
        private boolean itemReview;
        private String addressList;
        private String seoTitle;
        private String seoDescription;
        private String seoKeywords;
        private List<DeliveryProvider> deliveryProviders;
        private String description;
        private boolean showSupportChat;
        private String openingHours;
        private boolean showTax;
        private boolean useNewGoSocial;
        private String countryCode;
        private String currencyCode;
        private String symbol;
        private String createdDate;
        private boolean applyTaxAfterDiscount;
        private String shopeeConnectStatus;
        private String tiktokConnectStatus;
        private String lazadaConnectStatus;
        private String zoneCode;
        private String dashboardDomain;
        private boolean activatedGoAi;
        private List<StoreBranch> storeBranches;

        /**
         * Represents a delivery provider associated with the store.
         */
        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class DeliveryProvider {
            private String providerName;
            private List<String> allowedLocations;
            private List<String> allowedCountryCodeList;
            private boolean enabled;
        }

        /**
         * Represents a branch of the store.
         */
        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class StoreBranch {
            private String createdDate;
            private String lastModifiedDate;
            private long id;
            private String name;
            private String code;
            private String address;
            private String ward;
            private String district;
            private String city;
            private boolean isDefault;
            private String branchStatus;
            private String branchType;
            private String storeName;
            private boolean hideOnStoreFront;
            private String countryCode;
            private boolean status;
        }
    }

    /**
     * Seller information retrieved using the provided credentials.
     */
    private final APISellerLogin.LoginInformation loginInfo;

    /**
     * Constructs an instance of {@link APIGetStoreInformation} with the specified credentials.
     *
     * @param credentials The credentials used to authenticate and access the store API.
     */
    public APIGetStoreInformation(APISellerLogin.Credentials credentials) {
        loginInfo = new APISellerLogin().getSellerInformation(credentials);
    }

    /**
     * Retrieves the store information using the provided credentials.
     *
     * @return A list of {@link StoreInformation} objects representing the details of the store.
     */
    public StoreInformation getStoreInformation() {
        return new APIUtils().get("/storeservice/api/stores/%d".formatted(loginInfo.getStore().getId()), loginInfo.getAccessToken())
                .then().statusCode(200)
                .extract().as(StoreInformation.class);
    }
}
