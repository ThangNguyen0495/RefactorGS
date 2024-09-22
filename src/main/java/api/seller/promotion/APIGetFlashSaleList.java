package api.seller.promotion;

import api.seller.login.APISellerLogin;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import utility.APIUtils;

import java.util.List;
import java.util.Map;

/**
 * Class to handle fetching flash sale campaigns through the API.
 */
public class APIGetFlashSaleList {
    private final APISellerLogin.LoginInformation loginInfo;

    /**
     * Constructor to initialize the API with seller credentials.
     *
     * @param credentials the seller's credentials.
     */
    public APIGetFlashSaleList(APISellerLogin.Credentials credentials) {
        this.loginInfo = new APISellerLogin().getSellerInformation(credentials);
    }

    /**
     * Represents a flash sale campaign.
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FlashSaleCampaign {
        private String createdDate;
        private String lastModifiedDate;
        private int id;
        private String name;
        private String startDate;
        private String endDate;
        private int storeId;
        private String status;
        private List<Item> items;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Item {
            private int id;
            private String itemModelId;
            private String itemType;
            private String currency;
            private double newPrice;
            private int saleStock;
            private int purchaseLimitStock;
            private int soldStock;
            private int position;
            private int campaignId;
            private int transactionStock;
            private boolean enableListing;
            private String startDate;
            private String endDate;
            private String name;
            private String imageId;
            private Integer modelId;
            private int litemId;
        }
    }

    /**
     * Fetches the list of flash sale campaigns based on their status.
     *
     * @param status the status of the flash sales (e.g., "SCHEDULED", "IN_PROGRESS").
     * @return a list of FlashSaleCampaign objects.
     */
    public List<FlashSaleCampaign> getFlashSaleList(String status) {
        String url = String.format("/itemservice/api/campaigns/search/%d?status=%s", loginInfo.getStore().getId(), status);
        return new APIUtils().get(url, loginInfo.getAccessToken(), Map.of("time-zone", "Asia/Saigon"))
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList(".", FlashSaleCampaign.class);
    }
}