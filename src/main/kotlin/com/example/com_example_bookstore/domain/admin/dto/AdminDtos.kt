package com.example.com_example_bookstore.domain.admin.dto

data class AdminUserResponse(
    val id: Long,
    val email: String,
    val name: String,
    val role: String,
    val active: Boolean
)

data class DeactivateUserResponse(
    val id: Long,
    val active: Boolean
)

data class StatsResponse(
    val users: Long
)
