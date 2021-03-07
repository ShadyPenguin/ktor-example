package com.example.httpClient

import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import java.util.*

internal fun HttpRequestBuilder.authorize() {
    val auth = Base64.getEncoder().encodeToString("admin:admin".toByteArray())
    header("Authorization", "Basic $auth")
}

internal fun HttpRequestBuilder.baseUrl() {
    url {
        host = "0.0.0.0"
        port = 8080
    }
}