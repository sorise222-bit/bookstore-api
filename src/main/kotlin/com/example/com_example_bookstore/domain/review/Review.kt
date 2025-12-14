package com.example.com_example_bookstore.domain.review

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "review",
    indexes = [
        Index(name = "idx_review_book", columnList = "book_id"),
        Index(name = "idx_review_user", columnList = "user_id")
    ]
)
class Review(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    var id: Long = 0,

    @Column(name = "user_id", nullable = false)
    var userId: Long,

    @Column(name = "book_id", nullable = false)
    var bookId: Long,

    @Column(nullable = false)
    var rating: Int,

    @Column(columnDefinition = "TEXT", nullable = false)
    var content: String,

    @Column(name = "like_count", nullable = false)
    var likeCount: Int = 0,

    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "deleted_at")
    var deletedAt: LocalDateTime? = null
)
