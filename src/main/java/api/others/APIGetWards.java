package api.others;

import api.seller.login.APISellerLogin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import utility.APIUtils;

import java.util.List;

import static org.apache.commons.lang.math.RandomUtils.nextInt;

public class APIGetWards {
    private final APISellerLogin.LoginInformation loginInfo;

    /**
     * Constructs an APIGetCities object with the provided credentials to retrieve city data.
     *
     * @param credentials The credentials for the seller account to be used for API authentication.
     */
    public APIGetWards(APISellerLogin.Credentials credentials) {
        loginInfo = new APISellerLogin().getSellerInformation(credentials);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Ward {
        private int id;
        private String code;
        private String inCountry;
        private String outCountry;
        private int districtId;
    }

    public List<Ward> getWards(String districtCode) {
        if (districtCode.isEmpty()) return List.of();
        String path = "/catalogservices/api/district/%s/wards".formatted(districtCode);
        return new APIUtils().get(path, loginInfo.getAccessToken())
                .then().statusCode(200)
                .extract().jsonPath()
                .getList(".", Ward.class);
    }

    public Ward randomWard(String districtCode) {
        var districts = getWards(districtCode);
        if (districts.isEmpty()) return new Ward(0, "", "", "", 0);
        return districts.get(nextInt(districts.size()));
    }
}
