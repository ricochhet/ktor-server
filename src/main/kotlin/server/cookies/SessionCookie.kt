package server.cookies

import io.ktor.auth.*
import kotlinx.serialization.Serializable

@Serializable
data class SessionCookie(val name: String, val count: Int, val roles: Set<String> = emptySet()) : Principal
data class OriginalRequestURI(val uri: String)