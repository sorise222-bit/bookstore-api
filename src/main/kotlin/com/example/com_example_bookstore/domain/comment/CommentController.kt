package com.example.com_example_bookstore.domain.comment

import com.example.bookstore.common.ApiPaths
import com.example.com_example_bookstore.domain.comment.dto.CommentCreateRequest
import com.example.com_example_bookstore.domain.comment.dto.CommentUpdateRequest
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
class CommentController(
    private val commentService: CommentService
) {

    // POST /api/v1/reviews/{reviewId}/comments
    @PostMapping(ApiPaths.REVIEW_COMMENTS)
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @PathVariable reviewId: Long,
        @Valid @RequestBody req: CommentCreateRequest
    ) = mapOf("commentId" to commentService.create(reviewId, req))

    // GET /api/v1/reviews/{reviewId}/comments
    @GetMapping(ApiPaths.REVIEW_COMMENTS)
    fun listByReview(
        @PathVariable reviewId: Long,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(required = false) sort: String?
    ) = commentService.listByReview(reviewId, page, size, sort)

    // PATCH /api/v1/comments/{commentId}
    @PatchMapping("${ApiPaths.COMMENTS}/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun update(
        @PathVariable commentId: Long,
        @Valid @RequestBody req: CommentUpdateRequest
    ) {
        commentService.update(commentId, req)
    }

    // DELETE /api/v1/comments/{commentId}
    @DeleteMapping("${ApiPaths.COMMENTS}/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable commentId: Long) {
        commentService.delete(commentId)
    }

    // POST /api/v1/comments/{commentId}/like
    @PostMapping("${ApiPaths.COMMENTS}/{commentId}/like")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun like(@PathVariable commentId: Long) {
        commentService.like(commentId)
    }

    // DELETE /api/v1/comments/{commentId}/like
    @DeleteMapping("${ApiPaths.COMMENTS}/{commentId}/like")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun unlike(@PathVariable commentId: Long) {
        commentService.unlike(commentId)
    }
}
