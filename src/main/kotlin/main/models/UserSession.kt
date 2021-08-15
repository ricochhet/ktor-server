package main.models

import io.ktor.auth.*
import kotlinx.serialization.Serializable

@Serializable
data class UserSession(val name: String, val count: Int) : Principal