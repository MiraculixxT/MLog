package de.miraculixx.mlog

import de.miraculixx.mlog.utils.MessageClick
import de.miraculixx.mlog.utils.RawMessage
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit

fun RawMessage.toAdventure(): Component {
    var cmp = Component.text(message, TextColor.fromHexString(color)).apply {
        decorations(
            mapOf(
                TextDecoration.BOLD to TextDecoration.State.byBoolean(bold),
                TextDecoration.ITALIC to TextDecoration.State.byBoolean(italic),
                TextDecoration.STRIKETHROUGH to TextDecoration.State.byBoolean(strikethrough),
                TextDecoration.UNDERLINED to TextDecoration.State.byBoolean(underlined),
                TextDecoration.OBFUSCATED to TextDecoration.State.byBoolean(obfuscated)
            )
        )
        clickAction.forEach { (action, value) ->
            clickEvent(when (action) {
                MessageClick.URL -> ClickEvent.openUrl(value)
                MessageClick.RUN_COMMAND -> ClickEvent.runCommand(value)
                MessageClick.SUGGEST_COMMAND -> ClickEvent.suggestCommand(value)
            })
        }
        hover?.let { hoverEvent(asHoverEvent().value(it.toAdventure())) }
    }
    children.forEach { cmp = cmp.append(it.toAdventure()) }
    return cmp
}

fun Audience.sendMessage(message: RawMessage) {
    sendMessage(message.toAdventure())
}

fun RawMessage.send(target: Audience, console: Audience) {
    if (isConsole) console.sendMessage(this)
    else target.sendMessage(this)
}

fun Audience.target() = { msg: RawMessage ->
    msg.send(this, Bukkit.getConsoleSender())
}
