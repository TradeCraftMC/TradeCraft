package dev.tradecraft.tradecraft

import dev.tradecraft.tradecraft.config.TradeCraftConfiguration
import dev.tradecraft.tradecraft.database.DatabaseManager
import dev.tradecraft.tradecraft.database.converters.ItemStackConverter
import dev.tradecraft.tradecraft.virtualplayerinventory.VirtualPlayerInventoryListener
import dev.tradecraft.tradecraft.web.WebManager
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin

class TradeCraft : JavaPlugin() {
    companion object {
        lateinit var databaseManager: DatabaseManager;
        lateinit var configuration: TradeCraftConfiguration
        lateinit var webManager: WebManager
    }

    override fun onEnable() {
        this.saveDefaultConfig()
        configuration = TradeCraftConfiguration(this.config)

        logger.info("Connecting to database...");
        databaseManager = DatabaseManager(configuration)

        logger.info("Starting web server...")
        webManager = WebManager(configuration, logger)

        val virtualPlayerInventoryListener = VirtualPlayerInventoryListener()
        server.pluginManager.registerEvents(virtualPlayerInventoryListener, this)
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}
