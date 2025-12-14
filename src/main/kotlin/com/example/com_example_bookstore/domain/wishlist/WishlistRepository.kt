package com.example.com_example_bookstore.domain.wishlist

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface WishlistRepository : JpaRepository<Wishlist, Long> {
    fun findByUserId(userId: Long, pageable: Pageable): Page<Wishlist>
    fun existsByUserIdAndBookId(userId: Long, bookId: Long): Boolean
    fun deleteByUserIdAndBookId(userId: Long, bookId: Long): Long
    fun deleteByUserId(userId: Long): Long
}
