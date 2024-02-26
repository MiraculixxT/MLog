package de.miraculixx.mlog.global

import de.miraculixx.mlog.LOGGER
import de.miraculixx.mlog.MLogAPI
import de.miraculixx.mlog.prefix
import de.miraculixx.mlog.utils.cmp
import de.miraculixx.mlog.utils.sendMessage
import de.miraculixx.mlog.web.*
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.DedicatedServerModInitializer
import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader
import java.io.File
import java.net.URL
import kotlin.jvm.optionals.getOrNull

class APIImplementation(private val mcVersion: String, private val isServer: Boolean) : MLogAPI() {
    init {
        INSTANCE = this
    }

    override fun registerLogSending(modInstance: Any?, modID: String?, webhookURL: URL?, files: Set<File>?, zip: Boolean?): Boolean {
        if (modInstance == null || modID == null || webhookURL == null || files.isNullOrEmpty()) return false
        // check if the plugin instance is some kind of mod
        if (modInstance !is ModInitializer && modInstance !is DedicatedServerModInitializer && modInstance !is ClientModInitializer) return false
        val fabric = FabricLoader.getInstance()
        val fabricContainer = fabric.getModContainer("fabricloader").getOrNull() ?: return false // fabric does not exist?
        val container = fabric.getModContainer(modID).getOrNull() ?: return false // check if mod id exist
        val mlogContainer = fabric.getModContainer("mlog").getOrNull() ?: return false // get mlog container

        if (WebClient.logBackData.containsKey(modID)) return false // unregister before re-registering

        val modMeta = container.metadata
        val modData = LogPayloadModData(modMeta.id, modMeta.version.friendlyString)
        val environmentData = LogPayloadServerData(mcVersion, "fabric-${fabricContainer.metadata.version.friendlyString}-${if (isServer) "server" else "client"}", System.getProperty("os.name"))
        val payloadData = LogPayload(
            files,
            webhookURL.toString(),
            zip ?: false,
            LogPayloadData("unknown", -1L, mlogContainer.metadata.version.friendlyString, modData, environmentData)
        )

        WebClient.logBackData[modID] = payloadData
        LOGGER.sendMessage(prefix + cmp("Registered log sending for ${container.metadata.name}"))
        return true

    }

    override fun unregisterLogSending(modInstance: Any?, modID: String?): Boolean {
        if (modInstance == null || modID == null) return false
        // check if the plugin instance is some kind of mod
        if (modInstance !is ModInitializer && modInstance !is DedicatedServerModInitializer && modInstance !is ClientModInitializer) return false
        val fabric = FabricLoader.getInstance()
        val container = fabric.getModContainer(modID).getOrNull() ?: return false // check if mod id exist

        if (WebClient.logBackData.remove(modID) == null) return false // remove the log sending data
        LOGGER.sendMessage(prefix + cmp("Unregistered log sending for ${container.metadata.name}"))
        return true
    }
}