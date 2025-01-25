package dev.tradecraft.tradecraft.web.abst

import dev.tradecraft.tradecraft.database.objects.User
import io.undertow.io.Sender
import io.undertow.server.HttpServerExchange
import java.net.InetAddress

interface WebHandler {
    fun handle(request: HttpServerExchange, ip: InetAddress, user: User?): HttpResponse?
    fun handle(request: HttpServerExchange, relativeUrl: String, ip: InetAddress, user: User?): HttpResponse? = handle(request, ip, user)

}