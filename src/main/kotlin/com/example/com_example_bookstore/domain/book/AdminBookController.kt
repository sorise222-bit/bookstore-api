package com.example.com_example_bookstore.domain.book

import com.example.com_example_bookstore.domain.book.dto.UpdateStockRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/admin/books")
class AdminBookController(
    private val bookAdminService: BookAdminService
) {
    @PatchMapping("/{bookId}/stock")
    fun updateStock(
        @PathVariable bookId: Long,
        @RequestBody req: UpdateStockRequest
    ): ResponseEntity<Void> {
        bookAdminService.updateStock(bookId, req.stock)
        return ResponseEntity.ok().build()
    }
}
