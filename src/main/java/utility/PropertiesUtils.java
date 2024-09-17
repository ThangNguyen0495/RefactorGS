package utility;

import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Utility class for loading and retrieving properties from a configuration file.
 */
public class PropertiesUtils {

    private static final String PROPERTIES_FILE = "config.properties";
    private static final Properties properties = new Properties();

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
     * Retrieves the seller account property value.
     *
     * @return The seller account property value.
     */
    public static String getSellerAccount() {
        return getProperty("sellerAccount");
    }

    /**
     * Retrieves the seller password property value.
     *
     * @return The seller password property value.
     */
    public static String getSellerPassword() {
        return getProperty("sellerPassword");
    }

    /**
     * Retrieves the buyer account property value.
     *
     * @return The buyer account property value.
     */
    public static String getBuyerAccount() {
        return getProperty("buyerAccount");
    }

    /**
     * Retrieves the buyer password property value.
     *
     * @return The buyer password property value.
     */
    public static String getBuyerPassword() {
        return getProperty("buyerPassword");
    }

    /**
     * Retrieves the Storefront endpoint property value.
     *
     * @return The Storefront endpoint property value.
     */
    public static String getSFEndPoint() {
        return getProperty("sfEndpoint");
    }
}
