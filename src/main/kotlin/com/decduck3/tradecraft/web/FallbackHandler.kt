package com.decduck3.tradecraft.web

import com.decduck3.tradecraft.web.abst.HttpResponse
import com.decduck3.tradecraft.web.abst.WebHandler
import io.undertow.server.HttpServerExchange

class FallbackHandler : WebHandler {
    override fun handle(request: HttpServerExchange): HttpResponse {
        return HttpResponse(404)
    }
}