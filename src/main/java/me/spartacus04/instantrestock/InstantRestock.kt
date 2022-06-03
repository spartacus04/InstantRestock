package me.spartacus04.instantrestock

import com.google.gson.GsonBuilder
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.EntityType
import org.bukkit.entity.Villager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.plugin.java.JavaPlugin


class InstantRestock : JavaPlugin(), Listener {
    companion object {
        var instance: InstantRestock? = null
    }


    private lateinit var key: NamespacedKey
    private lateinit var config: Settings

    override fun onEnable() {
        super.onEnable()
        instance = this

        key = NamespacedKey(this, "instant_restock")
        reloadConfig()
        getCommand("instantrestock")?.setExecutor(CommandReload())

        server.pluginManager.registerEvents(this, this)
    }

    @EventHandler
    fun onPlayerInteractAtEntityEvent(e: PlayerInteractAtEntityEvent) {
        if(e.rightClicked.type != EntityType.VILLAGER) return

        val villager = e.rightClicked as Villager

        if (config.uninstallMode) {
            val trades = villager.persistentDataContainer.get(key, TradesDataType()) ?: return

            villager.recipes.forEachIndexed { i, r ->
                try {
                    if(trades[i] == -1) return@forEachIndexed
                    r.maxUses = trades[i]
                }
                catch (_: Exception) {
                    return@forEachIndexed
                }
            }

            return villager.persistentDataContainer.remove(key)
        }

        if(villager.profession == Villager.Profession.NITWIT || villager.profession == Villager.Profession.NONE) return

        if(!villager.persistentDataContainer.has(key, TradesDataType())) {
            villager.persistentDataContainer.set(key, TradesDataType(), villager.recipes.map {
                it.maxUses
            }.toIntArray())
        }

        val minecraft118: Boolean = Bukkit.getVersion().contains("1.18")

        villager.recipes.forEach {
            it.maxUses = Int.MAX_VALUE
            it.uses = 0
            if(minecraft118 && config.disablePricePenalty) it.demand = 0
        }
    }

    override fun reloadConfig() {
        val gson = GsonBuilder().setPrettyPrinting().setLenient().create()

        val configFile = this.dataFolder.resolve("config.json")

        if(!configFile.exists()) {
            if(!dataFolder.exists()) dataFolder.mkdirs()
            configFile.createNewFile()
            config = Settings()
            getResource("config.json")?.bufferedReader().use {
                configFile.writeText(it?.readText()!!)
            }
        } else {
            config = gson.fromJson(configFile.readText(), Settings::class.java)
        }
    }
}

class CommandReload: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(sender.hasPermission("instantrestock.reload")) {
            sender.sendMessage("§aReloading...")
            InstantRestock.instance?.reloadConfig()
            sender.sendMessage("§aReloaded!")
        } else {
            sender.sendMessage("§cYou don't have permission to do that!")
        }
        return true
    }
}