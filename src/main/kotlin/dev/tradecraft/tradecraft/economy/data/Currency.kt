package dev.tradecraft.tradecraft.economy.data

import dev.tradecraft.tradecraft.database.converters.MaterialConverter
import jakarta.persistence.*
import org.bukkit.Material

@Entity
class Currency {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private val id: String = ""
    private val baseValue: Double = 1.0

    private val backer: CurrencyBacker = CurrencyBacker.Fiat

    @Convert(converter = MaterialConverter::class)
    private val backingMaterial: Material? = null

    fun convertTo(amount: Int, target: Currency): Int {
        val ratio = this.baseValue / target.baseValue
        val newAmount = ratio * amount
        return newAmount.toInt()
    }
}