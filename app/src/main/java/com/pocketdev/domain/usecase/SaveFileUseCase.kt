package com.pocketdev.domain.usecase

import android.net.Uri
import com.pocketdev.domain.repository.FileRepository
import javax.inject.Inject

class SaveFileUseCase @Inject constructor(
    private val fileRepository: FileRepository
) {
    suspend operator fun invoke(uri: Uri, content: String): Result<Unit> {
        return fileRepository.writeFile(uri, content)
    }
}
