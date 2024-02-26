plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx", "kotlinx-serialization-json", "1.+")
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.+")

    val ktorVersion = property("ktorVersion")
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
        options.release.set(17)
    }
    compileKotlin {
        kotlinOptions.jvmTarget = "17"
    }
}