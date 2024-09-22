package api.seller.product;

import api.seller.login.APISellerLogin;
import lombok.Data;
import utility.APIUtils;

import java.util.List;

public class APIGetProductCollection {
    private final APISellerLogin.LoginInformation loginInfo;

    public APIGetProductCollection(APISellerLogin.Credentials credentials) {
        this.loginInfo = new APISellerLogin().getSellerInformation(credentials);
    }


    /**
     * Class representing a product collection.
     */
    @Data
    public static class ProductCollection {
        private int id; // ID of the collection
        private String collectionName; // Name of the collection
        private String collectionType; // Type of the collection (e.g., MANUAL)
        private int sellerId; // ID of the seller
        private String itemType; // Type of the items in the collection (e.g., BUSINESS_PRODUCT)
        private List<String> images; // List of images associated with the collection
        private List<String> filterOptions; // List of filter options for the collection
        private List<String> sortOptions; // List of sort options for the collection
    }

    public List<ProductCollection> getProductCollections(int productId){
        return new APIUtils().get("/itemservice/api/collections/products/%d".formatted(productId), loginInfo.getAccessToken())
                .then().statusCode(200)
                .extract().jsonPath()
                .getList(".", ProductCollection.class);
    }
}
