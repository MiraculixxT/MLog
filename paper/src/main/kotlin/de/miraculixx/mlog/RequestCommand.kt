package de.miraculixx.mlog

import de.miraculixx.mlog.interfaces.CommandResponses
import de.miraculixx.mlog.web.WebClient
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.stringArgument
import net.kyori.adventure.audience.Audience

object RequestCommand : CommandResponses {
    private const val type = "plugin"
    private val confirmations: MutableMap<Audience, String> = mutableMapOf()
    private val cooldown: MutableSet<String> = mutableSetOf()

    private val command = commandTree("mlogs") {
        anyExecutor { sender, _ ->
            sender.responseInfo(type)
        }

        stringArgument("plugin") {
            replaceSuggestions(ArgumentSuggestions.stringCollection { WebClient.logBackData.keys })

            anyExecutor { sender, args ->
                sender.responseMod(args[0] as String, type)
            }

            stringArgument("code") {
                anyExecutor { sender, args ->
                    val plugin = args[0] as String
                    val code = args[1] as String
                    sender.responseCode(plugin, code, cooldown, confirmations)
                }
            }
        }
    }

    fun disable() {
        CommandAPI.unregister("mlogs")
    }
}