package com.example.taskee.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taskee.R
import com.example.taskee.ui.components.*
import com.example.taskee.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun TaskeeHomeScreen(
    viewModel: TaskeeViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Handle undo snackbar
    LaunchedEffect(uiState.lastDeletedTask) {
        val deleted = uiState.lastDeletedTask
        if (deleted != null) {
            scope.launch {
                val result = snackbarHostState.showSnackbar(
                    message = "${deleted.title} deleted",
                    actionLabel = "Undo",
                    duration = SnackbarDuration.Short
                )
                if (result == SnackbarResult.ActionPerformed) {
                    viewModel.undoDeleteTask()
                } else {
                    viewModel.clearUndo()
                }
            }
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen"),
        containerColor = DarkBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.openCreate() },
                containerColor = AccentLime,
                contentColor = TextDark,
                shape = CircleShape,
                modifier = Modifier
                    .size(58.dp)
                    .testTag("add_fab")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add task or note",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            UserInfoHeader()

            PillTabBar(
                selectedTab = uiState.selectedTab,
                tasksCount = uiState.tasks.count { !it.isCompleted },
                notesCount = uiState.notes.size,
                onTabSelected = { viewModel.selectTab(it) }
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (uiState.selectedTab == 0) {
                // Tasks Tab
                val pendingTasks = uiState.tasks.filter { !it.isCompleted }
                val completedTasks = uiState.tasks.filter { it.isCompleted }

                if (uiState.tasks.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Image(
                                painter = painterResource(id = R.drawable.img_nodata),
                                contentDescription = "No tasks",
                                modifier = Modifier.size(180.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No tasks yet! Tap + to create one.",
                                color = TextSecondary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("task_list"),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        if (pendingTasks.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Pending (${pendingTasks.size})",
                                    color = TextSecondary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                                )
                            }
                            items(pendingTasks, key = { it.id }) { task ->
                                TaskItemCard(
                                    task = task,
                                    onToggleComplete = { viewModel.toggleTaskComplete(task.id) },
                                    onClick = { viewModel.openEditTask(task) },
                                    onDelete = { viewModel.deleteTask(task.id) }
                                )
                            }
                        }

                        if (completedTasks.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Completed (${completedTasks.size})",
                                    color = TextMuted,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                                )
                            }
                            items(completedTasks, key = { it.id }) { task ->
                                TaskItemCard(
                                    task = task,
                                    onToggleComplete = { viewModel.toggleTaskComplete(task.id) },
                                    onClick = { viewModel.openEditTask(task) },
                                    onDelete = { viewModel.deleteTask(task.id) }
                                )
                            }
                        }
                    }
                }
            } else {
                // Notes Tab
                if (uiState.notes.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Image(
                                painter = painterResource(id = R.drawable.img_nodata),
                                contentDescription = "No notes",
                                modifier = Modifier.size(180.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No notes yet! Tap + to capture a thought.",
                                color = TextSecondary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                } else {
                    val currentNote = uiState.notes[uiState.currentNoteIndex]
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        NoteSwipeCard(
                            note = currentNote,
                            currentIndex = uiState.currentNoteIndex,
                            totalNotes = uiState.notes.size,
                            onPrevious = { viewModel.previousNote() },
                            onNext = { viewModel.nextNote() },
                            onEdit = { viewModel.openEditNote(currentNote) },
                            onDelete = { viewModel.deleteNote(currentNote.id) }
                        )
                    }
                }
            }
        }
    }

    if (uiState.isCreateOrEditVisible) {
        CreateOrEditDialog(
            initialTab = uiState.dialogTab,
            editingTask = uiState.editingTask,
            editingNote = uiState.editingNote,
            onDismiss = { viewModel.closeDialog() },
            onSaveTask = { title, dueAt -> viewModel.saveTask(title, dueAt) },
            onSaveNote = { content -> viewModel.saveNote(content) }
        )
    }
}
