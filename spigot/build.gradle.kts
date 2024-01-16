plugins {
    java
    kotlin("jvm") version "1.9.22"
}

group = "me.spartacus04.instantrestock"
version = parent!!.version

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.20.4-R0.1-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation(project(":core"))
}