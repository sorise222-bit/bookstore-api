package com.example.com_example_bookstore.util

import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class PasswordHashPrinter(
    private val passwordEncoder: PasswordEncoder
) : CommandLineRunner {

    override fun run(vararg args: String) {
        val raw = "P@ssw0rd!"
        println("BCRYPT_HASH = ${passwordEncoder.encode(raw)}")
    }
}
