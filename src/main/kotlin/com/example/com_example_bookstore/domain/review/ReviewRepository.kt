package com.example.com_example_bookstore.domain.review

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface ReviewRepository : JpaRepository<Review, Long> {
    fun findByBookIdAndDeletedAtIsNull(bookId: Long, pageable: Pageable): Page<Review>
}
