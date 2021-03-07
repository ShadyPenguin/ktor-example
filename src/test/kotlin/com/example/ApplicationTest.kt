package com.example

import com.example.models.Customer
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import kotlin.test.Test
import kotlin.test.assertEquals

val objectMapper = jacksonObjectMapper()

class ApplicationTest {
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
            handleRequest(HttpMethod.Get, "/customer").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                val expected = listOf(baseCustomer, baseCustomer.copy(id=2))
                val actual: List<Customer> = objectMapper.readValue(response.content!!)
                assertEquals(expected, actual)
            }
        }
    }

    @Test
    fun `Create Customer`() {
        withTestApplication({ serve() }) {
            handleRequest(HttpMethod.Post, "/customer") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(objectMapper.writeValueAsString(baseCustomer))
            }.apply {
                assertEquals(HttpStatusCode.Created, response.status())
                val actual: Customer = objectMapper.readValue(response.content!!)
                assertEquals(baseCustomer, actual)
            }
        }
    }

}