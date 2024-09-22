package api.seller.setting;

import api.seller.login.APISellerLogin;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import utility.APIUtils;

import java.util.List;

/**
 * This class provides methods to fetch and process VAT information from the API.
 * It uses login credentials to retrieve VAT settings associated with a specific store.
 */
public class APIGetVATList {

    private final APISellerLogin.LoginInformation loginInfo;

    /**
     * Constructs an APIGetVATList instance with the given credentials.
     * Initializes login information using the provided credentials.
     *
     * @param credentials The credentials to use for authentication.
     */
    public APIGetVATList(APISellerLogin.Credentials credentials) {
        loginInfo = new APISellerLogin().getSellerInformation(credentials);
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
     * Retrieves a list of VAT IDs from the provided list of VATInformation objects
     * where the tax type is "SELL".
     * <p>
     * This method filters the VATInformation list, selecting only those entries
     * with a tax type of "SELL", and then maps the filtered results to their respective IDs.
     *
     * @param vatInfoList The list of VATInformation objects to filter and map.
     * @return A list of VAT IDs for VAT entries with a "SELL" tax type.
     */
    public static List<Integer> getVATIds(List<VATInformation> vatInfoList) {
        return vatInfoList.stream()
                .filter(vatInformation -> vatInformation.getTaxType().equals("SELL"))
                .map(VATInformation::getId)
                .toList();
    }

    /**
     * Retrieves a list of VAT names from the provided list of VATInformation objects
     * where the tax type is "SELL".
     * <p>
     * This method filters the VATInformation list, selecting only those entries
     * with a tax type of "SELL", and then maps the filtered results to their respective names.
     *
     * @param vatInfoList The list of VATInformation objects to filter and map.
     * @return A list of VAT names for VAT entries with a "SELL" tax type.
     */
    public static List<String> getVATNames(List<VATInformation> vatInfoList) {
        return vatInfoList.stream()
                .filter(vatInformation -> vatInformation.getTaxType().equals("SELL"))
                .map(VATInformation::getName)
                .toList();
    }

}
