package me.spartacus04.instantrestock

import org.bukkit.entity.EntityType
import org.bukkit.entity.Villager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.plugin.java.JavaPlugin

class InstantRestock : JavaPlugin(), Listener {
    override fun onEnable() {
        super.onEnable()

        server.pluginManager.registerEvents(this, this)
    }

    @EventHandler
    fun onPlayerInteractAtEntityEvent(e: PlayerInteractAtEntityEvent) {
        if(e.rightClicked.type != EntityType.VILLAGER) return

        val villager = e.rightClicked as Villager

        if(villager.profession == Villager.Profession.NITWIT || villager.profession == Villager.Profession.NONE) return

        villager.recipes.forEach {
            it.maxUses = Int.MAX_VALUE
            it.uses = 0
        }
    }
}