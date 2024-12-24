package api.seller.supplier;

import api.seller.login.APISellerLogin;
import api.seller.supplier.APIGetSupplierDetail.SupplierInformation;
import io.restassured.response.Response;
import utility.APIUtils;

import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;

public class APIGetSupplierList {
    private final APISellerLogin.LoginInformation loginInfo;

    /**
     * Constructor to initialize the APIGetSupplierList with seller login credentials.
     *
     * @param credentials The login credentials for the seller account.
     */
    public APIGetSupplierList(APISellerLogin.Credentials credentials) {
        loginInfo = new APISellerLogin().getSellerInformation(credentials);
    }

    /**
     * Constructs the API endpoint URL for fetching the supplier list.
     *
     * @param keyword   The search keyword for filtering suppliers.
     * @param pageIndex The page index for pagination.
     * @return The constructed API endpoint URL.
     */
    private String getListSupplierPath(String keyword, int pageIndex) {
        return String.format("/itemservice/api/suppliers/store/%d?page=%d&size=20&sort=id,desc&itemNameOrCode=%s",
                loginInfo.getStore().getId(), pageIndex, keyword);
    }

    /**
     * Sends a request to the API to fetch a page of suppliers based on the search criteria.
     *
     * @param keywords  The keywords to filter suppliers.
     * @param pageIndex The page index for pagination.
     * @return The response containing the supplier list for the specified page.
     */
    private Response getSupplierListResponse(String keywords, int pageIndex) {
        return new APIUtils().get(getListSupplierPath(keywords, pageIndex), loginInfo.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

    /**
     * Retrieves a list of all suppliers matching the search keyword.
     *
     * @param keyword The keyword to search for in supplier names.
     * @return A list of {@link SupplierInformation} matching the search criteria.
     */
    public List<SupplierInformation> getAllSupplierInformation(String keyword) {
        // Get the total number of suppliers
        int totalOfSuppliers = Integer.parseInt(getSupplierListResponse(keyword, 0).getHeader("X-Total-Count"));

        // Calculate the number of pages
        int numberOfPages = (totalOfSuppliers + 99) / 100; // Ensure rounding up

        // Fetch supplier data from all pages
        return IntStream.range(0, numberOfPages)
                .parallel()
                .mapToObj(pageIndex -> getSupplierListResponse(keyword, pageIndex).jsonPath().getList(".", SupplierInformation.class))
                .flatMap(Collection::stream)
                .toList();
    }

    /**
     * Searches for a supplier by name and returns its ID.
     *
     * @param name The name of the supplier to search for.
     * @return The ID of the supplier with the specified name.
     * @throws RuntimeException If no supplier with the given name is found.
     */
    public int searchSupplierIdByName(String name) {
        return getAllSupplierInformation(name).parallelStream()
                .filter(supplier -> supplier.getName().equals(name))
                .findAny()
                .map(SupplierInformation::getId)
                .orElseThrow(() -> new RuntimeException("Supplier with name '" + name + "' not found"));
    }
}