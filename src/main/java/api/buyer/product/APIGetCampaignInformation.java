package api.buyer.product;

import api.seller.login.APISellerLogin;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import utility.APIUtils;

import java.util.List;
import java.util.Map;

/**
 * Handles retrieval of discount campaign information for a product.
 */
public class APIGetCampaignInformation {
    private final APISellerLogin.LoginInformation loginInfo;

    /**
     * Constructs an APIGetDiscountCampaignInformation instance.
     *
     * @param credentials the seller's API credentials
     */
    public APIGetCampaignInformation(APISellerLogin.Credentials credentials) {
        this.loginInfo = new APISellerLogin().getSellerInformation(credentials);
    }
    /**
     * Represents the payload for checking discount campaigns.
     */
    @Data
    @AllArgsConstructor
    private static class CheckPayload {
        private List<Item> lstProduct;

        @Data
        @AllArgsConstructor
        public static class Item {
            private int itemId;
            private int branchId;
        }
    }

    /**
     * Represents information about a discount campaign.
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CampaignInformation {
        private int productId;
        private int branchId;
        private List<Wholesale> wholesales;
        private List<Integer> lstWholesaleIds;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Wholesale {
            private String type;
            private long wholesaleValue;
            private int minQuantity;
            private String minimumConditionType;
        }
    }

    /**
     * Creates a payload for the discount campaign check.
     *
     * @param itemId   the item ID
     * @param branchId the branch ID
     * @return a CheckPayload containing the item and branch IDs
     */
    private CheckPayload getPayload(int itemId, int branchId) {
        return new CheckPayload(List.of(new CheckPayload.Item(itemId, branchId)));
    }

    /**
     * Retrieves discount campaign information for a specified product and customer.
     *
     * @param itemId     the item ID
     * @param branchId   the branch ID
     * @param customerId the customer ID
     * @return CampaignInformation if available, otherwise null
     */
    public CampaignInformation getDiscountCampaignInformation(int itemId, int branchId, int customerId) {
        // Logger
        LogManager.getLogger().info("Get discount campaign information, itemId: {}, branchId: {}, customerId: {}", itemId, branchId, customerId);

        List<CampaignInformation> campaignInformationList = new APIUtils().post(
                        "/orderservices2/api/check-product-branch-wholesale/%d/%d".formatted(loginInfo.getStore().getId(), customerId),
                        loginInfo.getAccessToken(),
                        getPayload(itemId, branchId),
                        Map.of("platform", "ANDROID")
                )
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList(".", CampaignInformation.class);

        return campaignInformationList.isEmpty() ? null : campaignInformationList.getFirst();
    }
}
