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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pocketdev.R
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
    val providerSavedMessage = stringResource(R.string.settings_provider_saved)

    var showEditDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.onEvent(SettingsEvent.ClearError)
        }
    }

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            snackbarHostState.showSnackbar(providerSavedMessage)
            showEditDialog = false
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.settings_title)) }) },
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
                    text = stringResource(R.string.settings_appearance),
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
                    text = stringResource(R.string.settings_editor),
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
                    text = stringResource(R.string.settings_ai_providers),
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
                    text = stringResource(R.string.settings_ai_action_mode),
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
                    text = stringResource(R.string.settings_local_ai),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                SettingsNavigationCard(
                    icon = Icons.Default.Memory,
                    title = stringResource(R.string.settings_ollama_title),
                    subtitle = stringResource(R.string.settings_ollama_subtitle),
                    onClick = onNavigateToOllama
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.settings_remote_control),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                SettingsNavigationCard(
                    icon = Icons.Default.Computer,
                    title = stringResource(R.string.settings_pc_title),
                    subtitle = stringResource(R.string.settings_pc_subtitle),
                    onClick = onNavigateToPcConnection
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.settings_storage),
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
                    text = stringResource(R.string.settings_about),
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
                                AiActionMode.PLAN -> stringResource(R.string.action_mode_plan_desc)
                                AiActionMode.AUTOEDIT -> stringResource(R.string.action_mode_autoedit_desc)
                                AiActionMode.BYPASS -> stringResource(R.string.action_mode_bypass_desc)
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
                        Badge { Text(stringResource(R.string.settings_provider_active)) }
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
                        text = stringResource(R.string.settings_api_key_masked),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            IconButton(onClick = onEdit) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.settings_edit, provider.name)
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
        title = { Text(stringResource(R.string.dialog_configure_provider, provider.name)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = provider.baseUrl,
                    onValueChange = onBaseUrlChange,
                    label = { Text(stringResource(R.string.dialog_base_url)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = provider.type != AiProviderType.OLLAMA
                )
                OutlinedTextField(
                    value = provider.modelName,
                    onValueChange = onModelNameChange,
                    label = { Text(stringResource(R.string.dialog_model_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = provider.apiKey,
                    onValueChange = onApiKeyChange,
                    label = { Text(stringResource(R.string.dialog_api_key)) },
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
                Text(if (isSaving) stringResource(R.string.settings_saving) else stringResource(R.string.settings_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.settings_cancel))
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
                text = stringResource(R.string.settings_provider_setup),
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.help_deepseek_openai),
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = stringResource(R.string.help_anthropic),
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = stringResource(R.string.help_gemini),
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = stringResource(R.string.help_ollama),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
