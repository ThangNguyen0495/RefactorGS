package pages.web.seller.product.all_products;

import api.seller.login.APISellerLogin;
import api.seller.product.APIGetInventoryHistory;
import api.seller.product.APIGetProductDetail;
import api.seller.product.APIGetProductList;
import api.seller.product.APIGetStockAlert;
import org.apache.commons.lang.math.JVMRandom;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import utility.PropertiesUtils;
import utility.WebUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.IntStream;

import static org.apache.commons.lang.math.RandomUtils.nextInt;

public class ProductManagementPage {
    // Instance variables for API services
    private static final Logger logger = LogManager.getLogger();
    private final WebDriver driver;
    private final WebUtils webUtils;
    private APIGetProductDetail apiGetProductDetail;
    private APIGetProductList apiGetProductList;
    private APIGetInventoryHistory apiGetInventoryHistory;
    private APIGetStockAlert apiGetStockAlert;
    private static final long MAX_PRICE = 99999999999L;

    /**
     * Constructs a ProductManagementPage instance with the given WebDriver.
     *
     * @param driver the WebDriver instance to use for web interactions.
     */
    public ProductManagementPage(WebDriver driver) {
        this.driver = driver;
        this.webUtils = new WebUtils(driver);
    }

    /**
     * Initializes API services with the provided credentials and returns the current instance.
     *
     * @param credentials the credentials to use for API service authentication.
     * @return the current instance of {@code ProductManagementPage}.
     */
    public ProductManagementPage fetchInformation(APISellerLogin.Credentials credentials) {
        this.apiGetProductDetail = new APIGetProductDetail(credentials);
        this.apiGetProductList = new APIGetProductList(credentials);
        this.apiGetInventoryHistory = new APIGetInventoryHistory(credentials);
        this.apiGetStockAlert = new APIGetStockAlert(credentials);

        return this;
    }

    // Locators
    private final By loc_lblProductId = By.cssSelector("tr [class='gs-table-body-item'] strong");
    private final By loc_prgStatus = By.xpath("//*[contains(@class, 'uik-widget-table__wrapper')]/preceding-sibling::div[1]/span");
    private final By loc_chkSelectAll = By.cssSelector("thead input");
    private final By loc_lnkSelectAction = By.cssSelector(".actions");
    private final By loc_ddlListActions = By.cssSelector(".actions > div");
    private final By loc_dlgClearStock_btnOK = By.cssSelector(".modalClearStock .gs-button__green");
    private final By loc_dlgDeleteProduct_btnDelete = By.cssSelector(".modalDeleteProduct .gs-button__red");
    private final By loc_dlgActiveProduct_btnYes = By.cssSelector(".modalActivateProduct .gs-button__green");
    private final By loc_dlgDeactivateProduct_btnYes = By.cssSelector(".modalActivateProduct .gs-button__green");
    private final By loc_dlgUpdateStock_actionsChange = By.cssSelector("[class *= 'gs-button__blue']:nth-child(2)");
    private final By loc_dlgUpdateStock_txtStockValue = By.xpath("//*[@name='quantity']/parent::div/parent::div/preceding-sibling::input");
    private final By loc_dlgUpdateStock_btnUpdate = By.cssSelector(".product-multiple-stock_updater_modal .gs-button__green");
    private final By loc_dlgUpdateTax_ddlTaxOptions = By.cssSelector("input[name='taxRadioGroup']");
    private final By loc_dlgUpdateTax_btnOK = By.cssSelector(".modalActivateProduct .gs-button__green");
    private final By loc_dlgDisplayOutOfStockProduct_listOptions = By.cssSelector("input[name='productRadioGroup']");
    private final By loc_dlgDisplayOutOfStockProduct_btnYes = By.cssSelector(".modalActivateProduct .gs-button__green");
    private final By loc_dlgUpdateSellingPlatform_chkApp = By.cssSelector("#onApp");
    private final By loc_dlgUpdateSellingPlatform_chkWeb = By.cssSelector("#onWeb");
    private final By loc_dlgUpdateSellingPlatform_chkInStore = By.cssSelector("#inStore");
    private final By loc_dlgUpdateSellingPlatform_chkGoSocial = By.cssSelector("#inGosocial");
    private final By loc_dlgUpdateSellingPlatform_btnConfirm = By.cssSelector(".modalPlatformProduct .gs-button__green");
    private final By loc_dlgUpdatePrice_ddvSelectedPriceType = By.cssSelector(".modal-body .uik-select__arrowWrapper");
    private final By loc_dlgUpdatePrice_ddlPriceType = By.cssSelector(".modal-body .uik-select__optionList > .uik-select__option");
    private final By loc_dlgUpdatePrice_txtApplyAll = By.xpath("//*[@id='apply-all']/parent::div/parent::div//preceding-sibling::input");
    private final By loc_dlgUpdatePrice_btnApplyAll = By.cssSelector(".modal-body .gs-button__blue");
    private final By loc_dlgUpdatePrice_btnUpdate = By.cssSelector(".modal-footer .gs-button__green");
    private final By loc_dlgSetStockAlert_txtStockAlertValueForAllProducts = By.cssSelector("#stock-alert-all");
    private final By loc_dlgSetStockAlert_btnApply = By.cssSelector(".product-multiple-stock_alert_updater_modal .gs-button__blue");
    private final By loc_dlgSetStockAlert_btnUpdate = By.cssSelector(".product-multiple-stock_alert_updater_modal .gs-button__green");
    private final By loc_dlgManageProductByLotDate_chkExcludeExpireQuantity = By.cssSelector("#expiredQuantity");
    private final By loc_dlgManageProductByLotDate_btnYes = By.cssSelector(".bulk-update-lot-type-modal .gs-button__green");


