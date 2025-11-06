package me.spartacus04.instantrestock.commands

import me.spartacus04.colosseum.ColosseumPlugin
import me.spartacus04.colosseum.commandHandling.argument.arguments.ArgumentBoolean
import me.spartacus04.colosseum.commandHandling.argument.arguments.ArgumentString
import me.spartacus04.colosseum.commandHandling.command.ColosseumBaseCommand
import me.spartacus04.colosseum.commandHandling.command.ColosseumCommand
import me.spartacus04.colosseum.commandHandling.command.ColosseumNestedCommand
import me.spartacus04.colosseum.commandHandling.exceptions.MalformedArgumentException
import me.spartacus04.colosseum.i18n.sendI18nConfirm
import me.spartacus04.instantrestock.InstantRestock.Companion.CONFIG
import org.bukkit.command.CommandSender

class ConfigCommand(plugin: ColosseumPlugin) : ColosseumNestedCommand(plugin, "config", listOf(
    object : ColosseumCommand(plugin) {
        override val commandData = commandDescriptor("maxTrades") {
            arguments.add(ArgumentString(listOf("infinite", "1000", "100")))
        }

        override fun execute(ctx: CommandContext<CommandSender>) {
            val str = ctx.getArgument<String>(0).trim()

            if(str == "infinite") {
                CONFIG.MAX_TRADES = Int.MAX_VALUE
                CONFIG.save()

                ctx.sender.sendI18nConfirm(plugin, "config-saved")
            } else if (str.toIntOrNull() != null && str.toInt() > 0) {
                CONFIG.MAX_TRADES = str.toInt()
                CONFIG.save()

                ctx.sender.sendI18nConfirm(plugin, "config-saved")
            } else {
                throw MalformedArgumentException(str, "infinite or a positive number")
            }
        }
    },
    object : ColosseumCommand(plugin) {
        override val commandData = commandDescriptor("disablePricePenalty") {
            arguments.add(ArgumentBoolean())
        }

        override fun execute(ctx: CommandContext<CommandSender>) {
            val value = ctx.getArgument<Boolean>(0)

            CONFIG.DISABLE_PRICE_PENALTY = value
            CONFIG.save()

            ctx.sender.sendI18nConfirm(plugin, "config-saved")
        }
    },
    object : ColosseumCommand(plugin) {
        override val commandData = commandDescriptor("allowTravellingMerchants") {
            arguments.add(ArgumentBoolean())
        }

        override fun execute(ctx: CommandContext<CommandSender>) {
            val value = ctx.getArgument<Boolean>(0)

            CONFIG.ALLOW_TRAVELLING_MERCHANTS = value
            CONFIG.save()

            ctx.sender.sendI18nConfirm(plugin, "config-saved")
        }
    },
    object : ColosseumCommand(plugin) {
        override val commandData = commandDescriptor("uninstallMode") {
            arguments.add(ArgumentBoolean())
        }

        override fun execute(ctx: CommandContext<CommandSender>) {
            val value = ctx.getArgument<Boolean>(0)

            CONFIG.UNINSTALL_MODE = value
            CONFIG.save()

            ctx.sender.sendI18nConfirm(plugin, "config-saved")
        }
    },
    object : ColosseumBaseCommand(plugin, "villagerBlackList", listOf(
        object : ColosseumCommand(plugin) {
            override val commandData = commandDescriptor("add") {
                val remainingVillagers = villagerList.map { it.lowercase() }

                arguments.add(ArgumentString(remainingVillagers))
            }

            override fun execute(ctx: CommandContext<CommandSender>) {
                val profession = ctx.getArgument<String>(0).uppercase()

                if(!CONFIG.VILLAGER_BLACKLIST.contains(profession) && villagerList.contains(profession)) {
                    CONFIG.VILLAGER_BLACKLIST.add(profession)
                    CONFIG.save()

                    ctx.sender.sendI18nConfirm(plugin, "config-saved")
                } else {
                    throw MalformedArgumentException(ctx.getArgument<String>(0), "not already blacklisted profession")
                }
            }
        },
        object : ColosseumCommand(plugin) {
            override val commandData = commandDescriptor("remove") {
                val blacklistedVillagers = villagerList.map { it.lowercase() }

                arguments.add(ArgumentString(blacklistedVillagers))
            }

            override fun execute(ctx: CommandContext<CommandSender>) {
                val profession = ctx.getArgument<String>(0).uppercase()

                if(CONFIG.VILLAGER_BLACKLIST.contains(profession) && villagerList.contains(profession)) {
                    CONFIG.VILLAGER_BLACKLIST.remove(profession)
                    CONFIG.save()

                    ctx.sender.sendI18nConfirm(plugin, "config-saved")
                } else {
                    throw MalformedArgumentException(ctx.getArgument<String>(0), "already blacklisted profession")
                }
            }
        },
        object : ColosseumCommand(plugin) {
            override val commandData = commandDescriptor("list") {  }

            override fun execute(ctx: CommandContext<CommandSender>) {
                ctx.sender.sendMessage(CONFIG.VILLAGER_BLACKLIST.joinToString(", "))
            }
        }
    )) {}
)) {
    override val commandData = commandDescriptor("config") {
        permissions = setOf("instantrestock.config")
    }

    companion object {
        val villagerList = listOf(
            "ARMORER", "BUTCHER", "CARTOGRAPHER", "CLERIC", "FARMER", "FISHERMAN",
            "FLETCHER", "LEATHERWORKER", "LIBRARIAN", "MASON", "SHEPHERD", "TOOLSMITH",
            "WEAPONSMITH"
        )
    }
}