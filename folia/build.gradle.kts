plugins {
    java
    kotlin("jvm") version "1.9.0"
}

group = "me.spartacus04.instantrestock"
version = parent!!.version

dependencies {
    compileOnly("dev.folia:folia-api:1.20.1-R0.1-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation(project(":core"))
}