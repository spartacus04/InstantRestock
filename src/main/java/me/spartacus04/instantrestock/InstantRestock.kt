package me.spartacus04.instantrestock

import com.google.gson.GsonBuilder
import org.bukkit.NamespacedKey
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.AbstractVillager
import org.bukkit.entity.EntityType
import org.bukkit.entity.Villager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.VillagerAcquireTradeEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.plugin.java.JavaPlugin

class InstantRestock : JavaPlugin(), Listener {
    companion object {
        lateinit var instance: InstantRestock
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
        if(e.rightClicked !is AbstractVillager) return

        val merchant = e.rightClicked as AbstractVillager

        if(merchant.type == EntityType.WANDERING_TRADER && !config.allowTravellingMechants) {
            if(merchant.persistentDataContainer.has(key, TradesDataType())) {
                restoreVillagerTrades(merchant)
                merchant.persistentDataContainer.remove(key)
            }

            return
        }

        if(merchant.type == EntityType.VILLAGER) {
            if(config.villagerBlacklist.contains((merchant as Villager).profession.name)) {
                if(merchant.persistentDataContainer.has(key, TradesDataType())) {
                    restoreVillagerTrades(merchant)
                    merchant.persistentDataContainer.remove(key)
                }

                return
            }

            if(merchant.profession == Villager.Profession.NITWIT ||
                merchant.profession == Villager.Profession.NONE) return

        }

        if(config.uninstallMode) {
            if(merchant.persistentDataContainer.has(key, TradesDataType())) {
                restoreVillagerTrades(merchant)
                merchant.persistentDataContainer.remove(key)
            }

            return
        }

        if(!merchant.persistentDataContainer.has(key, TradesDataType())) {
            saveVillagerTrades(merchant)
        }

        setMaxTrades(merchant, config.maxTrades)
    }

    @EventHandler
    fun onVillagerUpgrade(e: VillagerAcquireTradeEvent) {
        val merchant = e.entity

        if(merchant.type == EntityType.WANDERING_TRADER && !config.allowTravellingMechants) return
        if(config.uninstallMode) return

        if(merchant.persistentDataContainer.has(key, TradesDataType())) {
            restoreVillagerTrades(merchant)
            saveVillagerTrades(merchant)

            setMaxTrades(merchant, config.maxTrades)
        }
    }

    private fun saveVillagerTrades(villager: AbstractVillager) {
        villager.persistentDataContainer.set(key, TradesDataType(), villager.recipes.map {
            it.maxUses
        }.toIntArray())
    }

    private fun restoreVillagerTrades(villager: AbstractVillager) {
        if(!villager.persistentDataContainer.has(key, TradesDataType())) return
        val trades = villager.persistentDataContainer.get(key, TradesDataType()) ?: return
        villager.recipes.forEachIndexed { i, r ->
            try {
                r.maxUses = trades[i]
            }
            catch (_: Exception) {
                return@forEachIndexed
            }
        }
    }

    private fun setMaxTrades(villager: AbstractVillager, maxTrades: Int) {
        villager.recipes.forEach {
            it.maxUses = maxTrades
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
}

class CommandReload: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(sender.hasPermission("instantrestock.reload")) {
            sender.sendMessage("§aReloading Instantrestock...")
            InstantRestock.instance.reloadConfig()
            sender.sendMessage("§aReloaded!")
        } else {
            sender.sendMessage("§cYou don't have permission to do that!")
        }
        return true
    }
}