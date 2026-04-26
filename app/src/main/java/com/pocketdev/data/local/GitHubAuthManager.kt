package com.pocketdev.data.local

import android.content.Context
import androidx.browser.customtabs.CustomTabsIntent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GitHubAuthManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val CLIENT_ID = "Ov23liMKMvMxdzfx7TY6"
        private const val AUTH_URL = "https://github.com/login/oauth/authorize"
        private const val REDIRECT_URI = "pocketdev://callback"
    }

    fun getAuthorizationUrl(): String {
        return "$AUTH_URL?client_id=$CLIENT_ID&redirect_uri=$REDIRECT_URI&scope=repo"
    }

    fun launchAuthFlow(customTabsIntent: CustomTabsIntent) {
        val authUrl = getAuthorizationUrl()
        customTabsIntent.launchUrl(context, android.net.Uri.parse(authUrl))
    }

    fun parseCallback(uri: android.net.Uri?): String? {
        if (uri?.scheme == "pocketdev" && uri.host == "callback") {
            return uri.getQueryParameter("code")
        }
        return null
    }
}
