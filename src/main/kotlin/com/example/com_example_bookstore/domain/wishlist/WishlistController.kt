package com.example.com_example_bookstore.domain.wishlist

import com.example.bookstore.common.ApiPaths
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(ApiPaths.WISHLIST) // "/api/v1/wishlist"
class WishlistController(
    private val wishlistService: WishlistService
) {

    // POST /api/v1/wishlist/{bookId}
    @PostMapping("/{bookId}")
    @ResponseStatus(HttpStatus.CREATED)
    fun add(@PathVariable bookId: Long) =
        mapOf("wishlistId" to wishlistService.add(bookId))

    // GET /api/v1/wishlist
    @GetMapping
    fun list(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(required = false) sort: String?
    ) = wishlistService.list(page, size, sort)

    // DELETE /api/v1/wishlist/{bookId}
    @DeleteMapping("/{bookId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun remove(@PathVariable bookId: Long) {
        wishlistService.remove(bookId)
    }

    // GET /api/v1/wishlist/exists/{bookId}
    @GetMapping("/exists/{bookId}")
    fun exists(@PathVariable bookId: Long) =
        wishlistService.exists(bookId)

    // DELETE /api/v1/wishlist
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun clear() {
        wishlistService.clear()
    }
}
