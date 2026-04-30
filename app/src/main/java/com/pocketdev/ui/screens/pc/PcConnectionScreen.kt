package com.pocketdev.ui.screens.pc

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pocketdev.R
import com.pocketdev.domain.model.PcConnectionConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PcConnectionScreen(
    viewModel: PcConnectionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.onEvent(PcConnectionEvent.ClearError)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.pc_title)) },
                actions = {
                    Text(
                        text = if (uiState.activeConnection != null) stringResource(R.string.pc_connected) else stringResource(R.string.pc_disconnected),
                        style = MaterialTheme.typography.bodySmall,
                        color = if (uiState.activeConnection != null)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.onEvent(PcConnectionEvent.ShowAddDialog) }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.pc_add_connection))
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        if (uiState.connections.isEmpty()) {
            EmptyConnectionState(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.connections, key = { it.id }) { connection ->
                    PcConnectionItem(
                        connection = connection,
                        isActive = connection.id == uiState.activeConnection?.id,
                        isTesting = uiState.testingConnection == connection.id,
                        onSetActive = { viewModel.onEvent(PcConnectionEvent.SetActive(connection.id)) },
                        onDelete = { viewModel.onEvent(PcConnectionEvent.RemoveConnection(connection.id)) },
                        onTest = { viewModel.onEvent(PcConnectionEvent.TestConnection(connection.id)) }
                    )
                }
            }
        }

        if (uiState.showAddDialog) {
            AddConnectionDialog(
                onDismiss = { viewModel.onEvent(PcConnectionEvent.HideAddDialog) },
                onConfirm = { name, host, port, apiKey ->
                    viewModel.onEvent(PcConnectionEvent.AddConnection(name, host, port, apiKey))
                }
            )
        }
    }
}

@Composable
private fun EmptyConnectionState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Computer,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(R.string.pc_no_pc_connected),
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = stringResource(R.string.pc_install_cli),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PcConnectionItem(
    connection: PcConnectionConfig,
    isActive: Boolean,
    isTesting: Boolean,
    onSetActive: () -> Unit,
    onDelete: () -> Unit,
    onTest: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
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
                IconButton(onClick = onSetActive) {
                    Icon(
                        imageVector = if (isActive) Icons.Default.RadioButtonChecked
                        else Icons.Default.RadioButtonUnchecked,
                        contentDescription = if (isActive) stringResource(R.string.pc_active) else stringResource(R.string.pc_select),
                        tint = if (isActive) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = connection.name,
                            style = MaterialTheme.typography.titleMedium
                        )
                        if (isActive) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = stringResource(R.string.pc_connected),
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Text(
                        text = "${connection.host}:${connection.port}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row {
                IconButton(onClick = onTest, enabled = !isTesting) {
                    if (isTesting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Computer,
                            contentDescription = stringResource(R.string.pc_test_connection),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.pc_delete),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun AddConnectionDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, Int, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var host by remember { mutableStateOf("") }
    var port by remember { mutableIntStateOf(8080) }
    var apiKey by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.pc_add_pc_title)) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.pc_name)) },
                    placeholder = { Text(stringResource(R.string.pc_placeholder_name)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = host,
                    onValueChange = { host = it },
                    label = { Text(stringResource(R.string.pc_host_ip)) },
                    placeholder = { Text(stringResource(R.string.pc_placeholder_host)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = port.toString(),
                    onValueChange = { port = it.toIntOrNull() ?: 8080 },
                    label = { Text(stringResource(R.string.pc_port)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = apiKey,
                    onValueChange = { apiKey = it },
                    label = { Text(stringResource(R.string.pc_api_key_optional)) },
                    placeholder = { Text(stringResource(R.string.pc_placeholder_api_key)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name, host, port, apiKey) },
                enabled = name.isNotBlank() && host.isNotBlank()
            ) {
                Text(stringResource(R.string.pc_add))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
