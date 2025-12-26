package com.example.com_example_bookstore.domain.order

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "order_item")
class OrderItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    var id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    var order: Order,

    @Column(name = "book_id", nullable = false)
    var bookId: Long,

    @Column(name = "title_snapshot", nullable = false, length = 255)
    var titleSnapshot: String,

    @Column(name = "price_snapshot", nullable = false, precision = 10, scale = 2)
    var priceSnapshot: BigDecimal,

    @Column(nullable = false)
    var quantity: Int,

    @Column(name = "line_total", nullable = false, precision = 10, scale = 2)
    var lineTotal: BigDecimal
)
