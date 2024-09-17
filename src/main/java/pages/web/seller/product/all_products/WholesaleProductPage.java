package pages.web.seller.product.all_products;

import api.seller.login.APIDashboardLogin;
import api.seller.product.APIGetProductDetail;
import api.seller.setting.APIGetStoreDefaultLanguage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import utility.WebUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static org.apache.commons.lang.math.JVMRandom.nextLong;
import static org.apache.commons.lang.math.RandomUtils.nextInt;

/**
 * Page Object Model class for managing wholesale pricing on the product page.
 */
public class WholesaleProductPage {

    private final WebUtils webUtils;
    private final Logger logger = LogManager.getLogger(WholesaleProductPage.class);

    private List<Long> wholesaleProductPrice;
    private List<Integer> wholesaleProductStock;
    private final List<String> variationList;
    private final List<Integer> variationModelList;
    private final Map<Integer, List<Integer>> productStockQuantity;
    private final List<Long> productSellingPrice;

    /**
     * Constructs an instance of WholesaleProductPage.
     *
     * @param driver       The WebDriver instance used for browser interactions.
     * @param credentials  The credentials for API login.
     * @param productInfo The product information associated with the wholesale pricing.
     */
    public WholesaleProductPage(WebDriver driver, APIDashboardLogin.Credentials credentials, APIGetProductDetail.ProductInformation productInfo) {
        this.webUtils = new WebUtils(driver);

        String defaultLanguage = new APIGetStoreDefaultLanguage(credentials).getDefaultLanguage();

        this.variationList = APIGetProductDetail.getVariationValues(productInfo, defaultLanguage);
        this.variationModelList = APIGetProductDetail.getVariationModelList(productInfo);
        this.productStockQuantity = APIGetProductDetail.getProductStockQuantityMap(productInfo);
        this.productSellingPrice = productInfo.isHasModel() ? APIGetProductDetail.getVariationSellingPrice(productInfo) : List.of(productInfo.getNewPrice());
    }

    // Locators for UI elements on the wholesale pricing page
    private final By withoutVariationAddWholesalePricingBtn = By.cssSelector(".wholesale-btn-group-header .gs-button__gray--outline");
    private final By saveBtn = By.cssSelector(".wholesale-btn-group-header > .gs-button__green");
    private final By withoutVariationBuyFrom = By.cssSelector("[name ^= 'buyFrom']");
    private final By withoutVariationWholesalePrice = By.xpath("//*[contains(@name, 'buyFrom')]/parent::div/parent::div//following-sibling::div[@class='wholesale-grid-item'][1]//input");
    private final By withoutVariationSegmentDropdown = By.cssSelector(".dropdown-search-checkbox-custom");
    private final By variationAddVariationBtn = By.cssSelector(".wholesale-btn-group-header > .gs-button__gray--outline");

    private By variationLocator(String variationValue) {
        return By.xpath("//*[text() = '%s']//ancestor::label/input".formatted(variationValue));
    }

    private final By okBtnOnAddVariationPopup = By.cssSelector(".footer-btn .gs-button__green");
    private final By variationAddWholesalePricingBtn = By.cssSelector(".border-bottom > .wholesale-group-header .gs-fake-link:nth-child(1)");
    private final By variationValue = By.cssSelector(".border-bottom > .wholesale-group-header > div > div");
    private final By variationBuyFrom = By.cssSelector("[name^='buyFrom-']");
    private final By variationWholesalePrice = By.xpath("//*[contains(@name, 'buyFrom')]/parent::div/parent::div//following-sibling::div[@class='wholesale-grid-item'][1]//input");
    private final By variationSegmentDropdown = By.cssSelector(".dropdown-search-checkbox-custom");
    private final By loc_chkSegment = By.cssSelector(".label-list  input");

    private int numOfWholesaleProduct;

    /**
     * Retrieves wholesale product information and initializes data for configuration.
     *
     * @return The current instance of WholesaleProductPage for method chaining.
     */
    public WholesaleProductPage getWholesaleProductInfo() {
        wholesaleProductPrice = new ArrayList<>(productSellingPrice);
        wholesaleProductStock = new ArrayList<>();
        IntStream.range(0, wholesaleProductPrice.size()).forEachOrdered(ignored -> wholesaleProductStock.add(0));
        numOfWholesaleProduct = nextInt(variationList.size()) + 1;
        IntStream.range(0, numOfWholesaleProduct).forEach(varIndex -> {
            wholesaleProductPrice.set(varIndex, nextLong(productSellingPrice.get(varIndex)) + 1);
            wholesaleProductStock.set(varIndex, nextInt(Math.max(Collections.max(productStockQuantity.get(variationModelList.get(varIndex))), 1)) + 1);
        });
        return this;
    }

