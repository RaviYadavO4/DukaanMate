package com.portfolio.dukaanmate.ui.localization

import androidx.compose.runtime.compositionLocalOf

enum class AppLanguage(val code: String, val label: String) {
    ENGLISH("en", "EN"),
    HINDI("hi", "हिंदी")
}

data class AppStrings(
    // Navigation
    val navDashboard: String,
    val navNewBill: String,
    val navStock: String,

    // Dashboard
    val todaySales: String,
    val scanAndBill: String,
    val totalProducts: String,
    val totalBills: String,
    val lowStockAlert: String,
    val itemsRunningLow: String,
    val recentTransactions: String,
    val noBillsYet: String,

    // Barcode Scanner
    val scanGuide: String,
    val cameraPermissionMsg: String,
    val grantPermission: String,

    // Billing Screen
    val posBillingTitle: String,
    val cartItems: String,
    val pickFromList: String,
    val emptyCartGuide: String,
    val productNotFoundToast: String,
    val totalAmount: String,
    val generateBill: String,
    val customerDetails: String,
    val customerName: String,
    val whatsappNumber: String,
    val generatePdfBill: String,
    val selectProductTitle: String,
    val close: String,

    // Inventory Screen
    val stockManagement: String,
    val addNewItem: String,
    val searchHint: String,
    val allItems: String,
    val lowStockFilter: String,
    val noProductsFound: String,
    val inStock: String,
    val lowStockLabel: String,

    // Add Product Dialog
    val addProductTitle: String,
    val barcodeNumber: String,
    val productName: String,
    val sellPrice: String,
    val buyPrice: String,
    val totalStock: String,
    val alertLimit: String,
    val save: String,
    val cancel: String
)

val EnglishStrings = AppStrings(
    navDashboard = "Dashboard",
    navNewBill = "New Bill",
    navStock = "Stock",
    todaySales = "Today's Total Sales",
    scanAndBill = "New Bill (Scan & Bill)",
    totalProducts = "Total Items",
    totalBills = "Total Bills",
    lowStockAlert = "Low Stock Alert",
    itemsRunningLow = "items running low",
    recentTransactions = "Recent Transactions",
    noBillsYet = "No bills generated yet",
    scanGuide = "Place barcode inside the frame",
    cameraPermissionMsg = "Enable camera to scan barcodes",
    grantPermission = "Grant Permission",
    posBillingTitle = "POS Billing",
    cartItems = "Cart Items",
    pickFromList = "+ Pick from List",
    emptyCartGuide = "Scan a barcode or tap '+ Pick from List'",
    productNotFoundToast = "Product not found (Add in Stock first):",
    totalAmount = "Total Amount",
    generateBill = "Generate Bill",
    customerDetails = "Customer Details (Optional)",
    customerName = "Customer Name",
    whatsappNumber = "WhatsApp Number (10 digits)",
    generatePdfBill = "Share PDF Bill",
    selectProductTitle = "Select Product",
    close = "Close",
    stockManagement = "Stock Management",
    addNewItem = "New Item",
    searchHint = "Search by name or barcode...",
    allItems = "All Items",
    lowStockFilter = "Low Stock",
    noProductsFound = "No items found",
    inStock = "Stock",
    lowStockLabel = "Low Stock",
    addProductTitle = "Add New Product",
    barcodeNumber = "Barcode Number *",
    productName = "Product Name *",
    sellPrice = "Sell Price ₹ *",
    buyPrice = "Buy Price ₹",
    totalStock = "Total Stock *",
    alertLimit = "Alert Limit",
    save = "Save",
    cancel = "Cancel"
)

val HindiStrings = AppStrings(
    navDashboard = "डैशबोर्ड",
    navNewBill = "नया बिल",
    navStock = "स्टॉक",
    todaySales = "आज की कुल बिक्री",
    scanAndBill = "नया बिल बनाएं (Scan & Bill)",
    totalProducts = "कुल सामान",
    totalBills = "कुल बिल्स",
    lowStockAlert = "लो-स्टॉक अलर्ट",
    itemsRunningLow = "आइटम्स कम हो रहे हैं",
    recentTransactions = "हाल के ट्रांज़ैक्शन",
    noBillsYet = "अभी कोई बिल नहीं बना है",
    scanGuide = "बारकोड को फ्रेम के बीच में रखें",
    cameraPermissionMsg = "बारकोड स्कैन करने के लिए कैमरा चालू करें",
    grantPermission = "परमिशन दें",
    posBillingTitle = "नया बिल (POS)",
    cartItems = "कार्ट आइटम्स",
    pickFromList = "+ लिस्ट से चुनें",
    emptyCartGuide = "बारकोड स्कैन करें या '+ लिस्ट से चुनें' दबाएं",
    productNotFoundToast = "प्रोडक्ट नहीं मिला (पहले स्टॉक में जोड़ें):",
    totalAmount = "कुल राशि",
    generateBill = "बिल बनाएं",
    customerDetails = "कस्टमर विवरण (वैकल्पिक)",
    customerName = "कस्टमर का नाम",
    whatsappNumber = "WhatsApp नंबर (10 अंक)",
    generatePdfBill = "PDF बिल भेजें",
    selectProductTitle = "सामान चुनें",
    close = "बंद करें",
    stockManagement = "स्टॉक मैनेजमेंट",
    addNewItem = "नया सामान",
    searchHint = "नाम या बारकोड से खोजें...",
    allItems = "सभी सामान",
    lowStockFilter = "कम स्टॉक",
    noProductsFound = "कोई सामान नहीं मिला",
    inStock = "स्टॉक",
    lowStockLabel = "कम स्टॉक",
    addProductTitle = "नया प्रोडक्ट जोड़ें",
    barcodeNumber = "बारकोड नंबर *",
    productName = "सामान का नाम *",
    sellPrice = "बिक्री रेट ₹ *",
    buyPrice = "खरीद रेट ₹",
    totalStock = "कुल स्टॉक *",
    alertLimit = "अलर्ट लिमिट",
    save = "सेव करें",
    cancel = "रद्द करें"
)

val LocalStrings = compositionLocalOf { EnglishStrings }
