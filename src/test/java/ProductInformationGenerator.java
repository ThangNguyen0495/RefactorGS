import api.seller.login.APIDashboardLogin;
import api.seller.product.APIGetProductDetail;
import api.seller.product.APIGetProductDetail.ProductInformation;
import api.seller.setting.APIGetBranchList;
import api.seller.setting.APIGetVATList;
import utility.VariationUtils;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.IntStream;

import static org.apache.commons.lang.math.JVMRandom.nextLong;
import static org.apache.commons.lang.math.RandomUtils.nextBoolean;
import static org.apache.commons.lang.math.RandomUtils.nextInt;

public class ProductInformationGenerator {
    private static final long MAX_PRICE = 99999999999L;

    private static final Random RANDOM = new Random();

    public static ProductInformation generateProductInformation(APIDashboardLogin.Credentials credentials, String langKey,
                                                                boolean hasModel,
                                                                boolean noCost,
                                                                boolean noDiscount,
                                                                boolean manageByIMEI,
                                                                boolean hasSEO,
                                                                boolean isHideStock,
                                                                boolean showOutOfStock,
                                                                boolean hasDimension,
                                                                boolean hasLot,
                                                                boolean hasAttribution,
                                                                boolean onWeb,
                                                                boolean onApp,
                                                                boolean inStore,
                                                                boolean inGoSOCIAL,
                                                                int... branchStock) {

        var vatInfoList = new APIGetVATList(credentials).getVATInformation();
        var vatNames = APIGetVATList.getVATNames(vatInfoList);
        var branchInfoList = new APIGetBranchList(credentials).getBranchInformation();
        var branchIds = APIGetBranchList.getBranchIds(branchInfoList);

        ProductInformation productInfo = new ProductInformation();
        String productName = hasModel ? "[%s] %s".formatted(langKey, manageByIMEI ? ("Auto - IMEI - Variation - ") : ("Auto - Normal - Variation - ")) : "[%s] %s".formatted(langKey, manageByIMEI ? ("Auto - IMEI - without variation - ") : ("Auto - Normal - without variation - "));
        productName += OffsetDateTime.now();

        String productDescription = "[%s] product description".formatted(langKey);

        // Set random values for attributes
        long orgPrice = nextLong(MAX_PRICE);
        long newPrice = noDiscount ? orgPrice : nextLong(orgPrice);
        long costPrice = noCost ? 0 : nextLong(newPrice);

        // Ensure pricing rules
        productInfo.setOrgPrice(orgPrice);
        productInfo.setNewPrice(newPrice);
        productInfo.setCostPrice(costPrice);

        // Handle model-related fields
        if (hasModel) {
            List<ProductInformation.Model> models = generateModels(langKey, noDiscount, noCost, branchIds, branchStock);
            productInfo.setModels(models);
        } else {
            productInfo.setModels(new ArrayList<>());
        }

        // Set other fields
        productInfo.setId(nextInt(10000));
        productInfo.setName(productName);
        productInfo.setDescription(productDescription);
        if (hasDimension) productInfo.setShippingInfo(generateShippingInfo());
        productInfo.setHasModel(hasModel);
        productInfo.setShowOutOfStock(showOutOfStock);
        if (hasSEO) {
            productInfo.setSeoTitle("SEO Title");
            productInfo.setSeoDescription("SEO Description");
            productInfo.setSeoKeywords("SEO Keywords");
            productInfo.setSeoUrl("seo-url");
        }
        productInfo.setBarcode(String.valueOf(Instant.now().toEpochMilli()));
        productInfo.setBranches(generateBranchStocks(branchIds, branchStock));
        productInfo.setLanguages(generateMainLanguages(langKey, productName, productDescription, hasSEO));
        if (hasAttribution) productInfo.setItemAttributes(generateItemAttributes(nextInt(10)));
        productInfo.setTaxId(nextInt(100));
        productInfo.setTaxName(vatNames.get(nextInt(2)));
        productInfo.setOnApp(onApp);
        productInfo.setOnWeb(onWeb);
        productInfo.setInStore(inStore);
        productInfo.setInGoSocial(inGoSOCIAL);
        productInfo.setHideStock(isHideStock);
        productInfo.setInventoryManageType(manageByIMEI ? "IMEI_SERIAL_NUMBER" : "Product");
        productInfo.setBhStatus("ACTIVE");
        productInfo.setLotAvailable(hasLot);
        productInfo.setExpiredQuality(nextBoolean());

        return productInfo;
    }


