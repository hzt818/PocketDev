package com.pocketdev.ui.i18n

import android.content.Context
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.pocketdev.R

class AndroidStrings(private val context: Context) : Strings {
    private val res = context.resources
    private val getString = { id: Int -> context.getString(id) }

    // App
    override val appName: String get() = getString(R.string.app_name)

    // Navigation
    override val navChat: String get() = getString(R.string.nav_chat)
    override val navRepos: String get() = getString(R.string.nav_repos)
    override val navEditor: String get() = getString(R.string.nav_editor)
    override val navBuild: String get() = getString(R.string.nav_build)
    override val navTerminal: String get() = getString(R.string.nav_terminal)
    override val navSettings: String get() = getString(R.string.nav_settings)

    // Chat Screen
    override val chatTitle: String get() = getString(R.string.chat_title)
    override val chatConversations: String get() = getString(R.string.chat_conversations)
    override val chatNewConversation: String get() = getString(R.string.chat_new_conversation)
    override val chatNoConversations: String get() = getString(R.string.chat_no_conversations)
    override val chatWelcome: String get() = getString(R.string.chat_welcome)
    override val chatWelcomeSubtitle: String get() = getString(R.string.chat_welcome_subtitle)
    override val chatThinking: String get() = getString(R.string.chat_thinking)
    override val chatInputPlaceholder: String get() = getString(R.string.chat_input_placeholder)
    override val chatSend: String get() = getString(R.string.chat_send)
    override val chatDelete: String get() = getString(R.string.chat_delete)
    override fun chatContextRepo(owner: String, repo: String): String =
        context.getString(R.string.chat_context_repo, owner, repo)

    // Settings Screen
    override val settingsTitle: String get() = getString(R.string.settings_title)
    override val settingsAppearance: String get() = getString(R.string.settings_appearance)
    override val settingsEditor: String get() = getString(R.string.settings_editor)
    override val settingsAiProviders: String get() = getString(R.string.settings_ai_providers)
    override val settingsAiActionMode: String get() = getString(R.string.settings_ai_action_mode)
    override val settingsLocalAi: String get() = getString(R.string.settings_local_ai)
    override val settingsRemoteControl: String get() = getString(R.string.settings_remote_control)
    override val settingsStorage: String get() = getString(R.string.settings_storage)
    override val settingsAbout: String get() = getString(R.string.settings_about)
    override val settingsProviderSetup: String get() = getString(R.string.settings_provider_setup)
    override val settingsProviderActive: String get() = getString(R.string.settings_provider_active)
    override val settingsApiKeyMasked: String get() = getString(R.string.settings_api_key_masked)
    override val settingsSaving: String get() = getString(R.string.settings_saving)
    override val settingsSave: String get() = getString(R.string.settings_save)
    override val settingsCancel: String get() = getString(R.string.settings_cancel)
    override fun settingsEdit(providerName: String): String =
        context.getString(R.string.settings_edit, providerName)
    override val settingsProviderSaved: String get() = getString(R.string.settings_provider_saved)

    // Action Modes
    override val actionModePlan: String get() = getString(R.string.action_mode_plan)
    override val actionModePlanDesc: String get() = getString(R.string.action_mode_plan_desc)
    override val actionModeAutoedit: String get() = getString(R.string.action_mode_autoedit_desc)
    override val actionModeAutoeditDesc: String get() = getString(R.string.action_mode_autoedit_desc)
    override val actionModeBypass: String get() = getString(R.string.action_mode_bypass)
    override val actionModeBypassDesc: String get() = getString(R.string.action_mode_bypass_desc)

    // Settings Navigation
    override val settingsOllamaTitle: String get() = getString(R.string.settings_ollama_title)
    override val settingsOllamaSubtitle: String get() = getString(R.string.settings_ollama_subtitle)
    override val settingsPcTitle: String get() = getString(R.string.settings_pc_title)
    override val settingsPcSubtitle: String get() = getString(R.string.settings_pc_subtitle)

    // Provider Edit Dialog
    override fun dialogConfigureProvider(providerName: String): String =
        context.getString(R.string.dialog_configure_provider, providerName)
    override val dialogBaseUrl: String get() = getString(R.string.dialog_base_url)
    override val dialogModelName: String get() = getString(R.string.dialog_model_name)
    override val dialogApiKey: String get() = getString(R.string.dialog_api_key)

    // Help Card
    override val helpDeepseekOpenai: String get() = getString(R.string.help_deepseek_openai)
    override val helpAnthropic: String get() = getString(R.string.help_anthropic)
    override val helpGemini: String get() = getString(R.string.help_gemini)
    override val helpOllama: String get() = getString(R.string.help_ollama)

