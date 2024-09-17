package pages.web.seller.product.all_products;

import api.seller.login.APIDashboardLogin;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import utility.WebUtils;

public class ProductManagementPage  {
    Logger logger = LogManager.getLogger();
    WebDriver driver;
    APIDashboardLogin.Credentials staffLoginInformation;
    APIDashboardLogin.Credentials sellerLoginInformation;
    WebUtils webUtils;

    public ProductManagementPage(WebDriver driver) {
        this.driver = driver;
        webUtils = new WebUtils(driver);
    }

    By loc_lblProductId = By.cssSelector("tr [class='gs-table-body-item'] strong");
    @Getter
    By loc_btnCreateProduct = By.xpath("(//*[contains(@class, 'gs-button__green gs-button--undefined')])[1]");
    By loc_btnExport = By.xpath("(//*[contains(@class, 'gs-button__green gs-button--undefined')])[2]");
    /**
     * 0: Export all products
     * 1: Export wholesale products
     * 2: Export history
     */
    By loc_ddlExportActions = By.xpath("(//*[contains(@class, 'gs-button__green gs-button--undefined')])[2]/following-sibling::div/button");
    By loc_dlgExportProductListingFile_btnExport = By.cssSelector(".modal-footer .gs-button__green");
    By loc_btnImport = By.xpath("(//*[contains(@class, 'gs-button__green gs-button--undefined')])[3]");
    By loc_ddlImportActions = By.xpath("(//*[contains(@class, 'gs-button__green gs-button--undefined')])[3]/following-sibling::div/button");
    By loc_dlgImport = By.cssSelector(".item-list-import-modal");
    String str_dlgImport_chkBranch = "//span[contains(text(), '%s')]/parent::div/preceding-sibling::input";
    By loc_dlgImport_btnDragAndDrop = By.cssSelector(".item-list-import-modal__drop-zone input");
    By loc_dlgImport_btnImport = By.cssSelector(".item-list-import-modal .gs-button__green");
    By loc_dlgImport_btnCancel = By.cssSelector(".item-list-import-modal .gs-button__white");
    By loc_prgStatus = By.xpath("//*[contains(@class, 'uik-widget-table__wrapper')]/preceding-sibling::div[1]/span");
    By loc_btnPrintBarcode = By.cssSelector(".gs-button__green--outline");
    By loc_dlgPrintBarcode = By.cssSelector(".product-list-barcode-printer");
    By loc_dlgPrintBarcode_btnCancel = By.cssSelector(".product-list-barcode-printer .gs-button__gray--outline");
    By loc_chkSelectAll = By.cssSelector("thead input");
    By loc_lnkSelectAction = By.cssSelector(".actions");
    By loc_icnDownloadExportFile = By.xpath("//*[contains(@class, 'd-desktop-flex')]//*[contains(text(), 'EXPORT_PRODUCT')]//following-sibling::div/div/img[@alt='download-file-blue']");
    /**
     * 0: Clear stock
     * 1: Delete
     * 2: Deactivate
     * 3: Activate
     * 4: Update stock
     * 5: Update Tax
     * 6: Display out of stock
     * 7: Update selling platform
     * 8: Update price
     * 9: Set stock alert
     * 10: Manage stock by Lot-date
     */
    By loc_ddlListActions = By.cssSelector(".actions > div");
    By loc_dlgClearStock = By.cssSelector(".modalClearStock");
    By loc_dlgClearStock_btnOK = By.cssSelector(".modalClearStock .gs-button__green");
    By loc_dlgDeleteProduct = By.cssSelector(".modalDeleteProduct");
    By loc_dlgDeleteProduct_btnDelete = By.cssSelector(".modalDeleteProduct .gs-button__red");
    By loc_dlgActiveProduct = By.cssSelector(".modalActivateProduct");
    By loc_dlgActiveProduct_btnYes = By.cssSelector(".modalActivateProduct .gs-button__green");
    By loc_dlgDeactivateProduct = By.cssSelector(".modalActivateProduct");
    By loc_dlgDeactivateProduct_btnYes = By.cssSelector(".modalActivateProduct .gs-button__green");
    By loc_dlgUpdateStock = By.cssSelector(".product-multiple-stock_updater_modal");
    By loc_dlgUpdateStock_ddvSelectedBranch = By.cssSelector(".product-multiple-stock_updater_modal .uik-select__valueWrapper");
    By loc_dlgUpdateStock_actionsChange = By.cssSelector("[class *= 'gs-button__blue']:nth-child(2)");
    By loc_dlgUpdateStock_txtStockValue = By.xpath("//*[@name='quantity']/parent::div/parent::div/preceding-sibling::input");
    By loc_dlgUpdateStock_btnUpdate = By.cssSelector(".product-multiple-stock_updater_modal .gs-button__green");
    By loc_dlgUpdateTax = By.cssSelector(".modalActivateProduct");
    By loc_dlgUpdateTax_ddlTaxOptions = By.cssSelector("input[name='taxRadioGroup']");
    By loc_dlgUpdateTax_btnOK = By.cssSelector(".modalActivateProduct .gs-button__green");
    By loc_dlgDisplayOutOfStockProduct = By.cssSelector(".modalActivateProduct");
    By loc_dlgDisplayOutOfStockProduct_listOptions = By.cssSelector("input[name='productRadioGroup']");
    By loc_dlgDisplayOutOfStockProduct_btnYes = By.cssSelector(".modalActivateProduct .gs-button__green");
    By loc_dlgUpdateSellingPlatform = By.cssSelector(".modalPlatformProduct");
    By loc_dlgUpdateSellingPlatform_chkApp = By.cssSelector("#onApp");
    By loc_dlgUpdateSellingPlatform_chkWeb = By.cssSelector("#onWeb");
    By loc_dlgUpdateSellingPlatform_chkInStore = By.cssSelector("#inStore");
    By loc_dlgUpdateSellingPlatform_chkGoSocial = By.cssSelector("#inGosocial");
    By loc_dlgUpdateSellingPlatform_btnConfirm = By.cssSelector(".modalPlatformProduct .gs-button__green");
    By loc_dlgUpdatePrice = By.cssSelector("#multi-price .modal-content");
    By loc_dlgUpdatePrice_ddvSelectedPriceType = By.cssSelector(".modal-body .uik-select__arrowWrapper");
    /**
     * 0: Listing price
     * 1: Selling price
     * 2: Cost price
     */
    By loc_dlgUpdatePrice_ddlPriceType = By.cssSelector(".modal-body .uik-select__optionList > .uik-select__option");
    By loc_dlgUpdatePrice_txtApplyAll = By.xpath("//*[@id='apply-all']/parent::div/parent::div//preceding-sibling::input");
    By loc_dlgUpdatePrice_btnApplyAll = By.cssSelector(".modal-body .gs-button__blue");
    By loc_dlgUpdatePrice_txtCostPrice = By.xpath("//*[contains(@name,'costPrice')]//parent::div//parent::div//preceding-sibling::input");
    By loc_dlgUpdatePrice_btnUpdate = By.cssSelector(".modal-footer .gs-button__green");
    By loc_dlgUpdatePrice_btnClose = By.cssSelector(".modal-footer .gs-button__gray--outline");
    By loc_dlgSetStockAlert = By.cssSelector(".product-multiple-stock_alert_updater_modal");
    By loc_dlgSetStockAlert_txtStockAlertValueForAllProducts = By.cssSelector("#stock-alert-all");
    By loc_dlgSetStockAlert_btnApply = By.cssSelector(".product-multiple-stock_alert_updater_modal .gs-button__blue");
    By loc_dlgSetStockAlert_btnUpdate = By.cssSelector(".product-multiple-stock_alert_updater_modal .gs-button__green");
    By loc_dlgConfirm_icnClose = By.cssSelector(".modal.fade.show .close");
    By loc_dlgManageProductByLotDate = By.cssSelector(".bulk-update-lot-type-modal");
    By loc_dlgManageProductByLotDate_chkExcludeExpireQuantity = By.cssSelector("#expiredQuantity");
    By loc_dlgManageProductByLotDate_btnYes = By.cssSelector(".bulk-update-lot-type-modal .gs-button__green");

//
//    public ProductManagementPage getCredentials(LoginInformation sellerLoginInformation) {
//        this.sellerLoginInformation = sellerLoginInformation;
//        return this;
//    }
//
//    public ProductManagementPage getCredentials(LoginInformation sellerLoginInformation, LoginInformation staffLoginInformation) {
//        this.sellerLoginInformation = sellerLoginInformation;
//        this.staffLoginInformation = staffLoginInformation;
//        productPage = new ProductPage(driver);
//        return this;
//    }
//
//    void navigateToProductManagementPage() {
//        driver.get("%s/product/list".formatted(DOMAIN));
//        driver.navigate().refresh();
//        logger.info("Navigate to product management page.");
//    }
//
//    enum BulkActions {
//        clearStock, delete, deactivate, active, updateStock, updateTax, displayOutOfStock, updateSellingPlatform, updatePrice, setStockAlert, manageStockByLotDate;
//
//        static List<BulkActions> bulkActionsValues() {
//            return new ArrayList<>(Arrays.asList(values()));
//        }
//    }
//
//    void openBulkActionsDropdown() {
//        if (commonAction.getListElement(loc_lblProductId).isEmpty())
//            driver.navigate().refresh();
//        if (!commonAction.isCheckedJS(loc_chkSelectAll)) {
//            commonAction.clickJS(loc_chkSelectAll);
//        }
//        commonAction.clickJS(loc_lnkSelectAction);
//    }
//
//    void exportAllProducts() {
//        navigateToProductManagementPage();
//        commonAction.clickJS(loc_btnExport);
//        commonAction.clickJS(loc_ddlExportActions, 0);
//        commonAction.clickJS(loc_dlgExportProductListingFile_btnExport);
//        logger.info("Export all products.");
//    }
//
//    void exportWholesaleProducts() {
//        navigateToProductManagementPage();
//        commonAction.clickJS(loc_btnExport);
//        if (!commonAction.getListElement(loc_ddlExportActions).isEmpty()) {
//            commonAction.clickJS(loc_ddlExportActions, 1);
//            logger.info("Export wholesale products.");
//        }
//    }
//
//    void navigateToDownloadHistoryPage() {
//        driver.get(DOMAIN + "/product/export-history");
//        driver.navigate().refresh();
//        logger.info("Navigate to download export product history page.");
//
//    }
//
//    void importProduct() {
//        // open list import actions
//        commonAction.clickJS(loc_btnImport);
//
//        // open import product popup
//        commonAction.clickJS(loc_ddlImportActions, 0);
//
//        // select branch
//        String branchName = new Login().getInfo(staffLoginInformation).getAssignedBranchesNames().get(0);
//        commonAction.clickJS(By.xpath(str_dlgImport_chkBranch.formatted(branchName)));
//
//        // check import product is opened or not
//        assertCustomize.assertFalse(commonAction.getListElement(loc_dlgImport).isEmpty(), "Can not open import popup");
//
//        // upload file
//        if (!commonAction.getListElement(loc_dlgImport).isEmpty()) {
//            commonAction.uploads(loc_dlgImport_btnDragAndDrop, new DataGenerator().getPathOfFileInResourcesRoot("import_product.xlsx"));
//
//            // complete import product
//            assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_dlgImport_btnImport, loc_prgStatus),
//                    "Can not import product.");
//        }
//    }
//
//    void applyAll(long price, int typeIndex) {
//        commonAction.sendKeys(loc_dlgUpdatePrice_txtApplyAll, String.valueOf(price));
//        commonAction.openDropdownJS(loc_dlgUpdatePrice_ddvSelectedPriceType, loc_dlgUpdatePrice_ddlPriceType);
//        commonAction.clickJS(loc_dlgUpdatePrice_ddlPriceType, typeIndex);
//        commonAction.click(loc_dlgUpdatePrice_btnApplyAll);
//    }
//
//    enum PriceType {
//        listing, selling, cost;
//
//        static List<PriceType> getAllPriceTypes() {
//            return Arrays.stream(values()).toList();
//        }
//    }
//
//    void bulkActionsUpdatePrice(long listingPrice, long sellingPrice, long costPrice) {
//        // bulk actions
//        openBulkActionsDropdown();
//
//        // open update price popup
//        commonAction.openPopupJS(loc_ddlListActions, bulkActionsValues().indexOf(updatePrice), loc_dlgUpdatePrice);
//
//        // input listing price
//
//        applyAll(listingPrice, getAllPriceTypes().indexOf(listing));
//
//        // input selling price
//        applyAll(sellingPrice, getAllPriceTypes().indexOf(selling));
//
//        // input cost price
//        if (permissions.getProduct().getProductManagement().isViewProductCostPrice()) {
//            applyAll(costPrice, getAllPriceTypes().indexOf(cost));
//        } else {
//            // view cost price
//            assertCustomize.assertTrue(commonAction.getValue(loc_dlgUpdatePrice_txtCostPrice, 0).equals("0"), "Product cost price still shows when staff does not have 'View product cost price' permission.");
//        }
//
//        // complete update price
//        commonAction.click(loc_dlgUpdatePrice_btnUpdate);
//    }
//
//    /* Check bulk actions */
//    List<String> getAllProductIdIn1stPage() {
//        // navigate to product management page
//        navigateToProductManagementPage();
//
//        // if page is not loaded, refresh page
//        if (commonAction.getListElement(loc_lblProductId).isEmpty()) {
//            driver.navigate().refresh();
//        }
//
//        // get number of products in 1st page
//        int bound = commonAction.getListElement(loc_lblProductId).size();
//
//        // return list productId
//        return IntStream.range(0, bound).mapToObj(index -> commonAction.getText(loc_lblProductId, index)).toList();
//    }
//
//    void waitUpdated() {
//        if (!commonAction.getListElement(loc_prgStatus).isEmpty()) {
//            driver.navigate().refresh();
//            logger.info("Wait bulk update.");
//            waitUpdated();
//        }
//    }
//
//    // bulk clear stock
//    public void bulkClearStock() {
//        // get list product need to updated
//        List<String> productIds = getAllProductIdIn1stPage();
//
//        // get before update stock in item-service
//        APIProductDetail productInformation = new APIProductDetail(sellerLoginInformation);
//        Map<String, List<Integer>> beforeUpdateStocksInItemService = productInformation.getCurrentProductStocksMap(productIds);
//
//        // get before update stock in ES
//        APIAllProducts allProducts = new APIAllProducts(sellerLoginInformation);
//        Map<String, Integer> beforeUpdateStocksInES = allProducts.getCurrentStocks(productIds);
//
//        // log
//        logger.info("Wait get product stock before clear stock.");
//
//        // open bulk actions dropdown
//        openBulkActionsDropdown();
//
//        // open confirm active popup
//        commonAction.openPopupJS(loc_ddlListActions, bulkActionsValues().indexOf(clearStock), loc_dlgClearStock);
//
//        // confirm clear stock
//        commonAction.click(loc_dlgClearStock_btnOK);
//
//        /* Do not need to wait product updated because calculate function needs ~ 2 minutes, that time is enough for product to be updated.*/
//        // check product stock are updated on item-service
//        List<Integer> expectedStockOnItemService = new ArrayList<>(productInformation.getExpectedListProductStockQuantityAfterClearStock(productIds, beforeUpdateStocksInItemService));
//        Collections.sort(expectedStockOnItemService);
//        List<Integer> actualStockOnItemService = new ArrayList<>(productInformation.getCurrentStockOfProducts(productIds));
//        Collections.sort(actualStockOnItemService);
//        assertCustomize.assertEquals(expectedStockOnItemService, actualStockOnItemService,
//                "Product stock are not updated on item-service, , stock must be %s, but found %s.".formatted(actualStockOnItemService.toString(), expectedStockOnItemService.toString()));
//        logger.info("Check product stock on item-service after clearing stock.");
//
//        // check product stock are updated on ES
//        List<Integer> expectedStockOnES = new ArrayList<>(allProducts.getExpectedListProductStockQuantityAfterClearStock(productIds, beforeUpdateStocksInES));
//        Collections.sort(expectedStockOnES);
//        List<Integer> actualStockOnES = new ArrayList<>(allProducts.getListProductStockQuantityAfterClearStock(productIds));
//        Collections.sort(actualStockOnES);
//        assertCustomize.assertEquals(expectedStockOnES, actualStockOnES,
//                "Product stock are not updated on ES, stock must be %s, but found %s.".formatted(actualStockOnES.toString(), expectedStockOnES.toString()));
//        logger.info("Check product stock on ES after clearing stock.");
//
//        // log
//        logger.info("Check product status after bulk actions: CLEAR STOCK.");
//
//        // verify test
//        AssertCustomize.verifyTest();
//    }
//
//
//    // bulk delete
//    public List<Boolean> checkProductCanBeDeleted(List<String> productIds) {
//        List<Integer> listProductIdThatInInCompleteTransfer = new APIInventoryHistory(sellerLoginInformation).listOfCanNotBeDeletedProductIds(productIds);
//        return productIds.stream().map(productId -> !listProductIdThatInInCompleteTransfer.contains(Integer.parseInt(productId))).toList();
//    }
//
//    public void bulkDeleteProduct() {
//        // get list product need to updated
//        List<String> productIds = getAllProductIdIn1stPage();
//
//        // open bulk actions dropdown
//        openBulkActionsDropdown();
//
//        // open confirm active popup
//        commonAction.openPopupJS(loc_ddlListActions, bulkActionsValues().indexOf(delete), loc_dlgDeleteProduct);
//
//        // confirm active product
//        commonAction.click(loc_dlgDeleteProduct_btnDelete);
//
//        // check actions are completed or not
//        if (!commonAction.getListElement(loc_prgStatus).isEmpty()) {
//            // wait updated
//            waitUpdated();
//
//            // check product must be deleted in ES
//            List<Integer> currentProductIds = new APIAllProducts(sellerLoginInformation).getListProductId();
//            List<Boolean> checkProductCanBeDeleted = checkProductCanBeDeleted(productIds);
//            assertCustomize.assertTrue(IntStream.range(0, productIds.size())
//                            .noneMatch(index -> (currentProductIds.contains(Integer.parseInt(productIds.get(index))) && checkProductCanBeDeleted.get(index))
//                                    || (!currentProductIds.contains(Integer.parseInt(productIds.get(index))) && !checkProductCanBeDeleted.get(index))),
//                    "Product is not deleted in ES.");
//
//            // check product must be deleted in item-service
//            List<Boolean> isDeleted = new APIProductDetail(sellerLoginInformation).isDeleted(productIds);
//            assertCustomize.assertEquals(checkProductCanBeDeleted, isDeleted, "Product is not deleted in item-service.");
//
//            // log
//            logger.info("Check product list after bulk actions: DELETE.");
//        } else logger.error("Can not bulk actions DELETE product.");
//
//        // verify test
//        AssertCustomize.verifyTest();
//    }
//
//    // bulk deactivate
//    public void bulkDeactivateProduct() {
//        // get list product need to updated
//        List<String> productIds = getAllProductIdIn1stPage();
//
//        // open bulk actions dropdown
//        openBulkActionsDropdown();
//
//        // open confirm deactivate popup
//        commonAction.openPopupJS(loc_ddlListActions, bulkActionsValues().indexOf(deactivate), loc_dlgDeactivateProduct);
//
//        // confirm active product
//        commonAction.click(loc_dlgDeactivateProduct_btnYes);
//
//        // check actions are completed or not
//        if (!commonAction.getListElement(loc_prgStatus).isEmpty()) {
//            // wait updated
//            waitUpdated();
//
//            // check product status after updating
//            assertCustomize.assertTrue(new APIProductDetail(sellerLoginInformation)
//                            .getListProductStatus(productIds)
//                            .stream()
//                            .allMatch(status -> status.equals("INACTIVE")),
//                    "All selected products must be DEACTIVATE, but some product is not updated.");
//
//            // log
//            logger.info("Check product status after bulk actions: DEACTIVATE.");
//        } else logger.error("Can not bulk actions ACTIVE product.");
//
//        // verify test
//        AssertCustomize.verifyTest();
//    }
//
//    // bulk active
//    public void bulkActivateProduct() {
//        // get list product need to updated
//        List<String> productIds = getAllProductIdIn1stPage();
//
//        // open bulk actions dropdown
//        openBulkActionsDropdown();
//
//        // open confirm active popup
//        commonAction.openPopupJS(loc_ddlListActions, bulkActionsValues().indexOf(active), loc_dlgActiveProduct);
//
//        // confirm active product
//        commonAction.click(loc_dlgActiveProduct_btnYes);
//
//        // check actions are completed or not
//        if (!commonAction.getListElement(loc_prgStatus).isEmpty()) {
//            // wait updated
//            waitUpdated();
//
//            // check product in product management
//            assertCustomize.assertTrue(new APIProductDetail(sellerLoginInformation)
//                            .getListProductStatus(productIds)
//                            .stream()
//                            .allMatch(status -> status.equals("ACTIVE")),
//                    "All selected products must be ACTIVE, but some product is not updated.");
//
//            // log
//            logger.info("Check product status after bulk actions: ACTIVATE.");
//        } else logger.error("Can not bulk actions ACTIVATE product.");
//
//        // verify test
//        AssertCustomize.verifyTest();
//    }
//
//    // bulk update stock
//    public void bulkUpdateStock() {
//        // get list product need to updated
//        List<String> productIds = getAllProductIdIn1stPage();
//
//        // get before update stock in item-service
//        APIProductDetail productInformation = new APIProductDetail(sellerLoginInformation);
//        Map<String, List<Integer>> beforeUpdateStocksInItemService = productInformation.getCurrentProductStocksMap(productIds);
//
//        // get before update stock in ES
//        APIAllProducts allProducts = new APIAllProducts(sellerLoginInformation);
//        int branchId = new BranchManagement(sellerLoginInformation).getInfo().getBranchID().get(0);
//        Map<String, Integer> beforeUpdateStocksInES = allProducts.getCurrentStocks(productIds, branchId);
//
//        // log
//        logger.info("Wait get product stock before update stock.");
//
//        // open bulk actions dropdown
//        openBulkActionsDropdown();
//
//        // open confirm active popup
//        commonAction.openPopupJS(loc_ddlListActions, bulkActionsValues().indexOf(updateStock), loc_dlgUpdateStock);
//
//        // select change actions
//        commonAction.click(loc_dlgUpdateStock_actionsChange);
//
//        // input stock value
//        int stock = nextInt(MAX_STOCK_QUANTITY);
//        commonAction.sendKeys(loc_dlgUpdateStock_txtStockValue, String.valueOf(stock));
//        logger.info("Input stock value: %,d.".formatted(stock));
//
//        // confirm update stock
//        commonAction.click(loc_dlgUpdateStock_btnUpdate);
//
//        /* Do not need to wait product updated because calculate function needs ~ 2 minutes, that time is enough for product to be updated.*/
//        // check product stock are updated on item-service
//        List<Integer> expectedStockOnItemService = new ArrayList<>(productInformation.getExpectedListProductStockQuantityAfterUpdateStock(productIds, branchId, beforeUpdateStocksInItemService, stock));
//        Collections.sort(expectedStockOnItemService);
//        List<Integer> actualStockOnItemService = new ArrayList<>(productInformation.getCurrentStockOfProducts(productIds));
//        Collections.sort(actualStockOnItemService);
//        assertCustomize.assertEquals(expectedStockOnItemService, actualStockOnItemService,
//                "Product stock are not updated on item-service, , stock must be %s, but found %s.".formatted(actualStockOnItemService.toString(), expectedStockOnItemService.toString()));
//        logger.info("Check product stock on item-service after updating stock.");
//
//        // check product stock are updated on ES
//        List<Integer> expectedStockOnES = new ArrayList<>(allProducts.getExpectedListProductStockQuantityAfterUpdateStock(productIds, beforeUpdateStocksInES, stock));
//        Collections.sort(expectedStockOnES);
//        List<Integer> actualStockOnES = new ArrayList<>(allProducts.getListProductStockQuantityAfterUpdateStock(productIds, branchId));
//        Collections.sort(actualStockOnES);
//        assertCustomize.assertEquals(expectedStockOnES, actualStockOnES,
//                "Product stock are not updated on ES, stock must be %s, but found %s.".formatted(actualStockOnES.toString(), expectedStockOnES.toString()));
//        logger.info("Check product stock on ES after updating stock.");
//
//        // log
//        logger.info("Check product status after bulk actions: UPDATE STOCK.");
//
//        // verify test
//        AssertCustomize.verifyTest();
//    }
//
//
//    // bulk update tax
//    public void bulkUpdateTax() {
//        // get list product need to updated
//        List<String> productIds = getAllProductIdIn1stPage();
//
//        // open bulk actions dropdown
//        openBulkActionsDropdown();
//
//        // open confirm deactivate popup
//        commonAction.openPopupJS(loc_ddlListActions, bulkActionsValues().indexOf(updateTax), loc_dlgUpdateTax);
//
//        // get taxId
//        int bound = commonAction.getListElement(loc_dlgUpdateTax_ddlTaxOptions).size();
//        int taxIndex = nextInt(bound);
//        int newTaxId = Integer.parseInt(commonAction.getValue(loc_dlgUpdateTax_ddlTaxOptions, taxIndex));
//        commonAction.clickJS(loc_dlgUpdateTax_ddlTaxOptions, taxIndex);
//        logger.info("Bulk actions update tax: %d.".formatted(newTaxId));
//
//        // confirm active product
//        commonAction.click(loc_dlgUpdateTax_btnOK);
//
//        // check actions are completed or not
//        if (!commonAction.getListElement(loc_prgStatus).isEmpty()) {
//            // wait updated
//            waitUpdated();
//
//            // check product status after updating
//            APIProductDetail productInformation = new APIProductDetail(sellerLoginInformation);
//            List<Integer> taxList = productInformation.getListProductTaxId(productIds);
//            assertCustomize.assertTrue(IntStream.range(0, taxList.size())
//                            .allMatch(index -> taxList.get(index) == newTaxId),
//                    "Tax of selected products must be %s.".formatted(taxList.toString()));
//
//            // log
//            logger.info("Check product taxId after bulk actions: UPDATE TAX.");
//        } else logger.error("Can not bulk actions Update Tax product.");
//
//        // verify test
//        AssertCustomize.verifyTest();
//    }
//
//    enum DisplayOutOfStockActions {
//        displayWhenOutOfStock, doNotDisplayWhenOutOfStock;
//
//        static List<DisplayOutOfStockActions> displayOutOfStockActions() {
//            return new ArrayList<>(Arrays.asList(values()));
//        }
//    }
//
//    void bulkDisplayOutOfStock(DisplayOutOfStockActions displayOutOfStockActions) {
//        // open bulk actions dropdown
//        openBulkActionsDropdown();
//
//        // open display out of stock popup
//        commonAction.openPopupJS(loc_ddlListActions,
//                bulkActionsValues().indexOf(displayOutOfStock),
//                loc_dlgDisplayOutOfStockProduct);
//
//        // select option
//        commonAction.clickJS(loc_dlgDisplayOutOfStockProduct_listOptions,
//                displayOutOfStockActions().indexOf(displayOutOfStockActions));
//
//        // confirm bulk display when out of stock product
//        commonAction.click(loc_dlgDisplayOutOfStockProduct_btnYes);
//    }
//
//    public void bulkDisplayOutOfStockProduct() {
//        // get list product need to updated
//        List<String> productIds = getAllProductIdIn1stPage();
//
//        // bulk do not display when out of stock
//        bulkDisplayOutOfStock(doNotDisplayWhenOutOfStock);
//
//        // wait updated
//        waitUpdated();
//
//        // check product display after updating
//        APIProductDetail productInformation = new APIProductDetail(sellerLoginInformation);
//        List<Boolean> showOutOfStock = productInformation.getDisplayWhenOutOfStock(productIds);
//        assertCustomize.assertTrue(IntStream.range(0, showOutOfStock.size())
//                        .noneMatch(showOutOfStock::get),
//                "Display when out of selected products must be %s.".formatted(showOutOfStock.toString()));
//
//        // log
//        logger.info("Check product taxId after bulk actions: DO NOT DISPLAY OUT OF STOCK PRODUCT.");
//
//        // bulk display when out of stock
//        bulkDisplayOutOfStock(displayWhenOutOfStock);
//
//        // wait updated
//        waitUpdated();
//
//        // check product display after updating
//        showOutOfStock = productInformation.getDisplayWhenOutOfStock(productIds);
//        assertCustomize.assertTrue(IntStream.range(0, showOutOfStock.size())
//                        .allMatch(showOutOfStock::get),
//                "Display when out of selected products must be %s.".formatted(showOutOfStock.toString()));
//        // log
//        logger.info("Check product taxId after bulk actions: DISPLAY OUT OF STOCK PRODUCT.");
//
//        // verify test
//        AssertCustomize.verifyTest();
//    }
//
//    public void bulkUpdateSellingPlatform() {
//        // get list product need to updated
//        List<String> productIds = getAllProductIdIn1stPage();
//
//        // open bulk actions dropdown
//        openBulkActionsDropdown();
//
//        // open display out of stock popup
//        commonAction.openPopupJS(loc_ddlListActions,
//                bulkActionsValues().indexOf(updateSellingPlatform),
//                loc_dlgUpdateSellingPlatform);
//
//        // update selling platform
//        boolean onApp = !commonAction.isDisabledJS(loc_dlgUpdateSellingPlatform_chkApp) && nextBoolean();
//        if (commonAction.isCheckedJS(loc_dlgUpdateSellingPlatform_chkApp) != onApp)
//            commonAction.clickJS(loc_dlgUpdateSellingPlatform_chkApp);
//        logger.info("onApp: %s.".formatted(onApp));
//
//        boolean onWeb = !commonAction.isDisabledJS(loc_dlgUpdateSellingPlatform_chkWeb) && nextBoolean();
//        if (commonAction.isCheckedJS(loc_dlgUpdateSellingPlatform_chkWeb) != onWeb)
//            commonAction.clickJS(loc_dlgUpdateSellingPlatform_chkWeb);
//        logger.info("onWeb: %s.".formatted(onWeb));
//
//        boolean inStore = !commonAction.isDisabledJS(loc_dlgUpdateSellingPlatform_chkInStore) && nextBoolean();
//        if (commonAction.isCheckedJS(loc_dlgUpdateSellingPlatform_chkInStore) != inStore)
//            commonAction.clickJS(loc_dlgUpdateSellingPlatform_chkInStore);
//        logger.info("inStore: %s.".formatted(inStore));
//
//        boolean inGoSocial = !commonAction.isDisabledJS(loc_dlgUpdateSellingPlatform_chkGoSocial) && nextBoolean();
//        if (commonAction.isCheckedJS(loc_dlgUpdateSellingPlatform_chkGoSocial) != inGoSocial)
//            commonAction.clickJS(loc_dlgUpdateSellingPlatform_chkGoSocial);
//        logger.info("inGoSocial: %s.".formatted(inGoSocial));
//
//        // confirm bulk update selling platforms
//        commonAction.click(loc_dlgUpdateSellingPlatform_btnConfirm);
//
//        // wait updated
//        waitUpdated();
//
//        // check product display after updating
//        Map<String, List<Boolean>> sellingPlatforms = new APIProductDetail(sellerLoginInformation).getMapOfListSellingPlatform(productIds);
//        assertCustomize.assertTrue(new ArrayList<>(sellingPlatforms.get("onWeb")).stream()
//                        .allMatch(webPlatform -> webPlatform == onWeb),
//                "Web platform of selected products must be %s, but found %s.".formatted(onWeb, sellingPlatforms.get("onWeb")));
//
//        assertCustomize.assertTrue(new ArrayList<>(sellingPlatforms.get("onApp")).stream()
//                        .allMatch(appPlatform -> appPlatform == onApp),
//                "App platform of selected products must be %s, but found %s.".formatted(onApp, sellingPlatforms.get("onApp")));
//
//        assertCustomize.assertTrue(new ArrayList<>(sellingPlatforms.get("inStore")).stream()
//                        .allMatch(inStorePlatform -> inStorePlatform == inStore),
//                "In store platform of selected products must be %s, but found %s.".formatted(inStore, sellingPlatforms.get("inStore")));
//
//        assertCustomize.assertTrue(new ArrayList<>(sellingPlatforms.get("inGoSocial")).stream()
//                        .allMatch(inGoSocialPlatform -> inGoSocialPlatform == inGoSocial),
//                "GoSocial platform of selected products must be %s, but found %s.".formatted(inGoSocial, sellingPlatforms.get("inGoSocial")));
//
//        // log
//        logger.info("Check product selling platform after bulk actions: UPDATE SELLING PLATFORM.");
//
//        // verify test
//        AssertCustomize.verifyTest();
//    }
//
//    public void bulkUpdatePrice() {
//        // get list product need to updated
//        List<String> productIds = getAllProductIdIn1stPage();
//
//        // bulk actions
//        openBulkActionsDropdown();
//
//        // open update price popup
//        commonAction.openPopupJS(loc_ddlListActions, bulkActionsValues().indexOf(updatePrice), loc_dlgUpdatePrice);
//
//        // get map of products price
//        APIProductDetail productInformation = new APIProductDetail(sellerLoginInformation);
//        Map<String, List<Long>> mapOfProductsPrice = productInformation.getMapOfCurrentProductsPrice(productIds);
//
//        // input listing price
//        long maxListingPrice = Collections.max(new ArrayList<>(mapOfProductsPrice.get("listingPrice")));
//        long listingPrice = maxListingPrice + nextLong(Math.max(MAX_PRICE - maxListingPrice, 1));
//        applyAll(listingPrice, getAllPriceTypes().indexOf(listing));
//        logger.info("Input listing price: %,d.".formatted(listingPrice));
//
//        // input selling price
//        long maxSellingPrice = Collections.max(new ArrayList<>(mapOfProductsPrice.get("listingPrice")));
//        long sellingPrice = maxSellingPrice + nextLong(Math.max(listingPrice - maxSellingPrice, 1));
//        applyAll(sellingPrice, getAllPriceTypes().indexOf(selling));
//        logger.info("Input selling price: %,d.".formatted(sellingPrice));
//
//        // input cost price
//        long minCostPrice = Collections.min(new ArrayList<>(mapOfProductsPrice.get("costPrice")));
//        long costPrice = nextLong(Math.max(minCostPrice, 1));
//        applyAll(costPrice, getAllPriceTypes().indexOf(cost));
//        logger.info("Input cost price: %,d.".formatted(costPrice));
//
//        // complete update price
//        commonAction.click(loc_dlgUpdatePrice_btnUpdate);
//
//        // wait updated
//        waitUpdated();
//
//        // check product display after updating
//        Map<String, List<Long>> mapOfActualPrice = productInformation.getMapOfCurrentProductsPrice(productIds);
//        Map<String, List<Long>> mapOfExpectedPrice = productInformation.getMapOfExpectedProductsPrice(productIds, listingPrice, sellingPrice, costPrice);
//
//        // check product listing price
//        List<Long> actualListingPrice = new ArrayList<>(mapOfActualPrice.get("listingPrice"));
//        Collections.sort(actualListingPrice);
//        List<Long> expectedListingPrice = new ArrayList<>(mapOfExpectedPrice.get("listingPrice"));
//        Collections.sort(expectedListingPrice);
//        assertCustomize.assertEquals(actualListingPrice, expectedListingPrice,
//                "Product listing price after updating must be %s, but found %s.".formatted(expectedListingPrice, actualListingPrice));
//
//        // check product selling price
//        List<Long> actualSellingPrice = new ArrayList<>(mapOfActualPrice.get("sellingPrice"));
//        Collections.sort(actualSellingPrice);
//        List<Long> expectedSellingPrice = new ArrayList<>(mapOfExpectedPrice.get("sellingPrice"));
//        Collections.sort(expectedSellingPrice);
//        assertCustomize.assertEquals(actualSellingPrice, expectedSellingPrice,
//                "Product selling price after updating must be %s, but found %s.".formatted(expectedSellingPrice, actualSellingPrice));
//
//        // check product cost price
//        List<Long> actualCostPrice = new ArrayList<>(mapOfActualPrice.get("costPrice"));
//        Collections.sort(actualCostPrice);
//        List<Long> expectedCostPrice = new ArrayList<>(mapOfExpectedPrice.get("costPrice"));
//        Collections.sort(expectedCostPrice);
//        assertCustomize.assertEquals(actualCostPrice, expectedCostPrice,
//                "Product cost price after updating must be %s, but found %s.".formatted(expectedCostPrice, actualCostPrice));
//
//        // log
//        logger.info("Check product price after bulk actions: UPDATE PRICE.");
//
//        // verify test
//        AssertCustomize.verifyTest();
//    }
//
//
//    public void bulkSetStockAlert() {
//        // get list product need to updated
//        List<String> productIds = getAllProductIdIn1stPage();
//
//        // open bulk actions dropdown
//        openBulkActionsDropdown();
//
//        // open set stock alert popup
//        commonAction.openPopupJS(loc_ddlListActions, bulkActionsValues().indexOf(setStockAlert), loc_dlgSetStockAlert);
//
//        // set new stock alert number
//        int stockAlertValue = nextInt(MAX_STOCK_QUANTITY);
//        commonAction.sendKeys(loc_dlgSetStockAlert_txtStockAlertValueForAllProducts, String.valueOf(stockAlertValue));
//        commonAction.click(loc_dlgSetStockAlert_btnApply);
//        logger.info("Bulk actions set stock alert: %d.".formatted(stockAlertValue));
//
//        // confirm update new stock alert value
//        commonAction.click(loc_dlgSetStockAlert_btnUpdate);
//
//        // check actions are completed or not
//        if (!commonAction.getListElement(loc_prgStatus).isEmpty()) {
//            // wait updated
//            waitUpdated();
//
//            // check product stock alert value after updating
//            APIProductDetail productInformation = new APIProductDetail(sellerLoginInformation);
//            List<Integer> stockAlert = productInformation.getListOfProductStockAlert(productIds);
//            stockAlert.forEach(alert -> assertCustomize.assertEquals(alert, stockAlertValue, "Stock alert must be '%,d' but found '%,d', index: %d".formatted(stockAlertValue, alert, stockAlert.indexOf(alert))));
//
//            // log
//            logger.info("Check product stock alert value after bulk actions: SET STOCK ALERT.");
//        } else logger.error("Can not bulk actions set stock alert.");
//
//        // verify test
//        AssertCustomize.verifyTest();
//    }
//
//    public void bulkManageStockByLotDate() {
//        // get list product need to updated
//        List<String> productIds = getAllProductIdIn1stPage();
//
//        // get current product lot date
//        APIProductDetail productInformation = new APIProductDetail(sellerLoginInformation);
//        Map<String, List<Boolean>> beforeUpdateLotDate = productInformation.getMapOfCurrentManageByLotDate(productIds);
//
//
//        // open bulk actions dropdown
//        openBulkActionsDropdown();
//
//        // open display out of stock popup
//        commonAction.openPopupJS(loc_ddlListActions, bulkActionsValues().indexOf(manageStockByLotDate), loc_dlgManageProductByLotDate);
//
//        // set expire
//        boolean expiredQuality = nextBoolean();
//        if (commonAction.isCheckedJS(loc_dlgManageProductByLotDate_chkExcludeExpireQuantity) != expiredQuality)
//            commonAction.clickJS(loc_dlgManageProductByLotDate_chkExcludeExpireQuantity);
//        logger.info("Exclude expired quantity from remaining stock: {}.", expiredQuality);
//
//        // confirm bulk update selling platforms
//        commonAction.click(loc_dlgManageProductByLotDate_btnYes);
//
//        // wait updated
//        waitUpdated();
//
//        // check product display after updating
//        Map<String, List<Boolean>> expectedMap = productInformation.getMapOfExpectedManageByLotDate(productIds, beforeUpdateLotDate, expiredQuality);
//        Map<String, List<Boolean>> actualMap = productInformation.getMapOfCurrentManageByLotDate(productIds);
//        assertCustomize.assertEquals(expectedMap, actualMap,
//                "Map of managed by lot date of selected products must be %s, but found %s.".formatted(expectedMap, actualMap));
//
//        // verify test
//        AssertCustomize.verifyTest();
//    }
}
