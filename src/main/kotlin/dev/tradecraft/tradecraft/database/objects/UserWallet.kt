package dev.tradecraft.tradecraft.database.objects

import dev.tradecraft.tradecraft.TradeCraft
import dev.tradecraft.tradecraft.economy.data.Currency
import dev.tradecraft.tradecraft.economy.interfaces.Wallet
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
class UserWallet : Wallet {
    @Id
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private val user: User? = null

    private var amount: Int = 0

    @ManyToOne
    @JoinColumn(name = "currency_id")
    private val currency: Currency? = null


    override fun withdraw(amount: Int, currency: Currency): Boolean {
        if (this.currency == null) {
            return false
        }

        val removeAmount = if (currency == this.currency) amount else currency.convertTo(amount, this.currency)
        if (this.amount >= removeAmount) {
            this.amount -= removeAmount
            TradeCraft.databaseManager.useDatabaseSession { session ->
                session.merge(this)
            }
            return true
        } else {
            return false
        }
    }

    override fun deposit(amount: Int, currency: Currency): Boolean {
        if (this.currency == null) {
            return false
        }

        val addAmount = if (currency == this.currency) amount else currency.convertTo(amount, this.currency)
        this.amount += addAmount
        TradeCraft.databaseManager.useDatabaseSession { session ->
            session.merge(this)
        }
        return true
    }
}