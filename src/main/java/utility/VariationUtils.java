package utility;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.apache.commons.lang.math.RandomUtils.nextInt;

public class VariationUtils {

    private static final int MAX_VARIATION_QUANTITY = 2;
    private static final int MAX_VARIATION_QUANTITY_FOR_EACH_VARIATION = 20;
    private static final int MAX_VARIATION_QUANTITY_FOR_ALL_VARIATIONS = 50;

    /**
     * Generates a list of variation values based on the default language, index, and size.
     */
    private List<String> generateListString(String defaultLanguage, int index, int size) {
        return IntStream.range(0, size)
                .mapToObj(i -> String.format("%s_var%s_%s", defaultLanguage, index, i + 1))
                .collect(Collectors.toList());
    }

    /**
     * Returns the number of variation values for each group based on the total number of variations and groups.
     */
    public List<Integer> getNumOfValuesOnEachGroup(int numberOfVariations, int numberOfGroups) {
        if (numberOfGroups == 1) {
            return Collections.singletonList(numberOfVariations);
        }

        int factor = IntStream.range(2, Math.min(numberOfVariations, MAX_VARIATION_QUANTITY_FOR_EACH_VARIATION))
                .filter(i -> numberOfVariations % i == 0)
                .findFirst()
                .orElse(1);

        return List.of(factor, numberOfVariations / factor);
    }

    /**
     * Generates a random variation map with variation names and values.
     */
    public Map<String, List<String>> randomVariationMap(String defaultLanguage) {
        int numberOfGroups = nextInt(MAX_VARIATION_QUANTITY) + 1;
        int numberOfVariations = nextInt(numberOfGroups == 1
                ? MAX_VARIATION_QUANTITY_FOR_EACH_VARIATION
                : MAX_VARIATION_QUANTITY_FOR_ALL_VARIATIONS) + 1;

        List<Integer> numValuesPerGroup = getNumOfValuesOnEachGroup(numberOfVariations, numberOfGroups);

        return IntStream.range(0, numValuesPerGroup.size())
                .boxed()
                .collect(Collectors.toMap(
                        i -> String.format("%s_var%s", defaultLanguage, i + 1),
                        i -> generateListString(defaultLanguage, i + 1, numValuesPerGroup.get(i)),
                        (_, b) -> b,
                        TreeMap::new
                ));
    }

    /**
     * Combines variation values from two lists into a single list with all possible combinations.
     */
    public List<String> mixVariationValue(List<String> list1, List<String> list2) {
        return list1.stream()
                .flatMap(var1 -> list2.stream()
                        .map(var2 -> String.format("%s|%s", var1, var2)))
                .collect(Collectors.toList());
    }

    /**
     * Creates a variation name string from the keys of the variation map with a specified language prefix.
     */
    public String getVariationName(Map<String, List<String>> variationMap, String language) {
        List<String> varNames = new ArrayList<>(variationMap.keySet());
        return varNames.stream()
                .collect(Collectors.joining("|", String.format("%s_%s", language, varNames.getFirst()), ""));
    }

    /**
     * Generates a combined variation list with values prefixed by the specified language.
     */
    public List<String> getVariationList(Map<String, List<String>> variationMap, String language) {
        List<List<String>> varValues = new ArrayList<>(variationMap.values());
        List<String> variationList = varValues.getFirst().stream()
                .map(value -> String.format("%s_%s", language, value))
                .collect(Collectors.toList());

        for (int i = 1; i < varValues.size(); i++) {
            variationList = mixVariationValue(variationList, varValues.get(i));
        }

        return variationList;
    }

    /**
     * Generates a combined variation list without a language prefix.
     */
    public List<String> getVariationList(Map<String, List<String>> variationMap) {
        List<List<String>> varValues = new ArrayList<>(variationMap.values());
        List<String> variationList = new ArrayList<>(varValues.getFirst());

        for (int i = 1; i < varValues.size(); i++) {
            variationList = mixVariationValue(variationList, varValues.get(i));
        }

        return variationList;
    }
}