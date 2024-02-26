pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

rootProject.name = "MLog"

include(":core")
include(":paper")
include(":fabric")
include(":api")