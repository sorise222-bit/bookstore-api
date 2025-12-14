package com.example.com_example_bookstore.domain.review

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "review_like",
    uniqueConstraints = [UniqueConstraint(name = "uq_review_like", columnNames = ["review_id", "user_id"])],
    indexes = [
        Index(name = "idx_review_like_review", columnList = "review_id"),
        Index(name = "idx_review_like_user", columnList = "user_id")
    ]
)
class ReviewLike(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_like_id")
    var id: Long = 0,

    @Column(name = "review_id", nullable = false)
    var reviewId: Long,

    @Column(name = "user_id", nullable = false)
    var userId: Long,

    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
)
