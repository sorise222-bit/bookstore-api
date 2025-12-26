package com.example.com_example_bookstore.domain.order

import com.example.com_example_bookstore.domain.order.dto.AdminChangeStatusRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/admin/orders")
class AdminOrderController(
    private val orderService: OrderService
) {
    @PatchMapping("/{orderId}/status")
    fun changeStatus(
        @PathVariable orderId: Long,
        @RequestBody req: AdminChangeStatusRequest
    ): ResponseEntity<Void> {
        orderService.adminChangeStatus(orderId, req.status)
        return ResponseEntity.ok().build()
    }
}
