package dev.tradecraft.tradecraft.web.routes.auth

import dev.tradecraft.tradecraft.database.objects.User
import dev.tradecraft.tradecraft.web.WebManager
import dev.tradecraft.tradecraft.web.abst.HttpResponse
import dev.tradecraft.tradecraft.web.abst.WebHandler
import dev.tradecraft.tradecraft.web.abst.WebRoute
import io.undertow.server.HttpServerExchange
import java.net.InetAddress

@WebRoute(path = "/api/v1/auth/fetch", method = "GET")
class SelfFetchHandler : WebHandler {
    override fun handle(request: HttpServerExchange, ip: InetAddress, user: User?): HttpResponse? {
        if (user == null) return HttpResponse(403)
        return HttpResponse(200, WebManager.serializeBody(user))
    }
}