package api.seller.setting;

import api.seller.login.APIDashboardLogin;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import utility.APIUtils;

import java.util.List;

/**
 * This class provides methods to fetch and process VAT information from the API.
 * It uses login credentials to retrieve VAT settings associated with a specific store.
 */
public class APIGetVATList {

    private final APIDashboardLogin.SellerInformation loginInfo;

    /**
     * Constructs an APIGetVATList instance with the given credentials.
     * Initializes login information using the provided credentials.
     *
     * @param credentials The credentials to use for authentication.
     */
    public APIGetVATList(APIDashboardLogin.Credentials credentials) {
        loginInfo = new APIDashboardLogin().getSellerInformation(credentials);
    }

    /**
     * Data class representing VAT information.
     * Contains details such as ID, name, tax rate, and description.
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VATInformation {
        private int id;
        private String name;
        private Boolean useDefault;
        private String taxType;
        private Double rate;
        private String description;
    }

    /**
     * Fetches VAT information from the API for the store associated with the credentials.
     *
     * @return A list of VATInformation objects containing details about VAT settings.
     */
    public List<VATInformation> getVATInformation() {
        return new APIUtils().get("/storeservice/api/tax-settings/store/%d".formatted(loginInfo.getStore().getId()), loginInfo.getAccessToken())
                .then().statusCode(200)
                .extract().jsonPath()
                .getList(".", VATInformation.class);
    }

    /**
     * Extracts VAT IDs from a list of VATInformation objects.
     *
     * @param vatInfoList The list of VATInformation objects.
     * @return A list of VAT IDs extracted from the VATInformation objects.
     */
    public static List<Integer> getVATIds(List<VATInformation> vatInfoList) {
        return vatInfoList.stream().map(VATInformation::getId).toList();
    }
}
