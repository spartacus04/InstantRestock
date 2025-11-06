package me.spartacus04.instantrestock.listeners

import me.spartacus04.colosseum.ColosseumPlugin
import me.spartacus04.colosseum.i18n.sendI18nInfo
import me.spartacus04.colosseum.listeners.ColosseumListener
import me.spartacus04.colosseum.logging.sendUrl
import me.spartacus04.instantrestock.InstantRestock.Companion.CONFIG
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent

internal class PlayerJoinEvent(private val plugin: ColosseumPlugin) : ColosseumListener(plugin) {
    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        val player = e.player

        if(CONFIG.CHECK_UPDATE && player.hasPermission("instantrestock.notifyupdate"))
            plugin.checkForUpdates("spartacus04/InstantRestock") {
                if(it != plugin.description.version) {
                    player.sendI18nInfo(plugin, "update-available")
                    player.sendUrl(plugin, "https://modrinth.com/plugin/infinite-villager-trading")
                }
            }
    }
}