package api.others;

import api.seller.login.APISellerLogin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import utility.APIUtils;

import java.util.List;

import static org.apache.commons.lang.math.RandomUtils.nextInt;

public class APIGetDistricts {
    private final APISellerLogin.LoginInformation loginInfo;

    /**
     * Constructs an APIGetCities object with the provided credentials to retrieve city data.
     *
     * @param credentials The credentials for the seller account to be used for API authentication.
     */
    public APIGetDistricts(APISellerLogin.Credentials credentials) {
        loginInfo = new APISellerLogin().getSellerInformation(credentials);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class District {
        private int id;
        private String code;
        private String inCountry;
        private String outCountry;
        private String zone;
    }

    public List<District> getDistricts(String provinceCode) {
        if (provinceCode.isEmpty()) return List.of();
        String path = "/catalogservices/api/city/%s/districts".formatted(provinceCode);
        return new APIUtils().get(path, loginInfo.getAccessToken())
                .then().statusCode(200)
                .extract().jsonPath()
                .getList(".", District.class);
    }

    public District randomDistrict(String provinceCode) {
        var districts = getDistricts(provinceCode);
        if (districts.isEmpty()) return new District(0, "", "", "", "");
        return districts.get(nextInt(districts.size()));
    }
}
