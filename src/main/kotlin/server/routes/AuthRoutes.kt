package server.routes

import database.models.User
import database.services.UserService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI
import server.models.UserSession

fun Route.authRouting(loginAuthLevel: String = "auth-basic", successAuthLevel: String = "auth-session", loginPath: String = "/login", successPath: String = "/success", logoutPath: String = "/logout", registerPath: String = "/register") {
    route(registerPath) {
        val userService by closestDI().instance<UserService>()

        // Test route
        get {
            val allUsers = userService.getAllUsers()
            call.respond(allUsers)
            // call.respond(HttpStatusCode.OK, "Register")
        }

        // Primary route
        post {
            // val parameters = call.receiveParameters()
            // val username = parameters["username"].toString()
            // val password = parameters["password"].toString()
            val userRequest = call.receive<User>()
            userService.addUser(userRequest)
            call.respond(HttpStatusCode.Accepted)
        }

        // Test route
        delete("{id}") {
            val userId = call.parameters["id"]?.toIntOrNull() ?: throw NotFoundException()
            userService.deleteUser(userId)
            call.respond(HttpStatusCode.OK)
        }
    }


    // Generally should use "auth-basic" level.
    authenticate(loginAuthLevel) {
        route(loginPath) {
            get {
                call.respond(HttpStatusCode.OK, "Login")
            }
            post {
                val userName = call.principal<UserIdPrincipal>()?.name.toString()
                call.sessions.set(UserSession(name = userName, count = 1))
                call.respondRedirect(successPath)
            }
        }
    }

    // Generic example of limited session.
    // Generally should use "auth-session" for post-auth routes.
    authenticate(successAuthLevel) {
        get(successPath) {
            val userSession = call.principal<UserSession>()

            val name: String = userSession?.name ?: ""
            val visit: Int = userSession?.count ?: 0

            call.sessions.set(userSession?.copy(count = userSession.count + 1))
            call.respond("Success! Hello, ${name}! Your visit count is ${visit}.")
        }
    }

    route(logoutPath) {
        get {
            call.sessions.clear<UserSession>()
            call.respondRedirect(loginPath)
        }
    }
}

fun Application.registerAuthRoutes(loginAuthLevel: String = "auth-basic", successAuthLevel: String = "auth-session", loginPath: String = "/login", successPath: String = "/success", logoutPath: String = "/logout", registerPath: String = "/register") {
    routing {
        authRouting(loginAuthLevel, successAuthLevel, loginPath, successPath, logoutPath, registerPath)
    }
}