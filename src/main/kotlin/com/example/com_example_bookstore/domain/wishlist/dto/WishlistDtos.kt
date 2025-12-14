package com.example.com_example_bookstore.domain.wishlist.dto

import java.time.LocalDateTime

data class WishlistItemResponse(
    val wishlistId: Long,
    val bookId: Long,
    val createdAt: LocalDateTime
)

data class WishlistPageResponse(
    val content: List<WishlistItemResponse>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
    val sort: String?
)

data class WishlistExistsResponse(
    val exists: Boolean
)
