package pages.web.seller.product.all_products;

import org.openqa.selenium.By;

public class BaseProductElement {

    protected final By loc_txtProductName = By.cssSelector("input#productName");
    protected final By loc_txaProductDescription = By.cssSelector("div.fr-wrapper > div");
    protected final By loc_icnRemoveImages = By.cssSelector(".image-widget__btn-remove");
    protected final By loc_dlgAltText_btnCancel = By.cssSelector(".product-image .button__cancel-modal-image");
    protected final By imgUploads = By.xpath("//input[@type = 'file']");
    protected final By loc_ddvSelectedVAT = By.cssSelector(".form-group .uik-select__valueWrapper");
    protected final By loc_ddvNoVAT = By.xpath("//*[@class = 'uik-select__optionContent']/div[text()='Không áp dụng thuế'] | //*[@class = 'uik-select__optionContent']/div[text()='Tax does not apply']");

    protected By loc_ddvOthersVAT(String vatName) {
        return By.xpath("//*[@class = 'uik-select__optionContent']/div[text()='%s']".formatted(vatName));
    }

    protected final By loc_dlgUpdateSKU = By.cssSelector(".product-multiple-branch-sku_editor_modal");
    protected final By loc_txtWithoutVariationSKU = By.cssSelector("#productSKU,[class *=--n2] > div:nth-child(3) .align-items-center > span");
    protected final By loc_ddlManageInventory = By.cssSelector("#manageInventory");
    protected final By loc_ddvManageInventoryByIMEI = By.cssSelector("[value=\"IMEI_SERIAL_NUMBER\"]");
    protected final By loc_chkManageStockByLotDate = By.xpath("//*[@id='lotAvailable']//parent::div//preceding-sibling::label/input");
    protected final By loc_chkExcludeExpiredQuality = By.cssSelector(".box-expired-quality-product-detail label>input");
    protected final By loc_txtPriority = By.cssSelector("[name = productPriority]");
    protected final By loc_txtWeight = By.cssSelector("[for ='productWeight'] +* input");
    protected final By loc_txtLength = By.cssSelector("[for ='productLength'] +* input");
    protected final By loc_txtWidth = By.cssSelector("[for ='productWidth'] +* input");
    protected final By loc_txtHeight = By.cssSelector("[for ='productHeight'] +* input");
    protected final By loc_chkApp = By.cssSelector("[name = onApp]");
    protected final By loc_chkWeb = By.cssSelector("[name = onWeb]");
    protected final By loc_chkInStore = By.cssSelector("[name = inStore]");
    protected final By loc_chkGoSocial = By.cssSelector("[name = inGosocial]");
    protected final By loc_btnSave = By.cssSelector("[data-testid=\"desktop-saveBtn\"]");
    protected final By loc_btnDeactivate = By.xpath("(//*[text() = 'Ngừng bán' or text() = 'Deactivate' or text() = 'Bán ngay' or text() = 'Activate'])[1]/parent::div/parent::button");
    protected final By loc_btnDelete = By.xpath("(//*[text() = 'Xóa' or text() = 'Delete'])[1]/parent::div/parent::button");
    protected final By loc_dlgConfirmDelete_btnOK = By.xpath("//*[@data-testid=\"confirmBtn\"]");
    protected final By loc_dlgSuccessNotification = By.cssSelector(".modal-success");
    protected final By loc_dlgNotification_btnClose = By.cssSelector("[data-testid='closeBtn']");
    protected final By loc_btnAddAttribution = By.xpath("//span[text() = 'Add attribution' or text() = 'Thêm thuộc tính']");
    protected final By loc_icnDeleteAttribution = By.xpath("//tr[@class=\"attribute-item-row\"]//img");
    protected final By loc_txtAttributionName = By.cssSelector("[name *= 'input-attribute-name']");
    protected final By loc_txtAttributionValue = By.cssSelector("[id*= 'input-attribute-value']");
    protected final By loc_chkDisplayAttribute = By.xpath("//tr[@class=\"attribute-item-row\"]//input[@type=\"checkbox\"]");
    protected final By loc_txtSEOTitle = By.cssSelector("input#seoTitle");
    protected final By loc_txtSEODescription = By.cssSelector("input#seoDescription");
    protected final By loc_txtSEOKeywords = By.cssSelector("input#seoKeywords");
    protected final By loc_txtSEOUrl = By.cssSelector("input#seoUrl");
    protected final By loc_txtWithoutVariationListingPrice = By.xpath("(//*[@for ='productPrice'])[1]/following-sibling::div//input");
    protected final By loc_txtWithoutVariationSellingPrice = By.cssSelector("[for = 'productDiscountPrice'] +* input");
    protected final By loc_txtWithoutVariationCostPrice = By.xpath("(//*[@for ='productPrice'])[2]/following-sibling::div//input");
    protected final By loc_chkDisplayIfOutOfStock = By.xpath("//*[@name='showOutOfStock']/parent::div/preceding-sibling::label[2]/input");
    protected final By loc_chkHideRemainingStock = By.xpath("//*[@name='isHideStock']/parent::div/preceding-sibling::label[2]/input");

    protected final By loc_txtWithoutVariationBranchStock(String branchName) {
        return By.xpath("//div[div[text() = '%s']]//input".formatted(branchName));
    }

    protected final By loc_dlgAddIMEISelectedBranch = By.cssSelector(".modal-body .uik-select__valueWrapper");
    protected final By loc_dlgAddIMEI_chkSelectAllBranches = By.cssSelector(".managed-inventory-modal .uik-menuDrop__list > button:nth-child(1)  input");
    protected final By loc_dlgAddIMEI_icnDeleteIMEI = By.cssSelector(".code .fa-times");

