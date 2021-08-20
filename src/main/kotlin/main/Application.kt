package main

// Base server imports
import database.extensions.AuthorizationException
import database.extensions.RoleBasedAuthorization
import database.initializeDatabase
import database.models.Users
import database.services.UserService
import database.services.bindServices
import server.routes.registerGenericRoutes
import server.routes.registerAuthRoutes
import server.extensions.statusHandler
import server.cookies.SessionCookie

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
import io.ktor.request.*
import io.ktor.websocket.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI
import org.kodein.di.ktor.di
import server.cookies.OriginalRequestURI
import socket.routes.registerSocketRoutes
import util.crypto.sha256
import util.logging.KotlinLogger
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

    val userService by closestDI().instance<UserService>()

    if (useDatabaseModel == "TRUE") {
        KotlinLogger.info("Enable Feature: Database")
        initializeDatabase()
    }

    install(CORS) {
        KotlinLogger.info("Enable Feature: CORS")
        anyHost()
    }

    install(ContentNegotiation) {
        KotlinLogger.info("Enable Feature: ContentNegotiation")
        json()
    }

    install(CallLogging) {
        KotlinLogger.info("Enable Feature: CallLogging")
        level = Level.INFO
    }

    if (useSessionCookies == "TRUE") {
        install(Sessions) {
            KotlinLogger.info("Enable Feature: Sessions")
            cookie<SessionCookie>("UserSessionCookie", storage = directorySessionStorage(File(".sessions"))) {
                cookie.path = "/"
                cookie.maxAgeInSeconds = 60
                // cookie.secure = true

                transform(SessionTransportTransformerMessageAuthentication(hex(signature)))
            }
            cookie<OriginalRequestURI>("OriginalSessionCookie")
            /*header<SessionCookie>("UserSessionCookie", storage = directorySessionStorage(File(".sessions"))) {
                transform(SessionTransportTransformerMessageAuthentication(hex(signature)))
            }*/
        }
    }

    if (useAuthRoutes == "TRUE") {
        install(Authentication) {
            KotlinLogger.info("Enable Feature: Authentication")
            basic("BasicUserAuthentication") {
                realm = "Access to the '/' path"
                validate { credentials ->
                    val user = userService.findUserByEmail(credentials.name.sha256())

                    if (user != null) {
                        val name = credentials.name.sha256()
                        val password = credentials.password.sha256()

                        val dbUser = user[Users.email]
                        val dbPassword = user[Users.password]

                        if (name == dbUser && password == dbPassword) {
                            KotlinLogger.info("User '$name' logged in from 'BasicUserAuthentication'")
                            UserIdPrincipal(credentials.name)
                        } else {
                            KotlinLogger.warn("User '$name' failed to log in from 'BasicUserAuthentication'")
                            null
                        }
                    } else {
                        KotlinLogger.warn("User failed to log in from 'BasicUserAuthentication'")
                        null
                    }
                }
            }
            session<SessionCookie>("SessionUserAuthentication") {
                validate { session ->
                    KotlinLogger.info("User '${session.name}' logged in from 'SessionUserAuthentication")
                    session
                }
                challenge {
                    KotlinLogger.info("No valid session found for this route, redirect to log-in form")
                    call.sessions.set(OriginalRequestURI(call.request.uri))
                    call.respondRedirect("/login")
                }
            }
        }

        install(RoleBasedAuthorization) {
            KotlinLogger.info("Enable Feature: RoleBasedAuthorization")
            getRoles { (it as SessionCookie).roles }
        }
    }

    install(StatusPages) {
        KotlinLogger.info("Enable Feature: StatusPages")
        /*exception<Throwable> { cause ->
            call.respond(HttpStatusCode.InternalServerError, "Internal Server Error")
            throw cause
        }*/

        exception<AuthorizationException> { cause ->
            KotlinLogger.error(cause.toString())
            call.respond(HttpStatusCode.Forbidden)
        }

        statusHandler(HttpStatusCode.NotFound, HttpStatusCode.Unauthorized, HttpStatusCode.UnsupportedMediaType,
        HttpStatusCode.BadRequest, HttpStatusCode.OK, HttpStatusCode.Accepted, HttpStatusCode.Conflict, HttpStatusCode.Forbidden)
    }

    di {
        KotlinLogger.info("Kodein: bindServices()")
        bindServices()
    }

    if (useAuthRoutes == "TRUE") {
        KotlinLogger.info("Enable Feature: Auth Router")
        registerAuthRoutes()
    }

    registerGenericRoutes()
    registerCustomerRoutes()
    registerOrderRoutes()

    if (useWSServer == "TRUE") {
        install(WebSockets)
        KotlinLogger.info("Enable Feature: WebSocket Server")
        registerSocketRoutes("/ws")
    }
}