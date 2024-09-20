package api.seller.supplier;

import api.seller.login.APIDashboardLogin;
import lombok.Data;
import utility.APIUtils;

import java.util.List;

/**
 * The {@code APIGetPurchaseOrderDetail} class handles the retrieval of purchase order details
 * from the seller's dashboard API using the provided credentials.
 */
public class APIGetPurchaseOrderDetail {

    APIDashboardLogin.SellerInformation loginInfo;
    APIDashboardLogin.Credentials credentials;

    /**
     * Constructs a new {@code APIGetPurchaseOrderDetail} object using the provided credentials.
     *
     * @param credentials the credentials used to log in and retrieve seller information
     */
    public APIGetPurchaseOrderDetail(APIDashboardLogin.Credentials credentials) {
        this.credentials = credentials;
        loginInfo = new APIDashboardLogin().getSellerInformation(credentials);
    }

    /**
     * Data class representing the detailed information of a purchase order.
     */
    @Data
    public static class PurchaseOrderInformation {
        private int id;
        private String purchaseId;
        private Supplier supplier;
        private List<PurchaseOrderItem> purchaseOrderItems;
        private Discount discount;
        private List<Object> purchaseCosts; // Can be modeled later based on actual structure
        private String status;
        private String note;
        private int branchId;
        private List<Timeline> timelines;
        private String createdBy;
        private List<PurchaseOrderPayment> purchaseOrderPayments;
        private boolean forAccounting;
        private SupplierDebt supplierDebt;
        private String purchaseOrderPaymentStatus;

        @Data
        public static class Supplier {
            private int id;
            private String name;
            private String code;
            private String phoneNumber;
            private String email;
            private String address;
            private String province;
            private String district;
            private String ward;
            private int storeId;
            private String status;
            private String address2;
            private String countryCode;
            private String phoneCode;
            private String cityName;
            private String zipCode;
            private boolean codeGenerated;
        }

        @Data
        public static class PurchaseOrderItem {
            private int id;
            private int quantity;
            private int importPrice;
            private long itemId;
            private long modelId;
            private String itemName;
            private String itemVariationName;
            private String modelLabel;
            private String modelName;
            private ItemImage itemImageDto;
            private String inventoryManageType;
            private List<Object> codeList;  // Can be modeled later
            private List<Object> purchaseOrderItemLotRequests;  // Can be modeled later
            private int inventory;

            @Data
            public static class ItemImage {
                private String createdDate;
                private String lastModifiedDate;
                private long id;
                private String urlPrefix;
                private int rank;
                private String imageUUID;
                private String extension;
                private long itemId;
            }
        }

        @Data
        public static class Discount {
            private String type;
            private int value;
        }

        @Data
        public static class Timeline {
            private String status;
            private String createdDate;
        }

        @Data
        public static class PurchaseOrderPayment {
            private int id;
            private String paymentMethod;
            private int amount;
        }

        @Data
        public static class SupplierDebt {
            private int id;
            private Supplier supplier;
            private int storeId;
            private int branchId;
            private String branchName;
            private String name;
            private String staffName;
            private PurchaseOrderInfo purchaseOrder;
            private boolean published;
            private String description;
            private int subTotal;
            private int originalAmount;
            private int balanceAmount;
            private int currentDebtOfSupplier;
            private String createdBy;
            private String lastModifiedBy;
            private String createdDate;
            private String lastModifiedDate;
            private String status;
            private String receiptType;

            @Data
            public static class PurchaseOrderInfo {
                private String createdBy;
                private String createdDate;
                private String lastModifiedBy;
                private String lastModifiedDate;
                private int id;
                private String purchaseId;
                private String note;
                private String status;
                private Supplier supplier;
                private int storeId;
                private int branchId;
                private String discountType;
                private int discountValue;
                private int amount;
                private boolean forAccounting;
            }
        }
    }

    /**
     * Retrieves the purchase order information for the given purchase ID.
     *
     * @param purchaseId the ID of the purchase order to retrieve
     * @return a {@code PurchaseOrderInformation} object containing detailed information
     * about the specified purchase order
     */
    public PurchaseOrderInformation getPurchaseOrderInformation(int purchaseId) {
        return new APIUtils().get("/itemservice/api/purchase-orders/%s".formatted(purchaseId), loginInfo.getAccessToken())
                .then()
                .statusCode(200)
                .extract().as(PurchaseOrderInformation.class);
    }
}
