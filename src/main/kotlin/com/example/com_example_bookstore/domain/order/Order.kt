package com.example.com_example_bookstore.domain.order

import com.example.com_example_bookstore.domain.user.User
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "orders")
class Order(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    var id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    var status: OrderStatus = OrderStatus.CREATED,

    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    var totalPrice: BigDecimal = BigDecimal.ZERO,

    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "canceled_at")
    var canceledAt: LocalDateTime? = null,

    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL], orphanRemoval = true)
    var items: MutableList<OrderItem> = mutableListOf()
) {
    fun touch() {
        updatedAt = LocalDateTime.now()
    }

    fun cancel() {
        if (status != OrderStatus.CREATED && status != OrderStatus.PAID) {
            throw IllegalStateException("ORDER_CANNOT_CANCEL")
        }
        status = OrderStatus.CANCELED
        canceledAt = LocalDateTime.now()
        touch()
    }

    fun changeStatus(newStatus: OrderStatus) {
        if (status == OrderStatus.CANCELED) throw IllegalStateException("ORDER_ALREADY_CANCELED")
        status = newStatus
        touch()
    }
}
