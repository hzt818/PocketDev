package com.pocketdev.ui.screens.build

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pocketdev.R
import com.pocketdev.ui.i18n.UiMessage
import com.pocketdev.domain.model.BuildPhase
import com.pocketdev.domain.model.BuildProgress
import com.pocketdev.domain.model.BuildResult
import com.pocketdev.domain.model.GradleInfo

@Composable
fun BuildScreen(
    viewModel: BuildViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val errorText = uiState.error?.let { msg ->
        when (msg) {
            is UiMessage.StrRes -> stringResource(msg.id, *msg.args.toTypedArray())
            is UiMessage.Generic -> msg.message
        }
    }

    LaunchedEffect(errorText) {
        errorText?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.onEvent(BuildEvent.ClearError)
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
            // Build type selector
            BuildTypeSelector(
                selectedType = uiState.buildType,
                onTypeSelected = { viewModel.onEvent(BuildEvent.SetBuildType(it)) }
            )

            // Project path input
            ProjectPathInput(
                projectPath = uiState.projectPath,
                onPathChange = { viewModel.onEvent(BuildEvent.SetProjectPath(it)) }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Gradle info
            if (uiState.gradleInfo != null) {
                GradleInfoCard(gradleInfo = uiState.gradleInfo!!)
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }

            // Build controls
            BuildControls(
                isBuilding = uiState.isBuilding,
                onBuild = { viewModel.onEvent(BuildEvent.ExecuteBuild) },
                onStop = { viewModel.onEvent(BuildEvent.CancelBuild) },
                onRefresh = { viewModel.onEvent(BuildEvent.RefreshGradleInfo) }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Build progress or history
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                when {
                    uiState.isBuilding -> {
                        BuildProgressArea(
                            progress = uiState.currentProgress,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    uiState.buildHistory.isNotEmpty() -> {
                        BuildHistoryList(
                            history = uiState.buildHistory,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    else -> {
                        EmptyBuildState()
                    }
                }
            }
        }
    }
}

@Composable
private fun BuildTypeSelector(
    selectedType: String,
    onTypeSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.build_type),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(end = 8.dp)
        )
        listOf("debug", "release").forEach { type ->
            FilterChip(
                selected = selectedType == type,
                onClick = { onTypeSelected(type) },
                label = { Text(type.replaceFirstChar { it.uppercase() }) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

@Composable
private fun ProjectPathInput(
    projectPath: String,
    onPathChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = projectPath,
            onValueChange = onPathChange,
            modifier = Modifier.weight(1f),
            label = { Text(stringResource(R.string.build_project_path)) },
            placeholder = { Text(stringResource(R.string.build_placeholder_path)) },
            singleLine = true
        )
    }
}

@Composable
private fun GradleInfoCard(
    gradleInfo: GradleInfo,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (gradleInfo.available) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.errorContainer
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Build,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.build_gradle_version, gradleInfo.version ?: stringResource(R.string.build_not_found)),
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(modifier = Modifier.weight(1f))
                if (gradleInfo.available) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = stringResource(R.string.build_available),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            if (gradleInfo.available) {
                Text(
                    text = stringResource(R.string.build_gradle_home, gradleInfo.homeDir ?: stringResource(R.string.build_unknown)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
                Text(
                    text = if (gradleInfo.daemonRunning) stringResource(R.string.build_daemon_running) else stringResource(R.string.build_daemon_stopped),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            } else {
                Text(
                    text = stringResource(R.string.build_gradle_not_found),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}

@Composable
private fun BuildControls(
    isBuilding: Boolean,
    onBuild: () -> Unit,
    onStop: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = if (isBuilding) onStop else onBuild,
            enabled = true
        ) {
            Icon(
                imageVector = if (isBuilding) Icons.Default.Close else Icons.Default.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(if (isBuilding) stringResource(R.string.build_stop) else stringResource(R.string.build_build))
        }

        IconButton(onClick = onRefresh) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = stringResource(R.string.build_refresh)
            )
        }
    }
}

@Composable
private fun BuildProgressArea(
    progress: BuildProgress?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(8.dp)
    ) {
        progress?.let {
            Text(
                text = stringResource(R.string.build_phase, it.phase.name),
                style = MaterialTheme.typography.titleMedium,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { it.progress / 100f },
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "${it.progress}%",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = it.message,
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun BuildHistoryList(
    history: List<BuildResult>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.padding(horizontal = 8.dp)
    ) {
        items(history) { result ->
            BuildHistoryItem(result = result)
        }
    }
}

@Composable
private fun BuildHistoryItem(
    result: BuildResult,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (result.phase) {
        BuildPhase.COMPLETED -> MaterialTheme.colorScheme.primaryContainer
        BuildPhase.FAILED -> MaterialTheme.colorScheme.errorContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (result.phase == BuildPhase.COMPLETED) Icons.Default.Check else Icons.Default.Close,
                contentDescription = result.phase.name,
                tint = if (result.phase == BuildPhase.COMPLETED) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                }
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = result.phase.name,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = result.output,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2
                )
            }
        }
    }
}

@Composable
private fun EmptyBuildState(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Build,
                contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        CircleShape
                    )
                    .padding(16.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.build_no_history),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.build_run_to_see),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}