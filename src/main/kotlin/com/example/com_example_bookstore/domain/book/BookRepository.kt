package com.example.com_example_bookstore.domain.book


import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface BookRepository : JpaRepository<Book, Long> {

    @Query(
        """
        SELECT b FROM Book b
        WHERE (:keyword IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')))
          AND (:category IS NULL OR LOWER(b.categories) LIKE LOWER(CONCAT('%', :category, '%')))
        """
    )
    fun search(
        @Param("keyword") keyword: String?,
        @Param("category") category: String?,
        pageable: Pageable
    ): Page<Book>
}
