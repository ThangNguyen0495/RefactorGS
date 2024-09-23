package api.seller.sale_channel;

import api.seller.login.APISellerLogin;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import utility.APIUtils;

/**
 * API class to retrieve the preferences related to the store's listing on various web platforms.
 * This class uses the seller's credentials to authenticate and fetch store listing preferences.
 */
public class APIGetPreferences {

    private final APISellerLogin.LoginInformation loginInfo;

    /**
     * Constructor to initialize APIGetPreferences with seller credentials.
     *
     * @param credentials the seller's login credentials used to retrieve store information.
     */
    public APIGetPreferences(APISellerLogin.Credentials credentials) {
        this.loginInfo = new APISellerLogin().getSellerInformation(credentials);
    }

    /**
     * Represents the store listing preferences for web platforms, including contact and service details.
     */
    @Data
    public static class StoreListingWebs {
        private String createdBy;
        private String createdDate;
        private String lastModifiedBy;
        private String lastModifiedDate;
        private long id;
        private long storeId;

        private boolean enabledProduct;
        private boolean enabledPhoneProduct;
        private String phoneProduct;
        private boolean enabledZaloProduct;
        private String zaloProduct;
        private boolean enabledEmailProduct;
        private String emailProduct;

        private boolean enabledService;
        private boolean enabledPhoneService;
        private String phoneService;
        private boolean enabledZaloService;
        private String zaloService;
        private boolean enabledEmailService;
        private String emailService;
    }

    /**
     * Fetches the store listing web information for the authenticated store.
     * Retries up to 5 times in case of java.lang.IllegalStateException.
     *
     * @return StoreListingWebs object containing information about the store's listing and contact preferences.
     * @throws RuntimeException if the operation fails after 5 retries.
     */
    public StoreListingWebs getStoreListingWebInformation() {
        int maxRetries = 5;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                // Attempt to fetch the store listing information
                return new APIUtils().get("/storeservice/api/store-listing-webs/%d".formatted(loginInfo.getStore().getId()), loginInfo.getAccessToken())
                        .then()
                        .statusCode(200)
                        .extract()
                        .as(StoreListingWebs.class);
            } catch (IllegalStateException e) {
                // Optionally log the retry attempt
                LogManager.getLogger().info("Retrying to fetch store listing web information, attempt {}", attempt);
            }
        }

        throw new RuntimeException("Failed to fetch store listing web information after " + maxRetries + " attempts.");
    }


}
