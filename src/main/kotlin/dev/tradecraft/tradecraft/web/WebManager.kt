package dev.tradecraft.tradecraft.web

import dev.tradecraft.tradecraft.config.TradeCraftConfiguration
import dev.tradecraft.tradecraft.web.abst.WebHandler
import dev.tradecraft.tradecraft.web.abst.WebRoute
import io.github.classgraph.ClassGraph
import io.undertow.Handlers
import io.undertow.Undertow
import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange
import java.io.File
import java.nio.ByteBuffer
import java.util.logging.Logger
import kotlin.io.path.Path
import kotlin.math.log

class WebManager(configuration: TradeCraftConfiguration, logger: Logger) : HttpHandler {
    private val configuration: TradeCraftConfiguration = configuration
    private val logger: Logger = logger
    private val server: Undertow
    private val routeHandlers: HashMap<String, HashMap<String, WebHandler>> = HashMap()
    private val fallbackHandler: FallbackHandler = FallbackHandler()

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
                logger.info("Registered web handler with name of " + handler.javaClass.name)
            }
        }

        val handler = Handlers.path().addPrefixPath(configuration.getWebBaseUrl(), this)

        server = Undertow.builder().addHttpListener(configuration.getWebPort(), "0.0.0.0").setHandler(handler).build()
        server.start()
        logger.info("Web server started on 0.0.0.0:" + configuration.getWebPort())
    }

    override fun handleRequest(exchange: HttpServerExchange?) {
        if (exchange == null) {
            return
        }
        val canonicalPath = Path(exchange.requestPath).normalize().toString()
        val relativeUrl = "/${canonicalPath.substring(configuration.getWebBaseUrl().length).trimStart('/')}"
        val method = exchange.requestMethod.toString().uppercase()

        val handler = routeHandlers[relativeUrl]?.get(method) ?: fallbackHandler

        val response = handler.handle(exchange, relativeUrl)

        exchange.responseCode = response.code;
        if (response.body != null) {
            exchange.responseSender.send(ByteBuffer.wrap(response.body));
        }
        if (response.headers != null) {
            exchange.responseHeaders.putAll(response.headers)
        }
        exchange.responseSender.close();

    }
}