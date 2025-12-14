package com.example.com_example_bookstore.domain.review

import com.example.com_example_bookstore.security.UserIdProvider
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class ReviewService(
    private val reviewRepository: ReviewRepository,
    private val reviewLikeRepository: ReviewLikeRepository,
    private val userIdProvider: UserIdProvider
) {
    @Transactional
    fun create(req: ReviewCreateRequest): Long {
        val userId = userIdProvider.currentUserId()
        val review = Review(
            userId = userId,
            bookId = req.bookId,
            rating = req.rating,
            content = req.content
        )
        return reviewRepository.save(review).id
    }

    @Transactional(readOnly = true)
    fun listByBook(bookId: Long, page: Int, size: Int, sort: String?): org.springframework.data.domain.Page<ReviewResponse> {
        val pageable = PageRequest.of(
            page.coerceAtLeast(0),
            size.coerceIn(1, 50),
            parseSort(sort)
        )
        return reviewRepository.findByBookIdAndDeletedAtIsNull(bookId, pageable).map { it.toResponse() }
    }

    @Transactional
    fun update(reviewId: Long, req: ReviewUpdateRequest) {
        val me = userIdProvider.currentUserId()
        val review = reviewRepository.findById(reviewId).orElseThrow { NoSuchElementException("REVIEW_NOT_FOUND") }
        if (review.deletedAt != null) throw IllegalStateException("REVIEW_DELETED")

        if (review.userId != me) throw SecurityException("FORBIDDEN")

        req.rating?.let { review.rating = it }
        req.content?.let { review.content = it }
        review.updatedAt = LocalDateTime.now()
    }

    @Transactional
    fun delete(reviewId: Long) {
        val me = userIdProvider.currentUserId()
        val review = reviewRepository.findById(reviewId).orElseThrow { NoSuchElementException("REVIEW_NOT_FOUND") }
        if (review.deletedAt != null) return

        val canDelete = (review.userId == me) || userIdProvider.isAdmin()
        if (!canDelete) throw SecurityException("FORBIDDEN")

        review.deletedAt = LocalDateTime.now()
        review.updatedAt = LocalDateTime.now()
    }

    @Transactional
    fun like(reviewId: Long) {
        val me = userIdProvider.currentUserId()
        val review = reviewRepository.findById(reviewId).orElseThrow { NoSuchElementException("REVIEW_NOT_FOUND") }
        if (review.deletedAt != null) throw IllegalStateException("REVIEW_DELETED")

        if (reviewLikeRepository.existsByReviewIdAndUserId(reviewId, me)) return

        reviewLikeRepository.save(ReviewLike(reviewId = reviewId, userId = me))
        review.likeCount += 1
        review.updatedAt = LocalDateTime.now()
    }

    @Transactional
    fun unlike(reviewId: Long) {
        val me = userIdProvider.currentUserId()
        val review = reviewRepository.findById(reviewId).orElseThrow { NoSuchElementException("REVIEW_NOT_FOUND") }
        if (review.deletedAt != null) throw IllegalStateException("REVIEW_DELETED")

        val deleted = reviewLikeRepository.deleteByReviewIdAndUserId(reviewId, me)
        if (deleted > 0) {
            review.likeCount = (review.likeCount - 1).coerceAtLeast(0)
            review.updatedAt = LocalDateTime.now()
        }
    }

    private fun parseSort(sort: String?): Sort {
        if (sort.isNullOrBlank()) return Sort.by(Sort.Direction.DESC, "createdAt")
        val parts = sort.split(",")
        val field = parts.getOrNull(0)?.trim() ?: "createdAt"
        val dir = parts.getOrNull(1)?.trim()?.uppercase()
        val direction = if (dir == "ASC") Sort.Direction.ASC else Sort.Direction.DESC

        val allowed = setOf("createdAt", "updatedAt", "likeCount", "rating")
        val safeField = if (field in allowed) field else "createdAt"
        return Sort.by(direction, safeField)
    }

    private fun Review.toResponse() = ReviewResponse(
        reviewId = id,
        userId = userId,
        bookId = bookId,
        rating = rating,
        content = content,
        likeCount = likeCount,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
