package api.seller.supplier;

import api.seller.login.APISellerLogin;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import utility.APIUtils;

public class APIGetSupplierDetail {
    APISellerLogin.LoginInformation loginInfo;
    APISellerLogin.Credentials credentials;

    public APIGetSupplierDetail(APISellerLogin.Credentials credentials) {
        this.credentials = credentials;
        loginInfo = new APISellerLogin().getSellerInformation(credentials);
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SupplierInformation {
        private Integer id;
        private String name;
        private String code;
        private String phoneNumber;
        private String email;
        private String address;
        private String province;
        private String district;
        private String ward;
        private int storeId;
        private String status;
        private String address2;
        private String countryCode;
        private String phoneCode;
        private String cityName;
        private String zipCode;
        private boolean codeGenerated;
        private String responsibleStaff;
        private String description;
        private long totalBalance;
        private String debtPaymentStatus;
        private String responsibleStaffName;
    }


    public SupplierInformation getSupplierInformation(int supplierId) {
        String path = "/itemservice/api/suppliers/%d".formatted(supplierId);

        return new APIUtils().get(path, loginInfo.getAccessToken())
                .then().statusCode(200)
                .extract().as(SupplierInformation.class);
    }
}
