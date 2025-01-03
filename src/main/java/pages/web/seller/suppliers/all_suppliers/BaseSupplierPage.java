package pages.web.seller.suppliers.all_suppliers;

import api.seller.supplier.APIGetSupplierDetail;
import api.seller.supplier.APIGetSupplierDetail.SupplierInformation;
import api.seller.supplier.APIGetSupplierList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import utility.CountryUtils;
import utility.PropertiesUtils;
import utility.WebUtils;
import utility.helper.SupplierHelper;

import static api.seller.login.APISellerLogin.Credentials;
import static org.apache.commons.lang.math.RandomUtils.nextInt;

/**
 * This class handles actions related to supplier management, including creating suppliers,
 * filling out their details, and verifying supplier information after creation or update.
 * It provides methods for navigating to the create supplier page, inputting supplier information,
 * saving the supplier data, and verifying that the supplier data is correctly saved.
 */
public class BaseSupplierPage extends BaseSupplierElement {
    private final WebDriver driver;
    private final WebUtils webUtils;

    private final Logger logger = LogManager.getLogger();
    private SupplierInformation supplierInfo;
    private Credentials credentials;

    /**
     * Constructor that initializes the WebDriver and WebUtils instances for interacting with web elements.
     *
     * @param driver The WebDriver instance used to interact with the web page.
     */
    public BaseSupplierPage(WebDriver driver) {
        this.driver = driver;
        webUtils = new WebUtils(driver);
    }

    /**
     * Fetches supplier information from the API based on the credentials provided and stores it
     * in the `supplierInfo` object.
     *
     * @param credentials       The credentials required for API authentication.
     * @param isVietnamSupplier Flag indicating whether the supplier is based in Vietnam.
     * @return The current instance of {@link BaseSupplierPage}.
     */
    public BaseSupplierPage fetchSupplierInformation(Credentials credentials, boolean isVietnamSupplier) {
        this.credentials = credentials;
        this.supplierInfo = SupplierHelper.generateSupplierInformation(credentials, isVietnamSupplier);
        String supplierId = driver.getCurrentUrl().replaceAll("\\D+", "");
        if (!supplierId.isEmpty()) supplierInfo.setId(Integer.parseInt(supplierId));
        return this;
    }

    /**
     * Navigates to the page where a new supplier can be created.
     */
    public void navigateToCreateSupplierPage() {
        webUtils.executeWithAlertHandling(() -> {
            driver.get("%s/supplier/create".formatted(PropertiesUtils.getDomain()));
            logger.info("Navigated to create supplier page.");
        });

    }

    /**
     * Navigates to the supplier detail page for the given supplier ID.
     * This method uses the supplier's ID to construct the URL and navigates to the edit page of the supplier's details.
     *
     * @param supplierId The unique identifier of the supplier whose details are to be viewed or edited.
     */
    public void navigateToSupplierDetailPageByItsId(int supplierId) {
        webUtils.executeWithAlertHandling(() -> {
            driver.get("%s/supplier/edit/%d".formatted(PropertiesUtils.getDomain(), supplierId));
            logger.info("Navigated to supplier detail page, id: {}.", supplierId);
        });
       }

    /**
     * Inputs the supplier's name into the corresponding text field.
     */
    private void inputSupplierName() {
        String supplierName = supplierInfo.getName();
        webUtils.sendKeys(loc_txtSupplierName, supplierName);
        logger.info("Input supplier name: {}", supplierName);
    }

    /**
     * Inputs the supplier's code into the corresponding text field.
     */
    private void inputSupplierCode() {
        String supplierCode = supplierInfo.getCode();
        webUtils.sendKeys(loc_txtSupplierCode, supplierCode);
        logger.info("Input supplier code: {}", supplierCode);
    }

    /**
     * Inputs the supplier's phone number into the corresponding text field.
     */
    private void inputPhoneNumber() {
        String phoneNumber = supplierInfo.getPhoneNumber();
        webUtils.sendKeys(loc_txtPhoneNumber, phoneNumber);
        logger.info("Input phone number: {}", phoneNumber);
    }

    /**
     * Inputs the supplier's email address into the corresponding text field.
     */
    private void inputEmail() {
        String email = supplierInfo.getEmail();
        webUtils.sendKeys(loc_txtEmail, email);
        logger.info("Input email: {}", email);
    }

    /**
     * Selects the country for the supplier based on the country code stored in the supplier information.
     */
    private void selectCountry() {
        String countryCode = supplierInfo.getCountryCode();
        String countryName = CountryUtils.getCountryNameByCode(countryCode);
        webUtils.selectDropdownOptionByValue(loc_ddvSelectedCountry, countryCode);
        logger.info("Select country: {}", countryName);
    }

    /**
     * Inputs the supplier's address for Vietnamese suppliers.
     */
    private void inputVietnamAddress() {
        String address = supplierInfo.getAddress();
        webUtils.sendKeys(loc_txtVietnamAddress, address);
        logger.info("Input address: {}", address);
    }

