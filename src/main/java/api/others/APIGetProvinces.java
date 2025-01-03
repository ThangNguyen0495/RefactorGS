package api.others;

import api.seller.login.APISellerLogin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import utility.APIUtils;

import java.util.List;

import static org.apache.commons.lang.math.RandomUtils.nextInt;

public class APIGetProvinces {
    private final APISellerLogin.LoginInformation loginInfo;

    /**
     * Constructs an APIGetCities object with the provided credentials to retrieve city data.
     *
     * @param credentials The credentials for the seller account to be used for API authentication.
     */
    public APIGetProvinces(APISellerLogin.Credentials credentials) {
        loginInfo = new APISellerLogin().getSellerInformation(credentials);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Province {
        private String code;
        private String inCountry;
        private String outCountry;
    }

    /**
     * Retrieves a list of cities for a given country code by calling the API.
     *
     * @param countryCode The country code for which the cities need to be fetched.
     * @return A list of City objects representing the cities in the specified country.
     */
    public List<Province> getProvinces(String countryCode) {
        String path = "/catalogservices/api/country/%s/cities".formatted(countryCode);
        return new APIUtils().get(path, loginInfo.getAccessToken())
                .then().statusCode(200)
                .extract().jsonPath()
                .getList(".", Province.class);
    }

    /**
     * Retrieves a random city from the list of cities in a given country.
     *
     * @param countryCode The country code from which the random city should be selected.
     * @return A random City object representing a city in the specified country.
     */
    public Province randomProvince(String countryCode) {
        var cities = getProvinces(countryCode);
        if (cities.isEmpty()) return new Province(countryCode + "-OTHER", "", "");
        return cities.get(nextInt(cities.size()));
    }
}
