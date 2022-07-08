package me.spartacus04.instantrestock

import com.google.gson.GsonBuilder
import org.bukkit.NamespacedKey
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.EntityType
import org.bukkit.entity.Villager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.VillagerAcquireTradeEvent
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

        if (config.uninstallMode || config.villagerBlacklist.contains(villager.profession.name)) {
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

        villager.recipes.forEach {
            it.maxUses = config.maxTrades
            if(config.maxTrades == Int.MAX_VALUE) it.uses = 0
            if(config.version118 && config.disablePricePenalty) it.demand = 0
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
            if (config.maxTrades == 0) {
                config.maxTrades = Int.MAX_VALUE
            }
        }

        val version = server.javaClass.`package`.name.replace(".", ",").split(",")[3]
        val subVersion = Integer.parseInt(version.replace("1_", "").replace(Regex("_R\\d"), "").replace("v", ""))

        config.version118 = subVersion >= 18
    }

    @EventHandler
    fun onVillagerUpgrade(e: VillagerAcquireTradeEvent) {
        if(e.entity.type != EntityType.VILLAGER) return

        val villager = e.entity as Villager

        if(villager.persistentDataContainer.has(key, TradesDataType()) && !config.uninstallMode) {
            val trades = villager.persistentDataContainer.get(key, TradesDataType()) ?: return
            villager.recipes.forEachIndexed { i, r ->
                try {
                    r.maxUses = trades[i]
                }
                catch (_: Exception) {
                    return@forEachIndexed
                }
            }

            villager.persistentDataContainer.set(key, TradesDataType(), villager.recipes.map {
                it.maxUses
            }.toIntArray())

            villager.recipes.forEach {
                it.maxUses = config.maxTrades
                it.uses = 0
                if(config.version118 && config.disablePricePenalty) it.demand = 0
            }
        }
    }
}

class CommandReload: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(sender.hasPermission("instantrestock.reload")) {
            sender.sendMessage("§aReloading Instantrestock...")
            InstantRestock.instance?.reloadConfig()
            sender.sendMessage("§aReloaded!")
        } else {
            sender.sendMessage("§cYou don't have permission to do that!")
        }
        return true
    }
}