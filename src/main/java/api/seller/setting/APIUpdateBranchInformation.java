package api.seller.setting;

import api.seller.login.APISellerLogin;
import api.seller.login.APISellerLogin.Credentials;
import api.seller.login.APISellerLogin.LoginInformation;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utility.APIUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * Handles updating branch information for a seller.
 */
public class APIUpdateBranchInformation {
    private final LoginInformation loginInfo; // Login information for the seller
    private final Credentials credentials; // Seller's credentials for API access
    private static final Logger logger = LogManager.getLogger(); // Logger for tracking updates

    private static final String UPDATE_BRANCH_INFORMATION_PATH = "/storeservice/api/store-branch/%s"; // Endpoint for updating branch information
    private static final String CHANGE_BRANCH_STATUS_PATH = "/storeservice/api/store-branch/setting-status/%s/%s?status=%s"; // Endpoint for changing branch status

    private static final String ACTIVE_STATUS = "ACTIVE"; // Constant for active branch status
    private static final String INACTIVE_STATUS = "INACTIVE"; // Constant for inactive branch status

    @Data
    @AllArgsConstructor
    private static class UpdateBranchPayload {
        // Payload for branch update containing necessary fields
        private int id;
        private String name;
        private int storeId;
        private String code;
        private String address;
        private String ward;
        private String district;
        private String city;
        private String phoneNumberFirst;
        private Boolean isDefault;
        private String branchStatus;
        private String storeName;
        private boolean hideOnStoreFront;
        private String countryCode;
    }

    public APIUpdateBranchInformation(Credentials credentials) {
        this.credentials = credentials; // Initialize credentials
        this.loginInfo = new APISellerLogin().getSellerInformation(credentials); // Retrieve seller's login information
    }

    /**
     * Updates information for a specific branch.
     * <p>
     * This method retrieves the current branch information and compares it with the provided parameters.
     * If there are changes in the visibility or status of the branch, it performs the API update.
     * If no changes are detected, the API call is skipped, and a log entry is made.
     *
     * @param branchId         The ID of the branch to update.
     * @param isDefault        Indicates if the branch is a default branch.
     * @param hideOnStoreFront Indicates if the branch should be hidden on the storefront.
     * @param branchStatus     The new status for the branch (ACTIVE/INACTIVE).
     */
    private void updateBranchInfo(int branchId, boolean isDefault, boolean hideOnStoreFront, String branchStatus) {
        // Retrieve current branch information
        var branchInfos = new APIGetBranchList(credentials).getBranchInformation();
        int branchIndex = APIGetBranchList.getBranchIds(branchInfos).indexOf(branchId); // Find the index of the branch

        // Get current properties of the branch
        boolean currentHideOnStoreFront = APIGetBranchList.getHideOnStoreFrontFlag(branchInfos, branchIndex);
        String currentBranchStatus = APIGetBranchList.getBranchStatus(branchInfos, branchIndex);
        String branchName = APIGetBranchList.getBranchNames(branchInfos).get(branchIndex);

        // Check if an update is necessary
        if (!Objects.equals(currentHideOnStoreFront, hideOnStoreFront) || !currentBranchStatus.equals(branchStatus)) {
            // Create a payload for the branch update
            UpdateBranchPayload updateBranchPayload = new UpdateBranchPayload(
                    branchId,
                    branchName,
                    loginInfo.getStore().getId(),
                    APIGetBranchList.getBranchCode(branchInfos, branchIndex),
                    APIGetBranchList.getBranchAddress(branchInfos, branchIndex),
                    APIGetBranchList.getBranchWardCode(branchInfos, branchIndex),
                    APIGetBranchList.getBranchDistrictCode(branchInfos, branchIndex),
                    APIGetBranchList.getBranchCityCode(branchInfos, branchIndex),
                    APIGetBranchList.getBranchPhoneNumber(branchInfos, branchIndex),
                    isDefault,
                    branchStatus,
                    loginInfo.getStore().getName(),
                    hideOnStoreFront,
                    APIGetBranchList.getBranchCountryCode(branchInfos, branchIndex)
            );

            // Update branch information via API
            String branchUpdatePath = String.format(UPDATE_BRANCH_INFORMATION_PATH, loginInfo.getStore().getId());
            new APIUtils().put(branchUpdatePath, loginInfo.getAccessToken(), updateBranchPayload)
                    .then().statusCode(200);
            logger.info("[API] Updated branch '{}', hide on storefront: {}", branchName, hideOnStoreFront); // Log the update

            // Change the status of the branch
            String statusChangePath = String.format(CHANGE_BRANCH_STATUS_PATH, loginInfo.getStore().getId(), branchId, branchStatus);
            new APIUtils().put(statusChangePath, loginInfo.getAccessToken());
            logger.info("[API] Updated branch '{}' status: {}", branchName, branchStatus); // Log the status change
        } else {
            logger.info("[{}] Branch information has not changed; skipping API update.", branchName);
        }
    }

    /**
     * Updates the visibility and status of all paid branches.
     *
     * @param hideOnStoreFront Indicates whether to hide branches on the storefront.
     * @param active           Indicates whether to activate (true) or deactivate (false) branches.
     */
    public void updateAllPaidBranches(boolean hideOnStoreFront, boolean active) {
        // Retrieve branch information for paid branches
        var branchInfos = new APIGetBranchList(credentials).getBranchInformation();
        List<Integer> branchIds = APIGetBranchList.getBranchIds(branchInfos);

        // Update each paid branch based on the specified parameters
        IntStream.range(1, branchIds.size()).forEachOrdered(branchIndex ->
                updateBranchInfo(branchIds.get(branchIndex), false, hideOnStoreFront, active ? ACTIVE_STATUS : INACTIVE_STATUS)
        );
    }

    /**
     * Hides or shows the free branch on the storefront.
     *
     * @param hide Indicates whether to hide (true) or show (false) the branch.
     */
    public void setFreeBranchVisibilityOnShopOnline(boolean hide) {
        // Retrieve the list of branch information
        var branchInfos = new APIGetBranchList(credentials).getBranchInformation();

        // Get the ID of the free branch (the first element in the branch ID list)
        int freeBranchId = APIGetBranchList.getBranchIds(branchInfos).getFirst();

        // Update the visibility of the free branch based on the hide parameter
        updateBranchInfo(freeBranchId, true, hide, ACTIVE_STATUS);

        // Return the current instance to allow for method chaining
    }
}