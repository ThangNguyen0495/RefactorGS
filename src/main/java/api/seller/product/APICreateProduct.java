package api.seller.product;

import api.seller.login.APISellerLogin;
import api.seller.setting.APIGetBranchList;
import api.seller.setting.APIGetStoreDefaultLanguage;
import api.seller.setting.APIGetVATList;
import api.seller.user_feature.APIGetUserFeature;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import utility.APIUtils;
import utility.helper.ProductHelper;
import utility.helper.VariationHelper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static api.seller.user_feature.APIGetUserFeature.*;
import static org.apache.commons.lang.math.JVMRandom.nextLong;
import static org.apache.commons.lang.math.RandomUtils.nextBoolean;
import static org.apache.commons.lang.math.RandomUtils.nextInt;

public class APICreateProduct {
    // API endpoint for creating a product
    private static final String CREATE_PRODUCT_PATH = "/itemservice/api/items?fromSource=DASHBOARD";

    private final APISellerLogin.Credentials credentials;
    private APISellerLogin.LoginInformation loginInfo;

    private String defaultLanguage;
    private List<Integer> vatIds;
    private List<Integer> branchIds;
    private List<String> branchNames;
    private List<String> branchTypes;
    private ProductPayload payload;
    @Setter
    private boolean showOutOfStock = true;
    private boolean isHideStock = false;

    public APICreateProduct(APISellerLogin.Credentials credentials) {
        this.credentials = credentials;
        this.payload = new ProductPayload();
    }

    /**
     * Fetches necessary information from various APIs.
     * Initializes login information, VAT list, branch list, and language information.
     * Also sets up default language, VAT IDs, branch IDs, branch names, and branch types.
     */
    private void fetchInformation() {
        loginInfo = new APISellerLogin().getSellerInformation(credentials);
        var vatInfoList = new APIGetVATList(credentials).getVATInformation();
        var branchInfoList = new APIGetBranchList(credentials).getBranchInformation();

        defaultLanguage = new APIGetStoreDefaultLanguage(credentials).getDefaultLanguage();
        vatIds = APIGetVATList.getVATIds(vatInfoList);
        branchIds = APIGetBranchList.getBranchIds(branchInfoList);
        branchNames = APIGetBranchList.getBranchNames(branchInfoList);
        branchTypes = APIGetBranchList.getBranchTypes(branchInfoList);
    }

    public void setHideStock(boolean isHideStock) {
        this.isHideStock = isHideStock;
    }

    @Data
    @RequiredArgsConstructor
    public static class ProductPayload {
        private final List<Category> categories = List.of(new Category(null, 1, 1014), new Category(null, 2, 1680));
        private String name;
        private final int cateId = 1680;
        private final String itemType = "BUSINESS_PRODUCT";
        private final String currency = "đ";
        private String description;
        private final int discount = 0;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Long costPrice;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Long orgPrice;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Long newPrice;
        private final int totalComment = 0;
        private final int totalLike = 0;
        private final List<Image> images = List.of(new Image("28952be8-ef6b-4877-99fa-399c7ddd8c01", "https://d3a0f2zusjbf7r.cloudfront.net", "jpg", 0));
        private final int totalItem = 0;
        private final ShippingInfo shippingInfo = nextBoolean() ? new ShippingInfo(10, 10, 10, 10) : new ShippingInfo(0, 0, 0, 0);
        private String parentSku;
        private List<Model> models = new ArrayList<>();
        private List<Object> itemAttributes = new ArrayList<>();
        private List<Object> itemAttributeDeleteIds = new ArrayList<>();
        private String seoTitle;
        private String seoDescription;
        private String seoUrl;
        private String seoKeywords;
        private String priority;
        private String taxId;
        private final boolean quantityChanged = true;
        private final boolean isSelfDelivery = false;
        private boolean showOutOfStock;
        private String barcode;
        private Boolean isHideStock;
        private boolean lotAvailable;
        private boolean expiredQuality;
        private String inventoryManageType;
        private String conversionUnitId;
        private boolean onApp;
        private boolean onWeb;
        private boolean inStore;
        private boolean inGoSocial;
        private final boolean enabledListing = false;
        private List<Inventory> lstInventory = new ArrayList<>();
        private List<ItemModelCodeDTO> itemModelCodeDTOS = new ArrayList<>();
    }

    @Data
    @AllArgsConstructor
    public static class Category {
        private Integer id;
        private int level;
        private int cateId;
    }

    @Data
    @AllArgsConstructor
    public static class Image {
        private String imageUUID;
        private String urlPrefix;
        private String extension;
        private int rank;
    }

