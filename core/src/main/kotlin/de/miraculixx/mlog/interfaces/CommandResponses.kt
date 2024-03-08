package de.miraculixx.mlog.interfaces

import de.miraculixx.mlog.prefix
import de.miraculixx.mlog.utils.*
import de.miraculixx.mlog.utils.Target
import de.miraculixx.mlog.web.WebClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

interface CommandResponses : LogPayloads {
    fun Target.responseInfo(type: String, version: String, isServer: Boolean) {
        sendMessage(prefix + cmp("Version: ") + cmp(version, cMark))
        val link = "https://mutils.net/mlog"
        sendMessage(prefix + cmp("Information: ") + cmp(link, cMark).link(link).hover(cmp("Click to open the MLog documentation")))
        sendMessage(prefix + cmp("Usage: ") + cmp("/mlog${if (isServer) "-server" else ""} <$type> <code>", cMark))
    }

    fun Target.responseMod(mod: String, type: String) {
        val data = WebClient.logBackData[mod]
        if (data == null) {
            noSupport(mod)
            return
        }
        sendMessage(prefix + cmp("The $type ") + cmp(mod, cMark) + cmp(" requests following files on support:"))
        printFiles(data.files)
    }

    fun Target.responseCode(mod: String, code: String, cooldown: MutableSet<String>, executor: String, confirmations: MutableMap<String, String>) {
        val data = WebClient.logBackData[mod]
        if (data == null) {
            noSupport(mod)
            return
        }

        if (confirmations.containsKey(executor)) {
            confirmations.remove(executor)
            sendMessage(prefix + cmp("Sending files to the developer..."))
            CoroutineScope(Dispatchers.Default).launch {
                val finalFiles = prepareFiles(data.files, data.zip)
                when (sendPayload(data.webhook, finalFiles, data.data)) {
                    WebClient.Response.SUCCESS -> {
                        cooldown.add(mod)
                        sendMessage(prefix + cmp("Files sent successfully!", cSuccess))
                    }

                    WebClient.Response.INVALID_CODE -> sendMessage(prefix + cmp("The code you entered is invalid for $mod!", cError))
                    WebClient.Response.INTERNAL_ERROR -> sendMessage(prefix + cmp("An internal error occurred while sending the files! Please check console for more information", cError))
                    WebClient.Response.RATE_LIMIT -> sendMessage(prefix + cmp("Please wait a moment before trying again.", cError))
                    WebClient.Response.API_ERROR -> sendMessage(prefix + cmp("The endpoint responded with an error. Please notify the developers about this behaviour!", cError))
                }
            }
        } else {
            sendMessage(prefix + cmp("Following files will be send to the developers of ") + cmp(mod, cMark) + cmp(":"))
            printFiles(data.files)
            sendMessage(prefix + cmp("Confirm by entering the command again!", cSuccess))
            confirmations[executor] = mod
        }
    }

    private fun Target.printFiles(files: Set<File>) {
        files.forEach { sendMessage(cmp("· ", cHighlight) + cmp(it.path)) }
    }

    private fun Target.noSupport(mod: String) {
        sendMessage(prefix + cmp("This mod does not support MLog yet!", cError))
        sendMessage(prefix + cmp("Contact the developer of ", cError) + cmp(mod, cError, underlined = true) + cmp(" and ask them to add support for MLogs.", cError))
    }
}