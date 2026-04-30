package com.pocketdev.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pocketdev.R

data class FabMenuItem(
    val icon: ImageVector,
    val label: String,
    val onClick: () -> Unit
)

@Composable
fun FloatingActionMenu(
    modifier: Modifier = Modifier,
    items: List<FabMenuItem> = emptyList()
) {
    var isExpanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (isExpanded) 45f else 0f,
        label = "fab_rotation"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.End
            ) {
                items.forEach { item ->
                    FabMenuItemRow(
                        item = item,
                        onClick = {
                            item.onClick()
                            isExpanded = false
                        }
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = { isExpanded = !isExpanded },
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(R.string.fab_menu),
                modifier = Modifier.rotate(rotation)
            )
        }
    }
}

@Composable
private fun FabMenuItemRow(
    item: FabMenuItem,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(4.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.padding(end = 8.dp)
        ) {
            Text(
                text = item.label,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        SmallFloatingActionButton(
            onClick = onClick,
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            shape = CircleShape
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun DefaultFloatingActionMenu(
    onNavigateToChat: () -> Unit,
    onNavigateToEditor: () -> Unit,
    onNavigateToOllama: () -> Unit,
    onNavigateToPcConnection: () -> Unit,
    onNavigateToTerminal: () -> Unit,
    modifier: Modifier = Modifier
) {
    val defaultItems = listOf(
        FabMenuItem(Icons.Default.Chat, stringResource(R.string.fab_chat), onNavigateToChat),
        FabMenuItem(Icons.Default.Code, stringResource(R.string.fab_editor), onNavigateToEditor),
        FabMenuItem(Icons.Default.Memory, stringResource(R.string.fab_ollama), onNavigateToOllama),
        FabMenuItem(Icons.Default.Computer, stringResource(R.string.fab_pc), onNavigateToPcConnection),
        FabMenuItem(Icons.Default.Terminal, stringResource(R.string.fab_terminal), onNavigateToTerminal)
    )

    FloatingActionMenu(
        modifier = modifier,
        items = defaultItems
    )
}