package api.seller.supplier;

import api.seller.login.APISellerLogin;
import utility.APIUtils;
import utility.helper.SupplierHelper;

import static api.seller.login.APISellerLogin.Credentials;
import static api.seller.login.APISellerLogin.LoginInformation;
import static org.apache.commons.lang.math.RandomUtils.nextBoolean;

public class APICreateSupplier {
    private final LoginInformation loginInfo;
    private final Credentials credentials;
    public APICreateSupplier(Credentials credentials) {
        this.credentials = credentials;
        this.loginInfo = new APISellerLogin().getSellerInformation(credentials);
    }

    public int createThenGetSupplierId() {
        String path = "/itemservice/api/suppliers";
        var supplierInformation = SupplierHelper.generateSupplierInformation(credentials, nextBoolean());
        return new APIUtils().post(path, loginInfo.getAccessToken(), supplierInformation)
                .then().statusCode(201)
                .extract().jsonPath()
                .getInt("id");
    }
}
