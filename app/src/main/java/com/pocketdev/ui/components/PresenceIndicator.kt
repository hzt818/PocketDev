package com.pocketdev.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.pocketdev.domain.model.Collaborator

@Composable
fun PresenceIndicator(
    collaborators: List<Collaborator>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (collaborators.size > 3) {
            Text(
                text = "${collaborators.size} collaborators",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        Row(horizontalArrangement = Arrangement.End) {
            collaborators.take(3).reversed().forEachIndexed { index, collaborator ->
                CollaboratorAvatar(
                    collaborator = collaborator,
                    modifier = Modifier.padding(start = if (index > 0) (-8).dp else 0.dp)
                )
            }

            if (collaborators.size > 3) {
                Box(
                    modifier = Modifier
                        .padding(start = (-8).dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+${collaborators.size - 3}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun CollaboratorAvatar(
    collaborator: Collaborator,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(Color(collaborator.color))
            .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (collaborator.avatarUrl != null) {
            AsyncImage(
                model = collaborator.avatarUrl,
                contentDescription = collaborator.name,
                modifier = Modifier.size(28.dp),
                contentScale = ContentScale.Crop
            )
        } else {
            Text(
                text = collaborator.name.take(1).uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = Color.White
            )
        }
    }
}

@Composable
fun CollaboratorsList(
    collaborators: List<Collaborator>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(16.dp)) {
        Text(
            text = "Collaborators (${collaborators.size})",
            style = MaterialTheme.typography.titleSmall
        )

        collaborators.forEach { collaborator ->
            CollaboratorItem(collaborator = collaborator)
        }
    }
}

@Composable
private fun CollaboratorItem(
    collaborator: Collaborator,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CollaboratorAvatar(collaborator = collaborator)

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = collaborator.name,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            collaborator.cursorPosition?.let { pos ->
                Text(
                    text = "Line ${pos.line}, Col ${pos.column}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(Color(collaborator.color))
        )
    }
}

@Composable
private fun Modifier.fillMaxWidth(): Modifier {
    return this.then(Modifier.fillMaxWidth())
}
