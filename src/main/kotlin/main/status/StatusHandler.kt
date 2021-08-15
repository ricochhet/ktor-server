package main.status

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*

fun StatusPages.Configuration.statusHandler(vararg code: HttpStatusCode) {
    status(*code) { status ->
        call.respondText("${status.value} ${status.description}")
    }
}