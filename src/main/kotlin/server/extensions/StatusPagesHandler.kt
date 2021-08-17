package server.extensions

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*

fun StatusPages.Configuration.statusHandler(vararg code: HttpStatusCode) {
    status(*code) { status ->
        call.respond(status, "${status.value} ${status.description}")
    }
}