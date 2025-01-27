package dev.tradecraft.tradecraft.database.objects

import dev.tradecraft.tradecraft.TradeCraft
import dev.tradecraft.tradecraft.economy.data.Listing
import dev.tradecraft.tradecraft.economy.data.VendorProfile
import dev.tradecraft.tradecraft.economy.data.Currency
import dev.tradecraft.tradecraft.economy.interfaces.Customer
import jakarta.persistence.*
import org.bukkit.entity.Player
import org.hibernate.annotations.ColumnDefault

@Entity
class User : Customer {
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
    var vendorProfile: VendorProfile? = null

    override fun purchase(listing: Listing) {
        TODO("Not yet implemented")
    }

    override fun withdraw(amount: Int, currency: Currency): Boolean {
        TODO("Not yet implemented")
    }

    override fun deposit(amount: Int, currency: Currency): Boolean {
        TODO("Not yet implemented")
    }
}