package pages.web.seller.product.all_products;

import api.seller.product.APIGetProductDetail;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import utility.WebUtils;

import java.util.List;
import java.util.stream.IntStream;

import static org.apache.commons.lang.math.JVMRandom.nextLong;
import static org.apache.commons.lang.math.RandomUtils.nextInt;

public class WholesaleProductPage {

    private final WebUtils webUtils;
    private final Logger logger = LogManager.getLogger(WholesaleProductPage.class);
    private final APIGetProductDetail.ProductInformation productInfo;
    private final String defaultLanguage;

    public WholesaleProductPage(WebDriver driver, APIGetProductDetail.ProductInformation productInfo, String defaultLanguage) {
        this.webUtils = new WebUtils(driver);
        this.defaultLanguage = defaultLanguage;
        this.productInfo = productInfo;
    }

    // Locators
    private final By loc_btnAddWholesalePricing = By.xpath("//*[text() ='Thêm giá bán sỉ' or text() ='Add Wholesale Pricing']");
    private final By loc_btnSave = By.xpath("//button[*[text() ='Lưu' or text() = 'Save']]");
    private final By loc_txtBuyFrom = By.cssSelector("[name ^= 'buyFrom']");
    private final By loc_txtPricePerItem = By.xpath("//div[div/*[contains(@name, 'buyFrom')]]/following::div[1]//input");
    private final By loc_ddvSelectedSegment = By.cssSelector(".dropdown-search-checkbox-custom");
    private final By loc_ddlSegment = By.cssSelector(".label-list  input");
    private final By variationAddVariationBtn = By.xpath("//button[*[text() ='Thêm nhóm' or text() ='Add variation']]");
    private final By loc_dlgAddVariation_btnOK = By.cssSelector(".footer-btn .gs-button__green");
    private final By loc_lblVariationValue = By.cssSelector(".border-bottom > .wholesale-group-header > div > div");

    private By loc_dlgAddVariation_radVariation(String variationValue) {
        return By.xpath("//*[text() = '%s']//ancestor::label/input".formatted(variationValue));
    }

    // Helper Methods

    /**
     * Opens the segment dropdown, randomly selects a segment, and closes the dropdown.
     * The selected segment name is logged for reference.
     */
    private void selectSegment() {
        // Open the segment dropdown
        webUtils.click(loc_ddvSelectedSegment);

        // Retrieve the list of available segments
        List<WebElement> segments = webUtils.getListElement(loc_ddlSegment);

        // Select a random segment from the list
        int segmentIndex = nextInt(segments.size());
        String segmentName = webUtils.getValue(loc_ddlSegment, segmentIndex);

        // Click on the selected segment using JavaScript
        webUtils.clickJS(loc_ddlSegment, segmentIndex);
        logger.info("Selected segment: {}", segmentName);

        // Close the segment dropdown
        webUtils.click(loc_ddvSelectedSegment);
    }


    /**
     * Calculate and return the 'Buy From' quantity based on branch stock.
     *
     * @param maximumModelStock A list of branch stocks.
     * @return The 'Buy From' quantity.
     */
    private int calculateBuyFrom(int maximumModelStock) {
        return nextInt(Math.max(maximumModelStock, 1)) + 1;
    }

    /**
     * Calculate and return the 'Price Per Item' based on the given price.
     *
     * @param price The product price.
     * @return The 'Price Per Item'.
     */
    private long calculatePricePerItem(long price) {
        return nextLong(Math.max(price, 1));
    }

    /**
     * Opens the 'Add Variation' popup and selects the given variation.
     *
     * @param variationValue The value of the variation to select.
     */
    private void selectVariation(String variationValue) {
        // Click the 'Add Variation' button to open the variation selection popup
        webUtils.click(variationAddVariationBtn);
        logger.info("Opened add variation popup.");

        // Select the specified variation by checking the corresponding checkbox
        webUtils.checkCheckbox(loc_dlgAddVariation_radVariation(variationValue));
        logger.info("Selected variation: {}.", variationValue);

        // Click the 'OK' button to confirm the selection and close the popup
        webUtils.click(loc_dlgAddVariation_btnOK);

    }

    // Main Methods

    /**
     * Configures wholesale pricing for products without variations.
     */
    public void addWholesaleProductWithoutVariation() {
        // Click the 'Add Wholesale Pricing' button to open the wholesale price setup table
        webUtils.click(loc_btnAddWholesalePricing);
        logger.info("Opened setup wholesale price table.");

        // Calculate and input the 'Buy From' quantity based on available branch stock
        int buyFrom = calculateBuyFrom(APIGetProductDetail.getMaximumBranchStockForModel(productInfo, null));
        webUtils.sendKeys(loc_txtBuyFrom, String.valueOf(buyFrom));
        logger.info("Input 'Buy From' quantity: {}.", buyFrom);

        // Calculate and input the price per item based on the product's new price
        long pricePerItem = calculatePricePerItem(productInfo.getNewPrice());
        webUtils.sendKeys(loc_txtPricePerItem, String.valueOf(pricePerItem));
        logger.info("Input price per item: {}.", String.format("%,d", pricePerItem));

        // Select a segment from the dropdown
        selectSegment();

        // Save the wholesale pricing configuration
        webUtils.click(loc_btnSave);
        logger.info("Saved wholesale product configuration.");
    }


    /**
     * Adds wholesale pricing configuration for products with variations.
     */
    public void addWholesaleProductVariation() {
        // Retrieve the list of variation values and their corresponding model IDs
        List<String> variationValues = APIGetProductDetail.getVariationValues(productInfo, defaultLanguage);
        List<Integer> variationModelIds = APIGetProductDetail.getVariationModelList(productInfo);

        // Determine how many variations to configure for wholesale, randomly selecting at least one
        int numberOfWholesaleProduct = nextInt(variationValues.size()) + 1;

        // Select the required number of variations based on their values
        IntStream.range(0, numberOfWholesaleProduct).forEach(varIndex -> selectVariation(variationValues.get(varIndex)));

        // For each selected variation, configure wholesale pricing
        IntStream.range(0, numberOfWholesaleProduct).forEach(index -> {
            // Retrieve the variation's label value
            String value = webUtils.getText(loc_lblVariationValue, index).replace(",", "");

            // Find the index of the variation in the variation values list
            int varIndex = variationValues.indexOf(value);

            // Click the 'Add Wholesale Pricing' button for the current variation
            webUtils.clickJS(loc_btnAddWholesalePricing, index);

            // Calculate and input the 'Buy From' quantity based on available branch stock for the variation
            int buyFrom = calculateBuyFrom(APIGetProductDetail.getMaximumBranchStockForModel(productInfo, variationModelIds.get(varIndex)));
            webUtils.sendKeys(loc_txtBuyFrom, index, String.valueOf(buyFrom));
            logger.info("[{}] Input 'Buy From' quantity: {}.", value, buyFrom);

            // Calculate and input the price per item based on the variation's selling price
            long pricePerItem = calculatePricePerItem(APIGetProductDetail.getVariationSellingPrice(productInfo, varIndex));
            webUtils.sendKeys(loc_txtPricePerItem, index, String.valueOf(pricePerItem));
            logger.info("[{}] Input price per item: {}.", value, String.format("%,d", pricePerItem));

            // Select a segment for the variation
            selectSegment();
        });

        // Save the wholesale pricing configuration for all variations
        webUtils.click(loc_btnSave);
        logger.info("Saved wholesale product configuration for variations.");
    }
}
