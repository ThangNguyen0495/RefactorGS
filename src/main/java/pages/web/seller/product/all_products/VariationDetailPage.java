package pages.web.seller.product.all_products;

import api.seller.login.APISellerLogin;
import api.seller.product.APIGetProductDetail;
import api.seller.setting.APIGetStoreDefaultLanguage;
import api.seller.setting.APIGetStoreLanguage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import utility.PropertiesUtils;
import utility.WebUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import static org.apache.commons.lang.math.RandomUtils.nextBoolean;
import static org.apache.commons.lang.math.RandomUtils.nextInt;

/**
 * Page Object Model class for managing the Variation Detail page.
 */
public class VariationDetailPage {

    /**
     * The WebDriver instance used for browser interactions.
     */
    private final WebDriver driver;

    /**
     * Utility class for common web operations.
     */
    private final WebUtils webUtils;

    /**
     * The index of the variation to be managed.
     */
    private final int varIndex;

    /**
     * The variation value for the product.
     */
    private final String variationValue;

    /**
     * The default language code for the seller's store.
     */
    private final String defaultLanguage;

    /**
     * Logger instance for logging actions and information.
     */
    private final Logger logger = LogManager.getLogger();

    /**
     * List of language information for the store, used to handle translations.
     */
    private final List<APIGetStoreLanguage.LanguageInformation> languageInfoList;

    /**
     * Product information associated with the variation.
     */
    private final APIGetProductDetail.ProductInformation productInfo;

    /**
     * Constructs an instance of VariationDetailPage.
     *
     * @param driver      The WebDriver instance used for browser interactions.
     * @param varIndex    The index of the variation to be managed.
     * @param productInfo The product information associated with the variation.
     * @param credentials The credentials for API login.
     */
    public VariationDetailPage(WebDriver driver, int varIndex, APIGetProductDetail.ProductInformation productInfo, APISellerLogin.Credentials credentials) {
        this.driver = driver;
        this.varIndex = varIndex;
        this.productInfo = productInfo;
        this.webUtils = new WebUtils(driver);
        this.defaultLanguage = new APIGetStoreDefaultLanguage(credentials).getDefaultLanguage();
        this.languageInfoList = new APIGetStoreLanguage(credentials).getStoreLanguageInformation();
        this.variationValue = APIGetProductDetail.getVariationValue(productInfo, defaultLanguage, varIndex);
    }

    // Locators
    private final By loc_txtProductVersionName = By.cssSelector("#variationName");
    private final By loc_chkReuse = By.cssSelector(".des-header > div > label > input");
    private final By loc_rtfDescription = By.cssSelector(".fr-element");
    private final By loc_btnSave = By.xpath("(//*[text() = 'Lưu' or text() = 'Save'])[1]/parent::div/parent::button");
    private final By loc_btnDeactivate = By.xpath("(//*[text() = 'Ngừng bán' or text() = 'Deactivate' or text() = 'Bán ngay' or text() = 'Activate'])[1]/parent::div/parent::button");
    private final By loc_btnEditTranslation = By.xpath("(//*[text() = 'Sửa bản dịch' or text() = 'Edit Translation'])[1]/parent::div/parent::button");
    private final By loc_dlgEditTranslation = By.cssSelector(".modal-content");
    private final By loc_dlgEditTranslation_selectedLanguage = By.cssSelector(".uik-select__valueRenderedWrapper .text-truncate");

    private By loc_dlgEditTranslation_languageInDropdown(String languageName) {
        return By.xpath("//*[@class = 'uik-select__label']//*[text()='%s']".formatted(languageName));
    }

    private final By loc_dlgEditTranslation_variationName = By.cssSelector("#informationName");
    private final By loc_dlgEditTranslation_variationDescription = By.cssSelector(".modal-body .fr-element");
    private final By loc_dlgEditTranslation_btnSave = By.cssSelector(".modal-footer .gs-button__green");
    private final By loc_chkReUseParentAttribution = By.cssSelector("[name='cbx-resue-attribute']");
    private final By loc_lnkAddAttribution = By.cssSelector(".uik-checkbox__wrapper +.gs-fake-link");
    private final By loc_icnDeleteAttribution = By.cssSelector(".attribute-item-row button");
    private final By loc_txtAttributionName = By.cssSelector("[name *='input-attribute-name']");
    private final By loc_txtAttributionValue = By.cssSelector("[name='attribute-value']");
    private final By loc_chkDisplayAttribution = By.cssSelector("td input.uik-checkbox__checkbox");

    /**
     * Navigates to the Variation Detail page for the current product variation.
     */
    private void navigateToVariationDetailPage() {
        String url = "%s/product/%s/variation-detail/%s/edit".formatted(PropertiesUtils.getDomain(), productInfo.getId(), APIGetProductDetail.getVariationModelId(productInfo, varIndex));
        driver.get(url);
    }

    /**
     * Updates the product version name.
     */
    private void updateVariationProductName() {
        String name = "[Update][%s][%s] product version name".formatted(defaultLanguage, variationValue);
        webUtils.click(loc_txtProductVersionName);
        webUtils.sendKeys(loc_txtProductVersionName, name);
        logger.info("[{}] Updated product version name to: {}", variationValue, name);
    }

