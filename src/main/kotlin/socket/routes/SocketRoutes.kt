package socket.routes

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.websocket.*
import socket.models.Connection
import java.lang.Exception
import java.util.*

fun Route.socketRouting(path: String) {
    val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())
    // We don't define a definitive route since this functions more so as a "template."
    webSocket(path) {
        val thisConnection = Connection(this)
        connections += thisConnection
        try {
            // We cannot only send a string, the webSocket also requires us to send a frame (see below)
            send("Connection established.")

            for (frame in incoming) {
                frame as? Frame.Text ?: continue
                val received = frame.readText()
                val message = "SOCKET_${thisConnection.currentSocketID}: $received"

                connections.forEach {
                    it.session.send(message)
                }
            }
        } catch (e: Exception) {
            println(e.localizedMessage)
        } finally {
            connections -= thisConnection
        }
    }
}

fun Application.registerSocketRoutes(path: String, forceAuth: Boolean = false, authLevel: String = "auth-session") {
    routing {
        if (forceAuth) {
            authenticate(authLevel) {
                socketRouting(path)
            }
        } else {
            socketRouting(path)
        }
    }
}