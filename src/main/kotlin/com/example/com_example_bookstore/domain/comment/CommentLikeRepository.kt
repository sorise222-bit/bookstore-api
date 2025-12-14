package com.example.com_example_bookstore.domain.comment

import org.springframework.data.jpa.repository.JpaRepository

interface CommentLikeRepository : JpaRepository<CommentLike, Long> {
    fun existsByCommentIdAndUserId(commentId: Long, userId: Long): Boolean
    fun deleteByCommentIdAndUserId(commentId: Long, userId: Long): Long
}
