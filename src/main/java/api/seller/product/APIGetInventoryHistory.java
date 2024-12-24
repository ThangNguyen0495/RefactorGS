package api.seller.product;

import api.seller.affiliate.APIGetPartnerTransferDetail;
import api.seller.login.APISellerLogin;
import api.seller.order.APIGetReturnOrderList;
import api.seller.order.APIGetOrderDetail;
import api.seller.supplier.APIGetPurchaseOrderDetail;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.restassured.response.Response;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import utility.APIUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Class responsible for retrieving inventory history data via the API.
 */
public class APIGetInventoryHistory {
    private final APISellerLogin.LoginInformation loginInfo;
    private final APISellerLogin.Credentials credentials;

    private static final String INVENTORY_HISTORY_PATH = "/itemservice/api/inventory-search/%s?search=%s&branchIds=%s&page=%s&size=100";

    /**
     * Constructs an instance of APIGetInventoryHistory with the provided credentials.
     *
     * @param credentials The credentials required to authenticate and retrieve seller information.
     */
    public APIGetInventoryHistory(APISellerLogin.Credentials credentials) {
        this.credentials = credentials;
        this.loginInfo = new APISellerLogin().getSellerInformation(credentials);
    }

    /**
     * A data model representing the inventory history of a product.
     * Contains information such as product name, stock changes, remaining stock, and other details.
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class InventoryHistory {
        private String productName;   // Name of the product
        private long stockChange;     // Change in stock
        private long remainingStock;  // Remaining stock
        private String inventoryType; // Type of inventory
        private String actionType;    // Type of action (e.g., transfer, sale)
        private String orderId;       // Associated order ID, if applicable
        private String operator;      // Operator responsible for the action
        private String id;            // Inventory history entry ID
        private boolean hasConversion;// Indicates if a unit conversion is involved
    }

    /**
     * Retrieves the inventory history response from the API for a specific page.
     *
     * @param pageIndex The index of the page to retrieve.
     * @param keywords  The search keywords to filter inventory history results.
     * @param branchId  The ID of the branch to filter inventory history by.
     * @return The API response containing inventory history data for the specified page.
     */
    private Response getInventoryResponse(int pageIndex, String keywords, String branchId) {
        String url = INVENTORY_HISTORY_PATH.formatted(
                loginInfo.getStore().getId(), keywords, branchId, pageIndex
        );
        return new APIUtils().get(url, loginInfo.getAccessToken(), Map.of("langkey", "vi"))
                .then()
                .statusCode(200)
                .extract()
                .response();
    }


    /**
     * Retrieves all pages of inventory history.
     *
     * @param keyword   The search keyword for filtering inventory history.
     * @param branchIds The branch IDs to filter inventory history.
     * @return A list of InventoryHistory objects.
     */
    public List<APIGetInventoryHistory.InventoryHistory> getAllInventoryHistory(String keyword, String branchIds) {
        int totalCount = Integer.parseInt(getInventoryResponse(0, keyword, branchIds).getHeader("X-Total-Count"));
        int numberOfPages = totalCount / 100;

        // Fetch all pages of inventory history
        return IntStream.rangeClosed(0, numberOfPages)
                .parallel()
                .mapToObj(pageIndex -> getInventoryResponse(pageIndex, keyword, branchIds).jsonPath())
                .flatMap(jsonPath -> jsonPath.getList(".", InventoryHistory.class).stream())
                .collect(Collectors.toList());
    }

    /**
     * Checks if a product can be deleted based on its inventory history.
     * <p>
     * This method filters the inventory history of a product to determine if any
     * transfer orders with a prefix "CH" are incomplete (not "RECEIVED" or "CANCELLED").
     *
     * @param productId The product ID to check.
     * @return True if the product can be deleted (no incomplete transfers), false otherwise.
     */
    public boolean checkProductCanBeDeleted(int productId) {
        List<InventoryHistory> inventoryHistoryList = getAllInventoryHistory(String.valueOf(productId), "");

        LogManager.getLogger().info("===== STEP =====> [CheckCanBeDeleted] ProductId: {} ", productId);
        return inventoryHistoryList.stream()
                .filter(history -> history.getOrderId() != null && history.getOrderId().contains("CH"))
                .noneMatch(this::hasTransferInComplete);
    }

    /**
     * Checks if a product can be managed by lot date based on its inventory history.
     * <p>
     * This method checks if the inventory history contains any incomplete orders or transfers,
     * including purchase orders, transfers, orders, return orders, or partner transfer,
     * that would prevent management by lot date.
     *
     * @param productId The product ID to check.
     * @return True if the product can be managed by lot date, false otherwise.
     */
    public boolean checkProductCanBeManagedByLotDate(int productId) {
        var inventoryHistoryList = getAllInventoryHistory(String.valueOf(productId), "");

        LogManager.getLogger().info("===== STEP =====> [CheckCanBeManagedByLotDate] ProductId: {} ", productId);
        return inventoryHistoryList.stream()
                .filter(history -> history.getOrderId() != null)
                .allMatch(this::canManageStockByLotDate);
    }

