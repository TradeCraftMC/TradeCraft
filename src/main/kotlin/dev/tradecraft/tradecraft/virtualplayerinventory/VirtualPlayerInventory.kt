package dev.tradecraft.tradecraft.virtualplayerinventory

import dev.tradecraft.tradecraft.TradeCraft
import dev.tradecraft.tradecraft.database.objects.VirtualPlayerInventoryState
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import java.util.HashMap
import java.util.function.Function
import java.util.function.Supplier

class VirtualPlayerInventory(state: VirtualPlayerInventoryState, player: OfflinePlayer) {
    private val state: VirtualPlayerInventoryState = state;
    private val player: OfflinePlayer = player;

    companion object {
        fun create(player: Player): VirtualPlayerInventory {
            val state = TradeCraft.databaseManager.createVirtualInventory(player.uniqueId.toString())
            val inventory = VirtualPlayerInventory(state, player);
            inventory.sync()
            return inventory;
        }

        fun load(player: Player): Boolean {
            val state = TradeCraft.databaseManager.fetchVirtualInventory(player.uniqueId.toString()) ?: return false
            val virtualPlayerInventory = VirtualPlayerInventory(state, player);

            println(state)

            val onlinePlayer = player.player;
            if (onlinePlayer != null) {
                val playerInventory = onlinePlayer.inventory;
                virtualPlayerInventory.load(playerInventory)
                onlinePlayer.updateInventory()
            }

            return true;
        }

        fun fetch(player: Player): VirtualPlayerInventory? {
            val state = TradeCraft.databaseManager.fetchVirtualInventory(player.uniqueId.toString()) ?: return null
            val virtualPlayerInventory = VirtualPlayerInventory(state, player);
            return virtualPlayerInventory
        }
    }

    fun sync() {
        val onlinePlayer = player.player ?: return;
        val inventory = onlinePlayer.inventory;

        state.armor = inventory.armorContents.toList();
        state.inventory = inventory.storageContents.toList();
        state.extra = inventory.extraContents.toList();
        state.maxStackSize = inventory.maxStackSize;

        println(state.inventory.size)

        TradeCraft.databaseManager.updateVirtualInventory(state)
    }

    fun load(inventory: PlayerInventory) {
        inventory.setArmorContents(state.armor.toTypedArray());
        inventory.storageContents = state.inventory.toTypedArray()
        inventory.setExtraContents(state.extra.toTypedArray());
        inventory.maxStackSize = state.maxStackSize;
    }

    private fun <T> virtualSwitch(online: Function<Player, T>, offline: Supplier<T>): T {
        val onlinePlayer = player.player;
        if (onlinePlayer != null) {
            return online.apply(onlinePlayer)
        }
        return offline.get()
    }
}