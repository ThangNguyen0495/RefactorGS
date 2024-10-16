package utility;

import api.seller.login.APISellerLogin;

import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Utility class for loading and retrieving properties from a configuration file.
 */
public class PropertiesUtils {

    private static final String PROPERTIES_FILE = "config.properties";
    private static final Properties properties = new Properties();

    /*
      Static block to load the properties from the "config.properties" file.
      The file is loaded from the classpath, and if it cannot be found or an error occurs while loading,
      a RuntimeException is thrown.
     */
    static {
        try (InputStream input = PropertiesUtils.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (input == null) {
                throw new RuntimeException("Unable to find " + PROPERTIES_FILE);
            }
            // Load the properties file from the class path
            properties.load(input);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to load properties file: " + PROPERTIES_FILE, ex);
        }
    }

    /**
     * Retrieves the property value for the given key.
     *
     * @param key The property key.
     * @return The property value, or null if the key does not exist.
     */
    private static String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * Retrieves the domain property value.
     *
     * @return The domain property value.
     */
    public static String getDomain() {
        return getProperty("domain");
    }

    /**
     * Retrieves the API host property value.
     *
     * @return The API host property value.
     */
    public static String getAPIHost() {
        return getProperty("apiHost");
    }

    /**
     * Retrieves the browser property value.
     *
     * @return The browser property value.
     */
    public static String getBrowser() {
        return getProperty("browser");
    }

    /**
     * Retrieves the headless property value.
     *
     * @return true if headless mode is enabled; false otherwise.
     */
    public static boolean getHeadless() {
        String headlessProperty = getProperty("headless");
        return Boolean.parseBoolean(headlessProperty);
    }

    /**
     * Retrieves the seller credentials from the property file.
     *
     * @return APISellerLogin.Credentials containing the seller account and password.
     */
    public static APISellerLogin.Credentials getSellerCredentials() {
        return getCredentials("sellerAccount", "sellerPassword");
    }

    /**
     * Retrieves the buyer credentials from the property file.
     *
     * @return APISellerLogin.Credentials containing the buyer account and password.
     */
    public static APISellerLogin.Credentials getBuyerCredentials() {
        return getCredentials("buyerAccount", "buyerPassword");
    }

    /**
     * Helper method to retrieve credentials based on the provided account and password keys.
     *
     * @param accountKey  The key to retrieve the account from the property file.
     * @param passwordKey The key to retrieve the password from the property file.
     * @return APISellerLogin.Credentials containing the account and password.
     */
    private static APISellerLogin.Credentials getCredentials(String accountKey, String passwordKey) {
        return new APISellerLogin.Credentials(getProperty(accountKey), getProperty(passwordKey));
    }

    /**
     * Retrieves the Storefront URL from the configuration properties.
     *
     * @return The Storefront URL as a {@code String}.
     * This is typically used to access the Storefront endpoint.
     */
    public static String getStoreURL() {
        return getProperty("storeURL");
    }

    /**
     * Retrieves the value of the "enableProxy" property and returns it as a boolean.
     *
     * @return {@code true} if the "enableProxy" property is set to "true", {@code false} otherwise.
     */
    public static boolean getEnableProxy() {
        return Boolean.parseBoolean(getProperty("enableProxy"));
    }

    /**
     * Retrieves the seller bundle ID from the application properties.
     *
     * @return the seller bundle ID as a String.
     */
    public static String getSellerBundleId() {
        return getProperty("goSELLERBundleId");
    }

    /**
     * Retrieves the buyer bundle ID from the application properties.
     *
     * @return the buyer bundle ID as a String.
     */
    public static String getBuyerBundleId() {
        return getProperty("goBUYERBundleId");
    }
}
