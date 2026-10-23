package com.opencode.droid.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Session(
    val id: String,
    val title: String? = null,
    val agent: String = "build",
    val createdAt: Long? = null
)

@Serializable
data class MessagePart(
    val type: String = "text",
    val text: String = ""
)

@Serializable
data class Message(
    val id: String,
    val sessionId: String = "",
    val role: String = "assistant",
    val content: String = "",
    val parts: List<MessagePart> = emptyList(),
    val timestamp: Long? = null
)

@Serializable
data class PromptRequest(
    val parts: List<MessagePart> = emptyList(),
    val agent: String = "build"
)

@Serializable
data class ServerInfo(
    val version: String = "",
    val pid: Int? = null
)

@Serializable
data class WorkspaceFile(
    val path: String,
    val size: Long = 0,
    val isDirectory: Boolean = false
)
