package me.spartacus04.instantrestock

import com.github.Anon8281.universalScheduler.UniversalScheduler
import org.bukkit.plugin.java.JavaPlugin
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL

internal class Updater(private val plugin: JavaPlugin) {
    fun getVersion(consumer: (String) -> Unit) {
        UniversalScheduler.getScheduler(plugin).runTaskAsynchronously {
            try {
                val reader = BufferedReader(InputStreamReader(URL("https://api.github.com/repos/spartacus04/InstantRestock/releases/latest").openStream()))
                val text = reader.use {
                    it.readText()
                }

                val version = Regex("\"tag_name\": ?\"([^\"]+)\"").find(text)?.groupValues?.get(1)!!
                consumer(version)
            } catch (exception: IOException) {
                plugin.logger.info("Unable to check for updates: " + exception.message)
            }
        }
    }
}