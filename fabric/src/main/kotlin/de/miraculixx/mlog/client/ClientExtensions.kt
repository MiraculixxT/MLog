package de.miraculixx.mlog.client

import de.miraculixx.mlog.global.toComponent
import de.miraculixx.mlog.global.toRaw
import de.miraculixx.mlog.utils.RawMessage
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource

fun FabricClientCommandSource.sendMessage(message: RawMessage) {
    sendFeedback(message.toComponent())
}

fun RawMessage.send(target: FabricClientCommandSource) {
    if (isConsole) TemplateClient.LOGGER.info(toRaw())
    else target.sendMessage(this)
}

fun FabricClientCommandSource.target() = { msg: RawMessage -> msg.send(this) }