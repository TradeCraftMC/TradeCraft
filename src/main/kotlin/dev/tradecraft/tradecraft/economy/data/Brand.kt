package dev.tradecraft.tradecraft.economy.data

import jakarta.persistence.*


/*
    A single user can have exactly one brand - their personal brand.
    A company can have multiple brands
 */
@Entity
class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String = ""
    var name: String = ""

    @OneToMany(cascade = [(CascadeType.ALL)])
    val listings: MutableList<Listing> = emptyList<Listing>().toMutableList()
}
