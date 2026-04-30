package com.pocketdev.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val login: String,
    val name: String? = null,
    val email: String? = null,
    val avatarUrl: String,
    val accessToken: String
)
