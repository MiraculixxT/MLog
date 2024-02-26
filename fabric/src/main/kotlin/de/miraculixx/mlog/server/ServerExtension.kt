package de.miraculixx.mlog.server

import de.miraculixx.mlog.client.TemplateClient
import de.miraculixx.mlog.global.toComponent
import de.miraculixx.mlog.global.toRaw
import de.miraculixx.mlog.utils.RawMessage
import net.minecraft.commands.CommandSourceStack

fun CommandSourceStack.sendMessage(message: RawMessage) {
    sendSuccess({ message.toComponent() }, false)
}

fun RawMessage.send(target: CommandSourceStack) {
    if (isConsole) TemplateClient.LOGGER.info(toRaw())
    else target.sendMessage(this)
}

fun CommandSourceStack.target() = { msg: RawMessage -> msg.send(this) }
