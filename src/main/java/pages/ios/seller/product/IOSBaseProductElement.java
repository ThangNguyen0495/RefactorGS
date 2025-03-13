package pages.ios.seller.product;

import org.openqa.selenium.By;

public class IOSBaseProductElement {
    By loc_icnDeleteImages = By.xpath("//*[XCUIElementTypeImage[@name=\"ic_close_circle\"]]/XCUIElementTypeButton");
    By loc_icnProductImage = By.xpath("//*[XCUIElementTypeImage[@name=\"icon_selected_image_default\"]]/XCUIElementTypeButton");
    By loc_txtProductName = By.xpath("//*[XCUIElementTypeStaticText[@name=\"Product name *\" or @name=\"Tên sản phẩm *\"]]/XCUIElementTypeTextView");
    By loc_btnProductDescription = By.xpath("//*[XCUIElementTypeStaticText[@name=\"Description\" or @name=\"Mô tả\"]]/XCUIElementTypeButton");
    By loc_txtWithoutVariationListingPrice = By.xpath("(//*[XCUIElementTypeStaticText[@name=\"Selling price\" or @name=\"Giá bán\"]]/XCUIElementTypeOther//XCUIElementTypeTextField)[1]");
    By loc_txtWithoutVariationSellingPrice = By.xpath("(//*[XCUIElementTypeStaticText[@name=\"Selling price\" or @name=\"Giá bán\"]]/XCUIElementTypeOther//XCUIElementTypeTextField)[2]");
    By loc_txtWithoutVariationCostPrice = By.xpath("//*[XCUIElementTypeStaticText[@name=\"Cost price\" or @name=\"Giá gốc\"]]/XCUIElementTypeOther//XCUIElementTypeTextField");
    By loc_txtWithoutVariationSKU = By.xpath("//*[XCUIElementTypeStaticText[@name=\"SKU\" or @name=\"Mã SKU\"]]/XCUIElementTypeTextField");
    By loc_txtWithoutVariationBarcode = By.xpath("//*[XCUIElementTypeStaticText[@name=\"Barcode\"]]/XCUIElementTypeTextField");
    By loc_chkDisplayIfOutOfStock = By.xpath("//*[*[@name=\"Display if out of stock\" or @name=\"Hiển thị ngay cả khi hết hàng\"]]/XCUIElementTypeOther");
    By loc_chkHideRemainingStock = By.xpath("//*[XCUIElementTypeStaticText[@name=\"Hide remaining stock on online store\" or @name=\"Ẩn số lượng tồn kho trên cửa hàng trực tuyến\"]]/XCUIElementTypeOther");
    By loc_ddvSelectedManageInventoryType = By.xpath("//*[XCUIElementTypeStaticText[@name=\"Manage Inventory\" or @name=\"Quản lý kho hàng\"]]/XCUIElementTypeButton");
    By loc_ddvManageInventoryByIMEI = By.xpath("//XCUIElementTypeStaticText[@name=\"Manage inventory by IMEI/Serial number\" or @name=\"Quản lý theo số IMEI/Serial\"]");
    By loc_chkManageStockByLotDate = By.xpath("//*[*[@name=\"Manage stock by Lot-date\" or @name=\"Quản lý kho hàng theo Lô hạn sử dụng\"]]/XCUIElementTypeOther");
    By loc_btnInventory = By.xpath("//*[XCUIElementTypeImage[@name=\"icon_inventory\"]]/XCUIElementTypeButton");
    By loc_swShipping = By.xpath("//*[*[@name=\"icon_truck\"]]/XCUIElementTypeSwitch");
    By loc_txtWeight = By.xpath("//*[XCUIElementTypeStaticText[@name=\"Weight\" or @name=\"Cân nặng\"]]/XCUIElementTypeTextField");
    By loc_txtLength = By.xpath("//*[XCUIElementTypeStaticText[@name=\"Length\" or @name=\"Chiều dài\"]]/XCUIElementTypeTextField");
    By loc_txtWidth = By.xpath("//*[XCUIElementTypeStaticText[@name=\"Width\" or @name=\"Chiều rộng\"]]/XCUIElementTypeTextField");
    By loc_txtHeight = By.xpath("//*[XCUIElementTypeStaticText[@name=\"Height\" or @name=\"Chiều cao\"]]/XCUIElementTypeTextField");
    By loc_swPriority = By.xpath("//*[XCUIElementTypeStaticText[@name=\"Priority\" or @name=\"Độ ưu tiên\"]]/XCUIElementTypeSwitch");
    By loc_txtPriority = By.xpath("//*[XCUIElementTypeStaticText[@name=\"Priority\" or @name=\"Độ ưu tiên\"]]/*/XCUIElementTypeTextField");
    By loc_swWeb = By.xpath("//*[XCUIElementTypeStaticText[@name=\"Web\"]]/XCUIElementTypeSwitch");
    By loc_swApp = By.xpath("//*[XCUIElementTypeStaticText[@name=\"App\" or @name=\"Ứng dụng\"]]/XCUIElementTypeSwitch");
    By loc_swInStore = By.xpath("//*[XCUIElementTypeStaticText[@name=\"In-store\" or @name=\"Tại cửa hàng\"]]/XCUIElementTypeSwitch");
    By loc_swGoSocial = By.xpath("//*[XCUIElementTypeStaticText[@name=\"GoSocial\"]]/XCUIElementTypeSwitch");
    By loc_swVariation = By.xpath("//*[XCUIElementTypeStaticText[contains(@name,\"Variations\") or contains(@name, \"Phân loại hàng\")]]/XCUIElementTypeSwitch");
    By loc_btnAddVariation = By.xpath("//*[XCUIElementTypeStaticText[@name=\"Add Variation\" or @name=\"Thêm phân loại\" or @name=\"Edit Variation\" or @name=\"Sửa phân loại\"]]/XCUIElementTypeButton");
    By loc_btnEditMultiple = By.xpath("//XCUIElementTypeButton[@name=\"Edit multiple\" or @name=\"Sửa hàng loạt\"]");
    By loc_lstVariations= By.xpath("//XCUIElementTypeStaticText[contains(@name, \"available\") or contains(@name, \"còn\")]//parent::XCUIElementTypeCell");
    By loc_btnSave = By.xpath("//XCUIElementTypeButton[@name=\"icon checked white\"]");
    By loc_dlgWarningManagedByLot_btnOK = By.xpath("//XCUIElementTypeButton[@name=\"OK\"]");
}
