package server.routes

import database.extensions.withRole
import database.models.ROLE_ADMIN
import database.models.ROLE_USER
import database.models.User
import database.models.Users
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
import org.slf4j.LoggerFactory
import server.cookies.OriginalRequestURI
import server.cookies.SessionCookie
import util.crypto.sha256
import util.logging.KotlinLogger
import util.patterns.isMatchedString
import util.patterns.isValidEmail
import util.patterns.isValidPassword

fun Route.registerRoute() {
    val dotenv = dotenv()
    val userService by closestDI().instance<UserService>()

    post("/register") {
        runCatching {
            val userRequest = call.receive<User>()
            val emailValidator = userRequest.email.isValidEmail()
            val passwordValidator = userRequest.password.isValidPassword(dotenv["MIN_PASSWORD_LENGTH"].toInt())

            // Force-set USER settings
            userRequest.roles = ROLE_USER
            userRequest.createdAt = System.currentTimeMillis().toString()

            val roleValidator = userRequest.roles.isMatchedString(ROLE_USER)

            if (emailValidator && passwordValidator && roleValidator) {
                KotlinLogger.info("Register for '${userRequest.email}' passed all checks")
                val user = userService.findUserByEmail(userRequest.email.sha256())

                if (user == null) {
                    KotlinLogger.info("User '${userRequest.email}' successfully signed up")
                    userService.addUser(userRequest)
                    call.respond(HttpStatusCode.Accepted)
                } else {
                    KotlinLogger.warn("User '${userRequest.email}' has a pre-existing account.")
                    call.respond(HttpStatusCode.Conflict)
                }
            } else {
                KotlinLogger.warn("User $userRequest attempted to sign-up with ${userRequest.roles}.")
                call.respond(HttpStatusCode.BadRequest)
            }
        }.getOrElse {
            KotlinLogger.error(it.toString())
            call.respond(HttpStatusCode.BadRequest)
        }
    }
}

fun Route.loginRoute() {
    val userService by closestDI().instance<UserService>()

    route("/login") {
        get {
            call.respond(HttpStatusCode.OK, "Access: '/login'")
        }
        authenticate("BasicUserAuthentication") {
            post {
                val userName = call.principal<UserIdPrincipal>()?.name.toString()
                val user = userService.findUserByEmail(userName.sha256())

                if (user == null) {
                    KotlinLogger.warn("User '$userName' was not found, but attempted to sign-in")
                    call.respond(HttpStatusCode.BadRequest)
                } else {
                    val dbRoles = user[Users.roles].toString().split(",").toSet()

                    call.sessions.set(SessionCookie(name = userName, count = 1, roles = dbRoles))
                    val redirectURL = call.sessions.get<OriginalRequestURI>()?.also {
                        call.sessions.clear<OriginalRequestURI>()
                    }

                    KotlinLogger.info("User '$userName' successfully signed in. Redirect URI: ${redirectURL?.uri ?: "/"}")
                    call.respondRedirect("/session")
                }
            }
        }
    }
}

fun Route.logoutRoute() {
    route("/logout") {
        get {
            call.sessions.clear<SessionCookie>()
            call.respondRedirect("/login")
        }
    }
}

fun Route.sessionRoute() {
    val userService by closestDI().instance<UserService>()

    authenticate("SessionUserAuthentication") {
        withRole(ROLE_ADMIN) {
            route("/register") {
                get {
                    runCatching {
                        KotlinLogger.info("Executed 'getAllUsers' at '/register'")
                        val allUsers = userService.getAllUsers()
                        call.respond(HttpStatusCode.OK, allUsers)
                    }.getOrElse {
                        KotlinLogger.error(it.toString())
                        call.respond(HttpStatusCode.BadRequest)
                    }
                }
                get("{id}") {
                    val userId = call.parameters["id"]?.toIntOrNull() ?: throw NotFoundException()
                    val user = userService.getUser(userId)

                    if (user == null) {
                        KotlinLogger.warn("Executed 'getUser' on $user at '/register', but failed")
                        call.respond(HttpStatusCode.NotFound)
                    } else {
                        KotlinLogger.info("Executed 'getUser' on $user at '/register'")
                        call.respond(HttpStatusCode.OK, user)
                    }

                }
                delete("{id}") {
                    runCatching {
                        val userId = call.parameters["id"]?.toIntOrNull() ?: throw NotFoundException()
                        KotlinLogger.info("Executed 'deleteUser' on $userId at '/register'")
                        userService.deleteUser(userId)
                        call.respond(HttpStatusCode.OK)
                    }.getOrElse {
                        KotlinLogger.error(it.toString())
                        call.respond(HttpStatusCode.BadRequest)
                    }
                }
            }
        }
        withRole(ROLE_USER) {
            get("/session") {
                val sessionCookie = call.principal<SessionCookie>()

                val name: String = sessionCookie?.name ?: ""
                val visit: Int = sessionCookie?.count ?: 0

                call.sessions.set(sessionCookie?.copy(count = sessionCookie.count + 1))
                call.respond(HttpStatusCode.OK, "Success! Hello, ${name}! Your visit count is ${visit}.")
            }
        }
    }
}

fun Application.registerAuthRoutes() {
    routing {
        registerRoute()
        loginRoute()
        logoutRoute()
        sessionRoute()
    }
}