package de.miraculixx.mlog

import de.miraculixx.kpaper.main.KPaper
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIBukkitConfig

class MLog : KPaper() {
    companion object {
        lateinit var INSTANCE: KPaper
    }

    private lateinit var mLogAPIImplementation: APIImplementation

    override fun load() {
        INSTANCE = this
        CommandAPI.onLoad(CommandAPIBukkitConfig(this).silentLogs(true))
        configFolder = dataFolder
        mLogAPIImplementation = APIImplementation()
        RequestCommand
    }

    override fun startup() {
        CommandAPI.onEnable()
    }

    override fun shutdown() {
        RequestCommand.disable()
        CommandAPI.onDisable()
    }
}

val MLOG by lazy { MLog.INSTANCE }
