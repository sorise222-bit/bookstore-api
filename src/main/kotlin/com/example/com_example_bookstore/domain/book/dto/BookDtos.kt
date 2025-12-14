package com.example.com_example_bookstore.domain.book.dto


import jakarta.validation.constraints.*
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

data class BookCreateRequest(
    @field:NotBlank @field:Size(max = 255)
    val title: String,

    val description: String? = null,

    @field:NotNull @field:DecimalMin("0.0", inclusive = false)
    val price: BigDecimal,

    @field:NotNull @field:Min(0)
    val stock: Int,

    @field:Size(min = 1)
    val authors: List<@NotBlank String>,

    @field:Size(min = 1)
    val categories: List<@NotBlank String>,

    val publishedAt: LocalDate? = null
)

data class BookUpdateRequest(
    @field:Size(max = 255)
    val title: String? = null,

    val description: String? = null,

    @field:DecimalMin("0.0", inclusive = false)
    val price: BigDecimal? = null,

    @field:Min(0)
    val stock: Int? = null,

    val authors: List<@NotBlank String>? = null,
    val categories: List<@NotBlank String>? = null,
    val publishedAt: LocalDate? = null
)

data class BookResponse(
    val bookId: Long,
    val title: String,
    val description: String?,
    val price: BigDecimal,
    val stock: Int,
    val authors: List<String>,
    val categories: List<String>,
    val publishedAt: LocalDate?,
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
