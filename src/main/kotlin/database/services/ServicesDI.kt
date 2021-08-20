package database.services

import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton
import util.logging.KotlinLogger

fun DI.MainBuilder.bindServices() {
    KotlinLogger.info("Kodein: DI Builder bindServices()")
    bind<UserService>() with singleton { UserService() }
}