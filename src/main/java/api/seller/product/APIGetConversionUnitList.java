package api.seller.product;

import api.seller.login.APISellerLogin;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import utility.APIUtils;

import java.util.List;

/**
 * Handles interactions with the conversion units API.
 */
public class APIGetConversionUnitList {

    private final APISellerLogin.LoginInformation loginInfo;

    /**
     * Constructs an instance of APIGetConversionUnitList with the provided login credentials.
     *
     * @param credentials The login credentials for accessing the API.
     */
    public APIGetConversionUnitList(APISellerLogin.Credentials credentials) {
        this.loginInfo = new APISellerLogin().getSellerInformation(credentials);
    }

    /**
     * Represents the unit information returned by the API.
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UnitInformation {
        private int id;
        private String name;
    }

    /**
     * Retrieves all conversion units from the API.
     *
     * @return A list of {@link UnitInformation} objects representing the conversion units.
     */
    public List<UnitInformation> getAllConversionUnits() {
        String body = """
                {
                    "lstItemId": [],
                    "key": null
                }""";
        return new APIUtils().post("/itemservice/api/item/conversion-units/search?page=0&size=10", loginInfo.getAccessToken(), body)
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList("content", UnitInformation.class);
    }

    /**
     * Extracts the names of the conversion units from the provided list.
     *
     * @param unitInfoList A list of {@link UnitInformation} objects.
     * @return A list of conversion unit names.
     */
    public static List<String> getConversionUnitNames(List<UnitInformation> unitInfoList) {
        return unitInfoList.stream()
                .map(UnitInformation::getName)
                .toList();
    }

    /**
     * Extracts the IDs of the conversion units from the provided list.
     *
     * @param unitInfoList A list of {@link UnitInformation} objects.
     * @return A list of conversion unit IDs.
     */
    public static List<Integer> getConversionUnitIds(List<UnitInformation> unitInfoList) {
        return unitInfoList.stream()
                .map(UnitInformation::getId)
                .toList();
    }
}
