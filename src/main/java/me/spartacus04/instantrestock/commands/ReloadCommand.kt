package me.spartacus04.instantrestock.commands

import me.spartacus04.colosseum.ColosseumPlugin
import me.spartacus04.colosseum.commandHandling.command.ColosseumCommand
import me.spartacus04.colosseum.i18n.sendI18nConfirm
import me.spartacus04.colosseum.i18n.sendI18nWarn
import me.spartacus04.instantrestock.InstantRestock.Companion.CONFIG
import me.spartacus04.instantrestock.config.FieldLanguageMode
import org.bukkit.command.CommandSender

class ReloadCommand(private val plugin: ColosseumPlugin) : ColosseumCommand(plugin) {
    override val commandData = commandDescriptor("reload") {
        description = "Reloads the plugin configuration"
        permissions = setOf("instantrestock.reload")
    }

    override fun execute(ctx: CommandContext<CommandSender>) {
        ctx.sender.sendI18nWarn(plugin, "reloading")

        CONFIG.read()
        plugin.i18nManager!!.forcedLanguage = if(CONFIG.LANG == FieldLanguageMode.DEFAULT) null else "custom"

        ctx.sender.sendI18nConfirm(plugin, "reloaded")
    }
}