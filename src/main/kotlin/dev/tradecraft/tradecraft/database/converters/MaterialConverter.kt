package dev.tradecraft.tradecraft.database.converters

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import org.bukkit.Material
import org.bukkit.util.io.BukkitObjectOutputStream
import java.io.ByteArrayOutputStream
import java.util.*

@Converter
class MaterialConverter : AttributeConverter<Material, String> {
    override fun convertToDatabaseColumn(attribute: Material?): String? {
        if(attribute == null) {
            return null;
        }

        return attribute.name
    }

    override fun convertToEntityAttribute(dbData: String?): Material? {
        if(dbData == null) {
            return null
        }

        return Material.valueOf(dbData)
    }
}