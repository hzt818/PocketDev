package com.pocketdev.ui.screens.editor.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pocketdev.domain.model.FileItem
import com.pocketdev.domain.model.FileType

@Composable
fun FileTree(
    files: List<FileItem>,
    folderName: String,
    onFileClick: (FileItem) -> Unit,
    onBackClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false
) {
    val listState = rememberLazyListState()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onBackClick != null) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Default.FolderOpen,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(20.dp)
                )
                Text(
                    text = folderName,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }

            HorizontalDivider()

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Loading...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else if (files.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No files",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(files, key = { it.uri.toString() }) { file ->
                        FileTreeItem(
                            file = file,
                            onClick = { onFileClick(file) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FileTreeItem(
    file: FileItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    depth: Int = 0
) {
    val icon = when {
        file.isDirectory -> Icons.Default.Folder
        else -> getFileIcon(file.extension)
    }

    val iconColor = when {
        file.isDirectory -> Color(0xFFFFC107)
        else -> getFileColor(file.extension)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(
                start = (16 + depth * 16).dp,
                end = 16.dp,
                top = 10.dp,
                bottom = 10.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = file.name,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(start = 12.dp)
                .weight(1f)
        )
    }
}

private fun getFileIcon(extension: String?): ImageVector {
    return Icons.Default.Description
}

private fun getFileColor(extension: String?): Color {
    if (extension == null) return Color(0xFF9E9E9E)

    return when (extension.lowercase()) {
        "kt", "kts" -> Color(0xFF7F52FF)
        "java" -> Color(0xFFB07219)
        "py", "pyw" -> Color(0xFF3572A5)
        "js", "mjs", "cjs" -> Color(0xFFF7DF1E)
        "ts", "tsx", "mts" -> Color(0xFF3178C6)
        "go" -> Color(0xFF00ADD8)
        "rs" -> Color(0xFFDEA584)
        "c", "h" -> Color(0xFF555555)
        "cpp", "hpp", "cc", "hh", "cxx", "hxx" -> Color(0xFFF34B7D)
        "cs" -> Color(0xFF178600)
        "swift" -> Color(0xFFFA7343)
        "rb" -> Color(0xFFCC342D)
        "php" -> Color(0xFF4F5D95)
        "html", "htm" -> Color(0xFFE34C26)
        "css", "scss", "sass", "less" -> Color(0xFF563D7C)
        "json" -> Color(0xFF292929)
        "xml" -> Color(0xFF0060AC)
        "yaml", "yml" -> Color(0xFFCB171E)
        "md", "mdx" -> Color(0xFF083FA1)
        "toml" -> Color(0xFF9C4121)
        "gradle" -> Color(0xFF00B9AE)
        "properties" -> Color(0xFFB0B0B0)
        "sh", "bash", "zsh" -> Color(0xFF89E051)
        "sql" -> Color(0xFFE38C00)
        "txt" -> Color(0xFF9E9E9E)
        else -> Color(0xFF9E9E9E)
    }
}
