package com.example.com_example_bookstore.domain.cart.dto

import java.math.BigDecimal

data class AddCartItemRequest(
    val bookId: Long,
    val quantity: Int
)

data class UpdateCartItemQuantityRequest(
    val quantity: Int
)

data class CartItemResponse(
    val cartItemId: Long,
    val bookId: Long,
    val title: String,
    val price: BigDecimal,
    val quantity: Int,
    val lineTotal: BigDecimal
)

data class CartResponse(
    val items: List<CartItemResponse>,
    val totalPrice: BigDecimal
)
