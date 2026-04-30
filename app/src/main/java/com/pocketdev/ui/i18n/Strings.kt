package com.pocketdev.ui.i18n

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.text.TextStyle

val LocalStrings = compositionLocalOf<Strings> { error("No Strings CompositionLocal provided") }

interface Strings {
    // App
    val appName: String

    // Navigation
    val navChat: String
    val navRepos: String
    val navEditor: String
    val navBuild: String
    val navTerminal: String
    val navSettings: String

    // Chat Screen
    val chatTitle: String
    val chatConversations: String
    val chatNewConversation: String
    val chatNoConversations: String
    val chatWelcome: String
    val chatWelcomeSubtitle: String
    val chatThinking: String
    val chatInputPlaceholder: String
    val chatSend: String
    val chatDelete: String
    fun chatContextRepo(owner: String, repo: String): String

    // Settings Screen
    val settingsTitle: String
    val settingsAppearance: String
    val settingsEditor: String
    val settingsAiProviders: String
    val settingsAiActionMode: String
    val settingsLocalAi: String
    val settingsRemoteControl: String
    val settingsStorage: String
    val settingsAbout: String
    val settingsProviderSetup: String
    val settingsProviderActive: String
    val settingsApiKeyMasked: String
    val settingsSaving: String
    val settingsSave: String
    val settingsCancel: String
    fun settingsEdit(providerName: String): String
    val settingsProviderSaved: String

    // Action Modes
    val actionModePlan: String
    val actionModePlanDesc: String
    val actionModeAutoedit: String
    val actionModeAutoeditDesc: String
    val actionModeBypass: String
    val actionModeBypassDesc: String

    // Settings Navigation
    val settingsOllamaTitle: String
    val settingsOllamaSubtitle: String
    val settingsPcTitle: String
    val settingsPcSubtitle: String

    // Provider Edit Dialog
    fun dialogConfigureProvider(providerName: String): String
    val dialogBaseUrl: String
    val dialogModelName: String
    val dialogApiKey: String

    // Help Card
    val helpDeepseekOpenai: String
    val helpAnthropic: String
    val helpGemini: String
    val helpOllama: String

    // Settings Components
    val themeMode: String
    val dynamicColor: String
    val dynamicColorSubtitle: String
    fun fontSize(size: Int): String
    fun tabSize(size: Int): String
    val showLineNumbers: String
    val wordWrap: String
    val cache: String
    fun cacheSize(size: String): String
    val cacheClear: String
    fun appVersion(version: String): String
    val appDescription: String
    val signOut: String
    val notSignedIn: String

    // Repos Screen
    val reposTitle: String
    val reposConnectGithub: String
    val reposSignIn: String
    val reposLoginGithub: String

    // Repo Detail Screen
    val repoDetailTitle: String
    val repoDetailBack: String
    val repoDetailSelectBranch: String
    val repoDetailEdit: String

    // Ollama Screen
    val ollamaTitle: String
    val ollamaServerOn: String
    val ollamaServerOff: String
    val ollamaRefresh: String
    val ollamaAvailableModels: String
    val ollamaInstalledModels: String
    val ollamaServer: String
    val ollamaRunningPort: String
    val ollamaNotRunning: String
    val ollamaStop: String
    val ollamaStart: String
    val ollamaInstalled: String
    val ollamaDownload: String
    val ollamaRetry: String

    // PC Connection Screen
    val pcTitle: String
    val pcConnected: String
    val pcDisconnected: String
    val pcAddConnection: String
    val pcNoPcConnected: String
    val pcInstallCli: String
    val pcActive: String
    val pcSelect: String
    val pcTestConnection: String
    val pcDelete: String
    val pcAddPcTitle: String
    val pcName: String
    val pcHostIp: String
    val pcPort: String
    val pcApiKeyOptional: String
    val pcAdd: String

    // Editor Screen
    val editorNoFileOpen: String
    val editorOpenFolderHint: String
    val editorOpenFolder: String
    val editorAiAssist: String
    val editorSelectedCode: String
    val editorAskAi: String
    val editorAiResponse: String
    val editorInsertCursor: String

    // Editor Toolbar
    val editorToggleFiletree: String
    val editorOpenFolderTooltip: String
    val editorUndo: String
    val editorRedo: String
    val editorSearch: String
    val editorSave: String
    val editorZoomOut: String
    val editorZoomIn: String
    val editorMoreOptions: String
    val editorAutoSave: String
    val editorOn: String
    val editorOff: String

    // Search Replace Bar
    val searchFind: String
    val searchPlaceholder: String
    val searchFindNext: String
    val searchClose: String
    val searchReplace: String
    val searchReplaceWith: String
    val searchOne: String
    val searchAll: String

    // File Tree
    val fileLoading: String
    val fileNoFiles: String

    // Branch Selector
    val branchSelectTitle: String
    val branchDefault: String
    val branchProtected: String

    // Build Screen
    val buildType: String
    val buildDebug: String
    val buildRelease: String
    val buildProjectPath: String
    fun buildGradleVersion(version: String): String
    fun buildGradleHome(home: String): String
    val buildDaemonRunning: String
    val buildDaemonStopped: String
    val buildGradleNotFound: String
    val buildStop: String
    val buildBuild: String
    val buildRefresh: String
    fun buildPhase(phase: String): String
    val buildNoHistory: String
    val buildRunToSee: String

    // Terminal Screen
    val terminalLocal: String
    val terminalRemotePc: String
    val terminalNewSession: String
    val terminalNoActive: String
    val terminalCreateSession: String
    val terminalInputHint: String
    val terminalPrevious: String
    val terminalNext: String

    // Commit Dialog
    val commitTitle: String
    val commitMessageHint: String
    val commitMessageLabel: String
    val commitUpdateFile: String
    val commitCommit: String

    // Splash Screen
    val splashTagline: String

    // Default FAB Menu
    val fabChat: String
    val fabEditor: String
    val fabOllama: String
    val fabPc: String
    val fabTerminal: String

    // File Tree Browser
    val fileBrowserNoFiles: String

    // About Card
    val aboutAppName: String

    // Common
    val ok: String
    val cancel: String
    val retry: String
    val close: String
}
