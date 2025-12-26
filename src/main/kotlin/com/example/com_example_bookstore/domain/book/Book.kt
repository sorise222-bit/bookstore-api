package com.example.com_example_bookstore.domain.book

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "book")
class Book(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    var id: Long = 0,

    @Column(nullable = false, length = 255)
    var title: String,

    @Column(columnDefinition = "TEXT")
    var description: String? = null,

    @Column(nullable = false, precision = 10, scale = 2)
    var price: BigDecimal,

    @Column(nullable = false)
    var stock: Int = 0,

    @Column(columnDefinition = "TEXT", nullable = false)
    var authors: String,

    @Column(columnDefinition = "TEXT", nullable = false)
    var categories: String,

    @Column(name = "published_at")
    var publishedAt: LocalDate? = null,

    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()

) {

    fun decreaseStock(quantity: Int) {
        require(quantity > 0) { "quantity must be positive" }
        if (this.stock < quantity) {
            throw IllegalStateException("OUT_OF_STOCK")
        }
        this.stock -= quantity
    }

    fun increaseStock(quantity: Int) {
        require(quantity > 0) { "quantity must be positive" }
        this.stock += quantity
    }

    /**
     * 관리자 재고 직접 수정용
     */
    fun updateStock(quantity: Int) {
        require(quantity >= 0) { "stock cannot be negative" }
        this.stock = quantity
    }
}