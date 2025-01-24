package dev.tradecraft.tradecraft.economy.data

import dev.tradecraft.tradecraft.economy.interfaces.Vendor
import jakarta.persistence.*

@Entity
class VendorProfile : Vendor {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: String = ""

    @OneToMany(cascade = [(CascadeType.ALL)])
    val listings: List<Listing> = emptyList()

    override fun fetch(): List<Listing> = listings
    override fun profile(): VendorProfile = this
}