    protected final By loc_dlgAddIMEI_txtAddIMEI(String branchName) {
        return By.xpath("//td[count(//th[text()='%s']/preceding-sibling::th)+1]//input[@name='serial']".formatted(branchName));
    }

    protected final By loc_dlgAddIMEI_btnSave = By.cssSelector(".modal-footer > .gs-button__green");
    protected final By loc_btnAddVariation = By.xpath("//span[text() = 'Thêm nhóm' or text() = 'Add variation']");
    protected final By loc_txtVariationName = By.cssSelector("div.first-item > div > div > input");
    protected final By loc_txtVariationValue = By.cssSelector(".second-item .box-input input");
    protected final By loc_tblVariation_chkSelectAll = By.cssSelector(".product-form-variation-selector__table  th:nth-child(1) input");
    protected final By loc_tblVariation_lnkSelectAction = By.cssSelector("th .gs-fake-link");
    /**
     * <p>0: Update Price</p>
     * <p>1: Update Stock</p>
     * <p>2: Update SKU</p>
     * <p>3: Update Image</p>
     */
    protected final By loc_tblVariation_ddvActions = By.cssSelector(".uik-menuDrop__list > button");
    protected final By loc_tblVariation_txtStock = By.xpath("//*[contains(@name, 'stock')]|//td[div/input[contains(@name, 'barcode')]]/preceding::td[3]/span");
    protected final By loc_tblVariation_txtSKU = By.xpath("//*[contains(@name,'sku')]|//td[div/input[contains(@name, 'barcode')]]/preceding::td[1]/span");

    protected final By loc_dlgUpdateSKU_txtInputSKU(String branchName) { return By.xpath("//td[text() = '%s']/following-sibling::td//input | //tbody/tr//td[count(//*[text() = '%s']/preceding-sibling::th) + 1]/div/input".formatted(branchName, branchName));}

    protected final By loc_tblVariation_imgUploads = By.cssSelector("td > img");
    protected final By loc_dlgUploadsImage_btnUploads = By.cssSelector(".modal-content [type = file]");
    protected final By loc_dlgUpdatePrice_txtListingPrice = By.xpath("//*[contains(@class, 'product-variation-price-editor-modal__table')]//*[contains(@name, 'orgPrice')]//parent::div//parent::div//preceding-sibling::input");
    protected final By loc_dlgUpdatePrice_txtSellingPrice = By.xpath("//*[contains(@class, 'product-variation-price-editor-modal__table')]//*[contains(@name, 'discountPrice')]//parent::div//parent::div//preceding-sibling::input");
    protected final By loc_dlgUpdatePrice_txtCostPrice = By.xpath("//*[contains(@class, 'product-variation-price-editor-modal__table')]//*[contains(@name, 'costPrice')]//parent::div//parent::div//preceding-sibling::input");
    protected final By loc_dlgCommons_btnUpdate = By.cssSelector(".modal-footer .gs-button__green");
    protected final By loc_dlgUpdateStock_ddvSelectedBranch = By.cssSelector(".gs-dropdown-multiple-select__drop-header .uik-select__valueWrapper");
    protected final By loc_dlgUpdateStock_chkSelectAllBranches = By.cssSelector(".modal-body .uik-menuDrop__list > button:nth-child(1)  input");
    protected final By loc_dlgUpdateStock_tabChange = By.cssSelector(".modal-body  div > div > .gs-button:nth-child(2)");
    protected final By loc_dlgUpdateStock_txtStockValue = By.cssSelector(".modal-body  .quantity-input-field > input");

    protected By loc_dlgUpdateStock_txtBranchStock(String branchName) {
        return By.xpath("//td[text() ='%s']/following-sibling::td//input | //tbody//tr//td[count(//*[text()='%s']/preceding-sibling::th) +1]//input[@name='search-input']".formatted(branchName, branchName));
    }

    protected final By loc_btnDeleteVariation = By.cssSelector(".d-none .product-form-variation-selector__btn-delete");
    protected final By loc_lblVariations = By.cssSelector("[class $= --n1] > .gs-widget:nth-child(4) > .gs-widget__header > h3");
    protected final By loc_chkAddWholesalePricing = By.cssSelector(".uik-checkbox__wrapper > [name='enabledListing']");
    protected final By loc_btnConfigureWholesalePricing = By.xpath("//label/*[@name = 'enabledListing']//ancestor::div[contains(@class,'gs-widget__header')]/following-sibling::div//button");
    protected final By loc_chkAddConversionUnit = By.cssSelector(".uik-checkbox__wrapper > [name='conversionUnitCheckbox']");
    protected final By loc_btnConfigureAddConversionUnit = By.xpath("//div[div/label/input[@name=\"conversionUnitCheckbox\"]]/following-sibling::div//button");
    protected final By loc_dlgConfirm_btnOK = By.cssSelector(".modal-footer .gs-button__green");
    protected final By loc_lblEditTranslation = By.xpath("(//*[text() = 'Sửa bản dịch' or text() = 'Edit Translation'])[1]/parent::div/parent::button");
    protected final By loc_ttlUpdatePrice = By.cssSelector(".modal-title");
    protected final By loc_ttlUpdateSKU = By.cssSelector(".modal-title");
    protected final By loc_lblSEOSetting = By.xpath("//div[contains(@class, ' seo-editor')]//div/h3");

}
