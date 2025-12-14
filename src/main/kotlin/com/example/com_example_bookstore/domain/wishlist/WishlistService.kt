package com.example.com_example_bookstore.domain.wishlist

import com.example.com_example_bookstore.domain.wishlist.dto.*
import com.example.com_example_bookstore.security.UserIdProvider
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class WishlistService(
    private val wishlistRepository: WishlistRepository,
    private val userIdProvider: UserIdProvider
) {

    @Transactional
    fun add(bookId: Long): Long {
        val me = userIdProvider.currentUserId()
        if (wishlistRepository.existsByUserIdAndBookId(me, bookId)) {
            return wishlistRepository.findAll()
                .first { it.userId == me && it.bookId == bookId }.id
        }
        return wishlistRepository.save(Wishlist(userId = me, bookId = bookId)).id
    }

    @Transactional(readOnly = true)
    fun list(page: Int, size: Int, sort: String?): WishlistPageResponse {
        val pageable = PageRequest.of(
            page.coerceAtLeast(0),
            size.coerceIn(1, 50),
            parseSort(sort)
        )
        val me = userIdProvider.currentUserId()
        val result = wishlistRepository.findByUserId(me, pageable)
        return WishlistPageResponse(
            content = result.content.map { WishlistItemResponse(it.id, it.bookId, it.createdAt) },
            page = result.number,
            size = result.size,
            totalElements = result.totalElements,
            totalPages = result.totalPages,
            sort = sort
        )
    }

    @Transactional
    fun remove(bookId: Long) {
        val me = userIdProvider.currentUserId()
        wishlistRepository.deleteByUserIdAndBookId(me, bookId)
    }

    @Transactional(readOnly = true)
    fun exists(bookId: Long): WishlistExistsResponse {
        val me = userIdProvider.currentUserId()
        return WishlistExistsResponse(wishlistRepository.existsByUserIdAndBookId(me, bookId))
    }

    @Transactional
    fun clear() {
        val me = userIdProvider.currentUserId()
        wishlistRepository.deleteByUserId(me)
    }

    private fun parseSort(sort: String?): Sort {
        if (sort.isNullOrBlank()) return Sort.by(Sort.Direction.DESC, "createdAt")
        val p = sort.split(",")
        val field = p.getOrNull(0) ?: "createdAt"
        val dir = p.getOrNull(1)?.uppercase()
        val direction = if (dir == "ASC") Sort.Direction.ASC else Sort.Direction.DESC
        val allowed = setOf("createdAt")
        return Sort.by(direction, if (field in allowed) field else "createdAt")
    }
}
