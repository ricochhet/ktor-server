package database.services

import database.models.ROLE_USER
import database.models.User
import database.models.UserEntity
import database.models.Users
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import util.crypto.sha256
import util.logging.KotlinLogger

class UserService {
    fun getAllUsers(): Iterable<User> = transaction {
        KotlinLogger.info("Executed 'getAllUsers' at 'UserService'")
        UserEntity.all().map(UserEntity::toUser)
    }

    fun addUser(user: User) = transaction {
        runCatching {
            KotlinLogger.info("Executed 'addUser($user)' at 'UserService'")
            UserEntity.new {
                this.email = user.email.sha256()
                this.password = user.password.sha256()
                this.roles = ROLE_USER
                this.createdAt = System.currentTimeMillis().toString()
            }
        }.getOrElse {
            KotlinLogger.error(it.toString())
            return@getOrElse null
        }
    }

    fun deleteUser(userId: Int) = transaction {
        runCatching {
            KotlinLogger.info("Executed 'deleteUser($userId)' at 'UserService'")
            UserEntity[userId].delete()
        }.getOrElse {
            KotlinLogger.error(it.toString())
            return@getOrElse null
        }
    }

    fun getUser(userId: Int) = transaction {
        runCatching {
            KotlinLogger.info("Executed 'getUser($userId)' at 'UserService'")
            return@runCatching UserEntity[userId].toUser()
        }.getOrElse {
            KotlinLogger.error(it.toString())
            return@getOrElse null
        }
    }

    fun findUserByEmail(userEmail: String) = transaction {
        runCatching {
            KotlinLogger.info("Executed 'findUserByEmail($userEmail)' at 'UserService'")
            return@runCatching Users.select { Users.email eq userEmail }.single()
        }.getOrElse {
            KotlinLogger.error(it.toString())
            return@getOrElse null
        }
    }
}