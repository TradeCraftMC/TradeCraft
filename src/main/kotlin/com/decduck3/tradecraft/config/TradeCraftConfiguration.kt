package com.decduck3.tradecraft.config

import org.bukkit.configuration.file.FileConfiguration

class TradeCraftConfiguration(configuration: FileConfiguration) {
    private val configuration: FileConfiguration = configuration

    // Web UI
    fun getWebPort(): Int = configuration.getInt("web.port")
    fun getWebBaseUrl(): String = configuration.getString("web.baseUrl")!!

    // Database
    fun getDatabaseUrl(): String = configuration.getString("database.url").orEmpty()
    fun getDatabaseDialect(): String = configuration.getString("database.dialect").orEmpty()
    fun getDatabaseUsername(): String = configuration.getString("database.username").orEmpty()
    fun getDatabasePassword(): String = configuration.getString("database.password").orEmpty()

}