package com.portfolio.dukaanmate.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.portfolio.dukaanmate.data.local.dao.InvoiceDao
import com.portfolio.dukaanmate.data.local.dao.ProductDao
import com.portfolio.dukaanmate.data.local.entity.InvoiceEntity
import com.portfolio.dukaanmate.data.local.entity.InvoiceItemEntity
import com.portfolio.dukaanmate.data.local.entity.ProductEntity

@Database(
    entities = [ProductEntity::class, InvoiceEntity::class, InvoiceItemEntity::class],
    version = 1,
    exportSchema = false
)
abstract class DukaanDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun invoiceDao(): InvoiceDao
}
