package com.pocketdev.ui.navigation

sealed class Screen(val route: String) {
    data object Chat : Screen("chat")
    data object Settings : Screen("settings")
    data object Repos : Screen("repos")
    data object RepoDetail : Screen("repo/{repoFullName}/{repoId}/{repoOwner}/{repoDefaultBranch}") {
        fun createRoute(repoFullName: String, repoId: Long, repoOwner: String, repoDefaultBranch: String): String {
            return "repo/${repoFullName.replace("/", "_")}/$repoId/$repoOwner/$repoDefaultBranch"
        }
    }
    data object Ollama : Screen("ollama")
    data object PcConnection : Screen("pc_connection")
    data object Editor : Screen("editor")
    data object RemoteEditor : Screen("remote_editor/{repoFullName}/{branch}/{path}/{sha}") {
        fun createRoute(repoFullName: String, branch: String, path: String, sha: String): String {
            return "remote_editor/${repoFullName.replace("/", "_")}/$branch/${path.replace("/", "_")}/$sha"
        }
    }
    data object Build : Screen("build")
    data object Terminal : Screen("terminal")
}
