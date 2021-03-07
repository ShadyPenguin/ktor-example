package com.example.httpClient

import com.example.models.Customer
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get

object Client {
    val client = HttpClient(CIO)

    /**
     * Send a GET request to the HTTP Server to get [Customer] by provided ID
     */
    suspend fun getCustomerById(id: Int): Customer {
        val request = HttpRequestBuilder()
        request.authorize()
        request.baseUrl()
        request.url.encodedPath =  "/customer/$id"
        return client.get(request)
    }
}