    /**
     * Determines if an inventory history item can be managed by lot based on its action type and status.
     * <p>
     * This method checks various scenarios such as transfers, purchase orders, sales, and return orders to ensure
     * that none of them are in an incomplete state before allowing stock management by lot.
     * </p>
     * <ul>
     *   <li>{@code FROM_LOCK}: The order is in an initial state and considered incomplete, so stock cannot be managed.</li>
     *   <li>{@code FROM_EDIT_ORDER}: The order is being updated ("To Confirm" status), and it is still incomplete.</li>
     *   <li>{@code FROM_SOLD}: The order has been delivered, but if the return order is incomplete, stock cannot be managed by lot.</li>
     *   <li>{@code orderId.contains("CH")}: Indicates a transfer or partner transfer; if the transfer is incomplete, stock cannot be managed.</li>
     *   <li>{@code orderId.contains("PO")}: Indicates a purchase order; if the purchase order is incomplete, stock cannot be managed by lot.</li>
     * </ul>
     * <p>
     * A product can only be managed by lot if it is not part of an incomplete order, transfer, partner transfer,
     * return order, or purchase order.
     * </p>
     *
     * @param history The inventory history item to check.
     * @return True if the product can be managed by lot, false otherwise.
     */

    private boolean canManageStockByLotDate(InventoryHistory history) {
        String orderId = history.getOrderId();
        String actionType = history.getActionType();

        if (orderId.contains("CH")) {
            // Transfer or partner transfer
            return !hasTransferInComplete(history);
        }

        if (orderId.contains("PO")) {
            // Purchase order
            return !hasPurchaseOrderInComplete(history);
        }

        return switch (actionType) {
            case "FROM_LOCK" -> false; // Initial order status, incomplete order
            case "FROM_EDIT_ORDER" -> !hasOrderInComplete(history); // Updated order, still incomplete
            case "FROM_SOLD" -> !hasReturnOrderInComplete(history); // Delivered order, but return order must be complete
            default -> true; // Can manage by lot if none of the above conditions are met
        };
    }



    /**
     * Checks if a transfer-related history item is not completed or cancelled.
     * <p>
     * This method checks the status of the transfer and returns false if the status is "RECEIVED" or "CANCELLED".
     *
     * @param history The inventory history item.
     * @return True if the transfer status is not "RECEIVED" or "CANCELLED", false otherwise.
     */
    private boolean hasTransferInComplete(InventoryHistory history) {
        String orderIdWithoutPrefix = history.getOrderId().replaceAll("CH", "");
        String status = history.getActionType().equals("FROM_TRANSFER_AFFILIATE_OUT")
                ? new APIGetPartnerTransferDetail(credentials).getPartnerTransferInformation(Integer.parseInt(orderIdWithoutPrefix)).getStatus()
                : new APIGetTransferDetail(credentials).getTransferInformation(Integer.parseInt(orderIdWithoutPrefix)).getStatus();

        return !status.equals("RECEIVED") && !status.equals("CANCELLED");
    }

    /**
     * Checks if a purchase order-related history item is completed or cancelled.
     * <p>
     * This method verifies that the purchase order status is either "COMPLETED" or "CANCELLED".
     *
     * @param history The inventory history item.
     * @return True if the purchase order status is "COMPLETED" or "CANCELLED", false otherwise.
     */
    private boolean hasPurchaseOrderInComplete(InventoryHistory history) {
        String orderIdWithoutPrefix = history.getOrderId().replaceAll("PO", "");
        String status = new APIGetPurchaseOrderDetail(credentials).getPurchaseOrderInformation(Integer.parseInt(orderIdWithoutPrefix)).getStatus();

        return !status.equals("COMPLETED") && !status.equals("CANCELLED");
    }

    /**
     * Checks if an order-related history item is delivered or cancelled.
     * <p>
     * This method checks that the order status is either "DELIVERED", "CANCELLED", "REJECTED", or "FAILED".
     *
     * @param history The inventory history item.
     * @return True if the order status is one of the expected values, false otherwise.
     */
    private boolean hasOrderInComplete(InventoryHistory history) {
        String status = new APIGetOrderDetail(credentials).getOrderInformation(Integer.parseInt(history.getOrderId()))
                .getOrderInfo()
                .getStatus();

        return !List.of("DELIVERED", "CANCELLED", "REJECTED", "FAILED").contains(status);
    }

    /**
     * Checks if any return orders are in progress for a sold inventory history item.
     * <p>
     * This method checks if there are any "IN_PROGRESS" return orders for the given product history.
     *
     * @param history The inventory history item.
     * @return True if there are return orders in progress, false otherwise.
     */
    private boolean hasReturnOrderInComplete(InventoryHistory history) {
        var returnOrders = new APIGetReturnOrderList(credentials).getAllReturnOrdersInformation("", history.getOrderId());
        return returnOrders.stream().anyMatch(returnOrder -> returnOrder.getStatus().equals("IN_PROGRESS"));
    }
}
