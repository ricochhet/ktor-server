package server.routes

import database.models.User
import database.services.UserService
import io.github.cdimascio.dotenv.dotenv
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
import util.patterns.isValidPassword

fun Route.authRouting(loginAuthLevel: String, successAuthLevel: String, loginPath: String, successPath: String, logoutPath: String, registerPath: String) {
    val dotenv = dotenv()

    route(registerPath) {
        val userService by closestDI().instance<UserService>()

        get {
            runCatching {
                val allUsers = userService.getAllUsers()
                call.respond(HttpStatusCode.OK, allUsers)
            }.getOrElse {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        post {
            runCatching {
                val userRequest = call.receive<User>()
                val emailValidator = userRequest.email.isValidEmail()
                val passwordValidator = userRequest.password.isValidPassword(dotenv["MIN_PASSWORD_LENGTH"].toInt())

                if (emailValidator && passwordValidator) {
                    val user = userService.findUserByEmail(userRequest.email)

                    if (user == null) {
                        userService.addUser(userRequest)
                        call.respond(HttpStatusCode.Accepted)
                    } else {
                        call.respond(HttpStatusCode.Conflict)
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }.getOrElse {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        delete("{id}") {
            runCatching {
                val userId = call.parameters["id"]?.toIntOrNull() ?: throw NotFoundException()
                userService.deleteUser(userId)
                call.respond(HttpStatusCode.OK)
            }.getOrElse {
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

fun Application.registerAuthRoutes(loginAuthLevel: String, successAuthLevel: String, loginPath: String, successPath: String, logoutPath: String, registerPath: String) {
    routing {
        authRouting(loginAuthLevel, successAuthLevel, loginPath, successPath, logoutPath, registerPath)
    }
}