    // Settings Components
    override val themeMode: String get() = getString(R.string.theme_mode)
    override val dynamicColor: String get() = getString(R.string.dynamic_color)
    override val dynamicColorSubtitle: String get() = getString(R.string.dynamic_color_subtitle)
    override fun fontSize(size: Int): String = context.getString(R.string.font_size, size)
    override fun tabSize(size: Int): String = context.getString(R.string.tab_size, size)
    override val showLineNumbers: String get() = getString(R.string.show_line_numbers)
    override val wordWrap: String get() = getString(R.string.word_wrap)
    override val cache: String get() = getString(R.string.cache)
    override fun cacheSize(size: String): String = context.getString(R.string.cache_size, size)
    override val cacheClear: String get() = getString(R.string.cache_clear)
    override fun appVersion(version: String): String = context.getString(R.string.app_version, version)
    override val appDescription: String get() = getString(R.string.app_description)
    override val signOut: String get() = getString(R.string.sign_out)
    override val notSignedIn: String get() = getString(R.string.not_signed_in)

    // Repos Screen
    override val reposTitle: String get() = getString(R.string.repos_title)
    override val reposConnectGithub: String get() = getString(R.string.repos_connect_github)
    override val reposSignIn: String get() = getString(R.string.repos_sign_in)
    override val reposLoginGithub: String get() = getString(R.string.repos_login_github)

    // Repo Detail Screen
    override val repoDetailTitle: String get() = getString(R.string.repo_detail_title)
    override val repoDetailBack: String get() = getString(R.string.repo_detail_back)
    override val repoDetailSelectBranch: String get() = getString(R.string.repo_detail_select_branch)
    override val repoDetailEdit: String get() = getString(R.string.repo_detail_edit)

    // Ollama Screen
    override val ollamaTitle: String get() = getString(R.string.ollama_title)
    override val ollamaServerOn: String get() = getString(R.string.ollama_server_on)
    override val ollamaServerOff: String get() = getString(R.string.ollama_server_off)
    override val ollamaRefresh: String get() = getString(R.string.ollama_refresh)
    override val ollamaAvailableModels: String get() = getString(R.string.ollama_available_models)
    override val ollamaInstalledModels: String get() = getString(R.string.ollama_installed_models)
    override val ollamaServer: String get() = getString(R.string.ollama_server)
    override val ollamaRunningPort: String get() = getString(R.string.ollama_running_port)
    override val ollamaNotRunning: String get() = getString(R.string.ollama_not_running)
    override val ollamaStop: String get() = getString(R.string.ollama_stop)
    override val ollamaStart: String get() = getString(R.string.ollama_start)
    override val ollamaInstalled: String get() = getString(R.string.ollama_installed)
    override val ollamaDownload: String get() = getString(R.string.ollama_download)
    override val ollamaRetry: String get() = getString(R.string.ollama_retry)

    // PC Connection Screen
    override val pcTitle: String get() = getString(R.string.pc_title)
    override val pcConnected: String get() = getString(R.string.pc_connected)
    override val pcDisconnected: String get() = getString(R.string.pc_disconnected)
    override val pcAddConnection: String get() = getString(R.string.pc_add_connection)
    override val pcNoPcConnected: String get() = getString(R.string.pc_no_pc_connected)
    override val pcInstallCli: String get() = getString(R.string.pc_install_cli)
    override val pcActive: String get() = getString(R.string.pc_active)
    override val pcSelect: String get() = getString(R.string.pc_select)
    override val pcTestConnection: String get() = getString(R.string.pc_test_connection)
    override val pcDelete: String get() = getString(R.string.pc_delete)
    override val pcAddPcTitle: String get() = getString(R.string.pc_add_pc_title)
    override val pcName: String get() = getString(R.string.pc_name)
    override val pcHostIp: String get() = getString(R.string.pc_host_ip)
    override val pcPort: String get() = getString(R.string.pc_port)
    override val pcApiKeyOptional: String get() = getString(R.string.pc_api_key_optional)
    override val pcAdd: String get() = getString(R.string.pc_add)

    // Editor Screen
    override val editorNoFileOpen: String get() = getString(R.string.editor_no_file_open)
    override val editorOpenFolderHint: String get() = getString(R.string.editor_open_folder_hint)
    override val editorOpenFolder: String get() = getString(R.string.editor_open_folder)
    override val editorAiAssist: String get() = getString(R.string.editor_ai_assist)
    override val editorSelectedCode: String get() = getString(R.string.editor_selected_code)
    override val editorAskAi: String get() = getString(R.string.editor_ask_ai)
    override val editorAiResponse: String get() = getString(R.string.editor_ai_response)
    override val editorInsertCursor: String get() = getString(R.string.editor_insert_cursor)

