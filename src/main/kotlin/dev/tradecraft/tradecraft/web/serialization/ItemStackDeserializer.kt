package dev.tradecraft.tradecraft.web.serialization

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import dev.tradecraft.tradecraft.database.converters.ItemStackConverter
import org.bukkit.inventory.ItemStack

class ItemStackDeserializer : StdDeserializer<ItemStack>(ItemStack::class.java) {
    private val converter = ItemStackConverter();

    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): ItemStack? {
        if (p == null) return null;
        val node = p.codec.readTree<JsonNode>(p)
        val dataNode = node.get("data").asText() ?: return null
        val itemStack = converter.convertToEntityAttribute(dataNode)
        return itemStack;
    }
}