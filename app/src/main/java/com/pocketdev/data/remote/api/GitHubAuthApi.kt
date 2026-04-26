package com.pocketdev.data.remote.api

import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Query

interface GitHubAuthApi {
    @POST("login/oauth/access_token")
    suspend fun exchangeCodeForToken(
        @Query("client_id") clientId: String,
        @Query("client_secret") clientSecret: String,
        @Query("code") code: String
    ): String
}
