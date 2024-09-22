package api.seller.setting;

import api.seller.login.APISellerLogin;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import utility.APIUtils;

import java.util.List;

/**
 * This class provides methods to retrieve and process branch information from the API.
 * It uses login credentials to fetch branch details associated with a specific store.
 */
public class APIGetBranchList {

    private final APISellerLogin.LoginInformation loginInfo;

    /**
     * Constructs an APIGetBranchList instance with the given credentials.
     * Initializes login information using the provided credentials.
     *
     * @param credentials The credentials to use for authentication.
     */
    public APIGetBranchList(APISellerLogin.Credentials credentials) {
        this.loginInfo = new APISellerLogin().getSellerInformation(credentials);
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
     * @param branchInfos The list of BranchInformation objects.
     * @return A list of branch IDs.
     */
    public static List<Integer> getBranchIds(List<BranchInformation> branchInfos) {
        return branchInfos.stream().map(BranchInformation::getId).toList();
    }

    /**
     * Extracts the branch ID at a specific index from a list of BranchInformation objects.
     *
     * @param branchInfos  The list of BranchInformation objects containing branch details.
     * @param branchIndex  The index of the branch whose ID is to be retrieved.
     * @return The branch ID at the specified index.
     * @throws IndexOutOfBoundsException if the branchIndex is out of range.
     */
    public static int getBranchId(List<BranchInformation> branchInfos, int branchIndex) {
        return branchInfos.get(branchIndex).getId();
    }


    /**
     * Extracts branch names from a list of BranchInformation objects.
     *
     * @param branchInfos The list of BranchInformation objects.
     * @return A list of branch names.
     */
    public static List<String> getBranchNames(List<BranchInformation> branchInfos) {
        return branchInfos.stream().map(BranchInformation::getName).toList();
    }

    /**
     * Extracts branch types from a list of BranchInformation objects.
     *
     * @param branchInfos The list of BranchInformation objects from which to extract branch types.
     * @return A list of branch types.
     */
    public static List<String> getBranchTypes(List<BranchInformation> branchInfos) {
        return branchInfos.stream().map(BranchInformation::getBranchType).toList();
    }

    /**
     * Retrieves the names of branches that have a status of "ACTIVE"
     * from a list of BranchInformation objects.
     *
     * @param branchInfos The list of BranchInformation objects to filter active branches from.
     * @return A list of names of branches that have a status of "ACTIVE".
     */
    public static List<String> getActiveBranchNames(List<BranchInformation> branchInfos) {
        return branchInfos.stream()
                .filter(branchInformation -> branchInformation.getBranchStatus().equals("ACTIVE"))
                .map(BranchInformation::getName)
                .toList();
    }

    /**
     * Retrieves the IDs of branches that have a status of "ACTIVE"
     * from a list of BranchInformation objects.
     *
     * @param branchInfos The list of BranchInformation objects to filter active branches from.
     * @return A list of IDs of branches that have a status of "ACTIVE".
     */
    public static List<Integer> getActiveBranchIds(List<BranchInformation> branchInfos) {
        return branchInfos.stream()
                .filter(branchInformation -> branchInformation.getBranchStatus().equals("ACTIVE"))
                .map(BranchInformation::getId)
                .toList();
    }

    /**
     * Retrieves a list of {@code hideOnStoreFront} flags from the given list of {@code BranchInformation}.
     *
     * @param branchInfos The list of {@code BranchInformation} objects.
     * @return A list of {@code hideOnStoreFront} flags as Booleans.
     */
    public static List<Boolean> getHideOnStorefrontFlags(List<BranchInformation> branchInfos) {
        return branchInfos.stream()
                .map(BranchInformation::isHideOnStoreFront)
                .toList();
    }

    /**
     * Determines if a branch is shown on the storefront based on its status and {@code hideOnStoreFront} flag.
     * <p>
     * A branch is considered visible on the storefront if its status is "ACTIVE" and
     * its {@code hideOnStoreFront} flag is set to {@code false}.
     *
     * @param branchInfos The list of {@code BranchInformation} objects representing the branches.
     * @param branchIndex    The index of the branch to check in the list.
     * @return {@code true} if the branch is visible on the storefront, {@code false} otherwise.
     */
    public static boolean isBranchShownOnStorefront(List<BranchInformation> branchInfos, int branchIndex) {
        return "ACTIVE".equalsIgnoreCase(branchInfos.get(branchIndex).getBranchStatus())
               && !branchInfos.get(branchIndex).isHideOnStoreFront();
    }

    /**
     * Retrieves the branch code for the branch at the specified index.
     *
     * @param branchInfos The list of {@code BranchInformation} objects.
     * @param branchIndex The index of the branch in the list.
     * @return The branch code as a string.
     */
    public static String getBranchCode(List<BranchInformation> branchInfos, int branchIndex) {
        return branchInfos.get(branchIndex).getCode();
    }

    /**
     * Retrieves the branch address for the branch at the specified index.
     *
     * @param branchInfos The list of {@code BranchInformation} objects.
     * @param branchIndex The index of the branch in the list.
     * @return The branch address as a string.
     */
    public static String getBranchAddress(List<BranchInformation> branchInfos, int branchIndex) {
        return branchInfos.get(branchIndex).getAddress();
    }

    /**
     * Retrieves the ward code for the branch at the specified index.
     *
     * @param branchInfos The list of {@code BranchInformation} objects.
     * @param branchIndex The index of the branch in the list.
     * @return The ward code of the branch as a string.
     */
    public static String getBranchWardCode(List<BranchInformation> branchInfos, int branchIndex) {
        return branchInfos.get(branchIndex).getWard();
    }

    /**
     * Retrieves the district code for the branch at the specified index.
     *
     * @param branchInfos The list of {@code BranchInformation} objects.
     * @param branchIndex The index of the branch in the list.
     * @return The district code of the branch as a string.
     */
    public static String getBranchDistrictCode(List<BranchInformation> branchInfos, int branchIndex) {
        return branchInfos.get(branchIndex).getDistrict();
    }

    /**
     * Retrieves the city code for the branch at the specified index.
     *
     * @param branchInfos The list of {@code BranchInformation} objects.
     * @param branchIndex The index of the branch in the list.
     * @return The city code of the branch as a string.
     */
    public static String getBranchCityCode(List<BranchInformation> branchInfos, int branchIndex) {
        return branchInfos.get(branchIndex).getCity();
    }

    /**
     * Retrieves the phone number for the branch at the specified index.
     *
     * @param branchInfos The list of {@code BranchInformation} objects.
     * @param branchIndex The index of the branch in the list.
     * @return The branch's phone number as a string.
     */
    public static String getBranchPhoneNumber(List<BranchInformation> branchInfos, int branchIndex) {
        return branchInfos.get(branchIndex).getPhoneNumberFirst();
    }

    /**
     * Retrieves the status of the branch at the specified index.
     *
     * @param branchInfos The list of {@code BranchInformation} objects.
     * @param branchIndex The index of the branch in the list.
     * @return The status of the branch as a string (e.g., ACTIVE/INACTIVE).
     */
    public static String getBranchStatus(List<BranchInformation> branchInfos, int branchIndex) {
        return branchInfos.get(branchIndex).getBranchStatus();
    }

    /**
     * Retrieves the {@code hideOnStoreFront} flag for the branch at the specified index.
     *
     * @param branchInfos The list of {@code BranchInformation} objects.
     * @param branchIndex The index of the branch in the list.
     * @return {@code true} if the branch is hidden on the storefront, {@code false} otherwise.
     */
    public static boolean getHideOnStoreFrontFlag(List<BranchInformation> branchInfos, int branchIndex) {
        return branchInfos.get(branchIndex).isHideOnStoreFront();
    }

    /**
     * Retrieves the country code for the branch at the specified index.
     *
     * @param branchInfos The list of {@code BranchInformation} objects.
     * @param branchIndex The index of the branch in the list.
     * @return The country code of the branch as a string.
     */
    public static String getBranchCountryCode(List<BranchInformation> branchInfos, int branchIndex) {
        return branchInfos.get(branchIndex).getCountryCode();
    }
}