    void navigateToProductManagementPage() {
        driver.get("%s/product/list".formatted(PropertiesUtils.getDomain()));
        driver.navigate().refresh();
        logger.info("Navigate to product management page.");
    }

    /**
     * Opens the bulk actions dropdown and ensures that the 'Select All' checkbox is checked.
     * If no products are currently listed, refreshes the page to load the product list.
     */
    void openBulkActionsDropdown() {
        if (webUtils.getListElement(loc_lblProductId).isEmpty()) {
            driver.navigate().refresh();
        }
        if (!webUtils.isCheckedJS(loc_chkSelectAll)) {
            webUtils.clickJS(loc_chkSelectAll);
        }
        webUtils.clickJS(loc_lnkSelectAction);
    }

    /**
     * Selects a bulk action from the dropdown menu.
     *
     * @param actionIndex the index of the action to select, where:
     *                    <ul>
     *                      <li>0: Clear stock</li>
     *                      <li>1: Delete</li>
     *                      <li>2: Deactivate</li>
     *                      <li>3: Activate</li>
     *                      <li>4: Update stock</li>
     *                      <li>5: Update Tax</li>
     *                      <li>6: Display out of stock</li>
     *                      <li>7: Update selling platform</li>
     *                      <li>8: Update price</li>
     *                      <li>9: Set stock alert</li>
     *                      <li>10: Manage stock private final By Lot-date</li>
     *                    </ul>
     */
    private void selectBulkAction(int actionIndex) {
        // Open bulk actions dropdown
        openBulkActionsDropdown();

        // Select the specified bulk action from the dropdown
        webUtils.clickJS(loc_ddlListActions, actionIndex);
    }

    /**
     * Retrieves the IDs of all products selected for bulk updates on the first page of the product management section.
     * <p>
     * This method navigates to the product management page, refreshes if necessary, and collects product IDs
     * from the currently displayed products on the first page.
     *
     * @return A list of product IDs selected for bulk update.
     */
    private List<Integer> fetchSelectedProductIds() {
        // Navigate to the product management page
        navigateToProductManagementPage();

        // Refresh the page if the product IDs are not loaded
        refreshIfPageNotLoaded();

        // Collect and return product IDs from the first page
        return collectProductIds();
    }

    /**
     * Refreshes the page if the product ID labels are not loaded.
     */
    private void refreshIfPageNotLoaded() {
        if (webUtils.getListElement(loc_lblProductId).isEmpty()) {
            driver.navigate().refresh();
        }
    }

    /**
     * Collects and returns a list of product IDs from the first page.
     *
     * @return A list of product IDs displayed on the first page.
     */
    private List<Integer> collectProductIds() {
        // Get the number of products displayed on the first page
        int productCount = webUtils.getListElement(loc_lblProductId).size();

        // Collect product IDs into a list
        return IntStream.range(0, productCount)
                .mapToObj(index -> Integer.parseInt(webUtils.getText(loc_lblProductId, index)))
                .toList();
    }


    /**
     * Waits for the bulk update process to complete, retrying up to a maximum number of attempts if necessary.
     * <p>
     * This method refreshes the page and waits for a specified delay between each retry. The default wait time is
     * 10 seconds, but a custom delay can be passed as an argument. If the update process does not complete within
     * the maximum number of retries, an exception will be thrown.
     * </p>
     *
     * <ul>
     * 	<li>The method will wait for a short period between each attempt.</li>
     * 	<li>It refreshes the page after each wait to retrieve the current status.</li>
     * 	<li>If the bulk update process completes successfully within the maximum retries, the method will exit.</li>
     * 	<li>If the process fails to complete within the retries, it throws a {@code RuntimeException}.</li>
     * </ul>
     *
     * @throws RuntimeException if the bulk update does not complete within the allowed number of retries.
     */
    private void waitBulkUpdated() {
        final int maxAttempts = 5;
        long delayMil = 10000; // 10 secs

        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            // Wait for a short period before checking status
            try {
                Thread.sleep(delayMil);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Wait interrupted", e);
            }

            // Log current attempt
            logger.info("Waiting for bulk update... (Attempt {})", attempt + 1);

            // Refresh page to get the new status
            driver.navigate().refresh();

            // Check if the update is completed
            if (webUtils.getListElement(loc_prgStatus).isEmpty()) {
                logger.info("Bulk update completed successfully.");
                return;
            }
        }

