package dev.tradecraft.tradecraft.web

import dev.tradecraft.tradecraft.TradeCraft
import dev.tradecraft.tradecraft.web.abst.HttpResponse
import dev.tradecraft.tradecraft.web.abst.WebHandler
import io.undertow.server.HttpServerExchange

class FallbackHandler : WebHandler {
    override fun handle(request: HttpServerExchange, relativeUrl: String): HttpResponse {
        val resourcePath = "web$relativeUrl";
        val resource = TradeCraft::class.java.classLoader.getResource(resourcePath) ?: return HttpResponse(404)
        val resourceContent = resource.readBytes()

        return HttpResponse(200, resourceContent)
    }
}