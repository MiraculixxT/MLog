package de.miraculixx.mlog.global

import de.miraculixx.mlog.utils.MessageClick
import de.miraculixx.mlog.utils.RawMessage
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.HoverEvent
import net.minecraft.network.chat.Style

fun RawMessage.toComponent(): Component {
    var final = Component.literal(message)
        .withStyle(Style.EMPTY.apply {
            withColor(this@toComponent.color.substring(1).toInt(16))
            if (bold) withBold(true)
            if (italic) withItalic(true)
            if (strikethrough) withStrikethrough(true)
            if (underlined) withUnderlined(true)
            if (obfuscated) withObfuscated(true)
            clickAction.forEach { (action, value) ->
                val mcAction = when (action) {
                    MessageClick.URL -> ClickEvent.Action.OPEN_URL
                    MessageClick.RUN_COMMAND -> ClickEvent.Action.RUN_COMMAND
                    MessageClick.SUGGEST_COMMAND -> ClickEvent.Action.SUGGEST_COMMAND
                }
                withClickEvent(ClickEvent(mcAction, value))
            }
            hover?.let { withHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, it.toComponent())) }
        })
    children.forEach { final = final.append(it.toComponent()) }
    return final
}
