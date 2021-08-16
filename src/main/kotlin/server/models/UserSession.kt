package server.models

import io.ktor.auth.*
import kotlinx.serialization.Serializable

@Serializable
data class UserSession(val name: String, val expiration: Long, val count: Int) : Principal