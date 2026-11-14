package com.portfolio.dukaanmate.data.repository

import com.portfolio.dukaanmate.data.local.entity.InvoiceEntity
import com.portfolio.dukaanmate.data.local.entity.ProductEntity
import com.portfolio.dukaanmate.domain.model.CartItem
import kotlinx.coroutines.flow.Flow

interface DukaanRepository {
    val allProducts: Flow<List<ProductEntity>>
    val lowStockProducts: Flow<List<ProductEntity>>
    val allInvoices: Flow<List<InvoiceEntity>>

    fun getTodaySales(): Flow<Double?>
    suspend fun saveProduct(product: ProductEntity)
    suspend fun deleteProduct(product: ProductEntity)
    suspend fun findProductByBarcode(barcode: String): ProductEntity?
    suspend fun generateBill(
        customerName: String,
        customerPhone: String,
        cartItems: List<CartItem>
    ): InvoiceEntity
}
