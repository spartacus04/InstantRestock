plugins {
    java
    kotlin("jvm") version "1.9.10"
    id("com.github.johnrengelman.shadow") version "8.1.1"

    `maven-publish`
    id("io.papermc.hangar-publish-plugin") version "0.1.0"
    id("com.modrinth.minotaur") version "2.8.6"
}

allprojects {
    repositories {
        mavenLocal()
        maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
        maven { url = uri("https://repo.dmulloy2.net/nexus/repository/public/") }
        maven { url = uri("https://repo.maven.apache.org/maven2/") }
        maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
    }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":spigot"))
    implementation(project(":folia"))
}

group = "me.spartacus04.instantrestock"

version = if (property("version_patch") == "0") {
    "${property("version_major")}.${property("version_minor")}"
} else {
    "${property("version_major")}.${property("version_minor")}.${property("version_patch")}"
}

description = "instantrestock"
java.sourceCompatibility = JavaVersion.VERSION_1_8
java.targetCompatibility = JavaVersion.VERSION_1_8

tasks {
    shadowJar {
        archiveFileName.set("${rootProject.name}_${project.version}.jar")
        val dependencyPackage = "${rootProject.group}.dependencies.${rootProject.name.lowercase()}"
        from(subprojects.map { it.sourceSets.main.get().output })

        relocate("kotlin", "${dependencyPackage}.kotlin")
        relocate("com/google/gson", "${dependencyPackage}.gson")
        relocate("org/intellij/lang", "${dependencyPackage}.lang")
        relocate("org/jetbrains/annotations", "${dependencyPackage}.annotations")
        relocate("org/bstats", "${dependencyPackage}.bstats")
        exclude("ScopeJVMKt.class")
        exclude("DebugProbesKt.bin")
        exclude("META-INF/**")
    }
}

java {
    val javaVersion = JavaVersion.toVersion(17)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    if(JavaVersion.current() < javaVersion) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    }
}

artifacts.archives(tasks.shadowJar)

// publish

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
    val hangarApiKey = System.getenv("hangarApiKey");
    val hangarChangelog = System.getenv("hangarChangelog")

    publications.register("plugin") {
        version.set("${project.version}")
        id.set("${property("hangar_slug")}")
        channel.set("${property("hangar_channel")}")
        changelog.set(hangarChangelog)

        apiKey.set(hangarApiKey)

        platforms {
            register(io.papermc.hangarpublishplugin.model.Platforms.PAPER) {
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