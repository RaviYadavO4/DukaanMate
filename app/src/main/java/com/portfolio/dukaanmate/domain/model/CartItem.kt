package com.portfolio.dukaanmate.domain.model

import com.portfolio.dukaanmate.data.local.entity.ProductEntity

data class CartItem(
    val product: ProductEntity,
    val quantity: Int
)
