package com.decduck3.tradecraft.database

import com.decduck3.tradecraft.config.TradeCraftConfiguration
import com.decduck3.tradecraft.database.objects.User
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
            val metadataSources = MetadataSources(registry).addAnnotatedClasses(User::class.java)
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
}