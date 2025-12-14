package com.example.com_example_bookstore.domain.auth

import com.example.com_example_bookstore.domain.auth.dto.LoginRequest
import com.example.com_example_bookstore.domain.auth.dto.ReissueRequest
import com.example.com_example_bookstore.domain.auth.dto.TokenResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import com.example.bookstore.common.ApiPaths
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping(ApiPaths.AUTH)
class AuthController(
    private val authService: AuthService
) {
    @PostMapping("/login")
    fun login(@RequestBody req: LoginRequest): ResponseEntity<TokenResponse> =
        ResponseEntity.ok(authService.login(req))

    @PostMapping("/reissue")
    fun reissue(@RequestBody req: ReissueRequest): ResponseEntity<TokenResponse> =
        ResponseEntity.ok(authService.reissue(req))

    @PostMapping("/logout")
    fun logout(@RequestBody req: ReissueRequest): ResponseEntity<Map<String, Any>> {
        authService.logout(req.refreshToken)
        return ResponseEntity.ok(mapOf("isSuccess" to true, "message" to "로그아웃 완료"))
    }
}
