package com.example.com_example_bookstore.domain.comment

import com.example.com_example_bookstore.domain.comment.dto.*
import com.example.com_example_bookstore.security.UserIdProvider
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class CommentService(
    private val commentRepository: CommentRepository,
    private val commentLikeRepository: CommentLikeRepository,
    private val userIdProvider: UserIdProvider
) {

    @Transactional
    fun create(reviewId: Long, req: CommentCreateRequest): Long {
        val me = userIdProvider.currentUserId()
        val comment = Comment(
            reviewId = reviewId,
            userId = me,
            content = req.content
        )
        return commentRepository.save(comment).id
    }

    @Transactional(readOnly = true)
    fun listByReview(
        reviewId: Long,
        page: Int,
        size: Int,
        sort: String?
    ): PageResponse<CommentResponse> {
        val pageable = PageRequest.of(
            page.coerceAtLeast(0),
            size.coerceIn(1, 50),
            parseSort(sort)
        )

        val result = commentRepository.findByReviewIdAndDeletedAtIsNull(reviewId, pageable)

        return PageResponse(
            content = result.content.map { it.toResponse() },
            page = result.number,
            size = result.size,
            totalElements = result.totalElements,
            totalPages = result.totalPages,
            sort = sort
        )
    }

    @Transactional
    fun update(commentId: Long, req: CommentUpdateRequest) {
        val me = userIdProvider.currentUserId()
        val comment = commentRepository.findById(commentId).orElseThrow { NoSuchElementException("COMMENT_NOT_FOUND") }
        if (comment.deletedAt != null) throw IllegalStateException("COMMENT_DELETED")
        if (comment.userId != me) throw SecurityException("FORBIDDEN")

        comment.content = req.content
        comment.updatedAt = LocalDateTime.now()
    }

    @Transactional
    fun delete(commentId: Long) {
        val me = userIdProvider.currentUserId()
        val comment = commentRepository.findById(commentId).orElseThrow { NoSuchElementException("COMMENT_NOT_FOUND") }
        if (comment.deletedAt != null) return

        val canDelete = (comment.userId == me) || userIdProvider.isAdmin()
        if (!canDelete) throw SecurityException("FORBIDDEN")

        comment.deletedAt = LocalDateTime.now()
        comment.updatedAt = LocalDateTime.now()
    }

    @Transactional
    fun like(commentId: Long) {
        val me = userIdProvider.currentUserId()
        val comment = commentRepository.findById(commentId).orElseThrow { NoSuchElementException("COMMENT_NOT_FOUND") }
        if (comment.deletedAt != null) throw IllegalStateException("COMMENT_DELETED")

        if (commentLikeRepository.existsByCommentIdAndUserId(commentId, me)) return

        commentLikeRepository.save(CommentLike(commentId = commentId, userId = me))
        comment.likeCount += 1
        comment.updatedAt = LocalDateTime.now()
    }

    @Transactional
    fun unlike(commentId: Long) {
        val me = userIdProvider.currentUserId()
        val comment = commentRepository.findById(commentId).orElseThrow { NoSuchElementException("COMMENT_NOT_FOUND") }
        if (comment.deletedAt != null) throw IllegalStateException("COMMENT_DELETED")

        val deleted = commentLikeRepository.deleteByCommentIdAndUserId(commentId, me)
        if (deleted > 0) {
            comment.likeCount = (comment.likeCount - 1).coerceAtLeast(0)
            comment.updatedAt = LocalDateTime.now()
        }
    }

    private fun parseSort(sort: String?): Sort {
        if (sort.isNullOrBlank()) return Sort.by(Sort.Direction.DESC, "createdAt")
        val parts = sort.split(",")
        val field = parts.getOrNull(0)?.trim() ?: "createdAt"
        val dir = parts.getOrNull(1)?.trim()?.uppercase()
        val direction = if (dir == "ASC") Sort.Direction.ASC else Sort.Direction.DESC

        val allowed = setOf("createdAt", "updatedAt", "likeCount")
        val safeField = if (field in allowed) field else "createdAt"
        return Sort.by(direction, safeField)
    }

    private fun Comment.toResponse(): CommentResponse = CommentResponse(
        commentId = id,
        reviewId = reviewId,
        userId = userId,
        content = content,
        likeCount = likeCount,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
