package com.example

import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.features.*
import io.ktor.jackson.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*
import org.slf4j.event.Level
import java.util.*

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

/**
 * Please note that you can use any other name instead of *module*.
 * Also note that you can have more then one modules in your application.
 * */
@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
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
    /**
     * No Auth Routes
     */
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        get("/json/jackson") {
            call.respond(mapOf("hello" to "world"))
        }
    }
    /**
     * Admin Routes
     */
    routing {
        authenticate("admin") {
            get("/protected/route/basic") {
                val principal = call.principal<UserIdPrincipal>()!!
                call.respondText("Hello ${principal.name}")
            }
            get("/user/validate") {
                val principal = call.principal<UserIdPrincipal>()!!
                call.respondText("Hello ${principal.name}")
            }
        }
    }
    /**
     * Sending HTTP Requests
     */
    routing {
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

