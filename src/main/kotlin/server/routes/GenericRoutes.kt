package server.routes

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.genericRouting() {
    route("/") {
        get {
            call.respond(HttpStatusCode.OK);
        }
    }
}

fun Application.registerGenericRoutes() {
    routing {
        genericRouting()
    }
}