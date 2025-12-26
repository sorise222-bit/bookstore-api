package com.example.com_example_bookstore.domain.book

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BookAdminService(
    private val bookRepository: BookRepository
) {
    @Transactional
    fun updateStock(bookId: Long, stock: Int) {
        require(stock >= 0) { "stock cannot be negative" }
        val book = bookRepository.findById(bookId).orElseThrow { IllegalArgumentException("BOOK_NOT_FOUND") }
        book.updateStock(stock)   // 네 Book 엔티티에 있는 메서드
        bookRepository.save(book)
    }
}
