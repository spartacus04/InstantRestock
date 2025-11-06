package me.spartacus04.instantrestock

import me.spartacus04.colosseum.ColosseumPlugin
import me.spartacus04.colosseum.config.FileBind
import me.spartacus04.instantrestock.commands.MainCommand
import me.spartacus04.instantrestock.config.Config
import me.spartacus04.instantrestock.config.FieldLanguageMode
import me.spartacus04.instantrestock.listeners.PlayerJoinEvent
import me.spartacus04.instantrestock.listeners.VillagerEvent
import org.bstats.bukkit.Metrics
import org.bukkit.NamespacedKey

@Suppress("unused")
class InstantRestock : ColosseumPlugin() {
    override fun onEnable() {
        INSTANCE = this
        CONFIG = run {
            if (!dataFolder.exists()) dataFolder.mkdirs()
            FileBind.create(Config::class.java)
        }

        KEY = NamespacedKey(this, "instant_restock")

        buildI18nManager {
            loadInternalLanguageDirectory("langs")
            loadExternalLanguageFiles(dataFolder.resolve("lang.json"), "custom", "langs/en_US")
            setDefaultLocale("en_us")
            this.setLanguagesToLower(true)

            if(CONFIG.LANG == FieldLanguageMode.CUSTOM) {
                this.forceLanguage("custom")
            }
        }

        registerCommands {
            addCommands(MainCommand::class.java)
        }

        VillagerEvent(this).register()
        PlayerJoinEvent(this).register()

        if(CONFIG.ALLOW_METRICS)
            Metrics(this, 16589)

        if(CONFIG.CHECK_UPDATE)
            checkForUpdates("spartacus04/InstantRestock") {
                if(it != description.version) {
                    colosseumLogger.infoI18n(this, "update-available")
                    colosseumLogger.url("https://modrinth.com/plugin/infinite-villager-trading")
                }
            }

        colosseumLogger.confirm("Enabled InstantRestock!")
    }

    override fun onDisable() {
        super.onDisable()

        colosseumLogger.warn("Disabled InstantRestock!")
    }

    companion object {
        lateinit var INSTANCE: InstantRestock
        lateinit var CONFIG : Config
        lateinit var KEY: NamespacedKey
    }
}