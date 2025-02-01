package dev.tradecraft.tradecraft.commands

import dev.tradecraft.tradecraft.TradeCraft
import dev.tradecraft.tradecraft.database.objects.User
import dev.tradecraft.tradecraft.economy.data.Listing
import dev.tradecraft.tradecraft.economy.data.Brand
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CreateBrandCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return false

        TradeCraft.databaseManager.useDatabaseSession {
            val user = User.fetchByPlayerUuid(sender);
            if (user.brand == null) {
                user.brand = Brand()
            }
            if (sender.inventory.helmet != null) {
                val listing = Listing()
                listing.vendor = user.brand!!
                listing.stock = 1
                listing.contents.add(sender.inventory.helmet!!)


                user.brand!!.listings.add(listing)
            }

            it.merge(user)
        }

        return true;
    }
}