package com.example

import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.application.log
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.authenticate
import io.ktor.auth.authentication
import io.ktor.auth.basic
import io.ktor.auth.principal
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.jackson.jackson
import io.ktor.request.path
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import org.slf4j.event.Level
import java.util.*

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

/**
 * Please note that you can use any other name instead of *module*.
 * Also note that you can have more then one modules in your application.
 * */
@Suppress("unused") // Referenced in application.conf
fun Application.serve(testing: Boolean = false) {
    val client = HttpClient(CIO)

    authentication {
        basic(name = "admin") {
            realm = "Ktor Server"
            validate { credentials ->
                if (credentials.name == "admin" && credentials.password == "admin") {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }
        }

    }
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }
    install(DefaultHeaders) {
        header("X-Engine", "Ktor") // will send this header with each response
    }

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    routing {
        /**
         * No Auth Routes
         */
        get("/") {
            call.respondText("Hello World!")
        }
        get("/json/jackson") {
            call.respond(mapOf("hello" to "world"))
        }
        /**
         * Admin Routes
         */
        authenticate("admin") {
            get("/protected/route/basic") {
                val principal = call.principal<UserIdPrincipal>()!!
                call.respondText("Hello ${principal.name}")
            }
            get("/user/validate") {
                val principal = call.principal<UserIdPrincipal>()!!
                call.respond(principal)
            }
            customerRoutes()
        }
        /**
         * No Auth Routes -- Sending HTTP Requests to the HTTP Server
         */
        // Send request with basic authentication
        get("/request/authenticate") {
            val request = HttpRequestBuilder()
            request.url {
                host = "0.0.0.0"
                port = 8080
                encodedPath = "/user/validate"
            }
            val auth = Base64.getEncoder().encodeToString("admin:admin".toByteArray())
            request.header("Authorization", "Basic $auth")
            val response = client.get<String>(request)
            log.info(response)
            call.respondText("You successfully added authorization to your request")
        }
    }
}

