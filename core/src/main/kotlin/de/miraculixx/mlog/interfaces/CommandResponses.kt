package de.miraculixx.mlog.interfaces

import de.miraculixx.mlog.prefix
import de.miraculixx.mlog.utils.*
import de.miraculixx.mlog.web.WebClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.kyori.adventure.audience.Audience
import java.io.File

interface CommandResponses: LogPayloads {
    fun Audience.responseInfo(type: String) {
        sendMessage(
            prefix + cmp(
                "Easily send important logs and $type configurations to their developers to speed up the debugging and support process.\n" +
                        "You will receive a code from the developer which you can enter here to securely send all specified files.\n" +
                        "Before sending, you can review the files and cancel the request at any time."
            )
        )
        sendMessage(prefix + cmp("Usage: ") + cmp("/mlogs <$type> <code>", cMark))
    }

    fun Audience.responseMod(mod: String, type: String) {
        val data = WebClient.logBackData[mod]
        if (data == null) {
            noSupport(mod)
            return
        }
        sendMessage(prefix + cmp("The $type ", cHighlight) + cmp(mod, cMark) + cmp(" requests following files to be sent to their developers on support:"))
        printFiles(data.files)
    }

    fun Audience.responseCode(mod: String, code: String, cooldown: MutableSet<String>, confirmations: MutableMap<Audience, String>) {
        val data = WebClient.logBackData[mod]
        if (data == null) {
            noSupport(mod)
            return
        }

        if (confirmations.containsKey(this)) {
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
                    WebClient.Response.API_ERROR -> sendMessage(prefix + cmp("The endpoint responded with an error. Please notify the developers about this behaviour!", cError))
                }
            }
        } else {
            sendMessage(prefix + cmp("You are about to send the following files to the developer of ") + cmp(mod, cMark) + cmp(":"))
            printFiles(data.files)
            sendMessage(prefix + cmp("Confirm by entering the command again!", cSuccess))
            confirmations[this] = mod
        }
    }

    private fun Audience.printFiles(files: Set<File>) {
        files.forEach { sendMessage(cmp("· ", cHighlight) + cmp(it.path)) }
    }

    private fun Audience.noSupport(mod: String) {
        sendMessage(prefix + cmp("This mod does not support easy logs yet!", cError))
        sendMessage(prefix + cmp("Contact the developer of ", cError) + cmp(mod, cError, underlined = true) + cmp(" and ask them to add support for MLogs.", cError))
    }
}