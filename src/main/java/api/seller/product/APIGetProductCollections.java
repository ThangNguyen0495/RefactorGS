package api.seller.product;

import api.seller.login.APISellerLogin;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.Data;
import utility.APIUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Class to interact with the product collection API for sellers.
 */
public class APIGetProductCollections {
    private final APISellerLogin.LoginInformation loginInfo;

    /**
     * Constructor to initialize the API with seller credentials.
     *
     * @param credentials the seller's credentials.
     */
    public APIGetProductCollections(APISellerLogin.Credentials credentials) {
        this.loginInfo = new APISellerLogin().getSellerInformation(credentials);
    }

    /**
     * Represents an item in a product collection.
     */
    @Data
    public static class CollectionItem {
        private String collectionType; // Type of the collection
        private String itemType; // Type of the item
        private String createdDate; // Date the collection was created
        private String name; // Name of the collection
        private int id; // ID of the collection
        private int productNumber; // Number of products in the collection
    }

    /**
     * Fetches the response containing the collection list for the specified page.
     *
     * @param pageIndex the index of the page to fetch.
     * @return the response containing collection data.
     */
    private Response getCollectionListResponse(int pageIndex) {
        String path = String.format("/itemservice/api/collections/list/%d?page=%d&size=50&itemType=BUSINESS_PRODUCT&search=",
                loginInfo.getStore().getId(), pageIndex);
        return new APIUtils().get(path, loginInfo.getAccessToken())
                .then().statusCode(200).extract().response();
    }

    /**
     * Retrieves the complete list of product collections for the seller's store.
     *
     * @return a list of collection items.
     */
    public List<CollectionItem> getListProductCollection() {
        List<CollectionItem> collectionItemList = new ArrayList<>();

        // Get the total number of pages
        int totalPages = getCollectionListResponse(0).jsonPath().getInt("totalPage");

        // Fetch collection data from all pages
        List<JsonPath> jsonPaths = IntStream.range(0, totalPages)
                .parallel()
                .mapToObj(this::getCollectionListResponse)
                .map(Response::jsonPath)
                .toList();

        // Collect collection items from all pages
        jsonPaths.forEach(jsonPath -> collectionItemList.addAll(jsonPath.getList("lstCollection", CollectionItem.class)));

        return collectionItemList;
    }
}