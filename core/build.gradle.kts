plugins {
    java
    kotlin("jvm") version "1.9.10"
}

group = "me.spartacus04.instantrestock-core"
version = parent!!.version

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.20.3-R0.1-SNAPSHOT")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.bstats:bstats-bukkit:3.0.2")
}