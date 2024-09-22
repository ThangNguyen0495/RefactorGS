package api.seller.customer;

import api.seller.login.APISellerLogin;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.Data;
import utility.APIUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Class to interact with the segment API for sellers.
 */
public class APIGetSegmentList {
    private final APISellerLogin.LoginInformation loginInfo;

    /**
     * Constructor to initialize the API with seller credentials.
     *
     * @param credentials the seller's credentials.
     */
    public APIGetSegmentList(APISellerLogin.Credentials credentials) {
        this.loginInfo = new APISellerLogin().getSellerInformation(credentials);
    }

    /**
     * Represents a user segment.
     */
    @Data
    public static class Segment {
        private int id; // Segment ID
        private String name; // Segment name
        private int storeId; // Associated store ID
        private String matchCondition; // Condition for matching users
        private int userCount; // Count of users in the segment
    }

    /**
     * Fetches the response containing segments for the specified page.
     *
     * @param pageIndex the index of the page to fetch.
     * @return the response containing segment data.
     */
    private Response getSegmentListResponse(int pageIndex) {
        String path = String.format("/beehiveservices/api/segments/store/%d?page=%d&size=50&name.contains=&sort=id,desc",
                loginInfo.getStore().getId(), pageIndex);
        return new APIUtils().get(path, loginInfo.getAccessToken())
                .then().statusCode(200).extract().response();
    }

    /**
     * Retrieves the complete list of segments for the seller's store.
     *
     * @return a list of segments.
     */
    public List<Segment> getSegmentList() {
        List<Segment> segmentList = new ArrayList<>();

        // Get the total number of segments
        int totalSegments = Integer.parseInt(getSegmentListResponse(0).getHeader("X-Total-Count"));

        // Calculate the number of pages
        int numberOfPages = (totalSegments + 49) / 50; // Adjusted for page size of 50

        // Fetch segment data from all pages in parallel
        List<JsonPath> jsonPaths = IntStream.range(0, numberOfPages)
                .parallel()
                .mapToObj(this::getSegmentListResponse)
                .map(Response::jsonPath)
                .toList();

        // Collect segments from all pages
        jsonPaths.forEach(jsonPath -> segmentList.addAll(jsonPath.getList(".", Segment.class)));

        return segmentList;
    }
}
