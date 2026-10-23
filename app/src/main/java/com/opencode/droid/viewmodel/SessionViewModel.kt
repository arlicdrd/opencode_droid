package com.opencode.droid.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.opencode.droid.data.SettingsDataStore
import com.opencode.droid.data.model.*
import com.opencode.droid.data.network.OpenCodeApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SessionViewModel(application: Application) : AndroidViewModel(application) {
    private val settingsDataStore = SettingsDataStore(application)
    private val apiClient = OpenCodeApiClient("http://10.0.2.2:4096", "")

    private val _sessions = MutableStateFlow<List<Session>>(emptyList())
    val sessions: StateFlow<List<Session>> = _sessions.asStateFlow()

    private val _currentSession = MutableStateFlow<Session?>(null)
    val currentSession: StateFlow<Session?> = _currentSession.asStateFlow()

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    private val _workspaceFiles = MutableStateFlow<List<WorkspaceFile>>(emptyList())
    val workspaceFiles: StateFlow<List<WorkspaceFile>> = _workspaceFiles.asStateFlow()

    private val _serverUrl = MutableStateFlow("http://10.0.2.2:4096")
    val serverUrl: StateFlow<String> = _serverUrl.asStateFlow()

    private val _apiToken = MutableStateFlow("")
    val apiToken: StateFlow<String> = _apiToken.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        viewModelScope.launch {
            val url = settingsDataStore.serverUrlFlow.first()
            val token = settingsDataStore.apiTokenFlow.first()
            _serverUrl.value = url
            _apiToken.value = token
            apiClient.updateConfig(url, token)
            loadData()
        }
    }

    fun updateSettings(newUrl: String, newToken: String) {
        viewModelScope.launch {
            settingsDataStore.saveSettings(newUrl, newToken)
            _serverUrl.value = newUrl
            _apiToken.value = newToken
            apiClient.updateConfig(newUrl, newToken)
            _currentSession.value = null
            loadData()
        }
    }

    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val list = apiClient.getSessions()
                _sessions.value = list
                if (list.isNotEmpty() && _currentSession.value == null) selectSession(list.first())
                _workspaceFiles.value = apiClient.getWorkspaceFiles()
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectSession(session: Session) {
        _currentSession.value = session
        viewModelScope.launch {
            try {
                _messages.value = apiClient.getMessages(session.id)
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            }
        }
    }

    fun createSession(title: String = "New session") {
        viewModelScope.launch {
            try {
                // OpenCode creates session with optional body; minimal: reload list
                loadData()
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            }
        }
    }

    fun sendMessage(prompt: String) {
        val session = _currentSession.value ?: return
        _messages.value = _messages.value + Message(
            id = System.currentTimeMillis().toString(),
            sessionId = session.id, role = "user", content = prompt,
            parts = listOf(MessagePart(text = prompt))
        )
        viewModelScope.launch {
            try {
                val reply = apiClient.sendMessage(session.id, prompt)
                _messages.value = _messages.value + reply
            } catch (e: Exception) {
                _messages.value = _messages.value + Message(
                    id = System.currentTimeMillis().toString(),
                    sessionId = session.id, role = "assistant",
                    content = "Error: ${e.localizedMessage}"
                )
            }
        }
    }
}
