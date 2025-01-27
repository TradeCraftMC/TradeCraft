package dev.tradecraft.tradecraft.commands

import dev.tradecraft.tradecraft.TradeCraft
import dev.tradecraft.tradecraft.database.objects.User
import dev.tradecraft.tradecraft.economy.data.Listing
import dev.tradecraft.tradecraft.economy.data.VendorProfile
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CreateVendorCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return false

        TradeCraft.databaseManager.useDatabaseSession {
            val user = User.fetchByPlayerUuid(sender);
            if (user.vendorProfile == null) {
                user.vendorProfile = VendorProfile()
            }
            if (sender.inventory.helmet != null) {
                val listing = Listing()
                listing.vendor = user.vendorProfile!!
                listing.stock = 1
                listing.contents.add(sender.inventory.helmet!!)


                user.vendorProfile!!.listings.add(listing)
            }

            it.merge(user)
        }

        return true;
    }
}