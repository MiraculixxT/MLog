package de.miraculixx.mlog

import de.miraculixx.mlog.interfaces.CommandResponses
import de.miraculixx.mlog.web.WebClient
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.stringArgument

object RequestCommand : CommandResponses {
    private const val TYPE = "plugin"
    private val confirmations: MutableMap<String, String> = mutableMapOf()
    private val cooldown: MutableSet<String> = mutableSetOf()

    private val command = commandTree("mlogs") {
        anyExecutor { sender, _ ->
            sender.target().responseInfo(TYPE)
        }

        stringArgument("plugin") {
            withPermission(permissionCode)
            replaceSuggestions(ArgumentSuggestions.stringCollection { WebClient.logBackData.keys })

            anyExecutor { sender, args ->
                sender.target().responseMod(args[0] as String, TYPE)
            }

            stringArgument("code") {
                anyExecutor { sender, args ->
                    val plugin = args[0] as String
                    val code = args[1] as String
                    sender.target().responseCode(plugin, code, cooldown, sender.name, confirmations)
                }
            }
        }
    }

    fun disable() {
        CommandAPI.unregister("mlogs")
    }
}