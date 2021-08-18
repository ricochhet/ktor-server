package database.services

import database.models.User
import database.models.UserEntity
import database.models.Users
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import util.crypto.sha256

class UserService {
    fun getAllUsers(): Iterable<User> = transaction {
        UserEntity.all().map(UserEntity::toUser)
    }

    fun addUser(user: User) = transaction {
        runCatching {
            UserEntity.new {
                this.email = user.email.sha256()
                this.password = user.password.sha256()
            }
        }.getOrElse {
            return@getOrElse null
        }
    }

    fun deleteUser(userId: Int) = transaction {
        runCatching {
            UserEntity[userId].delete()
        }.getOrElse {
            return@getOrElse null
        }
    }

    fun findUserByEmail(userEmail: String) = transaction {
        runCatching {
            return@runCatching Users.select { Users.email eq userEmail }.single()
        }.getOrElse {
            return@getOrElse null
        }
    }
}