package api.seller.promotion;

import api.seller.customer.APIGetSegmentList;
import api.seller.login.APISellerLogin;
import api.seller.product.APIGetProductCollection;
import api.seller.product.APIGetProductDetail;
import api.seller.promotion.APICreateProductDiscountCampaign.DiscountCampaignPayload.Discount.Condition;
import api.seller.promotion.APICreateProductDiscountCampaign.DiscountCampaignPayload.Discount.Condition.ConditionValue;
import api.seller.setting.APIGetBranchList;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang.math.JVMRandom;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.logging.log4j.LogManager;
import utility.APIUtils;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Handles operations related to creating and managing product discount campaigns.
 * This class provides methods to create discount campaigns, generate conditions,
 * and manage ongoing campaigns by deleting them if necessary.
 */
public class APICreateProductDiscountCampaign {

    private final APISellerLogin.Credentials credentials;
    private final APISellerLogin.LoginInformation loginInfo;
    private APIGetProductDetail.ProductInformation productInfo;
    private final APIGetProductDiscountCampaignList apiGetProductDiscountCampaignList;
    private final APIDeleteProductDiscountCampaign apiDeleteProductDiscountCampaign;


    /**
     * Represents the payload for creating a discount campaign.
     */
    @Data
    @AllArgsConstructor
    public static class DiscountCampaignPayload {
        private String name;
        private int storeId;
        private final int timeCopy = 0;
        private String description;
        private List<Discount> discounts;

        @Data
        @AllArgsConstructor
        public static class Discount {
            private String couponCode;
            private String activeDate;
            private String couponType;
            private Long couponValue;
            private String expiredDate;
            private String type;
            private List<Condition> conditions;

            @Data
            @AllArgsConstructor
            public static class Condition {
                private String conditionOption;
                private String conditionType;
                private List<ConditionValue> values;

                @Data
                @AllArgsConstructor
                public static class ConditionValue {
                    private String conditionValue;
                }
            }
        }
    }

    /**
     * Constructs a ProductDiscountCampaign instance.
     *
     * @param credentials API credentials for the seller
     */
    public APICreateProductDiscountCampaign(APISellerLogin.Credentials credentials) {
        this.credentials = credentials; // Assigning credentials to the instance variable
        this.loginInfo = new APISellerLogin().getSellerInformation(credentials); // Fetching login info using credentials
        this.apiGetProductDiscountCampaignList = new APIGetProductDiscountCampaignList(credentials);
        this.apiDeleteProductDiscountCampaign = new APIDeleteProductDiscountCampaign(credentials);
    }

    /**
     * Ends any early discount campaigns by deleting scheduled and in-progress campaigns.
     * This method fetches the IDs of scheduled and in-progress campaigns and deletes them.
     */
    public void endEarlyDiscountCampaign() {
        // Fetching IDs of scheduled discount campaigns
        List<Integer> scheduledList = apiGetProductDiscountCampaignList.getDiscountCampaignList("SCHEDULED").stream()
                .map(APIGetProductDiscountCampaignList.DiscountCampaign::getId)
                .toList();
        // Deleting each scheduled campaign
        scheduledList.forEach(apiDeleteProductDiscountCampaign::deleteDiscountCampaign);

        // Fetching IDs of in-progress discount campaigns
        List<Integer> inProgressList = apiGetProductDiscountCampaignList.getDiscountCampaignList("IN_PROGRESS").stream()
                .map(APIGetProductDiscountCampaignList.DiscountCampaign::getId)
                .toList();
        // Deleting each in-progress campaign
        inProgressList.forEach(apiDeleteProductDiscountCampaign::deleteDiscountCampaign);
    }

