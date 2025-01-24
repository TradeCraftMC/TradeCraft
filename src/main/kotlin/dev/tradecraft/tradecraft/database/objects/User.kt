package dev.tradecraft.tradecraft.database.objects

import dev.tradecraft.tradecraft.economy.data.Listing
import dev.tradecraft.tradecraft.economy.data.VendorProfile
import dev.tradecraft.tradecraft.economy.interfaces.Currency
import dev.tradecraft.tradecraft.economy.interfaces.Customer
import jakarta.persistence.*

@Entity
@Table(name = "users")
class User : Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private val id: String = "";

    @OneToOne(cascade = [(CascadeType.ALL)])
    private val vendorProfile: VendorProfile? = null


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