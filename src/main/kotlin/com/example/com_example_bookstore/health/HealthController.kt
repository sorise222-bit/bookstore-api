package com.example.com_example_bookstore.health

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.time.OffsetDateTime

@RestController
class HealthController {

    @GetMapping("/health")
    fun health(): Map<String, Any> =
        mapOf(
            "status" to "UP",
            "version" to "v1",
            "time" to OffsetDateTime.now().toString()
        )
}
