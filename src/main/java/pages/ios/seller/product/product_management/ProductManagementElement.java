package pages.ios.seller.product.product_management;

import org.openqa.selenium.By;

public class ProductManagementElement {


    public static By loc_txtSearchBox = By.xpath("//XCUIElementTypeImage[@name=\"icon_search\"]/preceding-sibling::XCUIElementTypeTextField");
    By loc_lblProductName(String productName) {
        return By.xpath("//XCUIElementTypeStaticText[@name=\"%s\"]".formatted(productName));
    }
    By loc_lblProductName = By.xpath("//XCUIElementTypeCell//XCUIElementTypeStaticText[last()]");
    By loc_btnSort = By.xpath("//XCUIElementTypeButton[@name=\"icon sort priority\"]");
    By loc_ddvRecentlyUpdated = By.xpath("//XCUIElementTypeStaticText[@name=\"Recently updated\"]");
    By loc_ddvStockHighToLow = By.xpath("//XCUIElementTypeStaticText[@name=\"Stock: High to low\"]");
    By loc_ddvStockLowToHigh = By.xpath("//XCUIElementTypeStaticText[@name=\"Stock: Low to high\"]");
    By loc_ddvPriorityHighToLow = By.xpath("//XCUIElementTypeStaticText[@name=\"Priority: High to low\"]");
    By loc_ddvPriorityLowToHigh = By.xpath("//XCUIElementTypeStaticText[@name=\"Priority: Low to high\"]");
    By loc_btnFilter = By.xpath("(//XCUIElementTypeButton[@name=\"icon sort priority\"]/parent::*/following-sibling::*//XCUIElementTypeButton)[1]");

}
