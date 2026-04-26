package com.pocketdev.ui.screens.ollama

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pocketdev.domain.model.OllamaModelWithStatus
import com.pocketdev.domain.model.OllamaStatus
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OllamaScreen(
    viewModel: OllamaViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.onEvent(OllamaEvent.ClearError)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ollama Models") },
                actions = {
                    Text(
                        text = if (uiState.isServerRunning) "Server: ON" else "Server: OFF",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (uiState.isServerRunning)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = { viewModel.onEvent(OllamaEvent.RefreshModels) }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                OllamaServerStatusCard(
                    isRunning = uiState.isServerRunning,
                    onStart = { viewModel.onEvent(OllamaEvent.StartServer) },
                    onStop = { viewModel.onEvent(OllamaEvent.StopServer) }
                )
            }

            item {
                Text(
                    text = "Available Models",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            items(uiState.availableModels, key = { it.name }) { model ->
                OllamaModelCard(
                    model = model,
                    progress = uiState.downloadProgress[model.name],
                    isDownloading = uiState.downloadingModel == model.name,
                    onPull = { viewModel.onEvent(OllamaEvent.PullModel(model.name)) }
                )
            }

            if (uiState.installedModels.isNotEmpty()) {
                item {
                    Text(
                        text = "Installed Models",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                items(uiState.installedModels, key = { it.name }) { model ->
                    InstalledModelCard(
                        modelName = model.name,
                        size = model.size,
                        onDelete = { viewModel.onEvent(OllamaEvent.DeleteModel(model.name)) }
                    )
                }
            }
        }
    }
}

@Composable
private fun OllamaServerStatusCard(
    isRunning: Boolean,
    onStart: () -> Unit,
    onStop: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isRunning)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Ollama Server",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = if (isRunning) "Running on port 11434" else "Not running",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            TextButton(onClick = if (isRunning) onStop else onStart) {
                Text(if (isRunning) "Stop" else "Start")
            }
        }
    }
}

@Composable
private fun OllamaModelCard(
    model: OllamaModelWithStatus,
    progress: Float?,
    isDownloading: Boolean,
    onPull: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = model.displayName,
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = model.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = model.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                when (model.status) {
                    OllamaStatus.INSTALLED -> {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Installed",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    OllamaStatus.NOT_INSTALLED -> {
                        IconButton(
                            onClick = onPull,
                            enabled = !isDownloading
                        ) {
                            if (isDownloading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.CloudDownload,
                                    contentDescription = "Download",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                    OllamaStatus.DOWNLOADING -> {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    }
                    OllamaStatus.ERROR -> {
                        IconButton(onClick = onPull) {
                            Icon(
                                imageVector = Icons.Default.CloudDownload,
                                contentDescription = "Retry",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            if (isDownloading && progress != null) {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = formatSize(model.size),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun InstalledModelCard(
    modelName: String,
    size: Long,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Memory,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = modelName,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = formatSize(size),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

private fun formatSize(bytes: Long): String {
    val df = DecimalFormat("#.##")
    return when {
        bytes >= 1_000_000_000 -> "${df.format(bytes / 1_000_000_000.0)} GB"
        bytes >= 1_000_000 -> "${df.format(bytes / 1_000_000.0)} MB"
        bytes >= 1_000 -> "${df.format(bytes / 1_000.0)} KB"
        else -> "$bytes B"
    }
}
