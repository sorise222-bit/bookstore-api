package com.example.com_example_bookstore.domain.book


import com.example.bookstore.common.ApiPaths
import com.example.com_example_bookstore.domain.book.dto.BookCreateRequest
import com.example.com_example_bookstore.domain.book.dto.BookResponse
import com.example.com_example_bookstore.domain.book.dto.BookUpdateRequest
import com.example.com_example_bookstore.domain.book.dto.PageResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(ApiPaths.BOOKS)
class BookController(
    private val bookService: BookService
) {
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody req: BookCreateRequest): Map<String, Any> =
        mapOf("bookId" to bookService.create(req))

    @GetMapping
    fun list(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(required = false) sort: String?,
        @RequestParam(required = false) keyword: String?,
        @RequestParam(required = false) category: String?
    ): PageResponse<BookResponse> =
        bookService.list(page, size, sort, keyword, category)

    @GetMapping("/{bookId}")
    fun get(@PathVariable bookId: Long): BookResponse =
        bookService.get(bookId)

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{bookId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun update(@PathVariable bookId: Long, @Valid @RequestBody req: BookUpdateRequest) {
        bookService.update(bookId, req)
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{bookId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable bookId: Long) {
        bookService.delete(bookId)
    }
}
