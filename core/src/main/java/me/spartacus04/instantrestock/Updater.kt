package me.spartacus04.instantrestock

import java.util.function.Consumer

interface Updater {
    fun checkForUpdates(resId: Int, consumer: Consumer<String?>)
}