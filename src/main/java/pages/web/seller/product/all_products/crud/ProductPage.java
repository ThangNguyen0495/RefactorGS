//package pages.web.seller.product.all_products.crud;
//
//import api.seller.login.APIDashboardLogin;
//import api.seller.product.APIGetProductDetail;
//import api.seller.product.APIGetProductList;
//import api.seller.setting.APIGetBranchList;
//import api.seller.setting.APIGetStoreLanguage;
//import api.seller.setting.APIGetVATList;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.openqa.selenium.By;
//import org.openqa.selenium.Keys;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.WebElement;
//import org.testng.Assert;
//import pages.web.seller.product.all_products.crud.conversion_unit.ConversionUnitPage;
//import pages.web.seller.product.all_products.crud.variation_detail.VariationDetailPage;
//import pages.web.seller.product.all_products.crud.wholesale_price.WholesaleProductPage;
//import utility.VariationUtils;
//import utility.WebUtils;
//
//import java.time.Instant;
//import java.time.OffsetDateTime;
//import java.util.*;
//import java.util.stream.IntStream;
//
//import static org.apache.commons.lang.math.JVMRandom.nextLong;
//import static org.apache.commons.lang.math.RandomUtils.nextBoolean;
//import static org.apache.commons.lang.math.RandomUtils.nextInt;
//
//public class ProductPage {
//
//    private final By loc_ddvSelectedLanguage = By.cssSelector(".language-selector span.uik-btn__content");
//    private By loc_ddvLanguageValue(String language) {
//        return By.xpath("//*[@class = 'uik-select__label']/span[text()= '%s']".formatted(language));
//    }
//
//    private final By loc_txtProductName = By.cssSelector("input#productName");
//    private final By loc_txaProductDescription = By.cssSelector("div.fr-wrapper > div");
//    private final By loc_icnRemoveImages = By.cssSelector(".image-widget__btn-remove");
//    private final By imgUploads = By.cssSelector(".image-drop-zone input");
//    private final By loc_ddvSelectedVAT = By.cssSelector(".form-group .uik-select__valueWrapper");
//    private final By loc_ddvNoVAT = By.xpath("//*[@class = 'uik-select__optionContent']/div[text()='Không áp dụng thuế'] | //*[@class = 'uik-select__optionContent']/div[text()='Tax does not apply']");
//
//    private  By loc_ddvOthersVAT(String vatName) {
//        return By.xpath("//*[@class = 'uik-select__optionContent']/div[text()='%s']".formatted(vatName));
//    }
//
//    private final By loc_dlgUpdateSKU = By.cssSelector(".product-multiple-branch-sku_editor_modal");
//    private final By loc_txtWithoutVariationSKU = By.cssSelector("#productSKU,[class *=--n2] > div:nth-child(3) .align-items-center > span");
//    private final By loc_ddlManageInventory = By.cssSelector("#manageInventory");
//    private final By loc_ddvManageInventoryByIMEI = By.cssSelector("[value=\"IMEI_SERIAL_NUMBER\"]");
//    private final By loc_chkManageStockByLotDate = By.xpath("//*[@id='lotAvailable']//parent::div//preceding-sibling::label/input");
//    private final By loc_txtPriority = By.cssSelector("[name = productPriority]");
//    private final By loc_txtWeight = By.cssSelector("[for ='productWeight'] +* input");
//    private final By loc_txtLength = By.cssSelector("[for ='productLength'] +* input");
//    private final By loc_txtWidth = By.cssSelector("[for ='productWidth'] +* input");
//    private final By loc_txtHeight = By.cssSelector("[for ='productHeight'] +* input");
//    private final By loc_chkApp = By.cssSelector("[name = onApp]");
//    private final By loc_chkWeb = By.cssSelector("[name = onWeb]");
//    private final By loc_chkInStore = By.cssSelector("[name = inStore]");
//    private final By loc_chkGoSocial = By.cssSelector("[name = inGosocial]");
//    private final By loc_btnSave = By.cssSelector("[data-testid=\"desktop-saveBtn\"]");
//    private final By loc_btnDeactivate = By.xpath("(//*[text() = 'Ngừng bán' or text() = 'Deactivate' or text() = 'Bán ngay' or text() = 'Activate'])[1]/parent::div/parent::button");
//    private final By loc_btnDelete = By.xpath("(//*[text() = 'Xóa' or text() = 'Delete'])[1]/parent::div/parent::button");
//    private final By loc_dlgConfirmDelete_btnOK = By.cssSelector(".modal-footer .gs-button__green");
//    private final By loc_dlgSuccessNotification = By.cssSelector(".modal-success");
//    private final By loc_dlgNotification_btnClose = By.cssSelector("[data-testid='closeBtn']");
//    private final By loc_btnAddAttribution = By.cssSelector("div:nth-child(8) > div.gs-widget__header .gs-fake-link");
//    private final By loc_icnDeleteAttribution = By.cssSelector("div:nth-child(8) > div.gs-widget__content-wrapper button");
//    private final By loc_txtAttributionName = By.cssSelector("[name *= 'input-attribute-name']");
//    private final By loc_txtAttributionValue = By.cssSelector("[id*= 'input-attribute-value']");
//    private final By loc_chkDisplayAttribute = By.cssSelector("div:nth-child(8) > div.gs-widget__content-wrapper .uik-checkbox__checkbox");
//    private final By loc_txtSEOTitle = By.cssSelector("input#seoTitle");
//    private final By loc_txtSEODescription = By.cssSelector("input#seoDescription");
//    private final By loc_txtSEOKeywords = By.cssSelector("input#seoKeywords");
//    private final By loc_txtSEOUrl = By.cssSelector("input#seoUrl");
//    private final By loc_txtWithoutVariationListingPrice = By.xpath("//label[@for='productDiscountPrice']//parent::div//preceding-sibling::div//label//following-sibling::div//input");
//    private final By loc_txtWithoutVariationSellingPrice = By.cssSelector("[for = 'productDiscountPrice'] +* input");
//    private final By loc_txtWithoutVariationCostPrice = By.xpath("//label[@for='productDiscountPrice']//parent::div//following-sibling::div//label[@for = 'productPrice'] //following-sibling::div//input");
//    private final By loc_chkDisplayIfOutOfStock = By.xpath("//*[@name='showOutOfStock']/parent::div/preceding-sibling::label[2]/input");
//    private final By loc_chkHideRemainingStock = By.xpath("//*[@name='isHideStock']/parent::div/preceding-sibling::label[2]/input");
//    private final By loc_chkShowAsListingProduct = By.xpath("//*[@name='enabledListing']/parent::div/preceding-sibling::label[2]/input");
//    private final By loc_txtWithoutVariationBranchStock = By.cssSelector(".branch-list-stock__wrapper__row  input");
//    private final By loc_dlgAddIMEISelectedBranch = By.cssSelector(".modal-body .uik-select__valueWrapper");
//    private final By loc_dlgAddIMEI_chkSelectAllBranches = By.cssSelector(".managed-inventory-modal .uik-menuDrop__list > button:nth-child(1)  input");
//    private final By loc_dlgAddIMEI_icnDeleteIMEI = By.cssSelector(".code .fa-times");
//    private final By loc_dlgAddIMEI_txtAddIMEI = By.cssSelector(".input-code input");
//    private final By loc_dlgAddIMEI_btnSave = By.cssSelector(".modal-footer > .gs-button__green");
//    private final By loc_btnAddVariation = By.cssSelector("div:nth-child(4) > div.gs-widget__header > span");
//    private final By loc_txtVariationName = By.cssSelector("div.first-item > div > div > input");
//    private final By loc_txtVariationValue = By.cssSelector(".second-item .box-input input");
//    private final By loc_tblVariation_chkSelectAll = By.cssSelector(".product-form-variation-selector__table  th:nth-child(1) input");
//    private final By loc_tblVariation_lnkSelectAction = By.cssSelector("th .gs-fake-link");
//    /**
//     * <p>0: Update Price</p>
//     * <p>1: Update Stock</p>
//     * <p>2: Update SKU</p>
//     * <p>3: Update Image</p>
//     */
//    private final By loc_tblVariation_ddvActions = By.cssSelector(".uik-menuDrop__list > button");
//    private final By loc_tblVariation_txtStock = By.xpath("//td[div/input[contains(@name, 'barcode')]]/preceding::td[3]/span");
//    private final By loc_tblVariation_txtSKU = By.xpath("//td[div/input[contains(@name, 'barcode')]]/preceding::td[1]/span");
//    private final By loc_dlgUpdateSKU_txtInputSKU = By.cssSelector(".justify-content-center input");
//    private final By loc_tblVariation_imgUploads = By.cssSelector("td > img");
//    private final By loc_dlgUploadsImage_btnUploads = By.cssSelector(".modal-content [type = file]");
//    private final By loc_dlgUpdatePrice_txtListingPrice = By.xpath("//*[contains(@class, 'product-variation-price-editor-modal__table')]//*[contains(@name, 'orgPrice')]//parent::div//parent::div//preceding-sibling::input");
//    private final By loc_dlgUpdatePrice_txtSellingPrice = By.xpath("//*[contains(@class, 'product-variation-price-editor-modal__table')]//*[contains(@name, 'discountPrice')]//parent::div//parent::div//preceding-sibling::input");
//    private final By loc_dlgUpdatePrice_txtCostPrice = By.xpath("//*[contains(@class, 'product-variation-price-editor-modal__table')]//*[contains(@name, 'costPrice')]//parent::div//parent::div//preceding-sibling::input");
//    private final By loc_dlgCommons_btnUpdate = By.cssSelector(".modal-footer .gs-button__green");
//    private final By loc_dlgUpdateStock_ddvSelectedBranch = By.cssSelector(".gs-dropdown-multiple-select__drop-header .uik-select__valueWrapper");
//    private final By loc_dlgUpdateStock_chkSelectAllBranches = By.cssSelector(".modal-body .uik-menuDrop__list > button:nth-child(1)  input");
//    private final By loc_dlgUpdateStock_tabChange = By.cssSelector(".modal-body  div > div > .gs-button:nth-child(2)");
//    private final By loc_dlgUpdateStock_txtStockValue = By.cssSelector(".modal-body  .quantity-input-field > input");
//
//    private By loc_dlgUpdateStock_txtBranchStock(String branchName) {
//        return By.xpath("//td[text() ='%s']/following-sibling::td//input | //tbody//tr//td[count(//*[text()='%s']/preceding-sibling::th) +1]//*[@name='search-input']".formatted(branchName, branchName));
//    }
//
//    private final By loc_btnDeleteVariation = By.cssSelector(".d-none .product-form-variation-selector__btn-delete");
//    private final By loc_dlgEditTranslation = By.cssSelector(".modal.fade.show");
//    private final By loc_dlgEditTranslation_ddvSelectedLanguage = By.cssSelector(".product-translate .text-truncate");
//
//    private By dlgEditTranslation_ddvOtherLanguage(String languageName) {
//        return By.xpath("//*[@class = 'uik-select__label']//*[text()='%s']".formatted(languageName));
//    }
//
//    private final By loc_dlgEditTranslation_txtProductName = By.cssSelector("#informationName");
//    private final By loc_dlgEditTranslation_txtProductDescription = By.cssSelector(".modal-body .fr-element");
//    private final By loc_dlgEditTranslation_txtVariationName = By.xpath("//*[@class = 'product-translate-modal']/*[@class = 'product-translate__titleBody']/h3/parent::div/following-sibling::div[@class]/div[1]/descendant::input");
//    private final By loc_dlgEditTranslation_txtVariationValue = By.xpath("//*[@class = 'product-translate-modal']/*[@class = 'product-translate__titleBody']/h3/parent::div/following-sibling::div[@class]/div[2]/descendant::input");
//    private final By loc_dlgEditTranslation_txtSEOTitle = By.cssSelector(".modal-body #seoTitle");
//    private final By loc_dlgEditTranslation_txtSEODescription = By.cssSelector(".modal-body #seoDescription");
//    private final By loc_dlgEditTranslation_txtSEOKeywords = By.cssSelector(".modal-body #seoKeywords");
//    private final By loc_dlgEditTranslation_txtSEOUrl = By.cssSelector(".modal-body #seoUrl");
//    private final By loc_dlgEditTranslation_btnSave = By.cssSelector(".modal-footer .gs-button__green");
//    private final By loc_dlgToastSuccess = By.cssSelector(".Toastify__toast--success");
//    private final By loc_lblVariations = By.cssSelector("[class $= --n1] > .gs-widget:nth-child(4) > .gs-widget__header > h3");
//    private final By loc_chkAddWholesalePricing = By.cssSelector(".uik-checkbox__wrapper > [name='enabledListing']");
//    private final By loc_btnConfigureWholesalePricing = By.xpath("//label/*[@name = 'enabledListing']//ancestor::div[contains(@class,'gs-widget__header')]/following-sibling::div//button");
//    private final By loc_chkAddConversionUnit = By.cssSelector(".uik-checkbox__wrapper > [name='conversionUnitCheckbox']");
//    private final By loc_btnConfigureAddConversionUnit = By.xpath("//*[@name = 'conversionUnitCheckbox']//ancestor::div[contains(@class, 'border-radius-bottom')]/following-sibling::div//button");
//    private final By loc_dlgConfirm_btnOK = By.cssSelector(".modal-footer .gs-button__green");
//    private final By loc_lblEditTranslation = By.xpath("(//*[text() = 'Sửa bản dịch' or text() = 'Edit Translation'])[1]/parent::div/parent::button");
//    private final By loc_ttlUpdatePrice = By.cssSelector(".modal-title");
//    private final By loc_ttlUpdateSKU = By.cssSelector(".modal-title");
//    private final By loc_lblSEOSetting = By.xpath("//div[contains(@class, ' seo-editor')]//div/h3");
//
//    private final WebDriver driver;
//    private final WebUtils webUtils;
//    private static final long MAX_PRICE = 99999999999L;
//    private boolean noDiscount = nextBoolean();
//    private boolean hasCostPrice = false;
//    private boolean hasDimension = false;
//    private boolean hasSEO = false;
//    private boolean manageByLotDate = false;
//    private boolean hasAttribution = false;
//    Logger logger = LogManager.getLogger();
//    List<APIGetBranchList.BranchInformation> branchInfoList;
//    List<APIGetStoreLanguage.LanguageInformation> languageInfoList;
//    APIGetProductDetail.ProductInformation productInfo;
//    APIDashboardLogin.Credentials credentials;
//
//    String defaultLanguage;
//    List<String> activeBranchNames;
//    List<Integer> activeBranchIds;
//    List<String> storeLanguageCodes;
//    List<String> storeLanguageNames;
//
//    public ProductPage(WebDriver driver) {
//        this.driver = driver;
//
//        // init common function
//        webUtils = new WebUtils(driver);
//    }
//
//    /**
//     * Fetches necessary information including login credentials, branch details, and store language details.
//     * This method updates the state of the ProductPage with relevant information for branches, languages,
//     * and the default language setting based on the seller's information.
//     *
//     * @param credentials The API credentials containing login information (username, password).
//     * @return The current instance of ProductPage, with updated information for method chaining.
//     */
//    public ProductPage fetchInformation(APIDashboardLogin.Credentials credentials) {
//        // Update credentials for further API requests
//        this.credentials = credentials;
//
//        // Retrieve and store branch information
//        branchInfoList = new APIGetBranchList(credentials).getBranchInformation();
//
//        // Retrieve and store language information
//        languageInfoList = new APIGetStoreLanguage(credentials).getStoreLanguageInformation();
//
//        // Retrieve the default language of the seller
//        defaultLanguage = new APIDashboardLogin().getSellerInformation(credentials).getLangKey();
//
//        // Get active branch names and IDs
//        activeBranchNames = APIGetBranchList.getActiveBranchNames(branchInfoList);
//        activeBranchIds = APIGetBranchList.getActiveBranchIds(branchInfoList);
//
//        // Get all store language codes and names
//        storeLanguageCodes = APIGetStoreLanguage.getAllStoreLanguageCodes(languageInfoList);
//        storeLanguageNames = APIGetStoreLanguage.getAllStoreLanguageNames(languageInfoList);
//
//        // Return the current instance of ProductPage for method chaining
//        return this;
//    }
//
//
//    private boolean showOutOfStock = true;
//    boolean hideStock = false;
//    boolean enableListing = false;
//    boolean showOnApp = true;
//    boolean showOnWeb = true;
//    boolean showInStore = true;
//    boolean showInGoSocial = true;
//    private static boolean manageByIMEI;
//    private static int productId;
//    private static boolean hasModel;
//
//
//    public ProductPage setLanguage(String language) {
//        driver.get(System.getProperty("domain"));
//        driver.navigate().refresh();
//
//        // open language dropdown list
//        webUtils.click(loc_ddvSelectedLanguage);
//
//        // select language
//        webUtils.clickJS(loc_ddvLanguageValue(language));
//
//        // log
//        logger.info("New language: {}", language);
//        return this;
//    }
//
//
//    public ProductPage setNoDiscount(boolean noDiscount) {
//        this.noDiscount = noDiscount;
//        return this;
//    }
//
//    public ProductPage setHasCostPrice(boolean hasCostPrice) {
//        this.hasCostPrice = hasCostPrice;
//        return this;
//    }
//
//    public ProductPage setHasDimension(boolean hasDimension) {
//        this.hasDimension = hasDimension;
//        return this;
//    }
//
//    public ProductPage setHasSEO(boolean hasSEO) {
//        this.hasSEO = hasSEO;
//        return this;
//    }
//
//    public ProductPage setManageByLotDate(boolean manageByLotDate) {
//        this.manageByLotDate = manageByLotDate;
//        return this;
//    }
//
//    public ProductPage setHasAttribution(boolean hasAttribution) {
//        this.hasAttribution = hasAttribution;
//        return this;
//    }
//
//    public ProductPage setShowOutOfStock(boolean showOutOfStock) {
//        this.showOutOfStock = showOutOfStock;
//        return this;
//    }
//
//    public ProductPage setSellingPlatform(boolean showOnApp, boolean showOnWeb, boolean showInStore, boolean showInGoSocial) {
//        this.showOnApp = showOnApp;
//        this.showOnWeb = showOnWeb;
//        this.showInStore = showInStore;
//        this.showInGoSocial = showInGoSocial;
//        return this;
//    }
//
//    /**
//     * Navigates to the create product page and handles UI cleanup.
//     *
//     * @return The current instance of ProductPage for method chaining.
//     */
//    public ProductPage navigateToCreateProductPage() {
//        driver.get("%s/product/create".formatted(System.getProperty("domain")));
//        logger.info("Navigated to create product page.");
//
//        // Remove facebook bubble
//        webUtils.removeFbBubble();
//
//        return this;
//    }
//
//
//    /**
//     * Navigates to the product update page for a given product ID,
//     * removes any old wholesale product configurations if necessary,
//     * and handles page refreshing and UI cleanup.
//     *
//     * @param productId The ID of the product to update.
//     * @return The current instance of ProductPage for method chaining.
//     */
//    public ProductPage navigateToUpdateProductPage(int productId) {
//        // Set the product ID for the page
//        ProductPage.productId = productId;
//
//        // Retrieve product details from the API
//        productInfo = fetchProductInformation(productId);
//
//        // Navigate to the product update page
//        navigateToProductPage(productId);
//
//        // Remove old wholesale product configuration if present
//        removeOldWholesaleConfig();
//
//        // Perform UI cleanup
//        webUtils.removeFbBubble();
//
//        return this;
//    }
//
//    /**
//     * Fetches product information from the API.
//     *
//     * @param productId The ID of the product to fetch information for.
//     * @return An instance of APIGetProductDetail containing the product information.
//     */
//    private APIGetProductDetail.ProductInformation fetchProductInformation(int productId) {
//        return new APIGetProductDetail(credentials).getProductInformation(productId);
//    }
//
//    /**
//     * Navigates to the product update page using the given product ID.
//     *
//     * @param productId The ID of the product to navigate to.
//     */
//    private void navigateToProductPage(int productId) {
//        driver.get("%s/product/edit/%s".formatted(System.getProperty("domain"), productId));
//        logger.info("Navigated to product update page, productId: {}", productId);
//    }
//
//    /**
//     * Removes old wholesale product configurations if they exist.
//     */
//    private void removeOldWholesaleConfig() {
//        if (webUtils.isCheckedJS(loc_chkAddWholesalePricing)) {
//            webUtils.click(loc_chkAddWholesalePricing); // Uncheck to remove old config
//            webUtils.click(loc_dlgConfirm_btnOK); // Confirm removal
//            webUtils.click(loc_btnSave); // Save changes
//
//            // Verify removal was successful
//            boolean isSuccessNotificationPresent = !webUtils.getListElement(loc_dlgSuccessNotification).isEmpty();
//            Assert.assertTrue(isSuccessNotificationPresent, "Failed to remove old wholesale configuration.");
//            driver.navigate().refresh();
//            logger.info("Old wholesale configuration removed successfully.");
//        }
//    }
//
//    void inputProductName(String name) {
//        // input product name
//        webUtils.sendKeys(loc_txtProductName, name);
//        logger.info("Input product name: {}", name);
//
//        // Collect product name
//        productInfo.setName(name);
//    }
//
//    void inputProductDescription() {
//        // input product description
//        String description = "[%s] product descriptions".formatted(defaultLanguage);
//        webUtils.sendKeys(loc_txaProductDescription, description);
//        logger.info("Input product description: {}", description);
//
//        // Collect product description
//        productInfo.setDescription(description);
//    }
//
//    void uploadProductImage(String... imageFile) {
//        // remove old product image
//        List<WebElement> removeImageIcons = webUtils.getListElement(loc_icnRemoveImages);
//        if (!removeImageIcons.isEmpty())
//            IntStream.iterate(removeImageIcons.size() - 1, iconIndex -> iconIndex >= 0, iconIndex -> iconIndex - 1)
//                    .forEach(iconIndex -> webUtils.clickJS(loc_icnRemoveImages, iconIndex));
//        // upload product image
//        for (String imgFile : imageFile) {
//            String filePath = System.getProperty("user.dir") + "/src/main/resources/files/images" + imgFile;
//            webUtils.uploads(imgUploads, filePath);
//            logger.info("[Upload product image popup] Upload images, file path: {}", filePath);
//        }
//    }
//
//    void selectVAT() {
//        // open VAT dropdown
//        webUtils.clickJS(loc_ddvSelectedVAT);
//        logger.info("Open VAT dropdown.");
//
//        // get VAT name
//        List<APIGetVATList.VATInformation> vatInfoList = new APIGetVATList(credentials).getVATInformation();
//        List<String> vatList = APIGetVATList.getVATNames(vatInfoList);
//        if (vatList.size() > 1) {
//            if ((productInfo != null)) vatList.remove(productInfo.getTaxName());
//            String vatName = vatList.get(nextInt(vatList.size()));
//
//            // select VAT
//            webUtils.clickJS(vatName.equals("tax.value.include") ? loc_ddvNoVAT : loc_ddvOthersVAT(vatName));
//
//            // log
//            logger.info("Select VAT: {}", vatName);
//        }
//    }
//
//    void inputWithoutVariationProductSKU() {
//        String sku = "SKU" + Instant.now().toEpochMilli();
//        webUtils.sendKeys(loc_txtWithoutVariationSKU, sku);
//        logger.info("Input SKU: {}", sku);
//    }
//
//    void updateWithoutVariationProductSKU() {
//        // open update SKU popup
//        webUtils.click(loc_txtWithoutVariationSKU);
//        logger.info("Open update SKU popup.");
//
//        // wait Update SKU popup visible
//        webUtils.getElement(loc_dlgUpdateSKU);
//        logger.info("Wait update SKU popup visible.");
//
//        // input SKU for each branch
//        for (int brIndex = 0; brIndex < activeBranchNames.size(); brIndex++) {
//            String sku = "SKU_%s_%s".formatted(activeBranchNames.get(brIndex), Instant.now().toEpochMilli());
//            webUtils.sendKeys(loc_dlgUpdateSKU_txtInputSKU, brIndex, sku);
//            logger.info("Update SKU: {}", sku);
//        }
//
//        // click around
//        webUtils.click(loc_ttlUpdateSKU);
//
//        // close Update SKU popup
//        webUtils.click(loc_dlgCommons_btnUpdate);
//    }
//
//    void setManageInventory(boolean isIMEIProduct) {
//        manageByIMEI = isIMEIProduct;
//
//        // set manage inventory by product or IMEI/Serial number
//        if (!driver.getCurrentUrl().contains("/edit/") && isIMEIProduct) {
//            webUtils.click(loc_ddlManageInventory);
//            webUtils.click(loc_ddvManageInventoryByIMEI);
//        }
//
//        // manage by lot date
//        if (!isIMEIProduct && manageByLotDate) {
//            if (!webUtils.isCheckedJS(loc_chkManageStockByLotDate)) {
//                webUtils.clickJS(loc_chkManageStockByLotDate);
//            }
//        }
//
//        // log
//        logger.info("Manage inventory by: {}", isIMEIProduct ? "IMEI/Serial Number" : "Product");
//    }
//
//    void setSFDisplay() {
//        // Display if out of stock
//        if (webUtils.isCheckedJS(loc_chkDisplayIfOutOfStock) != showOutOfStock)
//            webUtils.clickJS(loc_chkDisplayIfOutOfStock);
//
//        // Hide remaining stock on online store
//        if (webUtils.isCheckedJS(loc_chkHideRemainingStock) != hideStock)
//            webUtils.clickJS(loc_chkHideRemainingStock);
//
//        // Show as listing product on storefront
//        if (webUtils.isCheckedJS(loc_chkShowAsListingProduct) != enableListing)
//            webUtils.clickJS(loc_chkShowAsListingProduct);
//    }
//
//    void setPriority(int priority) {
//        // set product priority (1-100)
//        webUtils.sendKeys(loc_txtPriority, String.valueOf(priority));
//        logger.info("Input priority: {}", priority);
//    }
//
//    void setProductDimension() {
//        String dimension = (hasDimension) ? "10" : "0";
//        // input product weight
//        webUtils.sendKeys(loc_txtWeight, dimension);
//        logger.info("Input weight: {}", dimension);
//
//        // input product length
//        webUtils.sendKeys(loc_txtLength, dimension);
//        logger.info("Input length: {}", dimension);
//
//        // input product width
//        webUtils.sendKeys(loc_txtWidth, dimension);
//        logger.info("Input width: {}", dimension);
//
//        // input product height
//        webUtils.sendKeys(loc_txtHeight, dimension);
//        logger.info("Input height: {}", dimension);
//
//    }
//
//    void selectPlatform() {
//        // App
//        if (webUtils.getElement(loc_chkApp).isSelected() != showOnApp)
//            webUtils.clickJS(loc_chkApp);
//
//        // Web
//        if (webUtils.getElement(loc_chkWeb).isSelected() != showOnWeb)
//            webUtils.clickJS(loc_chkWeb);
//
//        // In-store
//        if (webUtils.getElement(loc_chkInStore).isSelected() != showInStore)
//            webUtils.clickJS(loc_chkInStore);
//
//        // GoSocial
//        if (webUtils.getElement(loc_chkGoSocial).isSelected() != showInGoSocial)
//            webUtils.clickJS(loc_chkGoSocial);
//
//    }
//
//    void addAttribution() {
//        if (!webUtils.getListElement(loc_icnDeleteAttribution).isEmpty()) {
//            int bound = webUtils.getListElement(loc_icnDeleteAttribution).size();
//            IntStream.iterate(bound - 1, deleteIndex -> deleteIndex >= 0, deleteIndex -> deleteIndex - 1)
//                    .forEach(deleteIndex -> webUtils.clickJS(loc_icnDeleteAttribution, deleteIndex));
//        }
//
//        if (hasAttribution) {
//            int numOfAttribute = nextInt(10);
//            // add attribution
//            IntStream.range(0, numOfAttribute)
//                    .forEachOrdered(ignored -> webUtils.clickJS(loc_btnAddAttribution));
//
//            // input attribution
//            long epoch = Instant.now().toEpochMilli();
//            IntStream.range(0, numOfAttribute).forEach(attIndex -> {
//                webUtils.sendKeys(loc_txtAttributionName, attIndex, "name_%s_%s".formatted(attIndex, epoch));
//                webUtils.sendKeys(loc_txtAttributionValue, attIndex, "value_%s_%s".formatted(attIndex, epoch));
//                if (!Objects.equals(webUtils.isCheckedJS(loc_chkDisplayAttribute, attIndex), nextBoolean())) {
//                    webUtils.clickJS(loc_chkDisplayAttribute, attIndex);
//                }
//            });
//        }
//    }
//
//    void inputSEO() {
//        // SEO title
//        String title = "[%s] Auto - SEO Title - %s".formatted(defaultLanguage, Instant.now().toEpochMilli());
//        webUtils.sendKeys(loc_txtSEOTitle, title);
//        logger.info("SEO title: {}.", title);
//
//        // SEO description
//        String description = "[%s] Auto - SEO Description - %s".formatted(defaultLanguage, Instant.now().toEpochMilli());
//        webUtils.sendKeys(loc_txtSEODescription, description);
//        logger.info("SEO description: {}.", description);
//
//        // SEO keyword
//        String keyword = "[%s] Auto - SEO Keyword - %s".formatted(defaultLanguage, Instant.now().toEpochMilli());
//        webUtils.sendKeys(loc_txtSEOKeywords, keyword);
//        logger.info("SEO keyword: {}", keyword);
//
//        // SEO URL
//        String url = "%s%s".formatted(defaultLanguage, Instant.now().toEpochMilli());
//        webUtils.sendKeys(loc_txtSEOUrl, url);
//        logger.info("SEO url: {}.", url);
//    }
//
//    void productInfo(String name, boolean isIMEIProduct) {
//        inputProductName(name);
//        inputProductDescription();
//        uploadProductImage("img.jpg");
//        selectVAT();
//        setManageInventory(isIMEIProduct);
//        setSFDisplay();
//        setPriority(nextInt(100) + 1);
//        setProductDimension();
//        selectPlatform();
//        addAttribution();
//        if (hasSEO) inputSEO();
//    }
//
//    // Without variation product
//    public void inputWithoutVariationPrice() {
//        // get listing price
//        productInfo.setOrgPrice(nextLong(MAX_PRICE));
//
//        // get selling price
//        productInfo.setNewPrice(noDiscount ? productInfo.getOrgPrice() : nextLong(productInfo.getOrgPrice()));
//
//        // input listing price
//        webUtils.sendKeys(loc_txtWithoutVariationListingPrice, String.valueOf(productInfo.getOrgPrice()));
//        logger.info("Listing price: {}", String.format("%,d", productInfo.getCostPrice()));
//
//        // input selling price
//        webUtils.sendKeys(loc_txtWithoutVariationSellingPrice, String.valueOf(productInfo.getNewPrice()));
//        logger.info("Selling price: {}", String.format("%,d", productInfo.getNewPrice()));
//
//        // input cost price
//        productInfo.setCostPrice(hasCostPrice ? nextLong(productInfo.getNewPrice()) : 0);
//        webUtils.sendKeys(loc_txtWithoutVariationCostPrice, String.valueOf(productInfo.getCostPrice()));
//        logger.info("Cost price {}", String.format("%,d", productInfo.getCostPrice()));
//    }
//
//    void addIMEIForEachBranch(String variationValue, List<Integer> branchStock) {
//        // select all branches
//        webUtils.click(loc_dlgAddIMEISelectedBranch);
//        logger.info("[Add IMEI popup] Open all branches dropdown.");
//
//        if (!webUtils.isCheckedJS(loc_dlgAddIMEI_chkSelectAllBranches))
//            webUtils.clickJS(loc_dlgAddIMEI_chkSelectAllBranches);
//        else webUtils.click(loc_dlgAddIMEISelectedBranch);
//        logger.info("[Add IMEI popup] Select all branches.");
//
//        // remove old IMEI
//        int bound = webUtils.getListElement(loc_dlgAddIMEI_icnDeleteIMEI).size();
//        for (int index = bound - 1; index >= 0; index--) {
//            webUtils.clickJS(loc_dlgAddIMEI_icnDeleteIMEI, index);
//        }
//        logger.info("Remove old IMEI.");
//
//        // input IMEI/Serial number for each branch
//        for (int brIndex = 0; brIndex < activeBranchNames.size(); brIndex++) {
//            String brName = activeBranchNames.get(brIndex);
//            int brStockIndex = activeBranchNames.indexOf(activeBranchNames.get(brIndex));
//            for (int i = 0; i < branchStock.get(brStockIndex); i++) {
//                String imei = "%s%s_IMEI_%s_%s\n".formatted(variationValue != null ? "%s_".formatted(variationValue) : "", activeBranchNames.get(brIndex), Instant.now().toEpochMilli(), i);
//                webUtils.sendKeys(loc_dlgAddIMEI_txtAddIMEI, brIndex, imei);
//                logger.info("Input IMEI: {}", imei.replace("\n", ""));
//            }
//            logger.info("{}[{}] Add IMEI, stock: {}", variationValue == null ? "" : "[%s]".formatted(variationValue), brName, branchStock.get(brStockIndex));
//        }
//
//        // save IMEI/Serial number
//        webUtils.click(loc_dlgAddIMEI_btnSave);
//        logger.info("Close Add IMEI popup.");
//    }
//
//    public void inputWithoutVariationStock(int... branchStockQuantity) {
//        // get product stock quantity
//        productStockQuantity = Map.of("", IntStream.range(0, activeBranchNames.size()).mapToObj(i -> (branchStockQuantity.length > i) ? branchStockQuantity[i] : 0).toList())
//
//        /* input stock for each branch */
//        if (manageByIMEI) {
//            // open add IMEI popup
//            webUtils.click(loc_txtWithoutVariationBranchStock);
//            logger.info("[Create] Open Add IMEI popup without variation product.");
//
//            // add IMEI/Serial number for each branch
//            addIMEIForEachBranch("", productStockQuantity.get(""));
//            logger.info("[Create] Complete add stock for IMEI product.");
//
//        } else {
//            // update stock for normal product
//            IntStream.range(0, activeBranchNames.size()).forEach(brIndex -> {
//                webUtils.sendKeys(loc_txtWithoutVariationBranchStock, brIndex, String.valueOf(productStockQuantity.get("").get(brIndex)));
//                logger.info("[%s] Input stock: {}", activeBranchNames.get(brIndex), productStockQuantity.get("").get(brIndex));
//            });
//            logger.info("[Create] Complete update stock for Normal product.");
//        }
//
//    }
//
//    void addNormalStockForEachBranch(List<Integer> branchStock, int varIndex) {
//        // select all branches
//        webUtils.click(loc_dlgUpdateStock_ddvSelectedBranch);
//        logger.info("[Update stock popup] Open all branches dropdown.");
//
//        // select all branches
//        if (!webUtils.isCheckedJS(loc_dlgUpdateStock_chkSelectAllBranches))
//            webUtils.clickJS(loc_dlgUpdateStock_chkSelectAllBranches);
//        else webUtils.click(loc_dlgUpdateStock_ddvSelectedBranch);
//        logger.info("[Update stock popup] Select all branches.");
//
//        // switch to change stock tab
//        webUtils.click(loc_dlgUpdateStock_tabChange);
//
//        // input stock quantity to visible stock input field
//        int stock = Collections.max(branchStock) + 1;
//        webUtils.sendKeys(loc_dlgUpdateStock_txtStockValue, String.valueOf(stock));
//
//        // input stock for each branch
//        activeBranchNames.forEach(brName -> {
//            int brStockIndex = activeBranchNames.indexOf(brName);
//            if (!webUtils.getListElement(loc_dlgUpdateStock_txtBranchStock(brName)).isEmpty()) {
//                webUtils.sendKeys(loc_dlgUpdateStock_txtBranchStock(brName), String.valueOf(branchStock.get(brStockIndex)));
//                logger.info("{}[{}] Update stock: {}", variationList.get(varIndex) == null ? "" : "[%s]".formatted(variationList.get(varIndex)), brName, branchStock.get(brStockIndex));
//            } else {
//                logger.info("{}[{}] Add stock: {}", variationList.get(varIndex) == null ? "" : "[%s]".formatted(variationList.get(varIndex)), brName, stock);
//            }
//        });
//        // close Update stock popup
//        webUtils.click(loc_dlgCommons_btnUpdate);
//        logger.info("Close Update stock popup.");
//    }
//
//    void updateWithoutVariationStock(int... branchStockQuantity) {
//        // get product stock quantity
//        productStockQuantity = new HashMap<>();
//        productStockQuantity.put("", IntStream.range(0, activeBranchNames.size()).mapToObj(brIndex -> (branchStockQuantity.length > brIndex) ? (activeBranchNames.contains(activeBranchNames.get(brIndex)) ? branchStockQuantity[brIndex] : 0) : 0).toList());
//
//        /* input stock for each branch */
//        if (manageByIMEI) {
//            // open Add IMEI popup
//            webUtils.click(loc_txtWithoutVariationBranchStock);
//            logger.info("[Update] Open Add IMEI popup without variation product.");
//
//            // add IMEI/Serial number for each branch
//            addIMEIForEachBranch("", productStockQuantity.get(""));
//            logger.info("[Update] Complete add stock for IMEI product.");
//        } else {
//            // open Update stock popup
//            webUtils.click(loc_txtWithoutVariationBranchStock);
//            logger.info("Open Update stock popup.");
//
//            // add stock for each branch
//            addNormalStockForEachBranch(productStockQuantity.get(""), 0);
//            logger.info("[Update] Complete update stock for Normal product.");
//        }
//
//    }
//
//    /**
//     * Adds variations for a product by generating random variations, deleting any existing ones,
//     * and inputting the new variation names and values.
//     *
//     * @throws InterruptedException if the thread is interrupted during sleep
//     */
//    public void addVariations() throws InterruptedException {
//        // Generate and log variation map
//        var variationMap = VariationUtils.randomVariationMap(defaultLanguage);
//        logger.info("Variation map: {}", variationMap);
//
//        // Get and log variation list from variation map
//        variationList = VariationUtils.getVariationList(variationMap);
//        logger.info("Variation list: {}", variationList);
//
//        // Delete old variations
//        deleteOldVariations();
//
//        // Add new variations
//        addNewVariationGroups(variationMap);
//
//        // Click on variations label to save
//        webUtils.click(loc_lblVariations);
//    }
//
//    /**
//     * Deletes all existing variations by clicking on the delete icons in reverse order.
//     */
//    private void deleteOldVariations() {
//        List<WebElement> deleteVariationIcons = webUtils.getListElement(loc_btnDeleteVariation);
//        IntStream.iterate(deleteVariationIcons.size() - 1, index -> index >= 0, index -> index - 1)
//                .forEach(index -> webUtils.clickJS(loc_btnDeleteVariation, index));
//        logger.info("Removed old variations.");
//    }
//
//    /**
//     * Adds new variation groups based on the generated variation map.
//     *
//     * @param variationMap The map containing variation names as keys and their corresponding values as a list
//     * @throws InterruptedException if the thread is interrupted during sleep
//     */
//    private void addNewVariationGroups(Map<String, List<String>> variationMap) throws InterruptedException {
//        // Click to add variation groups
//        IntStream.range(0, variationMap.keySet().size()).forEachOrdered(ignored -> webUtils.clickJS(loc_btnAddVariation));
//        logger.info("Added new variation groups.");
//
//        // Loop through each variation group and input name and values
//        for (int groupIndex = 0; groupIndex < variationMap.keySet().size(); groupIndex++) {
//            String varName = getVariationName(variationMap, groupIndex);
//            logger.info("Input variation name: {}", varName);
//
//            inputVariationName(varName, groupIndex);
//            inputVariationValues(variationMap.get(varName), groupIndex);
//        }
//    }
//
//    /**
//     * Retrieves the variation name for a given index from the variation map.
//     *
//     * @param variationMap The map containing variation names as keys and their corresponding values as a list
//     * @param index The index of the variation group
//     * @return The variation name for the specified index
//     */
//    private String getVariationName(Map<String, List<String>> variationMap, int index) {
//        return variationMap.keySet().stream().toList().get(index);
//    }
//
//    /**
//     * Inputs the variation name into the appropriate input field.
//     *
//     * @param varName The name of the variation to be input
//     * @param groupIndex The index of the variation group
//     */
//    private void inputVariationName(String varName, int groupIndex) {
//        webUtils.sendKeys(loc_txtVariationName, groupIndex, varName);
//    }
//
//    /**
//     * Inputs the variation values for a given variation group.
//     *
//     * @param varValues The list of values to be input for the variation group
//     * @param groupIndex The index of the variation group
//     * @throws InterruptedException if the thread is interrupted during sleep
//     */
//    private void inputVariationValues(List<String> varValues, int groupIndex) throws InterruptedException {
//        for (String varValue : varValues) {
//            webUtils.getElement(loc_txtVariationValue, groupIndex).sendKeys(varValue);
//
//            // Wait for suggestion to appear
//            Thread.sleep(500);
//
//            // Complete the input of variation value by pressing Enter
//            webUtils.getElement(loc_txtVariationValue, groupIndex).sendKeys(Keys.chord(Keys.ENTER));
//            logger.info("Input variation value: {}", varValue);
//        }
//    }
//
//    void inputVariationPrice() {
//        // get listing, selling price
//        IntStream.range(0, variationList.size()).forEachOrdered(i -> {
//            productListingPrice.add(nextLong(MAX_PRICE));
//            if (noDiscount) productSellingPrice.add(productListingPrice.get(i));
//            else productSellingPrice.add(nextLong(productListingPrice.get(i)));
//        });
//
//        // select all variation
//        if (!webUtils.isCheckedJS(loc_tblVariation_chkSelectAll))
//            webUtils.clickJS(loc_tblVariation_chkSelectAll);
//
//        // open list action dropdown
//        webUtils.clickJS(loc_tblVariation_lnkSelectAction);
//
//        // open Update price popup
//        webUtils.click(loc_tblVariation_ddvActions);
//
//        // input product price
//        IntStream.range(0, variationList.size()).forEachOrdered(varIndex -> {
//            // get current variation
//            String variation = variationList.get(varIndex);
//
//            // input listing price
//            long listingPrice = productListingPrice.get(varIndex);
//            webUtils.sendKeys(loc_dlgUpdatePrice_txtListingPrice, varIndex, String.valueOf(listingPrice));
//            logger.info("[{}] Listing price: {}.", variation, listingPrice);
//
//            // input selling price
//            long sellingPrice = productSellingPrice.get(varIndex);
//            webUtils.sendKeys(loc_dlgUpdatePrice_txtSellingPrice, varIndex, String.valueOf(sellingPrice));
//            logger.info("[{}] Selling price: {}.", variation, sellingPrice);
//
//            // input costPrice
//            long costPrice = hasCostPrice ? nextLong(sellingPrice) : 0;
//            webUtils.sendKeys(loc_dlgUpdatePrice_txtCostPrice, varIndex, String.valueOf(costPrice));
//            logger.info("[{}] Cost price: {}.", variation, costPrice);
//        });
//
//
//        // click around
//        webUtils.click(loc_ttlUpdatePrice);
//
//        // close Update price popup
//        webUtils.click(loc_dlgCommons_btnUpdate);
//    }
//
//    void inputVariationStock(int... branchStockQuantity) {
//        // get product stock quantity
//        productStockQuantity = new HashMap<>();
//        for (String variationValue : variationList) {
//            List<Integer> variationStock = new ArrayList<>();
//            // set branch stock
//            for (int branchIndex = 0; branchIndex < activeBranchNames.size(); branchIndex++) {
//                variationStock.add((branchStockQuantity.length > branchIndex) ? ((activeBranchNames.contains(activeBranchNames.get(branchIndex)) ? (branchStockQuantity[branchIndex]) : 0)) : 0);
//            }
//            productStockQuantity.put(variationValue, variationStock);
//        }
//
//        // input product stock quantity
//        for (int varIndex = 0; varIndex < variationList.size(); varIndex++) {
//            // open Update stock popup
//            webUtils.clickJS(loc_tblVariation_txtStock, varIndex);
//
//            if (manageByIMEI) {
//                addIMEIForEachBranch(variationList.get(varIndex), productStockQuantity.get(variationList.get(varIndex)));
//            } else addNormalStockForEachBranch(productStockQuantity.get(variationList.get(varIndex)), varIndex);
//        }
//    }
//
//    void inputVariationSKU() {
//        // input SKU
//        for (int varIndex = 0; varIndex < variationList.size(); varIndex++) {
//            // open Update SKU popup
//            webUtils.click(loc_tblVariation_txtSKU, varIndex);
//
//            // input SKU for each branch
//            for (int brIndex = 0; brIndex < activeBranchNames.size(); brIndex++) {
//                String sku = "SKU_%s_%s_%s".formatted(variationList.get(varIndex), activeBranchNames.get(brIndex), Instant.now().toEpochMilli());
//                webUtils.sendKeys(loc_dlgUpdateSKU_txtInputSKU, brIndex, sku);
//                logger.info("[Update SKU popup] Input SKU: {}", sku);
//            }
//
//            // click around
//            webUtils.click(loc_ttlUpdateSKU);
//
//            // close Update SKU popup
//            webUtils.click(loc_dlgCommons_btnUpdate);
//        }
//    }
//
//    void uploadVariationImage(String... imageFile) {
//        // upload image for each variation
//        for (int varIndex = 0; varIndex < variationList.size(); varIndex++) {
//            // open Update SKU popup
//            webUtils.click(loc_tblVariation_imgUploads, varIndex);
//            logger.info("Open upload variation image popup.");
//
//            // upload image
//            for (String imgFile : imageFile) {
//                String filePath = System.getProperty("user.dir") + "/src/main/resources/files/images" + imgFile;
//                webUtils.uploads(loc_dlgUploadsImage_btnUploads, filePath);
//                logger.info("[Upload variation image popup] Upload images, file path: {}", filePath);
//            }
//
//            // close Update image popup
//            webUtils.click(loc_dlgCommons_btnUpdate);
//        }
//    }
//
//    /* Active/Deactivate product */
//    public ProductPage changeProductStatus(String status, int productId) {
//        // get product information
//        productInfo = new APIGetProductDetail(credentials).getProductInformation(productId);
//
//        if (!status.equals(productInfo.getBhStatus())) {
//            // log
//            logger.info("Change product status, id: {}", productId);
//
//            // navigate to product detail page by URL
//            navigateToProductPage(productId);
//
//            // wait page loaded
//            webUtils.getElement(loc_lblSEOSetting);
//
//            // change status
//            webUtils.clickJS(loc_btnDeactivate);
//
//            logger.info("change product status from %s to {}", productInfo.getBhStatus(), status);
//        }
//        return this;
//    }
//
//    public void deleteProduct(int productId) throws Exception {
//        // get product information
//        productInfo = new APIGetProductDetail(credentials).getProductInformation(productId);
//
//        if (!productInfo.isDeleted()) {
//            // log
//            logger.info("Delete product id: {}", productId);
//
//            // navigate to product detail page by URL
//            navigateToProductPage(productId);
//
//            // wait page loaded
//            webUtils.getElement(loc_lblSEOSetting);
//
//            // open Confirm delete popup
//            webUtils.click(loc_btnDelete);
//
//            // close confirm delete product popup
//            webUtils.click(loc_dlgConfirmDelete_btnOK);
//        }
//    }
//
//    /* Complete create/update product */
//    void completeCreateProduct() {
//        // save changes
//        webUtils.click(loc_btnSave);
//
//        // if create product successfully, close notification popup
//        if (!webUtils.getListElement(loc_dlgSuccessNotification).isEmpty()) {
//            // close notification popup
//            webUtils.click(loc_dlgNotification_btnClose);
//        } else Assert.fail("[Failed][Create product] Can not create product.");
//
//        // log
//        logger.info("Wait and get product id after creation.");
//
//        // wait api return list product
//        productId = new APIGetProductList(credentials).searchProductIdByName(productInfo.getName());
//
//        // log
//        logger.info("Complete create product, id: {}", productId);
//    }
//
//    void completeUpdateProduct() {
//        // save changes
//        webUtils.clickJS(loc_btnSave);
//
//        // if update product successfully, close notification popup
//        Assert.assertFalse(webUtils.getListElement(loc_dlgSuccessNotification).isEmpty(), "Can not update product.");
//        if (!webUtils.getListElement(loc_dlgSuccessNotification).isEmpty()) {
//            // close notification popup
//            webUtils.click(loc_dlgNotification_btnClose);
//
//            // log
//            logger.info("Complete update product.");
//        }
//    }
//
//    /**
//     * Navigates to the wholesale pricing page for the specified product.
//     *
//     * @param driver      The WebDriver instance to use for interaction with the web page.
//     * @param credentials The API credentials required for accessing product details.
//     * @param productInfo The product information containing details like ID and models.
//     * @return An instance of WholesaleProductPage for further interactions and method chaining.
//     */
//    public WholesaleProductPage navigateToWholesaleProductPage(WebDriver driver, APIDashboardLogin.Credentials credentials, APIGetProductDetail.ProductInformation productInfo) {
//        // Navigate to the update product page using the product ID from productInfo
//        navigateToUpdateProductPage(productInfo.getId());
//
//        // Ensure the 'Add Wholesale Pricing' checkbox is selected
//        if (!webUtils.isCheckedJS(loc_chkAddWholesalePricing)) {
//            webUtils.clickJS(loc_chkAddWholesalePricing);
//        }
//
//        // Click the 'Configure Wholesale Pricing' button
//        webUtils.click(loc_btnConfigureWholesalePricing);
//        webUtils.removeFbBubble(); // Remove Facebook bubble if present
//
//        // Return a new instance of WholesaleProductPage for further actions
//        return new WholesaleProductPage(driver, credentials, productInfo);
//    }
//
//    /**
//     * Configures wholesale pricing for a product based on its information.
//     *
//     * @param productId The ID of the product to configure.
//     */
//    public void configWholesaleProduct(int productId) {
//        // Retrieve product information using the provided product ID
//        var productInfo = new APIGetProductDetail(credentials).getProductInformation(productId);
//
//        // Validate if the product ID is valid
//        if (productId == 0) {
//            logger.info("Cannot find product ID."); // Log an error message if ID is invalid
//            return; // Exit the method if the product ID is invalid
//        }
//
//        // Navigate to the update product page
//        navigateToUpdateProductPage(productId);
//
//        // Navigate to the wholesale product page and obtain the page object
//        WholesaleProductPage wholesalePage = navigateToWholesaleProductPage(driver, credentials, productInfo);
//
//        // Configure the product's wholesale pricing based on its model presence
//        if (productInfo.isHasModel()) {
//            // Add wholesale product variation if the product has models
//            wholesalePage.getWholesaleProductInfo().addWholesaleProductVariation();
//        } else {
//            // Add wholesale product without variation if no models are present
//            wholesalePage.getWholesaleProductInfo().addWholesaleProductWithoutVariation();
//        }
//
//        // Save the wholesale product configuration
//        webUtils.click(loc_btnSave);
//    }
//
//    /**
//     * Navigates to the Conversion Unit page for the specified product.
//     *
//     * @param driver      The WebDriver instance to use for interaction with the web page.
//     * @param credentials The API credentials required for accessing product details.
//     * @param productInfo The product information containing details like ID and inventory management type.
//     * @return An instance of ConversionUnitPage for further interactions and method chaining.
//     */
//    public ConversionUnitPage navigateToConversionUnitPage(WebDriver driver, APIDashboardLogin.Credentials credentials, APIGetProductDetail.ProductInformation productInfo) {
//        // Navigate to the update product page using the product ID from productInfo
//        navigateToUpdateProductPage(productInfo.getId());
//
//        // Uncheck the 'Add Conversion Unit' checkbox if it is currently checked
//        if (webUtils.isCheckedJS(loc_chkAddConversionUnit)) {
//            webUtils.clickJS(loc_chkAddConversionUnit);
//        }
//
//        // Check the 'Add Conversion Unit' checkbox to enable configuration
//        webUtils.clickJS(loc_chkAddConversionUnit);
//
//        // Handle conversion unit configuration based on inventory management type
//        if (productInfo.getInventoryManageType().equals("IMEI_SERIAL_NUMBER")) {
//            logger.info("Conversion units are not supported for products managed by IMEI/Serial at this time.");
//        } else {
//            // Click the 'Configure Add Conversion Unit' button
//            webUtils.click(loc_btnConfigureAddConversionUnit);
//            webUtils.removeFbBubble(); // Remove Facebook bubble if present
//        }
//
//        // Return a new instance of ConversionUnitPage for further actions
//        return new ConversionUnitPage(driver, credentials, productInfo);
//    }
//
//    /**
//     * Configures the conversion unit for a product based on whether it has variations.
//     *
//     * @param productId The ID of the product to configure.
//     */
//    public void configConversionUnit(int productId) {
//        // Retrieve product information using the provided product ID
//        var productInfo = new APIGetProductDetail(credentials).getProductInformation(productId);
//
//        // Navigate to the Conversion Unit page and obtain the page object
//        ConversionUnitPage conversionUnitPage = navigateToConversionUnitPage(driver, credentials, productInfo);
//
//        // Configure the conversion unit based on whether the product has variations
//        if (productInfo.isHasModel()) {
//            // Add conversion unit variation if the product has models
//            conversionUnitPage.addConversionUnitVariation();
//        } else {
//            // Add conversion unit without variation if no models are present
//            conversionUnitPage.addConversionUnitWithoutVariation();
//        }
//    }
//
//    /* Create product */
//    public ProductPage createWithoutVariationProduct(boolean isIMEIProduct, int... branchStock) throws Exception {
//        // Logger
//        LogManager.getLogger().info("===== STEP =====> [CreateWithoutVariationProduct] START... ");
//
//        hasModel = false;
//
//        // product name
//        String name = "[%s] %s".formatted(defaultLanguage, isIMEIProduct ? ("Auto - IMEI - without variation - ") : ("Auto - Normal - without variation - "));
//        name += OffsetDateTime.now();
//        productInfo(name, isIMEIProduct);
//        inputWithoutVariationPrice();
//        if (!manageByLotDate) inputWithoutVariationStock(branchStock);
//        inputWithoutVariationProductSKU();
//        completeCreateProduct();
//
//        // Logger
//        LogManager.getLogger().info("===== STEP =====> [CreateWithoutVariationProduct] DONE!!! ");
//
//        return this;
//    }
//
//    public ProductPage createVariationProduct(boolean isIMEIProduct, int... branchStock) throws Exception {
//        // Logger
//        LogManager.getLogger().info("===== STEP =====> [CreateVariationProduct] START... ");
//
//        hasModel = true;
//
//        // product name
//        String name = "[%s] %s".formatted(defaultLanguage, isIMEIProduct ? ("Auto - IMEI - Variation - ") : ("Auto - Normal - Variation - "));
//        name += OffsetDateTime.now();
//        productInfo(name, isIMEIProduct);
//        addVariations();
//        uploadVariationImage("img.jpg");
//        inputVariationPrice();
//        if (!manageByLotDate) inputVariationStock( branchStock);
//        inputVariationSKU();
//        completeCreateProduct();
//
//        // Logger
//        LogManager.getLogger().info("===== STEP =====> [CreateVariationProduct] DONE!!! ");
//
//        return this;
//    }
//
//    /* Update Product */
//    public void updateWithoutVariationProduct(int... newBranchStock) {
//        // Logger
//        LogManager.getLogger().info("===== STEP =====> [UpdateWithoutVariationProduct] START... ");
//
//        hasModel = false;
//
//        // product name
//        String name = "[%s] %s".formatted(defaultLanguage, productInfo.getInventoryManageType().equals("IMEI_SERIAL_NUMBER") ? ("Auto - IMEI - without variation - ") : ("Auto - Normal - without variation - "));
//        name += OffsetDateTime.now();
//        productInfo(name, productInfo.getInventoryManageType().equals("IMEI_SERIAL_NUMBER"));
//        inputWithoutVariationPrice();
//        if (!manageByLotDate) updateWithoutVariationStock(newBranchStock);
//        updateWithoutVariationProductSKU();
//        completeUpdateProduct();
//
//        // Logger
//        LogManager.getLogger().info("===== STEP =====> [UpdateWithoutVariationProduct] DONE!!! ");
//    }
//
//    public ProductPage updateVariationProduct(int... newBranchStock) throws InterruptedException {
//        // Logger
//        LogManager.getLogger().info("===== STEP =====> [UpdateVariationProduct] START... ");
//
//        hasModel = true;
//
//        // product name
//        String name = "[%s] %s".formatted(defaultLanguage, productInfo.getInventoryManageType().equals("IMEI_SERIAL_NUMBER") ? ("Auto - IMEI - Variation - ") : ("Auto - Normal - Variation - "));
//        name += OffsetDateTime.now();
//        productInfo(name, productInfo.getInventoryManageType().equals("IMEI_SERIAL_NUMBER"));
//        if (!productInfo.isLotAvailable() && !manageByLotDate) {
//            addVariations();
//            uploadVariationImage("img.jpg");
//            inputVariationPrice();
//            inputVariationStock(newBranchStock);
//            inputVariationSKU();
//        }
//        completeUpdateProduct();
//
//        // Logger
//        LogManager.getLogger().info("===== STEP =====> [UpdateVariationProduct] DONE!!! ");
//
//        return this;
//    }
//
//    public void changeVariationStatus(int productID) {
//        // Logger
//        LogManager.getLogger().info("===== STEP =====> [ChangeProductStatus] START... ");
//
//        // update variation product name and description
//        // get current product information
//        APIGetProductDetail.ProductInformation productInfo = new APIGetProductDetail(credentials).getProductInformation(productID);
//
//        // update variation status
//        for (int modelId : APIGetProductDetail.getVariationModelList(productInfo))
//            new VariationDetailPage(driver, modelId, productInfo, credentials)
//                    .changeVariationStatus(List.of("ACTIVE", "INACTIVE").get(nextInt(2)));
//
//        // Logger
//        LogManager.getLogger().info("===== STEP =====> [ChangeProductStatus] DONE!!! ");
//    }
//
//    public void editVariationTranslation(int productID) {
//        // Logger
//        LogManager.getLogger().info("===== STEP =====> [AddVariationTranslation] START... ");
//        // update variation product name and description
//        // get current product information
//        APIGetProductDetail.ProductInformation productInfo = new APIGetProductDetail(credentials).getProductInformation(productID);
//
//        APIGetProductDetail.getVariationModelList(productInfo).forEach(barcode -> new VariationDetailPage(driver, barcode, productInfo, credentials).updateVariationProductNameAndDescription());
//
//        // Logger
//        LogManager.getLogger().info("===== STEP =====> [AddVariationTranslation] DONE!!! ");
//    }
//
//    /* Edit translation */
//    void addTranslation(String language, String languageName, APIGetProductDetail.ProductInformation productInfo) {
//        // open edit translation popup
//        if (APIGetStoreLanguage.getAllStoreLanguageCodes(languageInfoList).size() > 1) {
//            if (!webUtils.getListElement(loc_dlgEditTranslation).isEmpty()) {
//                // convert languageCode to languageName
//                if (language.equals("en") && (webUtils.getLangKey().equals("vi")))
//                    languageName = "Tiếng Anh";
//
//                // select language for translation
//                if (!webUtils.getText(loc_dlgEditTranslation_ddvSelectedLanguage).equals(languageName)) {
//                    // open language dropdown
//                    webUtils.click(loc_dlgEditTranslation_ddvSelectedLanguage);
//
//                    // select language
//                    webUtils.click(dlgEditTranslation_ddvOtherLanguage(languageName));
//                }
//                logger.info("Add translation for '{}' language.", languageName);
//
//                // input translate product name
//                String name = "[%s] %s%s".formatted(language, productInfo.getInventoryManageType().equals("IMEI_SERIAL_NUMBER") ? ("Auto - IMEI - without variation - ") : ("Auto - Normal - without variation - "), OffsetDateTime.now());
//                webUtils.sendKeys(loc_dlgEditTranslation_txtProductName, name);
//                logger.info("Input translation for product name: {}", name);
//
//
//                // input translate product description
//                String description = "[%s] product description".formatted(language);
//                webUtils.sendKeys(loc_dlgEditTranslation_txtProductDescription, description);
//                logger.info("Input translation for product description: {}", description);
//
//                // input variation if any
//                if (productInfo.isHasModel()) {
//                    List<String> variationName = IntStream.range(0, APIGetProductDetail.getVariationGroupName(productInfo, defaultLanguage).split("\\|").length).mapToObj(i -> "%s_var%s".formatted(language, i + 1)).toList();
//                    List<String> variationValue = new ArrayList<>();
//                    List<String> variationList = APIGetProductDetail.getVariationValues(productInfo, defaultLanguage);
//                    variationList.stream().map(varValue -> varValue.replace(defaultLanguage, language).split("\\|")).forEach(varValueList -> Arrays.stream(varValueList).filter(varValue -> !variationValue.contains(varValue)).forEach(var -> variationValue.add(var.contains("%s_".formatted(language)) ? var : "%s_%s".formatted(language, var))));
//                    Collections.sort(variationList);
//                    // input variation name
//                    IntStream.range(0, variationName.size()).forEachOrdered(varIndex -> webUtils.sendKeys(loc_dlgEditTranslation_txtVariationName, varIndex, variationName.get(varIndex)));
//                    // input variation value
//                    IntStream.range(0, variationValue.size()).forEachOrdered(varIndex -> webUtils.sendKeys(loc_dlgEditTranslation_txtVariationValue, varIndex, variationValue.get(varIndex)));
//                }
//
//                // input SEO
//                // input title
//                String title = "[%s] Auto - SEO Title - %s".formatted(language, Instant.now().toEpochMilli());
//                webUtils.sendKeys(loc_dlgEditTranslation_txtSEOTitle, title);
//                logger.info("Input translation for SEO title: {}", title);
//
//                // input description
//                String seoDescription = "[%s] Auto - SEO Description - %s".formatted(language, Instant.now().toEpochMilli());
//                webUtils.sendKeys(loc_dlgEditTranslation_txtSEODescription, seoDescription);
//                logger.info("Input translation for SEO description: {}", seoDescription);
//
//                // input keywords
//                String keywords = "[%s] Auto - SEO Keyword - %s".formatted(language, Instant.now().toEpochMilli());
//                webUtils.sendKeys(loc_dlgEditTranslation_txtSEOKeywords, keywords);
//                logger.info("Input translation for SEO keywords: {}", keywords);
//
//                // input url
//                String url = "%s%s".formatted(language, Instant.now().toEpochMilli());
//                webUtils.sendKeys(loc_dlgEditTranslation_txtSEOUrl, url);
//                logger.info("Input translation for SEO url: {}", url);
//
//                // save changes
//                webUtils.clickJS(loc_dlgEditTranslation_btnSave);
//                logger.info("Save translation");
//                Assert.assertFalse(webUtils.getListElement(loc_dlgToastSuccess).isEmpty(),
//                        "Can not add new translation for '%s' language.".formatted(languageName));
//            }
//        }
//    }
//
//    public void editTranslation(int productId) {
//        // Logger
//        LogManager.getLogger().info("===== STEP =====> [AddProductTranslation] START... ");
//
//        // navigate to product detail page by URL
//        navigateToProductPage(productId);
//        logger.info("Navigate to product detail page, productId: {}", productId);
//
//        // get online store language
//        List<String> langCodeList = new ArrayList<>(storeLanguageCodes);
//        List<String> langNameList = new ArrayList<>(storeLanguageNames);
//        langCodeList.remove(defaultLanguage);
//        logger.info("List languages are not translated: {}", langCodeList.toString());
//
//        // get product information
//        productInfo = new APIGetProductDetail(credentials).getProductInformation(productId);
//
//        // add translation
//        // open edit translation popup
//        webUtils.clickJS(loc_lblEditTranslation);
//        Assert.assertFalse(webUtils.getListElement(loc_dlgEditTranslation).isEmpty(),
//                "Can not open edit translation popup.");
//
//        for (String langCode : langCodeList) {
//            addTranslation(langCode, langNameList.get(storeLanguageCodes.indexOf(langCode)), productInfo);
//        }
//
//        // save edit translation
//        completeUpdateProduct();
//
//        // Logger
//        LogManager.getLogger().info("===== STEP =====> [AddProductTranslation] DONE!!! ");
//    }
//
//    public void addVariationAttribution() {
//        // Logger
//        LogManager.getLogger().info("===== STEP =====> [AddVariationAttribution] START... ");
//
//        // get current product information
//        APIGetProductDetail.ProductInformation productInfo = new APIGetProductDetail(credentials).getProductInformation(productId);
//
//        // update variation status
//        APIGetProductDetail.getVariationModelList(productInfo).forEach(modelId -> new VariationDetailPage(driver, modelId, productInfo, credentials)
//                .updateAttribution());
//
//        // Logger
//        LogManager.getLogger().info("===== STEP =====> [AddVariationAttribution] DONE!!! ");
//    }
//}
