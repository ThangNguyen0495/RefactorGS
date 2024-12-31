import org.testng.Assert;
import org.testng.annotations.*;
import utility.ListenerUtils;

import java.util.Arrays;
import java.util.List;

@Listeners(ListenerUtils.class)
public class CheckTest {

    @DataProvider(name = "regressionTest")
    Object[][] dataRegression() {
        return new Object[][]{
                {"Product1"},
                {"Product2"},
                {"Product3"},
                {"Product4"},
                {"Product5"},
                {"Product6"}
        };
    }

    @DataProvider(name = "smokeTest")
    Object[][] dataSmoke() {
        List<String> testNames = List.of("6");

        // Get all test cases from the regression data provider and filter based on testNames
        Object[][] allData = dataRegression();

        return Arrays.stream(allData)
                .filter(data -> testNames.stream().anyMatch(testName -> ((String) data[0]).contains(testName)))
                .toArray(Object[][]::new);
    }

    @Test(dataProvider = "regressionTest")
    void testRegression(String string) {
        Assert.assertNotEquals(string, "Product6");
    }

    @Test(dataProvider = "smokeTest")
    void testSmoke(String string) {
        Assert.assertNotEquals(string, "Product6");
    }
}
