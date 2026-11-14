package com.portfolio.dukaanmate.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "products",
    indices = [Index(value = ["barcode"], unique = true)]
)
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val barcode: String,
    val name: String,
    val sellPrice: Double,
    val buyPrice: Double,
    val stockQty: Int,
    val minStockAlert: Int = 5
)
