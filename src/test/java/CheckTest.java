import com.fasterxml.jackson.core.JsonProcessingException;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import utility.ListenerUtils;


@Listeners(ListenerUtils.class)
public class CheckTest {

    @Test
    void t() throws JsonProcessingException {

        Assert.fail();


    }

    @Test
    void t1() throws JsonProcessingException {


    }

    @Test
    void t2() throws JsonProcessingException {


        throw new SkipException("Skipping the test as no linked products found.");

    }


}
