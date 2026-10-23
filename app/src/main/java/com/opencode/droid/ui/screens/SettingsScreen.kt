package com.opencode.droid.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.opencode.droid.viewmodel.SessionViewModel

@Composable
fun SettingsScreen(viewModel: SessionViewModel) {
    val url by viewModel.serverUrl.collectAsStateWithLifecycle()
    val token by viewModel.apiToken.collectAsStateWithLifecycle()
    var newUrl by remember(url) { mutableStateOf(url) }
    var newToken by remember(token) { mutableStateOf(token) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Server settings", modifier = Modifier.padding(bottom = 8.dp))
        OutlinedTextField(
            value = newUrl,
            onValueChange = { newUrl = it },
            label = { Text("Server URL") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = newToken,
            onValueChange = { newToken = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )
        Button(onClick = { viewModel.updateSettings(newUrl, newToken) }) {
            Text("Save")
        }
    }
}
