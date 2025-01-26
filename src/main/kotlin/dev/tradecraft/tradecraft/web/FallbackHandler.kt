package dev.tradecraft.tradecraft.web

import dev.tradecraft.tradecraft.TradeCraft
import dev.tradecraft.tradecraft.database.objects.User
import dev.tradecraft.tradecraft.web.abst.HttpResponse
import dev.tradecraft.tradecraft.web.abst.WebHandler
import io.undertow.server.HttpServerExchange
import java.net.InetAddress
import java.util.function.Consumer

class FallbackHandler : WebHandler {
    // Unused due to use overriding a higher up function
    override fun handle(
        request: HttpServerExchange, response: Consumer<HttpResponse?>, ip: InetAddress, user: User?
    ) {
    };

    override fun handle(
        request: HttpServerExchange, response: Consumer<HttpResponse?>, relativeUrl: String, ip: InetAddress,
        user: User?
    ) {
        val resourcePath = "web$relativeUrl";
        val resource =
            TradeCraft::class.java.classLoader.getResource(resourcePath) ?: return response.accept(HttpResponse(404))
        val resourceContent = resource.readBytes()

        response.accept(HttpResponse(200, resourceContent))
    }
}