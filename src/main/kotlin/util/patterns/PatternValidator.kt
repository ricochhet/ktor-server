package util.patterns

import util.patterns.Patterns.EMAIL_ADDRESS

fun String.isValidEmail(): Boolean {
    return this.isNotEmpty() && EMAIL_ADDRESS.matcher(this).matches()
}