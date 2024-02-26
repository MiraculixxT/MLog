package de.miraculixx.mlog.server

import de.miraculixx.mlog.LOGGER
import de.miraculixx.mlog.global.toComponent
import de.miraculixx.mlog.utils.RawMessage
import net.minecraft.commands.CommandSourceStack

fun CommandSourceStack.sendMessage(message: RawMessage) {
    sendSuccess({ message.toComponent() }, false)
}

fun RawMessage.send(target: CommandSourceStack) {
    if (isConsole) LOGGER.info(toRaw())
    else target.sendMessage(this)
}

fun CommandSourceStack.target() = { msg: RawMessage -> msg.send(this) }
