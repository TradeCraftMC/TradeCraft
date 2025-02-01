package dev.tradecraft.tradecraft

import dev.tradecraft.tradecraft.commands.AuthorizeCommand
import dev.tradecraft.tradecraft.commands.CreateBrandCommand
import dev.tradecraft.tradecraft.config.TradeCraftConfiguration
import dev.tradecraft.tradecraft.database.DatabaseManager
import dev.tradecraft.tradecraft.virtualplayerinventory.VirtualPlayerInventoryListener
import dev.tradecraft.tradecraft.web.WebManager
import net.milkbowl.vault.permission.Permission
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Logger

class TradeCraft : JavaPlugin() {
    companion object {
        lateinit var databaseManager: DatabaseManager;
        lateinit var configuration: TradeCraftConfiguration
        lateinit var webManager: WebManager
        lateinit var logger: Logger
        lateinit var permissionManager: Permission
    }

    override fun onEnable() {
        TradeCraft.logger = logger

        permissionManager = server.servicesManager.getRegistration(Permission::class.java)!!.provider

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
        val createBrandCommand = CreateBrandCommand()
        getCommand("createbrand")!!.setExecutor(createBrandCommand)
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}
