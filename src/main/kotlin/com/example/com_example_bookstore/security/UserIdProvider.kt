package com.example.com_example_bookstore.security

interface UserIdProvider {
    fun currentUserId(): Long
    fun isAdmin(): Boolean
}
