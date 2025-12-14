package com.example.com_example_bookstore.domain.admin

import com.example.com_example_bookstore.domain.admin.dto.AdminUserResponse
import com.example.com_example_bookstore.domain.admin.dto.DeactivateUserResponse
import com.example.com_example_bookstore.domain.admin.dto.StatsResponse
import com.example.com_example_bookstore.domain.user.UserRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import com.example.bookstore.common.ApiPaths

@RestController
@RequestMapping(ApiPaths.ADMIN)
class AdminController(
    private val userRepository: UserRepository
) {
    // ✅ 관리자(1): 전체 사용자 목록
    @GetMapping("/users")
    fun listUsers(): ResponseEntity<List<AdminUserResponse>> {
        val users = userRepository.findAll().map {
            AdminUserResponse(
                id = it.id,
                email = it.email,
                name = it.name,
                role = it.role.name,
                active = it.deletedAt == null
            )
        }
        return ResponseEntity.ok(users)
    }

    // ✅ 관리자(2): 사용자 비활성화(소프트 삭제)
    @PatchMapping("/users/{id}/deactivate")
    fun deactivate(@PathVariable id: Long): ResponseEntity<DeactivateUserResponse> {
        val user = userRepository.findById(id)
            .orElseThrow { IllegalArgumentException("USER_NOT_FOUND") }

        user.deletedAt = LocalDateTime.now()
        userRepository.save(user)

        return ResponseEntity.ok(
            DeactivateUserResponse(
                id = user.id,
                active = user.deletedAt == null
            )
        )
    }

    // ✅ 관리자(3): 통계(일단 users count로 간단히)
    @GetMapping("/stats")
    fun stats(): ResponseEntity<StatsResponse> {
        return ResponseEntity.ok(
            StatsResponse(users = userRepository.count())
        )
    }
}
