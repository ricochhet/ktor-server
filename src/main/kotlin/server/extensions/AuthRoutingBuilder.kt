package server.extensions

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

/**
 * Builds a route to match `POST` requests with specified [path]
 */
@ContextDsl
fun Route.postAuth(authLevel: String, path: String, body: PipelineInterceptor<Unit, ApplicationCall>): Route {
    return authenticate(authLevel) {
        route(path, HttpMethod.Post) { handle(body) }
    }
}

/**
 * Builds a route to match `POST` requests
 */
@ContextDsl
fun Route.postAuth(authLevel: String, body: PipelineInterceptor<Unit, ApplicationCall>): Route {
    return authenticate(authLevel) {
        method(HttpMethod.Post) { handle(body) }
    }
}

/**
 * Builds a route to match `HEAD` requests with specified [path]
 */
@ContextDsl
fun Route.headAuth(authLevel: String, path: String, body: PipelineInterceptor<Unit, ApplicationCall>): Route {
    return authenticate(authLevel) {
        route(path, HttpMethod.Head) { handle(body) }
    }
}

/**
 * Builds a route to match `HEAD` requests
 */
@ContextDsl
fun Route.headAuth(authLevel: String, body: PipelineInterceptor<Unit, ApplicationCall>): Route {
    return authenticate(authLevel) {
        method(HttpMethod.Head) { handle(body) }
    }
}

/**
 * Builds a route to match `PUT` requests with specified [path]
 */
@ContextDsl
fun Route.putAuth(authLevel: String, path: String, body: PipelineInterceptor<Unit, ApplicationCall>): Route {
    return authenticate(authLevel) {
        route(path, HttpMethod.Put) { handle(body) }
    }
}

/**
 * Builds a route to match `PUT` requests
 */
@ContextDsl
fun Route.putAuth(authLevel: String, body: PipelineInterceptor<Unit, ApplicationCall>): Route {
    return authenticate(authLevel) {
        method(HttpMethod.Put) { handle(body) }
    }
}

/**
 * Builds a route to match `PATCH` requests with specified [path]
 */
@ContextDsl
fun Route.patchAuth(authLevel: String, path: String, body: PipelineInterceptor<Unit, ApplicationCall>): Route {
    return authenticate(authLevel) {
        route(path, HttpMethod.Patch) { handle(body) }
    }
}

/**
 * Builds a route to match `PATCH` requests
 */
@ContextDsl
fun Route.patchAuth(authLevel: String, body: PipelineInterceptor<Unit, ApplicationCall>): Route {
    return authenticate(authLevel) {
        method(HttpMethod.Patch) { handle(body) }
    }
}

/**
 * Builds a route to match `DELETE` requests with specified [path]
 */
@ContextDsl
fun Route.deleteAuth(authLevel: String, path: String, body: PipelineInterceptor<Unit, ApplicationCall>): Route {
    return authenticate(authLevel) {
        route(path, HttpMethod.Delete) { handle(body) }
    }
}

/**
 * Builds a route to match `DELETE` requests
 */
@ContextDsl
fun Route.deleteAuth(authLevel: String, body: PipelineInterceptor<Unit, ApplicationCall>): Route {
    return authenticate(authLevel) {
        method(HttpMethod.Delete) { handle(body) }
    }
}

/**
 * Builds a route to match `OPTIONS` requests with specified [path]
 */
@ContextDsl
public fun Route.optionsAuth(authLevel: String, path: String, body: PipelineInterceptor<Unit, ApplicationCall>): Route {
    return authenticate(authLevel) {
        route(path, HttpMethod.Options) { handle(body) }
    }
}

/**
 * Builds a route to match `OPTIONS` requests
 */
@ContextDsl
public fun Route.optionsAuth(authLevel: String, body: PipelineInterceptor<Unit, ApplicationCall>): Route {
    return authenticate(authLevel) {
        method(HttpMethod.Options) { handle(body) }
    }
}