package com.pocketdev.domain.model

import android.net.Uri

data class FileItem(
    val name: String,
    val path: String,
    val isDirectory: Boolean,
    val extension: String?,
    val uri: Uri,
    val size: Long = 0L,
    val lastModified: Long = 0L
)
