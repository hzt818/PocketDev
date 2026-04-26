package com.pocketdev.domain.usecase

import android.net.Uri
import com.pocketdev.domain.repository.FileRepository
import javax.inject.Inject

class ReadFileUseCase @Inject constructor(
    private val fileRepository: FileRepository
) {
    suspend operator fun invoke(uri: Uri): Result<String> {
        return fileRepository.readFile(uri)
    }
}
