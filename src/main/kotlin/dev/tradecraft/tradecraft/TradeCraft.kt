package dev.tradecraft.tradecraft

import dev.tradecraft.tradecraft.commands.AuthorizeCommand
import dev.tradecraft.tradecraft.config.TradeCraftConfiguration
import dev.tradecraft.tradecraft.database.DatabaseManager
import dev.tradecraft.tradecraft.database.converters.ItemStackConverter
import dev.tradecraft.tradecraft.virtualplayerinventory.VirtualPlayerInventoryListener
import dev.tradecraft.tradecraft.web.WebManager
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Logger

class TradeCraft : JavaPlugin() {
    companion object {
        lateinit var databaseManager: DatabaseManager;
        lateinit var configuration: TradeCraftConfiguration
        lateinit var webManager: WebManager
        lateinit var logger: Logger
    }

    override fun onEnable() {
        TradeCraft.logger = logger

        this.saveDefaultConfig()
        configuration = TradeCraftConfiguration(this.config)

        logger.info("Connecting to database...");
        databaseManager = DatabaseManager()

        logger.info("Starting web server...")
        webManager = WebManager()

        val virtualPlayerInventoryListener = VirtualPlayerInventoryListener()
        server.pluginManager.registerEvents(virtualPlayerInventoryListener, this)

        val authorizeCommand = AuthorizeCommand()
        getCommand("authorize")!!.setExecutor(authorizeCommand)
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}
