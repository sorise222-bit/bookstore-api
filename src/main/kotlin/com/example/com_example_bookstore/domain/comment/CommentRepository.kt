package com.example.com_example_bookstore.domain.comment

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface CommentRepository : JpaRepository<Comment, Long> {
    fun findByReviewIdAndDeletedAtIsNull(reviewId: Long, pageable: Pageable): Page<Comment>
}
