package main.routes

import main.models.*
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.customerRouting() {
    route("/customer") {
        get {
            if (customerStorage.isNotEmpty()) {
                call.respond(customerStorage)
            } else {
                call.respond(HttpStatusCode.NotFound, "No customers found")
            }
        }
        get("{id}") {
            val id = call.parameters["id"] ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                "Missing or malformed id"
            )
            val customer =
                customerStorage.find { it.id == id } ?: return@get call.respond(
                    HttpStatusCode.NotFound,
                    "No customer with id $id"
                )
            call.respond(customer)
        }
        post {
            val customer = call.receive<Customer>()
            customerStorage.add(customer)
            call.respond(HttpStatusCode.Created, "Customer stored correctly")
        }
        delete("{id}") {
            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            if (customerStorage.removeIf { it.id == id }) {
                call.respond(HttpStatusCode.Accepted, "Customer removed correctly")
            } else {
                call.respond(HttpStatusCode.NotFound, "Not Found")
            }
        }
    }
}


fun Application.registerCustomerRoutes() {
    routing {
        customerRouting()
    }
}