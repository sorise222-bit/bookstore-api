package com.example.com_example_bookstore.security

import com.example.com_example_bookstore.domain.user.Role
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.util.Date

@Component
class JwtProvider(
    @Value("\${JWT_SECRET}") private val secret: String,
    @Value("\${JWT_ACCESS_EXP_MS}") private val accessExpMs: Long,
    @Value("\${JWT_REFRESH_EXP_MS}") private val refreshExpMs: Long
) {
    private val key = Keys.hmacShaKeyFor(secret.toByteArray(StandardCharsets.UTF_8))

    fun createAccessToken(userId: Long, role: Role): String =
        createToken(userId, role, accessExpMs, "access")

    fun createRefreshToken(userId: Long, role: Role): String =
        createToken(userId, role, refreshExpMs, "refresh")

    private fun createToken(userId: Long, role: Role, expMs: Long, type: String): String {
        val now = Date()
        val exp = Date(now.time + expMs)
        return Jwts.builder()
            .subject(userId.toString())
            .claim("role", role.name)   // "user" or "admin"
            .claim("typ", type)
            .issuedAt(now)
            .expiration(exp)
            .signWith(key)
            .compact()
    }

    fun parse(token: String) =
        Jwts.parser().verifyWith(key).build().parseSignedClaims(token).payload

    fun getUserId(token: String): Long = parse(token).subject.toLong()
    fun getRole(token: String): Role = Role.valueOf(parse(token).get("role", String::class.java))
    fun getType(token: String): String = parse(token).get("typ", String::class.java)
}
