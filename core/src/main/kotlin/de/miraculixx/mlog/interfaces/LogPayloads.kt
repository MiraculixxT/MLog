package de.miraculixx.mlog.interfaces

import de.miraculixx.mlog.LOGGER
import de.miraculixx.mlog.prefix
import de.miraculixx.mlog.utils.Zipping
import de.miraculixx.mlog.utils.cmp
import de.miraculixx.mlog.utils.regexIP
import de.miraculixx.mlog.utils.sendMessage
import de.miraculixx.mlog.web.LogPayloadData
import de.miraculixx.mlog.web.WebClient
import java.io.File

interface LogPayloads {
    val allowedFolder: Set<String>
        get() = setOf("logs", "crash-reports", "plugins", "mods", "config")
    val idChars: String
        get() = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"

    fun redactIPAddresses(source: File): File {
        LOGGER.sendMessage(cmp("Redacting IP addresses from ${source.path}..."))
        val redactedTempFile = File(WebClient.tempFolder, "${source.nameWithoutExtension}.redacted.${source.extension}")
        redactedTempFile.bufferedWriter().use { writer ->
            source.forEachLine { line ->
                writer.write(line.replace(regexIP, "<ip-address>") + "\n")
            }
        }
        return redactedTempFile.apply { deleteOnExit() }
    }

    fun prepareFiles(targets: Set<File>, zip: Boolean): Set<File> {
        LOGGER.sendMessage(cmp("Preparing ${targets.size} files (zipped $zip) to send..."))
        val processedFiles = targets.mapNotNull { file ->
            val extension = file.extension
            when {
                // Receive the latest crash report if crash report folder is requested
                file.name == "crash-reports" -> {
                    file.listFiles()?.filter { it.isFile && extension == "txt" }
                        ?.maxByOrNull { it.lastModified() }
                        ?.let { redactIPAddresses(it) }
                }

                // Check if the file is in an allowed directory
                !isInAllowedDirectory(file.path) -> null

                // Zip the file if it is a directory
                file.isDirectory -> {
                    LOGGER.sendMessage(cmp("Zipping folder ${file.path}..."))
                    val tempZipFile = File(WebClient.tempFolder, "${file.name}.${getRandomID()}.zip")
                    Zipping.zipFolder(file, tempZipFile)
                    tempZipFile.apply { deleteOnExit() }
                }

                // Redact IP addresses from log files
                extension == "log" || extension == "txt" -> redactIPAddresses(file)

                else -> file
            }
        }

        return if (zip) {
            LOGGER.sendMessage(cmp("Zipping all ${processedFiles.size} files together..."))
            val tempZipFile = File(WebClient.tempFolder, "${getRandomID()}.zip").apply { deleteOnExit() }
            Zipping.zipFiles(processedFiles, tempZipFile)
            setOf(tempZipFile)
        } else processedFiles.toSet()
    }

    suspend fun sendPayload(webhook: String, targets: Set<File>, payloadData: LogPayloadData): WebClient.Response {
        LOGGER.sendMessage(cmp("Sending ${targets.size} files to $webhook..."))
        return WebClient.sendMultipart(webhook, targets, payloadData)
    }


    //
    // Internal functions
    //

    private fun getRandomID(length: Int = 8) = (1..length).joinToString("") { idChars.random().toString() }

    private fun isInAllowedDirectory(path: String): Boolean {
        val firstFolder = path.substringBefore(File.separatorChar)
        return firstFolder != path && firstFolder in allowedFolder
    }
}