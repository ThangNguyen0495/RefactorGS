package pages.web.seller.product.all_products;

import api.seller.login.APISellerLogin;
import api.seller.product.APICreateConversionUnit;
import api.seller.product.APIGetConversionUnits;
import api.seller.product.APIGetProductDetail;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import utility.helper.ProductHelper;
import utility.WebUtils;

import java.util.List;
import java.util.stream.IntStream;

import static org.apache.commons.lang.math.RandomUtils.nextInt;

/**
 * Page Object Model class for managing the Conversion Unit page.
 */
public class ConversionUnitPage {

    private final WebUtils webUtils;
    private final APISellerLogin.Credentials credentials;
    private final Logger logger = LogManager.getLogger();
    private final APIGetProductDetail.ProductInformation productInfo;
    private final List<String> variationValues;
    private final List<APIGetConversionUnits.UnitInformation> unitInfoList;

    // Locators
    private final By loc_btnSelectUnit = By.xpath("//*[text() = 'Chọn đơn vị' or text() = 'Select unit']/parent::button");
    private final By loc_btnSelectVariation = By.cssSelector(".gs-button__green--outline");
    private final By loc_btnSave = By.xpath("//*[text() = 'Lưu' or text() = 'Save']/parent::button");
    private By loc_ddvUnitResult(String unitName) {
        return By.xpath("//*[text() = '%s']".formatted(unitName));
    }
    private By loc_dlgSelectVariation_radVariation(String variationValue) {
        return By.xpath("//div[*[text() = '%s']]/input".formatted(variationValue));
    }
    private final By loc_dlgSelectVariation_btnSave = By.cssSelector(".modal-footer > .gs-button__green");
    private final By loc_lblVariationValue = By.xpath("//*[contains(@class, 'conversion-variation')]/*[@class='name-variation']");
    private final By loc_btnVariationConfigure = By.xpath("//button[*[text() = 'Thiết lập' or text() = 'Configure']]");
    private final By loc_txtUnitName = By.cssSelector("#unit-0");
    private final By loc_txtUnitQuantity = By.cssSelector("[name *= quantity]");

    /**
     * Constructs an instance of ConversionUnitPage.
     *
     * @param driver          The WebDriver instance used for browser interactions.
     * @param credentials     The credentials for API login.
     * @param productInfo     The product information associated with the conversion unit.
     * @param defaultLanguage The default language for variations.
     */
    public ConversionUnitPage(WebDriver driver, APISellerLogin.Credentials credentials, APIGetProductDetail.ProductInformation productInfo, String defaultLanguage) {
        this.credentials = credentials;
        this.productInfo = productInfo;
        this.webUtils = new WebUtils(driver);
        this.unitInfoList = new APIGetConversionUnits(credentials).getAllConversionUnits();
        this.variationValues = APIGetProductDetail.getVariationValues(productInfo, defaultLanguage);
    }

    /**
     * Adds a conversion unit configuration based on whether variations are used.
     */
    public void addConversionUnitConfiguration() {
        if (!productInfo.getInventoryManageType().equals("IMEI_SERIAL_NUMBER")) {
            if (productInfo.isHasModel()) {
                configureConversionUnitsWithVariations();
            } else {
                configureConversionUnitForWithoutVariation();
            }
        } else {
            logger.info("Skipping conversion unit configuration: Product inventory is managed by IMEI/SERIAL.");
        }
    }

    /**
     * Configures a conversion unit for a product without variations.
     */
    private void configureConversionUnitForWithoutVariation() {
        configureConversionUnit(null);
    }

    /**
     * Configures a conversion unit for a product with or without variations.
     */
    private void configureConversionUnit(Integer modelId) {
        clickSelectUnitButton();
        selectConversionUnit();

        long quantity = getQuantityForUnit(modelId);
        webUtils.sendKeys(loc_txtUnitQuantity, String.valueOf(quantity));
        logger.info("Conversion unit quantity: {}", quantity);

        clickSaveButton();
    }



    /**
     * Configures conversion units with variations.
     */
    private void configureConversionUnitsWithVariations() {
        int numberOfConversionUnits = nextInt(APIGetProductDetail.getVariationModelList(productInfo).size()) + 1;
        logger.info("Number of conversion units to configure: {}", numberOfConversionUnits);

        // Process each variation
        IntStream.range(0, numberOfConversionUnits).forEach(this::processVariation);

        clickSaveButton(); // Save the configurations
    }

    /**
     * Clicks the button to select a unit.
     */
    private void clickSelectUnitButton() {
        webUtils.click(loc_btnSelectUnit);
        logger.info("Clicked Select Unit button.");
    }

    /**
     * Selects a conversion unit from the list.
     */
    private void selectConversionUnit() {
        List<String> unitNameList = APIGetConversionUnits.getConversionUnitNames(unitInfoList);
        String unitName = unitNameList.isEmpty() ? new APICreateConversionUnit(credentials).createConversionUnitAndGetName() : unitNameList.get(nextInt(unitNameList.size()));

        webUtils.sendKeys(loc_txtUnitName, unitName);
        webUtils.click(loc_txtUnitName);
        webUtils.click(loc_ddvUnitResult(unitName));
        logger.info("Selected conversion unit: {}", unitName);
    }

    /**
     * Calculates the quantity for the conversion unit.
     *
     * @return The calculated quantity.
     */
    private long getQuantityForUnit(Integer modelId) {
        long maxBranchStock = APIGetProductDetail.getMaximumBranchStockForModel(productInfo, modelId);
        long quantity = Math.min(Math.max(maxBranchStock, 1), ProductHelper.MAX_PRICE / productInfo.getOrgPrice());
        logger.info("Calculated quantity for conversion unit: {}", quantity);
        return quantity;
    }

    /**
     * Processes and configures conversion units for each variation.
     *
     * @param varIndex The index of the current variation.
     */
    private void processVariation(int varIndex) {
        webUtils.click(loc_btnSelectVariation);
        logger.info("Opened select variation popup.");

        String variation = variationValues.get(varIndex);
        logger.info("Selecting variation: {}", variation);
        webUtils.checkCheckbox(loc_dlgSelectVariation_radVariation(variation));

        webUtils.click(loc_dlgSelectVariation_btnSave);
        logger.info("Closed Select Variation popup.");

        configureConversionUnitForVariation(varIndex);
    }

    /**
     * Configures a conversion unit for a specific variation.
     *
     * @param varIndex The index of the current variation.
     */
    private void configureConversionUnitForVariation(int varIndex) {
        String variationValue = webUtils.getText(loc_lblVariationValue);
        webUtils.clickJS(loc_btnVariationConfigure, varIndex);
        logger.info("Navigated to configure conversion unit for variation page, variation value: {}", variationValue);

        // Add conversion unit for variation
        configureConversionUnit(APIGetProductDetail.getVariationModelId(productInfo, varIndex));

        webUtils.waitURLShouldBeContains("/conversion-unit/variation/edit/");
        logger.info("Waiting for setup conversion unit page to load.");
    }

    /**
     * Clicks the save button to apply changes.
     */
    private void clickSaveButton() {
        webUtils.click(loc_btnSave);
        logger.info("Clicked Save button.");
    }
}