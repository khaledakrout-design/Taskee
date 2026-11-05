package com.example.taskee.ui.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.taskee.data.Note
import com.example.taskee.data.Priority
import com.example.taskee.data.Task
import com.example.taskee.data.TaskCategory
import com.example.taskee.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateOrEditDialog(
    initialTab: Int,
    editingTask: Task?,
    editingNote: Note?,
    onDismiss: () -> Unit,
    onSaveTask: (title: String, dueAtMillis: Long?, priority: Priority, category: TaskCategory) -> Unit,
    onSaveNote: (content: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember(initialTab, editingNote, editingTask) {
        mutableIntStateOf(if (editingNote != null) 1 else initialTab)
    }

    // Task fields
    var taskTitle by remember(editingTask) { mutableStateOf(editingTask?.title ?: "") }
    var taskDueAtMillis by remember(editingTask) { mutableStateOf(editingTask?.dueAtMillis) }
    var taskPriority by remember(editingTask) { mutableStateOf(editingTask?.priority ?: Priority.MEDIUM) }
    var taskCategory by remember(editingTask) { mutableStateOf(editingTask?.category ?: TaskCategory.PERSONAL) }
    var taskTitleError by remember(editingTask) { mutableStateOf(false) }

    // Note fields
    var noteContent by remember(editingNote) { mutableStateOf(editingNote?.content ?: "") }
    var noteContentError by remember(editingNote) { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight()
                .testTag("create_edit_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val title = when {
                        editingTask != null -> "Modifier la tâche"
                        editingNote != null -> "Modifier la note"
                        selectedTab == 0 -> "Créer une tâche"
                        else -> "Créer une note"
                    }
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fermer",
                            tint = TextSecondary
                        )
                    }
                }

                // If not editing an existing item, show the switcher tabs
                if (editingTask == null && editingNote == null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(BorderColor)
                            .padding(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (selectedTab == 0) TabGrey else Color.Transparent)
                                .clickable { selectedTab = 0 }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Tâche",
                                color = if (selectedTab == 0) TextPrimary else TextSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (selectedTab == 1) TabGrey else Color.Transparent)
                                .clickable { selectedTab = 1 }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Note",
                                color = if (selectedTab == 1) TextPrimary else TextSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Tab 0: Task Form
                if (selectedTab == 0) {
                    Text(
                        text = "Titre",
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
                        placeholder = { Text("Écrivez le titre de la tâche...", color = TextMuted) },
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

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Category",
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
                                    .testTag("dialog_category_${cat.name.lowercase()}"),
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

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Catégorie",
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

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
                                    .testTag("dialog_priority_${p.name.lowercase()}"),
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

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Date & Heure d'échéance",
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Date picker button
                        val cal = Calendar.getInstance()
                        taskDueAtMillis?.let { cal.timeInMillis = it }

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
                            border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(brush = androidx.compose.ui.graphics.SolidColor(BorderColor)),
                            modifier = Modifier.weight(1f)
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
                            border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(brush = androidx.compose.ui.graphics.SolidColor(BorderColor)),
                            modifier = Modifier.weight(1f)
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

                        if (taskDueAtMillis != null) {
                            IconButton(
                                onClick = { taskDueAtMillis = null },
                                modifier = Modifier.align(Alignment.CenterVertically)
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
                            .defaultMinSize(minHeight = 50.dp)
                            .testTag("submit_task_button")
                    ) {
                        Text(
                            text = if (editingTask != null) "Modifier la tâche" else "Créer la tâche",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                } else {
                    // Tab 1: Note Form
                    Text(
                        text = "Écrire une note",
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = noteContent,
                        onValueChange = {
                            noteContent = it
                            if (it.isNotBlank()) noteContentError = false
                        },
                        placeholder = { Text("Notez vos idées...", color = TextMuted) },
                        isError = noteContentError,
                        supportingText = if (noteContentError) {
                            { Text("La note ne peut pas être vide", color = StatusError) }
                        } else null,
                        minLines = 4,
                        maxLines = 6,
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
                            .testTag("note_content_input")
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    Button(
                        onClick = {
                            if (noteContent.trim().isBlank()) {
                                noteContentError = true
                            } else {
                                onSaveNote(noteContent.trim())
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentLime,
                            contentColor = TextDark
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("submit_note_button")
                    ) {
                        Text(
                            text = if (editingNote != null) "Modifier la note" else "Créer la note",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}
