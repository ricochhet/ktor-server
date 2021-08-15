package main.status

import util.*
import java.util.*

val date: Date = getCurrentDateTime()
val dateInString: String = date.toString(standardDateFormat)

val ROOT_MSG = """
    Welcome to the API server.
    ------------------------------------
    SysTime : ${main.status.dateInString}
""".trimIndent()