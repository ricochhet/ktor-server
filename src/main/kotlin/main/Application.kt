package main

// Base server imports
import server.routes.registerAuthRoutes
import server.extensions.statusHandler
import server.models.UserSession
import server.data.hashedUserTable

// Specific server imports
import main.routes.registerGenericRoutes
import main.routes.registerCustomerRoutes
import main.routes.registerOrderRoutes

// Generic imports
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.serialization.*
import io.ktor.sessions.*
import io.ktor.util.*
import java.security.Security
import org.slf4j.event.Level
import io.github.cdimascio.dotenv.dotenv
import io.ktor.websocket.*
import socket.routes.registerSocketRoutes
import java.io.File

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module(testing: Boolean = false) {
    // Initialize dotenv module
    val dotenv = dotenv()
    val signature = dotenv["SIGN_KEY_0"]

    // Prevents: WARN  io.ktor.util.random - NativePRNGNonBlocking is not found, fallback to SHA1PRNG
    // I'm not too sure if there's any notable security issues from this. But it works.
    System.setProperty("io.ktor.random.secure.random.provider", "DRBG")
    Security.setProperty("securerandom.drbg.config", "HMAC_DRBG,SHA-512,256,pr_and_reseed")

    install(CORS) {
        anyHost()
    }

    install(ContentNegotiation) {
        json()
    }

    install(CallLogging) {
        // Configure call logging
        level = Level.INFO
    }

    install(Sessions) {
        // Configure session cookies

        // See: https://ktor.io/docs/cookie-header.html, https://ktor.io/docs/client-server.html, https://ktor.io/docs/storages.html
        cookie<UserSession>("user_session", storage = directorySessionStorage(File(".sessions"))) {
            cookie.path = "/"
            cookie.maxAgeInSeconds = 60

            // Uncomment this if you plan to use the server in prod.
            // cookie.secure = true

            // See: https://ktor.io/docs/transformers.html
            transform(SessionTransportTransformerMessageAuthentication(hex(signature)))
        }

        // Unused, can be used in replacement of cookie, See: https://ktor.io/docs/cookie-header.html
        // Quote: "Cookies suit better for plain HTML applications while custom headers are intended for APIs."

        /*header<UserSession>("user_session", storage = directorySessionStorage(File(".sessions"))) {
            transform(SessionTransportTransformerMessageAuthentication(hex(signature)))
        }*/
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

    // Authentication routes
    registerAuthRoutes(
        baseAuthLevel = "auth-basic",
        validAuthLevel = "auth-session",
        baseAuthPath = "/login",
        validAuthPath = "/success",
        closeAuthPath = "/logout"
    )

    // Generic, non-specific routes
    registerGenericRoutes()

    // Sub routes
    registerCustomerRoutes()
    registerOrderRoutes()

    // This is technically optional, but since I want to expand this template
    // for a project, I have to set it up here. Feel free to remove it. :)
    if (dotenv["USE_WS_SERVER"] == "TRUE") {
        install(WebSockets)
        registerSocketRoutes("/ws")
    }
}