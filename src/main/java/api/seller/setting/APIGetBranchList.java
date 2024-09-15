package api.seller.setting;

import api.seller.login.APIDashboardLogin;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import utility.APIUtils;

import java.util.List;

/**
 * This class provides methods to retrieve and process branch information from the API.
 * It uses login credentials to fetch branch details associated with a specific store.
 */
public class APIGetBranchList {

    private final APIDashboardLogin.SellerInformation loginInfo;

    /**
     * Constructs an APIGetBranchList instance with the given credentials.
     * Initializes login information using the provided credentials.
     *
     * @param credentials The credentials to use for authentication.
     */
    public APIGetBranchList(APIDashboardLogin.Credentials credentials) {
        this.loginInfo = new APIDashboardLogin().getSellerInformation(credentials);
    }

    /**
     * Data class representing branch information.
     * Contains details such as created date, last modified date, branch ID, name, address, and various other attributes.
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BranchInformation {
        private String createdDate;
        private String lastModifiedDate;
        private int id;
        private String name;
        private long storeId;
        private String code;
        private String address;
        private String ward;
        private String district;
        private String city;
        private String phoneNumberFirst;
        private String email;
        private boolean isDefault;
        private String branchStatus;
        private String branchType;
        private String storeName;
        private boolean hideOnStoreFront;
        private String countryCode;
        private boolean status;
    }

    /**
     * Fetches branch information from the API for the store associated with the credentials.
     * Retrieves details about the branches such as ID, name, address, etc.
     *
     * @return A list of BranchInformation objects containing details about the store's branches.
     */
    public List<BranchInformation> getBranchInformation() {
        return new APIUtils().get("/storeservice/api/store-branch/full?storeId=%s&page=0&size=100".formatted(loginInfo.getStore().getId()), loginInfo.getAccessToken())
                .then().statusCode(200)
                .extract().jsonPath()
                .getList(".", BranchInformation.class);
    }

    /**
     * Extracts branch IDs from a list of BranchInformation objects.
     *
     * @param branchInfoList The list of BranchInformation objects.
     * @return A list of branch IDs.
     */
    public static List<Integer> getBranchIds(List<BranchInformation> branchInfoList) {
        return branchInfoList.stream().map(BranchInformation::getId).toList();
    }

    /**
     * Extracts branch names from a list of BranchInformation objects.
     *
     * @param branchInfoList The list of BranchInformation objects.
     * @return A list of branch names.
     */
    public static List<String> getBranchNames(List<BranchInformation> branchInfoList) {
        return branchInfoList.stream().map(BranchInformation::getName).toList();
    }

    /**
     * Extracts branch types from a list of BranchInformation objects.
     *
     * @param branchInfoList The list of BranchInformation objects.
     * @return A list of branch types.
     */
    public static List<String> getBranchTypes(List<BranchInformation> branchInfoList) {
        return branchInfoList.stream().map(BranchInformation::getBranchType).toList();
    }
}