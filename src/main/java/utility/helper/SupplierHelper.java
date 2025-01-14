package utility.helper;

import api.others.APIGetDistricts;
import api.others.APIGetProvinces;
import api.others.APIGetWards;
import api.seller.login.APISellerLogin;
import api.seller.setting.APIGetStaffList;
import utility.CountryUtils;
import utility.PropertiesUtils;

import java.time.Instant;
import java.time.LocalDateTime;

import static api.seller.supplier.APIGetSupplierDetail.SupplierInformation;

public class SupplierHelper {
    private static final String langKey = PropertiesUtils.getLangKey();

    /**
     * Generates supplier information based on provided credentials and country preference.
     *
     * @param credentials  The credentials for API access.
     * @param isVNSupplier Indicates whether the supplier is based in Vietnam.
     * @return Populated SupplierInformation object.
     */
    public static SupplierInformation generateSupplierInformation(APISellerLogin.Credentials credentials, boolean isVNSupplier) {
        SupplierInformation supplierInfo = new SupplierInformation();

        // Determine country details
        String countryName = isVNSupplier ? "Vietnam" : CountryUtils.randomCountry();
        supplierInfo.setCountryName(langKey.equals("vi") && isVNSupplier ? "Việt Nam" : countryName);
        supplierInfo.setCountryCode(CountryUtils.getCountryCode(countryName));

        // Generate base attributes
        LocalDateTime currentDateTime = LocalDateTime.now();
        long epochMillis = Instant.now().toEpochMilli();
        supplierInfo.setStoreId(new APISellerLogin().getSellerInformation(credentials).getStore().getId());
        supplierInfo.setName("[%s] Supplier name %s".formatted(countryName, currentDateTime));
        supplierInfo.setCode("CODE%s".formatted(epochMillis % 100_000_000));
        supplierInfo.setPhoneCode(CountryUtils.getPhoneCode(countryName));
        supplierInfo.setPhoneNumber(String.valueOf(epochMillis));
        supplierInfo.setEmail("%s@qa.team".formatted(epochMillis));
        supplierInfo.setAddress("Address %s".formatted(currentDateTime));
        supplierInfo.setDescription("Description %s".formatted(currentDateTime));

        // Responsible staff
        var staff = new APIGetStaffList(credentials).randomStaff();
        supplierInfo.setResponsibleStaff(staff.getId() == 0 ? "" : String.valueOf(staff.getId()));
        supplierInfo.setResponsibleStaffName(staff.getName());

        // Province: Random based on countryCode
        var province = new APIGetProvinces(credentials).randomProvince(supplierInfo.getCountryCode());
        String provinceCode = province.getCode();
        supplierInfo.setProvince(provinceCode);

        // Handle Vietnam-specific details
        if (isVNSupplier) {
            supplierInfo.setVietnamCityName(langKey.equals("vi") ? province.getInCountry() : province.getOutCountry());
            populateVietnamSpecificAttributes(credentials, supplierInfo, provinceCode);
            return supplierInfo;
        }

        // Handle foreign country-specific details
        supplierInfo.setForeignProvinceName(langKey.equals("vi") ? province.getInCountry() : province.getOutCountry());
        populateForeignSpecificAttributes(supplierInfo);
        return supplierInfo;
    }

    /**
     * Populates attributes specific to Vietnam suppliers.
     *
     * @param credentials  API credentials for fetching location details.
     * @param supplierInfo SupplierInformation object to populate.
     * @param provinceCode The code of the selected province.
     */
    private static void populateVietnamSpecificAttributes(APISellerLogin.Credentials credentials, SupplierInformation supplierInfo, String provinceCode) {
        var district = new APIGetDistricts(credentials).randomDistrict(provinceCode);
        var ward = new APIGetWards(credentials).randomWard(district.getCode());

        supplierInfo.setDistrict(district.getCode());
        supplierInfo.setVietnamDistrictName(langKey.equals("vi") ? district.getInCountry() : district.getOutCountry());
        supplierInfo.setWard(ward.getCode());
        supplierInfo.setVietnamWardName(langKey.equals("vi") ? ward.getInCountry() : ward.getOutCountry());
        supplierInfo.setAddress2(""); // Address2 not used for Vietnam
        supplierInfo.setCityName(""); // City not used for Vietnam
        supplierInfo.setZipCode("");  // Zip code not used for Vietnam
    }

    /**
     * Populates attributes specific to foreign country suppliers.
     *
     * @param supplierInfo SupplierInformation object to populate.
     */
    private static void populateForeignSpecificAttributes(SupplierInformation supplierInfo) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        long epochMillis = Instant.now().toEpochMilli();

        supplierInfo.setCityName("City %s".formatted(currentDateTime));
        supplierInfo.setDistrict(""); // District not used for foreign countries
        supplierInfo.setWard("");     // Ward not used for foreign countries
        supplierInfo.setAddress2("Address2 %s".formatted(currentDateTime));
        supplierInfo.setZipCode(String.valueOf(epochMillis));
    }
}