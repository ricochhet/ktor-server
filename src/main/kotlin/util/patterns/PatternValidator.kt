package util.patterns

import util.logging.KotlinLogger
import util.patterns.Patterns.EMAIL_ADDRESS

fun String.isValidEmail(): Boolean {
    KotlinLogger.info("Valid email?: ${this.isNotEmpty() && EMAIL_ADDRESS.matcher(this).matches()}")
    return this.isNotEmpty() && EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isValidPassword(minLength: Int): Boolean {
    KotlinLogger.info("Valid password?: ${this.length >= minLength}")
    if (this.length >= minLength) {
        return true
    }

    return false
}

fun String.isMatchedString(default: String): Boolean {
    KotlinLogger.info("Matched string?: ${this == default}")
    if (this == default) {
        return true
    }

    return false
}