package de.miraculixx.mlog

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import java.io.File

lateinit var configFolder: File
lateinit var consoleAudience: Audience
val prefix = Component.text("MLog", NamedTextColor.BLUE)
    .append(Component.text(" >>").color(NamedTextColor.DARK_GRAY))
    .append(Component.text(" ").color(NamedTextColor.GRAY))
