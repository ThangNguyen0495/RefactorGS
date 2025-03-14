package pages.ios.seller.home;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

public class HomeElement {
    By loc_icnAccount = AppiumBy.iOSNsPredicateString("name == \"Tài khoản\" or name == \"Account\"");
    By loc_icnCreateProduct = By.xpath("//*[XCUIElementTypeImage[@name=\"icon_home_create_new_product\"]]/XCUIElementTypeButton");
    By loc_icnProductManagement = By.xpath("//XCUIElementTypeImage[@name=\"icon_home_product_management\"]/preceding-sibling::XCUIElementTypeButton");
    By loc_icnSupplierManagement = By.xpath("//*[*[@name=\"icon_home_suplier\"]]/XCUIElementTypeButton");
}
