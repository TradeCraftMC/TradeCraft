package dev.tradecraft.tradecraft.economy.data

import dev.tradecraft.tradecraft.database.converters.MaterialConverter
import dev.tradecraft.tradecraft.web.utils.CrudValidator
import jakarta.persistence.*
import org.bukkit.Material

@Entity
class Currency : CrudValidator<Currency> {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private val id: String = ""

    @Column(unique = true)
    var identifier: String = ""

    @Column(unique = true)
    var name: String = ""

    @Column(unique = true)
    var sign: String = ""

    var baseValue: Double = 1.0
    var canConvert: Boolean = true

    var backer: CurrencyBacker = CurrencyBacker.Fiat

    @Convert(converter = MaterialConverter::class)
    var backingMaterial: Material? = null

    fun convertTo(amount: Int, target: Currency): Int? {
        if (!canConvert) {
            return null
        }
        val ratio = this.baseValue / target.baseValue
        val newAmount = ratio * amount
        return newAmount.toInt()
    }

    // Need an identifier, and a material if the CurrencyBacker is Material
    override fun validate(value: Currency): String? {
        if (identifier == "") return "Identifier must not be empty"
        if (identifier.length != 3) return "Identifier must be 3 characters long"
        if (name == "") return "Name must not be empty"
        if (sign == "") return "Sign must not be empty"
        if (sign.length != 1) return "Sign must be a single character"
        if (backer == CurrencyBacker.Material && backingMaterial == null) return "Material currency backer requires backingMaterial"

        return null;
    }

}