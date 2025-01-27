package dev.tradecraft.tradecraft.economy.data

import dev.tradecraft.tradecraft.economy.interfaces.Vendor
import jakarta.persistence.*

@Entity
class VendorProfile : Vendor {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String = ""

    @OneToMany(cascade = [(CascadeType.ALL)])
    val listings: MutableList<Listing> = emptyList<Listing>().toMutableList()

    override fun fetch(): List<Listing> = listings
    override fun profile(): VendorProfile = this
}
