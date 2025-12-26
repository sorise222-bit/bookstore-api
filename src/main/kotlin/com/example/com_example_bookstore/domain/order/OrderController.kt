package com.example.com_example_bookstore.domain.order

import com.example.com_example_bookstore.domain.order.dto.AdminChangeStatusRequest
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/orders")
class OrderController(
    private val orderService: OrderService
) {
    private fun currentUserId(): Long {
        val auth = SecurityContextHolder.getContext().authentication
            ?: throw IllegalStateException("UNAUTHORIZED")
        val principal = auth.principal ?: throw IllegalStateException("UNAUTHORIZED")
        return when (principal) {
            is Long -> principal
            is Int -> principal.toLong()
            is String -> principal.toLongOrNull() ?: throw IllegalStateException("UNAUTHORIZED")
            else -> throw IllegalStateException("UNAUTHORIZED")
        }
    }

    @PostMapping("/from-cart")
    fun createFromCart(): ResponseEntity<Map<String, Any>> {
        val orderId = orderService.createFromCart(currentUserId())
        return ResponseEntity.ok(mapOf("orderId" to orderId))
    }

    @GetMapping
    fun myOrders() = orderService.myOrders(currentUserId())

    @GetMapping("/{orderId}")
    fun myOrderDetail(@PathVariable orderId: Long) =
        orderService.myOrderDetail(currentUserId(), orderId)

    @PostMapping("/{orderId}/cancel")
    fun cancel(@PathVariable orderId: Long): ResponseEntity<Void> {
        orderService.cancelMyOrder(currentUserId(), orderId)
        return ResponseEntity.ok().build()
    }
}
