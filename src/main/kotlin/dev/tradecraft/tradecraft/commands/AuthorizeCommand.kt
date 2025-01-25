package dev.tradecraft.tradecraft.commands

import dev.tradecraft.tradecraft.TradeCraft
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class AuthorizeCommand : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            return false
        }
        if (args.size != 1) {
            sender.spigot().sendMessage(
                TextComponent("[TradeCraft] ").color(ChatColor.GOLD), TextComponent("Usage: ").color(ChatColor.WHITE),
                TextComponent("/authorize <code>").color(ChatColor.GRAY)
            )
            return true
        }
        val code = args[0]
        TradeCraft.webManager.authenticationManager.linkManager.confirmLink(code, sender.uniqueId.toString())
        sender.spigot().sendMessage(
            TextComponent("[TradeCraft] ").color(ChatColor.GOLD), TextComponent("Authorized! ").color(ChatColor.WHITE)
        )

        return true
    }

    override fun onTabComplete(
        sender: CommandSender, command: Command, label: String, args: Array<out String>
    ): MutableList<String> {
        return mutableListOf()
    }
}

private fun TextComponent.color(color: ChatColor): TextComponent {
    this.color = color
    return this
}
