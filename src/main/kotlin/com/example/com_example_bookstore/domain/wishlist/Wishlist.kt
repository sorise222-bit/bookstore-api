package com.example.com_example_bookstore.domain.wishlist

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "wishlist",
    uniqueConstraints = [UniqueConstraint(name = "uq_wishlist_user_book", columnNames = ["user_id", "book_id"])],
    indexes = [
        Index(name = "idx_wishlist_user", columnList = "user_id"),
        Index(name = "idx_wishlist_book", columnList = "book_id")
    ]
)
class Wishlist(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wishlist_id")
    var id: Long = 0,

    @Column(name = "user_id", nullable = false)
    var userId: Long,

    @Column(name = "book_id", nullable = false)
    var bookId: Long,

    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
)
