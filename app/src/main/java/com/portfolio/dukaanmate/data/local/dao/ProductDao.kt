package com.portfolio.dukaanmate.data.local.dao

import androidx.room.*
import com.portfolio.dukaanmate.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)

    @Delete
    suspend fun deleteProduct(product: ProductEntity)

    @Query("SELECT * FROM products ORDER BY name ASC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE barcode = :barcode LIMIT 1")
    suspend fun getProductByBarcode(barcode: String): ProductEntity?

    @Query("SELECT * FROM products WHERE stockQty <= minStockAlert ORDER BY stockQty ASC")
    fun getLowStockProducts(): Flow<List<ProductEntity>>

    @Query("UPDATE products SET stockQty = stockQty - :soldQty WHERE id = :productId AND stockQty >= :soldQty")
    suspend fun reduceStock(productId: Int, soldQty: Int)
}
