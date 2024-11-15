import org.jetbrains.dokka.base.DokkaBase
import org.jetbrains.dokka.base.DokkaBaseConfiguration
import proguard.gradle.ProGuardTask
import io.papermc.hangarpublishplugin.model.Platforms
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    java
    kotlin("jvm") version "2.0.21"
    id("com.github.johnrengelman.shadow") version "8.1.1"

    id("org.jetbrains.dokka") version "1.9.20"

    `maven-publish`
    id("io.papermc.hangar-publish-plugin") version "0.1.2"
    id("com.modrinth.minotaur") version "2.8.7"
}

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.dokka:dokka-base:1.9.20")
        classpath("com.guardsquare:proguard-gradle:7.6.0") {
            exclude("com.android.tools.build")
        }
    }
}

repositories {
    mavenLocal()
    maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
    maven { url = uri("https://repo.dmulloy2.net/nexus/repository/public/") }
    maven { url = uri("https://repo.maven.apache.org/maven2/") }
    maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.21.3-R0.1-SNAPSHOT")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.bstats:bstats-bukkit:3.1.0")
    implementation("com.github.Anon8281:UniversalScheduler:0.1.6")
}

group = "me.spartacus04.instantrestock"

version = System.getenv("instantrestockVersion") ?: "dev"

description = "instantrestock"
java.sourceCompatibility = JavaVersion.VERSION_1_8
java.targetCompatibility = JavaVersion.VERSION_1_8

tasks.shadowJar {
    archiveFileName.set("${rootProject.name}_${project.version}-shadowed.jar")
    val dependencyPackage = "${rootProject.group}.dependencies.${rootProject.name.lowercase()}"
    from(subprojects.map { it.sourceSets.main.get().output })

    relocate("kotlin", "${dependencyPackage}.kotlin")
    relocate("com/google/gson", "${dependencyPackage}.gson")
    relocate("org/intellij/lang", "${dependencyPackage}.lang")
    relocate("org/jetbrains/annotations", "${dependencyPackage}.annotations")
    relocate("org/bstats", "${dependencyPackage}.bstats")
    relocate("com/github/Anon8281/universalScheduler", "${dependencyPackage}.universalScheduler")
    exclude("ScopeJVMKt.class")
    exclude("DebugProbesKt.bin")
    exclude("META-INF/**")

    minimize()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions.jvmTarget.set(JvmTarget.JVM_1_8);
}

artifacts.archives(tasks.shadowJar)

tasks.register<ProGuardTask>("proguardJar") {
    outputs.upToDateWhen { false }
    dependsOn("clean")
    dependsOn("shadowJar")

    injars(tasks.shadowJar.flatMap { it.archiveFile })

    configuration("proguard-rules.pro")

    outjars("build/libs/${rootProject.name}_${project.version}.jar")
}

tasks.processResources {
    filesMatching("plugin.yml") {
        expand("version" to project.rootProject.version)
    }
}

// publish

tasks.dokkaHtml {
    val githubTag = System.getenv("githubTag")

    if(githubTag != null) {
        version = "$version - $githubTag"
    }

    pluginConfiguration<DokkaBase, DokkaBaseConfiguration> {
        customStyleSheets = listOf(file("docsAssets/logo-styles.css"))
        customAssets = listOf(file("icon.webp"))
        footerMessage = "Infinite Villager Trades is licensed under the <a href=\"https://github.com/spartacus04/InstantRestock/blob/master/LICENSE.MD\">MIT</a> License."
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = rootProject.group.toString()
            artifactId = rootProject.name.lowercase()
            version = "${rootProject.version}"

            from(components["java"])
        }
    }
}

hangarPublish {
    val hangarApiKey = System.getenv("hangarApiKey")
    val hangarChangelog = System.getenv("hangarChangelog")

    publications.register("plugin") {
        version.set("${project.version}")
        id.set("${property("hangar_slug")}")
        channel.set("${property("hangar_channel")}")
        changelog.set(hangarChangelog)

        apiKey.set(hangarApiKey)

        platforms {
            register(Platforms.PAPER) {
                jar.set(tasks.shadowJar.flatMap { it.archiveFile })
                platformVersions.set("${property("minecraft_versions")}".split(","))
            }
        }
    }
}


modrinth {
    val modrinthApiKey = System.getenv("modrinthApiKey")
    val modrinthChangelog = System.getenv("modrinthChangelog")

    token.set(modrinthApiKey)
    projectId.set("${property("modrinth_projectId")}")
    versionNumber.set(rootProject.version.toString())
    versionType.set("${property("modrinth_channel")}")
    uploadFile.set(tasks.shadowJar.flatMap { it.archiveFile })
    gameVersions.set("${property("minecraft_versions")}".split(","))
    loaders.set("${property("modrinth_loaders")}".split(","))

    changelog.set(modrinthChangelog)

    syncBodyFrom.set(rootProject.file("README.md").readText())
}