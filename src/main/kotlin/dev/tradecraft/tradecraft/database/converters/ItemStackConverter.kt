package dev.tradecraft.tradecraft.database.converters

import com.google.gson.Gson
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.*

@Converter
class ItemStackConverter : AttributeConverter<ItemStack, String> {
    override fun convertToDatabaseColumn(itemStack: ItemStack?): String? {
        if (itemStack == null) {
            return null;
        }

        val stream = ByteArrayOutputStream();
        BukkitObjectOutputStream(stream).use {
            it.writeObject(itemStack);
        }
        return Base64.getEncoder().encodeToString(stream.toByteArray())
    }

    override fun convertToEntityAttribute(binary: String?): ItemStack? {
        if (binary == null) {
            return null;
        }

        val bytes = Base64.getDecoder().decode(binary);
        BukkitObjectInputStream(ByteArrayInputStream(bytes)).use {
            return it.readObject() as ItemStack
        }
    }
}