package com.example.com_example_bookstore.domain.book

import jakarta.persistence.LockModeType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface BookRepository : JpaRepository<Book, Long> {

    // ✅ 장바구니→주문 생성 시 재고 동시성 방어(락)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from Book b where b.id in :ids")
    fun findAllByIdForUpdate(@Param("ids") ids: List<Long>): List<Book>

    // ✅ 목록 조회/검색/카테고리 필터 + 페이지네이션
    @Query(
        """
        select b from Book b
        where (:keyword is null or lower(b.title) like lower(concat('%', :keyword, '%')))
          and (:category is null or lower(b.categories) like lower(concat('%', :category, '%')))
        """
    )
    fun search(
        @Param("keyword") keyword: String?,
        @Param("category") category: String?,
        pageable: Pageable
    ): Page<Book>
}
