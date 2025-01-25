package dev.tradecraft.tradecraft.database.objects

import dev.tradecraft.tradecraft.economy.data.Listing
import dev.tradecraft.tradecraft.economy.data.VendorProfile
import dev.tradecraft.tradecraft.economy.data.Currency
import dev.tradecraft.tradecraft.economy.interfaces.Customer
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
class User : Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String = "";
    var playerUUID: String = ""

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