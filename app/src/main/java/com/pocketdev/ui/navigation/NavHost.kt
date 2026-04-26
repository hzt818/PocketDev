package com.pocketdev.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Source
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pocketdev.ui.screens.chat.ChatScreen
import com.pocketdev.ui.screens.editor.EditorScreen
import com.pocketdev.ui.screens.ollama.OllamaScreen
import com.pocketdev.ui.screens.pc.PcConnectionScreen
import com.pocketdev.ui.screens.repos.ReposScreen
import com.pocketdev.ui.screens.settings.SettingsScreen

data class BottomNavItem(
    val screen: Screen,
    val icon: ImageVector,
    val label: String
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Chat, Icons.Default.Chat, "Chat"),
    BottomNavItem(Screen.Repos, Icons.Default.Source, "Repos"),
    BottomNavItem(Screen.Editor, Icons.Default.Code, "Editor"),
    BottomNavItem(Screen.Settings, Icons.Default.Settings, "Settings")
)

@Composable
fun PocketDevNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.screen.route } == true,
                        onClick = {
                            navController.navigate(item.screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Chat.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Chat.route) { ChatScreen() }
            composable(Screen.Settings.route) { SettingsScreen() }
            composable(Screen.Repos.route) { ReposScreen() }
            composable(Screen.Ollama.route) { OllamaScreen() }
            composable(Screen.PcConnection.route) { PcConnectionScreen() }
            composable(Screen.Editor.route) { EditorScreen() }
        }
    }
}