    private static ProductInformation.ShippingInfo generateShippingInfo() {
        ProductInformation.ShippingInfo shippingInfo = new ProductInformation.ShippingInfo();
        shippingInfo.setWeight(nextInt(100));
        shippingInfo.setWidth(nextInt(100));
        shippingInfo.setHeight(nextInt(100));
        shippingInfo.setLength(nextInt(100));
        return shippingInfo;
    }

    private static List<ProductInformation.Model> generateModels(String langKey, boolean noDiscount, boolean noCost, List<Integer> branchIds, int... branchStock) {
        List<ProductInformation.Model> models = new ArrayList<>();

        // Generate a random variation map
        Map<String, List<String>> variationMap = VariationUtils.randomVariationMap(langKey);

        // Get the variation list for model names
        List<String> variationList = VariationUtils.getVariationList(variationMap, langKey);

        // Get variation group name
        String variationGroup = VariationUtils.getVariationName(variationMap, langKey);

        // Create models with variations
        IntStream.range(0, variationList.size()).forEach(variationIndex -> {
            ProductInformation.Model model = new ProductInformation.Model();
            model.setId(nextInt(10000));
            model.setName(variationList.get(variationIndex));
            model.setSku("SKU_" + variationList.get(variationIndex) + "_" + Instant.now().toEpochMilli());
            model.setOrgPrice(nextLong(MAX_PRICE));
            model.setNewPrice(noDiscount ? model.getOrgPrice() : nextLong(model.getOrgPrice()));
            model.setLabel(variationGroup);
            model.setOrgName("");
            model.setDescription("");
            model.setBarcode(String.valueOf(Instant.now().toEpochMilli()));
            model.setVersionName("");
            model.setUseProductDescription(nextBoolean());
            model.setReuseAttributes(true);
            model.setBranches(generateBranchStocks(branchIds, branchStock));
            model.setLanguages(generateVersionLanguages(langKey, variationGroup, variationList.get(variationIndex)));
            model.setModelAttributes(generateItemAttributes(nextInt(10)));
            model.setCostPrice(noCost ? 0 : model.getNewPrice());
            models.add(model);
        });

        return models;
    }

    private static List<ProductInformation.BranchStock> generateBranchStocks(List<Integer> branchIds, int... branchStockQuantity) {
        List<ProductInformation.BranchStock> branchStocks = new ArrayList<>();
        // Set totalItem based on branchStockQuantity if available
        IntStream.range(0, branchIds.size()).forEach(branchIndex -> {
            APIGetProductDetail.ProductInformation.BranchStock branchStock = new APIGetProductDetail.ProductInformation.BranchStock();
            branchStock.setBranchId(branchIds.get(branchIndex));
            branchStock.setTotalItem(branchIndex < branchStockQuantity.length ? branchStockQuantity[branchIndex] : 0);
            branchStock.setSoldItem(0);
            branchStocks.add(branchStock);
        });
        return branchStocks;
    }


    private static List<ProductInformation.MainLanguage> generateMainLanguages(String langKey, String productName, String productDescription, boolean hasSEO) {
        List<ProductInformation.MainLanguage> languages = new ArrayList<>();
        ProductInformation.MainLanguage language = new ProductInformation.MainLanguage();

        // Set basic fields
        language.setLanguage(langKey);
        language.setName(productName);
        language.setDescription(productDescription);

        // Set SEO fields conditionally
        if (hasSEO) {
            language.setSeoTitle("SEO Title" + Instant.now().toEpochMilli());
            language.setSeoDescription("SEO Description" + Instant.now().toEpochMilli());
            language.setSeoKeywords("SEO Keywords" + Instant.now().toEpochMilli());
            language.setSeoUrl(String.valueOf(Instant.now().toEpochMilli()));
        }

        languages.add(language);
        return languages;
    }


    private static List<ProductInformation.ItemAttribute> generateItemAttributes(int numberOf) {
        List<ProductInformation.ItemAttribute> attributes = new ArrayList<>();

        IntStream.rangeClosed(1, numberOf).forEach(attributeIndex -> {
            ProductInformation.ItemAttribute attribute = new ProductInformation.ItemAttribute();
            attribute.setAttributeName("Attribute Name" + attributeIndex);
            attribute.setAttributeValue("Attribute Value" + attributeIndex);
            attribute.setDisplay(nextBoolean());
            attributes.add(attribute);
        });

        return attributes;
    }

    private static List<ProductInformation.Model.VersionLanguage> generateVersionLanguages(String langKey, String modelName, String modelLabel) {

        List<ProductInformation.Model.VersionLanguage> languages = new ArrayList<>();

        ProductInformation.Model.VersionLanguage language = new ProductInformation.Model.VersionLanguage();
        language.setLanguage(langKey);
        language.setLabel(modelLabel);
        language.setVersionName(modelName);

        languages.add(language);

        return languages;
    }


}
