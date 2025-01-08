package pages.ios.seller.home;

import org.openqa.selenium.By;

public class HomeElement {
    public static By loc_icnAccount = By.xpath("//XCUIElementTypeImage[@name=\"icon_tabbar_account\"]/parent::XCUIElementTypeButton");
    By loc_icnCreateProduct = By.xpath("//*[*[@name=\"Add new product\"]]/XCUIElementTypeButton");
    By loc_icnProductManagement = By.xpath("//XCUIElementTypeImage[@name=\"icon_home_product_management\"]/preceding-sibling::XCUIElementTypeButton");
    By loc_icnSupplierManagement = By.xpath("//*[*[@name=\"icon_home_suplier\"]]/XCUIElementTypeButton");
}
