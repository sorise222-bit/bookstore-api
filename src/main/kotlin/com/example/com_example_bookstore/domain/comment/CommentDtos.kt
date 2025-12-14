package com.example.com_example_bookstore.domain.comment.dto

import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime

data class CommentCreateRequest(
    @field:NotBlank
    val content: String
)

data class CommentUpdateRequest(
    @field:NotBlank
    val content: String
)

data class CommentResponse(
    val commentId: Long,
    val reviewId: Long,
    val userId: Long,
    val content: String,
    val likeCount: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class PageResponse<T>(
    val content: List<T>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
    val sort: String?
)
