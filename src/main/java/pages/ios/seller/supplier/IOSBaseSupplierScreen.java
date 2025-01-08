package pages.ios.seller.supplier;

import api.seller.login.APISellerLogin;
import api.seller.supplier.APIGetSupplierDetail;
import api.seller.supplier.APIGetSupplierList;
import io.appium.java_client.AppiumBy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import pages.web.seller.suppliers.all_suppliers.BaseSupplierPage;
import utility.IOSUtils;
import utility.PropertiesUtils;
import utility.WebUtils;
import utility.helper.SupplierHelper;

import java.util.Optional;


public class IOSBaseSupplierScreen {
    private final WebDriver driver;
    private final IOSUtils iosUtils;

    private final Logger logger = LogManager.getLogger();
    private APIGetSupplierDetail.SupplierInformation supplierInfo;
    private APISellerLogin.Credentials credentials;

    /**
     * Constructor that initializes the WebDriver and WebUtils instances for interacting with web elements.
     *
     * @param driver The WebDriver instance used to interact with the web page.
     */
    public IOSBaseSupplierScreen(WebDriver driver) {
        this.driver = driver;
        iosUtils = new IOSUtils(driver);
    }

    By loc_btnHeaderSave = By.xpath("//XCUIElementTypeButton[@name=\"Save\"]");
    By loc_txtSupplierName = By.xpath("//*[*[@name=\"Supplier name*\"]]/XCUIElementTypeTextField");
    By loc_txtSupplierCode = By.xpath("//*[*[@name=\"Supplier code\"]]/XCUIElementTypeTextField");
    By loc_txtPhoneNumber = By.xpath("//*[*[@name=\"Phone\"]]/XCUIElementTypeTextField");
    By loc_txtEmail = By.xpath("//*[*[@name=\"Email\"]]/XCUIElementTypeTextField");
    By loc_ddvSelectedCountry = By.xpath("//*[*[@name=\"Country\"]]/XCUIElementTypeButton");
    By loc_txtVietnamAddress = By.xpath("//*[*[@name=\"Address\"]]/XCUIElementTypeTextField");
    By loc_lblSelectedVietnamCity = By.xpath("//*[*[@name=\"City/Province\"]]/XCUIElementTypeButton");
    By loc_lblSelectedVietnamDistrict = By.xpath("//*[*[@name=\"District\"]]/XCUIElementTypeButton");
    By loc_lblSelectedVietnamWard = By.xpath("//*[*[@name=\"Ward\"]]/XCUIElementTypeButton");
    By loc_txtForeignStreetAddress = By.xpath("//*[*[@name=\"Street address\"]]/XCUIElementTypeTextField");
    By loc_txtForeignAddress2 = By.xpath("//*[*[@name=\"Address 2\"]]/XCUIElementTypeTextField");
    By loc_txtForeignCity = By.xpath("//*[*[@name=\"City\"]]/XCUIElementTypeTextField");
    By loc_lblSelectedForeignProvince = By.xpath("//*[*[@name=\"State / Region / Province\"]]/XCUIElementTypeButton");
    By loc_txtForeignZipcode = By.xpath("//*[*[@name=\"ZipCode\"]]/XCUIElementTypeTextField");
    By loc_lblSelectedResponsibleStaff = By.xpath("//*[*[@name=\"Responsible Staff\"]]/XCUIElementTypeButton");

    By loc_ddvResponsibleStaff(String staffName) {
        return By.xpath("//*[@name = '%s']".formatted(staffName));
    }


    /**
     * Fetches supplier information from the API based on the credentials provided and stores it
     * in the `supplierInfo` object.
     *
     * @param credentials       The credentials required for API authentication.
     * @param isVietnamSupplier Flag indicating whether the supplier is based in Vietnam.
     * @return The current instance of {@link BaseSupplierPage}.
     */
    public IOSBaseSupplierScreen fetchSupplierInformation(APISellerLogin.Credentials credentials, boolean isVietnamSupplier) {
        this.credentials = credentials;
        this.supplierInfo = SupplierHelper.generateSupplierInformation(credentials, isVietnamSupplier);
        return this;
    }

