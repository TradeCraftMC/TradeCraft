package dev.tradecraft.tradecraft.web.auth

import dev.tradecraft.tradecraft.TradeCraft
import dev.tradecraft.tradecraft.database.objects.User
import java.net.InetAddress
import java.util.*
import kotlin.collections.HashMap

class SessionManager {
    data class Session(val user: User, val expiry: Long, val token: String, val sessionIp: InetAddress)

    private val sessions: HashMap<String, Session> = HashMap()

    fun createSession(user: User, sessionIp: InetAddress): Session {
        val sessionToken = UUID.randomUUID().toString()

        // 31 days
        val expiry = System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 31)

        val session = Session(user, expiry, sessionToken, sessionIp)
        sessions[sessionToken] = session
        return session
    }

    fun fetchSession(token: String): User? {
        val session = sessions[token] ?: return null

        val currentTime = System.currentTimeMillis()
        if (session.expiry < currentTime) {
            sessions.remove(token)
            return null
        }

        return session.user
    }

    fun getSessions(user: User): List<Session> = sessions.values.toList().filter { session -> session.user == user }
}