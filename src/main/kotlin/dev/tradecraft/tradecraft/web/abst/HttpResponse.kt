package dev.tradecraft.tradecraft.web.abst

import dev.tradecraft.tradecraft.web.WebManager
import io.undertow.util.HeaderMap
import io.undertow.util.Headers
import java.net.http.HttpHeaders
import java.nio.ByteBuffer

data class HttpResponse(val code: Int, val body: ByteArray? = null, val headers: HeaderMap? = null) {
    companion object {
        fun createRedirect(location: String): HttpResponse {
            return HttpResponse(302, headers = HeaderMap().add(Headers.LOCATION, location))
        }

        fun createJSONResponse(code: Int, body: Any): HttpResponse {
            return HttpResponse(
                code, WebManager.serializeBody(body),
                HeaderMap().add(Headers.CONTENT_TYPE, "application/json")
            )
        }
    }
}
