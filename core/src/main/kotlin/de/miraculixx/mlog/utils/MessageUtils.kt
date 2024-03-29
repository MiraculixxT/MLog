package de.miraculixx.mlog.utils

import java.util.logging.Logger


const val cHighlight = "#5555FF"
const val cBase = "#AAAAAA"
const val cError = "#FF5555"
const val cSuccess = "#55FF55"
const val cMark = "#6e94ff"
const val cHide = "#1f2124"

data class RawMessage(
    val message: String,
    val color: String = cBase,
    val bold: Boolean = false,
    val italic: Boolean = false,
    val underlined: Boolean = false,
    val strikethrough: Boolean = false,
    val obfuscated: Boolean = false,
    val isConsole: Boolean = false,
    val hover: RawMessage? = null,
    val clickAction: Map<MessageClick, String> = mapOf(),
    val children: List<RawMessage> = listOf()
) {
    operator fun plus(other: RawMessage) = copy(children = children + listOf(other))

    fun link(url: String) = copy(clickAction = clickAction + (MessageClick.URL to url))

    fun command(command: String) = copy(clickAction = clickAction + (MessageClick.RUN_COMMAND to command))

    fun suggest(command: String) = copy(clickAction = clickAction + (MessageClick.SUGGEST_COMMAND to command))

    fun hover(hover: RawMessage) = copy(hover = hover)

    fun toRaw(): String {
        return buildString {
            append(message)
            children.forEach { append(it.toRaw()) }
        }
    }
}

fun cmp(text: String, color: String = cBase, bold: Boolean = false, italic: Boolean = false, strikethrough: Boolean = false, underlined: Boolean = false) =
    RawMessage(text, color, bold, italic, underlined, strikethrough)

fun consoleCmp(text: String, color: String = cBase, bold: Boolean = false, italic: Boolean = false, strikethrough: Boolean = false, underlined: Boolean = false) =
    RawMessage(text, color, bold, italic, underlined, strikethrough, isConsole = true)

typealias Target = (RawMessage) -> Unit

fun Target.sendMessage(message: RawMessage) = invoke(message)

fun Logger.sendMessage(message: RawMessage) {
    info(message.toRaw())
}

enum class MessageClick {
    URL,
    RUN_COMMAND,
    SUGGEST_COMMAND
}