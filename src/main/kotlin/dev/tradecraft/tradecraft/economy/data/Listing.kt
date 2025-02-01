package dev.tradecraft.tradecraft.economy.data

import dev.tradecraft.tradecraft.database.converters.ItemStackConverter
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
    @JoinColumn(name = "vendorId")
    var vendor: Brand = Brand() // The vendor selling this item

    @JdbcTypeCode(SqlTypes.JSON)
    var costs: HashMap<Int, Pair<Int, Currency>> = HashMap() // Purchase threshold to particular unit cost

    var stock: Int = 0 // Remaining stock

    @Convert(converter = ItemStackConverter::class)
    val contents: MutableList<ItemStack> = emptyList<ItemStack>().toMutableList() // What's in a single purchase

    @OneToMany(cascade = [CascadeType.ALL])
    var discounts: MutableList<LimitedDiscount> = emptyList<LimitedDiscount>().toMutableList()
}
