package api.seller.order;

import api.seller.login.APIDashboardLogin;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import utility.APIUtils;

import java.util.List;
import java.util.Map;

/**
 * Class for fetching detailed information about an order from the API.
 */
public class APIGetOrderDetail {

    private final APIDashboardLogin.SellerInformation loginInfo;

    /**
     * Constructor for APIGetOrderDetail.
     * Initializes the API login information using provided credentials.
     *
     * @param credentials API credentials for authentication.
     */
    public APIGetOrderDetail(APIDashboardLogin.Credentials credentials) {
        loginInfo = new APIDashboardLogin().getSellerInformation(credentials);
    }

    /**
     * Class representing all the details related to an order, including order info, customer info,
     * billing and shipping info, and order items.
     */
    @Data
    public static class OrderInformation {

        private OrderInfo orderInfo;
        private CustomerInfo customerInfo;
        private BillingInfo billingInfo;
        private ShippingInfo shippingInfo;
        private StoreBranch storeBranch;
        private List<Item> items;
        private List<OrderTagInfo> orderTagInfos;
        private List<Discount> summaryDiscounts;
        private List<CouponDiscount> couponDiscounts;
        private List<CouponDiscount> allCouponDiscounts;
        private int totalSummaryDiscounts;
        private int totalSummaryDiscountWithoutFreeshippingAndPoint;
        private boolean isNewOrder;
        private EarningPoint earningPoint;
        private BcOrderGroup bcOrderGroup;
        private boolean isMPOSRefund;
        private boolean newOrder;

        /**
         * Class representing the basic information of the order.
         */
        @Data
        public static class OrderInfo {
            private String orderId;
            private String orderNumber;
            private String createDate;
            private String lastModifiedDate;
            private String paymentMethod;
            private int itemsCount;
            private int totalQuantity;
            private boolean paid;
            private boolean isAllowEarningPoint;
            private String status;
            private int totalPrice;
            private int totalAmount;
            private int subTotal;
            private int subTotalAfterDiscount;
            private String note;
            private String currency;
            private int totalDiscount;
            private List<Object> discounts;
            private String transactionNumber;
            private int totalTaxAmount;
            private int pointAmount;
            private int receivedAmount;
            private int debtAmount;
            private String payType;
            private String channel;
            private int refundedAmount;
            private String inStoreCreatedBy;
            private String createdBy;
            private List<CouponCode> couponCodes;
            private String appInstall;
            private String inStore;
            private boolean isInStore;
        }

        /**
         * Class representing the customer information related to the order.
         */
        @Data
        public static class CustomerInfo {
            private int customerId;
            private String name;
            private String phone;
            private int userId;
            private int debtAmount;
            private boolean guest;
            private Avatar avatar;

            /**
             * Class representing the avatar details of the customer.
             */
            @Data
            public static class Avatar {
                private int imageId;
                private String urlPrefix;
                private String extension;
                private String fullUrl;
            }
        }

        /**
         * Class representing the billing information related to the order.
         */
        @Data
        public static class BillingInfo {
            private String contactName;
            private String address1;
            private String address2;
            private String address3;
            private String address4;
            private String address5;
            private String phone;
            private String phone2;
            private String country;
            private String countryCode;
            private String district;
            private String ward;
            private String outSideCity;
            private String zipCode;
            private String phoneCode;
            private String insideCityCode;
            private String stateCode;
            private String fullAddress;
            private String fullAddressEn;
        }

        /**
         * Class representing the shipping information for the order.
         */
        @Data
        public static class ShippingInfo {
            private String fullAddress;
            private String fullAddressEn;
        }

        /**
         * Class representing the store branch where the order was placed or processed.
         */
        @Data
        public static class StoreBranch {
            private String createdDate;
            private String lastModifiedDate;
            private int id;
            private String name;
            private int storeId;
            private String code;
            private String address;
            private String ward;
            private String district;
            private String city;
            private String phoneNumberFirst;
            private boolean isDefault;
            private String branchStatus;
            private String branchType;
            private String address2;
            private String countryCode;
            private String cityName;
            private String zipCode;
            private boolean status;
        }

        /**
         * Class representing an item in the order, including details about quantity, price, and discounts.
         */
        @Data
        public static class Item {
            private int id;
            private long itemId;
            private long variationId;
            private String name;
            private String variationName;
            private int price;
            private int totalAmount;
            private String currency;
            private String imageUrl;
            private int quantity;
            private String sku;
            private int weight;
            private int height;
            private int length;
            private int width;
            private String createdDate;
            private boolean isDeposit;
            private boolean flashSale;
            private String inventoryManageType;
            private List<Object> orderItemIMEIs;
            private boolean isHasLot;
            private boolean isHasLocation;
            private boolean isOrderCreatedBeforeItemEnabledLotDate;
            private List<Object> lstReturnedImei;
            private int totalDiscount;
            private int priceDiscount;
            private int totalQuantity;
            private List<ItemTotalDiscount> itemTotalDiscounts;

            /**
             * Class representing a discount applied to the item.
             */
            @Data
            public static class ItemTotalDiscount {
                private int value;
                private String discountType;
                private String label;
                private int referenceId;
            }
        }

        /**
         * Class representing a discount applied to the order.
         */
        @Data
        public static class Discount {
            private int value;
            private String discountType;
            private String label;
        }

        /**
         * Class representing a coupon discount applied to the order.
         */
        @Data
        public static class CouponDiscount {
            private int value;
            private String discountType;
            private String label;
            private String couponType;
        }

        /**
         * Class representing earning points details for the order.
         */
        @Data
        public static class EarningPoint {
            // Define fields if needed later
        }

        /**
         * Class representing a group of related orders, including order count and total price.
         */
        @Data
        public static class BcOrderGroup {
            private String createdDate;
            private String lastModifiedDate;
            private int id;
            private boolean paid;
            private List<Object> orders;
            private List<Integer> orderIds;
            private int orderCount;
            private int itemCount;
            private int totalQuantity;
            private int totalPrice;
            private boolean withSameProviderShouldReturnTheCheapest;
        }

        /**
         * Class representing coupon codes applied to the order.
         */
        @Data
        public static class CouponCode {
            private int discountValue;
            private String discountCode;
            private String couponType;
        }

        /**
         * Class representing custom tags or information associated with the order.
         */
        @Data
        @JsonIgnoreProperties
        public static class OrderTagInfo {
            // Define fields if needed later
        }
    }

    /**
     * Fetches the detailed order information using the provided orderId.
     *
     * @param orderId The unique identifier of the order.
     * @return OrderInformation containing all relevant details about the order.
     */
    public OrderInformation getOrderInformation(long orderId) {
        return new APIUtils().get("/orderservice3/api/gs/order-details/ids/%s?getLoyaltyEarningPoint=true".formatted(orderId), loginInfo.getAccessToken(), Map.of("langkey", "vi"))
                .then()
                .statusCode(200)
                .extract()
                .as(OrderInformation.class);
    }
}
