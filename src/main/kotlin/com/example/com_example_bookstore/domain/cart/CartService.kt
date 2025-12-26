package com.example.com_example_bookstore.domain.cart

import com.example.com_example_bookstore.domain.book.BookRepository
import com.example.com_example_bookstore.domain.cart.dto.CartItemResponse
import com.example.com_example_bookstore.domain.cart.dto.CartResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class CartService(
    private val cartRepository: CartRepository,
    private val cartItemRepository: CartItemRepository,
    private val bookRepository: BookRepository
) {

    @Transactional
    fun addItem(userId: Long, bookId: Long, quantity: Int) {
        require(quantity >= 1) { "quantity must be >= 1" }

        // 책 존재 확인 (카트에 없는 책 담기 방지)
        val bookExists = bookRepository.existsById(bookId)
        if (!bookExists) throw IllegalArgumentException("BOOK_NOT_FOUND")

        val cart = getOrCreateCart(userId)
        cart.addOrIncrease(bookId, quantity)
        cartRepository.save(cart)
    }

    @Transactional(readOnly = true)
    fun getCart(userId: Long): CartResponse {
        val cart = cartRepository.findByUserId(userId).orElse(null)
            ?: return CartResponse(items = emptyList(), totalPrice = BigDecimal.ZERO)

        if (cart.items.isEmpty()) return CartResponse(items = emptyList(), totalPrice = BigDecimal.ZERO)

        // 카트에 담긴 bookId들로 책 정보 한번에 조회
        val bookIds = cart.items.map { it.bookId }.distinct()
        val booksById = bookRepository.findAllById(bookIds).associateBy { it.id }

        val items = cart.items.map { ci ->
            val book = booksById[ci.bookId] ?: throw IllegalStateException("BOOK_NOT_FOUND_IN_CART")
            val lineTotal = book.price.multiply(BigDecimal(ci.quantity))
            CartItemResponse(
                cartItemId = ci.id,
                bookId = book.id,
                title = book.title,
                price = book.price,
                quantity = ci.quantity,
                lineTotal = lineTotal
            )
        }

        val total = items.fold(BigDecimal.ZERO) { acc, it -> acc + it.lineTotal }
        return CartResponse(items = items, totalPrice = total)
    }

    @Transactional
    fun updateQuantity(userId: Long, cartItemId: Long, quantity: Int) {
        require(quantity >= 1) { "quantity must be >= 1" }

        val cart = cartRepository.findByUserId(userId).orElseThrow { IllegalArgumentException("CART_NOT_FOUND") }
        val item = cart.items.firstOrNull { it.id == cartItemId }
            ?: throw IllegalArgumentException("CART_ITEM_NOT_FOUND")

        item.changeQuantity(quantity)
        cart.touch()
        cartRepository.save(cart)
    }

    @Transactional
    fun deleteItem(userId: Long, cartItemId: Long) {
        val cart = cartRepository.findByUserId(userId).orElseThrow { IllegalArgumentException("CART_NOT_FOUND") }
        val removed = cart.items.removeIf { it.id == cartItemId }
        if (!removed) throw IllegalArgumentException("CART_ITEM_NOT_FOUND")

        cart.touch()
        cartRepository.save(cart)
        // orphanRemoval=true + cascade=ALL 이라 보통 자동 삭제됨
    }

    @Transactional
    fun clearCart(userId: Long) {
        val cart = cartRepository.findByUserId(userId).orElseThrow { IllegalArgumentException("CART_NOT_FOUND") }
        cart.items.clear()
        cart.touch()
        cartRepository.save(cart)
    }

    private fun getOrCreateCart(userId: Long): Cart {
        return cartRepository.findByUserId(userId).orElseGet {
            // 여기 User 엔티티 참조가 필요함 (Cart에 user: User가 있으니까)
            // 이 부분은 네 프로젝트 User 엔티티 접근 방식에 맞춰서 바꿔야 해.

            // ✅ 방법 1: UserRepository가 있으면 user를 조회해서 넣기 (추천)
            // val user = userRepository.findById(userId).orElseThrow { ... }
            // Cart(user = user)

            // ✅ 방법 2(임시): user를 프록시로 잡기 (EntityManager.getReference)
            throw IllegalStateException("NEED_USER_INJECTION")
        }
    }
}
