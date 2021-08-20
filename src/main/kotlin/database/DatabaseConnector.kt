package database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import database.models.Users
import io.ktor.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import util.logging.KotlinLogger

fun Application.initializeDatabase() {
    val dbConfig = HikariConfig("./src/main/resources/hikari.properties")
    val dataSource = HikariDataSource(dbConfig)

    Database.connect(dataSource)
    createDatabaseTables()
    KotlinLogger.info("Initialized Database")
}

private fun createDatabaseTables() = transaction {
    SchemaUtils.create(Users)
    KotlinLogger.info("Database: create table 'users'")
}