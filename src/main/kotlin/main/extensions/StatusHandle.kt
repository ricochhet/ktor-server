package main.extensions

import util.*
import java.util.*

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.response.*

val date: Date = getCurrentDateTime()
val dateInString: String = date.toString(standardDateFormat)
val ROOT_MSG = """
    Welcome to the API server.
    ------------------------------------
    SysTime : $dateInString
""".trimIndent()

fun StatusPages.Configuration.statusHandler(vararg code: HttpStatusCode) {
    status(*code) { status ->
        call.respond("${status.value} ${status.description}")
    }
}