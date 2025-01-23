package api.buyer.product;

import api.seller.login.APISellerLogin;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.restassured.response.Response;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import utility.APIUtils;

import java.util.List;

/**
 * Handles retrieval of flash sale information for products.
 */
public class APIGetFlashSaleInformation {
    private final APISellerLogin.LoginInformation loginInfo;

    /**
     * Constructs an APIGetFlashSaleInformation instance.
     *
     * @param credentials the seller's API credentials
     */
    public APIGetFlashSaleInformation(APISellerLogin.Credentials credentials) {
        this.loginInfo = new APISellerLogin().getSellerInformation(credentials);
    }

    /**
     * Represents information about a flash sale.
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FlashSaleInformation {
        private String status;
        private List<Item> items;

        /**
         * Represents an item in the flash sale.
         */
        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Item {
            private String itemModelId;
            private long newPrice;
            private int saleStock;
            private int purchaseLimitStock;
            private int soldStock;
            private int transactionStock;
            private int litemId;
        }
    }

    /**
     * Retrieves flash sale information for a specified item and model.
     *
     * @param itemId  the item ID
     * @param modelId the model ID, can be null
     * @return FlashSaleInformation if available, otherwise null
     */
    public FlashSaleInformation getFlashSaleInformation(int itemId, Integer modelId) {
        // Logger
        LogManager.getLogger().info("Get flash sale information, itemId: {}, modelId: {}", itemId, modelId);

        // Build API path
        String apiPath = "/itemservice/api/campaigns/product/%d?modelId=%s".formatted(itemId, modelId != null ? modelId : "");

        // Get response
        Response response = new APIUtils().get(apiPath, loginInfo.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .response();

        return response.asPrettyString().isEmpty() ? null : response.as(FlashSaleInformation.class);
    }
}
