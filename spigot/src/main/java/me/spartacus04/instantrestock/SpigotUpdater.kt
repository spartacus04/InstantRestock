package me.spartacus04.instantrestock

import org.bukkit.Bukkit
import java.io.IOException
import java.net.URL
import java.util.*
import java.util.function.Consumer


class SpigotUpdater : Updater {
    override fun checkForUpdates(resId: Int, consumer: Consumer<String?>) {
        Bukkit.getScheduler().run {
            try {
                URL("https://api.spigotmc.org/legacy/update.php?resource=$resId").openStream()
                    .use { inputStream ->
                        Scanner(inputStream).use { scanner ->
                            if (scanner.hasNext()) {
                                consumer.accept(scanner.next())
                            }
                        }
                    }
            } catch (exception: IOException) {
                Bukkit.getConsoleSender().sendMessage("Unable to check for updates: " + exception.message)
            }
        }
    }
}