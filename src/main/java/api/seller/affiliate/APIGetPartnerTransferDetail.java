package api.seller.affiliate;

import api.seller.login.APIDashboardLogin;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import utility.APIUtils;

import java.util.List;

/**
 * This class retrieves partner transfer details via the API.
 * It uses the seller's credentials to fetch the information.
 */
public class APIGetPartnerTransferDetail {

    private final APIDashboardLogin.SellerInformation loginInfo;

    /**
     * Constructor to initialize the APIGetPartnerTransferDetail with credentials.
     *
     * @param credentials The seller's credentials to authenticate the request.
     */
    public APIGetPartnerTransferDetail(APIDashboardLogin.Credentials credentials) {
        loginInfo = new APIDashboardLogin().getSellerInformation(credentials);
    }

    /**
     * Represents detailed partner transfer information.
     */
    @Data
    public static class PartnerTransferInformation {

        private int id;
        private int originBranchId;
        private int destinationBranchId;
        private String status;
        private String note;
        private int storeId;
        private long createdByStaffId;
        private String staffName;
        private List<PartnerTransferInformation.Item> items;
        private String createdDate;
        private int resellerStoreId;
        private String handlingDataStatus;
        private boolean changedPriceOrRate;
        private boolean hasLotLocation;
        private String transferType;

        /**
         * Represents the item details in the partner transfer.
         */
        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Item {
            private int id;
            private int itemId;
            private int quantity;
            private String inventoryManageType;
            private List<String> codeList;
            private int price;
            private int commissionRate;
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
     * Fetches the partner transfer information for a given transfer ID.
     *
     * @param transferId The ID of the partner transfer to be retrieved.
     * @return PartnerTransferInformation The detailed information of the partner transfer.
     */
    public PartnerTransferInformation getPartnerTransferInformation(int transferId) {
        return new APIUtils().get("/itemservice/api/transfers/detail/%s/%s".formatted(loginInfo.getStore().getId(), transferId), loginInfo.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .as(PartnerTransferInformation.class);
    }
}
