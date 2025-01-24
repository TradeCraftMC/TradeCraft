package dev.tradecraft.tradecraft.economy.interfaces

interface Currency {
    fun convertTo(amount: Int, currency: Currency): Int
}