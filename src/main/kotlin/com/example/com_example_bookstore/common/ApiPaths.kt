package com.example.bookstore.common

object ApiPaths {

    const val API_V1 = "/api/v1"

    // auth / admin
    const val AUTH = "$API_V1/auth"
    const val ADMIN = "$API_V1/admin"

    // user
    const val USERS = "$API_V1/users"

    // book
    const val BOOKS = "$API_V1/books"

    // review
    const val REVIEWS = "$API_V1/reviews"
    const val BOOK_REVIEWS = "$BOOKS/{bookId}/reviews"

    // comment
    const val COMMENTS = "$API_V1/comments"
    const val REVIEW_COMMENTS = "$REVIEWS/{reviewId}/comments"

    const val WISHLIST = "$API_V1/wishlist"




}
