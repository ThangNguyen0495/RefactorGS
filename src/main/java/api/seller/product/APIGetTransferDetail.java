package api.seller.product;

import api.seller.login.APISellerLogin;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import utility.APIUtils;

import java.util.List;

/**
 * This class retrieves transfer details via the API.
 * It uses the seller's credentials to authenticate and fetch information.
 */
public class APIGetTransferDetail {

    private final APISellerLogin.LoginInformation loginInfo;

    /**
     * Constructor to initialize the APIGetTransferDetail with credentials.
     *
     * @param credentials The seller's credentials to authenticate the request.
     */
    public APIGetTransferDetail(APISellerLogin.Credentials credentials) {
        loginInfo = new APISellerLogin().getSellerInformation(credentials);
    }

    /**
     * Represents detailed transfer information.
     */
    @Data
    public static class TransferInformation {

        private int id;
        private int originBranchId;
        private int destinationBranchId;
        private String status;
        private String note;
        private int storeId;
        private long createdByStaffId;
        private String staffName;
        private List<Item> items;
        private String createdDate;
        private boolean changedPriceOrRate;
        private boolean hasLotLocation;
        private String transferType;

        /**
         * Represents the item details in the transfer.
         */
        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Item {
            private int id;
            private long itemId;
            private int quantity;
            private String inventoryManageType;
            private List<Object> codeList;
            private boolean hasLot;
            private boolean hasLocation;
            private int weight;
            private int width;
            private int height;
            private int length;
            private String itemName;
            private String modelLabel;
            private String modelValue;
            private int remaining;
            private String sku;
            private String itemModelId;
        }
    }

    /**
     * Fetches the transfer information for a given transfer ID.
     *
     * @param transferId The ID of the transfer to be retrieved.
     * @return TransferInformation The detailed information of the transfer.
     */
    public TransferInformation getTransferInformation(int transferId) {
        return new APIUtils().get("/itemservice/api/transfers/detail/%s/%s".formatted(loginInfo.getStore().getId(), transferId), loginInfo.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .as(TransferInformation.class);
    }
}