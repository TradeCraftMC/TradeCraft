package dev.tradecraft.tradecraft.database.objects

import dev.tradecraft.tradecraft.database.converters.ItemStackConverter
import jakarta.persistence.*
import org.bukkit.inventory.ItemStack

@Entity
@Table(name = "vpi_state")
class VirtualPlayerInventoryState() {
    constructor(playerUUID: String) : this() {
        this.playerUUID = playerUUID
    }

    @Id
    var playerUUID: String = ""

    @Convert(converter = ItemStackConverter::class)
    var armor: List<ItemStack> = emptyList()
    @Convert(converter = ItemStackConverter::class)
    var inventory: List<ItemStack> = emptyList()
    @Convert(converter = ItemStackConverter::class)
    var extra: List<ItemStack> = emptyList()

    var maxStackSize: Int = 64
}