package de.miraculixx.mlog.utils


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
    var followMessage: RawMessage? = null
) {
    operator fun plus(other: RawMessage) = apply { followMessage = other }
}

fun cmp(text: String, color: String = cBase, bold: Boolean = false, italic: Boolean = false, strikethrough: Boolean = false, underlined: Boolean = false) =
    RawMessage(text, color, bold, italic, underlined, strikethrough)

fun consoleCmp(text: String, color: String = cBase, bold: Boolean = false, italic: Boolean = false, strikethrough: Boolean = false, underlined: Boolean = false) =
    RawMessage(text, color, bold, italic, underlined, strikethrough, isConsole = true)

typealias Target = (RawMessage) -> Unit

fun Target.sendMessage(message: RawMessage) = invoke(message)
