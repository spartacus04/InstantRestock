package me.spartacus04.instantrestock

import me.spartacus04.instantrestock.SettingsContainer.Companion.CONFIG
import me.spartacus04.instantrestock.SettingsContainer.Companion.VERSION118
import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.entity.AbstractVillager
import org.bukkit.entity.EntityType
import org.bukkit.entity.Villager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.VillagerAcquireTradeEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.plugin.java.JavaPlugin

class InstantRestock : JavaPlugin(), Listener {
    private lateinit var key: NamespacedKey

    override fun onEnable() {
        super.onEnable()

        Metrics(this, 16589)

        key = NamespacedKey(this, "instant_restock")
        SettingsContainer.reloadConfig(this)
        getCommand("instantrestock")!!.setExecutor(MainCommand(this))

        server.pluginManager.registerEvents(this, this)

        Updater(this, 88098).getVersion {
            if(it != description.version) {
                Bukkit.getConsoleSender().sendMessage(
                    "[§aInfiniteVillagerTrading§f] a new update is available!"
                )
            }
        }
    }

    @EventHandler
    fun onPlayerInteractAtEntityEvent(e: PlayerInteractAtEntityEvent) {
        if(e.rightClicked !is AbstractVillager) return

        val merchant = e.rightClicked as AbstractVillager

        if(merchant.type == EntityType.WANDERING_TRADER && !CONFIG.allowTravellingMerchants) {
            if(merchant.persistentDataContainer.has(key, TradesDataType())) {
                restoreVillagerTrades(merchant)
                merchant.persistentDataContainer.remove(key)
            }

            return
        }

        if(merchant.type == EntityType.VILLAGER) {
            if(CONFIG.villagerBlacklist.contains((merchant as Villager).profession.name)) {
                if(merchant.persistentDataContainer.has(key, TradesDataType())) {
                    restoreVillagerTrades(merchant)
                    merchant.persistentDataContainer.remove(key)
                }

                return
            }

            if(merchant.profession == Villager.Profession.NITWIT ||
                merchant.profession == Villager.Profession.NONE) return

            Villager.Profession.SHEPHERD
        }

        if(CONFIG.uninstallMode) {
            if(merchant.persistentDataContainer.has(key, TradesDataType())) {
                restoreVillagerTrades(merchant)
                merchant.persistentDataContainer.remove(key)
            }

            return
        }

        if(!merchant.persistentDataContainer.has(key, TradesDataType())) {
            saveVillagerTrades(merchant)
        }

        setMaxTrades(merchant, CONFIG.maxTrades)
    }

    @EventHandler
    fun onVillagerUpgrade(e: VillagerAcquireTradeEvent) {
        val merchant = e.entity

        if(merchant.type == EntityType.WANDERING_TRADER && !CONFIG.allowTravellingMerchants) return
        if(CONFIG.uninstallMode) return

        if(merchant.persistentDataContainer.has(key, TradesDataType())) {
            restoreVillagerTrades(merchant)
            saveVillagerTrades(merchant)

            setMaxTrades(merchant, CONFIG.maxTrades)
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
            if(CONFIG.maxTrades == Int.MAX_VALUE) it.uses = 0
            if(VERSION118 && CONFIG.disablePricePenalty) it.demand = 0
        }
    }
}

