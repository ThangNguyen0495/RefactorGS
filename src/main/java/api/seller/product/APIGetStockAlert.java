package api.seller.product;

import api.seller.login.APISellerLogin;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import utility.APIUtils;

import java.util.List;

public class APIGetStockAlert {

    APISellerLogin.LoginInformation loginInfo;
    APISellerLogin.Credentials loginInformation;
    public APIGetStockAlert(APISellerLogin.Credentials loginInformation) {
        this.loginInformation = loginInformation;
    	loginInfo = new APISellerLogin().getSellerInformation(loginInformation);
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class StockAlert {
        private int itemId;
        private Integer modelId;
        private int alertNumber;
    }

    private List<StockAlert> getListStockAlert(int productId) {
        return new APIUtils().get("/itemservice/api/stock-alert/storeId/%s/itemId/%s/with-models".formatted(loginInfo.getStore().getId(), productId), loginInfo.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList(".", StockAlert.class);

    }

    public List<Integer> getProductStockAlert(int productId) {
        var stockAlerts = getListStockAlert(productId);
        return (stockAlerts.size() > 1)
                ? stockAlerts.stream().filter(alert -> alert.getModelId() != null).map(StockAlert::getAlertNumber).toList()
                : stockAlerts.stream().map(StockAlert::getAlertNumber).toList();
    }
}
