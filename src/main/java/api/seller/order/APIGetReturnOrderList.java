package api.seller.order;

import api.seller.login.APISellerLogin;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import lombok.Data;
import utility.APIUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Handles retrieval of all return orders for a seller.
 */
public class APIGetReturnOrderList {

    private final APISellerLogin.LoginInformation loginInfo;

    /**
     * Constructs an APIGetAllReturnOrder instance.
     *
     * @param credentials the seller's API credentials
     */
    public APIGetReturnOrderList(APISellerLogin.Credentials credentials) {
        this.loginInfo = new APISellerLogin().getSellerInformation(credentials);
    }

    /**
     * Represents a return order.
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ReturnOrder {
        private String id;
        private String returnOrderId;
        private long bcOrderId;
        private int storeId;
        private long customerId;
        private String customerName;
        private String status;
        private int totalRefund;
        private int refundAmount;
        private String currency;
        private boolean restock;
        private String refundStatus;
        private String returnBranchId;
        private String returnBranchName;
        private String note;
        private String createdDate;
        private String createdBy;
        private String lastModifiedDate;
        private String lastModifiedBy;
    }

    /**
     * Retrieves the response containing all return orders for a specific page.
     *
     * @param pageIndex    the index of the page to retrieve
     * @param branchIds    the IDs of the branches to filter by
     * @param searchKeyword the keyword to search for
     * @return the response containing return orders
     */
    private Response getAllReturnOrderResponse(int pageIndex, String branchIds, String searchKeyword) {
        String endpoint = "/orderservices2/api/return-order/%s?page=%s&size=100&searchKeyword=%s&searchType=ORDER_ID&branchId=%s&restock=&status=&refundStatus=&staffName=";
        return new APIUtils()
                .get(endpoint.formatted(loginInfo.getStore().getId(), pageIndex, searchKeyword, branchIds), loginInfo.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

    /**
     * Retrieves a list of all return orders based on the specified branch IDs and search keyword.
     *
     * @param branchIds    the IDs of the branches to filter by
     * @param searchKeyword the keyword to search for
     * @return a list of return orders
     */
    public List<ReturnOrder> getAllReturnOrdersInformation(String branchIds, String searchKeyword) {
        List<ReturnOrder> returnOrderList = new ArrayList<>();

        // Get initial response to determine the total number of return orders
        Response response = getAllReturnOrderResponse(0, branchIds, searchKeyword);
        int totalOfReturnOrders = Integer.parseInt(response.getHeader("X-Total-Count"));
        int numberOfPages = (totalOfReturnOrders + 99) / 100; // Calculate number of pages

        // Retrieve return orders in parallel for each page
        List<String> responseStrings = IntStream.range(0, numberOfPages)
                .parallel()
                .mapToObj(pageIndex -> getAllReturnOrderResponse(pageIndex, branchIds, searchKeyword).asPrettyString())
                .toList();

        // Parse JSON responses into return order objects
        for (String responseString : responseStrings) {
            try {
                returnOrderList.addAll(new ObjectMapper().readValue(responseString, new TypeReference<List<ReturnOrder>>() {}));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error parsing JSON response", e);
            }
        }

        return returnOrderList;
    }
}
