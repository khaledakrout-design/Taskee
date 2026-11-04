package com.example.taskee.ui.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.taskee.data.Note
import com.example.taskee.data.Task
import com.example.taskee.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CreateOrEditDialog(
    initialTab: Int,
    editingTask: Task?,
    editingNote: Note?,
    onDismiss: () -> Unit,
    onSaveTask: (title: String, dueAtMillis: Long?) -> Unit,
    onSaveNote: (content: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(if (editingNote != null) 1 else initialTab) }

    // Task fields
    var taskTitle by remember { mutableStateOf(editingTask?.title ?: "") }
    var taskDueAtMillis by remember { mutableStateOf(editingTask?.dueAtMillis) }
    var taskTitleError by remember { mutableStateOf(false) }

    // Note fields
    var noteContent by remember { mutableStateOf(editingNote?.content ?: "") }
    var noteContentError by remember { mutableStateOf(false) }

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
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val title = when {
                        editingTask != null -> "Update Task"
                        editingNote != null -> "Update Note"
                        selectedTab == 0 -> "Create Task"
                        else -> "Create Note"
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
                            contentDescription = "Close",
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
                                text = "Task",
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
                        text = "Title",
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
                        placeholder = { Text("Write task title...", color = TextMuted) },
                        isError = taskTitleError,
                        supportingText = if (taskTitleError) {
                            { Text("Title cannot be empty", color = StatusError) }
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
                        text = "Due Date & Time",
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
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = CardBackground,
                                contentColor = TextPrimary
                            ),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(BorderColor)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = "Date",
                                tint = AccentLime,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            val dateLabel = if (taskDueAtMillis != null) {
                                SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(taskDueAtMillis!!))
                            } else "Set Date"
                            Text(dateLabel, fontSize = 13.sp)
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
                                    false
                                )
                                timePicker.show()
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = CardBackground,
                                contentColor = TextPrimary
                            ),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(BorderColor)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = "Time",
                                tint = AccentLime,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            val timeLabel = if (taskDueAtMillis != null) {
                                SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(taskDueAtMillis!!))
                            } else "Set Time"
                            Text(timeLabel, fontSize = 13.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    Button(
                        onClick = {
                            if (taskTitle.trim().isBlank()) {
                                taskTitleError = true
                            } else {
                                onSaveTask(taskTitle.trim(), taskDueAtMillis)
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
                            .testTag("submit_task_button")
                    ) {
                        Text(
                            text = if (editingTask != null) "Update Task" else "Create Task",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                } else {
                    // Tab 1: Note Form
                    Text(
                        text = "Write a Note",
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
                        placeholder = { Text("Capture your thoughts...", color = TextMuted) },
                        isError = noteContentError,
                        supportingText = if (noteContentError) {
                            { Text("Note cannot be empty", color = StatusError) }
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
                            text = if (editingNote != null) "Update Note" else "Create Note",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}
