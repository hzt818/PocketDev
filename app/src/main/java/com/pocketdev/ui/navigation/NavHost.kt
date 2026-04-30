package com.pocketdev.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Source
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.pocketdev.R
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pocketdev.ui.components.DefaultFloatingActionMenu
import com.pocketdev.ui.screens.chat.ChatScreen
import com.pocketdev.ui.screens.editor.EditorScreen
import com.pocketdev.ui.screens.ollama.OllamaScreen
import com.pocketdev.ui.screens.pc.PcConnectionScreen
import com.pocketdev.ui.screens.repos.RepoDetailScreen
import com.pocketdev.ui.screens.repos.ReposScreen
import com.pocketdev.ui.screens.settings.SettingsScreen
import com.pocketdev.ui.screens.build.BuildScreen
import com.pocketdev.ui.screens.terminal.TerminalScreen

data class BottomNavItem(
    val screen: Screen,
    val icon: ImageVector,
    val labelResId: Int
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Chat, Icons.Default.Chat, R.string.nav_chat),
    BottomNavItem(Screen.Repos, Icons.Default.Source, R.string.nav_repos),
    BottomNavItem(Screen.Editor, Icons.Default.Code, R.string.nav_editor),
    BottomNavItem(Screen.Build, Icons.Default.Build, R.string.nav_build),
    BottomNavItem(Screen.Terminal, Icons.Default.Terminal, R.string.nav_terminal),
    BottomNavItem(Screen.Settings, Icons.Default.Settings, R.string.nav_settings)
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
                        icon = { Icon(item.icon, contentDescription = stringResource(item.labelResId)) },
                        label = { Text(stringResource(item.labelResId)) },
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
        },
        floatingActionButton = {
            DefaultFloatingActionMenu(
                onNavigateToChat = { navController.navigate(Screen.Chat.route) },
                onNavigateToEditor = { navController.navigate(Screen.Editor.route) },
                onNavigateToOllama = { navController.navigate(Screen.Ollama.route) },
                onNavigateToPcConnection = { navController.navigate(Screen.PcConnection.route) },
                onNavigateToTerminal = { navController.navigate(Screen.Terminal.route) }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            NavHost(
                navController = navController,
                startDestination = Screen.Chat.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screen.Chat.route) { ChatScreen() }
                composable(Screen.Settings.route) {
                    SettingsScreen(
                        onNavigateToOllama = { navController.navigate(Screen.Ollama.route) },
                        onNavigateToPcConnection = { navController.navigate(Screen.PcConnection.route) }
                    )
                }
                composable(Screen.Repos.route) { ReposScreen() }
                composable(Screen.Ollama.route) { OllamaScreen() }
                composable(Screen.PcConnection.route) { PcConnectionScreen() }
                composable(Screen.Editor.route) { EditorScreen() }
                composable(Screen.Build.route) { BuildScreen() }
                composable(Screen.Terminal.route) { TerminalScreen() }
                composable(Screen.RepoDetail.route) { backStackEntry ->
                    val repoFullName = backStackEntry.arguments?.getString("repoFullName")?.replace("_", "/") ?: ""
                    val repoId = backStackEntry.arguments?.getString("repoId")?.toLongOrNull() ?: 0L
                    val repoOwner = backStackEntry.arguments?.getString("repoOwner") ?: ""
                    val repoDefaultBranch = backStackEntry.arguments?.getString("repoDefaultBranch") ?: "main"
                    RepoDetailScreen(
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToEditor = { fullName, branch, path, sha ->
                            navController.navigate(
                                Screen.RemoteEditor.createRoute(fullName, branch, path, sha)
                            )
                        }
                    )
                }
                composable(Screen.RemoteEditor.route) { backStackEntry ->
                    val repoFullName = backStackEntry.arguments?.getString("repoFullName")?.replace("_", "/") ?: ""
                    val branch = backStackEntry.arguments?.getString("branch") ?: "main"
                    val path = backStackEntry.arguments?.getString("path")?.replace("_", "/") ?: ""
                    val sha = backStackEntry.arguments?.getString("sha") ?: ""
                    com.pocketdev.ui.screens.editor.RemoteEditorScreen(
                        repoFullName = repoFullName,
                        branch = branch,
                        filePath = path,
                        fileSha = sha,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
