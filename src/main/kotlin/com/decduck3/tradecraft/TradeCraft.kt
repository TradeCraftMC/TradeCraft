package com.decduck3.tradecraft

import com.decduck3.tradecraft.config.TradeCraftConfiguration
import com.decduck3.tradecraft.database.DatabaseManager
import com.decduck3.tradecraft.web.WebManager
import org.bukkit.plugin.java.JavaPlugin

class TradeCraft : JavaPlugin() {

    private lateinit var databaseManager: DatabaseManager;
    private lateinit var configuration: TradeCraftConfiguration
    private lateinit var webManager: WebManager

    override fun onEnable() {
        this.saveDefaultConfig()
        configuration = TradeCraftConfiguration(this.config)

        logger.info("Connecting to database...");
        databaseManager = DatabaseManager(configuration)

        logger.info("Starting web server...")
        webManager = WebManager(configuration, logger)
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}
