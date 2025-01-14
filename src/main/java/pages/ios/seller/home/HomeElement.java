package pages.ios.seller.home;

import org.openqa.selenium.By;

public class HomeElement {
    By loc_icnAccount = By.xpath("//XCUIElementTypeImage[@name=\"icon_tabbar_account\"]/parent::XCUIElementTypeButton");
    By loc_icnCreateProduct = By.xpath("//*[XCUIElementTypeImage[@name=\"icon_home_create_new_product\"]]/XCUIElementTypeButton");
    By loc_icnProductManagement = By.xpath("//XCUIElementTypeImage[@name=\"icon_home_product_management\"]/preceding-sibling::XCUIElementTypeButton");
    By loc_icnSupplierManagement = By.xpath("//*[*[@name=\"icon_home_suplier\"]]/XCUIElementTypeButton");
}
