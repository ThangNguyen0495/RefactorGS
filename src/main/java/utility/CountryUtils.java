package utility;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import com.mifmif.common.regex.Generex;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import static org.apache.commons.lang.math.RandomUtils.nextInt;

public class CountryUtils {

    private static final String COUNTRY_CODES_FILE = "country/CountryCodes.json";
    private static final JsonNode countryData;

    static {
        try {
            countryData = new ObjectMapper().readTree(readFileToString());
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Reads the contents of a file and returns it as a String.
     *
     * @return The contents of the file as a String.
     * @throws IOException        if an error occurs during file reading.
     * @throws URISyntaxException if the file path is invalid.
     */
    private static String readFileToString() throws IOException, URISyntaxException {
        URL url = Resources.getResource(CountryUtils.COUNTRY_CODES_FILE);
        return FileUtils.readFileToString(new File(url.toURI()), StandardCharsets.UTF_8);
    }

    /**
     * Returns a list of all countries available in the JSON data.
     *
     * @return A list of country names.
     */
    public static List<String> getCountryList() {
        Iterator<String> fieldNames = countryData.fieldNames();
        List<String> countries = new ArrayList<>();
        fieldNames.forEachRemaining(countries::add);
        return countries;
    }

    /**
     * Returns a random country from the list of available countries, excluding Vietnam.
     *
     * @return A randomly selected country name, or throws an exception if no valid countries are available.
     */
    public static String randomCountry() {
        // Get the list of countries and filter out "Vietnam"
        List<String> availableCountries = getCountryList()
                .stream()
                .filter(country -> !country.equalsIgnoreCase("Vietnam"))
                .toList();

        // Validate the filtered list
        if (availableCountries.isEmpty()) {
            throw new IllegalStateException("No countries available for selection.");
        }

        // Select a random country from the filtered list
        return availableCountries.get(nextInt(availableCountries.size()));
    }


    /**
     * Fetches a specific attribute (e.g., phoneCode, countryCode, phoneRegex) for a given country.
     *
     * @param country  The name of the country.
     * @param attribute The attribute to fetch (e.g., "phoneCode").
     * @return The value of the requested attribute, or null if not found.
     */
    private static String getCountryAttribute(String country, String attribute) {
        JsonNode countryNode = countryData.get(country);
        if (countryNode == null) {
            throw new IllegalArgumentException("Country not found: " + country);
        }
        JsonNode attributeNode = countryNode.get(attribute);
        if (attributeNode == null) {
            throw new IllegalArgumentException("Attribute '" + attribute + "' not found for country: " + country);
        }
        return attributeNode.asText();
    }

    /**
     * Returns the phone code for a given country.
     *
     * @param country The name of the country.
     * @return The phone code (e.g., +84) for the given country.
     */
    public static String getPhoneCode(String country) {
        return "+" + getCountryAttribute(country, "phoneCode");
    }

    /**
     * Returns the country code for a given country.
     *
     * @param country The name of the country.
     * @return The country code (e.g., VN) for the given country.
     */
    public static String getCountryCode(String country) {
        return getCountryAttribute(country, "countryCode");
    }

    /**
     * Retrieves the name of a country based on its country code.
     *
     * @param countryCode The code of the country to find.
     * @return The name of the country corresponding to the given code.
     * @throws IllegalArgumentException If no country matches the given code.
     */
    public static String getCountryNameByCode(String countryCode) {
        return getCountryList().stream()
                .filter(country -> getCountryCode(country).equalsIgnoreCase(countryCode))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("No country found with the code '%s'".formatted(countryCode))
                );
    }


    /**
     * Returns the phone regex for a given country.
     *
     * @param country The name of the country.
     * @return The phone regex for validating or generating phone numbers for the given country.
     */
    public static String getPhoneRegex(String country) {
        return getCountryAttribute(country, "phoneRegex");
    }

    /**
     * Generates a random phone number based on the provided regex.
     *
     * @param regex The regex pattern to generate the phone number.
     * @return A random valid phone number.
     */
    public static String generatePhoneFromRegex(String regex) {
        Generex generex = new Generex(regex);
        return IntStream.range(0, 1000).parallel()
                .mapToObj(ignored -> generex.random())
                .filter(phone -> phone.matches("\\d+"))
                .findAny()
                .orElseThrow(() -> new RuntimeException("Failed to generate a valid phone number from regex: " + regex));
    }

    /**
     * Generates a random valid phone number for a given country.
     *
     * @param country The name of the country.
     * @return A random valid phone number for the specified country.
     */
    public static String generatePhoneNumberByCountry(String country) {
        return generatePhoneFromRegex(getPhoneRegex(country));
    }
}
