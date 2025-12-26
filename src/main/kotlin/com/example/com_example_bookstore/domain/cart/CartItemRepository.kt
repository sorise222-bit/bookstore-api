package com.example.com_example_bookstore.domain.cart

import org.springframework.data.jpa.repository.JpaRepository

interface CartItemRepository : JpaRepository<CartItem, Long>
