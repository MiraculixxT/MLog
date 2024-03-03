package de.miraculixx.mlog.global

import de.miraculixx.mlog.utils.RawMessage
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component

fun RawMessage.toComponent(): Component {
    var final = Component.literal(message)
        .withColor(color.substring(1).toInt(16))
        .withStyle(
            *buildSet {
                if (bold) add(ChatFormatting.BOLD)
                if (italic) add(ChatFormatting.ITALIC)
                if (strikethrough) add(ChatFormatting.STRIKETHROUGH)
                if (underlined) add(ChatFormatting.UNDERLINE)
                if (obfuscated) add(ChatFormatting.OBFUSCATED)
            }.toTypedArray()
        )
    children.forEach { final = final.append(it.toComponent()) }
    return final
}
