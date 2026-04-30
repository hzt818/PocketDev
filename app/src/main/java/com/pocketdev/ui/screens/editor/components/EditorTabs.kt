package com.pocketdev.ui.screens.editor.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pocketdev.R
import com.pocketdev.domain.model.EditorTab

@Composable
fun EditorTabs(
    tabs: List<EditorTab>,
    activeTabIndex: Int,
    onTabSelect: (Int) -> Unit,
    onTabClose: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 2.dp
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            itemsIndexed(tabs, key = { _, tab -> tab.id }) { index, tab ->
                EditorTabItem(
                    tab = tab,
                    isActive = index == activeTabIndex,
                    onSelect = { onTabSelect(index) },
                    onClose = { onTabClose(tab.id) }
                )
            }
        }
    }
}

@Composable
private fun EditorTabItem(
    tab: EditorTab,
    isActive: Boolean,
    onSelect: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isActive) {
        MaterialTheme.colorScheme.surface
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    val topBarColor = if (isActive) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    Surface(
        modifier = modifier
            .width(160.dp)
            .fillMaxHeight()
            .clickable(onClick = onSelect),
        color = backgroundColor,
        tonalElevation = if (isActive) 0.dp else 0.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .fillMaxHeight()
                    .background(topBarColor)
            )

            Text(
                text = tab.fileName,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            )

            if (tab.isModified) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                )
            }

            IconButton(
                onClick = onClose,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.search_close),
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
