package dev.tradecraft.tradecraft.web

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectReader
import com.fasterxml.jackson.databind.ObjectWriter
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module
import dev.tradecraft.tradecraft.TradeCraft
import dev.tradecraft.tradecraft.database.objects.User
import dev.tradecraft.tradecraft.web.abst.WebHandler
import dev.tradecraft.tradecraft.web.abst.WebRoute
import dev.tradecraft.tradecraft.web.abst.WebRoutePrefix
import dev.tradecraft.tradecraft.web.auth.AuthenticationManager
import dev.tradecraft.tradecraft.web.routes.auth.WebsocketLinkHandler
import dev.tradecraft.tradecraft.web.serialization.ItemStackModule
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
    private val routePrefixHandlers: HashMap<String, WebHandler> = HashMap()
    private val fallbackHandler: FallbackHandler = FallbackHandler()
    private val trustedProxies: List<IpAddressMatcher> =
        TradeCraft.configuration.getTrustedProxies().map { IpAddressMatcher(it) }
    val authenticationManager = AuthenticationManager()

    companion object {
        private val mapper =
            ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).registerModule(Hibernate6Module())
                .registerModules(ItemStackModule.create())
        val webWriter: ObjectWriter = mapper.writer()
        val webReader: ObjectReader = mapper.reader()

        fun serializeBody(obj: Any): ByteArray {
            try {
                return webWriter.writeValueAsString(obj).encodeToByteArray()
            } catch (e: Exception) {
                e.printStackTrace()
                throw e;
            }
        }
    }

    init {
        // Lookup and find all the handlers
        val possibleRoutes =
            ClassGraph().acceptPackages("dev.tradecraft.tradecraft.web.routes").enableAnnotationInfo().enableClassInfo()
                .scan()
        possibleRoutes.use {
            val routes = it.getClassesWithAnyAnnotation(WebRoute::class.java, WebRoutePrefix::class.java)
                .filter { i -> i.implementsInterface(WebHandler::class.java) }

            routes.forEach {
                val handler = it.loadClass().getConstructor().newInstance() as WebHandler

                val annotations = it.getAnnotationInfoRepeatable(WebRoute::class.java)
                for (annotation in annotations) {
                    val method = annotation.parameterValues[0].value.toString()
                    val path = annotation.parameterValues[1].value.toString()

                    routeHandlers.getOrPut(path) { HashMap() }[method] = handler
                }

                val annotation = it.getAnnotationInfo(WebRoutePrefix::class.java)
                if (annotation != null) {
                    val prefix = annotation.parameterValues[0].value.toString()
                    routePrefixHandlers.put(prefix, handler)
                }

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

        val handler = routeHandlers[relativeUrl]?.get(method) ?: routePrefixHandlers.filter {
            relativeUrl.startsWith(
                it.key
            )
        }.values.firstOrNull() ?: fallbackHandler

        var sourceAddress = exchange.sourceAddress.address

        var user: User? = null
        if (exchange.requestHeaders.headerNames.contains(Headers.AUTHORIZATION)) {
            val authorization = exchange.requestHeaders.get(Headers.AUTHORIZATION)[0].split(" ")
            if (authorization.size == 2) {
                val type = authorization[0]
                when (type) {
                    "Session" -> {
                        val token = authorization[1]
                        user = authenticationManager.sessionManager.fetchSession(token)
                    }
                }
            }
        }

        if (exchange.requestHeaders.headerNames.contains(Headers.X_FORWARDED_FOR)) {
            val forwardForHeader = exchange.requestHeaders.get(Headers.X_FORWARDED_FOR)[0]
            val matches = trustedProxies.find { it.matches(sourceAddress.toString()) } != null
            if (matches) {
                sourceAddress = InetAddress.getByName(forwardForHeader.toString())
            }
        }

        var handled = false;
        handler.handle(exchange, { response ->
            if (response != null) {
                if (handled) {
                    TradeCraft.logger.warning(
                        "response.accept called more than once, skipping response (${response.code} ${
                            response.body?.let {
                                java.lang.String(
                                    it
                                )
                            }
                        })")
                    return@handle;
                }
                handled = true
                exchange.responseCode = response.code;
                if (response.headers != null) {
                    exchange.responseHeaders.putAll(response.headers)
                }
                if (response.body != null) {
                    exchange.responseSender.send(ByteBuffer.wrap(response.body));
                }
            }

            exchange.responseSender.close();
        }, relativeUrl, sourceAddress, user)
    }
}