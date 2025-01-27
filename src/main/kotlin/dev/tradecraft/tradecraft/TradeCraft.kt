package dev.tradecraft.tradecraft

import dev.tradecraft.tradecraft.commands.AuthorizeCommand
import dev.tradecraft.tradecraft.commands.CreateVendorCommand
import dev.tradecraft.tradecraft.config.TradeCraftConfiguration
import dev.tradecraft.tradecraft.database.DatabaseManager
import dev.tradecraft.tradecraft.database.converters.ItemStackConverter
import dev.tradecraft.tradecraft.virtualplayerinventory.VirtualPlayerInventoryListener
import dev.tradecraft.tradecraft.web.WebManager
import net.milkbowl.vault.permission.Permission
import org.bukkit.Material
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.RegisteredServiceProvider
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
        val createVendorCommand = CreateVendorCommand()
        getCommand("createvendor")!!.setExecutor(createVendorCommand)
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}
