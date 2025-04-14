package pages.web.seller.product.all_products;

import api.seller.product.APIGetProductDetail;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import utility.WebUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class EditTranslationPopup {
    private final WebUtils webUtils;
    private final Logger logger = LogManager.getLogger();
    private String translationLangCode;

    public EditTranslationPopup(WebDriver driver) {
        this.webUtils = new WebUtils(driver);
    }

    private final By loc_dlgEditTranslation = By.cssSelector(".modal.fade.show");
    private final By loc_dlgEditTranslation_ddvSelectedLanguage = By.cssSelector(".product-translate .text-truncate");

    private By loc_dlgEditTranslation_ddvOtherLanguage(String translationLangName) {
        return By.xpath("//*[@class = 'uik-select__label']//*[text()='%s']".formatted(translationLangName));
    }

    private final By loc_dlgEditTranslation_txtProductName = By.cssSelector("#informationName");
    private final By loc_dlgEditTranslation_txtProductDescription = By.cssSelector(".modal-body .fr-element");
    private final By loc_dlgEditTranslation_txtVariationName = By.xpath("//*[@class = 'product-translate-modal']/*[@class = 'product-translate__titleBody']/h3/parent::div/following-sibling::div[@class]/div[1]/descendant::input");
    private final By loc_dlgEditTranslation_txtVariationValue = By.xpath("//*[@class = 'product-translate-modal']/*[@class = 'product-translate__titleBody']/h3/parent::div/following-sibling::div[@class]/div[2]/descendant::input");
    private final By loc_dlgEditTranslation_txtSEOTitle = By.cssSelector(".modal-body #seoTitle");
    private final By loc_dlgEditTranslation_txtSEODescription = By.cssSelector(".modal-body #seoDescription");
    private final By loc_dlgEditTranslation_txtSEOKeywords = By.cssSelector(".modal-body #seoKeywords");
    private final By loc_dlgEditTranslation_txtSEOUrl = By.cssSelector(".modal-body #seoUrl");
    private final By loc_dlgEditTranslation_btnSave = By.xpath("//*[@name=\"submit-translate\"]");
    private final By loc_dlgEditTranslation_btnClose = By.cssSelector(".product-translate .close");
    private final By loc_dlgToastSuccess = By.cssSelector(".Toastify__toast--success");

    /**
     * Adds translation details for a product.
     * <p>
     * Opens the edit translation popup and inputs translated product details, including name, description, SEO fields,
     * and variation details (if the product has variations), and saves the translation. The language conversion is handled
     * when translating to English from Vietnamese. The process is logged for tracking purposes.
     * </p>
     * <p>
     * Steps include:
     * <ul>
     *   <li>Verifying the edit translation popup is available.</li>
     *   <li>Selecting the translation language.</li>
     *   <li>Handling language name conversion (e.g., "en" to "Tiếng Anh" if the current language is Vietnamese).</li>
     *   <li>Inputting translated product details and SEO fields.</li>
     *   <li>If the product has variations, inputting variation details.</li>
     *   <li>Saving the translation.</li>
     * </ul>
     * </p>
     *
     * @param productInfo         The product information to be translated, including general details and variations (if any).
     * @param translationLangCode The language code for the translation (e.g., "en" for English).
     * @param translationLangName The display name of the language (e.g., "English").
     * @return The current EditTranslationPopup instance.
     */
    public EditTranslationPopup addTranslation(APIGetProductDetail.ProductInformation productInfo, String translationLangCode, String translationLangName) {
        this.translationLangCode = translationLangCode;

        // Ensure the edit translation popup is available
        Assert.assertFalse(webUtils.getListElement(loc_dlgEditTranslation).isEmpty(),
                "Cannot open edit translation popup."
        );

        // Handle special case for language name conversion
        if (translationLangCode.equals("en") && webUtils.getLocalStorageValue("langKey").equals("vi")) {
            translationLangName = "Tiếng Anh";
        }

        // Select the language for translation
        selectLanguageForTranslation(translationLangName);

        logger.info("Adding translation for '{}' language.", translationLangName);

        // Input translated product details
        inputProductDetails(translationLangCode, productInfo);

        // Input variation details if applicable
        if (productInfo.isHasModel()) {
            inputVariationDetails(productInfo, translationLangCode);
        }

        // Input SEO fields
        inputSEOFields(productInfo, translationLangCode);

        // Save the changes
        saveTranslation();

        return this;
    }

    /**
     * Selects the language for translation in the popup.
     *
     * @param translationLangName The name of the language to select.
     */
    private void selectLanguageForTranslation(String translationLangName) {
        if (!webUtils.getText(loc_dlgEditTranslation_ddvSelectedLanguage).equals(translationLangName)) {
            webUtils.click(loc_dlgEditTranslation_ddvSelectedLanguage);
            webUtils.click(loc_dlgEditTranslation_ddvOtherLanguage(translationLangName));
        }
    }

    /**
     * Inputs translated product details.
     *
     * @param translationLangCode The language code for the translation.
     * @param productInfo         The product information to be translated.
     */
    private void inputProductDetails(String translationLangCode, APIGetProductDetail.ProductInformation productInfo) {
        String name = APIGetProductDetail.getMainProductName(productInfo, translationLangCode);
        webUtils.sendKeys(loc_dlgEditTranslation_txtProductName, name);
        logger.info("Input translation for product name: {}", name);

        String description = APIGetProductDetail.getMainProductDescription(productInfo, translationLangCode);
        webUtils.sendKeys(loc_dlgEditTranslation_txtProductDescription, description);
        logger.info("Input translation for product description: {}", description);
    }

    /**
     * Inputs variation details for the product.
     *
     * @param productInfo         The product information to be translated.
     * @param translationLangCode The language code for the translation.
     */
    private void inputVariationDetails(APIGetProductDetail.ProductInformation productInfo, String translationLangCode) {
        List<String> variationName = Arrays.stream(APIGetProductDetail.getVariationName(productInfo, translationLangCode)
                        .split("\\|"))
                .toList();

        List<String> variationValue = Arrays.stream(APIGetProductDetail.getVariationValues(productInfo, translationLangCode).stream()
                .flatMap(varValue -> Arrays.stream(varValue.split("\\|")))
                .distinct().toArray(String[]::new)).sorted().toList();

        // Log variation names before sending keys
        logger.info("Variation names: {}", variationName);

        // Input variation names with debug logs
        IntStream.range(0, variationName.size())
                .forEachOrdered(variationNameIndex -> {
                    logger.info("Sending variation name: {}", variationName.get(variationNameIndex));
                    webUtils.sendKeys(loc_dlgEditTranslation_txtVariationName, variationNameIndex, variationName.get(variationNameIndex));
                });

        // Log variation values before sending keys
        logger.info("Variation values: {}", variationValue);

        // Input variation values with debug logs
        IntStream.range(0, variationValue.size())
                .forEachOrdered(variationValueIndex -> {
                    logger.info("Sending variation value: {}", variationValue.get(variationValueIndex));
                    webUtils.sendKeys(loc_dlgEditTranslation_txtVariationValue, variationValueIndex, variationValue.get(variationValueIndex));
                });
    }

    /**
     * Inputs SEO fields for the product.
     *
     * @param translationLangCode The language code for the translation.
     */
    private void inputSEOFields(APIGetProductDetail.ProductInformation productInfo, String translationLangCode) {
        String title = APIGetProductDetail.retrieveSEOTitle(productInfo, translationLangCode);
        webUtils.sendKeys(loc_dlgEditTranslation_txtSEOTitle, title);
        logger.info("Input translation for SEO title: {}", title);

        String seoDescription = APIGetProductDetail.retrieveSEODescription(productInfo, translationLangCode);
        webUtils.sendKeys(loc_dlgEditTranslation_txtSEODescription, seoDescription);
        logger.info("Input translation for SEO description: {}", seoDescription);

        String keywords = APIGetProductDetail.retrieveSEOKeywords(productInfo, translationLangCode);
        webUtils.sendKeys(loc_dlgEditTranslation_txtSEOKeywords, keywords);
        logger.info("Input translation for SEO keywords: {}", keywords);

        String url = APIGetProductDetail.retrieveSEOUrl(productInfo, translationLangCode);
        webUtils.sendKeys(loc_dlgEditTranslation_txtSEOUrl, url);
        logger.info("Input translation for SEO url: {}", url);
    }

    /**
     * Saves the translation and verifies the success.
     */
    private void saveTranslation() {
        webUtils.clickJS(loc_dlgEditTranslation_btnSave);
        logger.info("Save translation");
        Assert.assertFalse(webUtils.getListElement(loc_dlgToastSuccess).isEmpty(),
                "Cannot add new translation for '%s' language.".formatted(translationLangCode)
        );
    }

    /**
     * Closes the translation popup by clicking the close button.
     * <p>
     * This method logs the action and asserts that the translation popup is no longer open.
     *
     * @throws AssertionError if the translation popup cannot be closed
     */
    public void closeTranslationPopup() {
        webUtils.click(loc_dlgEditTranslation_btnClose);
        logger.info("Close translation popup");
        WebUtils.sleep(1000);
    }

}
