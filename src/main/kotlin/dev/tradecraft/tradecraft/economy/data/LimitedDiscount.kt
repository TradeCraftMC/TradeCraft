package dev.tradecraft.tradecraft.economy.data

import jakarta.persistence.*
import java.util.Date

@Entity
class LimitedDiscount {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: String = ""

    @ManyToOne
    @JoinColumn(name = "listingId")
    val listing: Listing = Listing()

    val multiplier: Double = 1.0
    val ends: Long = Date().time
}
