package api.seller.product;

import api.seller.login.APISellerLogin;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang.math.JVMRandom;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.logging.log4j.LogManager;
import utility.APIUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles operations related to adding wholesale product pricing.
 */
public class APIAddWholesaleProduct {
    private final APISellerLogin.LoginInformation loginInfo;
    private APIGetProductDetail.ProductInformation productInfo;

    /**
     * Constructs an APIAddWholesaleProduct instance with the given credentials.
     *
     * @param credentials API credentials for the seller
     */
    public APIAddWholesaleProduct(APISellerLogin.Credentials credentials) {
        this.loginInfo = new APISellerLogin().getSellerInformation(credentials);
    }

    @Data
    @AllArgsConstructor
    public static class WholesalePricingPayload {
        private int itemId;
        private List<WholesalePricingDto> lstWholesalePricingDto;

        @Data
        @AllArgsConstructor
        public static class WholesalePricingDto {
            private final String id = null;
            private final String title = "Wholesale price 1";
            private int minQuatity;
            private String itemModelIds;
            private String currency;
            private long price;
            private final String segmentIds = "ALL";
            private int itemId;
            private final String action = null;
        }
    }

    /**
     * Generates a list of WholesalePricingDto based on product variations and branch stock.
     *
     * @return A list of WholesalePricingDto objects.
     */
    public List<WholesalePricingPayload.WholesalePricingDto> generateWholesalePricingDtoList() {
        List<WholesalePricingPayload.WholesalePricingDto> wholesalePricingDtoList = new ArrayList<>();
        int numberOfVariations = productInfo.isHasModel() ? RandomUtils.nextInt(APIGetProductDetail.getVariationModelList(productInfo).size()) + 1 : 1;

        for (int variationIndex = 0; variationIndex < numberOfVariations; variationIndex++) {
            int minQuantity = productInfo.isHasModel()
                    ? calculateMinQuantity(APIGetProductDetail.getMinimumBranchStockForModel(productInfo, APIGetProductDetail.getVariationModelId(productInfo, variationIndex)))
                    : calculateMinQuantity(APIGetProductDetail.getMinimumBranchStockForModel(productInfo, null));

            long price = productInfo.isHasModel()
                    ? calculatePrice(APIGetProductDetail.getVariationSellingPrice(productInfo, variationIndex))
                    : calculatePrice(productInfo.getNewPrice());

            String itemModelIds = productInfo.isHasModel()
                    ? "%d_%d".formatted(productInfo.getId(), APIGetProductDetail.getVariationModelId(productInfo, variationIndex))
                    : String.valueOf(productInfo.getId());

            wholesalePricingDtoList.add(new WholesalePricingPayload.WholesalePricingDto(minQuantity, itemModelIds, loginInfo.getStore().getSymbol(), price, productInfo.getId()));
        }

        return wholesalePricingDtoList;
    }

    /**
     * Calculates the minimum quantity based on the minimum stock available for a model.
     *
     * @param minModelStock The minimum stock available for a model. If the value is less than 1, it defaults to 1.
     * @return The calculated minimum quantity, which is a random value between 1 and the minimum model stock (inclusive).
     */
    private int calculateMinQuantity(int minModelStock) {
        return RandomUtils.nextInt(Math.max(minModelStock, 1)) + 1;
    }

    /**
     * Calculates the price per item based on the provided price.
     *
     * @param price The base price.
     * @return The calculated price per item.
     */
    private long calculatePrice(long price) {
        return JVMRandom.nextLong(Math.max(price, 1));
    }

    /**
     * Generates the WholesalePricingPayload object.
     *
     * @return A WholesalePricingPayload containing the product information and pricing details.
     */
    public WholesalePricingPayload generateWholesalePricingPayload() {
        return new WholesalePricingPayload(productInfo.getId(), generateWholesalePricingDtoList());
    }

    /**
     * Adds wholesale pricing for the specified product.
     *
     * @param productInfo Product information for which the wholesale pricing is to be added.
     */
    public void addWholesalePriceProduct(APIGetProductDetail.ProductInformation productInfo) {
        // Assigns the provided product information to the instance variable
        this.productInfo = productInfo;

        LogManager.getLogger().info("===== STEP =====> [AddWholesaleProduct] START... ");

        // Makes a POST request to add the wholesale pricing for the product
        new APIUtils().post("/itemservice/api/item/wholesale-pricing", loginInfo.getAccessToken(), generateWholesalePricingPayload())
                .then().statusCode(200); // Verifies that the response status is 200 OK

        LogManager.getLogger().info("===== STEP =====> [AddWholesaleProduct] DONE!!! ");
    }
}
