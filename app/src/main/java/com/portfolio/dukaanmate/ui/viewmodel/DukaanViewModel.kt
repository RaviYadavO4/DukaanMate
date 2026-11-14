package com.portfolio.dukaanmate.ui.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.portfolio.dukaanmate.data.local.LanguagePreferences
import com.portfolio.dukaanmate.data.local.entity.InvoiceEntity
import com.portfolio.dukaanmate.data.local.entity.ProductEntity
import com.portfolio.dukaanmate.data.repository.DukaanRepository
import com.portfolio.dukaanmate.domain.model.CartItem
import com.portfolio.dukaanmate.ui.localization.AppLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DukaanViewModel(
    private val repository: DukaanRepository,
    private val languagePreferences: LanguagePreferences
) : ViewModel() {

    val allProducts = repository.allProducts.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val lowStockProducts = repository.lowStockProducts.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val allInvoices = repository.allInvoices.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val todaySales = repository.getTodaySales().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0
    )

    val cartItems = mutableStateListOf<CartItem>()

    private val _language = MutableStateFlow(languagePreferences.getSavedLanguage())
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(languagePreferences.isDarkTheme())
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    fun toggleLanguage() {
        val newLanguage = if (_language.value == AppLanguage.ENGLISH) {
            AppLanguage.HINDI
        } else {
            AppLanguage.ENGLISH
        }
        _language.value = newLanguage
        languagePreferences.saveLanguage(newLanguage) // Save permanently in device
    }

    fun toggleTheme() {
        val newTheme = !_isDarkTheme.value
        _isDarkTheme.value = newTheme
        languagePreferences.saveDarkTheme(newTheme)
    }

    fun addProduct(
        barcode: String, name: String, sellPrice: Double,
        buyPrice: Double, stockQty: Int, minStock: Int
    ) {
        viewModelScope.launch {
            repository.saveProduct(
                ProductEntity(
                    barcode = barcode.trim(),
                    name = name.trim(),
                    sellPrice = sellPrice,
                    buyPrice = buyPrice,
                    stockQty = stockQty,
                    minStockAlert = minStock
                )
            )
        }
    }

    fun deleteProduct(product: ProductEntity) {
        viewModelScope.launch { repository.deleteProduct(product) }
    }

    fun scanBarcodeForBilling(barcode: String, onNotFound: (String) -> Unit) {
        viewModelScope.launch {
            val product = repository.findProductByBarcode(barcode)
            if (product != null) addToCart(product) else onNotFound(barcode)
        }
    }

    fun addToCart(product: ProductEntity) {
        val index = cartItems.indexOfFirst { it.product.id == product.id }
        if (index != -1) {
            val current = cartItems[index]
            if (current.quantity < product.stockQty) {
                cartItems[index] = current.copy(quantity = current.quantity + 1)
            }
        } else if (product.stockQty > 0) {
            cartItems.add(CartItem(product, 1))
        }
    }

    fun updateCartQuantity(productId: Int, delta: Int) {
        val index = cartItems.indexOfFirst { it.product.id == productId }
        if (index != -1) {
            val current = cartItems[index]
            val newQty = current.quantity + delta
            if (newQty <= 0) cartItems.removeAt(index)
            else if (newQty <= current.product.stockQty) {
                cartItems[index] = current.copy(quantity = newQty)
            }
        }
    }

    fun getCartTotal(): Double = cartItems.sumOf { it.product.sellPrice * it.quantity }

    fun checkoutBill(
        customerName: String,
        customerPhone: String,
        onBillCreated: (InvoiceEntity, List<CartItem>) -> Unit
    ) {
        if (cartItems.isEmpty()) return
        val snapshot = cartItems.toList()
        viewModelScope.launch {
            val invoice = repository.generateBill(customerName, customerPhone, snapshot)
            cartItems.clear()
            onBillCreated(invoice, snapshot)
        }
    }
}