    /**
     * Updates the product description.
     */
    private void updateVariationProductDescription() {
        boolean reuseDescription = nextBoolean();
        if (webUtils.isCheckedJS(loc_chkReuse) != reuseDescription) {
            webUtils.clickJS(loc_chkReuse);
        }

        if (!reuseDescription) {
            String description = "[Update][%s][%s] Product description".formatted(defaultLanguage, variationValue);
            webUtils.sendKeys(loc_rtfDescription, description);
            logger.info("[{}] Updated product description to: {}.", variationValue, description);
        }
    }

    /**
     * Saves the changes made to the product version name and description.
     */
    private void completeUpdateProductVersionNameAndDescription() {
        webUtils.click(loc_btnSave);
        logger.info("[{}] Update successfully.", variationValue);
    }

    /**
     * Updates the translation for the product variation in the specified language.
     *
     * @param languageCode The language code for the translation.
     * @param languageName The name of the language.
     */
    private void updateVariationTranslation(String languageCode, String languageName) {
        String variation = APIGetProductDetail.getVariationValue(productInfo, languageCode, varIndex);
        if (!webUtils.getListElement(loc_dlgEditTranslation).isEmpty()) {
            if (languageCode.equals("en") && webUtils.getLocalStorageValue("langKey").equals("vi")) {
                languageName = "Tiếng Anh";
            }

            if (!webUtils.getText(loc_dlgEditTranslation_selectedLanguage).equals(languageName)) {
                webUtils.click(loc_dlgEditTranslation_selectedLanguage);
                webUtils.click(loc_dlgEditTranslation_languageInDropdown(languageName));
            }
            logger.info("[{}] Selected language for translation: {}.", variation, languageName);

            String name = "[Update][%s][%s] Product version name".formatted(languageCode, variation);
            webUtils.sendKeys(loc_dlgEditTranslation_variationName, name);
            logger.info("[{}] Edited translation for product version name: {}.", variation, name);

            String description = "[Update][%s][%s] Product description".formatted(languageCode, variation);
            webUtils.sendKeys(loc_dlgEditTranslation_variationDescription, description);
            logger.info("[{}] Edited translation for product description: {}.", variation, description);

            webUtils.click(loc_dlgEditTranslation_btnSave);
            logger.info("[{}] Translation updated successfully.", variation);
        }
    }

    /**
     * Updates the product version name, description, and translations in all languages.
     */
    public void updateVariationProductNameAndDescription() {
        navigateToVariationDetailPage();
        updateVariationProductName();
        updateVariationProductDescription();

        List<String> langCodeList = new ArrayList<>(APIGetStoreLanguage.getAllStoreLanguageCodes(languageInfoList));
        List<String> langNameList = new ArrayList<>(APIGetStoreLanguage.getAllStoreLanguageNames(languageInfoList));
        langCodeList.remove(defaultLanguage);

        webUtils.click(loc_btnEditTranslation);
        Assert.assertFalse(webUtils.getListElement(loc_dlgEditTranslation).isEmpty(), "Cannot open edit translation popup.");

        langCodeList.forEach(languageCode -> updateVariationTranslation(languageCode, langNameList.get(langCodeList.indexOf(languageCode))));

        completeUpdateProductVersionNameAndDescription();
    }

    /**
     * Changes the status of the variation.
     *
     * @param status The new status to set (e.g., "ACTIVE", "DEACTIVATED").
     */
    public void changeVariationStatus(String status) {
        navigateToVariationDetailPage();
        if (APIGetProductDetail.getVariationStatus(productInfo, varIndex).equals(status)) {
            webUtils.clickJS(loc_btnDeactivate);
        }
        logger.info("[{}] Status updated to: {}.", variationValue, status);
    }

    /**
     * Updates the attributions for the product variation.
     */
    public void updateAttribution() {
        navigateToVariationDetailPage();

        boolean isUseParentAttribution = nextBoolean();
        if (isUseParentAttribution) {
            webUtils.checkCheckbox(loc_chkReUseParentAttribution);
        } else {
            webUtils.uncheckCheckbox(loc_chkReUseParentAttribution);
        }

        if (!isUseParentAttribution) {
            // Remove old attributions
            if (!webUtils.getListElement(loc_icnDeleteAttribution).isEmpty()) {
                int bound = webUtils.getListElement(loc_icnDeleteAttribution).size();
                IntStream.range(0, bound).forEach(index -> webUtils.clickJS(loc_icnDeleteAttribution, bound - index - 1));
            }

            int numOfAttribute = nextInt(10);
            IntStream.range(0, numOfAttribute).forEachOrdered(ignored -> webUtils.clickJS(loc_lnkAddAttribution));

            long epoch = Instant.now().toEpochMilli();
            IntStream.range(0, numOfAttribute).forEach(attIndex -> {
                webUtils.sendKeys(loc_txtAttributionName, attIndex, "name_%s_%s".formatted(attIndex, epoch));
                webUtils.sendKeys(loc_txtAttributionValue, attIndex, "value_%s_%s".formatted(attIndex, epoch));
                if (!Objects.equals(webUtils.isCheckedJS(loc_chkDisplayAttribution, attIndex), nextBoolean())) {
                    webUtils.clickJS(loc_chkDisplayAttribution, attIndex);
                }
            });

            webUtils.click(loc_btnSave);
        }
    }
}
