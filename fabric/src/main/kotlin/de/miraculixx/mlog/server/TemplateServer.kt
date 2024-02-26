package de.miraculixx.mlog.server

import com.mojang.brigadier.arguments.StringArgumentType
import de.miraculixx.mlog.client.TemplateClient.responseCode
import de.miraculixx.mlog.client.TemplateClient.responseInfo
import de.miraculixx.mlog.client.TemplateClient.responseMod
import de.miraculixx.mlog.global.StringSuggestionProvider
import de.miraculixx.mlog.permissionCode
import de.miraculixx.mlog.web.WebClient
import me.lucko.fabric.api.permissions.v0.Permissions
import net.fabricmc.api.DedicatedServerModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.commands.Commands
import java.util.logging.Logger

object TemplateServer : DedicatedServerModInitializer {
    val LOGGER = Logger.getLogger("MLog-Server")
    private const val TYPE = "mod"

    private val confirmations: MutableMap<String, String> = mutableMapOf()
    private val cooldown: MutableSet<String> = mutableSetOf()

    override fun onInitializeServer() {

        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
            dispatcher.register(
                Commands.literal("mlog")
                    .executes { ctx ->
                        ctx.source.target().responseInfo(TYPE)
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
