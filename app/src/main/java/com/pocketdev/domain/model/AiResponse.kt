package com.pocketdev.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AiResponse(
    val explanation: String,
    val actions: List<AICodeAction>
)

@Serializable
data class AICodeAction(
    @SerialName("fileName")
    val fileName: String,
    @SerialName("codeContent")
    val codeContent: String,
    @SerialName("commitMessage")
    val commitMessage: String
)
