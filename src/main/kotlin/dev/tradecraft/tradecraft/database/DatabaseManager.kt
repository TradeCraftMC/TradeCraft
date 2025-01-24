package dev.tradecraft.tradecraft.database

import dev.tradecraft.tradecraft.config.TradeCraftConfiguration
import dev.tradecraft.tradecraft.database.objects.User
import dev.tradecraft.tradecraft.database.objects.VirtualPlayerInventoryState
import dev.tradecraft.tradecraft.economy.data.LimitedDiscount
import dev.tradecraft.tradecraft.economy.data.Listing
import dev.tradecraft.tradecraft.economy.data.VendorProfile
import org.hibernate.SessionFactory
import org.hibernate.boot.MetadataSources
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import org.hibernate.cfg.Configuration

class DatabaseManager(configuration: TradeCraftConfiguration) {
    private val sessionFactory: SessionFactory;
    private val configuration: TradeCraftConfiguration = configuration

    init {
        val registry = StandardServiceRegistryBuilder().build()
        try {
            val metadataSources = MetadataSources(registry).addAnnotatedClasses(
                User::class.java, VirtualPlayerInventoryState::class.java, VendorProfile::class.java,
                Listing::class.java, LimitedDiscount::class.java
            )
            val dbConfiguration = Configuration(metadataSources);

            dbConfiguration.setProperty("hibernate.connection.url", configuration.getDatabaseUrl())
            dbConfiguration.setProperty("hibernate.dialect", configuration.getDatabaseDialect())
            dbConfiguration.setProperty("hibernate.connection.username", configuration.getDatabaseUsername())
            dbConfiguration.setProperty("hibernate.connection.password", configuration.getDatabasePassword())

            dbConfiguration.setProperty("hibernate.hbm2ddl.auto", "update")

            sessionFactory = dbConfiguration.buildSessionFactory()
        } catch (e: Exception) {
            StandardServiceRegistryBuilder.destroy(registry);
            throw RuntimeException("Unable to initialize session factory", e);
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