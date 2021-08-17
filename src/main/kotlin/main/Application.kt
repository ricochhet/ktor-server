package main

// Base server imports
import database.initializeDatabase
import database.services.bindServices
import server.routes.registerGenericRoutes
import server.routes.registerAuthRoutes
import server.extensions.statusHandler
import server.cookies.SessionCookie
import server.data.hashedUserTable

// Specific server imports
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
import org.kodein.di.ktor.di
import socket.routes.registerSocketRoutes
import java.io.File

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module(testing: Boolean = false) {
    System.setProperty("io.ktor.random.secure.random.provider", "DRBG")
    Security.setProperty("securerandom.drbg.config", "HMAC_DRBG,SHA-512,256,pr_and_reseed")

    val dotenv = dotenv()
    val signature = dotenv["SIGN_KEY_0"]

    val useDatabaseModel = dotenv["USE_DATABASE_MODEL"]
    val useSessionCookies = dotenv["USE_SESSION_COOKIES"]
    val useAuthRoutes = dotenv["USE_AUTH_ROUTES"]
    val useWSServer = dotenv["USE_WS_SERVER"]

    if (useDatabaseModel == "TRUE") {
        initializeDatabase()
    }

    install(CORS) {
        anyHost()
    }

    install(ContentNegotiation) {
        json()
    }

    install(CallLogging) {
        level = Level.INFO
    }

    if (useSessionCookies == "TRUE") {
        install(Sessions) {
            cookie<SessionCookie>("UserSessionCookie", storage = directorySessionStorage(File(".sessions"))) {
                cookie.path = "/"
                cookie.maxAgeInSeconds = 60
                // cookie.secure = true

                transform(SessionTransportTransformerMessageAuthentication(hex(signature)))
            }
            /*header<SessionCookie>("UserSessionCookie", storage = directorySessionStorage(File(".sessions"))) {
                transform(SessionTransportTransformerMessageAuthentication(hex(signature)))
            }*/
        }
    }

    if (useAuthRoutes == "TRUE") {
        install(Authentication) {
            basic("auth-basic") {
                realm = "Access to the '/' path"
                validate { credentials ->
                    hashedUserTable.authenticate(credentials)
                }
            }
            session<SessionCookie>("auth-session") {
                validate { session ->
                    session
                }
                challenge {
                    call.respondRedirect("/login")
                }
            }
        }
    }

    install(StatusPages) {
        exception<Throwable> { cause ->
            call.respond(HttpStatusCode.InternalServerError, "Internal Server Error")
            throw cause
        }

        statusHandler(HttpStatusCode.NotFound, HttpStatusCode.Unauthorized, HttpStatusCode.UnsupportedMediaType,
        HttpStatusCode.BadRequest, HttpStatusCode.OK, HttpStatusCode.Accepted)
    }

    di {
        bindServices()
    }

    if (useAuthRoutes == "TRUE") {
        registerAuthRoutes(
            loginAuthLevel = "auth-basic",
            successAuthLevel = "auth-session",
            loginPath = "/login",
            successPath = "/success",
            logoutPath = "/logout",
            registerPath = "/register"
        )
    }

    registerGenericRoutes()
    registerCustomerRoutes()
    registerOrderRoutes()

    if (useWSServer == "TRUE") {
        install(WebSockets)
        registerSocketRoutes("/ws")
    }
}