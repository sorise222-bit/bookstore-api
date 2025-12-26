package com.example.com_example_bookstore.domain.cart

import com.example.com_example_bookstore.domain.cart.dto.AddCartItemRequest
import com.example.com_example_bookstore.domain.cart.dto.UpdateCartItemQuantityRequest
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/cart")
class CartController(
    private val cartService: CartService
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

    @PostMapping("/items")
    fun addItem(@RequestBody req: AddCartItemRequest): ResponseEntity<Void> {
        cartService.addItem(currentUserId(), req.bookId, req.quantity)
        return ResponseEntity.ok().build()
    }

    @GetMapping
    fun getCart() = cartService.getCart(currentUserId())

    @PatchMapping("/items/{cartItemId}")
    fun updateQuantity(
        @PathVariable cartItemId: Long,
        @RequestBody req: UpdateCartItemQuantityRequest
    ): ResponseEntity<Void> {
        cartService.updateQuantity(currentUserId(), cartItemId, req.quantity)
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/items/{cartItemId}")
    fun deleteItem(@PathVariable cartItemId: Long): ResponseEntity<Void> {
        cartService.deleteItem(currentUserId(), cartItemId)
        return ResponseEntity.ok().build()
    }
}
