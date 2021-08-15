package main.extensions

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*

/**
 * Builds a route to match `GET` requests with specified [path]
 */
@ContextDsl
fun Route.getAuth(authLevel: String, path: String, body: PipelineInterceptor<Unit, ApplicationCall>): Route {
    return authenticate(authLevel) {
        route(path, HttpMethod.Get) { handle(body) }
    }
}

/**
 * Builds a route to match `GET` requests
 */
@ContextDsl
fun Route.getAuth(authLevel: String, body: PipelineInterceptor<Unit, ApplicationCall>): Route {
    return authenticate(authLevel) {
        method(HttpMethod.Get) { handle(body) }
    }
}