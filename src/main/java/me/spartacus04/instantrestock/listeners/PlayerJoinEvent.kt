package me.spartacus04.instantrestock.listeners

import me.spartacus04.colosseum.listeners.ColosseumListener
import me.spartacus04.colosseum.utils.PluginUpdater
import me.spartacus04.instantrestock.InstantRestockState
import me.spartacus04.instantrestock.InstantRestockState.CONFIG
import me.spartacus04.instantrestock.InstantRestockState.PLUGIN
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin

internal class PlayerJoinEvent(plugin: JavaPlugin) : ColosseumListener(plugin) {
    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        val player = e.player

        if(CONFIG.CHECK_UPDATE && player.hasPermission("instantrestock.notifyupdate"))
            PluginUpdater(PLUGIN, "spartacus04/InstantRestock", InstantRestockState.I18N).getVersion {
                if(it != PLUGIN.description.version) {
                    player.sendMessage(
                        InstantRestockState.I18N.messageFormatter.info(InstantRestockState.I18N.getFormatted(player, "update-available"))
                    )
                    player.sendMessage(
                        InstantRestockState.I18N.messageFormatter.url("https://modrinth.com/plugin/infinite-villager-trading")
                    )
                }
            }
    }
}