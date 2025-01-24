package dev.tradecraft.tradecraft.virtualplayerinventory

import dev.tradecraft.tradecraft.TradeCraft
import dev.tradecraft.tradecraft.database.objects.VirtualPlayerInventoryState
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class VirtualPlayerInventoryListener : Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        if (!VirtualPlayerInventory.load(event.player)) {
            VirtualPlayerInventory.create(event.player)
        }
    }

    @EventHandler
    fun onPlayerLeave(event: PlayerQuitEvent) {
        val virtualPlayerInventory = VirtualPlayerInventory.fetch(event.player)
            ?: throw RuntimeException("Player left the server without a virtual inventory being created")
        virtualPlayerInventory.sync()

    }
}