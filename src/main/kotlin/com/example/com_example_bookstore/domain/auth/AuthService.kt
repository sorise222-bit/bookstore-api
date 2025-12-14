package com.example.com_example_bookstore.domain.auth

import com.example.com_example_bookstore.domain.auth.dto.LoginRequest
import com.example.com_example_bookstore.domain.auth.dto.ReissueRequest
import com.example.com_example_bookstore.domain.auth.dto.TokenResponse
import com.example.com_example_bookstore.domain.user.UserRepository
import com.example.com_example_bookstore.security.JwtProvider
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtProvider: JwtProvider,
    @Value("\${JWT_REFRESH_EXP_MS}") private val refreshExpMs: Long
) {

    @Transactional
    fun login(req: LoginRequest): TokenResponse {
        val user = userRepository.findByEmail(req.email)
            ?: throw IllegalArgumentException("INVALID_CREDENTIALS")

        if (!passwordEncoder.matches(req.password, user.passwordHash)) {
            throw IllegalArgumentException("INVALID_CREDENTIALS")
        }

        val access = jwtProvider.createAccessToken(user.id, user.role)
        val refresh = jwtProvider.createRefreshToken(user.id, user.role)

        refreshTokenRepository.save(
            RefreshToken(
                user = user,
                refreshToken = refresh,
                expiresAt = LocalDateTime.now().plusNanos(refreshExpMs * 1_000_000)
            )
        )

        return TokenResponse(access, refresh)
    }

    @Transactional
    fun reissue(req: ReissueRequest): TokenResponse {
        val saved = refreshTokenRepository.findByRefreshToken(req.refreshToken)
            ?: throw IllegalArgumentException("REFRESH_NOT_FOUND")

        if (saved.revoked) throw IllegalArgumentException("REFRESH_REVOKED")
        if (saved.expiresAt.isBefore(LocalDateTime.now())) throw IllegalArgumentException("REFRESH_EXPIRED")

        // refresh 토큰 자체도 위변조 검증(서명/만료) + typ 검사
        if (jwtProvider.getType(req.refreshToken) != "refresh") {
            throw IllegalArgumentException("INVALID_TOKEN_TYPE")
        }

        val user = saved.user
        val newAccess = jwtProvider.createAccessToken(user.id, user.role)
        val newRefresh = jwtProvider.createRefreshToken(user.id, user.role)

        // 기존 refresh 토큰 폐기 + 새 토큰 저장
        saved.revoked = true
        refreshTokenRepository.save(saved)

        refreshTokenRepository.save(
            RefreshToken(
                user = user,
                refreshToken = newRefresh,
                expiresAt = LocalDateTime.now().plusNanos(refreshExpMs * 1_000_000)
            )
        )

        return TokenResponse(newAccess, newRefresh)
    }

    @Transactional
    fun logout(refreshToken: String) {
        val saved = refreshTokenRepository.findByRefreshToken(refreshToken)
            ?: return
        saved.revoked = true
        refreshTokenRepository.save(saved)
    }
}
