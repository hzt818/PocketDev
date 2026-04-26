package com.pocketdev.ui.navigation

sealed class Screen(val route: String) {
    data object Chat : Screen("chat")
    data object Settings : Screen("settings")
    data object Repos : Screen("repos")
    data object Ollama : Screen("ollama")
    data object PcConnection : Screen("pc_connection")
    data object Editor : Screen("editor")
}