    // Editor Toolbar
    override val editorToggleFiletree: String get() = getString(R.string.editor_toggle_filetree)
    override val editorOpenFolderTooltip: String get() = getString(R.string.editor_open_folder_tooltip)
    override val editorUndo: String get() = getString(R.string.editor_undo)
    override val editorRedo: String get() = getString(R.string.editor_redo)
    override val editorSearch: String get() = getString(R.string.editor_search)
    override val editorSave: String get() = getString(R.string.editor_save)
    override val editorZoomOut: String get() = getString(R.string.editor_zoom_out)
    override val editorZoomIn: String get() = getString(R.string.editor_zoom_in)
    override val editorMoreOptions: String get() = getString(R.string.editor_more_options)
    override val editorAutoSave: String get() = getString(R.string.editor_auto_save)
    override val editorOn: String get() = getString(R.string.editor_on)
    override val editorOff: String get() = getString(R.string.editor_off)

    // Search Replace Bar
    override val searchFind: String get() = getString(R.string.search_find)
    override val searchPlaceholder: String get() = getString(R.string.search_placeholder)
    override val searchFindNext: String get() = getString(R.string.search_find_next)
    override val searchClose: String get() = getString(R.string.search_close)
    override val searchReplace: String get() = getString(R.string.search_replace)
    override val searchReplaceWith: String get() = getString(R.string.search_replace_with)
    override val searchOne: String get() = getString(R.string.search_one)
    override val searchAll: String get() = getString(R.string.search_all)

    // File Tree
    override val fileLoading: String get() = getString(R.string.file_loading)
    override val fileNoFiles: String get() = getString(R.string.file_no_files)

    // Branch Selector
    override val branchSelectTitle: String get() = getString(R.string.branch_select_title)
    override val branchDefault: String get() = getString(R.string.branch_default)
    override val branchProtected: String get() = getString(R.string.branch_protected)

    // Build Screen
    override val buildType: String get() = getString(R.string.build_type)
    override val buildDebug: String get() = getString(R.string.build_debug)
    override val buildRelease: String get() = getString(R.string.build_release)
    override val buildProjectPath: String get() = getString(R.string.build_project_path)
    override fun buildGradleVersion(version: String): String =
        context.getString(R.string.build_gradle_version, version)
    override fun buildGradleHome(home: String): String =
        context.getString(R.string.build_gradle_home, home)
    override val buildDaemonRunning: String get() = getString(R.string.build_daemon_running)
    override val buildDaemonStopped: String get() = getString(R.string.build_daemon_stopped)
    override val buildGradleNotFound: String get() = getString(R.string.build_gradle_not_found)
    override val buildStop: String get() = getString(R.string.build_stop)
    override val buildBuild: String get() = getString(R.string.build_build)
    override val buildRefresh: String get() = getString(R.string.build_refresh)
    override fun buildPhase(phase: String): String =
        context.getString(R.string.build_phase, phase)
    override val buildNoHistory: String get() = getString(R.string.build_no_history)
    override val buildRunToSee: String get() = getString(R.string.build_run_to_see)

    // Terminal Screen
    override val terminalLocal: String get() = getString(R.string.terminal_local)
    override val terminalRemotePc: String get() = getString(R.string.terminal_remote_pc)
    override val terminalNewSession: String get() = getString(R.string.terminal_new_session)
    override val terminalNoActive: String get() = getString(R.string.terminal_no_active)
    override val terminalCreateSession: String get() = getString(R.string.terminal_create_session)
    override val terminalInputHint: String get() = getString(R.string.terminal_input_hint)
    override val terminalPrevious: String get() = getString(R.string.terminal_previous)
    override val terminalNext: String get() = getString(R.string.terminal_next)

    // Commit Dialog
    override val commitTitle: String get() = getString(R.string.commit_title)
    override val commitMessageHint: String get() = getString(R.string.commit_message_hint)
    override val commitMessageLabel: String get() = getString(R.string.commit_message_label)
    override val commitUpdateFile: String get() = getString(R.string.commit_update_file)
    override val commitCommit: String get() = getString(R.string.commit_commit)

    // Splash Screen
    override val splashTagline: String get() = getString(R.string.splash_tagline)

    // Default FAB Menu
    override val fabChat: String get() = getString(R.string.fab_chat)
    override val fabEditor: String get() = getString(R.string.fab_editor)
    override val fabOllama: String get() = getString(R.string.fab_ollama)
    override val fabPc: String get() = getString(R.string.fab_pc)
    override val fabTerminal: String get() = getString(R.string.fab_terminal)

    // File Tree Browser
    override val fileBrowserNoFiles: String get() = getString(R.string.file_browser_no_files)

    // About Card
    override val aboutAppName: String get() = getString(R.string.about_app_name)

    // Common
    override val ok: String get() = getString(R.string.ok)
    override val cancel: String get() = getString(R.string.cancel)
    override val retry: String get() = getString(R.string.retry)
    override val close: String get() = getString(R.string.close)
}
