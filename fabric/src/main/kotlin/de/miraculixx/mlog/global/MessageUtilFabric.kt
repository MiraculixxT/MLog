package de.miraculixx.mlog.global

import de.miraculixx.mlog.utils.RawMessage
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component

fun RawMessage.toComponent(): Component {
    return Component.literal(message)
        .withColor(color.toInt(16))
        .withStyle(
            *buildSet {
                if (bold) add(ChatFormatting.BOLD)
                if (italic) add(ChatFormatting.ITALIC)
                if (strikethrough) add(ChatFormatting.STRIKETHROUGH)
                if (underlined) add(ChatFormatting.UNDERLINE)
                if (obfuscated) add(ChatFormatting.OBFUSCATED)
            }.toTypedArray()
        ).apply {
            followMessage?.toComponent()?.let { append(it) }
        }
}
