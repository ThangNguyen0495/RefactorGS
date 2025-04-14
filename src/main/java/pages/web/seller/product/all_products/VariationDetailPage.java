package pages.web.seller.product.all_products;

import api.seller.product.APIGetProductDetail;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import utility.PropertiesUtils;
import utility.WebUtils;

import java.util.List;
import java.util.stream.IntStream;

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
     * The unique identifier for the product model associated with this variation.
     */
    private final int modelId;

    /**
     * Logger instance for logging actions and information.
     */
    private final Logger logger = LogManager.getLogger();


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
     */
    public VariationDetailPage(WebDriver driver, int varIndex, APIGetProductDetail.ProductInformation productInfo) {
        this.driver = driver;
        this.varIndex = varIndex;
        this.productInfo = productInfo;
        this.webUtils = new WebUtils(driver);
        this.variationValue = productInfo.getModels().get(varIndex).getName();
        this.modelId = productInfo.getModels().get(varIndex).getId();
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
    private final By loc_dlgEditTranslation_btnSave = By.xpath("//*[@name=\"submit-translate\"]");
    private final By loc_chkReUseParentAttribution = By.cssSelector("[name='cbx-resue-attribute']");
    private final By loc_lnkAddAttribution = By.cssSelector(".uik-checkbox__wrapper +.gs-fake-link");
    private final By loc_icnDeleteAttribution = By.cssSelector(".attribute-item-row button");
    private final By loc_txtAttributionName = By.cssSelector("[name *='input-attribute-name']");
    private final By loc_txtAttributionValue = By.cssSelector("[name='attribute-value']");
    private final By loc_chkDisplayAttribution = By.cssSelector("td input.uik-checkbox__checkbox");

    /**
     * Navigates to the Variation Detail page for the current product variation.
     */
    public VariationDetailPage navigateToVariationDetailPage() {
        String url = "%s/product/%s/variation-detail/%s/edit".formatted(PropertiesUtils.getDomain(), productInfo.getId(), APIGetProductDetail.getVariationModelId(productInfo, varIndex));
        driver.get(url);
        logger.info("Navigating to the variation detail page for variation: {}.", variationValue);

        return this;
    }

    /**
     * Updates the product version name.
     */
    private void updateVariationProductName(String defaultLanguage) {
        String name = APIGetProductDetail.getVersionName(productInfo, modelId, defaultLanguage);
        webUtils.click(loc_txtProductVersionName);
        webUtils.sendKeys(loc_txtProductVersionName, name);
        logger.info("[{}] Updated product version name to: {}", variationValue, name);
    }

    /**
     * Updates the product description.
     */
    private void updateVariationProductDescription(String defaultLanguage) {
        // Uncheck reuse parent description
        webUtils.uncheckCheckbox(loc_chkReuse);

        // Input new version description
        String description = APIGetProductDetail.getVersionDescription(productInfo, modelId, defaultLanguage);
        webUtils.sendKeys(loc_rtfDescription, description);
        logger.info("[{}] Updated product description to: {}.", variationValue, description);
    }

    /**
     * Saves the changes made to the product variation information (name, description, and other details).
     */
    private void saveChange() {
        logger.info("Saving changes for variation: {}.", variationValue);

        // Click the save button to confirm changes
        webUtils.click(loc_btnSave);

        // Log the successful save of the variation changes
        logger.info("[{}] Variation information changes saved successfully.", variationValue);
    }

    /**
     * Updates the translation for the product variation in the specified language.
     *
     * @param languageCode The language code for the translation.
     * @param languageName The name of the language.
     */
    private void updateVariationTranslation(String languageCode, String languageName) {
        String variation = APIGetProductDetail.getVariationValue(productInfo, languageCode, varIndex);

        logger.info("[{}] Updating translation for language: {} ({}).", variation, languageName, languageCode);

        // Handle special case for English when the current language is Vietnamese
        if (languageCode.equals("en") && webUtils.getLocalStorageValue("langKey").equals("vi")) {
            languageName = "Tiếng Anh";
            logger.info("[{}] Adjusted language name to Vietnamese: {}.", variation, languageName);
        }

        // If the current language in the dropdown is not the target language, switch it
        if (!webUtils.getText(loc_dlgEditTranslation_selectedLanguage).equals(languageName)) {
            logger.info("[{}] Changing selected language from {} to {}.", variation,
                    webUtils.getText(loc_dlgEditTranslation_selectedLanguage), languageName);
            webUtils.click(loc_dlgEditTranslation_selectedLanguage);
            webUtils.click(loc_dlgEditTranslation_languageInDropdown(languageName));
        }

        logger.info("[{}] Selected language for translation: {}.", variation, languageName);

        // Fetch and set the product version name
        String name = APIGetProductDetail.getVersionName(productInfo, modelId, languageCode);
        webUtils.sendKeys(loc_dlgEditTranslation_variationName, name);
        logger.info("[{}] Edited translation for product version name: {}.", variation, name);

        // Fetch and set the product description
        String description = APIGetProductDetail.getVersionDescription(productInfo, modelId, languageCode);
        webUtils.sendKeys(loc_dlgEditTranslation_variationDescription, description);
        logger.info("[{}] Edited translation for product description: {}.", variation, description);

        // Save the updated translation
        webUtils.click(loc_dlgEditTranslation_btnSave);
        logger.info("[{}] Translation updated successfully.", variation);
    }

    /**
     * Updates the product version name, description, and translations in all languages.
     */
    public void updateVariationProductNameAndDescription(List<String> untranslatedLanguageCodes, List<String> untranslatedLanguageNames, String defaultLanguage) {
        logger.info("Starting update of variation product name and description.");

        // Update the variation product name and description
        logger.info("Updating product name and description for the default language: {}.", defaultLanguage);
        updateVariationProductName(defaultLanguage);
        updateVariationProductDescription(defaultLanguage);

        // Complete the process of updating the product's version name and description
        logger.info("Completing update for product version name and description.");
        saveChange();

        // Open the translation editing dialog
        logger.info("Opening the translation editing dialog.");
        WebUtils.retryUntil(5, 1000,
                "Cannot open variation translation popup after 5 retries",
                () -> !webUtils.getListElement(loc_dlgEditTranslation).isEmpty(),
                () -> {
                    webUtils.click(loc_btnEditTranslation);
                    return null;
                }
        );

        // For each remaining language, update the translation for the variation
        untranslatedLanguageCodes.forEach(languageCode -> {
            String languageName = untranslatedLanguageNames.get(untranslatedLanguageCodes.indexOf(languageCode));
            logger.info("Updating translation for language code: {}, language name: {}.", languageCode, languageName);
            updateVariationTranslation(languageCode, languageName);
        });

        logger.info("Completed update of variation product name and description, including translations.");
    }

    /**
     * Changes the status of the variation.
     */
    public void changeVariationStatus() {
        logger.info("Starting status change for variation: {}.", variationValue);

        // Change variation status
        logger.info("Clicking the button to change variation status.");
        webUtils.clickJS(loc_btnDeactivate);

        // Log status change
        String newStatus = productInfo.getModels().get(varIndex).getStatus();
        logger.info("Status for variation [{}] updated to: {}.", variationValue, newStatus);

        logger.info("Completed status change for variation: {}.", variationValue);
    }

    /**
     * Updates the attributions for the product variation.
     */
    public void updateVariationAttribution() {
        logger.info("[{}] Starting update of variation attribution.", variationValue);

        // Uncheck the "Reuse Parent Attribution" checkbox to allow for custom attributions
        logger.info("[{}] Unchecking 'Reuse Parent Attribution' checkbox.", variationValue);
        webUtils.uncheckCheckbox(loc_chkReUseParentAttribution);

        // Remove any existing attributions before adding new ones
        if (!productInfo.getItemAttributes().isEmpty()) {
            int bound = webUtils.getListElement(loc_icnDeleteAttribution).size();
            logger.info("[{}] Found {} existing attributions to delete.", variationValue, bound);

            // Iterate through the existing attributions in reverse order and delete them
            IntStream.range(0, bound).forEach(index -> {
                logger.info("[{}] Deleting attribution at index {}.", variationValue, bound - index - 1);
                webUtils.clickJS(loc_icnDeleteAttribution, bound - index - 1);
            });
        } else {
            logger.info("[{}] No existing attributions found.", variationValue);
        }

        // Retrieve model attributes for the specified variation index
        var modelAttribute = productInfo.getModels().get(varIndex).getModelAttributes();
        logger.info("[{}] Retrieved {} model attributes.", variationValue, modelAttribute.size());

        // For each model attribute, click to add a new attribution entry
        IntStream.range(0, modelAttribute.size()).forEachOrdered(ignored -> {
            logger.info("[{}] Adding new attribution entry.", variationValue);
            webUtils.clickJS(loc_lnkAddAttribution);
        });

        // Populate the attribution fields with names, values, and display settings
        IntStream.range(0, modelAttribute.size()).forEach(attIndex -> {
            logger.info("[{}] Updating attribution field for attribute index {}.", variationValue, attIndex);

            // Send the attribute name to the corresponding input field
            logger.info("[{}] Setting attribution name: {}.", variationValue, modelAttribute.get(attIndex).getAttributeName());
            webUtils.sendKeys(loc_txtAttributionName, attIndex, modelAttribute.get(attIndex).getAttributeName());

            // Send the attribute value to the corresponding input field
            logger.info("[{}] Setting attribution value: {}.", variationValue, modelAttribute.get(attIndex).getAttributeValue());
            webUtils.sendKeys(loc_txtAttributionValue, attIndex, modelAttribute.get(attIndex).getAttributeValue());

            // Check if the display setting matches the model attribute and update it accordingly
            boolean isDisplay = modelAttribute.get(attIndex).getIsDisplay();
            if (isDisplay) {
                webUtils.checkCheckbox(loc_chkDisplayAttribution, attIndex);
            } else {
                webUtils.uncheckCheckbox(loc_chkDisplayAttribution, attIndex);
            }
            logger.info("[{}] Updated display setting for attribute index {}: {}.", variationValue, attIndex, isDisplay);
        });

        // Save attribution changes
        logger.info("[{}] Saving attribution changes.", variationValue);
        saveChange();

        logger.info("[{}] Completed updating variation attribution.", variationValue);
    }
}