    @Data
    @AllArgsConstructor
    public static class ShippingInfo {
        private int weight;
        private int height;
        private int length;
        private int width;
    }

    @Data
    public static class ItemModelCodeDTO {
        private int branchId;
        private String code;
        private final String status = "AVAILABLE";

        public ItemModelCodeDTO(int branchId, String branchName, String variation, int index) {
            this.branchId = branchId;
            this.code = String.format("%s%s_%s", variation.isEmpty() ? "" : String.format("%s_", variation), branchName, index);
        }
    }

    @Data
    @AllArgsConstructor
    public static class Inventory {
        private int branchId;
        private String branchType;
        private final String inventoryActionType = "FROM_CREATE_AT_ITEM_SCREEN";
        private final int inventoryCurrent = 0;
        private int inventoryStock;
        private final String inventoryType = "SET";
        private final String sku = "";
    }

    @Data
    @AllArgsConstructor
    public static class Model {
        private String name;
        private long orgPrice;
        private final int discount = 0;
        private long newPrice;
        private final int totalItem = 0;
        private String label;
        private final String sku = "";
        private final int newStock = 0;
        private final int costPrice = 0;
        private List<Inventory> lstInventory;
        private List<ItemModelCodeDTO> itemModelCodeDTOS;
    }

    /**
     * Initializes the basic information for the product payload.
     * Sets values like description, SEO fields, and tax ID.
     *
     * @param isManagedByIMEI Indicates whether the product is managed by IMEI.
     * @return Initialized ProductPayload object.
     */
    private ProductPayload initializeBasicInformation(boolean isManagedByIMEI) {
        ProductPayload payload = new ProductPayload();
        // Set storefront display
        payload.setIsHideStock(isHideStock);
        payload.setShowOutOfStock(showOutOfStock);

        // Init platform information
        // Get all user packages
        var userPackages = new APIGetUserFeature(this.credentials).getUserFeature();

        // Init platform information
        payload.setOnApp(hasGoAPP(userPackages));
        payload.setOnWeb(hasGoWEB(userPackages));
        payload.setInGoSocial(hasGoSOCIAL(userPackages));
        payload.setInStore(hasGoPOS(userPackages));

        // Then clear it for next test
        isHideStock = false;
        showOutOfStock = true;

        // Init other information
        payload.setLotAvailable(payload.isLotAvailable() && !isManagedByIMEI);
        payload.setInventoryManageType(isManagedByIMEI ? "IMEI_SERIAL_NUMBER" : "PRODUCT");
        payload.setDescription(String.format("[%s] product description.", defaultLanguage));
        payload.setTaxId(vatIds.isEmpty() ? "" : vatIds.get(nextInt(vatIds.size())).toString());

        // Generate SEO fields based on current timestamp
        String timestamp = String.valueOf(Instant.now().toEpochMilli());
        payload.setSeoTitle(String.format("[%s] Auto - SEO Title - %s", defaultLanguage, timestamp));
        payload.setSeoDescription(String.format("[%s] Auto - SEO Description - %s", defaultLanguage, timestamp));
        payload.setSeoKeywords(String.format("[%s] Auto - SEO Keyword - %s", defaultLanguage, timestamp));
        payload.setSeoUrl(String.format("%s%s", defaultLanguage, timestamp));

        return payload;
    }

    /**
     * Creates a ProductPayload object for a product without variations.
     * Sets the product name, pricing, and inventory based on branch stock.
     *
     * @param isManagedByIMEI Indicates whether the product is managed by IMEI.
     * @param branchStock     Array of stock values for each branch.
     * @return Configured ProductPayload object.
     */
    private ProductPayload createPayloadWithoutVariation(boolean isManagedByIMEI, int... branchStock) {
        fetchInformation();
        ProductPayload payload = initializeBasicInformation(isManagedByIMEI);

        // Set product name with a timestamp
        String productName = String.format("[%s] %s%s", defaultLanguage,
                isManagedByIMEI ? "Auto - IMEI - without variation - " : "Auto - Normal - without variation - ",
                LocalDateTime.now().toString().substring(0, 19));
        payload.setName(productName);

        // Set pricing values
        payload.setOrgPrice(nextLong(ProductHelper.MAX_PRICE));
        payload.setNewPrice(nextLong(payload.getOrgPrice()));
        payload.setCostPrice(nextLong(payload.getNewPrice()));

        // Create inventory list with stock values
        List<Inventory> inventoryList = IntStream.range(0, branchIds.size())
                .mapToObj(branchIndex -> new Inventory(branchIds.get(branchIndex),
                        branchTypes.get(branchIndex),
                        (!payload.isLotAvailable() && (branchStock.length > branchIndex)) ? branchStock[branchIndex] : 0))
                .toList();
        payload.setLstInventory(inventoryList);

        // Create item model codes based on inventory
        List<ItemModelCodeDTO> itemModelCodeDTOS = inventoryList.stream()
                .flatMap(inventory -> IntStream.range(0, inventory.getInventoryStock())
                        .mapToObj(index -> new ItemModelCodeDTO(inventory.getBranchId(), branchNames.get(branchIds.indexOf(inventory.getBranchId())), "", index)))
                .toList();
        payload.setItemModelCodeDTOS(itemModelCodeDTOS);

        return payload;
    }

