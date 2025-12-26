package com.example.com_example_bookstore.domain.order

import com.example.com_example_bookstore.domain.book.BookRepository
import com.example.com_example_bookstore.domain.cart.CartRepository
import com.example.com_example_bookstore.domain.order.dto.OrderItemResponse
import com.example.com_example_bookstore.domain.order.dto.OrderResponse
import com.example.com_example_bookstore.domain.user.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class OrderService(
    private val cartRepository: CartRepository,
    private val userRepository: UserRepository,
    private val bookRepository: BookRepository,
    private val orderRepository: OrderRepository
) {

    @Transactional
    fun createFromCart(userId: Long): Long {
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("USER_NOT_FOUND") }

        val cart = cartRepository.findByUserId(userId).orElseThrow { IllegalArgumentException("CART_NOT_FOUND") }
        if (cart.items.isEmpty()) throw IllegalArgumentException("CART_EMPTY")

        val bookIds = cart.items.map { it.bookId }.distinct()
        val books = bookRepository.findAllByIdForUpdate(bookIds)
        val bookById = books.associateBy { it.id }

        // 재고 체크
        cart.items.forEach { ci ->
            val book = bookById[ci.bookId] ?: throw IllegalArgumentException("BOOK_NOT_FOUND")
            if (book.stock < ci.quantity) throw IllegalStateException("OUT_OF_STOCK")
        }

        val order = Order(user = user, status = OrderStatus.CREATED)

        var total = BigDecimal.ZERO
        cart.items.forEach { ci ->
            val book = bookById[ci.bookId]!! // 위에서 체크
            // 재고 차감 (Book 엔티티 메서드 사용)
            book.decreaseStock(ci.quantity)

            val lineTotal = book.price.multiply(BigDecimal(ci.quantity))
            total += lineTotal

            order.items.add(
                OrderItem(
                    order = order,
                    bookId = book.id,
                    titleSnapshot = book.title,
                    priceSnapshot = book.price,
                    quantity = ci.quantity,
                    lineTotal = lineTotal
                )
            )
        }

        order.totalPrice = total
        val saved = orderRepository.save(order)

        // 카트 비우기
        cart.items.clear()
        cart.touch()
        cartRepository.save(cart)

        return saved.id
    }

    @Transactional(readOnly = true)
    fun myOrders(userId: Long): List<OrderResponse> {
        return orderRepository.findAllByUserIdOrderByCreatedAtDesc(userId).map { it.toResponse() }
    }

    @Transactional(readOnly = true)
    fun myOrderDetail(userId: Long, orderId: Long): OrderResponse {
        val order = orderRepository.findById(orderId).orElseThrow { IllegalArgumentException("ORDER_NOT_FOUND") }
        if (order.user.id != userId) throw IllegalStateException("FORBIDDEN")
        return order.toResponse()
    }

    @Transactional
    fun cancelMyOrder(userId: Long, orderId: Long) {
        val order = orderRepository.findById(orderId).orElseThrow { IllegalArgumentException("ORDER_NOT_FOUND") }
        if (order.user.id != userId) throw IllegalStateException("FORBIDDEN")

        // 취소 가능 상태 체크 & 상태 변경
        order.cancel()

        // 재고 복구: 주문 아이템들의 bookId를 락으로 가져와서 복구
        val ids = order.items.map { it.bookId }.distinct()
        val books = bookRepository.findAllByIdForUpdate(ids).associateBy { it.id }

        order.items.forEach { oi ->
            val book = books[oi.bookId] ?: throw IllegalArgumentException("BOOK_NOT_FOUND")
            book.increaseStock(oi.quantity)
        }

        orderRepository.save(order)
    }

    @Transactional
    fun adminChangeStatus(orderId: Long, newStatus: OrderStatus) {
        val order = orderRepository.findById(orderId).orElseThrow { IllegalArgumentException("ORDER_NOT_FOUND") }
        order.changeStatus(newStatus)
        orderRepository.save(order)
    }

    private fun Order.toResponse(): OrderResponse {
        return OrderResponse(
            orderId = this.id,
            status = this.status,
            totalPrice = this.totalPrice,
            createdAt = this.createdAt,
            canceledAt = this.canceledAt,
            items = this.items.map {
                OrderItemResponse(
                    bookId = it.bookId,
                    title = it.titleSnapshot,
                    price = it.priceSnapshot,
                    quantity = it.quantity,
                    lineTotal = it.lineTotal
                )
            }
        )
    }
}
