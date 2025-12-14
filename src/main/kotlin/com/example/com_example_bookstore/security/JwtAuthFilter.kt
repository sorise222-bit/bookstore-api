package com.example.com_example_bookstore.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import java.util.Locale

class JwtAuthFilter(
    private val jwtProvider: JwtProvider
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        val token = authHeader.substring(7)

        try {
            val userId = jwtProvider.getUserId(token)
            val role = jwtProvider.getRole(token) // enum user/admin

            val authorities = listOf(
                SimpleGrantedAuthority("ROLE_${role.name.uppercase(Locale.getDefault())}")
            )

            val authentication = UsernamePasswordAuthenticationToken(
                userId,
                null,
                authorities
            )

            SecurityContextHolder.getContext().authentication = authentication

        } catch (e: Exception) {
            // 인증 실패 → SecurityContext 비워둠 → 보호 API에서 401/403
        }

        filterChain.doFilter(request, response)
    }
}
