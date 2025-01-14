package api.buyer.product;

import api.seller.login.APISellerLogin;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import utility.APIUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * Handles retrieval of wholesale product information.
 */
public class APIGetWholesaleInformation {
    private final APISellerLogin.LoginInformation loginInfo;

    /**
     * Constructs an APIGetWholesaleProductInformation instance.
     *
     * @param credentials the seller's API credentials
     */
    public APIGetWholesaleInformation(APISellerLogin.Credentials credentials) {
        this.loginInfo = new APISellerLogin().getSellerInformation(credentials);
    }

    /**
     * Represents information about a wholesale product.
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WholesaleInformation {
        private long id;
        private String itemModelIds;
        private int minQuatity;
        private BigDecimal price;
        private int itemId;
        private String segmentIds;
    }

    /**
     * Retrieves wholesale product information for a specified item and customer.
     *
     * @param itemId    the item ID
     * @param customerId the customer ID
     * @param modelId    the model ID, can be null
     * @return WholesaleInformation if available, otherwise null
     */
    public WholesaleInformation getWholesaleInformation(int itemId, int customerId, Integer modelId) {
        // Logger
        LogManager.getLogger().info("Get wholesale product information, itemId: {}, customerId: {}, modelId: {}", itemId, customerId, modelId);

        // Build the API path
        String apiPath = "/itemservice/api/item/wholesale-pricing/get-list-store-front/%d/%d/GOSELL?userId=%d&modelId=%s"
                .formatted(loginInfo.getStore().getId(), itemId, customerId, modelId != null ? modelId : "");

        // Get response string
        String responseString = new APIUtils().get(apiPath, loginInfo.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .asPrettyString();

        // Parse the response
        try {
            List<WholesaleInformation> wholesaleProductInformationList = new ObjectMapper().readValue(responseString, new TypeReference<>() {});
            return wholesaleProductInformationList.isEmpty() ? null : wholesaleProductInformationList.getFirst();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing JSON response", e);
        }
    }
}
