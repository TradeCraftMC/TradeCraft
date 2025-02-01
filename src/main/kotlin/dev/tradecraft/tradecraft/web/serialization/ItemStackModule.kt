package dev.tradecraft.tradecraft.web.serialization

import com.fasterxml.jackson.databind.module.SimpleModule
import org.bukkit.inventory.ItemStack

class ItemStackModule : SimpleModule("ItemStackModule") {
    companion object {
        fun create(): SimpleModule {
            val module = SimpleModule("ItemStackModule")
            module.addSerializer(ItemStack::class.java, ItemStackSerializer())
            module.addDeserializer(ItemStack::class.java, ItemStackDeserializer())
            return module
        }
    }
}