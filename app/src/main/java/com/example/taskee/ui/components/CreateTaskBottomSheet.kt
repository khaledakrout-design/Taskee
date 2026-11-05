package com.example.taskee.ui.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taskee.data.Priority
import com.example.taskee.data.TaskCategory
import com.example.taskee.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateTaskBottomSheet(
    onDismissRequest: () -> Unit,
    onSaveTask: (title: String, dueAtMillis: Long?, priority: Priority, category: TaskCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var taskTitle by remember { mutableStateOf("") }
    var taskDueAtMillis by remember { mutableStateOf<Long?>(null) }
    var taskPriority by remember { mutableStateOf(Priority.MEDIUM) }
    var taskCategory by remember { mutableStateOf(TaskCategory.PERSONAL) }
    var taskTitleError by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = DarkSurface,
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = BorderColor,
                modifier = Modifier.padding(top = 8.dp)
            )
        },
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        modifier = modifier.testTag("create_task_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Nouvelle tâche",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 22.sp
                    )
                )

                IconButton(
                    onClick = onDismissRequest,
                    modifier = Modifier.testTag("close_sheet_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Fermer",
                        tint = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Task title input
            Text(
                text = "Titre de la tâche",
                style = MaterialTheme.typography.labelLarge.copy(
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = taskTitle,
                onValueChange = {
                    taskTitle = it
                    if (it.isNotBlank()) taskTitleError = false
                },
                placeholder = { Text("Que devez-vous faire ?", color = TextMuted) },
                isError = taskTitleError,
                supportingText = if (taskTitleError) {
                    { Text("Le titre ne peut pas être vide", color = StatusError) }
                } else null,
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentLime,
                    unfocusedBorderColor = BorderColor,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = CardBackground,
                    unfocusedContainerColor = CardBackground
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("task_title_input")
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Category Selection
            Text(
                text = "Catégorie",
                style = MaterialTheme.typography.labelLarge.copy(
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                TaskCategory.entries.forEach { cat ->
                    val isSelected = taskCategory == cat
                    val catColor = when (cat) {
                        TaskCategory.WORK -> Color(0xFFAB47BC)
                        TaskCategory.PERSONAL -> Color(0xFF26A69A)
                        TaskCategory.SHOPPING -> Color(0xFFFFA726)
                        TaskCategory.HEALTH -> Color(0xFFEC407A)
                        TaskCategory.OTHER -> Color(0xFF78909C)
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) catColor.copy(alpha = 0.2f) else CardBackground)
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) catColor else BorderColor,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { taskCategory = cat }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                            .testTag("select_category_${cat.name.lowercase()}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = cat.label,
                            color = if (isSelected) catColor else TextSecondary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Priority Selection
            Text(
                text = "Niveau de priorité",
                style = MaterialTheme.typography.labelLarge.copy(
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Priority.entries.forEach { p ->
                    val isSelected = taskPriority == p
                    val chipColor = when (p) {
                        Priority.HIGH -> Color(0xFFEF5350)
                        Priority.MEDIUM -> Color(0xFFFFB74D)
                        Priority.LOW -> Color(0xFF64B5F6)
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) chipColor.copy(alpha = 0.2f) else CardBackground)
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) chipColor else BorderColor,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { taskPriority = p }
                            .padding(horizontal = 18.dp, vertical = 8.dp)
                            .testTag("select_priority_${p.name.lowercase()}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = p.label,
                            color = if (isSelected) chipColor else TextSecondary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Due Date & Time Section
            Text(
                text = "Date & Heure d'échéance",
                style = MaterialTheme.typography.labelLarge.copy(
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Quick preset chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickDateChip(
                    label = "Aujourd'hui",
                    onClick = {
                        val cal = Calendar.getInstance()
                        cal.set(Calendar.HOUR_OF_DAY, 18)
                        cal.set(Calendar.MINUTE, 0)
                        cal.set(Calendar.SECOND, 0)
                        taskDueAtMillis = cal.timeInMillis
                    }
                )
                QuickDateChip(
                    label = "Demain",
                    onClick = {
                        val cal = Calendar.getInstance()
                        cal.add(Calendar.DAY_OF_YEAR, 1)
                        cal.set(Calendar.HOUR_OF_DAY, 9)
                        cal.set(Calendar.MINUTE, 0)
                        cal.set(Calendar.SECOND, 0)
                        taskDueAtMillis = cal.timeInMillis
                    }
                )
                QuickDateChip(
                    label = "Semaine prochaine",
                    onClick = {
                        val cal = Calendar.getInstance()
                        cal.add(Calendar.WEEK_OF_YEAR, 1)
                        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                        cal.set(Calendar.HOUR_OF_DAY, 9)
                        cal.set(Calendar.MINUTE, 0)
                        cal.set(Calendar.SECOND, 0)
                        taskDueAtMillis = cal.timeInMillis
                    }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Custom Date and Time pickers
            val cal = Calendar.getInstance()
            taskDueAtMillis?.let { cal.timeInMillis = it }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Date picker button
                OutlinedButton(
                    onClick = {
                        val datePicker = DatePickerDialog(
                            context,
                            { _, year, month, dayOfMonth ->
                                cal.set(Calendar.YEAR, year)
                                cal.set(Calendar.MONTH, month)
                                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                                taskDueAtMillis = cal.timeInMillis
                            },
                            cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH),
                            cal.get(Calendar.DAY_OF_MONTH)
                        )
                        datePicker.show()
                    },
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = CardBackground,
                        contentColor = TextPrimary
                    ),
                    border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                        brush = androidx.compose.ui.graphics.SolidColor(BorderColor)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("pick_date_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = "Choisir la date",
                        tint = AccentLime,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    val dateLabel = if (taskDueAtMillis != null) {
                        SimpleDateFormat("d MMM", Locale.FRENCH).format(Date(taskDueAtMillis!!))
                    } else "Choisir la date"
                    Text(
                        text = dateLabel,
                        fontSize = 13.sp,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Time picker button
                OutlinedButton(
                    onClick = {
                        val timePicker = TimePickerDialog(
                            context,
                            { _, hourOfDay, minute ->
                                cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                                cal.set(Calendar.MINUTE, minute)
                                taskDueAtMillis = cal.timeInMillis
                            },
                            cal.get(Calendar.HOUR_OF_DAY),
                            cal.get(Calendar.MINUTE),
                            true
                        )
                        timePicker.show()
                    },
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = CardBackground,
                        contentColor = TextPrimary
                    ),
                    border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                        brush = androidx.compose.ui.graphics.SolidColor(BorderColor)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("pick_time_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Choisir l'heure",
                        tint = AccentLime,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    val timeLabel = if (taskDueAtMillis != null) {
                        SimpleDateFormat("HH:mm", Locale.FRENCH).format(Date(taskDueAtMillis!!))
                    } else "Choisir l'heure"
                    Text(
                        text = timeLabel,
                        fontSize = 13.sp,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Clear button
                if (taskDueAtMillis != null) {
                    IconButton(
                        onClick = { taskDueAtMillis = null },
                        modifier = Modifier.testTag("clear_due_date_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Effacer la date",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Submit Button
            Button(
                onClick = {
                    if (taskTitle.trim().isBlank()) {
                        taskTitleError = true
                    } else {
                        onSaveTask(taskTitle.trim(), taskDueAtMillis, taskPriority, taskCategory)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentLime,
                    contentColor = TextDark
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 52.dp)
                    .testTag("submit_task_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Créer la tâche",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    softWrap = false
                )
            }
        }
    }
}

@Composable
private fun QuickDateChip(
    label: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(CardBackground)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
