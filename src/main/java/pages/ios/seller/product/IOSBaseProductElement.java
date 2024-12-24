package pages.ios.seller.product;

import org.openqa.selenium.By;

public class IOSBaseProductElement {
    By loc_icnDeleteImages = By.xpath("//*[XCUIElementTypeImage[@name=\"icon_close_product_image_selected\"]]/XCUIElementTypeButton");
    By loc_icnProductImage = By.xpath("//XCUIElementTypeImage[@name=\"icon_selected_image_default\"]/preceding-sibling::XCUIElementTypeButton");
    By loc_txtProductName = By.xpath("//XCUIElementTypeStaticText[@name=\"Product name *\"]/following-sibling::XCUIElementTypeTextView");
    By loc_btnProductDescription = By.xpath("//XCUIElementTypeStaticText[@name=\"Description\"]/following-sibling::XCUIElementTypeButton");
    By loc_txtWithoutVariationListingPrice = By.xpath("(//XCUIElementTypeStaticText[@name=\"Selling price\"]/following-sibling::XCUIElementTypeOther//XCUIElementTypeTextField)[1]");
    By loc_txtWithoutVariationSellingPrice = By.xpath("(//XCUIElementTypeStaticText[@name=\"Selling price\"]/following-sibling::XCUIElementTypeOther//XCUIElementTypeTextField)[2]");
    By loc_txtWithoutVariationCostPrice = By.xpath("//XCUIElementTypeStaticText[@name=\"Cost price\"]/following-sibling::XCUIElementTypeOther//XCUIElementTypeTextField");
    By loc_txtWithoutVariationSKU = By.xpath("//XCUIElementTypeStaticText[@name=\"SKU\"]/following-sibling::XCUIElementTypeTextField");
    By loc_txtWithoutVariationBarcode = By.xpath("//XCUIElementTypeStaticText[@name=\"Barcode\"]//following-sibling::XCUIElementTypeTextField");
    public static By loc_chkDisplayIfOutOfStock = By.xpath("//*[*[@name=\"Display if out of stock\"]]/XCUIElementTypeOther");
    By loc_chkHideRemainingStock = By.xpath("//XCUIElementTypeStaticText[@name=\"Hide remaining stock on online store\"]//preceding-sibling::XCUIElementTypeOther");
    By loc_ddvSelectedManageInventoryType = By.xpath("//XCUIElementTypeStaticText[@name=\"Manage Inventory\"]/following-sibling::XCUIElementTypeButton");
    By loc_ddvManageInventoryByIMEI = By.xpath("//XCUIElementTypeStaticText[@name=\"Manage inventory by IMEI/Serial number\"]");
    By loc_chkManageStockByLotDate = By.xpath("//XCUIElementTypeOther[XCUIElementTypeStaticText[@name=\"Manage stock by Lot-date\"]]/XCUIElementTypeOther");
    By loc_btnInventory = By.xpath("//XCUIElementTypeImage[@name=\"icon_inventory\"]/preceding-sibling::XCUIElementTypeButton");
    By loc_swShipping = By.xpath("//XCUIElementTypeImage[@name=\"icon_truck\"]//following-sibling::XCUIElementTypeSwitch");
    By loc_txtWeight = By.xpath("//XCUIElementTypeStaticText[@name=\"Weight\"]//following-sibling::XCUIElementTypeTextField");
    By loc_txtLength = By.xpath("//XCUIElementTypeStaticText[@name=\"Length\"]//following-sibling::XCUIElementTypeTextField");
    By loc_txtWidth = By.xpath("//XCUIElementTypeStaticText[@name=\"Width\"]//following-sibling::XCUIElementTypeTextField");
    By loc_txtHeight = By.xpath("//XCUIElementTypeStaticText[@name=\"Height\"]//following-sibling::XCUIElementTypeTextField");
    By loc_swPriority = By.xpath("//XCUIElementTypeStaticText[@name=\"Priority\"]//following-sibling::XCUIElementTypeSwitch");
    By loc_txtPriority = By.xpath("//XCUIElementTypeStaticText[@name=\"Priority\"]/following-sibling::*/XCUIElementTypeTextField");
    By loc_swWeb = By.xpath("//XCUIElementTypeStaticText[@name=\"Web\"]//following-sibling::XCUIElementTypeSwitch");
    By loc_swApp = By.xpath("//XCUIElementTypeStaticText[@name=\"App\"]//following-sibling::XCUIElementTypeSwitch");
    By loc_swInStore = By.xpath("//XCUIElementTypeStaticText[@name=\"In-store\"]//following-sibling::XCUIElementTypeSwitch");
    By loc_swGoSocial = By.xpath("//XCUIElementTypeStaticText[@name=\"GoSocial\"]//following-sibling::XCUIElementTypeSwitch");
    By loc_swVariation = By.xpath("//XCUIElementTypeStaticText[contains(@name,\"Variations\")]//following-sibling::XCUIElementTypeSwitch");
    By loc_btnAddVariation = By.xpath("//XCUIElementTypeStaticText[@name=\"Add Variation\" or @name=\"Edit Variation\"]/preceding-sibling::XCUIElementTypeButton");
    By loc_btnEditMultiple = By.xpath("//XCUIElementTypeButton[@name=\"Edit multiple\"]");
    By loc_lstVariations = By.xpath("//XCUIElementTypeStaticText[contains(@name, \"available\")]//parent::XCUIElementTypeCell");
    By loc_btnSave = By.xpath("//XCUIElementTypeButton[@name=\"icon checked white\"]");
    By loc_dlgWarningManagedByLot_btnOK = By.xpath("//XCUIElementTypeButton[@name=\"OK\"]");
}