    /**
     * Creates a ProductPayload object for a product with variations.
     * Generates random variation data and populates the product payload with it.
     *
     * @param isManagedByIMEI Indicates whether the product is managed by IMEI.
     * @param branchStock     Array of initial stock values for each branch.
     * @return Configured ProductPayload object.
     */
    public ProductPayload createPayloadWithVariation(boolean isManagedByIMEI, int... branchStock) {
        fetchInformation();
        ProductPayload payload = initializeBasicInformation(isManagedByIMEI);
        // Set product name with a timestamp
        String productName = String.format("[%s] %s%s", defaultLanguage,
                isManagedByIMEI ? "Auto - IMEI - variation - " : "Auto - Normal - variation - ",
                LocalDateTime.now().toString().substring(0, 19));
        payload.setName(productName);

        // Generate variation data
        Map<String, List<String>> variationMap = VariationHelper.randomVariationMap(defaultLanguage);
        String variationName = variationMap.keySet().toString().replaceAll("[\\[\\]\\s]", "").replaceAll(",", "|");
        List<String> variationList = VariationHelper.getVariationValues(variationMap);

        // Create models with variations
        List<Model> models = variationList.stream().map(variation -> createModel(variation, variationName, branchStock))
                .toList();

        payload.setModels(models);

        return payload;
    }

    /**
     * Creates a Model object for a specific variation.
     * Sets pricing, inventory, and item model codes for the model.
     *
     * @param variation     The variation string.
     * @param variationName The name of the variation.
     * @param branchStock   Array of stock values for each branch.
     * @return Configured Model object.
     */
    private Model createModel(String variation, String variationName, int... branchStock) {
        long listingPrice = nextLong(ProductHelper.MAX_PRICE);
        long sellingPrice = nextLong(listingPrice);

        // Create inventory list with stock values
        List<Inventory> inventoryList = IntStream.range(0, branchIds.size())
                .mapToObj(branchIndex -> new Inventory(branchIds.get(branchIndex),
                        branchTypes.get(branchIndex),
                        (!payload.isLotAvailable() && (branchStock.length > branchIndex)) ? (branchStock[branchIndex]) : 0))
                .toList();

        // Create item model codes based on inventory
        List<ItemModelCodeDTO> itemModelCodeDTOS = inventoryList.stream()
                .flatMap(inventory -> IntStream.range(0, inventory.getInventoryStock())
                        .mapToObj(index -> new ItemModelCodeDTO(inventory.getBranchId(), branchNames.get(branchIds.indexOf(inventory.getBranchId())), variation, index)))
                .toList();

        return new Model(variation, listingPrice, sellingPrice, variationName, inventoryList, itemModelCodeDTOS);
    }

    /**
     * Creates a new product by sending a POST request to the API.
     * Resets the payload after the product creation request is processed.
     *
     * @param isManagedByIMEI Indicates whether the product is managed by IMEI.
     * @param withVariation   Indicates whether the product should have variations.
     * @param branchStock     Array of stock values for each branch.
     * @return The ID of the created product.
     */
    public int createProductThenGetId(boolean isManagedByIMEI, boolean withVariation, int... branchStock) {
        payload = withVariation ? createPayloadWithVariation(isManagedByIMEI, branchStock) : createPayloadWithoutVariation(isManagedByIMEI, branchStock);

        // Send POST request to create the product
        int productId = new APIUtils().post(CREATE_PRODUCT_PATH, loginInfo.getAccessToken(), payload)
                .then().statusCode(201)
                .extract()
                .response()
                .jsonPath()
                .getInt("id");

        // Reset payload for next usage
        payload = new ProductPayload();

        // Return productId
        return productId;
    }
}
