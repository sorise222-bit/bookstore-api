package com.example.com_example_bookstore.domain.user

import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import com.example.bookstore.common.ApiPaths

data class MeResponse(
    val id: Long,
    val email: String,
    val name: String,
    val role: String
)

@RestController
@RequestMapping(ApiPaths.USERS)
class UserController(
    private val userRepository: UserRepository
) {
    @GetMapping("/me")
    fun me(authentication: Authentication): ResponseEntity<MeResponse> {
        val userId = authentication.principal as Long
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("USER_NOT_FOUND") }

        return ResponseEntity.ok(
            MeResponse(
                id = user.id,
                email = user.email,
                name = user.name,
                role = user.role.name
            )
        )
    }
}
