import api.seller.login.APISellerLogin;
import api.seller.product.APIGetProductDetail;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import utility.ListenerUtils;
import utility.PropertiesUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;


@Listeners(ListenerUtils.class)
public class CheckTest {
    public static String getProperty(String key, String langKey) {
        Locale locale = Locale.forLanguageTag(langKey); // "en" for English, "vi" for Vietnamese
        ResourceBundle bundle = ResourceBundle.getBundle("localization/dashboard", locale);
        return bundle.getString(key);
    }

    @Test
    void t() throws JsonProcessingException {

        String ppFile = "/Users/nguyenthang/IdeaProjects/LocalProject1/src/main/resources/localization/dashboard_vi.properties";

        Properties properties = new Properties();
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(ppFile);
            properties.load(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println(properties.getProperty("input.blank.error", "en"));

    }


}
