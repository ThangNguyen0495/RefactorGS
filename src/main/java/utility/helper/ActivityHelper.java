package utility.helper;

import static utility.WebDriverManager.appBundleId;

public class ActivityHelper {
    // Seller
    public final static String sellerLoginActivity = "com.mediastep.gosellseller.modules.credentials.login.LoginActivity";
    public final static String sellerHomeActivity = "com.mediastep.gosellseller.modules.tabs.main.MainActivity";
    public final static String sellerCreateProductActivity = "com.mediastep.gosellseller.modules.upload_product.CreateProductActivity";
    public final static String sellerProductMgmtActivity = "com.mediastep.gosellseller.modules.product_management.ProductManagementActivity";
    public final static String sellerProductDetailActivity = "com.mediastep.gosellseller.modules.upload_product.CreateProductActivity";
    public final static String sellerProductBranchInventoryActivity = "com.mediastep.gosellseller.modules.upload_product.inventory";
    public final static String sellerProductInventoryActivity = "com.mediastep.gosellseller.modules.upload_product.inventory.InventoryActivity";
    public final static String sellerCreateSupplierActivity = "com.mediastep.gosellseller.modules.supplier.create_supplier.CreateSupplierActivity";
    public final static String sellerSupplierMgmtActivity = "com.mediastep.gosellseller.modules.supplier.supplier_management.SupplierManagementActivity";

    // Buyer
    public final static String buyerSplashActivity = "%s.ui.modules.splash.SplashScreenActivity".formatted(appBundleId);
    public final static String buyerHomeActivity = "com.mediastep.gosell.ui.MainActivity";
}
