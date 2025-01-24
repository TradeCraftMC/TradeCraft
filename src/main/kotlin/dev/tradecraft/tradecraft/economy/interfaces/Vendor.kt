package dev.tradecraft.tradecraft.economy.interfaces

import dev.tradecraft.tradecraft.economy.data.Listing
import dev.tradecraft.tradecraft.economy.data.VendorProfile

interface Vendor {
    fun fetch(): List<Listing>
    fun profile(): VendorProfile
}