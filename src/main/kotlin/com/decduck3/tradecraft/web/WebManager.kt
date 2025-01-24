package com.decduck3.tradecraft.web

import com.decduck3.tradecraft.config.TradeCraftConfiguration
import com.decduck3.tradecraft.web.abst.WebHandler
import com.decduck3.tradecraft.web.abst.WebRoute
import io.github.classgraph.ClassGraph
import io.undertow.Handlers
import io.undertow.Undertow
import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange
import java.nio.ByteBuffer
import java.util.logging.Logger

class WebManager(configuration: TradeCraftConfiguration, logger: Logger) : HttpHandler {
    private val configuration: TradeCraftConfiguration = configuration
    private val logger: Logger = logger
    private val server: Undertow
    private val routeHandlers: HashMap<String, HashMap<String, WebHandler>> = HashMap()
    private val fallbackHandler: FallbackHandler = FallbackHandler()

    init {

        // Lookup and find all the handlers
        val possibleRoutes =
            ClassGraph().acceptPackages("com.decduck3.tradecraft.web.routes").enableAnnotationInfo().enableClassInfo()
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
        val relativeUrl = "/${exchange.requestPath.substring(configuration.getWebBaseUrl().length).trimStart('/')}"
        val method = exchange.requestMethod.toString().uppercase()

        val handler = routeHandlers[relativeUrl]?.get(method) ?: fallbackHandler

        val response = handler.handle(exchange)

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