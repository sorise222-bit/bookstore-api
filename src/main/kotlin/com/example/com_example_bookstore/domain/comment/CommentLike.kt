package com.example.com_example_bookstore.domain.comment

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "comment_like",
    uniqueConstraints = [UniqueConstraint(name = "uq_comment_like", columnNames = ["comment_id", "user_id"])],
    indexes = [
        Index(name = "idx_comment_like_comment", columnList = "comment_id"),
        Index(name = "idx_comment_like_user", columnList = "user_id")
    ]
)
class CommentLike(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_like_id")
    var id: Long = 0,

    @Column(name = "comment_id", nullable = false)
    var commentId: Long,

    @Column(name = "user_id", nullable = false)
    var userId: Long,

    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
)
