package com.pocketdev.domain.model

data class EditorTab(
    val id: String,
    val fileName: String,
    val filePath: String,
    val content: String,
    val originalContent: String,
    val isModified: Boolean,
    val cursorLine: Int,
    val cursorColumn: Int,
    val language: FileType
) {
    companion object {
        fun create(
            fileName: String,
            filePath: String,
            content: String,
            language: FileType
        ): EditorTab = EditorTab(
            id = "${filePath}_${System.currentTimeMillis()}",
            fileName = fileName,
            filePath = filePath,
            content = content,
            originalContent = content,
            isModified = false,
            cursorLine = 1,
            cursorColumn = 1,
            language = language
        )
    }
}