    /**
     * Adds wholesale pricing configuration for products without variations.
     */
    public void addWholesaleProductWithoutVariation() {
        // Click 'Add Wholesale Pricing' button
        webUtils.click(withoutVariationAddWholesalePricingBtn);
        logger.info("Opened setup wholesale price table.");

        // Input 'Buy From' quantity
        webUtils.sendKeys(withoutVariationBuyFrom, String.valueOf(wholesaleProductStock.getFirst()));
        logger.info("Input 'Buy From': {}.", wholesaleProductStock.getFirst());

        // Input 'Price Per Item'
        webUtils.sendKeys(withoutVariationWholesalePrice, String.valueOf(wholesaleProductPrice.getFirst()));
        logger.info("Input price per item: {}.", String.format("%,d", wholesaleProductPrice.getFirst()));

        // Open and select segment from dropdown
        webUtils.click(withoutVariationSegmentDropdown);
        logger.info("Opened segment dropdown.");

        // Get list segments
        List<WebElement> elements = webUtils.getListElement(loc_chkSegment);

        // Select segment
        int segmentIndex = nextInt(elements.size());
        logger.info("[WithoutVariation] Select segment: {}", webUtils.getAttribute(loc_chkSegment, "id"));
        webUtils.clickJS(loc_chkSegment, segmentIndex);

        // Close segment dropdown
        webUtils.click(withoutVariationSegmentDropdown);
        logger.info("Closed segment dropdown.");

        // Complete wholesale product configuration
        webUtils.click(saveBtn);
    }

    /**
     * Selects a variation based on the provided value.
     *
     * @param variationValue The variation value to be selected.
     */
    private void selectVariation(String variationValue) {
        By locator = variationLocator(variationValue);
        webUtils.clickJS(locator);

        if (!webUtils.isCheckedJS(locator)) {
            selectVariation(variationValue);
        }
    }

    /**
     * Configures wholesale pricing for variations and returns a list of variations configured for sale.
     *
     * @return List of variations configured for sale.
     */
    private List<String> addConfigureForVariation() {
        List<String> variationSaleList = new ArrayList<>();
        for (int varIndex = 0; varIndex < numOfWholesaleProduct; varIndex++) {
            String variation = variationList.get(varIndex).replace(" ", "|");

            // Open 'Add Variation' popup
            webUtils.click(variationAddVariationBtn);
            logger.info("Opened add variation popup for wholesale configuration.");

            // Select variation
            selectVariation(variation);
            logger.info("Configured wholesale pricing for variation '{}'.", variation);

            // Close 'Add Variation' popup
            webUtils.click(okBtnOnAddVariationPopup);
            variationSaleList.add("%s,".formatted(variation));
        }
        return variationSaleList;
    }

    /**
     * Adds wholesale pricing configuration for products with variations.
     */
    public void addWholesaleProductVariation() {
        List<String> variationSaleList = addConfigureForVariation();

        // Configure wholesale pricing for each variation
        for (int index = 0; index < variationSaleList.size(); index++) {
            String value = webUtils.getText(variationValue, index);
            int varIndex = variationSaleList.indexOf(value);

            webUtils.clickJS(variationAddWholesalePricingBtn, index);

            // Input 'Buy From' quantity
            webUtils.sendKeys(variationBuyFrom, index, String.valueOf(wholesaleProductStock.get(varIndex)));
            logger.info("[{}] Input 'Buy From': {}.", value, wholesaleProductStock.get(varIndex));

            // Input 'Price Per Item'
            webUtils.sendKeys(variationWholesalePrice, index, String.valueOf(wholesaleProductPrice.get(varIndex)));
            logger.info("[{}] Input price per item: {}.", value, String.format("%,d", wholesaleProductPrice.get(varIndex)));

            // Open and select segment from dropdown
            webUtils.click(variationSegmentDropdown, index);

            // Get list segments
            List<WebElement> elements = webUtils.getListElement(loc_chkSegment);

            // Select segment
            int segmentIndex = nextInt(elements.size());
            logger.info("[WithVariation] Select segment: {}", webUtils.getAttribute(loc_chkSegment, "id"));
            webUtils.clickJS(loc_chkSegment, segmentIndex);

            // Close segment dropdown
            webUtils.click(variationSegmentDropdown, index);
        }

        // Complete wholesale product configuration
        webUtils.click(saveBtn);
    }
}
