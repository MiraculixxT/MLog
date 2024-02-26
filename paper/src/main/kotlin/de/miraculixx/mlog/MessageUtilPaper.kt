package de.miraculixx.mlog

import de.miraculixx.kpaper.extensions.console
import de.miraculixx.mlog.utils.RawMessage
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration

fun RawMessage.toAdventure(): Component {
    return Component.text(message, TextColor.fromHexString(color)).apply {
        decorations(
            mapOf(
                TextDecoration.BOLD to TextDecoration.State.byBoolean(bold),
                TextDecoration.ITALIC to TextDecoration.State.byBoolean(italic),
                TextDecoration.STRIKETHROUGH to TextDecoration.State.byBoolean(strikethrough),
                TextDecoration.UNDERLINED to TextDecoration.State.byBoolean(underlined),
                TextDecoration.OBFUSCATED to TextDecoration.State.byBoolean(obfuscated)
            )
        )
        followMessage?.toAdventure()?.let { append(it) }
    }
}

fun Audience.sendMessage(message: RawMessage) {
    sendMessage(message.toAdventure())
}

fun RawMessage.send(target: Audience, console: Audience) {
    if (isConsole) console.sendMessage(this)
    else target.sendMessage(this)
}

fun Audience.target() = { msg: RawMessage -> msg.send(this, console) }
