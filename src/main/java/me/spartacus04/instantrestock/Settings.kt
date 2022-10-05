package me.spartacus04.instantrestock

import com.google.gson.GsonBuilder
import org.bukkit.plugin.java.JavaPlugin

data class Settings(
    var maxTrades: Int = Int.MAX_VALUE,
    var villagerBlacklist: ArrayList<String> = ArrayList(),
    var disablePricePenalty: Boolean = false,
    var uninstallMode: Boolean = false,
    var allowTravellingMerchants: Boolean = true,
)

class SettingsContainer {
    companion object {
        lateinit var CONFIG : Settings
        var VERSION118: Boolean = false

        val villagerList = listOf(
            "ARMORER", "BUTCHER", "CARTOGRAPHER", "CLERIC", "FARMER", "FISHERMAN",
            "FLETCHER", "LEATHERWORKER", "LIBRARIAN", "MASON", "SHEPHERD", "TOOLSMITH",
            "WEAPONSMITH"
        )

        fun reloadConfig(plugin: JavaPlugin) {
            val gson = GsonBuilder().setPrettyPrinting().setLenient().create()

            val configFile = plugin.dataFolder.resolve("config.json")

            if(!configFile.exists()) {
                if(!plugin.dataFolder.exists()) plugin.dataFolder.mkdirs()
                configFile.createNewFile()
                CONFIG = Settings()
                plugin.getResource("config.json")?.bufferedReader().use {
                    configFile.writeText(it?.readText()!!)
                }
            } else {
                if (configFile.readText().contains("Mechants")) {
                    configFile.delete()
                    return reloadConfig(plugin)
                }

                CONFIG = gson.fromJson(configFile.readText(), Settings::class.java)
                if (CONFIG.maxTrades == 0) {
                    CONFIG.maxTrades = Int.MAX_VALUE
                }
            }

            val version = plugin.server.javaClass.`package`.name.replace(".", ",").split(",")[3]
            val subVersion = Integer.parseInt(version.replace("1_", "").replace(Regex("_R\\d"), "").replace("v", ""))

            VERSION118 = subVersion >= 18
        }

        fun saveConfig(plugin: JavaPlugin) {
            val gson = GsonBuilder().setPrettyPrinting().setLenient().create()

            val configFile = plugin.dataFolder.resolve("config.json")

            configFile.delete()
            configFile.createNewFile()
            configFile.bufferedWriter().use {
                it.write(gson.toJson(CONFIG))
            }
        }
    }
}