package com.example.com_example_bookstore.domain.book

import com.example.com_example_bookstore.common.JsonListCodec
import com.example.com_example_bookstore.domain.book.dto.BookCreateRequest
import com.example.com_example_bookstore.domain.book.dto.BookResponse
import com.example.com_example_bookstore.domain.book.dto.BookUpdateRequest
import com.example.com_example_bookstore.domain.book.dto.PageResponse
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class BookService(
    private val bookRepository: BookRepository
) {
    @Transactional
    fun create(req: BookCreateRequest): Long {
        val book = Book(
            title = req.title,
            description = req.description,
            price = req.price,
            stock = req.stock,
            authors = JsonListCodec.toJson(req.authors),
            categories = JsonListCodec.toJson(req.categories),
            publishedAt = req.publishedAt
        )
        return bookRepository.save(book).id
    }

    @Transactional(readOnly = true)
    fun list(page: Int, size: Int, sort: String?, keyword: String?, category: String?): PageResponse<BookResponse> {
        val pageable = PageRequest.of(
            page.coerceAtLeast(0),
            size.coerceIn(1, 50),
            parseSort(sort)
        )
        val result = bookRepository.search(keyword?.takeIf { it.isNotBlank() }, category?.takeIf { it.isNotBlank() }, pageable)
        return PageResponse(
            content = result.content.map { it.toResponse() },
            page = result.number,
            size = result.size,
            totalElements = result.totalElements,
            totalPages = result.totalPages,
            sort = sort
        )
    }

    @Transactional(readOnly = true)
    fun get(bookId: Long): BookResponse =
        bookRepository.findById(bookId).orElseThrow { NoSuchElementException("BOOK_NOT_FOUND") }.toResponse()

    @Transactional
    fun update(bookId: Long, req: BookUpdateRequest) {
        val book = bookRepository.findById(bookId).orElseThrow { NoSuchElementException("BOOK_NOT_FOUND") }

        req.title?.let { book.title = it }
        if (req.description != null) book.description = req.description
        req.price?.let { book.price = it }
        req.stock?.let { book.stock = it }
        req.authors?.let { book.authors = JsonListCodec.toJson(it) }
        req.categories?.let { book.categories = JsonListCodec.toJson(it) }
        if (req.publishedAt != null) book.publishedAt = req.publishedAt

        book.updatedAt = LocalDateTime.now()
    }

    @Transactional
    fun delete(bookId: Long) {
        if (!bookRepository.existsById(bookId)) throw NoSuchElementException("BOOK_NOT_FOUND")
        bookRepository.deleteById(bookId)
    }

    private fun parseSort(sort: String?): Sort {
        // 형식: field,DESC|ASC
        if (sort.isNullOrBlank()) return Sort.by(Sort.Direction.DESC, "createdAt")
        val parts = sort.split(",")
        val field = parts.getOrNull(0)?.trim().takeUnless { it.isNullOrBlank() } ?: "createdAt"
        val dir = parts.getOrNull(1)?.trim()?.uppercase()
        val direction = if (dir == "ASC") Sort.Direction.ASC else Sort.Direction.DESC

        // 허용 필드만 받는 게 안전 (임의 필드 정렬 방지)
        val allowed = setOf("createdAt", "updatedAt", "price", "stock", "title")
        val safeField = if (field in allowed) field else "createdAt"
        return Sort.by(direction, safeField)
    }

    private fun Book.toResponse(): BookResponse = BookResponse(
        bookId = id,
        title = title,
        description = description,
        price = price,
        stock = stock,
        authors = JsonListCodec.fromJson(authors),
        categories = JsonListCodec.fromJson(categories),
        publishedAt = publishedAt,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
