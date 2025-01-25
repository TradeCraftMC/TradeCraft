package dev.tradecraft.tradecraft.web

import com.google.gson.GsonBuilder
import dev.tradecraft.tradecraft.TradeCraft
import dev.tradecraft.tradecraft.web.abst.WebHandler
import dev.tradecraft.tradecraft.web.abst.WebRoute
import dev.tradecraft.tradecraft.web.auth.AuthenticationManager
import dev.tradecraft.tradecraft.web.routes.auth.WebsocketLinkHandler
import io.github.classgraph.ClassGraph
import io.undertow.Handlers
import io.undertow.Undertow
import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange
import io.undertow.util.Headers
import org.springframework.security.web.util.matcher.IpAddressMatcher
import java.net.InetAddress
import java.nio.ByteBuffer
import kotlin.io.path.Path

class WebManager : HttpHandler {
    private val server: Undertow
    private val routeHandlers: HashMap<String, HashMap<String, WebHandler>> = HashMap()
    private val fallbackHandler: FallbackHandler = FallbackHandler()
    private val trustedProxies: List<IpAddressMatcher> =
        TradeCraft.configuration.getTrustedProxies().map { IpAddressMatcher(it) }
    val authenticationManager = AuthenticationManager()

    companion object {
        val webGson = GsonBuilder().create()

        fun serializeBody(obj: Any): ByteArray = webGson.toJson(obj).encodeToByteArray()
    }

    init {
        // Lookup and find all the handlers
        val possibleRoutes =
            ClassGraph().acceptPackages("dev.tradecraft.tradecraft.web.routes").enableAnnotationInfo().enableClassInfo()
                .scan()
        possibleRoutes.use {
            val routes = it.getClassesWithAnnotation(WebRoute::class.java)
                .filter { i -> i.implementsInterface(WebHandler::class.java) }

            routes.forEach {
                val annotation = it.getAnnotationInfo(WebRoute::class.java)
                val method = annotation.parameterValues[0].value.toString()
                val path = annotation.parameterValues[1].value.toString()

                val handler = it.loadClass().getConstructor().newInstance() as WebHandler
                routeHandlers.getOrPut(path) { HashMap() }[method] = handler
                TradeCraft.logger.info("Registered web handler with name of " + handler.javaClass.name)
            }
        }

        val handler = Handlers.path().addPrefixPath("/api/v1/auth/link-ws", Handlers.websocket(WebsocketLinkHandler()))
            .addPrefixPath(TradeCraft.configuration.getWebBaseUrl(), this)

        server =
            Undertow.builder().addHttpListener(TradeCraft.configuration.getWebPort(), "0.0.0.0").setHandler(handler)
                .build()
        server.start()
        TradeCraft.logger.info("Web server started on 0.0.0.0:" + TradeCraft.configuration.getWebPort())
    }

    override fun handleRequest(exchange: HttpServerExchange?) {
        if (exchange == null) {
            return
        }
        val canonicalPath = Path(exchange.requestPath).normalize().toString()
        val relativeUrl =
            "/${canonicalPath.substring(TradeCraft.configuration.getWebBaseUrl().length).trimStart('/').trimEnd(('/'))}"
        val method = exchange.requestMethod.toString().uppercase()

        val handler = routeHandlers[relativeUrl]?.get(method) ?: fallbackHandler

        var sourceAddress = exchange.sourceAddress.address

        val forwardForHeader =
            exchange.requestHeaders.find { it.headerName == Headers.X_FORWARDED_FOR }?.getOrNull(0)
        if (forwardForHeader != null) {
            val matches = trustedProxies.find { it.matches(sourceAddress.toString()) } != null
            if (matches) {
                sourceAddress = InetAddress.getByName(forwardForHeader)
            }
        }

        val response = handler.handle(exchange, relativeUrl, sourceAddress, null)

        if (response != null) {
            exchange.responseCode = response.code;
            if (response.body != null) {
                exchange.responseSender.send(ByteBuffer.wrap(response.body));
            }
            if (response.headers != null) {
                exchange.responseHeaders.putAll(response.headers)
            }
        }

        exchange.responseSender.close();
    }
}