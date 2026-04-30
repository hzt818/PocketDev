package com.pocketdev.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class EditorPreferences(
    val fontSize: Int = 14,
    val tabSize: Int = 4,
    val showLineNumbers: Boolean = true,
    val wordWrap: Boolean = false
)
