package me.spartacus04.instantrestock.listeners

import me.spartacus04.colosseum.listeners.ColosseumListener
import me.spartacus04.instantrestock.InstantRestockState.CONFIG
import me.spartacus04.instantrestock.InstantRestockState.KEY
import me.spartacus04.instantrestock.InstantRestockState.VERSION
import me.spartacus04.instantrestock.TradesDataType
import org.bukkit.entity.AbstractVillager
import org.bukkit.entity.EntityType
import org.bukkit.entity.Villager
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.VillagerAcquireTradeEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.plugin.java.JavaPlugin

internal class VillagerEvent(plugin: JavaPlugin) : ColosseumListener(plugin) {
    @EventHandler
    fun onPlayerInteractAtEntityEvent(e: PlayerInteractAtEntityEvent) {
        if(e.rightClicked !is AbstractVillager) return

        val merchant = e.rightClicked as AbstractVillager

        if(CONFIG.UNINSTALL_MODE) {
            if(merchant.persistentDataContainer.has(KEY, TradesDataType())) {
                restoreVillagerTrades(merchant)
                merchant.persistentDataContainer.remove(KEY)
            }

            return
        }

        if(merchant.type == EntityType.WANDERING_TRADER && !CONFIG.ALLOW_TRAVELLING_MERCHANTS) {
            if(merchant.persistentDataContainer.has(KEY, TradesDataType())) {
                restoreVillagerTrades(merchant)
                merchant.persistentDataContainer.remove(KEY)
            }

            return
        }

        if(merchant.type == EntityType.VILLAGER) {
            if(CONFIG.VILLAGER_BLACKLIST.contains(getProfessionKey(merchant as Villager).uppercase())) {
                if(merchant.persistentDataContainer.has(KEY, TradesDataType())) {
                    restoreVillagerTrades(merchant)
                    merchant.persistentDataContainer.remove(KEY)
                }

                return
            }
        }

        if(!merchant.persistentDataContainer.has(KEY, TradesDataType())) {
            saveVillagerTrades(merchant)
        }

        setMaxTrades(merchant, CONFIG.MAX_TRADES)
    }

    @EventHandler
    fun onVillagerUpgrade(e: VillagerAcquireTradeEvent) {
        val merchant = e.entity

        if(CONFIG.UNINSTALL_MODE) return
        if(merchant.type == EntityType.WANDERING_TRADER && !CONFIG.ALLOW_TRAVELLING_MERCHANTS) return

        if(merchant.persistentDataContainer.has(KEY, TradesDataType())) {
            restoreVillagerTrades(merchant)
            saveVillagerTrades(merchant)

            setMaxTrades(merchant, CONFIG.MAX_TRADES)
        }
    }

    private fun saveVillagerTrades(villager: AbstractVillager) {
        villager.persistentDataContainer.set(KEY, TradesDataType(), villager.recipes.map {
            it.maxUses
        }.toIntArray())
    }

    private fun restoreVillagerTrades(villager: AbstractVillager) {
        if(!villager.persistentDataContainer.has(KEY, TradesDataType())) return
        val trades = villager.persistentDataContainer.get(KEY, TradesDataType()) ?: return
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
            it.maxUses = if(maxTrades <= 0) {
                Int.MAX_VALUE
            } else {
                maxTrades
            }

            if(CONFIG.MAX_TRADES == Int.MAX_VALUE) it.uses = 0
            if(VERSION >= "1.18" && CONFIG.DISABLE_PRICE_PENALTY) it.demand = 0
        }
    }

    /**
     * In API versions 1.20.6 and earlier, Villager.Profession is a class.
     * In versions 1.21 and later, it is an interface.
     * This method uses reflection to get the profession.key.key field of the villager.
     *
     * @param villager The villager to get the profession of.
     * @return The profession name of the villager.
     */
    private fun getProfessionKey(villager: Villager): String {
        val profession = villager::class.java.getMethod("getProfession").invoke(villager)
        val key = profession::class.java.getMethod("getKey").invoke(profession)
        val keyKey = key::class.java.getMethod("getKey").invoke(key)
        return keyKey.toString()
    }
}