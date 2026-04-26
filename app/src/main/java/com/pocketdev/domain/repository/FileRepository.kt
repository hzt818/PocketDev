package com.pocketdev.domain.repository

import android.net.Uri
import com.pocketdev.domain.model.FileItem

interface FileRepository {
    suspend fun openFolder(uri: Uri): Result<List<FileItem>>
    suspend fun listFiles(folderUri: Uri): Result<List<FileItem>>
    suspend fun readFile(uri: Uri): Result<String>
    suspend fun writeFile(uri: Uri, content: String): Result<Unit>
    suspend fun getFileInfo(uri: Uri): Result<FileItem>
    fun getFileType(extension: String): com.pocketdev.domain.model.FileType
}
