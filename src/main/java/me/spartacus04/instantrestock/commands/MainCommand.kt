package me.spartacus04.instantrestock.commands

import me.spartacus04.instantrestock.InstantRestockState.CONFIG
import me.spartacus04.instantrestock.InstantRestockState.I18N
import me.spartacus04.instantrestock.config.FieldLanguageMode
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

internal class MainCommand : CommandExecutor, TabCompleter {
    private val villagerList = listOf(
        "ARMORER", "BUTCHER", "CARTOGRAPHER", "CLERIC", "FARMER", "FISHERMAN",
        "FLETCHER", "LEATHERWORKER", "LIBRARIAN", "MASON", "SHEPHERD", "TOOLSMITH",
        "WEAPONSMITH"
    )

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        try {
            when(args[0]) {
                "reload" -> {
                    if(sender.hasPermission("instantrestock.reload")) {
                        sender.sendMessage(
                            I18N.messageFormatter.info(I18N.getFormatted(sender, "reloading"))
                        )
                        CONFIG.read()

                        if(CONFIG.LANG == FieldLanguageMode.DEFAULT)
                            I18N.loadLanguagesFromResources()
                        else
                            I18N.customMode = true

                        sender.sendMessage(
                            I18N.messageFormatter.info(I18N.getFormatted(sender, "reloaded"))
                        )
                    } else {
                        sender.sendMessage(
                            I18N.messageFormatter.error(I18N.getFormatted(sender, "no-permission"))
                        )
                    }
                }
                "config" -> {
                    if(sender.hasPermission("instantrestock.config")) {
                        when(args[1]) {
                            "maxTrades" -> {
                                if(args[2] == "infinite") {
                                    CONFIG.MAX_TRADES = Integer.MAX_VALUE
                                    CONFIG.save()
                                }
                                else if (args[2].toIntOrNull() != null) {
                                    CONFIG.MAX_TRADES = args[2].toInt()
                                    CONFIG.save()
                                }
                                else {
                                    sender.sendMessage("§cInvalid value")
                                }
                            }
                            "disablePricePenalty" -> {
                                CONFIG.DISABLE_PRICE_PENALTY = args[2].lowercase().toBooleanStrict()
                                CONFIG.save()
                            }
                            "uninstallMode" -> {
                                CONFIG.UNINSTALL_MODE = args[2].lowercase().toBooleanStrict()
                                CONFIG.save()
                            }
                            "allowTravellingMerchants" -> {
                                CONFIG.ALLOW_TRAVELLING_MERCHANTS = args[2].lowercase().toBooleanStrict()
                                CONFIG.save()
                            }
                            "VILLAGER_BLACKLIST" -> {
                                when(args[2]) {
                                    "add" -> {
                                        if(villagerList.contains(args[3].uppercase()) && !CONFIG.VILLAGER_BLACKLIST.contains(args[3].uppercase())) {
                                            CONFIG.VILLAGER_BLACKLIST.add(args[3].uppercase())
                                            CONFIG.save()
                                        }
                                    }
                                    "remove" -> {
                                        if(villagerList.contains(args[3].uppercase()) && CONFIG.VILLAGER_BLACKLIST.contains(args[3].uppercase())) {
                                            CONFIG.VILLAGER_BLACKLIST.remove(args[3].uppercase())
                                            CONFIG.save()
                                        }
                                    }
                                    "list" -> {
                                        sender.sendMessage(CONFIG.VILLAGER_BLACKLIST.joinToString { ", " })
                                    }
                                }
                            }
                        }

                        sender.sendMessage(
                            I18N.messageFormatter.info(I18N.getFormatted(sender, "config-updated"))
                        )
                    } else {
                        sender.sendMessage(
                            I18N.messageFormatter.error(I18N.getFormatted(sender, "no-permission"))
                        )
                    }
                }
            }
        }
        catch (e: Exception) {
            println(e)
            sender.sendMessage(
                I18N.messageFormatter.error(I18N.getFormatted(sender, "invalid-arguments"))
            )
        }

        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String> {
        val list = ArrayList<String>()

        when(args.size) {
            1 -> {
                if(sender.hasPermission("instantrestock.reload"))
                    list.add("reload")

                if(sender.hasPermission("instantrestock.config"))
                    list.add("config")
            }
            2 -> {
                if(args[0] == "config") {
                    list.addAll(listOf(
                        "maxTrades",
                        "villagerBlacklist",
                        "disablePricePenalty",
                        "uninstallMode",
                        "allowTravellingMerchants",
                        "lang"
                    ))
                }
            }
            3 -> {
                if(args[0] == "config") {
                    when(args[1]) {
                        "maxTrades" -> {
                            list.addAll(listOf("infinite", "1000", "100"))
                        }
                        "villagerBlacklist" -> {
                            list.addAll(listOf("add", "remove", "list"))
                        }
                        "disablePricePenalty", "uninstallMode", "allowTravellingMerchants" -> {
                            list.addAll(listOf("true", "false"))
                        }
                        "lang" -> {
                            list.addAll(listOf("default", "custom"))
                        }
                    }
                }
            }
            4 -> {
                if(args[0] == "config" && args[1] == "villagerBlacklist") {
                    when(args[2]) {
                        "add", "remove" -> list.addAll(villagerList)
                    }
                }
            }
        }

        return list
    }
}