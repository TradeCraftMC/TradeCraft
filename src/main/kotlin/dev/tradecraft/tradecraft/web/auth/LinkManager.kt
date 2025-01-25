package dev.tradecraft.tradecraft.web.auth

import dev.tradecraft.tradecraft.TradeCraft
import java.net.InetAddress
import java.util.*
import java.util.function.Consumer
import kotlin.collections.HashMap

class LinkManager {

    data class LinkData(var playerUuid: String?, val callback: Consumer<SessionManager.Session>, val expiry: Long, val sessionIp: InetAddress)

    private val linkCodes: HashMap<String, LinkData> = HashMap()

    fun createLink(callback: Consumer<SessionManager.Session>, sessionIp: InetAddress): String {
        val code = UUID.randomUUID().toString().substring(0, 6).uppercase();
        val expiry = System.currentTimeMillis() + (1000 * 60 * 60)
        linkCodes[code] = LinkData(null, callback, expiry, sessionIp);
        return code
    }

    fun confirmLink(code: String, playerUuid: String): Boolean {
        val linkData = linkCodes[code.uppercase()] ?: return false
        if (linkData.playerUuid != null) {
            return false
        }

        linkData.playerUuid = playerUuid
        val session = TradeCraft.webManager.authenticationManager.createPlayerSession(playerUuid, linkData.sessionIp)
        linkData.callback.accept(session)
        return true
    }

    fun checkLink(code: String): Boolean {
        return linkCodes.containsKey(code)
    }

    fun cleanExpiredCodes() {
        val currentTime = System.currentTimeMillis()
        for (code in linkCodes.keys) {
            val linkData = linkCodes[code]!!
            if (linkData.expiry < currentTime) {
                linkCodes.remove(code)
            }
        }
    }
}