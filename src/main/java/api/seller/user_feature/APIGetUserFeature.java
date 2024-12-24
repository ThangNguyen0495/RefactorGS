package api.seller.user_feature;

import api.seller.login.APISellerLogin;
import lombok.Data;
import lombok.Getter;
import utility.APIUtils;

import java.time.Instant;
import java.util.List;

/**
 * APIGetUserFeature is responsible for retrieving and analyzing user features from the API.
 * It interacts with the seller login API and utilizes the APIUtils class for HTTP requests.
 */
public class APIGetUserFeature {
    private final APISellerLogin.LoginInformation loginInfo;

    /**
     * Constructor to initialize APIGetUserFeature with seller credentials.
     *
     * @param credentials The seller's login credentials.
     */
    public APIGetUserFeature(APISellerLogin.Credentials credentials) {
        this.loginInfo = new APISellerLogin().getSellerInformation(credentials);
    }

    /**
     * Constructs the API endpoint path for fetching user features based on user ID.
     *
     * @param userId The user ID for which to fetch features.
     * @return The formatted API path.
     */
    private String getUserFeaturePath(int userId) {
        return " /beehiveservices/api/user-features/user-ids/%d?langKey=".formatted(userId);
    }

    /**
     * Represents a package associated with a user, including its features and metadata.
     */
    @Data
    public static class UserPackage {
        private UserFeature userFeature;
        private String packageName;
        private boolean hasOpenOrderRequest;
        private List<String> featureCodes;

        /**
         * Represents the feature details of a user's package.
         */
        @Data
        public static class UserFeature {
            private int id;
            private int userId;
            private int packageId;
            private long registerPackageDate;
            private long expiredPackageDate;
            private String packagePay;
        }
    }

    @Getter
    private enum PackageId {
        GoOMNI(5), GoWEB(6), GoAPP(7), GoPOS(8), GoLEAD(9), GoSOCIAL(10);

        // Getter method for index
        private final int index;

        // Constructor
        PackageId(int index) {
            this.index = index;
        }

    }

    /**
     * Retrieves the list of user packages for the logged-in user.
     *
     * @return A list of UserPackage objects.
     */
    public List<UserPackage> getUserFeature() {
        return new APIUtils()
                .get(getUserFeaturePath(loginInfo.getId()), loginInfo.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList(".", UserPackage.class);
    }

    /**
     * Checks if the user has an active GoWEB package.
     *
     * @param userPackages The list of user packages.
     * @return True if the user has an active GoWEB package, otherwise false.
     */
    public static boolean hasGoWEB(List<UserPackage> userPackages) {
        return hasActivePackage(userPackages, "GoWEB");
    }

    /**
     * Checks if the user has an active GoAPP package.
     *
     * @param userPackages The list of user packages.
     * @return True if the user has an active GoAPP package, otherwise false.
     */
    public static boolean hasGoAPP(List<UserPackage> userPackages) {
        return hasActivePackage(userPackages, "GoAPP");
    }

    /**
     * Checks if the user has an active GoSOCIAL package.
     *
     * @param userPackages The list of user packages.
     * @return True if the user has an active GoSOCIAL package, otherwise false.
     */
    public static boolean hasGoSOCIAL(List<UserPackage> userPackages) {
        return hasActivePackage(userPackages, "GoSOCIAL");
    }

    /**
     * Checks if the user has an active GoPOS package.
     *
     * @param userPackages The list of user packages.
     * @return True if the user has an active GoPOS package, otherwise false.
     */
    public static boolean hasGoPOS(List<UserPackage> userPackages) {
        return hasActivePackage(userPackages, "GoPOS");
    }

    /**
     * Checks if the user has an active package by name.
     *
     * @param userPackages The list of user packages.
     * @param packageName  The name of the package to check.
     * @return True if the package is active, otherwise false.
     */
    private static boolean hasActivePackage(List<UserPackage> userPackages, String packageName) {
        // Return false if the list is empty or null (no packages to check).
        if (userPackages.isEmpty()) return false;

        // Stream through the list of user packages to find a match and check activity status.
        return userPackages.stream()
                .filter(userPackage ->
                        // Check if the package name matches OR the package ID matches the given enum value.
                        userPackage.getPackageName() != null && userPackage.getPackageName().equals(packageName)
                        || userPackage.getUserFeature().getPackageId() == PackageId.valueOf(packageName).getIndex()
                )
                .findFirst() // Find the first package that matches the criteria.
                .map(userPackage ->
                        // Check if the package's expiry date is later than the current time.
                        userPackage.getUserFeature().getExpiredPackageDate() * 1000 > Instant.now().toEpochMilli()
                )
                .orElse(false); // Return false if no matching package is found or the map returns null.
    }

}