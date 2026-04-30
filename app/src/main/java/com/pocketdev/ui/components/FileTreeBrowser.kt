package com.pocketdev.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pocketdev.R
import com.pocketdev.domain.model.RemoteFile
import com.pocketdev.domain.model.RemoteFileType

@Composable
fun FileTreeBrowser(
    files: List<RemoteFile>,
    breadcrumb: List<String>,
    onFileClick: (RemoteFile) -> Unit,
    onNavigateUp: () -> Unit,
    onBreadcrumbClick: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        BreadcrumbBar(
            breadcrumb = breadcrumb,
            onNavigateUp = onNavigateUp,
            onBreadcrumbClick = onBreadcrumbClick
        )

        HorizontalDivider()

        if (files.isEmpty()) {
            EmptyState()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                itemsIndexed(files) { index, file ->
                    FileItem(
                        file = file,
                        onClick = { onFileClick(file) }
                    )
                    if (index < files.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BreadcrumbBar(
    breadcrumb: List<String>,
    onNavigateUp: () -> Unit,
    onBreadcrumbClick: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (breadcrumb.isNotEmpty()) {
            IconButton(
                onClick = onNavigateUp,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.repo_detail_back),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "/",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            breadcrumb.forEachIndexed { index, segment ->
                Text(
                    text = segment,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (index == breadcrumb.lastIndex) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.primary
                    },
                    modifier = Modifier.clickable { onBreadcrumbClick(index) }
                )

                if (index < breadcrumb.lastIndex) {
                    Text(
                        text = "/",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun FileItem(
    file: RemoteFile,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = when (file.type) {
                RemoteFileType.DIRECTORY -> Icons.Default.Folder
                RemoteFileType.FILE -> Icons.Default.Description
                else -> Icons.Default.Description
            },
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = when (file.type) {
                RemoteFileType.DIRECTORY -> Color(0xFFFFB74D)
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = file.name,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (file.type == RemoteFileType.FILE) {
                Text(
                    text = formatFileSize(file.size),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (file.type == RemoteFileType.DIRECTORY) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Folder,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.file_browser_no_files),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun getFileIcon(fileName: String) = Icons.Default.Description

private fun formatFileSize(size: Long): String {
    return when {
        size < 1024 -> "$size B"
        size < 1024 * 1024 -> "${size / 1024} KB"
        size < 1024 * 1024 * 1024 -> "${size / (1024 * 1024)} MB"
        else -> "${size / (1024 * 1024 * 1024)} GB"
    }
}