        // Throw exception if the bulk update did not complete within the allowed retries
        throw new RuntimeException("Max retries reached. Bulk update did not complete.");
    }


    /**
     * Clears the stock of products in bulk and verifies the stock updates on ItemService and Elasticsearch.
     */
    private void bulkClearStock() {
        bulkStockUpdateAction(0, loc_dlgClearStock_btnOK, "CLEAR STOCK");
    }

    /**
     * Updates the stock of products in bulk and verifies the stock updates on ItemService and Elasticsearch.
     */
    private void bulkUpdateStock() {
        int newStock = generateRandomStock();
        bulkStockUpdateAction(4, loc_dlgUpdateStock_btnUpdate, "UPDATE STOCK", newStock);
    }

    /**
     * Executes a bulk stock action and verifies the stock updates on ItemService and Elasticsearch.
     *
     * @param actionIndex          The index of the bulk action to perform.
     * @param confirmButtonLocator The locator of the confirm button for the bulk action.
     * @param actionDescription    The description of the bulk action for logging purposes.
     * @param stockValue           The stock value to update (ignored for clearing stock).
     */
    private void bulkStockUpdateAction(int actionIndex, By confirmButtonLocator, String actionDescription, int... stockValue) {
        // Retrieve product IDs from the first page
        List<Integer> productIds = fetchSelectedProductIds();

        // Get product stock before the action
        List<Integer> beforeStock = getStockList(productIds);

        // Perform the bulk action
        performBulkAction(actionIndex, confirmButtonLocator, actionDescription, stockValue);

        // Verify stock updates on ItemService and Elasticsearch
        int newStock = stockValue.length > 0 ? stockValue[0] : 0;
        verifyStockUpdates(productIds, actionIndex, beforeStock, newStock);
    }

    /**
     * Retrieves the current stock for each product in the provided list of product IDs.
     *
     * @param productIds The list of product IDs.
     * @return A list of stock quantities before the bulk action.
     */
    private List<Integer> getStockList(List<Integer> productIds) {
        return productIds.stream()
                .map(productId -> apiGetProductDetail.getProductInformation(productId))
                .map(APIGetProductDetail::getTotalStockQuantity)
                .toList();
    }

    /**
     * Generates a random stock quantity for stock updates.
     *
     * @return A random stock value between 0 and MAX_STOCK_QUANTITY.
     */
    private int generateRandomStock() {
        final int MAX_STOCK_QUANTITY = 10_000_000;
        return RandomUtils.nextInt(MAX_STOCK_QUANTITY);
    }

    /**
     * Performs a bulk action such as clearing or updating stock.
     *
     * @param actionIndex          The index of the action to be performed in the bulk action's dropdown.
     * @param confirmButtonLocator The locator of the confirm button for the action.
     * @param actionDescription    A description of the action being performed for logging purposes.
     * @param stockValue           The stock value to input for update actions (ignored for clear stock).
     */
    private void performBulkAction(int actionIndex, By confirmButtonLocator, String actionDescription, int... stockValue) {
        selectBulkAction(actionIndex);

        if (stockValue.length > 0) {
            // Input stock value and confirm the update
            webUtils.clickJS(loc_dlgUpdateStock_actionsChange);
            webUtils.sendKeys(loc_dlgUpdateStock_txtStockValue, String.valueOf(stockValue[0]));
            logger.info("Input stock value: {}.", String.format("%,d", stockValue[0]));
            webUtils.click(loc_dlgUpdateStock_btnUpdate);
        } else {
            // Confirm the clear stock action
            webUtils.click(confirmButtonLocator);
        }

        // Wait for the bulk update to complete
        waitBulkUpdated();

        // Log action completion
        logger.info("Checked product information after bulk action: {}.", actionDescription);
    }

    /**
     * Verifies that stock updates are correctly reflected in both ItemService and Elasticsearch.
     *
     * @param productIds  The list of product IDs to verify.
     * @param actionIndex The index of the performed action (0 for clear stock, 4 for update stock).
     * @param beforeStock The stock quantities before the bulk action.
     * @param newStock    The new stock value to verify.
     */
    private void verifyStockUpdates(List<Integer> productIds, int actionIndex, List<Integer> beforeStock, int newStock) {
        IntStream.range(0, productIds.size()).forEach(productIndex -> {
            int productId = productIds.get(productIndex);
            var productInfo = apiGetProductDetail.getProductInformation(productId);
            int variationNum = productInfo.isHasModel()
                    ? APIGetProductDetail.getVariationModelList(productInfo).size()
                    : 1;

            // Check stock values in ItemService and Elasticsearch
            verifyStockInService("ItemService", productId, productInfo, beforeStock.get(productIndex), newStock, variationNum, actionIndex);
            verifyStockInService("Elasticsearch", productId, productInfo, beforeStock.get(productIndex), newStock, variationNum, actionIndex);
        });
    }

    /**
     * Verifies the stock in the specified service (ItemService/Elasticsearch).
     *
     * @param serviceName  The name of the service (ItemService or Elasticsearch).
     * @param productId    The ID of the product.
     * @param productInfo  The product information.
     * @param beforeStock  The stock value before the action.
     * @param newStock     The new stock value to verify.
     * @param variationNum The number of variations for the product.
     * @param actionIndex  The index of the performed action (0 for clear stock, 4 for update stock).
     */
    private void verifyStockInService(String serviceName, int productId, APIGetProductDetail.ProductInformation productInfo, int beforeStock, int newStock, int variationNum, int actionIndex) {
        int actualStock = serviceName.equals("ItemService")
                ? APIGetProductDetail.getTotalStockQuantity(productInfo)
                : apiGetProductList.fetchRemainingStockByProductId(productId);

        // Check if stock should remain unchanged for certain conditions
        boolean shouldStockRemainUnchanged = productInfo.isLotAvailable() ||
                                             (productInfo.getInventoryManageType().equals("IMEI_SERIAL_NUMBER") && actionIndex == 4);

        int expectedStock = shouldStockRemainUnchanged ? beforeStock : newStock * variationNum;

        Assert.assertEquals(actualStock, expectedStock,
                "[%s] Product stock is not as expected, productId: %d".formatted(serviceName, productId));

        logger.info("Verify product stock, productId: {}", productId);
    }

    /**
     * Performs a bulk delete operation on products and verifies the deletion status.
     * <p>
     * This method selects all products on the first page, performs a bulk delete operation,
     * and then verifies that the products have been deleted by checking their statuses in both
     * ItemService and Elasticsearch.
     */
    private void bulkDeleteProducts() {
        // Retrieve the list of product IDs on the first page
        List<Integer> productIds = fetchSelectedProductIds();

        // Select the delete action from the dropdown
        selectBulkAction(1);

        // Confirm the deletion in the popup
        webUtils.click(loc_dlgDeleteProduct_btnDelete);

        // Wait for the bulk delete operation to complete
        waitBulkUpdated();

        // Verify the deletion status of each product
        verifyProductDeletionStatus(productIds);
    }

    /**
     * Verifies that the deletion status of each product is accurately reflected in both ItemService and Elasticsearch.
     * <p>
     * This method checks whether a product is marked as deleted by querying its status in both systems
     * and compares it with the expected deletion status derived from the inventory history.
     *
     * @param productIds The list of product IDs to verify.
     */
    private void verifyProductDeletionStatus(List<Integer> productIds) {
        productIds.forEach(productId -> {
            // Fetch product information from ItemService
            var productInfo = apiGetProductDetail.getProductInformation(productId);

            // Determine if the product should be deleted based on inventory history
            boolean shouldBeDeleted = apiGetInventoryHistory.checkProductCanBeDeleted(productId);

            // Verify deletion status in ItemService
            Assert.assertEquals(
                    productInfo.isDeleted(), shouldBeDeleted,
                    "[ItemService] Product deletion status mismatch: expected '%s', found '%s', productId: %d"
                            .formatted(shouldBeDeleted, productInfo.isDeleted(), productId)
            );

            // Verify deletion status in Elasticsearch
            Assert.assertEquals(
                    apiGetProductList.isProductDeletedFromElasticsearch(productId), shouldBeDeleted,
                    "[Elasticsearch] Product deletion status mismatch: expected '%s', found '%s', productId: %d"
                            .formatted(shouldBeDeleted, apiGetProductList.isProductDeletedFromElasticsearch(productId), productId)
            );

            // Log verification result
            logger.info("Verified deletion status for productId: {}", productId);
        });
    }

    /**
     * Verifies that the status of each product is consistent in both ItemService and Elasticsearch.
     * <p>
     * This method checks whether the product status matches the expected status for a list of products
     * in both systems and logs the results for each product.
     *
     * @param productIds     The list of product IDs to verify.
     * @param expectedStatus The expected status of the product (e.g., "INACTIVE" or "ACTIVE").
     */
    private void verifyProductStatus(List<Integer> productIds, String expectedStatus) {
        productIds.forEach(productId -> {
            // Fetch product information
            var productInfo = apiGetProductDetail.getProductInformation(productId);

            // Verify in ItemService
            Assert.assertEquals(productInfo.getBhStatus(), expectedStatus,
                    "[ItemService] Product status must be updated to '%s', productId: %d".formatted(expectedStatus, productId));

            // Verify in Elasticsearch
            Assert.assertEquals(apiGetProductList.fetchElasticsearchProductStatus(productId), expectedStatus,
                    "[Elasticsearch] Product status must be updated to '%s', productId: %d".formatted(expectedStatus, productId));

            logger.info("Verify product status, productId: {}", productId);
        });
    }


    /**
     * Performs bulk activation or deactivation of products and verifies the status updates.
     *
     * @param actionIndex       The index of the action to be performed (2: deactivate, 3: activate).
     * @param confirmButton     The locator for the confirmation button (deactivate or activate).
     * @param expectedStatus    The expected product status ("INACTIVE" for deactivate, "ACTIVE" for activate).
     * @param actionDescription A description of the bulk action being performed (e.g., "DEACTIVATE", "ACTIVATE").
     */
    private void bulkUpdateProductStatus(int actionIndex, By confirmButton, String expectedStatus, String actionDescription) {
        // Get the list of product IDs from the first page
        List<Integer> productIds = fetchSelectedProductIds();

        // Select the appropriate action (deactivate or activate)
        selectBulkAction(actionIndex);

        // Confirm the action (deactivate or activate)
        webUtils.click(confirmButton);

        // Wait for the status update to complete
        waitBulkUpdated();

        // Verify product status in both ItemService and Elasticsearch
        verifyProductStatus(productIds, expectedStatus);

        // Log the result
        logger.info("Check product status after bulk actions: {}.", actionDescription);
    }

    /**
     * Performs bulk deactivation of products and verifies the status updates.
     */
    private void bulkDeactivateProduct() {
        bulkUpdateProductStatus(2, loc_dlgDeactivateProduct_btnYes, "INACTIVE", "DEACTIVATE");
    }

    /**
     * Performs bulk activation of products and verifies the status updates.
     */
    private void bulkActivateProduct() {
        bulkUpdateProductStatus(3, loc_dlgActiveProduct_btnYes, "ACTIVE", "ACTIVATE");
    }

    /**
     * Performs bulk update of the tax for products and verifies the updates.
     */
    private void bulkUpdateTax() {
        // Get the list of product IDs from the first page
        List<Integer> productIds = fetchSelectedProductIds();

        // Select "Update Tax" action from the dropdown
        selectBulkAction(5);

        // Randomly select a new tax ID from the available options
        int bound = webUtils.getListElement(loc_dlgUpdateTax_ddlTaxOptions).size();
        int taxIndex = nextInt(bound);
        int newTaxId = Integer.parseInt(webUtils.getValue(loc_dlgUpdateTax_ddlTaxOptions, taxIndex));
        webUtils.clickJS(loc_dlgUpdateTax_ddlTaxOptions, taxIndex);
        logger.info("Bulk actions update tax: %d.".formatted(newTaxId));

        // Confirm the tax update
        webUtils.click(loc_dlgUpdateTax_btnOK);

        // Wait for the status update to complete
        waitBulkUpdated();

        // Verify the updated tax ID for each product
        verifyProductTaxIdUpdate(productIds, newTaxId);

        // Log the result
        logger.info("Check product taxId after bulk actions: UPDATE TAX.");
    }

    /**
     * Verifies that the tax ID of each product is updated correctly in ItemService.
     * <p>
     * This method checks whether the tax ID for a list of products matches the expected new tax ID.
     * It logs the verification results for each product.
     *
     * @param productIds The list of product IDs to verify.
     * @param newTaxId  The expected new tax ID to compare against.
     */
    private void verifyProductTaxIdUpdate(List<Integer> productIds, int newTaxId) {
        productIds.forEach(productId -> {
            // Fetch product information
            var productInfo = apiGetProductDetail.getProductInformation(productId);

            // Verify the product's tax ID
            Assert.assertEquals(productInfo.getTaxId(), newTaxId,
                    "[ItemService] Product tax must be updated to '%d' but found '%d', productId: %d"
                            .formatted(newTaxId, productInfo.getTaxId(), productId));

            logger.info("Verify product tax, productId: {}", productId);
        });
    }

    /**
     * Bulk updates the display setting for products when they are out of stock.
     *
     * @param optionIndex the option to select:
     *                    <ul>
     *                      <li>0: Display products when out of stock</li>
     *                      <li>1: Hide products when out of stock</li>
     *                    </ul>
     */
    private void bulkDisplayOutOfStock(int optionIndex) {
        // Open the display out of stock popup from bulk actions
        selectBulkAction(6);

        // Select the desired option for out-of-stock display
        webUtils.clickJS(loc_dlgDisplayOutOfStockProduct_listOptions, optionIndex);

        // Confirm the action
        webUtils.click(loc_dlgDisplayOutOfStockProduct_btnYes);

        // Wait for the bulk action to complete
        waitBulkUpdated();
    }

    /**
     * Verifies the display setting for a list of products when they are out of stock.
     * <p>
     * This method checks whether each product is set to display or hide when out of stock,
     * based on the expected display state provided as a parameter. It logs the result of each verification.
     *
     * @param productIds A list of product IDs to verify.
     * @param shouldShow True if the product should be displayed when out of stock, false otherwise.
     */
    private void verifyOutOfStockDisplay(List<Integer> productIds, boolean shouldShow) {
        productIds.forEach(productId -> {
            var productInfo = apiGetProductDetail.getProductInformation(productId);
            if (shouldShow) {
                Assert.assertTrue(productInfo.isShowOutOfStock(),
                        "[ItemService] Cannot update product to display when out of stock, productId: %d".formatted(productId));
            } else {
                Assert.assertFalse(productInfo.isShowOutOfStock(),
                        "[ItemService] Cannot update product to hide when out of stock, productId: %d".formatted(productId));
            }

            logger.info("Verify out of stock display, productId: {}", productId);
        });
    }


    /**
     * Manages the display of products when they are out of stock. Verifies the changes for both display options.
     */
    private void bulkDisplayOutOfStockProduct() {
        // Get the list of product IDs from the first page
        List<Integer> productIds = fetchSelectedProductIds();

        // Display products when out of stock
        bulkDisplayOutOfStock(0);
        verifyOutOfStockDisplay(productIds, true);
        logger.info("Check product after bulk actions: DISPLAY OUT OF STOCK PRODUCT.");

        // Hide products when out of stock
        bulkDisplayOutOfStock(1);
        verifyOutOfStockDisplay(productIds, false);
        logger.info("Check product after bulk actions: DO NOT DISPLAY OUT OF STOCK PRODUCT.");
    }

    /**
     * Updates the selling platform configurations for multiple products in bulk.
     * <p>
     * This method updates selling platforms (App, Web, InStore, GoSocial) in bulk for products
     * on the first page and verifies the updates.
     */
    private void bulkUpdateSellingPlatform() {
        // Retrieve the list of product IDs from the first page
        List<Integer> productIds = fetchSelectedProductIds();

        // Select the "Update Selling Platform" action from the dropdown
        selectBulkAction(7);

        // Configure selling platforms by toggling checkboxes
        Map<String, Boolean> platformStates = configureSellingPlatforms();

        // Confirm the platform update
        webUtils.click(loc_dlgUpdateSellingPlatform_btnConfirm);

        // Wait for the update to complete
        waitBulkUpdated();

        // Verify the selling platform configuration for each product
        verifySellingPlatformConfiguration(productIds, platformStates);

        // Log the result
        logger.info("Check product selling platform after bulk actions: UPDATE SELLING PLATFORM.");
    }

    /**
     * Configures the selling platforms by randomly toggling the platform checkboxes.
     *
     * @return A map containing the state of each platform (true if enabled, false if disabled)
     */
    private Map<String, Boolean> configureSellingPlatforms() {
        Map<String, Boolean> platformStates = new HashMap<>();
        platformStates.put("App", toggleCheckbox(loc_dlgUpdateSellingPlatform_chkApp));
        platformStates.put("Web", toggleCheckbox(loc_dlgUpdateSellingPlatform_chkWeb));
        platformStates.put("InStore", toggleCheckbox(loc_dlgUpdateSellingPlatform_chkInStore));
        platformStates.put("GoSocial", toggleCheckbox(loc_dlgUpdateSellingPlatform_chkGoSocial));

        return platformStates;
    }

    /**
     * Verifies the selling platform configuration for a list of products.
     * <p>
     * This method checks whether each product is correctly configured on the specified platforms (App, Web, InStore, GoSocial)
     * against the expected platform states provided in the map. It logs any mismatches found during verification.
     *
     * @param productIds     A list of product IDs to verify.
     * @param platformStates A map containing the expected platform states for verification, where the keys are the platform names
     *                       ("App", "Web", "InStore", "GoSocial") and the values are booleans indicating whether the product
     *                       should be available on those platforms.
     */
    private void verifySellingPlatformConfiguration(List<Integer> productIds, Map<String, Boolean> platformStates) {
        productIds.forEach(productId -> {
            var productInfo = apiGetProductDetail.getProductInformation(productId);

            Assert.assertEquals(productInfo.isOnApp(), platformStates.get("App"),
                    "[ItemService] App platform mismatch, productId: %d".formatted(productId));
            Assert.assertEquals(productInfo.isOnWeb(), platformStates.get("Web"),
                    "[ItemService] Web platform mismatch, productId: %d".formatted(productId));
            Assert.assertEquals(productInfo.isInStore(), platformStates.get("InStore"),
                    "[ItemService] InStore platform mismatch, productId: %d".formatted(productId));
            Assert.assertEquals(productInfo.isInGosocial(), platformStates.get("GoSocial"),
                    "[ItemService] GoSocial platform mismatch, productId: %d".formatted(productId));

            logger.info("Verify product selling platform, productId: {}", productId);
        });
    }

    /**
     * Toggles a checkbox for a given element.
     *
     * @param locator Locator of the checkbox element
     * @return The new state of the checkbox after toggling (true if checked, false otherwise)
     */
    private boolean toggleCheckbox(By locator) {
        boolean newState = !webUtils.isDisabledJS(locator) && RandomUtils.nextBoolean();
        if (newState) {
            webUtils.checkCheckbox(locator);
        } else {
            webUtils.uncheckCheckbox(locator);
        }
        return newState;
    }

    /**
     * Applies the specified price to all selected products and selects the price type.
     *
     * @param price     the price value to apply to all products.
     * @param typeIndex the index of the price type to select, where:
     *                  <ul>
     *                    <li>0: Listing price</li>
     *                    <li>1: Selling price</li>
     *                    <li>2: Cost price</li>
     *                  </ul>
     */
    private void bulkUpdatePrice(long price, int typeIndex) {
        webUtils.sendKeys(loc_dlgUpdatePrice_txtApplyAll, String.valueOf(price));
        webUtils.clickJS(loc_dlgUpdatePrice_ddvSelectedPriceType);
        webUtils.clickJS(loc_dlgUpdatePrice_ddlPriceType, typeIndex);
        webUtils.click(loc_dlgUpdatePrice_btnApplyAll);
    }

    /**
     * Performs a bulk update of prices for selected products. This includes setting the listing price, selling price, and cost price.
     *
     * @param listingPrice the new listing price to apply to all selected products.
     * @param sellingPrice the new selling price to apply to all selected products.
     * @param costPrice    the new cost price to apply to all selected products.
     */
    private void bulkActionsUpdatePrice(long listingPrice, long sellingPrice, long costPrice) {
        // Open the bulk actions menu and select the 'Update price' action
        selectBulkAction(8);

        // Input the new cost price for all selected products
        bulkUpdatePrice(costPrice, 2);

        // Input the new selling price for all selected products
        bulkUpdatePrice(sellingPrice, 1);

        // Input the new listing price for all selected products
        bulkUpdatePrice(listingPrice, 0);

        // Complete the price update process
        webUtils.click(loc_dlgUpdatePrice_btnUpdate);
    }

    /**
     * Updates the prices of products in bulk. This includes:
     * <ul>
     *   <li>Fetching product details to determine current price ranges.</li>
     *   <li>Calculating new prices and applying them.</li>
     *   <li>Verifying that the prices have been updated correctly.</li>
     * </ul>
     */
    private void bulkUpdatePrice() {
        List<Integer> productIds = fetchSelectedProductIds();

        // Retrieve and calculate price lists
        List<Long> listingPriceList = fetchPriceList(productIds, APIGetProductDetail::getVariationListingPrice, APIGetProductDetail.ProductInformation::getOrgPrice);
        List<Long> sellingPriceList = fetchPriceList(productIds, APIGetProductDetail::getVariationSellingPrice, APIGetProductDetail.ProductInformation::getNewPrice);
        List<Long> costPriceList = fetchPriceList(productIds, APIGetProductDetail::getVariationCostPrice, APIGetProductDetail.ProductInformation::getCostPrice);

        // Calculate new prices
        long listingPrice = calculateNewListingPrice(listingPriceList);
        long sellingPrice = calculateNewPrice(sellingPriceList, listingPrice);
        long costPrice = calculateCostPrice(costPriceList);

        // Apply new prices
        bulkActionsUpdatePrice(listingPrice, sellingPrice, costPrice);
        logger.info("Input listing price: %,d.".formatted(listingPrice));
        logger.info("Input selling price: %,d.".formatted(sellingPrice));
        logger.info("Input cost price: %,d.".formatted(costPrice));

        // Wait for the update to complete
        waitBulkUpdated();

        verifyProductPrices(productIds, listingPrice, sellingPrice, costPrice);
    }

    /**
     * Fetches a list of prices for products using the provided API methods.
     *
     * @param productIds           the list of product IDs to fetch prices for.
     * @param variationPriceMethod the method to fetch variation prices.
     * @param defaultPriceMethod   the method to fetch default prices.
     * @return a list of prices for the products.
     */
    private List<Long> fetchPriceList(List<Integer> productIds, Function<APIGetProductDetail.ProductInformation, List<Long>> variationPriceMethod, Function<APIGetProductDetail.ProductInformation, Long> defaultPriceMethod) {
        return productIds.stream()
                .map(productId -> apiGetProductDetail.getProductInformation(productId))
                .flatMap(productInfo -> (productInfo.isHasModel() ? variationPriceMethod.apply(productInfo) : List.of(defaultPriceMethod.apply(productInfo))).stream())
                .toList();
    }

    /**
     * Calculates a new listing price based on the current listing price list and a maximum value.
     *
     * @param listingPriceList the current list of listing prices.
     * @return the new calculated listing price.
     */
    private long calculateNewListingPrice(List<Long> listingPriceList) {
        long maxListingPrice = Collections.max(new ArrayList<>(listingPriceList));
        return maxListingPrice + JVMRandom.nextLong(Math.max(MAX_PRICE - maxListingPrice, 1));
    }

    /**
     * Calculates a new selling price based on the current selling price list and the listing price.
     *
     * @param sellingPriceList the current list of selling prices.
     * @param listingPrice     the listing price used as a reference.
     * @return the new calculated selling price.
     */
    private long calculateNewPrice(List<Long> sellingPriceList, long listingPrice) {
        long maxSellingPrice = Collections.max(new ArrayList<>(sellingPriceList));
        return maxSellingPrice + JVMRandom.nextLong(Math.max(listingPrice - maxSellingPrice, 1));
    }

    /**
     * Calculates a new cost price based on the current cost price list.
     *
     * @param costPriceList the current list of cost prices.
     * @return the new calculated cost price.
     */
    private long calculateCostPrice(List<Long> costPriceList) {
        long minCostPrice = Collections.min(new ArrayList<>(costPriceList));
        return JVMRandom.nextLong(Math.max(minCostPrice, 1));
    }

    /**
     * Verifies that the product prices have been updated correctly.
     *
     * @param productIds           the list of product IDs to verify.
     * @param expectedListingPrice the expected listing price.
     * @param expectedSellingPrice the expected selling price.
     * @param expectedCostPrice    the expected cost price.
     */
    private void verifyProductPrices(List<Integer> productIds, long expectedListingPrice, long expectedSellingPrice, long expectedCostPrice) {
        productIds.forEach(productId -> {
            var productInfo = apiGetProductDetail.getProductInformation(productId);
            Assert.assertTrue(APIGetProductDetail.getVariationListingPrice(productInfo).parallelStream().allMatch(price -> price == expectedListingPrice), "[ItemService] Listing price must be '%,d', productId: %d".formatted(expectedListingPrice, productId));
            Assert.assertTrue(APIGetProductDetail.getVariationSellingPrice(productInfo).parallelStream().allMatch(price -> price == expectedSellingPrice), "[ItemService] Selling price must be '%,d', productId: %d".formatted(expectedSellingPrice, productId));
            Assert.assertTrue(APIGetProductDetail.getVariationCostPrice(productInfo).parallelStream().allMatch(price -> price == expectedCostPrice), "[ItemService] Cost price must be '%,d', productId: %d".formatted(expectedCostPrice, productId));
            logger.info("Verify product prices: {}", productId);
        });
        logger.info("Check product price after bulk actions: UPDATE PRICE.");
    }

    /**
     * Sets a stock alert value for all selected products.
     */
    private void bulkSetStockAlert() {
        List<Integer> productIds = fetchSelectedProductIds();

        // Open the bulk actions menu and select the 'Set stock alert' action
        selectBulkAction(9);

        // Set new stock alert value
        int stockAlertValue = RandomUtils.nextInt(10);
        webUtils.sendKeys(loc_dlgSetStockAlert_txtStockAlertValueForAllProducts, String.valueOf(stockAlertValue));
        webUtils.click(loc_dlgSetStockAlert_btnApply);
        logger.info("Bulk actions set stock alert: %d.".formatted(stockAlertValue));

        // Confirm and complete the update
        webUtils.click(loc_dlgSetStockAlert_btnUpdate);
        waitBulkUpdated();

        // Verify that the stock alert value has been updated
        verifyStockAlert(productIds, stockAlertValue);
    }

    /**
     * Verifies that the stock alert value has been updated correctly for all products.
     *
     * @param productIds         the list of product IDs to verify.
     * @param expectedStockAlert the expected stock alert value.
     */
    private void verifyStockAlert(List<Integer> productIds, int expectedStockAlert) {
        productIds.forEach(productId -> {
            List<Integer> stockAlert = apiGetStockAlert.getProductStockAlert(productId);
            Assert.assertTrue(stockAlert.parallelStream().allMatch(alert -> alert == expectedStockAlert), "[ItemService] Product stock alert must be '%,d', productId: %d".formatted(expectedStockAlert, productId));
            logger.info("Verify product stock alert, productId: {}", productId);
        });
        logger.info("Check product stock alert value after bulk actions: SET STOCK ALERT.");
    }

    /**
     * Manages stock by lot date for selected products.
     */
    private void bulkManageStockByLotDate() {
        List<Integer> productIds = fetchSelectedProductIds();

        // Get current product lot date status
        List<Boolean> beforeLot = fetchLotAvailability(productIds);
        List<Boolean> beforeExpiredQuality = fetchExpiredQualityStatus(productIds);

        // Open the bulk actions menu and select the 'Manage stock by Lot-date' action
        selectBulkAction(10);

        // Set expire option
        boolean isExpiredQuality = RandomUtils.nextBoolean();
        if (webUtils.isCheckedJS(loc_dlgManageProductByLotDate_chkExcludeExpireQuantity) != isExpiredQuality)
            webUtils.clickJS(loc_dlgManageProductByLotDate_chkExcludeExpireQuantity);
        logger.info("Exclude expired quantity from remaining stock: {}.", isExpiredQuality);

        // Confirm the bulk update
        webUtils.click(loc_dlgManageProductByLotDate_btnYes);
        waitBulkUpdated();

        // Verify that the stock lot and expired quality statuses have been updated correctly
        verifyStockLotAndQuality(productIds, beforeLot, beforeExpiredQuality, isExpiredQuality);
    }

    /**
     * Fetches the lot availability status for a list of products.
     *
     * @param productIds the list of product IDs to fetch status for.
     * @return a list of boolean values indicating lot availability.
     */
    private List<Boolean> fetchLotAvailability(List<Integer> productIds) {
        return productIds.stream()
                .map(productId -> apiGetProductDetail.getProductInformation(productId))
                .map(APIGetProductDetail.ProductInformation::isLotAvailable)
                .toList();
    }

    /**
     * Fetches the expired quality status for a list of products.
     *
     * @param productIds the list of product IDs to fetch status for.
     * @return a list of boolean values indicating expired quality status.
     */
    private List<Boolean> fetchExpiredQualityStatus(List<Integer> productIds) {
        return productIds.stream()
                .map(productId -> apiGetProductDetail.getProductInformation(productId))
                .map(APIGetProductDetail.ProductInformation::isExpiredQuality)
                .toList();
    }

    /**
     * Verifies that the stock lot and expired quality statuses have been updated correctly.
     *
     * @param productIds           the list of product IDs to verify.
     * @param beforeLot            the list of lot availability statuses before the update.
     * @param beforeExpiredQuality the list of expired quality statuses before the update.
     * @param isExpiredQuality     the value indicating whether expired quality should be excluded.
     */
    private void verifyStockLotAndQuality(List<Integer> productIds, List<Boolean> beforeLot, List<Boolean> beforeExpiredQuality, boolean isExpiredQuality) {
        IntStream.range(0, productIds.size()).forEach(productIndex -> {
            int productId = productIds.get(productIndex);
            var productInfo = apiGetProductDetail.getProductInformation(productId);
            if (!productInfo.isDeleted()) {
                boolean expectedLotAvailable = beforeLot.get(productIndex) || (!productInfo.getInventoryManageType().equals("IMEI_SERIAL_NUMBER") && apiGetInventoryHistory.checkProductCanBeManagedByLotDate(productId));
                Assert.assertEquals(productInfo.isLotAvailable(), expectedLotAvailable, "[ItemService] Product stock lot must be '%s' but found '%s', productId: %d".formatted(expectedLotAvailable, productInfo.isLotAvailable(), productId));

                boolean expectedExpiredQuality = ((!beforeLot.get(productIndex) && expectedLotAvailable) && isExpiredQuality) || (beforeLot.get(productIndex) && beforeExpiredQuality.get(productIndex));
                Assert.assertEquals(productInfo.isExpiredQuality(), expectedExpiredQuality, "[ItemService] Product expired quality must be '%s' but found '%s', productId: %d".formatted(expectedExpiredQuality, productInfo.isExpiredQuality(), productId));
            }
            logger.info("Verify product lot, productId: {}", productId);
        });
        logger.info("Check product stock lot and expired quality after bulk actions: MANAGE STOCK BY LOT DATE.");
    }

    /**
     * Performs a bulk update of selected products on the product management page
     * and verifies the product information after the update.
     *
     * <p>This method provides various bulk operations such as clearing stock,
     * deleting products, activating or deactivating products, updating stock,
     * and managing prices. Each action corresponds to a specific index.</p>
     *
     * @param actionIndex The index representing the bulk action to be performed.
     *                    Valid values are:
     *                    <ul>
     *                      <li>0 - Clear stock</li>
     *                      <li>1 - Delete products</li>
     *                      <li>2 - Deactivate product</li>
     *                      <li>3 - Activate product</li>
     *                      <li>4 - Update stock</li>
     *                      <li>5 - Update tax</li>
     *                      <li>6 - Display out of stock products</li>
     *                      <li>7 - Update selling platform</li>
     *                      <li>8 - Update price</li>
     *                      <li>9 - Set stock alert</li>
     *                      <li>10 - Manage stock by lot date</li>
     *                    </ul>
     *
     * @throws IllegalArgumentException if the actionIndex is out of range.
     */
    public void bulkUpdateAndVerifyProducts(int actionIndex) {
        switch (actionIndex) {
            case 0 -> bulkClearStock();
            case 1 -> bulkDeleteProducts();
            case 2 -> bulkDeactivateProduct();
            case 3 -> bulkActivateProduct();
            case 4 -> bulkUpdateStock();
            case 5 -> bulkUpdateTax();
            case 6 -> bulkDisplayOutOfStockProduct();
            case 7 -> bulkUpdateSellingPlatform();
            case 8 -> bulkUpdatePrice();
            case 9 -> bulkSetStockAlert();
            case 10 -> bulkManageStockByLotDate();
            default -> throw new IllegalArgumentException("Invalid action index: " + actionIndex);
        }
    }
}
