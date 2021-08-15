package main.routes

import main.status.ROOT_MSG

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.genericRouting() {
    route("/") {
        get {
            call.respondText(ROOT_MSG);
        }
    }
}

fun Application.registerGenericRoutes() {
    routing {
        genericRouting()
    }
}