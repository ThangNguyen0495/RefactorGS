package api.seller.promotion;

import api.seller.login.APISellerLogin;
import api.seller.product.APIGetProductDetail;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utility.APIUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.IntStream;

import static java.lang.Thread.sleep;
import static org.apache.commons.lang.math.JVMRandom.nextLong;
import static org.apache.commons.lang.math.RandomUtils.nextInt;

/**
 * Class to handle creation of Flash Sale campaigns through the API.
 */
public class APICreateFlashSale {
    private final Logger logger = LogManager.getLogger(); // Logger for tracking the process
    private final APISellerLogin.LoginInformation loginInfo; // Seller login information
    private final APIEndFlashSale apiEndFlashSale; // API to end existing flash sales
    private final APIDeleteFlashSale apiDeleteFlashSale; // API to delete scheduled flash sales
    private final APIGetFlashSaleList apiGetFlashSaleList; // API to retrieve existing flash sales

    /**
     * Constructor to initialize the API with seller credentials.
     *
     * @param credentials the seller's credentials.
     */
    public APICreateFlashSale(APISellerLogin.Credentials credentials) {
        // Retrieve login information for the seller
        this.loginInfo = new APISellerLogin().getSellerInformation(credentials);

        // Initialize APIs for managing flash sales
        this.apiEndFlashSale = new APIEndFlashSale(credentials);
        this.apiDeleteFlashSale = new APIDeleteFlashSale(credentials);
        this.apiGetFlashSaleList = new APIGetFlashSaleList(credentials);
    }

    /**
     * Ends any ongoing or scheduled flash sales for the store.
     * Ensures no overlapping campaigns exist before creating a new one.
     */
    public void endEarlyFlashSales() {
        // Fetch scheduled flash sales and delete them
        List<Integer> scheduledSales = apiGetFlashSaleList.getFlashSaleList("SCHEDULED")
                .stream().map(APIGetFlashSaleList.FlashSaleCampaign::getId).toList();

        scheduledSales.forEach(apiDeleteFlashSale::deleteScheduledFlashSale);

        // Fetch in-progress flash sales and end them
        List<Integer> inProgressSales = apiGetFlashSaleList.getFlashSaleList("IN_PROGRESS")
                .stream().map(APIGetFlashSaleList.FlashSaleCampaign::getId).toList();

        inProgressSales.forEach(apiEndFlashSale::endInProgressFlashSale);
    }

    /**
     * Creates a flash sale item from the given product information and variation index.
     *
     * @param productInfo the product information.
     * @param varIndex    the variation index.
     * @return a FlashSalePayload.Item if stock is available; otherwise, null.
     */
    private FlashSalePayload.Item createFlashSaleItem(APIGetProductDetail.ProductInformation productInfo, int varIndex) {
        Integer modelId = productInfo.isHasModel() ?
                APIGetProductDetail.getVariationModelId(productInfo, varIndex) : null;

        // Get stock levels for branches
        List<Integer> branchStocks = APIGetProductDetail.getBranchStocks(productInfo, modelId);

        // Check if there is available stock to create a flash sale item
        if (Collections.max(branchStocks) > 0) {
            int stock = nextInt(Collections.max(branchStocks)) + 1; // Random stock level
            int purchaseLimit = nextInt(stock) + 1; // Random purchase limit
            long price = nextLong(productInfo.isHasModel() ?
                    APIGetProductDetail.getVariationSellingPrice(productInfo, varIndex) :
                    productInfo.getNewPrice());

            // Return a new FlashSalePayload.Item with the generated values
            return new FlashSalePayload.Item(
                    String.valueOf(productInfo.getId()),
                    String.valueOf(purchaseLimit),
                    productInfo.isHasModel() ? String.valueOf(modelId) : null,
                    String.valueOf(price),
                    String.valueOf(stock)
            );
        }

        return null; // No item created due to insufficient stock
    }

    /**
     * Creates a list of flash sale items based on product variations.
     *
     * @param productInfo the product information.
     * @return a list of flash sale items.
     */
    private List<FlashSalePayload.Item> createFlashSaleItems(APIGetProductDetail.ProductInformation productInfo) {
        List<FlashSalePayload.Item> items = new ArrayList<>();

        // Generate a random number of items based on product variations
        IntStream.range(0, nextInt(Math.max(APIGetProductDetail.getVariationModelList(productInfo).size(), 1)) + 1)
                .mapToObj(varIndex -> createFlashSaleItem(productInfo, varIndex))
                .filter(Objects::nonNull)
                .forEach(items::add);

        return items; // Return the populated list of items
    }

