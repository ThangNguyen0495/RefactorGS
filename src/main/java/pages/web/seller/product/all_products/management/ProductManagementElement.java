package pages.web.seller.product.all_products.management;

import lombok.Getter;
import org.openqa.selenium.By;

public class ProductManagementElement {
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
}
