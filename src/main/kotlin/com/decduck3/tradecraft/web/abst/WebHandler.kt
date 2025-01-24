package com.decduck3.tradecraft.web.abst

import io.undertow.io.Sender
import io.undertow.server.HttpServerExchange

interface WebHandler {
    fun handle(request: HttpServerExchange): HttpResponse
}