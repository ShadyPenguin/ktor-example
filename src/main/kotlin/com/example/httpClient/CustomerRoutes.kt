package com.example

import com.example.models.Customer
import com.example.models.customerStorage
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.put
import io.ktor.routing.route

val baseCustomer = Customer(
    id = 1,
    name = "Jake",
    age = 33,
    height = 72
)

fun Route.customerRoutes() {
    route("/customer") {
        get {
            call.respond(status = HttpStatusCode.OK, customerStorage.toMutableList())
        }
        get("{id}") {
            val id: Int = call.parameters["id"]!!.toInt()
            val customer = customerStorage.find { it.id == id } ?: return@get call.respond(status = HttpStatusCode.NotFound, "Customer Not Found")
            call.respond(status = HttpStatusCode.OK, customer)
        }
        post {
            val customer = call.receive<Customer>()
            customerStorage.add(customer)
            call.respond(status = HttpStatusCode.Created, customer)
        }
        put("{id}") {
            val id: Int = call.parameters["id"]!!.toInt()
            val customer = customerStorage.find { it.id == id } ?: return@put call.respond(
                status = HttpStatusCode.NotFound,
                "Customer Not Found"
            )
            customerStorage[customerStorage.indexOf(customer)] = call.receive()
            call.respond(status = HttpStatusCode.Accepted, customer)
        }
        delete("{id}") {
            val id = call.parameters["id"]!!.toInt()
            val customer = customerStorage.find { it.id == id } ?: return@delete call.respond(status= HttpStatusCode.NotFound, message = "Customer Not Found")
            customerStorage.remove(customer)
            call.respond(status = HttpStatusCode.NoContent, message = customer.id)
        }
    }
}