    private APIGetSupplierDetail.SupplierInformation fetchSupplierInformation(int supplierId) {
        return new APIGetSupplierDetail(PropertiesUtils.getSellerCredentials()).getSupplierInformation(supplierId);
    }

    /**
     * Navigates to the page where a new supplier can be created.
     */
    public void navigateToCreateSupplierPage() {
        // Navigate to product management screen,
        // Then navigate to create supplier screen
        new IOSSupplierManagementScreen(driver).navigateToSupplierManagementScreen()
                .navigateToCreateSupplierScreen();

        // Log
        logger.info("Navigate to create supplier screen.");
    }

    /**
     * Navigates to the supplier detail page for the given supplier ID.
     * This method uses the supplier's ID to construct the URL and navigates to the edit page of the supplier's details.
     *
     * @param supplierId The unique identifier of the supplier whose details are to be viewed or edited.
     */
    public void navigateToSupplierDetailScreenByItsId(int supplierId) {
        // Get product information
        var supplierInfo = fetchSupplierInformation(supplierId);

        // Get product name
        String supplierName = supplierInfo.getName();

        // Navigate to product detail screen
        new IOSSupplierManagementScreen(driver).navigateToSupplierManagementScreen()
                .navigateToSupplierDetailScreen(supplierName);

        // Log
        logger.info("Navigate to supplier detail screen");

    }

    /**
     * Inputs the supplier's name into the corresponding text field.
     */
    private void inputSupplierName() {
        String supplierName = supplierInfo.getName();
        iosUtils.sendKeys(loc_txtSupplierName, supplierName);
        logger.info("Input supplier name: {}", supplierName);
    }

    /**
     * Inputs the supplier's code into the corresponding text field.
     */
    private void inputSupplierCode() {
        String supplierCode = supplierInfo.getCode();
        iosUtils.sendKeys(loc_txtSupplierCode, supplierCode);
        logger.info("Input supplier code: {}", supplierCode);
    }

    /**
     * Inputs the supplier's phone number into the corresponding text field.
     */
    private void inputPhoneNumber() {
        String phoneNumber = supplierInfo.getPhoneNumber();
        iosUtils.sendKeys(loc_txtPhoneNumber, phoneNumber);
        logger.info("Input phone number: {}", phoneNumber);
    }

    /**
     * Inputs the supplier's email address into the corresponding text field.
     */
    private void inputEmail() {
        String email = supplierInfo.getEmail();
        iosUtils.sendKeys(loc_txtEmail, email);
        logger.info("Input email: {}", email);
    }

    /**
     * Selects the country for the supplier based on the country code stored in the supplier information.
     */
    private void selectCountry() {
        String countryName = supplierInfo.getCountryName();
        iosUtils.click(loc_ddvSelectedCountry);
        new PopupHandler(driver).selectItem(countryName);
        logger.info("Select country: {}", countryName);
    }

    /**
     * Inputs the supplier's address for Vietnamese suppliers.
     */
    private void inputVietnamAddress() {
        String address = supplierInfo.getAddress();
        iosUtils.sendKeys(loc_txtVietnamAddress, address);
        logger.info("Input address: {}", address);
    }

    /* Vietnam Address Handling */

    /**
     * Selects the city for Vietnamese suppliers.
     */
    private void selectVietnamCity() {
        String cityName = supplierInfo.getVietnamCityName();
        iosUtils.click(loc_lblSelectedVietnamCity);
        new PopupHandler(driver).selectItem(cityName);

        logger.info("Select city: {}", cityName);
    }

    /**
     * Selects the district for Vietnamese suppliers.
     */
    private void selectVietnamDistrict() {
        String districtName = supplierInfo.getVietnamDistrictName();
        iosUtils.click(loc_lblSelectedVietnamDistrict);
        new PopupHandler(driver).selectItem(districtName);
        logger.info("Select district: {}", districtName);
    }

    /**
     * Selects the ward for Vietnamese suppliers.
     */
    private void selectVietnamWard() {
        String wardName = supplierInfo.getVietnamWardName();
        iosUtils.click(loc_lblSelectedVietnamWard);
        new PopupHandler(driver).selectItem(wardName);
        logger.info("Select ward: {}", wardName);
    }

