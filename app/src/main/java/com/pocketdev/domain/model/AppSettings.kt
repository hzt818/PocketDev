package com.pocketdev.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val dynamicColor: Boolean = true,
    val editorPreferences: EditorPreferences = EditorPreferences(),
    val collaborationServerUrl: String? = null
)
