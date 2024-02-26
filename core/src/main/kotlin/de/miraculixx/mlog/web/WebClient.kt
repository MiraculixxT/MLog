package de.miraculixx.mlog.web

import de.miraculixx.mlog.configFolder
import de.miraculixx.mlog.consoleAudience
import de.miraculixx.mlog.prefix
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.kyori.adventure.text.Component
import java.io.File

object WebClient {
    private var ktor: HttpClient? = null
    val json = Json {
        prettyPrint = false
        encodeDefaults = true
    }

    val logBackData = mutableMapOf<String, LogPayload>()
    val tempFolder: File
        get() {
            val folder = File(configFolder, "temp")
            if (!folder.exists()) folder.mkdirs()
            return folder
        }

    suspend fun sendMultipart(url: String, files: Set<File>, data: LogPayloadData): Response {
        return try {
            if (ktor == null) ktor = HttpClient(CIO)
            val code = ktor?.post(url) {
                setBody(
                    MultiPartFormDataContent(formData {
                        append("data", json.encodeToString(data))

                        files.forEachIndexed { index, file ->
                            append("files.$index", file.readBytes(), Headers.build {
                                append(HttpHeaders.ContentDisposition, "filename=${file.name}")
                            })
                        }
                    })
                )
            }?.status ?: return Response.INTERNAL_ERROR
            consoleAudience.sendMessage(prefix.append(Component.text("Webhook endpoint responded with code $code")))
            when {
                code.isSuccess() -> Response.SUCCESS
                code == HttpStatusCode.Forbidden -> Response.INVALID_CODE //403
                else -> Response.API_ERROR
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Response.INTERNAL_ERROR
        }
    }

    enum class Response {
        SUCCESS, INVALID_CODE, API_ERROR, INTERNAL_ERROR
    }
}