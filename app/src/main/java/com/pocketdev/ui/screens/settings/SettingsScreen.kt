package com.pocketdev.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pocketdev.domain.model.AiActionMode
import com.pocketdev.domain.model.AiProviderConfig
import com.pocketdev.domain.model.AiProviderType
import com.pocketdev.ui.screens.settings.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onNavigateToOllama: () -> Unit = {},
    onNavigateToPcConnection: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    var showEditDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.onEvent(SettingsEvent.ClearError)
        }
    }

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            snackbarHostState.showSnackbar("Provider settings saved")
            showEditDialog = false
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Settings") }) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // User Profile Section
            item {
                UserProfileCard(
                    profile = uiState.userProfile,
                    onSignOut = { viewModel.onEvent(SettingsEvent.SignOut) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Appearance Section
            item {
                Text(
                    text = "Appearance",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                ThemeSettingsCard(
                    currentMode = uiState.appSettings.themeMode,
                    dynamicColorEnabled = uiState.appSettings.dynamicColor,
                    onThemeModeChange = { viewModel.onEvent(SettingsEvent.UpdateThemeMode(it)) },
                    onDynamicColorChange = { viewModel.onEvent(SettingsEvent.UpdateDynamicColor(it)) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Editor",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                EditorSettingsCard(
                    preferences = uiState.appSettings.editorPreferences,
                    onFontSizeChange = { viewModel.onEvent(SettingsEvent.UpdateFontSize(it)) },
                    onTabSizeChange = { viewModel.onEvent(SettingsEvent.UpdateTabSize(it)) },
                    onShowLineNumbersChange = { viewModel.onEvent(SettingsEvent.UpdateShowLineNumbers(it)) },
                    onWordWrapChange = { viewModel.onEvent(SettingsEvent.UpdateWordWrap(it)) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "AI Providers",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(uiState.providers) { provider ->
                ProviderCard(
                    provider = provider,
                    isActive = provider.type == uiState.activeProviderType,
                    onSelect = { viewModel.onEvent(SettingsEvent.SelectProvider(provider.type)) },
                    onEdit = {
                        viewModel.onEvent(SettingsEvent.EditProvider(provider))
                        showEditDialog = true
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Text(
                    text = "AI Action Mode",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                ActionModeSelector(
                    selectedMode = uiState.actionMode,
                    onModeSelected = { viewModel.onEvent(SettingsEvent.SetActionMode(it)) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Text(
                    text = "Local AI",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                SettingsNavigationCard(
                    icon = Icons.Default.Memory,
                    title = "Ollama Models",
                    subtitle = "Download and manage local AI models",
                    onClick = onNavigateToOllama
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Remote Control",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                SettingsNavigationCard(
                    icon = Icons.Default.Computer,
                    title = "PC Connections",
                    subtitle = "Connect to your computer for file editing",
                    onClick = onNavigateToPcConnection
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Storage",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                CacheSettingsCard(
                    cacheSize = uiState.cacheSize,
                    isClearing = uiState.isClearingCache,
                    onClearCache = { viewModel.onEvent(SettingsEvent.ClearCache) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "About",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                AboutCard()
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                HelpCard()
            }
        }
    }

    if (showEditDialog && uiState.editingProvider != null) {
        ProviderEditDialog(
            provider = uiState.editingProvider!!,
            onDismiss = {
                showEditDialog = false
                viewModel.onEvent(SettingsEvent.CancelEdit)
            },
            onBaseUrlChange = { viewModel.onEvent(SettingsEvent.UpdateBaseUrl(it)) },
            onApiKeyChange = { viewModel.onEvent(SettingsEvent.UpdateApiKey(it)) },
            onModelNameChange = { viewModel.onEvent(SettingsEvent.UpdateModelName(it)) },
            onSave = { viewModel.onEvent(SettingsEvent.SaveProvider) },
            isSaving = uiState.isSaving
        )
    }
}

@Composable
private fun ActionModeSelector(
    selectedMode: AiActionMode,
    onModeSelected: (AiActionMode) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            AiActionMode.entries.forEach { mode ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onModeSelected(mode) }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = mode == selectedMode,
                        onClick = { onModeSelected(mode) }
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = mode.name.lowercase().replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = when (mode) {
                                AiActionMode.PLAN -> "Review each change before it happens"
                                AiActionMode.AUTOEDIT -> "Changes happen automatically"
                                AiActionMode.BYPASS -> "Skip permission prompts"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProviderCard(
    provider: AiProviderConfig,
    isActive: Boolean,
    onSelect: () -> Unit,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onSelect)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = provider.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium
                    )
                    if (isActive) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Badge { Text("Active") }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = provider.modelName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (provider.apiKey.isNotBlank()) {
                    Text(
                        text = "API Key: ••••••••",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            IconButton(onClick = onEdit) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit ${provider.name}"
                )
            }
        }
    }
}

@Composable
private fun ProviderEditDialog(
    provider: AiProviderConfig,
    onDismiss: () -> Unit,
    onBaseUrlChange: (String) -> Unit,
    onApiKeyChange: (String) -> Unit,
    onModelNameChange: (String) -> Unit,
    onSave: () -> Unit,
    isSaving: Boolean
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Configure ${provider.name}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = provider.baseUrl,
                    onValueChange = onBaseUrlChange,
                    label = { Text("Base URL") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = provider.type != AiProviderType.OLLAMA
                )
                OutlinedTextField(
                    value = provider.modelName,
                    onValueChange = onModelNameChange,
                    label = { Text("Model Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = provider.apiKey,
                    onValueChange = onApiKeyChange,
                    label = { Text("API Key") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onSave,
                enabled = !isSaving
            ) {
                Text(if (isSaving) "Saving..." else "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun SettingsNavigationCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
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
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.NavigateNext,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun HelpCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Provider Setup",
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "• DeepSeek/OpenAI: Enter API key and model name",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "• Anthropic: Get API key from console.anthropic.com",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "• Gemini: Get API key from aistudio.google.com",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "• Ollama: Install locally, models auto-detected",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