    /* Foreign Address Handling */

    /**
     * Inputs the street address for foreign suppliers.
     */
    private void inputForeignStreetAddress() {
        String address = supplierInfo.getAddress();
        iosUtils.sendKeys(loc_txtForeignStreetAddress, address);
        logger.info("Input street address: {}", address);
    }

    /**
     * Inputs the second address line for foreign suppliers.
     */
    private void inputForeignAddress2() {
        String address2 = supplierInfo.getAddress2();
        iosUtils.sendKeys(loc_txtForeignAddress2, address2);
        logger.info("Input address2: {}", address2);
    }

    /**
     * Inputs the city name for foreign suppliers.
     */
    private void inputForeignCity() {
        String cityName = supplierInfo.getCityName();
        iosUtils.sendKeys(loc_txtForeignCity, cityName);
        logger.info("Input city: {}", cityName);
    }

    /**
     * Selects the province for foreign suppliers.
     */
    private void selectForeignProvince() {
        String provinceName = supplierInfo.getForeignProvinceName();
        iosUtils.click(loc_lblSelectedForeignProvince);
        new PopupHandler(driver).selectItem(provinceName);
        logger.info("Select province: {}", provinceName);
    }

    /**
     * Inputs the postal code (zipcode) for foreign suppliers.
     */
    private void inputForeignZipcode() {
        String zipcode = supplierInfo.getZipCode();
        if (zipcode.isEmpty()) return;
        iosUtils.sendKeys(loc_txtForeignZipcode, zipcode);
        logger.info("Input zipcode: {}", zipcode);
    }

    /**
     * Selects the responsible staff member from a dropdown if a staff name is provided.
     * Logs an appropriate message if no staff name is available.
     */
    private void selectResponsibleStaff() {
        String staffName = supplierInfo.getResponsibleStaffName();
        if (!staffName.isEmpty()) {
            iosUtils.click(loc_lblSelectedResponsibleStaff);
            iosUtils.click(loc_ddvResponsibleStaff(staffName));
            logger.info("Select responsible staff: {}", staffName);
            return;
        }

        logger.info("Store does not have any staff, so we cannot select responsible staff for the supplier.");
    }

    /**
     * Completes the process of creating or updating a supplier by clicking the "Save" button.
     */
    void saveChanges() {
        iosUtils.click(loc_btnHeaderSave);
        logger.info("Completed save changes to supplier information");

        // If it's an update, no need to fetch the supplier ID again.
        if (supplierInfo.getId() != null) return;

        // Wait for the API response to retrieve the new supplier's ID
        WebUtils.sleep(3000);
        int supplierId = new APIGetSupplierList(credentials).searchSupplierIdByName(supplierInfo.getName());

        // Log the creation of the new supplier
        logger.info("Completed creation of supplier, ID: {}", supplierId);

        // Set the new supplier ID
        supplierInfo.setId(supplierId);
    }

    /**
     * Creates a new supplier using the details stored in the `supplierInfo` object.
     * This method handles both Vietnamese and foreign suppliers, selecting the appropriate fields
     * based on the country code.
     *
     * @return The current instance of {@link BaseSupplierPage}.
     */
    public IOSBaseSupplierScreen createNewSupplier() {
        selectCountry();
        inputSupplierName();
        inputSupplierCode();
        inputPhoneNumber();
        inputEmail();

        if (supplierInfo.getCountryCode().equals("VN")) {
            handleVietNamAddress();
        } else {
            handleForeignAddress();
        }

        selectResponsibleStaff();
        saveChanges();

        return this;
    }

    /**
     * Handles the process of inputting the address details for Vietnamese suppliers.
     */
    private void handleVietNamAddress() {
        inputVietnamAddress();
        selectVietnamCity();
        selectVietnamDistrict();
        selectVietnamWard();
    }

    /**
     * Handles the process of inputting the address details for foreign suppliers.
     */
    private void handleForeignAddress() {
        inputForeignStreetAddress();
        inputForeignAddress2();
        inputForeignCity();
        selectForeignProvince();
        inputForeignZipcode();
    }

