import gradle.kotlin.dsl.accessors._285dcef16d8875fee0ec91e18e07daf9.implementation
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    kotlin("jvm")
    id("io.papermc.paperweight.userdev")
    id("xyz.jpenilla.run-paper")
    id("net.minecrell.plugin-yml.bukkit")
    id("com.modrinth.minotaur")
}

description = properties["description"] as String

val gameVersion by properties
val foliaSupport = properties["foliaSupport"] as String == "true"
val projectName = properties["projectName"] as String

repositories {
    mavenCentral()
}

dependencies {
    paperweight.paperDevBundle("${gameVersion}-R0.1-SNAPSHOT")

    // Kotlin libraries
    library(kotlin("stdlib"))
    library("org.jetbrains.kotlinx:kotlinx-serialization-json:1.+")
    library("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.+")

    // Utility libraries (optional)
    val useBrigadier = properties["useBrigadier"] as String == "true"
    if (useBrigadier) {
        library("dev.jorel:commandapi-bukkit-shade:9.3.+")
        library("dev.jorel:commandapi-bukkit-kotlin:9.3.+")
    }

    library("de.miraculixx:kpaper:1.+")

    val ktorVersion = property("ktorVersion")
    library("io.ktor:ktor-client-core-jvm:$ktorVersion")
    library("io.ktor:ktor-client-cio-jvm:$ktorVersion")
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }
}

bukkit {
    main = "$group.${projectName.lowercase()}.${projectName}"
    apiVersion = "1.16"
    foliaSupported = foliaSupport
    name = properties["modrinthProjectId"] as String

    // Optionals
    load = BukkitPluginDescription.PluginLoadOrder.STARTUP
    depend = listOf()
    softDepend = listOf()
}
