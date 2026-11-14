package com.portfolio.dukaanmate.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "invoices")
data class InvoiceEntity(
    @PrimaryKey(autoGenerate = true) val invoiceId: Int = 0,
    val invoiceNumber: String,
    val customerName: String = "Cash Customer",
    val customerPhone: String = "",
    val totalAmount: Double,
    val createdAt: Long = System.currentTimeMillis()
)
