package com.pocketdev.ui.screens.terminal

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle

/**
 * Parses ANSI escape codes and returns an AnnotatedString with appropriate colors.
 * Supports common ANSI color codes (foreground colors, bold, reset).
 */
private fun parseAnsiColors(text: String, defaultColor: Color): AnnotatedString {
    return buildAnnotatedString {
        var currentColor = defaultColor
        var isBold = false
        var remaining = text

        while (remaining.isNotEmpty()) {
            val ansiIndex = remaining.indexOf("["[0])
            if (ansiIndex == -1) {
                withStyle(SpanStyle(color = currentColor, fontWeight = if (isBold) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal)) {
                    append(remaining)
                }
                break
            }

            if (ansiIndex > 0) {
                withStyle(SpanStyle(color = currentColor, fontWeight = if (isBold) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal)) {
                    append(remaining.substring(0, ansiIndex))
                }
            }

            val codeEnd = remaining.indexOf('m', ansiIndex)
            if (codeEnd == -1) {
                withStyle(SpanStyle(color = currentColor)) {
                    append(remaining.substring(ansiIndex))
                }
                break
            }

            val ansiCode = remaining.substring(ansiIndex + 2, codeEnd)
            remaining = remaining.substring(codeEnd + 1)

            when (ansiCode) {
                "0" -> { currentColor = defaultColor; isBold = false }
                "1" -> isBold = true
                "30" -> currentColor = Color(0xFF888888)
                "31" -> currentColor = Color(0xFFFF6B6B)
                "32" -> currentColor = Color(0xFF6BFF6B)
                "33" -> currentColor = Color(0xFFFFFF6B)
                "34" -> currentColor = Color(0xFF6B6BFF)
                "35" -> currentColor = Color(0xFFFF6BFF)
                "36" -> currentColor = Color(0xFF6BFFFF)
                "37" -> currentColor = Color(0xFFFFFFFF.toInt())
                "90" -> currentColor = Color(0xFF888888)
                "91" -> currentColor = Color(0xFFFF6B6B)
                "92" -> currentColor = Color(0xFF6BFF6B)
                "93" -> currentColor = Color(0xFFFFFF6B)
                "94" -> currentColor = Color(0xFF6B6BFF)
                "95" -> currentColor = Color(0xFFFF6BFF)
                "96" -> currentColor = Color(0xFF6BFFFF)
                "97" -> currentColor = Color(0xFFFFFFFF.toInt())
                else -> { /* Unknown code, ignore */ }
            }
        }
    }
}