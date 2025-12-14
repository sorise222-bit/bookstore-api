package com.example.com_example_bookstore.domain.review

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

data class ReviewCreateRequest(
    @field:NotNull val bookId: Long,
    @field:Min(1) @field:Max(5) val rating: Int,
    @field:NotBlank val content: String
)

data class ReviewUpdateRequest(
    @field:Min(1) @field:Max(5) val rating: Int? = null,
    val content: String? = null
)

data class ReviewResponse(
    val reviewId: Long,
    val userId: Long,
    val bookId: Long,
    val rating: Int,
    val content: String,
    val likeCount: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
