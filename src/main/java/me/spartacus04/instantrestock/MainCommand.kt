package me.spartacus04.instantrestock

import me.spartacus04.instantrestock.SettingsContainer.Companion.CONFIG
import me.spartacus04.instantrestock.SettingsContainer.Companion.saveConfig
import me.spartacus04.instantrestock.SettingsContainer.Companion.villagerList
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.plugin.java.JavaPlugin

class MainCommand(private val plugin : JavaPlugin) : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        try {
            when(args[0]) {
                "reload" -> {
                    if(sender.hasPermission("instantrestock.reload")) {
                        sender.sendMessage("§aReloading Instantrestock...")
                        SettingsContainer.reloadConfig(plugin)
                        sender.sendMessage("§aReloaded!")
                    } else {
                        sender.sendMessage("§cYou don't have permission to do that!")
                    }
                }
                "config" -> {
                    if(sender.hasPermission("instantrestock.config")) {
                        when(args[1]) {
                            "maxTrades" -> {
                                if(args[2] == "infinite") {
                                    CONFIG.maxTrades = Integer.MAX_VALUE
                                    saveConfig(plugin)
                                }
                                else if (args[2].toIntOrNull() != null) {
                                    CONFIG.maxTrades = args[2].toInt()
                                    saveConfig(plugin)
                                }
                                else {
                                    sender.sendMessage("§cInvalid value")
                                }
                            }
                            "disablePricePenalty" -> {
                                CONFIG.disablePricePenalty = args[2].lowercase().toBooleanStrict()
                                saveConfig(plugin)
                            }
                            "uninstallMode" -> {
                                CONFIG.uninstallMode = args[2].lowercase().toBooleanStrict()
                                saveConfig(plugin)
                            }
                            "allowTravellingMerchants" -> {
                                CONFIG.allowTravellingMerchants = args[2].lowercase().toBooleanStrict()
                                saveConfig(plugin)
                            }
                            "villagerBlacklist" -> {
                                when(args[2]) {
                                    "add" -> {
                                        if(villagerList.contains(args[3].uppercase()) && !CONFIG.villagerBlacklist.contains(args[3].uppercase())) {
                                            CONFIG.villagerBlacklist.add(args[3].uppercase())
                                            saveConfig(plugin)
                                        }
                                    }
                                    "remove" -> {
                                        if(villagerList.contains(args[3].uppercase()) && CONFIG.villagerBlacklist.contains(args[3].uppercase())) {
                                            CONFIG.villagerBlacklist.remove(args[3].uppercase())
                                            saveConfig(plugin)
                                        }
                                    }
                                    "list" -> {
                                        sender.sendMessage(CONFIG.villagerBlacklist.joinToString { ", " })
                                    }
                                }
                            }
                        }
                    } else {
                        sender.sendMessage("§cYou don't have permission to do that!")
                    }
                }
            }
        }
        catch (_: Exception) {
            sender.sendMessage("§cInvalid value")
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
                        "allowTravellingMerchants"
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