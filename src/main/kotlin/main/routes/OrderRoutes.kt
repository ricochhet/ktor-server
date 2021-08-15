package main.routes

import main.models.orderStorage
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.listOrdersRoute() {
    get("/order") {
        if (orderStorage.isNotEmpty()) {
            call.respond(orderStorage)
        }
    }
}

fun Route.getOrderRoute() {
    get("/order/{id}") {
        val id = call.parameters["id"] ?: return@get call.respondText("Bad Request", status = HttpStatusCode.BadRequest)
        val order = orderStorage.find { it.number == id } ?: return@get call.respondText(
            "Not Found",
            status = HttpStatusCode.NotFound
        )
        call.respond(order)
    }
}

fun Route.totalizeOrderRoute() {
    get("/order/{id}/total") {
        val id = call.parameters["id"] ?: return@get call.respondText("Bad Request", status = HttpStatusCode.BadRequest)
        val order = orderStorage.find { it.number == id } ?: return@get call.respondText(
            "Not Found",
            status = HttpStatusCode.NotFound
        )
        val total = order.contents.map { it.price * it.amount }.sum()
        call.respond(total)
    }
}

fun Application.registerOrderRoutes(authType: String = "auth-basic") {
    routing {
        authenticate(authType) {
            listOrdersRoute()
            getOrderRoute()
            totalizeOrderRoute()
        }
    }
}