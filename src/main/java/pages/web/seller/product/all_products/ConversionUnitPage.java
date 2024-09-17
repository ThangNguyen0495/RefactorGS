package pages.web.seller.product.all_products;

import api.seller.login.APIDashboardLogin;
import api.seller.product.APICreateConversionUnit;
import api.seller.product.APIGetConversionUnitList;
import api.seller.product.APIGetProductDetail;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import utility.WebUtils;

import java.util.Collections;
import java.util.List;

import static org.apache.commons.lang.math.RandomUtils.nextInt;

/**
 * Page Object Model class for managing the Conversion Unit page.
 */
public class ConversionUnitPage {

    private static final long MAX_PRICE = 99999999999L;
    private final WebUtils webUtils;
    private final APIDashboardLogin.Credentials credentials;
    private final Logger logger = LogManager.getLogger();
    private final APIGetProductDetail.ProductInformation productInfo;
    List<String> variationList;
    private final List<APIGetConversionUnitList.UnitInformation> unitInfoList;

    /**
     * Constructs an instance of ConversionUnitPage.
     *
     * @param driver      The WebDriver instance used for browser interactions.
     * @param credentials The credentials for API login.
     * @param productInfo The product information associated with the conversion unit.
     */
    public ConversionUnitPage(WebDriver driver, APIDashboardLogin.Credentials credentials, APIGetProductDetail.ProductInformation productInfo) {
        this.credentials = credentials;
        this.productInfo = productInfo;
        this.webUtils = new WebUtils(driver);
        this.unitInfoList = new APIGetConversionUnitList(credentials).getAllConversionUnits();
    }

    // Locators
    private final By withoutVariationSelectUnitBtn = By.cssSelector(".gs-content-header-right-el .gs-button__green--outline");
    private final By withoutVariationSaveBtn = By.cssSelector(".gs-content-header-right-el .gs-button__green");
    private final By withoutVariationUnitTextBox = By.cssSelector("#unit-0");

    private By unitLocator(String unitName) {
        return By.xpath("//*[text() = '%s']".formatted(unitName));
    }

    private final By withoutVariationQuantity = By.cssSelector("[name *= quantity]");
    private final By selectVariationBtn = By.cssSelector(".gs-button__green--outline");
    private final By variationSaveBtn = By.cssSelector(".gs-button__green");

    private By variationLocator(String variationValue) {
        return By.xpath("//div[* = '%s'][@class = 'variation-name']/div/input".formatted(variationValue));
    }

    private final By saveBtnOnSelectVariationPopup = By.cssSelector(".modal-footer > .gs-button__green");
    private final By variationConfigureBtn = By.cssSelector(".conversion-configure > .gs-button__blue--outline");
    private final By selectUnitBtnOnSetupVariationConversionUnitPage = By.cssSelector(".btn-header-wrapper > .gs-button__green--outline");
    private final By saveBtnOnSetupVariationConversionUnitPage = By.cssSelector(".btn-header-wrapper > .gs-button__green");
    private final By unitTextBoxOnSetupVariationConversionUnitPage = By.cssSelector("#unit-0");
    private final By quantityOnSetupVariationConversionUnitPage = By.cssSelector("[name *= quantity]");

    /**
     * Adds a conversion unit configuration without variation.
     */
    public void addConversionUnitWithoutVariation() {
        if (!productInfo.getInventoryManageType().equals("IMEI_SERIAL_NUMBER")) {
            // Click Select Unit button
            webUtils.click(withoutVariationSelectUnitBtn);
            logger.info("Add new conversion unit.");

            // Select conversion unit
            webUtils.click(withoutVariationUnitTextBox);

            // Get all conversion unit names in store
            List<String> unitNameList = APIGetConversionUnitList.getConversionUnitNames(unitInfoList);

            // Get conversion name to assign to this product
            String unitName = unitNameList.isEmpty() ? new APICreateConversionUnit(credentials).createConversionUnitAndGetName() : unitNameList.get(nextInt(unitNameList.size()));
            webUtils.sendKeys(unitTextBoxOnSetupVariationConversionUnitPage, unitName);
            webUtils.click(unitLocator(unitName));
            logger.info("Select conversion unit: {}", unitName);

            // Input conversion unit quantity
            long quantity = Math.min(Math.max(Collections.max(APIGetProductDetail.getBranchStocks(productInfo, null)), 1), MAX_PRICE / productInfo.getOrgPrice());
            webUtils.sendKeys(withoutVariationQuantity, String.valueOf(quantity));
            logger.info("Conversion unit quantity: {}", quantity);

            // Click Save button
            webUtils.click(withoutVariationSaveBtn);
        }
    }

    /**
     * Selects a variation for the product.
     *
     * @param variationValue The variation name to select.
     */
    private void selectVariation(String variationValue) {
        webUtils.clickJS(variationLocator(variationValue));

        if (!webUtils.isCheckedJS(variationLocator(variationValue))) selectVariation(variationValue);
    }

    /**
     * Adds a conversion unit configuration with variations.
     */
    public void addConversionUnitVariation() {
        if (!productInfo.getInventoryManageType().equals("IMEI_SERIAL_NUMBER")) {
            // Number of conversion units
            int numberOfConversionUnit = nextInt(APIGetProductDetail.getVariationModelList(productInfo).size()) + 1;

            // Select variation
            for (int varIndex = 0; varIndex < numberOfConversionUnit; varIndex++) {
                // Open Select Variation popup
                webUtils.click(selectVariationBtn);
                logger.info("Open select variation popup.");

                // Get variation
                String variation = variationList.get(varIndex);

                // Select variation
                selectVariation(variation);
                logger.info("Select variation: {}", variation);

                // Close Add variation popup
                webUtils.click(saveBtnOnSelectVariationPopup);
                logger.info("Close Select variation popup.");

                // Add conversion unit configuration for variation
                webUtils.clickJS(variationConfigureBtn, varIndex);
                logger.info("Navigation to configure conversion unit for variation page.");

                // Click Select Unit button
                webUtils.clickJS(selectUnitBtnOnSetupVariationConversionUnitPage);

                // Get all conversion unit names in store
                List<String> unitNameList = APIGetConversionUnitList.getConversionUnitNames(unitInfoList);

                // Get conversion name to assign to this product
                String unitName = unitNameList.isEmpty() ? new APICreateConversionUnit(credentials).createConversionUnitAndGetName() : unitNameList.get(nextInt(unitNameList.size()));

                // Select conversion unit
                webUtils.sendKeys(withoutVariationUnitTextBox, unitName);
                webUtils.click(unitLocator(unitName));
                logger.info("[{}] Select conversion unit: {}", variation, unitName);

                // Input conversion unit quantity
                long quantity = MAX_PRICE / APIGetProductDetail.getVariationListingPrice(productInfo, varIndex);
                webUtils.sendKeys(quantityOnSetupVariationConversionUnitPage, String.valueOf(quantity));
                logger.info("[{}] Conversion unit quantity: {}", variation, quantity);

                // Click Save button on variation config
                webUtils.click(saveBtnOnSetupVariationConversionUnitPage);
                logger.info("[{}] Complete configure conversion unit.", variation);

                // Wait for conversion unit page to load
                webUtils.waitURLShouldBeContains("/conversion-unit/variation/edit/");
                logger.info("[{}] Wait setup conversion unit page loaded.", variation);
            }

            // Click Save button on setup conversion unit page
            webUtils.click(variationSaveBtn);
        }
    }
}