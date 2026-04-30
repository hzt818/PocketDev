package com.pocketdev.ui.i18n

import androidx.annotation.StringRes

sealed interface UiMessage {

    data class Generic(val message: String) : UiMessage

    data class StrRes(
        @StringRes val id: Int,
        val args: List<Any> = emptyList()
    ) : UiMessage

    companion object {
        fun fromMessageOrNull(message: String?, @StringRes fallbackRes: Int): UiMessage {
            return if (message != null) Generic(message) else StrRes(fallbackRes)
        }
    }
}
