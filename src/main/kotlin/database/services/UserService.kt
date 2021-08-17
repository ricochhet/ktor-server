package database.services

import database.models.User
import database.models.UserEntity
import org.jetbrains.exposed.sql.transactions.transaction

class UserService {
    fun getAllUsers(): Iterable<User> = transaction {
        UserEntity.all().map(UserEntity::toUser)
    }

    fun addUser(user: User) = transaction {
        UserEntity.new {
            this.email = user.email
            this.password = user.password
        }
    }

    fun deleteUser(userId: Int) = transaction {
        UserEntity[userId].delete()
    }
}