    /**
     * Generates a condition based on customer segments.
     *
     * @return Condition related to customer segments
     */
    private Condition getSegmentCondition() {
        // Fetching the list of customer segments
        var segmentList = new APIGetSegmentList(credentials).getSegmentList();
        int segmentConditionType = segmentList.isEmpty() ? 0 : RandomUtils.nextInt(2); // Determine segment condition type

        // Setting the condition label based on the type
        String segmentConditionLabel = segmentConditionType == 0
                ? "CUSTOMER_SEGMENT_ALL_CUSTOMERS"
                : "CUSTOMER_SEGMENT_SPECIFIC_SEGMENT";

        // Creating a condition value if the condition type is specific segment
        var conditionValue = segmentConditionType != 0
                ? new ConditionValue(String.valueOf(segmentList.getFirst().getId()))
                : null; // No condition value for all customers

        // Returning the constructed condition
        return new Condition(segmentConditionLabel, "CUSTOMER_SEGMENT", conditionValue != null ? List.of(conditionValue) : List.of());
    }

    /**
     * Generates a condition based on the products or collections the discount applies to.
     *
     * @return Condition related to product applicability
     */
    private Condition getAppliesToCondition() {
        // Fetching product collections
        var collectionList = new APIGetProductCollection(credentials).getProductCollections(productInfo.getId());
        int appliesToType = !collectionList.isEmpty() ? RandomUtils.nextInt(3) : List.of(0, 2).get(RandomUtils.nextInt(2)); // Determine applies to type

        // Setting the applies to label based on the type
        String appliesToLabel = appliesToType == 0 ? "APPLIES_TO_ALL_PRODUCTS"
                : (appliesToType == 1) ? "APPLIES_TO_SPECIFIC_COLLECTIONS" : "APPLIES_TO_SPECIFIC_PRODUCTS";

        // Creating a condition value if the condition type is specific
        var appliesToValue = (appliesToType != 0)
                ? new ConditionValue(appliesToType == 1 ? String.valueOf(collectionList.getFirst().getId()) : String.valueOf(productInfo.getId()))
                : null; // No condition value for all products

        // Returning the constructed condition
        return new Condition(appliesToLabel, "APPLIES_TO", (appliesToValue != null) ? List.of(appliesToValue) : List.of());
    }

    /**
     * Generates a condition based on minimum requirements for discount eligibility.
     *
     * @return Condition related to minimum requirements
     */
    private Condition getMinimumRequirement() {
        // Getting variation model list for the product
        List<Integer> variationModelList = APIGetProductDetail.getVariationModelList(productInfo);
        // Finding minimum stock across branches
        int min = Collections.min(productInfo.isHasModel() ? APIGetProductDetail.getBranchStocks(productInfo, variationModelList.get(0)) : APIGetProductDetail.getBranchStocks(productInfo, null));

        // If the product has models, find the minimum stock across all models
        if (productInfo.isHasModel()) {
            for (int index = 1; index < variationModelList.size(); index++) {
                min = Math.min(Collections.min(APIGetProductDetail.getBranchStocks(productInfo, index)), min);
            }
        }

        // Setting the minimum required quantity condition value
        var conditionValue = new ConditionValue(String.valueOf(RandomUtils.nextInt(Math.max(min, 1)) + 1));

        // Returning the constructed minimum requirement condition
        return new Condition("MIN_REQUIREMENTS_QUANTITY_OF_ITEMS", "MINIMUM_REQUIREMENTS", Collections.singletonList(conditionValue));
    }

    /**
     * Generates a condition based on branch applicability for the discount.
     *
     * @return Condition related to branch applicability
     */
    private Condition getBranchCondition() {
        // Randomly determine if the discount applies to all branches or specific branches
        int discountCampaignBranchConditionType = RandomUtils.nextInt(2);
        String applicableCondition = discountCampaignBranchConditionType == 0
                ? "APPLIES_TO_BRANCH_ALL_BRANCHES"
                : "APPLIES_TO_BRANCH_SPECIFIC_BRANCH";

        // Creating a condition value if the condition type is specific
        var conditionValue = discountCampaignBranchConditionType != 0
                ? new ConditionValue(String.valueOf(getRandomBranchId()))
                : null; // No condition value for all branches

        // Returning the constructed branch condition
        return new Condition(applicableCondition, "APPLIES_TO_BRANCH", (conditionValue != null) ? List.of(conditionValue) : List.of());
    }

