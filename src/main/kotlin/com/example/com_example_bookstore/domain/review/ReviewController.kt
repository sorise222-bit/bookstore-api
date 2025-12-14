package com.example.com_example_bookstore.domain.review

import com.example.bookstore.common.ApiPaths
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(ApiPaths.REVIEWS) // "/api/v1/reviews"
class ReviewController(
    private val reviewService: ReviewService
) {

    // POST /api/v1/reviews
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody req: ReviewCreateRequest) =
        mapOf("reviewId" to reviewService.create(req))

    // GET /api/v1/books/{bookId}/reviews
    // (book 하위 sub-resource)
    @GetMapping("${ApiPaths.BOOKS}/{bookId}/reviews")
    fun listByBook(
        @PathVariable bookId: Long,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(required = false) sort: String?
    ) = reviewService.listByBook(bookId, page, size, sort)

    // PATCH /api/v1/reviews/{reviewId}
    @PatchMapping("/{reviewId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun update(
        @PathVariable reviewId: Long,
        @Valid @RequestBody req: ReviewUpdateRequest
    ) {
        reviewService.update(reviewId, req)
    }

    // DELETE /api/v1/reviews/{reviewId}
    @DeleteMapping("/{reviewId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable reviewId: Long) {
        reviewService.delete(reviewId)
    }

    // POST /api/v1/reviews/{reviewId}/like
    @PostMapping("/{reviewId}/like")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun like(@PathVariable reviewId: Long) {
        reviewService.like(reviewId)
    }

    // DELETE /api/v1/reviews/{reviewId}/like
    @DeleteMapping("/{reviewId}/like")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun unlike(@PathVariable reviewId: Long) {
        reviewService.unlike(reviewId)
    }
}
