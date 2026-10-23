package com.opencode.droid.data.network

import com.opencode.droid.data.model.*
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import okhttp3.Credentials

class OpenCodeApiClient(
    private var baseUrl: String,
    private var password: String,
    private val username: String = "opencode"
) {
    private var client = createClient()

    private fun createClient() = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true; isLenient = true })
        }
        install(Logging) { level = LogLevel.INFO }
        defaultRequest {
            url(baseUrl.trim().trimEnd('/') + "/api/")
            if (password.isNotBlank()) header(HttpHeaders.Authorization, Credentials.basic(username, password))
            contentType(ContentType.Application.Json)
        }
    }

    fun updateConfig(newUrl: String, newPassword: String) {
        baseUrl = newUrl.trim().trimEnd('/')
        password = newPassword.trim()
        client.close()
        client = createClient()
    }

    suspend fun getInfo(): ServerInfo = client.get("info").body()

    suspend fun getSessions(): List<Session> = client.get("session").body()

    suspend fun getMessages(sessionId: String): List<Message> =
        client.get("session/$sessionId/message").body()

    suspend fun sendMessage(sessionId: String, prompt: String, agent: String = "build"): Message {
        val req = PromptRequest(parts = listOf(MessagePart(text = prompt)), agent = agent)
        return client.post("session/$sessionId/prompt") { setBody(req) }.body()
    }

    suspend fun getWorkspaceFiles(): List<WorkspaceFile> = try {
        client.get("fs/list?path=.").body()
    } catch (e: Exception) {
        emptyList()
    }
}
