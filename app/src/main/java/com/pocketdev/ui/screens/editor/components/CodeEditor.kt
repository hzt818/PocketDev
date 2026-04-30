package com.pocketdev.ui.screens.editor.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pocketdev.R
import com.pocketdev.domain.model.FileType
import com.pocketdev.ui.screens.editor.util.SyntaxHighlighter

@Composable
fun CodeEditor(
    content: String,
    language: FileType,
    fontSize: Float,
    onContentChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    showLineNumbers: Boolean = true,
    onCursorChange: ((Int, Int) -> Unit)? = null
) {
    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()
    var textFieldValue by remember(content) {
        mutableStateOf(TextFieldValue(content))
    }

    val lineCount = remember(content) {
        content.lines().size.coerceAtLeast(1)
    }

    val backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    val textColor = MaterialTheme.colorScheme.onSurface
    val lineNumberColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)

    Row(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        if (showLineNumbers) {
            LineNumbers(
                lineCount = lineCount,
                fontSize = fontSize,
                lineNumberColor = lineNumberColor,
                modifier = Modifier
                    .width(48.dp)
                    .fillMaxHeight()
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(verticalScrollState)
                .horizontalScroll(horizontalScrollState)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            BasicTextField(
                value = textFieldValue,
                onValueChange = { newValue ->
                    textFieldValue = newValue
                    onContentChange(newValue.text)
                },
                textStyle = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = fontSize.sp,
                    color = textColor,
                    lineHeight = (fontSize * 1.5).sp
                ),
                visualTransformation = { annotatedString ->
                    val highlighted = SyntaxHighlighter.highlight(annotatedString.text, language)
                    androidx.compose.ui.text.input.TransformedText(
                        highlighted,
                        androidx.compose.ui.text.input.OffsetMapping.Identity
                    )
                },
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { innerTextField ->
                    Box {
                        if (content.isEmpty()) {
                            Text(
                                text = stringResource(R.string.editor_start_typing),
                                style = TextStyle(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = fontSize.sp,
                                    color = lineNumberColor
                                )
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }
    }
}

@Composable
private fun LineNumbers(
    lineCount: Int,
    fontSize: Float,
    lineNumberColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    val lineNumberWidth = remember(lineCount) {
        (lineCount.toString().length * 10).coerceAtLeast(24).dp
    }

    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .width(lineNumberWidth + 16.dp)
                .verticalScroll(rememberScrollState())
                .padding(start = 8.dp)
        ) {
            repeat(lineCount) { index ->
                Text(
                    text = (index + 1).toString(),
                    style = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = fontSize.sp,
                        color = lineNumberColor,
                        textAlign = TextAlign.End
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 8.dp)
                )
            }
        }
    }
}
