package util.logging

import io.ktor.application.*
import org.slf4j.LoggerFactory

object KotlinLogger {
    fun info(message: String) {
        return LoggerFactory.getLogger(Application::class.simpleName).info(message)
    }

    fun warn(message: String) {
        return LoggerFactory.getLogger(Application::class.simpleName).warn(message)
    }

    fun error(message: String) {
        return LoggerFactory.getLogger(Application::class.simpleName).error(message)
    }
}