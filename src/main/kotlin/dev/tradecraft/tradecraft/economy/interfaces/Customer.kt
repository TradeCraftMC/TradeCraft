package dev.tradecraft.tradecraft.economy.interfaces

import dev.tradecraft.tradecraft.economy.data.Listing

interface Customer : Wallet {
    fun purchase(listing: Listing)
}