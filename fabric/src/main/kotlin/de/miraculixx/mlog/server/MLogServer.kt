package de.miraculixx.mlog.server

import com.mojang.brigadier.arguments.StringArgumentType
import de.miraculixx.mlog.LOGGER
import de.miraculixx.mlog.global.APIImplementation
import de.miraculixx.mlog.global.StringSuggestionProvider
import de.miraculixx.mlog.interfaces.CommandResponses
import de.miraculixx.mlog.permissionCode
import de.miraculixx.mlog.web.WebClient
import me.lucko.fabric.api.permissions.v0.Permissions
import net.fabricmc.api.DedicatedServerModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.commands.Commands
import java.util.logging.Logger
import kotlin.jvm.optionals.getOrNull

class MLogServer : DedicatedServerModInitializer, CommandResponses {
    private val TYPE = "mod"
    private lateinit var apiImplementation: APIImplementation

    private val confirmations: MutableMap<String, String> = mutableMapOf()
    private val cooldown: MutableSet<String> = mutableSetOf()

    override fun onInitializeServer() {
        LOGGER = Logger.getLogger("MLog-Server")


        ServerLifecycleEvents.SERVER_STARTING.register { server ->
            apiImplementation = APIImplementation(server.serverVersion, true)
        }

        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
            dispatcher.register(
                Commands.literal("mlog-server")
                    .executes { ctx ->
                        ctx.source.target().responseInfo(TYPE, FabricLoader.getInstance().getModContainer("mlog").getOrNull()?.metadata?.version?.friendlyString ?: "unknown", true)
                        1
                    }.then(
                        Commands.argument("plugin", StringArgumentType.string())
                            .requires { ctx -> Permissions.check(ctx, permissionCode, 2) }
                            .suggests(StringSuggestionProvider(WebClient.logBackData.keys))
                            .executes { ctx ->
                                ctx.source.target().responseMod(StringArgumentType.getString(ctx, "plugin"), TYPE)
                                1
                            }
                            .then(
                                Commands.argument("code", StringArgumentType.string())
                                    .executes { ctx ->
                                        ctx.source.target().responseCode(
                                            StringArgumentType.getString(ctx, "plugin"),
                                            StringArgumentType.getString(ctx, "code"),
                                            cooldown, ctx.source.textName, confirmations
                                        )
                                        1
                                    }
                            )
                    )
            )
        }
    }
}
