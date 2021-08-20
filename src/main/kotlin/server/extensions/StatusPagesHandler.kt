package server.extensions

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import util.logging.KotlinLogger

fun StatusPages.Configuration.statusHandler(vararg code: HttpStatusCode) {
    status(*code) { status ->
        KotlinLogger.info("StatusPages statusHandler() -> ${status.value} ${status.description}")
        call.respond(status, "${status.value} ${status.description}")
    }
}