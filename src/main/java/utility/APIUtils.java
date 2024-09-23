package utility;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

import static io.restassured.RestAssured.given;

/**
 * Utility class for interacting with APIs using RestAssured.
 * It supports GET, POST, PUT, and DELETE requests with optional OAuth2 authentication and headers.
 * This class also handles proxy configuration and base URI settings.
 */
public class APIUtils {

    /**
     * Initializes the APIUtils class.
     * Configures RestAssured proxy settings if enabled, and sets the base URI from the properties.
     */
    public APIUtils() {
        configureProxy();
        setBaseURI();
    }

    /**
     * Configures proxy settings if the 'enableProxy' property is set to true.
     * The proxy is set to 'localhost' on port 8888.
     */
    private void configureProxy() {
        if (PropertiesUtils.getEnableProxy()) {
            RestAssured.proxy("localhost", 8888);
        }
    }

    /**
     * Sets the base URI for API requests using the 'apiHost' property.
     */
    private void setBaseURI() {
        RestAssured.baseURI = PropertiesUtils.getAPIHost();
    }

    /**
     * Builds a request specification with optional OAuth2 authentication and headers.
     *
     * @param token   The OAuth2 token for authentication (can be null).
     * @param headers Optional headers to include in the request. If null, an empty map is used.
     * @return A RequestSpecification object for making API requests.
     */
    private RequestSpecification buildRequest(String token, Map<String, Object> headers) {
        RequestSpecification request = given()
                .relaxedHTTPSValidation()
                .contentType(ContentType.JSON);

        if (token != null) {
            request.auth().oauth2(token);
        }

        if (headers != null) {
            request.headers(headers);
        }

        return request;
    }

    /**
     * Sends an HTTP request with the specified method, path, token, body, and headers.
     *
     * @param method  The HTTP method to use (GET, POST, PUT, DELETE).
     * @param path    The API endpoint path.
     * @param token   The OAuth2 token for authentication (can be null).
     * @param body    The request body (can be null).
     * @param headers Optional headers to include in the request.
     * @return The API response.
     */
    private Response sendRequest(String method, String path, String token, Object body, Map<String, Object> headers) {
        RequestSpecification request = buildRequest(token, headers);

        if (body != null) {
            request.body(body);
        }

        return switch (method.toUpperCase()) {
            case "GET" -> request.get(path);
            case "POST" -> request.post(path);
            case "PUT" -> request.put(path);
            case "DELETE" -> request.delete(path);
            default -> throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        };
    }

    /**
     * Performs a GET request to the specified path with optional OAuth2 authentication and headers.
     *
     * @param path    The API endpoint path.
     * @param token   The OAuth2 token for authentication (can be null).
     * @param headers Optional headers to include in the request.
     * @return The API response.
     */
    public Response get(String path, String token, Map<String, Object> headers) {
        return sendRequest("GET", path, token, null, headers);
    }

    /**
     * Performs a GET request to the specified path with optional OAuth2 authentication.
     *
     * @param path  The API endpoint path.
     * @param token The OAuth2 token for authentication (can be null).
     * @return The API response.
     */
    public Response get(String path, String token) {
        return get(path, token, null);
    }

    /**
     * Performs a POST request to the specified path with optional OAuth2 authentication, body, and headers.
     *
     * @param path    The API endpoint path.
     * @param token   The OAuth2 token for authentication (can be null).
     * @param body    The request body to send (can be null).
     * @param headers Optional headers to include in the request.
     * @return The API response.
     */
    public Response post(String path, String token, Object body, Map<String, Object> headers) {
        return sendRequest("POST", path, token, body, headers);
    }

    /**
     * Performs a POST request to the specified path with optional OAuth2 authentication and body.
     *
     * @param path  The API endpoint path.
     * @param token The OAuth2 token for authentication (can be null).
     * @param body  The request body to send (can be null).
     * @return The API response.
     */
    public Response post(String path, String token, Object body) {
        return post(path, token, body, null);
    }

    /**
     * Performs a PUT request to the specified path with optional OAuth2 authentication, body, and headers.
     *
     * @param path    The API endpoint path.
     * @param token   The OAuth2 token for authentication (can be null).
     * @param body    The request body to send (can be null).
     * @param headers Optional headers to include in the request.
     * @return The API response.
     */
    public Response put(String path, String token, Object body, Map<String, Object> headers) {
        return sendRequest("PUT", path, token, body, headers);
    }

    /**
     * Performs a PUT request to the specified path with optional OAuth2 authentication and body.
     *
     * @param path  The API endpoint path.
     * @param token The OAuth2 token for authentication (can be null).
     * @param body  The request body to send (can be null).
     * @return The API response.
     */
    public Response put(String path, String token, Object body) {
        return put(path, token, body, null);
    }

    /**
     * Performs a DELETE request to the specified path with optional OAuth2 authentication, body, and headers.
     *
     * @param path    The API endpoint path.
     * @param token   The OAuth2 token for authentication (can be null).
     * @param body    The request body to send (can be null).
     * @param headers Optional headers to include in the request.
     * @return The API response.
     */
    public Response delete(String path, String token, Object body, Map<String, Object> headers) {
        return sendRequest("DELETE", path, token, body, headers);
    }

    /**
     * Performs a DELETE request to the specified path with optional OAuth2 authentication.
     *
     * @param path  The API endpoint path.
     * @param token The OAuth2 token for authentication (can be null).
     * @return The API response.
     */
    public Response delete(String path, String token) {
        return delete(path, token, null, null);
    }
}
