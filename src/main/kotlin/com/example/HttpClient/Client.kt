package com.example.HttpClient

import com.example.Models.Customer
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.header
import java.util.*

object Client {
    val client = HttpClient(CIO)

    suspend fun getCustomerById(id: Int): Customer {
        val request = HttpRequestBuilder()
        request.url {
            host = "0.0.0.0"
            port = 8080
            encodedPath = "/customer/$id"
        }
        val auth = Base64.getEncoder().encodeToString("admin:admin".toByteArray())
        request.header("Authorization", "Basic $auth")
        return client.get(request)
    }
}