    /**
     * Creates the payload for the flash sale request based on product information and timing.
     *
     * @param productInfo the product information to base the flash sale on.
     * @param time        optional start and end times in minutes.
     * @return a FlashSalePayload containing the flash sale details.
     */
    private FlashSalePayload createFlashSalePayload(APIGetProductDetail.ProductInformation productInfo, int... time) {
        String flashSaleName = "Auto - Flash sale campaign - " + LocalDateTime.now().toString().substring(0, 19);

        // Calculate start and end times based on current time and provided parameters
        Instant startTime = Instant.now().plus(time.length > 0 ? time[0] : nextInt(60), ChronoUnit.MINUTES);
        Instant endTime = Instant.now().plus(time.length > 1 ? time[1] : time[0] + nextInt(60), ChronoUnit.MINUTES);

        // Create items for the flash sale
        List<FlashSalePayload.Item> items = createFlashSaleItems(productInfo);

        return new FlashSalePayload(flashSaleName, startTime.toString(), endTime.toString(), items);
    }

    /**
     * Attempts to create a flash sale multiple times (up to 5) until successful.
     *
     * @param productInfo the product information.
     * @param time        optional start and end times in minutes.
     * @return the response from the API upon successful creation of the flash sale.
     * @throws RuntimeException if the flash sale creation fails after 5 attempts.
     */
    private Response createFlashSaleResponse(APIGetProductDetail.ProductInformation productInfo, int... time) {
        logger.info("===== STEP =====> [CreateFlashSale] START... ");

        for (int attempt = 0; attempt < 5; attempt++) {
            FlashSalePayload payload = createFlashSalePayload(productInfo, time);

            // Send POST request to create the flash sale
            Response response = new APIUtils().post(
                    String.format("/itemservice/api/campaigns/%d", loginInfo.getStore().getId()),
                    loginInfo.getAccessToken(),
                    payload,
                    Map.of("time-zone", "Asia/Saigon")
            );

            if (response.getStatusCode() == 200) {
                logger.info("===== STEP =====> [CreateFlashSale] DONE!!! ");
                return response; // Exit if successful
            }

            logger.warn("Flash sale creation attempt {} failed with status code: {}", attempt + 1, response.getStatusCode());
            pauseBetweenAttempts(); // Wait before retrying
        }

        logger.error("Failed to create flash sale after 5 attempts.");
        throw new RuntimeException("Failed to create flash sale after 5 attempts."); // Throw an exception on failure
    }

    /**
     * Pauses execution for a set duration between attempts to create a flash sale.
     */
    private void pauseBetweenAttempts() {
        try {
            sleep(3000); // Sleep for 3 seconds
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore interrupted status
            throw new RuntimeException("Thread was interrupted", e);
        }
    }

    // Start time of the flash sale campaign, initialized after a flash sale is created
    private static String startDate;

    /**
     * Public method to create a flash sale after ending any ongoing or scheduled ones.
     *
     * @param productInfo the product information.
     * @param time        optional start and end times in minutes.
     */
    public void createFlashSale(APIGetProductDetail.ProductInformation productInfo, int... time) {
        endEarlyFlashSales(); // End existing sales before creating a new one

        // Create the flash sale and store its start date
        startDate = createFlashSaleResponse(productInfo, time)
                .as(APIGetFlashSaleList.FlashSaleCampaign.class)
                .getStartDate(); // Attempt to create the flash sale
    }

    /**
     * Payload class representing the flash sale campaign structure.
     */
    @Data
    @AllArgsConstructor
    public static class FlashSalePayload {
        private String name; // Name of the flash sale campaign
        private String startDate; // Start date/time of the flash sale
        private String endDate; // End date/time of the flash sale
        private List<Item> items; // List of items included in the flash sale

        @Data
        @AllArgsConstructor
        public static class Item {
            private String itemId; // ID of the item
            private String limitPurchaseStock; // Purchase limit for the item
            @JsonInclude(JsonInclude.Include.NON_NULL)
            private String modelId; // Model ID if applicable
            private String price; // Sale price of the item
            private String saleStock; // Available stock for the sale
        }
    }

    /**
     * Pauses execution until the flash sale starts by calculating the wait time.
     *
     * @throws RuntimeException if the thread sleep is interrupted.
     */
    public static void waitForFlashSaleStart() {
        Instant startTime = Instant.parse(startDate); // Parse start date from stored value
        Instant now = Instant.now(); // Get current time

        // Calculate the wait time in milliseconds
        long waitTimeMillis = startTime.toEpochMilli() - now.toEpochMilli();

        // Ensure wait time is positive; if it's negative, the flash sale has already started
        if (waitTimeMillis > 0) {
           LogManager.getLogger().info("Waiting for flash sale to start. Wait time: {} milliseconds.", waitTimeMillis);
            try {
                Thread.sleep(waitTimeMillis); // Pause execution
            } catch (InterruptedException e) {
                throw new RuntimeException("Thread sleep was interrupted", e);
            }
        }
    }

}