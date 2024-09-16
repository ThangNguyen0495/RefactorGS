package api.seller.setting;

import api.seller.login.APIDashboardLogin;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import utility.APIUtils;

import java.util.List;
import java.util.function.Function;

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
     * Retrieves a list of specific language information (codes or names) from a list of {@link LanguageInformation}.
     *
     * @param languageInfoList The list of language information from which to extract data.
     * @param extractor        A function that extracts the required field from {@link LanguageInformation}.
     * @return A list of extracted language information (e.g., codes or names).
     */
    public static List<String> getLanguageInfo(List<LanguageInformation> languageInfoList, Function<LanguageInformation, String> extractor) {
        return languageInfoList.stream()
                .map(extractor)
                .toList();
    }

    /**
     * Retrieves a list of all store language codes from a list of {@link LanguageInformation}.
     *
     * @param languageInfoList The list of language information.
     * @return A list of language codes.
     */
    public static List<String> getAllStoreLanguageCodes(List<LanguageInformation> languageInfoList) {
        return getLanguageInfo(languageInfoList, LanguageInformation::getLangCode);
    }

    /**
     * Retrieves a list of all store language names from a list of {@link LanguageInformation}.
     *
     * @param languageInfoList The list of language information.
     * @return A list of language names.
     */
    public static List<String> getAllStoreLanguageNames(List<LanguageInformation> languageInfoList) {
        return getLanguageInfo(languageInfoList, LanguageInformation::getLangName);
    }
}
