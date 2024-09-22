package api.seller.promotion;

import api.seller.login.APISellerLogin;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import utility.APIUtils;

import java.util.List;
import java.util.Map;

/**
 * This class handles the retrieval of product discount campaigns for a seller.
 */
public class APIGetProductDiscountCampaignList {
    private final APISellerLogin.LoginInformation loginInfo;

    /**
     * Constructs an APIGetProductDiscountCampaignList instance with the given seller credentials.
     *
     * @param credentials the credentials of the seller to retrieve information
     */
    public APIGetProductDiscountCampaignList(APISellerLogin.Credentials credentials) {
        this.loginInfo = new APISellerLogin().getSellerInformation(credentials);
    }

    /**
     * Represents a discount campaign associated with a product.
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DiscountCampaign {
        private int id;
        private String name;
        private String description;
        private int storeId;
        private int timeCopy;
        private String createdDate;
    }

    /**
     * Retrieves a list of discount campaigns based on the specified status.
     *
     * @param status the status of the discount campaigns to retrieve (e.g., "IN_PROGRESS", "EXPIRED")
     * @return a list of discount campaigns that match the specified status
     */
    public List<DiscountCampaign> getDiscountCampaignList(String status) {
        String url = String.format("/orderservices2/api/gs-discount-campaigns?storeId=%s&type=WHOLE_SALE&status=%s",
                loginInfo.getStore().getId(),
                status);

        return new APIUtils().get(url, loginInfo.getAccessToken(), Map.of("time-zone", "Asia/Saigon"))
                .then()
                .statusCode(200) // Ensure the response status is 200 OK
                .extract()
                .jsonPath()
                .getList(".", DiscountCampaign.class); // Extract the list of discount campaigns
    }
}