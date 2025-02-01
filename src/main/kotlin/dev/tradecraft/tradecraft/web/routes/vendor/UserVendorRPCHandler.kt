package dev.tradecraft.tradecraft.web.routes.vendor

import dev.tradecraft.tradecraft.database.objects.User
import dev.tradecraft.tradecraft.economy.interfaces.Vendor
import dev.tradecraft.tradecraft.web.abst.WebRoutePrefix
import dev.tradecraft.tradecraft.web.utils.RPCHandler

@WebRoutePrefix("/api/v1/user/vendor")
class UserVendorRPCHandler : RPCHandler<Vendor, User>(Vendor::class.java, User::class.java) {
}