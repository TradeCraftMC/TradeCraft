package dev.tradecraft.tradecraft.web.abst

import dev.tradecraft.tradecraft.database.objects.User
import io.undertow.io.Sender
import io.undertow.server.HttpServerExchange
import java.net.InetAddress
import java.util.function.Consumer
import java.util.function.Supplier

interface WebHandler {
    fun handle(request: HttpServerExchange, response: Consumer<HttpResponse?>, ip: InetAddress, user: User?): Any?
    fun handle(
        request: HttpServerExchange, response: Consumer<HttpResponse?>, relativeUrl: String, ip: InetAddress,
        user: User?
    ): Any? = handle(request, response, ip, user)

}