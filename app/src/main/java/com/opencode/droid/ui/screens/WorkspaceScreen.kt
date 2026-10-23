package com.opencode.droid.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.opencode.droid.viewmodel.SessionViewModel

@Composable
fun WorkspaceScreen(viewModel: SessionViewModel) {
    val files by viewModel.workspaceFiles.collectAsStateWithLifecycle()
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Workspace files", modifier = Modifier.padding(bottom = 8.dp))
        files.forEach { f ->
            Text(if (f.isDirectory) "[dir] ${f.path}" else f.path)
        }
    }
}
