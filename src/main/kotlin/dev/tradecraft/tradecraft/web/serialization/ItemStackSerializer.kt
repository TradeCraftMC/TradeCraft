package dev.tradecraft.tradecraft.web.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import dev.tradecraft.tradecraft.database.converters.ItemStackConverter
import org.bukkit.inventory.ItemStack

class ItemStackSerializer : StdSerializer<ItemStack>(ItemStack::class.java) {
    private val converter = ItemStackConverter();
    override fun serialize(value: ItemStack?, gen: JsonGenerator?, provider: SerializerProvider?) {
        if(gen == null) return;
        if(value == null) return gen.writeNull();
        gen.writeStartObject()
        gen.writeStringField("data", converter.convertToDatabaseColumn(value));
        gen.writeStringField("material", value.data?.itemType.toString())
        gen.writeStringField("name", value.itemMeta?.displayName)

        gen.writeArrayFieldStart("enchantments")
        for((enchantment, level) in value.enchantments){
            gen.writeStartObject()
            gen.writeStringField("enchantment", enchantment.translationKey)
            gen.writeNumberField("level", level)
            gen.writeEndObject()
        }
        gen.writeEndArray()

        gen.writeEndObject()
    }
}