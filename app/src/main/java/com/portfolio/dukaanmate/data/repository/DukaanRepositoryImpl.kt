package com.portfolio.dukaanmate.data.repository

import androidx.room.withTransaction
import com.portfolio.dukaanmate.data.local.DukaanDatabase
import com.portfolio.dukaanmate.data.local.dao.InvoiceDao
import com.portfolio.dukaanmate.data.local.dao.ProductDao
import com.portfolio.dukaanmate.data.local.entity.InvoiceEntity
import com.portfolio.dukaanmate.data.local.entity.InvoiceItemEntity
import com.portfolio.dukaanmate.data.local.entity.ProductEntity
import com.portfolio.dukaanmate.domain.model.CartItem
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class DukaanRepositoryImpl(
    private val database: DukaanDatabase,
    private val productDao: ProductDao,
    private val invoiceDao: InvoiceDao
) : DukaanRepository {

    override val allProducts: Flow<List<ProductEntity>> = productDao.getAllProducts()
    override val lowStockProducts: Flow<List<ProductEntity>> = productDao.getLowStockProducts()
    override val allInvoices: Flow<List<InvoiceEntity>> = invoiceDao.getAllInvoices()

    override fun getTodaySales(): Flow<Double?> {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return invoiceDao.getTodaySales(calendar.timeInMillis)
    }

    override suspend fun saveProduct(product: ProductEntity) = productDao.insertProduct(product)
    override suspend fun deleteProduct(product: ProductEntity) = productDao.deleteProduct(product)
    override suspend fun findProductByBarcode(barcode: String): ProductEntity? =
        productDao.getProductByBarcode(barcode)

    override suspend fun generateBill(
        customerName: String,
        customerPhone: String,
        cartItems: List<CartItem>
    ): InvoiceEntity {
        val total = cartItems.sumOf { it.product.sellPrice * it.quantity }
        val invoiceNumber = "INV-${System.currentTimeMillis().toString().takeLast(6)}"

        val invoice = InvoiceEntity(
            invoiceNumber = invoiceNumber,
            customerName = customerName.ifBlank { "Cash Customer" },
            customerPhone = customerPhone,
            totalAmount = total
        )

        var generatedId = 0L
        database.withTransaction {
            generatedId = invoiceDao.insertInvoice(invoice)
            val items = cartItems.map { cartItem ->
                productDao.reduceStock(cartItem.product.id, cartItem.quantity)
                InvoiceItemEntity(
                    invoiceId = generatedId.toInt(),
                    productName = cartItem.product.name,
                    quantity = cartItem.quantity,
                    unitPrice = cartItem.product.sellPrice
                )
            }
            invoiceDao.insertInvoiceItems(items)
        }
        return invoice.copy(invoiceId = generatedId.toInt())
    }
}
