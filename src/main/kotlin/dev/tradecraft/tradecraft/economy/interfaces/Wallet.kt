package dev.tradecraft.tradecraft.economy.interfaces

import dev.tradecraft.tradecraft.economy.data.Currency

interface Wallet {
    // Withdraw an amount of a given currency
    fun withdraw(amount: Int, currency: Currency): Boolean
    // Deposit an amount of a given currency
    fun deposit(amount: Int, currency: Currency): Boolean
}