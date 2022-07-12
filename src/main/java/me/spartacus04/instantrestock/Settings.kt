package me.spartacus04.instantrestock

data class Settings(
    var maxTrades: Int = Int.MAX_VALUE,
    var villagerBlacklist: List<String> = listOf(),
    var disablePricePenalty: Boolean = false,
    var uninstallMode: Boolean = false,
    var version118: Boolean = true,
    var allowTravellingMechants: Boolean = true,
)