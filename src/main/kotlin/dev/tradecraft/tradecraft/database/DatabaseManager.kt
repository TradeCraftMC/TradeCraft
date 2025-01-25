package dev.tradecraft.tradecraft.database

import dev.tradecraft.tradecraft.TradeCraft
import dev.tradecraft.tradecraft.config.TradeCraftConfiguration
import dev.tradecraft.tradecraft.database.objects.User
import dev.tradecraft.tradecraft.database.objects.UserWallet
import dev.tradecraft.tradecraft.database.objects.VirtualPlayerInventoryState
import dev.tradecraft.tradecraft.economy.data.*
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.boot.MetadataSources
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import org.hibernate.cfg.Configuration
import java.util.function.Consumer

class DatabaseManager{
    private val sessionFactory: SessionFactory;
    private val classes: List<Class<*>> = listOf();

    init {
        val registry = StandardServiceRegistryBuilder().build()
        try {
            val metadataSources = MetadataSources(registry).addAnnotatedClasses(
                User::class.java, UserWallet::class.java, VirtualPlayerInventoryState::class.java,
                VendorProfile::class.java,
                Listing::class.java, LimitedDiscount::class.java, Currency::class.java,
            )
            val dbConfiguration = Configuration(metadataSources);

            dbConfiguration.setProperty("hibernate.connection.url", TradeCraft.configuration.getDatabaseUrl())
            dbConfiguration.setProperty("hibernate.dialect", TradeCraft.configuration.getDatabaseDialect())
            dbConfiguration.setProperty("hibernate.connection.username", TradeCraft.configuration.getDatabaseUsername())
            dbConfiguration.setProperty("hibernate.connection.password", TradeCraft.configuration.getDatabasePassword())

            dbConfiguration.setProperty("hibernate.hbm2ddl.auto", "update")

            sessionFactory = dbConfiguration.buildSessionFactory()
        } catch (e: Exception) {
            StandardServiceRegistryBuilder.destroy(registry);
            throw RuntimeException("Unable to initialize session factory", e);
        }
    }

    fun useDatabaseSession(func: Consumer<Session>) {
        sessionFactory.inTransaction { session ->
            func.accept(session)
        }
    }

    fun createVirtualInventory(playerUUID: String): VirtualPlayerInventoryState {
        val virtualPlayerInventory = VirtualPlayerInventoryState(playerUUID)
        sessionFactory.inTransaction { session ->
            session.persist(virtualPlayerInventory)
        }
        return virtualPlayerInventory
    }

    fun updateVirtualInventory(virtualPlayerInventoryState: VirtualPlayerInventoryState) {
        sessionFactory.inTransaction { session ->
            session.merge(virtualPlayerInventoryState)
        }
    }

    fun fetchVirtualInventory(playerUUID: String): VirtualPlayerInventoryState? {
        var inventory: VirtualPlayerInventoryState? = null;
        sessionFactory.inTransaction { session ->
            inventory = session.get(VirtualPlayerInventoryState::class.java, playerUUID)
        }
        return inventory
    }
}