package api.seller.setting;

import api.seller.login.APIDashboardLogin;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import utility.APIUtils;

import java.util.List;

/**
 * This class provides methods to retrieve and process store language information from the API.
 * It uses login credentials to fetch language settings associated with a specific store.
 */
public class APIGetStoreLanguage {

    private final APIDashboardLogin.SellerInformation loginInfo;

    /**
     * Constructs an APIGetStoreLanguage instance with the given credentials.
     * Initializes login information using the provided credentials.
     *
     * @param credentials The credentials to use for authentication.
     */
    public APIGetStoreLanguage(APIDashboardLogin.Credentials credentials) {
        this.loginInfo = new APIDashboardLogin().getSellerInformation(credentials);
    }

    /**
     * Data class representing store language information.
     * Contains details such as language code, default status, published status, initial status, and language name.
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LanguageInformation {
        private String langCode;
        private Boolean isDefault;
        private boolean published;
        private boolean isInitial;
        private String langName;
    }

    /**
     * Fetches language information from the API for the store associated with the credentials.
     *
     * @return A list of LanguageInformation objects containing details about the store's languages.
     */
    public List<LanguageInformation> getStoreLanguageInformation() {
        return new APIUtils().get("/storeservice/api/store-language/store/%d/all".formatted(loginInfo.getStore().getId()), loginInfo.getAccessToken())
                .then().statusCode(200)
                .extract().jsonPath()
                .getList(".", LanguageInformation.class);
    }

    /**
     * Retrieves the default language code from a list of LanguageInformation objects.
     *
     * @param languageInfoList The list of LanguageInformation objects.
     * @return The language code of the default language, or null if no default language is found.
     */
    public static String getDefaultLanguageCode(List<LanguageInformation> languageInfoList) {
        return languageInfoList.parallelStream()
                .filter(LanguageInformation::getIsDefault)
                .findAny()
                .map(LanguageInformation::getLangCode)
                .orElse(null);
    }
}
