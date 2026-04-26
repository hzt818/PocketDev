package com.pocketdev.ui.components

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.TextStyle

@Composable
fun MarkdownText(
    markdown: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = LocalTextStyle.current
) {
    val annotatedString = parseMarkdown(markdown, textStyle)

    androidx.compose.material3.Text(
        text = annotatedString,
        modifier = modifier,
        style = textStyle
    )
}

private fun parseMarkdown(markdown: String, baseTextStyle: TextStyle): androidx.compose.ui.text.AnnotatedString {
    return buildAnnotatedString {
        var currentIndex = 0
        val lines = markdown.split("\n")

        for (lineIndex in lines.indices) {
            val line = lines[lineIndex]

            when {
                line.startsWith("### ") -> {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold, fontSize = baseTextStyle.fontSize * 0.875f)) {
                        appendLine(line.removePrefix("### "))
                    }
                }
                line.startsWith("## ") -> {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold, fontSize = baseTextStyle.fontSize * 1.0f)) {
                        appendLine(line.removePrefix("## "))
                    }
                }
                line.startsWith("# ") -> {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold, fontSize = baseTextStyle.fontSize * 1.25f)) {
                        appendLine(line.removePrefix("# "))
                    }
                }
                line.startsWith("- ") || line.startsWith("* ") -> {
                    append("  • ")
                    appendLine(line.removePrefix("- ").removePrefix("* "))
                }
                line.contains("**") -> {
                    parseBoldItalic(line)
                    appendLine()
                }
                line.contains("`") -> {
                    parseInlineCode(line)
                    appendLine()
                }
                else -> {
                    appendLine(line)
                }
            }
            currentIndex += line.length + 1
        }
    }
}

private fun AnnotatedString.Builder.parseBoldItalic(line: String) {
    var remaining = line
    while (remaining.isNotEmpty()) {
        val boldIndex = remaining.indexOf("**")
        val italicIndex = remaining.indexOf("*")

        when {
            boldIndex == -1 && italicIndex == -1 -> {
                append(remaining)
                break
            }
            boldIndex != -1 && (italicIndex == -1 || boldIndex < italicIndex) -> {
                append(remaining.substring(0, boldIndex))
                val endIndex = remaining.indexOf("**", boldIndex + 2)
                if (endIndex != -1) {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(remaining.substring(boldIndex + 2, endIndex))
                    }
                    remaining = remaining.substring(endIndex + 2)
                } else {
                    append(remaining)
                    break
                }
            }
            italicIndex != -1 -> {
                append(remaining.substring(0, italicIndex))
                val endIndex = remaining.indexOf("*", italicIndex + 1)
                if (endIndex != -1) {
                    withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                        append(remaining.substring(italicIndex + 1, endIndex))
                    }
                    remaining = remaining.substring(endIndex + 1)
                } else {
                    append(remaining)
                    break
                }
            }
        }
    }
}

private fun AnnotatedString.Builder.parseInlineCode(line: String) {
    var remaining = line
    while (remaining.isNotEmpty()) {
        val codeIndex = remaining.indexOf("`")
        if (codeIndex == -1) {
            append(remaining)
            break
        }
        append(remaining.substring(0, codeIndex))
        val endIndex = remaining.indexOf("`", codeIndex + 1)
        if (endIndex != -1) {
            withStyle(SpanStyle(fontFamily = FontFamily.Monospace, background = Color(0xFFE7E0EC))) {
                append(remaining.substring(codeIndex + 1, endIndex))
            }
            remaining = remaining.substring(endIndex + 1)
        } else {
            append(remaining)
            break
        }
    }
}
