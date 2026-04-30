package com.pocketdev.ui.screens.repos

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pocketdev.domain.model.RemoteBranch
import com.pocketdev.domain.model.RemoteFile
import com.pocketdev.domain.model.RemoteFileType
import com.pocketdev.ui.components.BranchSelector
import com.pocketdev.ui.components.FileTreeBrowser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepoDetailScreen(
    onNavigateBack: () -> Unit,
    onNavigateToEditor: (fullName: String, branch: String, path: String, sha: String) -> Unit,
    viewModel: RepoDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var showBranchSelector by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.onEvent(RepoDetailEvent.ClearError)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = uiState.repository?.name ?: "Repository",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (uiState.currentPath.isNotEmpty()) {
                            Text(
                                text = uiState.currentPath,
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showBranchSelector = true }) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = uiState.selectedBranch,
                                style = MaterialTheme.typography.labelSmall
                            )
                            Icon(
                                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = "Select branch",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.selectedFile != null -> {
                    FileContentView(
                        file = uiState.selectedFile!!,
                        content = uiState.fileContent?.content,
                        isLoading = uiState.isLoadingContent,
                        onBack = { viewModel.onEvent(RepoDetailEvent.ClearFileSelection) },
                        onEdit = { file ->
                            uiState.repository?.let { repo ->
                                onNavigateToEditor(
                                    repo.fullName,
                                    uiState.selectedBranch,
                                    file.path,
                                    file.sha
                                )
                            }
                        }
                    )
                }
                else -> {
                    FileTreeBrowser(
                        files = uiState.files,
                        breadcrumb = uiState.breadcrumb,
                        onFileClick = { file ->
                            viewModel.onEvent(RepoDetailEvent.NavigateToFile(file))
                        },
                        onNavigateUp = { viewModel.onEvent(RepoDetailEvent.NavigateUp) },
                        onBreadcrumbClick = { index ->
                            val path = uiState.breadcrumb.take(index + 1).joinToString("/")
                            viewModel.onEvent(RepoDetailEvent.NavigateToPath(path))
                        }
                    )
                }
            }

            AnimatedVisibility(
                visible = showBranchSelector,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                BranchSelector(
                    branches = uiState.branches,
                    selectedBranch = uiState.selectedBranch,
                    onBranchSelected = { branch ->
                        viewModel.onEvent(RepoDetailEvent.SelectBranch(branch))
                        showBranchSelector = false
                    },
                    onDismiss = { showBranchSelector = false }
                )
            }
        }
    }
}

@Suppress("UNUSED_PARAMETER")
@Composable
private fun FileContentView(
    file: RemoteFile,
    content: String?,
    isLoading: Boolean,
    onBack: () -> Unit,
    onEdit: (RemoteFile) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (file.type == RemoteFileType.DIRECTORY) Icons.Default.Folder else Icons.Default.Description,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = file.name,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            if (file.type != RemoteFileType.DIRECTORY) {
                androidx.compose.material3.Button(
                    onClick = { onEdit(file) }
                ) {
                    Text("Edit")
                }
            }
        }

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            content?.let {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = it,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
