package dev.tradecraft.tradecraft.web.routes.auth

import dev.tradecraft.tradecraft.TradeCraft
import dev.tradecraft.tradecraft.database.objects.User
import dev.tradecraft.tradecraft.web.WebManager
import dev.tradecraft.tradecraft.web.abst.HttpResponse
import dev.tradecraft.tradecraft.web.abst.SimpleAPIResponse
import dev.tradecraft.tradecraft.web.abst.WebHandler
import dev.tradecraft.tradecraft.web.abst.WebRoute
import io.undertow.server.HttpServerExchange
import java.net.InetAddress
import java.util.function.Consumer

@WebRoute(path = "/api/v1/auth/link", method = "GET")
class LinkCheckHandler : WebHandler {
    override fun handle(
        request: HttpServerExchange, response: Consumer<HttpResponse?>, ip: InetAddress, user: User?
    ): Any? {
        val failResponse =
            HttpResponse(200, WebManager.serializeBody(SimpleAPIResponse(200, "Invalid or expired code", false)))
        val code = request.queryParameters["code"] ?: return response.accept(failResponse)

        val result = TradeCraft.webManager.authenticationManager.linkManager.checkLink(code.toString());
        return response.accept(
            if (result) HttpResponse(
                200, WebManager.serializeBody(SimpleAPIResponse(200, "Valid code", true))
            ) else failResponse
        )
    }
}