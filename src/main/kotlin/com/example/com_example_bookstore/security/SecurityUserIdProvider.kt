package com.example.com_example_bookstore.security

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class SecurityUserIdProvider : UserIdProvider {

    override fun currentUserId(): Long {
        val auth = SecurityContextHolder.getContext().authentication
            ?: throw SecurityException("UNAUTHORIZED")

        val principal = auth.principal
        return when (principal) {
            is Long -> principal
            is Int -> principal.toLong()
            is String -> principal.toLong()
            else -> throw SecurityException("UNAUTHORIZED")
        }
    }

    override fun isAdmin(): Boolean {
        val auth = SecurityContextHolder.getContext().authentication ?: return false
        return auth.authorities.any { it.authority == "ROLE_ADMIN" }
    }
}
