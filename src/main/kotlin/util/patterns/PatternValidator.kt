package util.patterns

import util.patterns.Patterns.EMAIL_ADDRESS

fun String.isValidEmail(): Boolean {
    return this.isNotEmpty() && EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isValidPassword(minLength: Int): Boolean {
    if (this.length >= minLength) {
        return true
    }

    return false
}