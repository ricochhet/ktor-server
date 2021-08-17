package database.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Users: IntIdTable() {
    val email = varchar("email", 255)
    val password = varchar("password", 255)
}

class UserEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserEntity>(Users)

    var email by Users.email
    var password by Users.password

    override fun toString(): String = "User($email, $password)"

    fun toUser() = User(id.value, email, password)
}

@Serializable
data class User(
    val id: Int = 0,
    val email: String,
    val password: String
)