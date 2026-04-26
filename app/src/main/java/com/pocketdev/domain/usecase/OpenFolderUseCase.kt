package com.pocketdev.domain.usecase

import android.net.Uri
import com.pocketdev.domain.model.FileItem
import com.pocketdev.domain.repository.FileRepository
import javax.inject.Inject

class OpenFolderUseCase @Inject constructor(
    private val fileRepository: FileRepository
) {
    suspend operator fun invoke(folderUri: Uri): Result<List<FileItem>> {
        return fileRepository.openFolder(folderUri)
    }
}
