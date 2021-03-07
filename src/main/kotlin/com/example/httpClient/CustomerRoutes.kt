package com.example

import com.example.models.Customer
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing

val baseCustomer = Customer(
    id = 1,
    name = "Jake",
    age = 33,
    height = 72
)

fun Application.customerRoutes() {
    routing {
        customerById()
        customerList()
        createCustomer()
    }
}

fun Route.customerList() {
    get("/customer") {
        val customers = listOf(baseCustomer, baseCustomer.copy(id=2))
        call.respond(status = HttpStatusCode.OK, customers)
    }
}

fun Route.customerById() {
    get("/customer/{id}") {
        val id: Int = call.parameters["id"]!!.toInt()
        call.respond(status = HttpStatusCode.OK, baseCustomer.copy(id=id))
    }
}

fun Route.createCustomer() {
    post("/customer") {
        val customer = call.receive<Customer>()
        call.respond(status = HttpStatusCode.Created, customer)
    }
}