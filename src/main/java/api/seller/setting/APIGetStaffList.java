package api.seller.setting;

import api.seller.login.APISellerLogin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import utility.APIUtils;

import java.util.List;

import static org.apache.commons.lang.math.RandomUtils.nextInt;

public class APIGetStaffList {
    private final APISellerLogin.LoginInformation loginInfo;

    public APIGetStaffList(APISellerLogin.Credentials credentials) {
        this.loginInfo = new APISellerLogin().getSellerInformation(credentials);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StaffInformation {
        private int id;
        private long userId;
        private String email;
        private String name;
        private boolean enabled;
        private int storeId;
        private List<APIGetBranchList.BranchInformation> branches;
        private List<Integer> branchIds;
        private String permissionGroupNames;
        private String permissionGroupIds;
        private String type;
    }

    public List<StaffInformation> getStaffList() {
        // Attempt to fetch the branch information
        return new APIUtils().get("/storeservice/api/store-staffs/store/%s?isEnabledCC=false&page=0&size=10&sort=id,desc".formatted(loginInfo.getStore().getId()), loginInfo.getAccessToken())
                .then().statusCode(200)
                .extract().jsonPath()
                .getList(".", StaffInformation.class);
    }

    public StaffInformation randomStaff() {
        var staffList = getStaffList();

        // Return a default StaffInformation if the list is empty
        if (staffList.isEmpty()) {
            return getDefaultStaffInformation();
        }

        // Find any enabled staff or return the default if none found
        return staffList.parallelStream()
                .filter(StaffInformation::isEnabled)
                .findAny()
                .orElse(getDefaultStaffInformation());
    }

    // Helper method to create a default StaffInformation
    private StaffInformation getDefaultStaffInformation() {
        return new StaffInformation(
                0, 0, "", "",
                true, loginInfo.getStore().getId(),
                null, null, null, null, null
        );
    }
}
