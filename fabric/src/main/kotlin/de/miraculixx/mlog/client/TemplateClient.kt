package de.miraculixx.mlog.client

import com.mojang.brigadier.arguments.StringArgumentType
import de.miraculixx.mlog.LOGGER
import de.miraculixx.mlog.global.StringSuggestionProvider
import de.miraculixx.mlog.interfaces.CommandResponses
import de.miraculixx.mlog.web.WebClient
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import java.util.logging.Logger

object TemplateClient : ClientModInitializer, CommandResponses {
    private const val TYPE = "mod"

    private val confirmations: MutableMap<String, String> = mutableMapOf()

    override fun onInitializeClient() {
        LOGGER = Logger.getLogger("MLog-Client")

        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            dispatcher.register(
                ClientCommandManager.literal("mlog")
                    .executes { ctx ->
                        ctx.source.target().responseInfo(TYPE)
                        1
                    }.then(
                        ClientCommandManager.argument("plugin", StringArgumentType.string())
                            .suggests(StringSuggestionProvider(WebClient.logBackData.keys))
                            .executes { ctx ->
                                ctx.source.target().responseMod(StringArgumentType.getString(ctx, "plugin"), TYPE)
                                1
                            }
                            .then(
                                ClientCommandManager.argument("code", StringArgumentType.string())
                                    .executes { ctx ->
                                        ctx.source.target().responseCode(
                                            StringArgumentType.getString(ctx, "plugin"),
                                            StringArgumentType.getString(ctx, "code"),
                                            mutableSetOf(), "client", confirmations
                                        )
                                        1
                                    }
                            )
                    )
            )
        }
    }
}
