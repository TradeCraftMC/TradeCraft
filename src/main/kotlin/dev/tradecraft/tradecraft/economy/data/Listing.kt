package dev.tradecraft.tradecraft.economy.data

import dev.tradecraft.tradecraft.database.converters.ItemStackConverter
import dev.tradecraft.tradecraft.economy.interfaces.Currency
import jakarta.persistence.*
import org.bukkit.inventory.ItemStack
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
class Listing {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: String = ""

    @ManyToOne
    @JoinColumn(name = "vendor_id")
    val vendor: VendorProfile = VendorProfile() // The vendor selling this item

    @JdbcTypeCode(SqlTypes.JSON)
    val costs: HashMap<Int, Pair<Int, Currency>> = HashMap() // Purchase threshold to particular unit cost

    val stock: Int = 0 // Remaining stock

    @Convert(converter = ItemStackConverter::class)
    val contents: List<ItemStack> = emptyList() // What's in a single purchase

    @OneToMany(cascade = [CascadeType.ALL])
    val discounts: List<LimitedDiscount> = emptyList()
}
