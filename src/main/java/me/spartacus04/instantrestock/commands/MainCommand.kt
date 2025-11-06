package me.spartacus04.instantrestock.commands

import me.spartacus04.colosseum.ColosseumPlugin
import me.spartacus04.colosseum.commandHandling.command.ColosseumBaseCommand

class MainCommand(plugin: ColosseumPlugin) : ColosseumBaseCommand(plugin, "instantrestock", listOf(
    ConfigCommand(plugin),
    ReloadCommand(plugin)
))