package com.portfolio.dukaanmate.data.local.dao

import androidx.room.*
import com.portfolio.dukaanmate.data.local.entity.InvoiceEntity
import com.portfolio.dukaanmate.data.local.entity.InvoiceItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InvoiceDao {
    @Insert
    suspend fun insertInvoice(invoice: InvoiceEntity): Long

    @Insert
    suspend fun insertInvoiceItems(items: List<InvoiceItemEntity>)

    @Query("SELECT * FROM invoices ORDER BY createdAt DESC")
    fun getAllInvoices(): Flow<List<InvoiceEntity>>

    @Query("SELECT SUM(totalAmount) FROM invoices WHERE createdAt >= :startOfDay")
    fun getTodaySales(startOfDay: Long): Flow<Double?>
}
