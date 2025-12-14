package com.example.com_example_bookstore.domain.review

import org.springframework.data.jpa.repository.JpaRepository

interface ReviewLikeRepository : JpaRepository<ReviewLike, Long> {
    fun existsByReviewIdAndUserId(reviewId: Long, userId: Long): Boolean
    fun deleteByReviewIdAndUserId(reviewId: Long, userId: Long): Long
    fun countByReviewId(reviewId: Long): Long
}
