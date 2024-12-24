package pages.web.seller.suppliers.all_suppliers;

import org.openqa.selenium.By;

public class BaseSupplierElement {
    By loc_btnHeaderSave = By.cssSelector(".btn-save");
    By loc_txtSupplierName = By.cssSelector("#name");
    By loc_txtSupplierCode = By.cssSelector("#code");
    By loc_txtPhoneNumber = By.cssSelector("#phone-number");
    By loc_txtEmail = By.cssSelector("#email");
    By loc_ddvSelectedCountry = By.cssSelector("#countryCode");
    By loc_txtVietnamAddress = By.cssSelector("#address");
    By loc_lblSelectedVietnamCity = By.cssSelector("#province");
    By loc_lblSelectedVietnamDistrict = By.cssSelector("#district");
    By loc_lblSelectedVietnamWard = By.cssSelector("#ward");
    By loc_txtForeignStreetAddress = By.cssSelector("#address");
    By loc_txtForeignAddress2 = By.cssSelector("#address2");
    By loc_txtForeignCity = By.cssSelector("#cityName");
    By loc_lblSelectedForeignProvince = By.cssSelector("#province");
    By loc_txtForeignZipcode = By.cssSelector("#zipCode");
    By loc_lblSelectedResponsibleStaff = By.cssSelector("#staff");
    By loc_ddlResponsibleStaff = By.cssSelector("#staff option");
    By loc_txtDescription = By.cssSelector("#description");
    By loc_dlgToastSuccess = By.cssSelector(".Toastify__toast--success");
}