    /* Vietnam Address Handling */

    /**
     * Selects the city for Vietnamese suppliers.
     */
    private void selectVietnamCity() {
        String cityCode = supplierInfo.getProvince();
        webUtils.selectDropdownOptionByValue(loc_lblSelectedVietnamCity, cityCode);
        logger.info("Select city: {}", cityCode);
    }

    /**
     * Selects the district for Vietnamese suppliers.
     */
    private void selectVietnamDistrict() {
        String districtCode = supplierInfo.getDistrict();
        webUtils.selectDropdownOptionByValue(loc_lblSelectedVietnamDistrict, districtCode);
        logger.info("Select district: {}", districtCode);
    }

    /**
     * Selects the ward for Vietnamese suppliers.
     */
    private void selectVietnamWard() {
        String wardCode = supplierInfo.getWard();
        webUtils.selectDropdownOptionByValue(loc_lblSelectedVietnamWard, wardCode);
        logger.info("Select ward: {}", wardCode);
    }

    /* Foreign Address Handling */

    /**
     * Inputs the street address for foreign suppliers.
     */
    private void inputForeignStreetAddress() {
        String address = supplierInfo.getAddress();
        webUtils.sendKeys(loc_txtForeignStreetAddress, address);
        logger.info("Input street address: {}", address);
    }

    /**
     * Inputs the second address line for foreign suppliers.
     */
    private void inputForeignAddress2() {
        String address2 = supplierInfo.getAddress2();
        webUtils.sendKeys(loc_txtForeignAddress2, address2);
        logger.info("Input address2: {}", address2);
    }

    /**
     * Inputs the city name for foreign suppliers.
     */
    private void inputForeignCity() {
        String cityName = supplierInfo.getCityName();
        webUtils.sendKeys(loc_txtForeignCity, cityName);
        logger.info("Input city: {}", cityName);
    }

    /**
     * Selects the province for foreign suppliers.
     */
    private void selectForeignProvince() {
        String provinceCode = supplierInfo.getProvince();
        webUtils.selectDropdownOptionByValue(loc_lblSelectedForeignProvince, provinceCode);
        logger.info("Select province: {}", provinceCode);
    }

    /**
     * Inputs the postal code (zipcode) for foreign suppliers.
     */
    private void inputForeignZipcode() {
        String zipcode = supplierInfo.getZipCode();
        if (zipcode.isEmpty()) return;
        webUtils.sendKeys(loc_txtForeignZipcode, zipcode);
        logger.info("Input zipcode: {}", zipcode);
    }

    /**
     * Selects the responsible staff member from a dropdown.
     */
    private void selectResponsibleStaff() {
        int index = nextInt(webUtils.getListElement(loc_ddlResponsibleStaff).size());
        String staffName = webUtils.getText(loc_ddlResponsibleStaff, index);
        String staffId = webUtils.getValue(loc_ddlResponsibleStaff, index);
        webUtils.selectDropdownOptionByValue(loc_lblSelectedResponsibleStaff, staffId);
        logger.info("Select responsible staff: {}", staffName);
    }

    /**
     * Inputs a description for the supplier in the description field.
     */
    private void inputDescription() {
        String description = supplierInfo.getDescription();
        webUtils.sendKeys(loc_txtDescription, description);
        logger.info("Input description: {}", description);
    }

    /**
     * Completes the process of creating or updating a supplier by clicking the "Save" button.
     */
    void saveChanges() {
        webUtils.click(loc_btnHeaderSave);
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
    public BaseSupplierPage createNewSupplier() {
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
        inputDescription();
        saveChanges();

        // Ensure the supplier creation was successful
        webUtils.waitForCondition(ExpectedConditions.urlContains("/supplier/list"), 30_000);
        Assert.assertTrue(driver.getCurrentUrl().contains("/supplier/list"),
                "Cannot create/update supplier.");

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
        SupplierInformation actualSupplierInfo = new APIGetSupplierDetail(credentials)
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
        Assert.assertEquals(actualSupplierInfo.getAddress2(), supplierInfo.getAddress2(),
                "Address2 does not match.");
        Assert.assertEquals(actualSupplierInfo.getCityName(), supplierInfo.getCityName(),
                "City name does not match.");
        Assert.assertEquals(actualSupplierInfo.getProvince(), supplierInfo.getProvince(),
                "Province does not match.");
        Assert.assertEquals(actualSupplierInfo.getDistrict(), supplierInfo.getDistrict(),
                "District does not match.");
        Assert.assertEquals(actualSupplierInfo.getWard(), supplierInfo.getWard(),
                "Ward does not match.");
        Assert.assertEquals(actualSupplierInfo.getZipCode(), supplierInfo.getZipCode(),
                "Zipcode does not match.");
        Assert.assertEquals(actualSupplierInfo.getDescription(), supplierInfo.getDescription(),
                "Description does not match.");

        logger.info("Supplier information verification completed.");
    }
}
