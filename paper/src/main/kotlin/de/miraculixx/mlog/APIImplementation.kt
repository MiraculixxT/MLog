package de.miraculixx.mlog

import de.miraculixx.kpaper.extensions.console
import de.miraculixx.kpaper.extensions.server
import de.miraculixx.mlog.utils.cmp
import de.miraculixx.mlog.web.*
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.net.URL

class APIImplementation : MLogAPI() {
    init {
        INSTANCE = this
    }

    @Suppress("DEPRECATION") // JavaPlugin#description is deprecated but perfectly fine for our plugin and allows back-porting to older versions
    override fun registerLogSending(pluginInstance: Any?, pluginID: String?, webhookURL: URL?, files: Set<File>?, zip: Boolean?): Boolean {
        if (pluginInstance == null || pluginID == null || webhookURL == null || files.isNullOrEmpty()) return false
        if (pluginInstance !is JavaPlugin) return false // check if the plugin instance is a JavaPlugin
        if (pluginID != pluginInstance.name) return false // check if the plugin ID matches the plugin name

        if (WebClient.logBackData.containsKey(pluginID)) return false // unregister before re-registering

        val modData = LogPayloadModData(pluginInstance.name, pluginInstance.description.version)
        val serverData = LogPayloadServerData(server.minecraftVersion, server.version, System.getProperty("os.name"))
        val payloadData = LogPayload(
            files,
            webhookURL.toString(),
            zip ?: false,
            LogPayloadData("unknown", -1L, MLOG.description.version, modData, serverData)
        )

        WebClient.logBackData[pluginID] = payloadData
        console.sendMessage(prefix + cmp("Registered log sending for ${pluginInstance.name}"))
        return true
    }

    override fun unregisterLogSending(modInstance: Any?, modID: String?): Boolean {
        if (modInstance == null || modID == null) return false
        if (modInstance !is JavaPlugin) return false // check if the plugin instance is a JavaPlugin
        if (modID != modInstance.name) return false // check if the plugin ID matches the plugin name

        if (WebClient.logBackData.remove(modID) == null) return false // remove the log sending data
        console.sendMessage(prefix + cmp("Unregistered log sending for ${modInstance.name}"))
        return true
    }
}