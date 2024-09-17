package utility;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class APIUtils {

    public APIUtils() {
//        RestAssured.proxy("localhost", 8888);
        RestAssured.baseURI = PropertiesUtils.getAPIHost();
    }

    /**
     * Performs a GET request to the specified path with OAuth2 authentication and optional headers.
     *
     * @param path    The API endpoint path.
     * @param token   The OAuth2 token for authentication.
     * @param headers Optional headers to include in the request.
     * @return The API response.
     */
    public Response get(String path, String token, Map<String, Object> headers) {
        return given()
                .relaxedHTTPSValidation()
                .auth().oauth2(token)
                .headers((headers == null) ? Map.of() : headers)
                .contentType(ContentType.JSON)
                .when().get(path);
    }

    /**
     * Performs a GET request to the specified path with OAuth2 authentication and no headers.
     *
     * @param path  The API endpoint path.
     * @param token The OAuth2 token for authentication.
     * @return The API response.
     */
    public Response get(String path, String token) {
        return get(path, token, null);
    }

    /**
     * Performs a POST request to the specified path with OAuth2 authentication and optional headers and body.
     *
     * @param path    The API endpoint path.
     * @param token   The OAuth2 token for authentication.
     * @param body    The request body to send.
     * @param headers Optional headers to include in the request.
     * @return The API response.
     */
    public Response post(String path, String token, Object body, Map<String, Object> headers) {
        return given()
                .relaxedHTTPSValidation()
                .auth().oauth2(token)
                .headers((headers == null) ? Map.of() : headers)
                .contentType(ContentType.JSON)
                .body(body)
                .when().post(path);
    }

    /**
     * Performs a POST request to the specified path with OAuth2 authentication and optional headers.
     *
     * @param path    The API endpoint path.
     * @param token   The OAuth2 token for authentication.
     * @param headers Optional headers to include in the request.
     * @return The API response.
     */
    public Response post(String path, String token, Map<String, Object> headers) {
        return post(path, token, null, headers);
    }

    /**
     * Performs a POST request to the specified path with OAuth2 authentication and no headers.
     *
     * @param path  The API endpoint path.
     * @param token The OAuth2 token for authentication.
     * @param body  The request body to send.
     * @return The API response.
     */
    public Response post(String path, String token, Object body) {
        return post(path, token, body, null);
    }

    /**
     * Performs a PUT request to the specified path with OAuth2 authentication, optional headers, and body.
     *
     * @param path    The API endpoint path.
     * @param token   The OAuth2 token for authentication.
     * @param body    The request body to send.
     * @param headers Optional headers to include in the request.
     * @return The API response.
     */
    public Response put(String path, String token, Object body, Map<String, Object> headers) {
        return given()
                .relaxedHTTPSValidation()
                .auth().oauth2(token)
                .headers((headers == null) ? Map.of() : headers)
                .contentType(ContentType.JSON)
                .body(body)
                .when().put(path);
    }

    /**
     * Performs a PUT request to the specified path with OAuth2 authentication and no headers.
     *
     * @param path  The API endpoint path.
     * @param token The OAuth2 token for authentication.
     * @param body  The request body to send.
     * @return The API response.
     */
    public Response put(String path, String token, Object body) {
        return put(path, token, body, null);
    }

    /**
     * Performs a DELETE request to the specified path with OAuth2 authentication, optional headers, and body.
     *
     * @param path    The API endpoint path.
     * @param token   The OAuth2 token for authentication.
     * @param body    The request body to send (optional).
     * @param headers Optional headers to include in the request.
     * @return The API response.
     */
    public Response delete(String path, String token, String body, Map<String, Object> headers) {
        return given()
                .relaxedHTTPSValidation()
                .auth().oauth2(token)
                .headers((headers == null) ? Map.of() : headers)
                .contentType(ContentType.JSON)
                .body(body)
                .when().delete(path);
    }

    /**
     * Performs a DELETE request to the specified path with OAuth2 authentication and optional headers.
     *
     * @param path    The API endpoint path.
     * @param token   The OAuth2 token for authentication.
     * @param headers Optional headers to include in the request.
     * @return The API response.
     */
    public Response delete(String path, String token, Map<String, Object> headers) {
        return delete(path, token, null, headers);
    }

    /**
     * Performs a DELETE request to the specified path with OAuth2 authentication and no headers.
     *
     * @param path  The API endpoint path.
     * @param token The OAuth2 token for authentication.
     * @return The API response.
     */
    public Response delete(String path, String token) {
        return delete(path, token, null, null);
    }

    /**
     * Performs a POST request to the specified path with OAuth2 authentication, a request body, and optional headers.
     *
     * @param path    The API endpoint path.
     * @param token   The OAuth2 token for authentication.
     * @param body    The request body to send.
     * @param headers Optional headers to include in the request.
     * @return The API response.
     */
    public Response search(String path, String token, String body, Map<String, Object> headers) {
        return given()
                .relaxedHTTPSValidation()
                .auth().oauth2(token)
                .headers((headers == null) ? Map.of() : headers)
                .contentType(ContentType.JSON)
                .body(body)
                .when().post(path);
    }

    /**
     * Performs a POST request to the specified path with OAuth2 authentication, a request body, and no headers.
     *
     * @param path  The API endpoint path.
     * @param token The OAuth2 token for authentication.
     * @param body  The request body to send.
     * @return The API response.
     */
    public Response search(String path, String token, String body) {
        return search(path, token, body, null);
    }

    /**
     * Performs a POST request to the specified path with OAuth2 authentication and a request body.
     *
     * @param path The API endpoint path.
     * @param body The request body to send.
     * @return The API response.
     */
    public Response login(String path, Object body) {
        return given().relaxedHTTPSValidation()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post(path);
    }
}
