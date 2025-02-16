package me.spartacus04.instantrestock

import me.spartacus04.colosseum.config.FileBind
import me.spartacus04.colosseum.i18n.ColosseumI18nManager
import me.spartacus04.colosseum.utils.version.MinecraftServerVersion
import me.spartacus04.instantrestock.config.Config
import me.spartacus04.instantrestock.config.FieldLanguageMode
import org.bukkit.NamespacedKey
import org.bukkit.plugin.java.JavaPlugin

/**
 * Represents the state of the plugin.
 *
 *
 * @property PLUGIN The plugin instance.
 * @property VERSION The current Minecraft server version.
 * @property CONFIG The configuration file.
 * @property KEY The namespaced key for the plugin.
 * @property I18N The i18n manager.
 */
object InstantRestockState {
    val PLUGIN = JavaPlugin.getPlugin(InstantRestock::class.java)
    internal val VERSION = MinecraftServerVersion.current

    val CONFIG = run {
        if (!PLUGIN.dataFolder.exists()) PLUGIN.dataFolder.mkdirs()
        FileBind.create(Config::class.java)
    }

    val KEY = NamespacedKey(PLUGIN, "instant_restock")
    internal val I18N = ColosseumI18nManager(PLUGIN, "InfiniteVillagerTrades", "langs/").apply {
        this.loadLanguagesFromResources()

        if(CONFIG.LANG == FieldLanguageMode.CUSTOM) {
            this.customMode = true
        }
    }
}