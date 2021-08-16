package socket.models

import io.ktor.http.cio.websocket.*
import java.util.concurrent.atomic.*

class Connection(val session: DefaultWebSocketSession) {
    companion object {
        var lastKnownID = AtomicInteger(0)
    }

    val currentSocketID: Int = lastKnownID.getAndIncrement()
}