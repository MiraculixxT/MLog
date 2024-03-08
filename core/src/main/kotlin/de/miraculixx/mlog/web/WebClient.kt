package de.miraculixx.mlog.web

import de.miraculixx.mlog.LOGGER
import de.miraculixx.mlog.configFolder
import de.miraculixx.mlog.prefix
import de.miraculixx.mlog.utils.cmp
import de.miraculixx.mlog.utils.sendMessage
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

object WebClient {
    private var ktor: HttpClient? = null
    private val json = Json {
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
            val response = ktor?.post(url) {
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
                header("Code", data.code)
            }
            val code = response?.status ?: return Response.INTERNAL_ERROR
            LOGGER.sendMessage(cmp("Webhook endpoint responded with code $code"))
            val type = when {
                code.isSuccess() -> Response.SUCCESS
                code == HttpStatusCode.Forbidden -> Response.INVALID_CODE //403
                code == HttpStatusCode.TooManyRequests -> Response.RATE_LIMIT //429
                else -> Response.API_ERROR
            }
            if (type != Response.SUCCESS) LOGGER.sendMessage(cmp("Error: ${response.bodyAsText()}"))
            type
        } catch (e: Exception) {
            e.printStackTrace()
            Response.INTERNAL_ERROR
        }
    }

    enum class Response {
        SUCCESS, INVALID_CODE, API_ERROR, RATE_LIMIT, INTERNAL_ERROR
    }
}