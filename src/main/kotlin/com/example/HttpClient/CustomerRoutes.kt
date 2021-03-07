package com.example

import com.example.Models.Customer
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
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
    }
}

fun Route.customerById() {
    get("/customer/{id}") {
        val id: Int = call.parameters["id"]!!.toInt()
        call.respond(status = HttpStatusCode.OK, baseCustomer.copy(id=id))
    }
}
