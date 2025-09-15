package me.spartacus04.instantrestock.config

import com.google.gson.annotations.SerializedName
import me.spartacus04.colosseum.config.ConfigField
import me.spartacus04.colosseum.config.FileBind
import me.spartacus04.instantrestock.InstantRestockState.PLUGIN

/**
 * Represents the configuration for the plugin.
 *
 * @property MAX_TRADES The maximum number of trades allowed.
 * @property VILLAGER_BLACKLIST The list of blacklisted villagers.
 * @property DISABLE_PRICE_PENALTY Indicates if the price penalty is disabled.
 * @property UNINSTALL_MODE Indicates if the uninstall mode is enabled.
 * @property ALLOW_TRAVELLING_MERCHANTS Indicates if travelling merchants are allowed.
 * @property CHECK_UPDATE Indicates if checking for updates is allowed.
 * @property ALLOW_METRICS Indicates if metrics are allowed.
 */
data class Config(
    @ConfigField(
        "Language",
        "The language mode of the plugin",
        "default"
    )
    @SerializedName("lang")
    var LANG: FieldLanguageMode = FieldLanguageMode.DEFAULT,

    @ConfigField(
        "Max trades per villager",
        "The maximum amount of trades a villager can have per restock",
        "0"
    )
    @SerializedName("maxTrades")
    var MAX_TRADES: Int = 0,

    @ConfigField(
        "Villager blacklist",
        "The list of professions that should not be restocked",
        "[]"
    )
    @SerializedName("villagerBlacklist")
    var VILLAGER_BLACKLIST: ArrayList<String> = ArrayList(),

    @ConfigField(
        "Disable price penalty",
        "Indicates if the price penalty should be disabled",
        "true"
    )
    @SerializedName("disablePricePenalty")
    var DISABLE_PRICE_PENALTY: Boolean = true,

    @ConfigField(
        "Uninstall mode",
        "Indicates if the plugin should be in uninstall mode",
        "false"
    )
    @SerializedName("uninstallMode")
    var UNINSTALL_MODE: Boolean = false,

    @ConfigField(
        "Allow travelling merchants",
        "Indicates if travelling merchants are allowed",
        "true"
    )
    @SerializedName("allowTravellingMerchants")
    var ALLOW_TRAVELLING_MERCHANTS: Boolean = true,

    @ConfigField(
        "Check for updates",
        "Indicates if the plugin should check for updates",
        "true"
    )
    @SerializedName("checkUpdate")
    var CHECK_UPDATE: Boolean = true,

    @ConfigField(
        "Allow metrics",
        "Indicates if metrics are allowed",
        "true"
    )
    @SerializedName("allowMetrics")
    var ALLOW_METRICS: Boolean = true
) : FileBind("config.json", Config::class.java, PLUGIN) {
    @Suppress("unused")
    @SerializedName("\$schema")
    private val schema = "https://raw.githubusercontent.com/spartacus04/InstantRestock/master/configSchema.json"
}
