package dev.tradecraft.tradecraft.web.routes

import dev.tradecraft.tradecraft.database.objects.User
import dev.tradecraft.tradecraft.web.abst.HttpResponse
import dev.tradecraft.tradecraft.web.abst.WebHandler
import dev.tradecraft.tradecraft.web.abst.WebRoute
import io.undertow.server.HttpServerExchange
import java.net.InetAddress

@WebRoute(path = "/api/v1", method = "GET")
class HelloRoute  : WebHandler {
    override fun handle(request: HttpServerExchange, ip: InetAddress, user: User?): HttpResponse {
        return HttpResponse(200)
    }
}