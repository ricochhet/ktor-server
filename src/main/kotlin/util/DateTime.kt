package util

import java.text.SimpleDateFormat
import java.util.*

fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
    val sdfFormatter = SimpleDateFormat(format, locale)
    return sdfFormatter.format(this);
}

fun getCurrentDateTime(): Date {
    return Calendar.getInstance().time
}

const val standardDateFormat: String = "yyyy-MM-dd HH:mm:ss.SSS"