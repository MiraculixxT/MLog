package de.miraculixx.mlog

import de.miraculixx.kpaper.main.KPaper
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIBukkitConfig
import java.io.File
import java.net.URL

class MLog : KPaper() {
    companion object {
        lateinit var INSTANCE: KPaper
    }

    private lateinit var mLogAPIImplementation: APIImplementation

    override fun load() {
        INSTANCE = this
        LOGGER = logger
        CommandAPI.onLoad(CommandAPIBukkitConfig(this).silentLogs(true))
        configFolder = dataFolder
        mLogAPIImplementation = APIImplementation()
        RequestCommand
    }

    override fun startup() {
        CommandAPI.onEnable()

        // Register self logging
        val success = MLogAPI.INSTANCE.registerLogSending(this, "mlog", URL("https://mlog.mutils.net/webhook/mlog/908621996009619477"), setOf(File("logs/latest.log")), false)
        if (!success) {
            logger.warning("Failed to register self logging")
        }
    }

    override fun shutdown() {
        RequestCommand.disable()
        CommandAPI.onDisable()
    }
}

val MLOG by lazy { MLog.INSTANCE }
