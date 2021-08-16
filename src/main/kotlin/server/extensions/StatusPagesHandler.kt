package server.extensions

import util.*
import java.util.*

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*

val date: Date = getCurrentDateTime()
val dateInString: String = date.toString(standardDateFormat)
val ROOT_MSG = "SysTime : $dateInString"

fun StatusPages.Configuration.statusHandler(vararg code: HttpStatusCode) {
    status(*code) { status ->
        call.respond(status, "${status.value} ${status.description}")
    }
}