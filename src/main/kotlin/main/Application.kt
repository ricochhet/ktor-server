package main

import main.routes.registerAuthRoutes
import main.routes.registerGenericRoutes
import main.routes.registerCustomerRoutes
import main.routes.registerOrderRoutes

import main.models.UserSession
import main.data.hashedUserTable

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.serialization.*
import io.ktor.sessions.*
import main.status.statusHandler

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module(testing: Boolean = false) {
    install(ContentNegotiation) {
        json()
    }

    install(Sessions) {
        cookie<UserSession>("user_session") {
            cookie.path = "/"
            cookie.maxAgeInSeconds = 60
        }
    }

    install(Authentication) {
        basic("auth-basic") {
            // Configure basic authentication
            realm = "Access to the '/' path"
            validate { credentials ->
                hashedUserTable.authenticate(credentials)
            }
        }
        session<UserSession>("auth-session") {
            // Configure session auth
            validate { session ->
                session
            }
            challenge {
                call.respondRedirect("/login")
            }
        }
    }

    install(StatusPages) {
        // Configure status pages
        exception<Throwable> { cause ->
            call.respond(HttpStatusCode.InternalServerError, "Internal Server Error")
            throw cause
        }

        // Specify statuses to use a generic handle
        statusHandler(HttpStatusCode.NotFound, HttpStatusCode.Unauthorized)
    }

    // Main routes
    registerAuthRoutes()
    registerGenericRoutes()

    // Sub routes
    registerCustomerRoutes()
    registerOrderRoutes()
}