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
import server.cookies.SessionCookie
import util.patterns.isValidEmail

fun Route.authRouting(loginAuthLevel: String = "auth-basic", successAuthLevel: String = "auth-session", loginPath: String = "/login", successPath: String = "/success", logoutPath: String = "/logout", registerPath: String = "/register") {
    route(registerPath) {
        val userService by closestDI().instance<UserService>()

        get {
            try {
                val allUsers = userService.getAllUsers()
                call.respond(HttpStatusCode.OK, allUsers)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        post {
            try {
                val userRequest = call.receive<User>()
                val emailValidator = userRequest.email.isValidEmail()

                if (emailValidator) {
                    userService.addUser(userRequest)
                    call.respond(HttpStatusCode.Accepted)
                } else {
                    call.respond(HttpStatusCode.BadRequest)
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        delete("{id}") {
            try {
                val userId = call.parameters["id"]?.toIntOrNull() ?: throw NotFoundException()
                userService.deleteUser(userId)
                call.respond(HttpStatusCode.OK)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest)
            }
        }
    }

    authenticate(loginAuthLevel) {
        route(loginPath) {
            get {
                call.respond(HttpStatusCode.OK, "Login")
            }
            post {
                val userName = call.principal<UserIdPrincipal>()?.name.toString()
                call.sessions.set(SessionCookie(name = userName, count = 1))
                call.respondRedirect(successPath)
            }
        }
    }

    authenticate(successAuthLevel) {
        get(successPath) {
            val sessionCookie = call.principal<SessionCookie>()

            val name: String = sessionCookie?.name ?: ""
            val visit: Int = sessionCookie?.count ?: 0

            call.sessions.set(sessionCookie?.copy(count = sessionCookie.count + 1))
            call.respond(HttpStatusCode.OK, "Success! Hello, ${name}! Your visit count is ${visit}.")
        }
    }

    route(logoutPath) {
        get {
            call.sessions.clear<SessionCookie>()
            call.respondRedirect(loginPath)
        }
    }
}

fun Application.registerAuthRoutes(loginAuthLevel: String = "auth-basic", successAuthLevel: String = "auth-session", loginPath: String = "/login", successPath: String = "/success", logoutPath: String = "/logout", registerPath: String = "/register") {
    routing {
        authRouting(loginAuthLevel, successAuthLevel, loginPath, successPath, logoutPath, registerPath)
    }
}