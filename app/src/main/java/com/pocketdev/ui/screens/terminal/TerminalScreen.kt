package com.pocketdev.ui.screens.terminal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pocketdev.R
import com.pocketdev.ui.i18n.UiMessage
import com.pocketdev.domain.model.TerminalOutput
import com.pocketdev.domain.model.TerminalSession
import com.pocketdev.domain.model.ShellType

@Composable
fun TerminalScreen(
    viewModel: TerminalViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current

    val errorText = uiState.error?.let { msg ->
        when (msg) {
            is UiMessage.StrRes -> stringResource(msg.id, *msg.args.toTypedArray())
            is UiMessage.Generic -> msg.message
        }
    }

    LaunchedEffect(errorText) {
        errorText?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.onEvent(TerminalEvent.ClearError)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Shell type selector
            ShellTypeSelector(
                selectedType = uiState.selectedShellType,
                onTypeSelected = { viewModel.onEvent(TerminalEvent.SetShellType(it)) }
            )

            // Session tabs header
            SessionTabs(
                sessions = uiState.sessions,
                activeSessionId = uiState.activeSessionId,
                onSelectSession = { viewModel.onEvent(TerminalEvent.SelectSession(it)) },
                onCreateSession = { viewModel.onEvent(TerminalEvent.CreateSession) },
                onCloseSession = { viewModel.onEvent(TerminalEvent.CloseSession(it)) }
            )

            HorizontalDivider()

            // Terminal output area
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                val activeSessionId = uiState.activeSessionId
                if (activeSessionId != null) {
                    val outputs = uiState.outputs[activeSessionId] ?: emptyList()
                    TerminalOutputArea(
                        outputs = outputs,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    EmptyTerminalState(
                        onCreateSession = { viewModel.onEvent(TerminalEvent.CreateSession) }
                    )
                }
            }

            // Input area
            if (uiState.activeSessionId != null) {
                TerminalInput(
                    onSendInput = { input ->
                        viewModel.onEvent(TerminalEvent.SendInput(input))
                        focusManager.clearFocus()
                    },
                    onHistoryPrevious = { viewModel.onEvent(TerminalEvent.HistoryPrevious) },
                    onHistoryNext = { viewModel.onEvent(TerminalEvent.HistoryNext) },
                    historyInput = if (uiState.historyIndex >= 0 && uiState.historyIndex < uiState.commandHistory.size) {
                        uiState.commandHistory[uiState.historyIndex]
                    } else "",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun ShellTypeSelector(
    selectedType: ShellType,
    onTypeSelected: (ShellType) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilterChip(
            selected = selectedType == ShellType.LOCAL,
            onClick = { onTypeSelected(ShellType.LOCAL) },
            label = { Text(stringResource(R.string.terminal_local)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Terminal,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        FilterChip(
            selected = selectedType == ShellType.REMOTE,
            onClick = { onTypeSelected(ShellType.REMOTE) },
            label = { Text(stringResource(R.string.terminal_remote_pc)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Computer,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )
    }
}

@Composable
private fun SessionTabs(
    sessions: List<TerminalSession>,
    activeSessionId: String?,
    onSelectSession: (String) -> Unit,
    onCreateSession: () -> Unit,
    onCloseSession: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Existing session tabs
        items(sessions) { session ->
            SessionTab(
                session = session,
                isActive = session.id == activeSessionId,
                onSelect = { onSelectSession(session.id) },
                onClose = { onCloseSession(session.id) }
            )
        }

        // New session button
        item {
            IconButton(
                onClick = onCreateSession,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.terminal_new_session),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun SessionTab(
    session: TerminalSession,
    isActive: Boolean,
    onSelect: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isActive) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    val textColor = if (isActive) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        modifier = modifier
            .padding(end = 4.dp)
            .clickable { onSelect() },
        shape = RoundedCornerShape(8.dp),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Terminal,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = textColor
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = session.cwd.substringAfterLast('/'),
                style = MaterialTheme.typography.bodySmall,
                color = textColor
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.close),
                modifier = Modifier
                    .size(14.dp)
                    .clickable { onClose() },
                tint = textColor.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun TerminalOutputArea(
    outputs: List<TerminalOutput>,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    LaunchedEffect(outputs.size) {
        if (outputs.isNotEmpty()) {
            listState.animateScrollToItem(outputs.size - 1)
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier
            .background(Color(0xFF1E1E1E))
            .padding(8.dp)
    ) {
        items(outputs) { output ->
            TerminalLine(output = output)
        }
    }
}

@Composable
private fun TerminalLine(
    output: TerminalOutput,
    modifier: Modifier = Modifier
) {
    val textColor = if (output.isError) {
        Color(0xFFFF6B6B)
    } else {
        Color(0xFFE0E0E0)
    }

    val annotatedText = parseAnsiColors(output.data, textColor)

    Text(
        text = annotatedText,
        modifier = modifier,
        fontFamily = FontFamily.Monospace,
        fontSize = 14.sp
    )
}

@Composable
private fun EmptyTerminalState(
    onCreateSession: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Terminal,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.terminal_no_active),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.terminal_create_session),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(24.dp))
        androidx.compose.material3.Button(onClick = onCreateSession) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.terminal_new_session))
        }
    }
}

@Composable
private fun TerminalInput(
    onSendInput: (String) -> Unit,
    onHistoryPrevious: () -> Unit,
    onHistoryNext: () -> Unit,
    historyInput: String,
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf("") }

    LaunchedEffect(historyInput) {
        if (historyInput.isNotEmpty()) {
            inputText = historyInput
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$ ",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.primary
            )
        )
        OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            modifier = Modifier.weight(1f),
            placeholder = { Text(stringResource(R.string.terminal_input_hint)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Send,
                capitalization = KeyboardCapitalization.None
            ),
            keyboardActions = KeyboardActions(
                onSend = {
                    if (inputText.isNotBlank()) {
                        onSendInput(inputText)
                        inputText = ""
                    }
                }
            )
        )
        IconButton(
            onClick = { onSendInput(inputText) },
            enabled = inputText.isNotBlank()
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowUp,
                contentDescription = stringResource(R.string.terminal_previous),
                modifier = Modifier.size(20.dp)
            )
        }
        IconButton(onClick = onHistoryPrevious) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = stringResource(R.string.terminal_next),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

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