package dev.tradecraft.tradecraft.web.routes.currency

import dev.tradecraft.tradecraft.database.objects.User
import dev.tradecraft.tradecraft.economy.data.Currency
import dev.tradecraft.tradecraft.web.abst.WebRoute
import dev.tradecraft.tradecraft.web.utils.CrudHandler
import jakarta.persistence.Id
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.hasAnnotation

@WebRoute(path = "/api/v1/currency", method = "DELETE")
@WebRoute(path = "/api/v1/currency", method = "PATCH")
@WebRoute(path = "/api/v1/currency", method = "POST")
@WebRoute(path = "/api/v1/currency", method = "GET")
class CurrencyCRUDHandler : CrudHandler<Currency>(Currency::class) {
    override fun authenticate(user: User?, method: String): Boolean {
        if (method == "GET") return true
        return user != null && user.admin
    }

    override fun checkBlacklist(user: User?, field: KMutableProperty<*>): Boolean = true
}