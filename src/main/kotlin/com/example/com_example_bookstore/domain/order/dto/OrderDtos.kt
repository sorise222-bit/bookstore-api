package com.example.com_example_bookstore.domain.order.dto

import com.example.com_example_bookstore.domain.order.OrderStatus
import java.math.BigDecimal
import java.time.LocalDateTime

data class OrderItemResponse(
    val bookId: Long,
    val title: String,
    val price: BigDecimal,
    val quantity: Int,
    val lineTotal: BigDecimal
)

data class OrderResponse(
    val orderId: Long,
    val status: OrderStatus,
    val totalPrice: BigDecimal,
    val createdAt: LocalDateTime,
    val canceledAt: LocalDateTime?,
    val items: List<OrderItemResponse>
)

data class AdminChangeStatusRequest(
    val status: OrderStatus
)
