package dev.tradecraft.tradecraft.web.auth

import dev.tradecraft.tradecraft.TradeCraft
import dev.tradecraft.tradecraft.database.objects.User
import java.net.InetAddress

class AuthenticationManager {
    val sessionManager: SessionManager = SessionManager()
    val linkManager: LinkManager = LinkManager()

    private fun fetchOrCreateUser(playerUuid: String): User {
        var user: User? = null
        TradeCraft.databaseManager.useDatabaseSession { session ->
            user = session.createSelectionQuery("from User where playerUUID = :playerUUID", User::class.java)
                .setParameter("playerUUID", playerUuid).resultList.firstOrNull();
            if (user == null) {
                user = User()
                user!!.playerUUID = playerUuid
                session.persist(user)
            }
        }
        if (user == null) {
            throw RuntimeException("Failed to create user - what happened?")
        }
        return user!!
    }

    fun createPlayerSession(playerUuid: String, sessionIp: InetAddress): SessionManager.Session {
        val user = fetchOrCreateUser(playerUuid)
        val session = sessionManager.createSession(user, sessionIp)
        return session
    }
}