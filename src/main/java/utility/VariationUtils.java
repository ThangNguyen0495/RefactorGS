package utility;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.apache.commons.lang.math.RandomUtils.nextInt;

public class VariationUtils {

    private static final int MAX_VARIATION_QUANTITY = 2;
    private static final int MAX_VARIATION_QUANTITY_FOR_EACH_VARIATION = 5;
    private static final int MAX_VARIATION_QUANTITY_FOR_ALL_VARIATIONS = 10;

    /**
     * Generates a list of variation values based on the default language, index, and size.
     */
    private static List<String> generateListString(String defaultLanguage, int index, int size) {
        return IntStream.range(0, size)
                .mapToObj(i -> String.format("%s_var%s_%s", defaultLanguage, index, i + 1))
                .toList();
    }

    /**
     * Returns the number of variation values for each group based on the total number of variations and groups.
     */
    private static List<Integer> getNumOfValuesOnEachGroup(int numberOfVariations, int numberOfGroups) {
        if (numberOfGroups == 1) {
            return Collections.singletonList(numberOfVariations);
        }

        int factor = IntStream.range(2, Math.min(numberOfVariations, MAX_VARIATION_QUANTITY_FOR_EACH_VARIATION))
                .filter(i -> numberOfVariations % i == 0)
                .findFirst()
                .orElse(1);

        return java.util.List.of(factor, numberOfVariations / factor);
    }

    /**
     * Generates a random variation map with variation names and values.
     */
    public static Map<String, List<String>> randomVariationMap(String defaultLanguage) {
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
    private static List<String> mixVariationValue(List<String> list1, List<String> list2) {
        return list1.stream()
                .flatMap(var1 -> list2.stream()
                        .map(var2 -> String.format("%s|%s", var1, var2)))
                .toList();
    }

    /**
     * Retrieves a variation group name string from the keys of the variation map.
     * Assumes the map has at least one and at most two keys.
     *
     * @param variationMap A map where the keys represent variation names.
     * @return A string combining the variation names, separated by a pipe if there are two.
     */
    public static String getVariationName(Map<String, List<String>> variationMap) {
        List<String> varNames = new ArrayList<>(variationMap.keySet());
        return String.join("|", varNames);
    }

    /**
     * Retrieves a list of variation values from the values of the variation map.
     * If the map has multiple value lists, combines them; otherwise, returns the single list.
     *
     * @param variationMap A map where the values are lists of variation values.
     * @return A list of combined variation values if multiple lists are present, otherwise the single list.
     */
    public static List<String> getVariationList(Map<String, List<String>> variationMap) {
        List<List<String>> varValues = new ArrayList<>(variationMap.values());
        return (varValues.size() > 1) ? mixVariationValue(varValues.getFirst(), varValues.getLast()) : varValues.getFirst();
    }


    /**
     * Generates a map of variation groups and their corresponding values.
     * If there is only one group, it directly maps the group to its list of values.
     * Otherwise, it splits both the group and values by '|' and constructs a map
     * where each group name is associated with its respective values.
     *
     * @param group  A string representing variation groups, separated by '|'.
     * @param values A list of values corresponding to the groups, where each value
     *               contains '|' to represent individual group values.
     * @return A map where the keys are group names and the values are lists of corresponding values.
     */
    public static Map<String, List<String>> getVariationMap(String group, List<String> values) {
        String[] groupArray = group.split("\\|");
        int numberOfGroups = groupArray.length;

        // If there is only one group, return a simple map
        if (numberOfGroups == 1) {
            return Map.of(group, values);
        }

        // For multiple groups, split values and map to respective group names
        return IntStream.range(0, numberOfGroups)
                .boxed()
                .collect(Collectors.toMap(
                        groupIndex -> groupArray[groupIndex],
                        groupIndex -> values.stream()
                                .map(value -> value.split("\\|")[groupIndex])
                                .distinct()
                                .toList(),
                        (_, b) -> b, TreeMap::new
                ));
    }
}