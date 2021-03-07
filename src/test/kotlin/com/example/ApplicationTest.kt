package com.example

import com.example.models.Customer
import com.example.models.customerStorage
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

val objectMapper = jacksonObjectMapper()

class ApplicationTest {


    @BeforeEach
    fun beforeEach() {
        customerStorage.add(baseCustomer)
    }

    @AfterEach
    fun afterEach() {
        customerStorage.clear()
    }

    @Test
    fun testRoot() {
        withTestApplication({ serve() }) {
            handleRequest(HttpMethod.Get, "/").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("Hello World!", response.content)
            }
        }
    }

    @Test
    fun `test customer`() {
        withTestApplication({ serve() }) {
            handleRequest(HttpMethod.Get, "/customer") {
                val auth = Base64.getEncoder().encodeToString("admin:admin".toByteArray())
                addHeader(HttpHeaders.Authorization, "Basic $auth")
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                val actual: List<Customer> = objectMapper.readValue(response.content!!)
                assertEquals(listOf(baseCustomer), actual)
            }
        }
    }

    @Test
    fun `Create Customer`() {
        withTestApplication({ serve() }) {
            handleRequest(HttpMethod.Post, "/customer") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(objectMapper.writeValueAsString(baseCustomer))
                val auth = Base64.getEncoder().encodeToString("admin:admin".toByteArray())
                addHeader(HttpHeaders.Authorization, "Basic $auth")
            }.apply {
                assertEquals(HttpStatusCode.Created, response.status())
                val actual: Customer = objectMapper.readValue(response.content!!)
                assertEquals(baseCustomer, actual)
            }
        }
    }

}