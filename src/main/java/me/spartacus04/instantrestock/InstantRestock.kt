package me.spartacus04.instantrestock

import me.spartacus04.colosseum.utils.PluginUpdater
import me.spartacus04.instantrestock.InstantRestockState.CONFIG
import me.spartacus04.instantrestock.InstantRestockState.I18N
import me.spartacus04.instantrestock.commands.MainCommand
import me.spartacus04.instantrestock.listeners.PlayerJoinEvent
import me.spartacus04.instantrestock.listeners.VillagerEvent
import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

@Suppress("unused")
class InstantRestock : JavaPlugin() {
    override fun onEnable() {
        getCommand("instantrestock")!!.setExecutor(MainCommand())

        VillagerEvent(this).register()
        PlayerJoinEvent(this).register()

        if(CONFIG.ALLOW_METRICS)
            Metrics(this, 16589)

        if(CONFIG.CHECK_UPDATE)
            PluginUpdater(this, "spartacus04/InstantRestock", I18N).getVersion {
                if(it != description.version) {
                    Bukkit.getConsoleSender().sendMessage(
                        I18N.messageFormatter.info(I18N.getFormatted(Bukkit.getConsoleSender(), "update-available"))
                    )
                    Bukkit.getConsoleSender().sendMessage(
                        I18N.messageFormatter.url("https://modrinth.com/plugin/infinite-villager-trading")
                    )
                }
            }

        I18N.confirm("Enabled InstantRestock!")
    }
}