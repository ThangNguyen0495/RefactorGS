package utility;

import api.seller.login.APISellerLogin;
import org.testng.Assert;

import java.io.InputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * Utility class for loading and retrieving properties from a configuration file.
 */
public class PropertiesUtils {
    private static final String ENV_PROPERTIES_FILE = "config.properties";
    private static final Properties envProperties = new Properties();

    static {
        try (InputStream input = PropertiesUtils.class.getClassLoader().getResourceAsStream(ENV_PROPERTIES_FILE)) {
            if (input == null) {
                throw new RuntimeException("Unable to find " + ENV_PROPERTIES_FILE);
            }
            // Load the properties file from the class path
            envProperties.load(input);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to load properties file: " + ENV_PROPERTIES_FILE, ex);
        }
    }

    /**
     * Retrieves the value associated with the specified key from the dashboard properties
     * for the specified language key.
     *
     * @param key     The key for the desired property.
     * @param langKey The language key to determine which language-specific properties file to load.
     * @return The property value as a String, or null if the key is not found.
     */
    public static String getDashboardProperty(String key, String langKey) {
        Assert.assertTrue(
                "en".equals(langKey) || "vi".equals(langKey),
                "LangKey must be 'en' (English) or 'vi' (Vietnamese)"
        );
        Locale locale = Locale.forLanguageTag(langKey); // "en" for English, "vi" for Vietnamese
        ResourceBundle bundle = ResourceBundle.getBundle("localization/dashboard", locale);
        return bundle.getString(key);
    }

    /**
     * Retrieves the value associated with the specified key from the storefront properties
     * for the specified language key.
     *
     * @param key     The key for the desired property.
     * @param langKey The language key to determine which language-specific properties file to load.
     * @return The property value as a String, or null if the key is not found.
     */
    public static String getStorefrontProperty(String key, String langKey) {
        Locale locale = Locale.forLanguageTag(langKey); // "en" for English, "vi" for Vietnamese
        ResourceBundle bundle = ResourceBundle.getBundle("localization/dashboard", locale);
        return bundle.getString(key);
    }

    /**
     * Retrieves the property value for the given key.
     *
     * @param key The property key.
     * @return The property value, or null if the key does not exist.
     */
    private static String getProperty(String key) {
        return envProperties.getProperty(key);
    }

    /**
     * Retrieves the environment setting from configuration properties.
     *
     * @return The environment setting value.
     */
    public static String getEnv() {
        return getProperty("env");
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
