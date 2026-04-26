package com.pocketdev.data.remote.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GitHubAuthInterceptor @Inject constructor(
    private val tokenProvider: () -> String?
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = tokenProvider()

        val newRequest = if (token != null) {
            originalRequest.newBuilder()
                .header("Authorization", "token $token")
                .header("Accept", "application/vnd.github.v3+json")
                .build()
        } else {
            originalRequest.newBuilder()
                .header("Accept", "application/vnd.github.v3+json")
                .build()
        }

        return chain.proceed(newRequest)
    }
}
