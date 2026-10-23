package com.opencode.droid.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "opencode_settings")

class SettingsDataStore(private val context: Context) {
    companion object {
        private val SERVER_URL_KEY = stringPreferencesKey("server_url")
        private val API_TOKEN_KEY = stringPreferencesKey("api_token")
    }

    val serverUrlFlow: Flow<String> = context.dataStore.data
        .map { it[SERVER_URL_KEY] ?: "http://10.0.2.2:4096" }

    val apiTokenFlow: Flow<String> = context.dataStore.data
        .map { it[API_TOKEN_KEY] ?: "" }

    suspend fun saveSettings(serverUrl: String, apiToken: String) {
        context.dataStore.edit {
            it[SERVER_URL_KEY] = serverUrl
            it[API_TOKEN_KEY] = apiToken
        }
    }
}