    /**
     * Retrieves a random active branch ID.
     *
     * @return Random active branch ID
     */
    private int getRandomBranchId() {
        // Fetching branch information
        var branchInfos = new APIGetBranchList(credentials).getBranchInformation();
        // Getting active branch IDs
        List<Integer> activeBranchList = APIGetBranchList.getActiveBranchIds(branchInfos);
        return activeBranchList.get(RandomUtils.nextInt(activeBranchList.size())); // Returning a random active branch ID
    }

    /**
     * Collects all conditions into a list.
     *
     * @return List of all conditions for the discount campaign
     */
    private List<Condition> getAllConditions() {
        // Collecting all conditions into a list and returning it
        return List.of(
                getSegmentCondition(),
                getAppliesToCondition(),
                getMinimumRequirement(),
                getBranchCondition()
        );
    }

    /**
     * Generates the discount campaign payload.
     *
     * @param startDatePlus Number of days to add to the current date for campaign start
     * @return DiscountCampaignPayload containing the details of the campaign
     */
    private DiscountCampaignPayload getDiscountCampaign(int startDatePlus) {
        LocalDateTime localDateTime = LocalDateTime.now(); // Getting the current date and time
        ZoneId localZoneId = ZoneId.systemDefault(); // Getting the system default time zone
        ZoneId gmtZoneId = ZoneId.of("GMT+0"); // Defining GMT time zone

        // Calculating the start time for the campaign based on the current date
        Instant startTime = localDateTime.truncatedTo(ChronoUnit.DAYS)
                .atZone(localZoneId)
                .withZoneSameInstant(gmtZoneId)
                .plusDays(startDatePlus) // Adding specified days to start date
                .toInstant();

        // Setting the end time for the campaign
        Instant endTime = startTime.plus(Duration.ofHours(23).plusMinutes(59));

        // Randomly determining coupon type and value
        int couponType = RandomUtils.nextInt(2);
        String couponTypeLabel = couponType == 0 ? "PERCENTAGE" : "FIXED_AMOUNT";

        long minFixAmount = productInfo.isHasModel() ? Collections.min(APIGetProductDetail.getVariationSellingPrice(productInfo)) : productInfo.getNewPrice();
        long couponValue = couponType == 0 ? RandomUtils.nextInt(100) + 1 : JVMRandom.nextLong(Math.max(minFixAmount, 1)) + 1;

        // Creating a discount object with the necessary details
        DiscountCampaignPayload.Discount discount = new DiscountCampaignPayload.Discount("unused_code", startTime.toString(), couponTypeLabel, couponValue, endTime.toString(), "WHOLE_SALE", getAllConditions());

        // Returning the constructed discount campaign payload
        return new DiscountCampaignPayload("Auto - [Product] Discount campaign - %s".formatted(LocalDateTime.now().toString().substring(0, 19)),
                loginInfo.getStore().getId(),
                "",
                Collections.singletonList(discount));
    }

    /**
     * Creates a product discount campaign with the specified product information.
     *
     * @param productInfo   Product information for the discount campaign
     * @param startDatePlus Number of days to add to the current date for campaign start
     */
    public void createProductDiscountCampaign(APIGetProductDetail.ProductInformation productInfo, int startDatePlus) {
        LogManager.getLogger().info("===== STEP =====> [CreateDiscountCampaign] START... "); // Logging the start of campaign creation

        this.productInfo = productInfo; // Assigning product information to the instance variable
        endEarlyDiscountCampaign(); // Ending any ongoing campaigns

        // Generating the discount campaign payload
        DiscountCampaignPayload discountCampaignPayload = getDiscountCampaign(startDatePlus);

        // Making an API call to create the discount campaign
        new APIUtils().post("/orderservices2/api/gs-discount-campaigns/coupons",
                loginInfo.getAccessToken(),
                discountCampaignPayload,
                Map.of("time-zone", "Asia/Saigon"))
                .then().statusCode(200); // Verifying the response status is 200 OK

        LogManager.getLogger().info("===== STEP =====> [CreateDiscountCampaign] DONE!!! "); // Logging the successful creation of the campaign
    }
}
