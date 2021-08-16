package main.extensions

import io.ktor.routing.*

fun isSessionRatelimited(visit: Int, limit: Int): Boolean {
    if (visit > limit) {
        return true
    }

    return false
}

fun isSessionExpired(expiration: Long): Boolean {
    if (System.currentTimeMillis() > expiration) {
        return true
    }

    return false
}