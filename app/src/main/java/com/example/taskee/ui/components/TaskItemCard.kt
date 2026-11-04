package com.example.taskee.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taskee.data.Task
import com.example.taskee.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TaskItemCard(
    task: Task,
    onToggleComplete: () -> Unit,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .clickable(onClick = onClick)
            .testTag("task_item_${task.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, top = 12.dp, bottom = 12.dp, end = 0.dp)
        ) {
            // Circle Checkbox
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(
                        if (task.isCompleted) AccentLime else Color.Transparent
                    )
                    .clickable(onClick = onToggleComplete)
                    .then(
                        if (!task.isCompleted) Modifier.background(
                            Color.Transparent,
                            CircleShape
                        ) else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (task.isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Completed",
                        tint = DarkBackground,
                        modifier = Modifier.size(18.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.1f))
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Title and due date
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = task.title,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = if (task.isCompleted) TextMuted else TextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                    )
                )

                if (task.dueAtMillis != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formatDueDateTime(task.dueAtMillis),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (task.isCompleted) TextMuted else TextSecondary,
                            fontSize = 13.sp
                        )
                    )
                }
            }

            // Delete action
            IconButton(
                onClick = onDelete,
                modifier = Modifier.testTag("delete_task_${task.id}")
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Delete task",
                    tint = StatusError.copy(alpha = 0.8f),
                    modifier = Modifier.size(20.dp)
                )
            }

            // Right accent strip
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height(48.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 4.dp,
                            bottomStart = 4.dp,
                            topEnd = 0.dp,
                            bottomEnd = 0.dp
                        )
                    )
                    .background(if (task.isCompleted) StatusSuccess else StatusError)
            )
        }
    }
}

private fun formatDueDateTime(millis: Long): String {
    val date = Date(millis)
    val formatter = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
    return formatter.format(date)
}
