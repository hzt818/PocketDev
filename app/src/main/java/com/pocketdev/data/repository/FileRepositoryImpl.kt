package com.pocketdev.data.repository

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.pocketdev.domain.model.FileItem
import com.pocketdev.domain.model.FileType
import com.pocketdev.domain.repository.FileRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : FileRepository {

    override suspend fun openFolder(uri: Uri): Result<List<FileItem>> = withContext(Dispatchers.IO) {
        try {
            val documentFile = DocumentFile.fromTreeUri(context, uri)
                ?: return@withContext Result.failure(Exception("Cannot access folder"))

            val files = listFilesImpl(documentFile)
            Result.success(files)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun listFiles(folderUri: Uri): Result<List<FileItem>> = withContext(Dispatchers.IO) {
        try {
            val documentFile = DocumentFile.fromTreeUri(context, folderUri)
                ?: return@withContext Result.failure(Exception("Cannot access folder"))

            val files = listFilesImpl(documentFile)
            Result.success(files)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun listFilesImpl(documentFile: DocumentFile): List<FileItem> {
        return documentFile.listFiles()
            .filter { it.canRead() }
            .map { file ->
                FileItem(
                    name = file.name ?: "Unknown",
                    path = file.uri.toString(),
                    isDirectory = file.isDirectory,
                    extension = if (file.isDirectory) null else file.name?.substringAfterLast('.', ""),
                    uri = file.uri,
                    size = file.length(),
                    lastModified = file.lastModified()
                )
            }
            .sortedWith(compareBy({ !it.isDirectory }, { it.name.lowercase() }))
    }

    override suspend fun readFile(uri: Uri): Result<String> = withContext(Dispatchers.IO) {
        try {
            val documentFile = DocumentFile.fromSingleUri(context, uri)
                ?: return@withContext Result.failure(Exception("File not found"))

            if (!documentFile.canRead()) {
                return@withContext Result.failure(Exception("Cannot read file"))
            }

            val content = context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
                ?: return@withContext Result.failure(Exception("Cannot read file"))
            Result.success(content)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun writeFile(uri: Uri, content: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val documentFile = DocumentFile.fromSingleUri(context, uri)
                ?: return@withContext Result.failure(Exception("File not found"))

            if (!documentFile.canWrite()) {
                return@withContext Result.failure(Exception("Cannot write to file"))
            }

            context.contentResolver.openOutputStream(uri)?.bufferedWriter()?.use { it.write(content) }
                ?: return@withContext Result.failure(Exception("Cannot write to file"))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getFileInfo(uri: Uri): Result<FileItem> = withContext(Dispatchers.IO) {
        try {
            val documentFile = DocumentFile.fromSingleUri(context, uri)
                ?: return@withContext Result.failure(Exception("File not found"))

            val name = documentFile.name ?: "Unknown"
            Result.success(
                FileItem(
                    name = name,
                    path = documentFile.uri.toString(),
                    isDirectory = documentFile.isDirectory,
                    extension = if (documentFile.isDirectory) null else name.substringAfterLast('.', ""),
                    uri = documentFile.uri,
                    size = documentFile.length(),
                    lastModified = documentFile.lastModified()
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getFileType(extension: String): FileType = FileType.fromExtension(extension)
}
