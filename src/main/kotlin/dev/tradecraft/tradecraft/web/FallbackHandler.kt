package dev.tradecraft.tradecraft.web

import dev.tradecraft.tradecraft.TradeCraft
import dev.tradecraft.tradecraft.database.objects.User
import dev.tradecraft.tradecraft.web.abst.HttpResponse
import dev.tradecraft.tradecraft.web.abst.WebHandler
import io.undertow.server.HttpServerExchange
import java.net.InetAddress

class FallbackHandler : WebHandler {
    // Unused due to use overriding a higher up function
    override fun handle(request: HttpServerExchange, ip: InetAddress, user: User?): HttpResponse? = null

    override fun handle(request: HttpServerExchange, relativeUrl: String, ip: InetAddress, user: User?): HttpResponse {
        val resourcePath = "web$relativeUrl";
        val resource = TradeCraft::class.java.classLoader.getResource(resourcePath) ?: return HttpResponse(404)
        val resourceContent = resource.readBytes()

        return HttpResponse(200, resourceContent)
    }
}