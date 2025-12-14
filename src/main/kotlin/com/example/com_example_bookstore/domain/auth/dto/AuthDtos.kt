package com.example.com_example_bookstore.domain.auth.dto

data class LoginRequest(
    val email: String,
    val password: String
)

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String
)

data class ReissueRequest(
    val refreshToken: String
)
