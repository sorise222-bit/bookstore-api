package com.example.com_example_bookstore.common

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

object JsonListCodec {
    private val om = jacksonObjectMapper()

    fun toJson(list: List<String>): String = om.writeValueAsString(list)
    fun fromJson(json: String): List<String> = runCatching { om.readValue<List<String>>(json) }.getOrDefault(emptyList())
}