package de.miraculixx.mlog.utils

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration



val cHighlight: NamedTextColor = NamedTextColor.BLUE
val cBase: NamedTextColor = NamedTextColor.GRAY
val cError: NamedTextColor = NamedTextColor.RED
val cSuccess: NamedTextColor = NamedTextColor.GREEN
val cMark = TextColor.fromHexString("#6e94ff")!!
val cHide = TextColor.fromHexString("#1f2124")!!

fun cmp(text: String, color: TextColor = cBase, bold: Boolean = false, italic: Boolean = false, strikethrough: Boolean = false, underlined: Boolean = false): Component {
    return Component.text(text).color(color)
        .decorations(
            mapOf(
                TextDecoration.BOLD to TextDecoration.State.byBoolean(bold),
                TextDecoration.ITALIC to TextDecoration.State.byBoolean(italic),
                TextDecoration.STRIKETHROUGH to TextDecoration.State.byBoolean(strikethrough),
                TextDecoration.UNDERLINED to TextDecoration.State.byBoolean(underlined)
            )
        )
}

operator fun Component.plus(other: Component): Component {
    return append(other)
}
