package com.example.com_example_bookstore.common

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleBadRequest(e: IllegalArgumentException): ResponseEntity<ApiError> {
        // 메시지로 코드 전달하는 방식(지금 네 코드 스타일에 맞춤)
        val code = e.message ?: "BAD_REQUEST"
        val status = when (code) {
            "BOOK_NOT_FOUND", "ORDER_NOT_FOUND", "CART_NOT_FOUND", "CART_ITEM_NOT_FOUND", "USER_NOT_FOUND" -> HttpStatus.NOT_FOUND
            "CART_EMPTY" -> HttpStatus.BAD_REQUEST
            else -> HttpStatus.BAD_REQUEST
        }
        return ResponseEntity.status(status).body(ApiError(code, code))
    }

    @ExceptionHandler(IllegalStateException::class)
    fun handleConflictOrForbidden(e: IllegalStateException): ResponseEntity<ApiError> {
        val code = e.message ?: "ILLEGAL_STATE"
        val status = when (code) {
            "OUT_OF_STOCK" -> HttpStatus.CONFLICT
            "ORDER_CANNOT_CANCEL", "ORDER_ALREADY_CANCELED" -> HttpStatus.CONFLICT
            "FORBIDDEN" -> HttpStatus.FORBIDDEN
            "UNAUTHORIZED" -> HttpStatus.UNAUTHORIZED
            else -> HttpStatus.CONFLICT
        }
        return ResponseEntity.status(status).body(ApiError(code, code))
    }
}
