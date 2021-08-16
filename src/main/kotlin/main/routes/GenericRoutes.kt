package main.routes

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*

import server.extensions.ROOT_MSG

fun Route.genericRouting() {
    route("/") {
        get {
            call.respond(ROOT_MSG);
        }
    }
}

fun Application.registerGenericRoutes() {
    routing {
        genericRouting()
    }
}