    /**
     * Verifies the supplier's information by comparing the expected values with the actual data fetched
     * from the API after the supplier has been created or updated.
     */
    public void verifySupplierInformation() {
        // Fetch the actual supplier data from the API
        APIGetSupplierDetail.SupplierInformation actualSupplierInfo = new APIGetSupplierDetail(credentials)
                .getSupplierInformation(supplierInfo.getId());
        logger.info("Fetched current supplier information for verification");

        // Compare each field to ensure it matches the expected values
        Assert.assertEquals(actualSupplierInfo.getName(), supplierInfo.getName(),
                "Supplier name does not match.");
        Assert.assertEquals(actualSupplierInfo.getCode(), supplierInfo.getCode(),
                "Supplier code does not match.");
        Assert.assertEquals(actualSupplierInfo.getPhoneNumber(), supplierInfo.getPhoneNumber(),
                "Phone number does not match.");
        Assert.assertEquals(actualSupplierInfo.getEmail(), supplierInfo.getEmail(),
                "Email does not match.");
        Assert.assertEquals(actualSupplierInfo.getCountryCode(), supplierInfo.getCountryCode(),
                "Country code does not match.");
        Assert.assertEquals(actualSupplierInfo.getAddress(), supplierInfo.getAddress(),
                "Address does not match.");
        Assert.assertEquals(Optional.ofNullable(actualSupplierInfo.getAddress2()).orElse(""), supplierInfo.getAddress2(),
                "Address2 does not match.");
        Assert.assertEquals(Optional.ofNullable(actualSupplierInfo.getCityName()).orElse(""), supplierInfo.getCityName(),
                "City name does not match.");
        Assert.assertEquals(actualSupplierInfo.getProvince(), supplierInfo.getProvince(),
                "Province does not match.");
        Assert.assertEquals(Optional.ofNullable(actualSupplierInfo.getDistrict()).orElse(""), supplierInfo.getDistrict(),
                "District does not match.");
        Assert.assertEquals(Optional.ofNullable(actualSupplierInfo.getWard()).orElse(""), supplierInfo.getWard(),
                "Ward does not match.");
        Assert.assertEquals(Optional.ofNullable(actualSupplierInfo.getZipCode()).orElse(""), supplierInfo.getZipCode(),
                "Zipcode does not match.");
        Assert.assertEquals(actualSupplierInfo.getResponsibleStaff(), supplierInfo.getResponsibleStaff(),
                "Responsible staff does not match.");

        logger.info("Supplier information verification completed.");
    }

    /**
     * The PopupHandler class serves as a base class for managing popup interactions.
     * It provides common functionality such as searching for items, selecting them,
     * and handling the close button logic. Derived classes represent specific popups.
     */
    private static class PopupHandler {
        private final IOSUtils iosUtils;

        /**
         * Constructor initializes the PopupHandler with the given WebDriver instance.
         *
         * @param driver the WebDriver used to interact with the application
         */
        public PopupHandler(WebDriver driver) {
            this.iosUtils = new IOSUtils(driver);
        }

        /**
         * Constructs the XPath locator for dropdown items containing the specified text.
         *
         * @param text the text to locate in the dropdown items
         * @return a By object representing the XPath for the dropdown value
         */
        private By getDropdownValueLocator(String text) {
            return AppiumBy.iOSClassChain("**/XCUIElementTypeCell/XCUIElementTypeStaticText[`name == \"%s\"`]".formatted(text));
        }

        /**
         * Selects an item in the popup by searching for it and clicking the result.
         * Handles closing the popup if the close button is present.
         *
         * @param itemName the name of the item to select
         */
        public void selectItem(String itemName) {
            // Select item
            iosUtils.click(getDropdownValueLocator(itemName));

            // Select item again if the previous step element is not visible screen
            if (!iosUtils.getListElement(getDropdownValueLocator(itemName), 1000).isEmpty()) {
                iosUtils.click(getDropdownValueLocator(itemName));
            }

            LogManager.getLogger().info("Select item: {}", itemName);
        }
    }
}
