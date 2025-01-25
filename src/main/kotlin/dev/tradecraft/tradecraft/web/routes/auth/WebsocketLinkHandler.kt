package dev.tradecraft.tradecraft.web.routes.auth

import dev.tradecraft.tradecraft.TradeCraft
import dev.tradecraft.tradecraft.web.WebManager
import dev.tradecraft.tradecraft.web.abst.WebHandler
import io.undertow.websockets.WebSocketConnectionCallback
import io.undertow.websockets.core.WebSocketChannel
import io.undertow.websockets.core.WebSockets
import io.undertow.websockets.spi.WebSocketHttpExchange

// This handler is manually added in WebManager, because it's using websockets
class WebsocketLinkHandler : WebSocketConnectionCallback {
    override fun onConnect(exchange: WebSocketHttpExchange?, channel: WebSocketChannel?) {
        val code = TradeCraft.webManager.authenticationManager.linkManager.createLink({ session ->
            WebSockets.sendText("connect:${session.token}", channel, null)
        }, channel!!.sourceAddress.address)
        WebSockets.sendText("code:$code", channel, null);
    }
}