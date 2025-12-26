package com.example.com_example_bookstore.domain.cart

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "cart_item",
    uniqueConstraints = [UniqueConstraint(name = "uk_cart_item_cart_book", columnNames = ["cart_id", "book_id"])]
)
class CartItem(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_item_id")
    var id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    var cart: Cart,

    @Column(name = "book_id", nullable = false)
    var bookId: Long,

    @Column(nullable = false)
    var quantity: Int,

    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    fun changeQuantity(newQty: Int) {
        require(newQty >= 1) { "quantity must be >= 1" }
        quantity = newQty
        updatedAt = LocalDateTime.now()
    }

    fun increase(delta: Int) {
        require(delta >= 1) { "delta must be >= 1" }
        quantity += delta
        updatedAt = LocalDateTime.now()
    }
}
