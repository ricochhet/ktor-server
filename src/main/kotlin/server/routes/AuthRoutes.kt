package server.routes

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import server.extensions.isSessionExpired
import server.extensions.isSessionRatelimited
import server.models.UserSession
import util.standardDateFormat
import util.toString
import java.util.*

fun Route.authRouting(baseAuthLevel: String = "auth-basic", validAuthLevel: String = "auth-session", baseAuthPath: String = "/login", validAuthPath: String = "/success", closeAuthPath: String = "/logout") {
    // Generally should use "auth-basic" level.
    authenticate(baseAuthLevel) {
        route(baseAuthPath) {
            get {
                call.respond(HttpStatusCode.OK, "Session authentication")
            }
            post {
                val userName = call.principal<UserIdPrincipal>()?.name.toString()
                // 900000 milliseconds = 15 minutes
                val expireDate = System.currentTimeMillis() + 900000

                call.sessions.set(UserSession(name = userName, expiration = expireDate, count = 1))
                call.respondRedirect(validAuthPath)
            }
        }
    }

    // Generic example of limited session.
    // Generally should use "auth-session" for post-auth routes.
    authenticate(validAuthLevel) {
        get(validAuthPath) {
            val userSession = call.principal<UserSession>()
            val visit: Int = userSession?.count ?: 0
            val expiration: Long = userSession?.expiration ?: 0
            val date = Date(expiration)

            if (isSessionExpired(expiration)) return@get call.respond(HttpStatusCode.Unauthorized, "Your session has expired.")
            if (isSessionRatelimited(visit, 10)) return@get call.respond(HttpStatusCode.TooManyRequests, "You have gone over your rate limit.")

            call.sessions.set(userSession?.copy(count = userSession.count + 1))
            call.respond("Success! Hello, ${userSession?.name}! Your visit count is ${userSession?.count}. Your session will expire on: ${date.toString(standardDateFormat)}.")
        }
    }

    route(closeAuthPath) {
        get {
            call.sessions.clear<UserSession>()
            call.respondRedirect(baseAuthPath)
        }
    }
}

fun Application.registerAuthRoutes(baseAuthLevel: String = "auth-basic", validAuthLevel: String = "auth-session", baseAuthPath: String = "/login", validAuthPath: String = "/success", closeAuthPath: String = "/logout") {
    routing {
        authRouting(baseAuthLevel, validAuthLevel, baseAuthPath, validAuthPath, closeAuthPath)
    }
}