package com.opencode.droid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.opencode.droid.ui.screens.ChatScreen
import com.opencode.droid.ui.screens.SettingsScreen
import com.opencode.droid.ui.screens.WorkspaceScreen
import com.opencode.droid.ui.theme.OpenCodeDroidTheme
import com.opencode.droid.viewmodel.SessionViewModel

private data class Tab(val route: String, val label: String, val icon: ImageVector)

class MainActivity : ComponentActivity() {
    private val viewModel: SessionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OpenCodeDroidTheme {
                val nav = rememberNavController()
                val tabs = listOf(
                    Tab("chat", "Chat", Icons.Filled.Chat),
                    Tab("workspace", "Workspace", Icons.Filled.Folder),
                    Tab("settings", "Settings", Icons.Filled.Settings)
                )
                val current = nav.currentBackStackEntryAsState().value?.destination?.route
                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            tabs.forEach { tab ->
                                NavigationBarItem(
                                    selected = current == tab.route,
                                    onClick = {
                                        nav.navigate(tab.route) {
                                            popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = { Icon(tab.icon, contentDescription = tab.label) },
                                    label = { Text(tab.label) }
                                )
                            }
                        }
                    }
                ) { padding ->
                    NavHost(nav, startDestination = "chat", modifier = Modifier.padding(padding)) {
                        composable("chat") { ChatScreen(viewModel) }
                        composable("workspace") { WorkspaceScreen(viewModel) }
                        composable("settings") { SettingsScreen(viewModel) }
                    }
                }
            }
        }
    }
}
