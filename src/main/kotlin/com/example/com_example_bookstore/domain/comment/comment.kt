package com.example.com_example_bookstore.domain.comment

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "comment",
    indexes = [
        Index(name = "idx_comment_review", columnList = "review_id"),
        Index(name = "idx_comment_user", columnList = "user_id")
    ]
)
class Comment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    var id: Long = 0,

    @Column(name = "review_id", nullable = false)
    var reviewId: Long,

    @Column(name = "user_id", nullable = false)
    var userId: Long,

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
