package pages.web.seller.product.all_products;

import api.seller.product.APIGetProductDetail;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import utility.WebUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class EditTranslationPopup {
    private final WebUtils webUtils;
    private final Logger logger = LogManager.getLogger();
    private final String translationLangCode;
    private String translationLangName;
    private final String defaultLangCode;

    public EditTranslationPopup(WebDriver driver, String translationLangCode, String translationLangName, String defaultLangCode) {
        this.webUtils = new WebUtils(driver);
        this.defaultLangCode = defaultLangCode;
        this.translationLangCode = translationLangCode;
        this.translationLangName = translationLangName;
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
    private final By loc_dlgEditTranslation_btnSave = By.cssSelector(".modal-footer .gs-button__green");
    private final By loc_dlgToastSuccess = By.cssSelector(".Toastify__toast--success");

    /**
     * Adds translation details for a product.
     * <p>
     * Opens the edit translation popup, inputs translated product details, including name, description, SEO fields,
     * and variation details (if any), and saves the translation. The process is logged for tracking.
     * </p>
     * @param productInfo   The product information to be translated.
     */
    public void addTranslation(APIGetProductDetail.ProductInformation productInfo) {
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
        inputSEOFields(translationLangCode);

        // Save the changes
        saveTranslation();
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
     * @param translationLangCode     The language code for the translation.
     * @param productInfo  The product information to be translated.
     */
    private void inputProductDetails(String translationLangCode, APIGetProductDetail.ProductInformation productInfo) {
        String name = "[%s] %s%s".formatted(
                translationLangCode,
                productInfo.getInventoryManageType().equals("IMEI_SERIAL_NUMBER") ? "Auto - IMEI - without variation - " : "Auto - Normal - without variation - ",
                LocalDateTime.now().toString().substring(0, 19)
        );
        webUtils.sendKeys(loc_dlgEditTranslation_txtProductName, name);
        logger.info("Input translation for product name: {}", name);

        String description = "[%s] product description".formatted(translationLangCode);
        webUtils.sendKeys(loc_dlgEditTranslation_txtProductDescription, description);
        logger.info("Input translation for product description: {}", description);
    }

    /**
     * Inputs variation details for the product.
     *
     * @param productInfo The product information to be translated.
     * @param translationLangCode    The language code for the translation.
     */
    private void inputVariationDetails(APIGetProductDetail.ProductInformation productInfo, String translationLangCode) {
        List<String> variationName = Arrays.stream(APIGetProductDetail.getVariationGroupName(productInfo, defaultLangCode)
                        .split("\\|"))
                .map(varName -> varName.replace(defaultLangCode, translationLangCode))
                .toList();

        List<String> variationValue = APIGetProductDetail.getVariationValues(productInfo, defaultLangCode).stream()
                .flatMap(varValue -> Arrays.stream(varValue.replace(defaultLangCode, translationLangCode).split("\\|")))
                .distinct()
                .toList();

        IntStream.range(0, variationName.size())
                .forEachOrdered(varIndex -> webUtils.sendKeys(loc_dlgEditTranslation_txtVariationName, varIndex, variationName.get(varIndex)));

        IntStream.range(0, variationValue.size())
                .forEachOrdered(varIndex -> webUtils.sendKeys(loc_dlgEditTranslation_txtVariationValue, varIndex, variationValue.get(varIndex)));
    }

    /**
     * Inputs SEO fields for the product.
     *
     * @param translationLangCode The language code for the translation.
     */
    private void inputSEOFields(String translationLangCode) {
        String title = "[%s] Auto - SEO Title - %s".formatted(translationLangCode, Instant.now().toEpochMilli());
        webUtils.sendKeys(loc_dlgEditTranslation_txtSEOTitle, title);
        logger.info("Input translation for SEO title: {}", title);

        String seoDescription = "[%s] Auto - SEO Description - %s".formatted(translationLangCode, Instant.now().toEpochMilli());
        webUtils.sendKeys(loc_dlgEditTranslation_txtSEODescription, seoDescription);
        logger.info("Input translation for SEO description: {}", seoDescription);

        String keywords = "[%s] Auto - SEO Keyword - %s".formatted(translationLangCode, Instant.now().toEpochMilli());
        webUtils.sendKeys(loc_dlgEditTranslation_txtSEOKeywords, keywords);
        logger.info("Input translation for SEO keywords: {}", keywords);

        String url = "%s%s".formatted(translationLangCode, Instant.now().toEpochMilli());
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
}
