package main.routes

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import main.models.UserSession

fun Route.authRouting() {
    authenticate("auth-basic") {
        route("/login") {
            post {
                val userName = call.principal<UserIdPrincipal>()?.name.toString()
                call.sessions.set(UserSession(name = userName, count = 1))
                call.respondRedirect("/success")
            }
        }
    }

    authenticate("auth-session") {
        get("/success") {
            val userSession = call.principal<UserSession>()
            call.sessions.set(userSession?.copy(count = userSession.count + 1))
            call.respondText("Success! Hello, ${userSession?.name}! Your visit count is ${userSession?.count}.")
        }
    }

    route("/logout") {
        get {
            call.sessions.clear<UserSession>()
            call.respondRedirect("/login")
        }
    }
}

fun Application.registerAuthRoutes() {
    routing {
        authRouting()
    }
}