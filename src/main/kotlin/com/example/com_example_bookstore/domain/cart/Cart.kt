package com.example.com_example_bookstore.domain.cart

import com.example.com_example_bookstore.domain.user.User
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "cart",
    uniqueConstraints = [UniqueConstraint(name = "uk_cart_user", columnNames = ["user_id"])]
)
class Cart(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    var id: Long = 0,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    @OneToMany(mappedBy = "cart", cascade = [CascadeType.ALL], orphanRemoval = true)
    var items: MutableList<CartItem> = mutableListOf()
) {
    fun touch() {
        updatedAt = LocalDateTime.now()
    }

    fun addOrIncrease(bookId: Long, quantity: Int) {
        require(quantity >= 1) { "quantity must be >= 1" }

        val existing = items.firstOrNull { it.bookId == bookId }
        if (existing != null) {
            existing.increase(quantity)
        } else {
            items.add(
                CartItem(
                    cart = this,
                    bookId = bookId,
                    quantity = quantity
                )
            )
        }
        touch()
    }
}
