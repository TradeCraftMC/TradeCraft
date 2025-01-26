package dev.tradecraft.tradecraft.web.routes.auth

import dev.tradecraft.tradecraft.database.objects.User
import dev.tradecraft.tradecraft.web.WebManager
import dev.tradecraft.tradecraft.web.abst.HttpResponse
import dev.tradecraft.tradecraft.web.abst.WebHandler
import dev.tradecraft.tradecraft.web.abst.WebRoute
import io.undertow.server.HttpServerExchange
import java.net.InetAddress
import java.util.function.Consumer

@WebRoute(path = "/api/v1/auth/fetch", method = "GET")
class SelfFetchHandler : WebHandler {
    override fun handle(
        request: HttpServerExchange, response: Consumer<HttpResponse?>, ip: InetAddress, user: User?
    ) {
        if (user == null) return response.accept(HttpResponse(403))
        return response.accept(HttpResponse(200, WebManager.serializeBody(user)))
    }
}