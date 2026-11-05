package com.example.taskee.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.example.taskee.data.Priority
import com.example.taskee.data.Task
import com.example.taskee.data.TaskCategory
import com.example.taskee.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalLayoutApi::class)
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
            .padding(horizontal = 16.dp, vertical = 5.dp)
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
                .padding(start = 4.dp, top = 8.dp, bottom = 8.dp, end = 2.dp)
        ) {
            // Checkbox
            Checkbox(
                checked = task.completed,
                onCheckedChange = { onToggleComplete() },
                colors = CheckboxDefaults.colors(
                    checkedColor = AccentLime,
                    checkmarkColor = TextDark,
                    uncheckedColor = TextSecondary
                ),
                modifier = Modifier.testTag("task_checkbox_${task.id}")
            )

            Spacer(modifier = Modifier.width(4.dp))

            // Main Content: Title + Metadata row
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = task.title,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = if (task.completed) TextMuted else TextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        textDecoration = if (task.completed) TextDecoration.LineThrough else TextDecoration.None
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Metadata row: Category, Priority, Due date
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CategoryChip(
                        category = task.category,
                        isCompleted = task.completed
                    )

                    PriorityChip(
                        priority = task.priority,
                        isCompleted = task.completed
                    )

                    if (task.dueAtMillis != null) {
                        Text(
                            text = formatDueDateTime(task.dueAtMillis),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (task.completed) TextMuted else TextSecondary,
                                fontSize = 12.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                }
            }

            // Delete action button
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .size(36.dp)
                    .testTag("delete_task_${task.id}")
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Delete task",
                    tint = StatusError.copy(alpha = 0.8f),
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(2.dp))

            // Right Accent Strip
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .height(44.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 4.dp,
                            bottomStart = 4.dp,
                            topEnd = 0.dp,
                            bottomEnd = 0.dp
                        )
                    )
                    .background(if (task.completed) StatusSuccess else StatusError)
            )
        }
    }
}

@Composable
fun CategoryChip(
    category: TaskCategory,
    isCompleted: Boolean,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (category) {
        TaskCategory.WORK -> Color(0xFFAB47BC).copy(alpha = if (isCompleted) 0.15f else 0.22f) to Color(0xFFCE93D8)
        TaskCategory.PERSONAL -> Color(0xFF26A69A).copy(alpha = if (isCompleted) 0.15f else 0.22f) to Color(0xFF80CBC4)
        TaskCategory.SHOPPING -> Color(0xFFFFA726).copy(alpha = if (isCompleted) 0.15f else 0.22f) to Color(0xFFFFCC80)
        TaskCategory.HEALTH -> Color(0xFFEC407A).copy(alpha = if (isCompleted) 0.15f else 0.22f) to Color(0xFFF48FB1)
        TaskCategory.OTHER -> Color(0xFF78909C).copy(alpha = if (isCompleted) 0.15f else 0.22f) to Color(0xFFB0BEC5)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 7.dp, vertical = 2.dp)
            .testTag("category_chip_${category.name.lowercase()}"),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = category.label,
            color = if (isCompleted) TextMuted else textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }
}

@Composable
fun PriorityChip(
    priority: Priority,
    isCompleted: Boolean,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (priority) {
        Priority.HIGH -> Color(0xFFEF5350).copy(alpha = if (isCompleted) 0.15f else 0.22f) to Color(0xFFEF5350)
        Priority.MEDIUM -> Color(0xFFFFB74D).copy(alpha = if (isCompleted) 0.15f else 0.22f) to Color(0xFFFFB74D)
        Priority.LOW -> Color(0xFF64B5F6).copy(alpha = if (isCompleted) 0.15f else 0.22f) to Color(0xFF64B5F6)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 7.dp, vertical = 2.dp)
            .testTag("priority_chip_${priority.name.lowercase()}"),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = priority.label,
            color = if (isCompleted) TextMuted else textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }
}

private fun formatDueDateTime(millis: Long): String {
    val date = Date(millis)
    val formatter = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
    return formatter.format(date)
}
