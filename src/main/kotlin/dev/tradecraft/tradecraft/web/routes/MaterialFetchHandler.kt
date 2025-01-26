package dev.tradecraft.tradecraft.web.routes

import dev.tradecraft.tradecraft.database.objects.User
import dev.tradecraft.tradecraft.web.WebManager
import dev.tradecraft.tradecraft.web.abst.HttpResponse
import dev.tradecraft.tradecraft.web.abst.WebHandler
import dev.tradecraft.tradecraft.web.abst.WebRoute
import io.undertow.server.HttpServerExchange
import org.bukkit.Material
import java.net.InetAddress
import java.util.function.Consumer

@WebRoute(path = "/api/v1/materials", method = "GET")
class MaterialFetchHandler :WebHandler {
    override fun handle(
        request: HttpServerExchange, response: Consumer<HttpResponse?>, ip: InetAddress, user: User?
    ) {
        val entries = Material.entries.map { it.name }
        response.accept(HttpResponse.createJSONResponse(200, entries))
    }
}