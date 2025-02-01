package dev.tradecraft.tradecraft.database.objects

import dev.tradecraft.tradecraft.TradeCraft
import dev.tradecraft.tradecraft.economy.data.Brand
import dev.tradecraft.tradecraft.economy.data.Listing
import dev.tradecraft.tradecraft.economy.data.Currency
import dev.tradecraft.tradecraft.economy.interfaces.Customer
import dev.tradecraft.tradecraft.economy.interfaces.Vendor
import dev.tradecraft.tradecraft.web.utils.InputError
import dev.tradecraft.tradecraft.web.utils.RPCValidator
import jakarta.persistence.*
import org.bukkit.entity.Player
import org.hibernate.annotations.ColumnDefault

@Entity
class User : Customer, Vendor, RPCValidator {
    companion object {
        fun fetchByPlayerUuid(player: Player): User {
            return TradeCraft.webManager.authenticationManager.fetchOrCreateUser(player.uniqueId.toString())
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String = "";
    var playerUUID: String = ""
    var name: String = ""

    @ColumnDefault(value = "false")
    var admin: Boolean = false

    @OneToOne(cascade = [(CascadeType.ALL)])
    var brand: Brand? = null

    override fun purchase(listing: Listing) {
        TODO("Not yet implemented")
    }

    override fun withdraw(amount: Int, currency: Currency): Boolean {
        TODO("Not yet implemented")
    }

    override fun deposit(amount: Int, currency: Currency): Boolean {
        TODO("Not yet implemented")
    }

    // Vendor logic
    override fun fetchBrands(): List<Brand> = if (this.brand != null) listOf(this.brand!!) else emptyList()

    override fun createBrand(brand: Brand): String = throw InputError("Cannot create a brand object on user")

    override fun removeBrand(brandId: String) = throw InputError("Cannot remove a brand object on user")

    // RPC logic
    override fun validate(fieldName: String, user: User?): Boolean = true // user?.id.equals(this.id)
}