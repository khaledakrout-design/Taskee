package com.example.taskee.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.taskee.R
import com.example.taskee.ui.components.*
import com.example.taskee.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskeeHomeScreen(
    viewModel: TaskeeViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showTaskBottomSheet by remember { mutableStateOf(false) }

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

    TaskeeTheme(darkTheme = isDarkMode) {
        Scaffold(
            modifier = modifier
                .fillMaxSize()
                .testTag("home_screen"),
            containerColor = DarkBackground,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        if (uiState.selectedTab == 0) {
                            showTaskBottomSheet = true
                        } else {
                            viewModel.openCreate(1)
                        }
                    },
                    containerColor = AccentLime,
                    contentColor = TextDark,
                    shape = CircleShape,
                    modifier = Modifier
                        .size(58.dp)
                        .testTag("add_fab")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = if (uiState.selectedTab == 0) "Create new task" else "Create new note",
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
                UserInfoHeader(
                    isDarkMode = isDarkMode,
                    onToggleTheme = { viewModel.toggleTheme() },
                    onActionClick = { viewModel.openCreate() }
                )

            PillTabBar(
                selectedTab = uiState.selectedTab,
                tasksCount = uiState.tasks.count { !it.completed },
                notesCount = uiState.notes.size,
                onTabSelected = { viewModel.selectTab(it) }
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (uiState.selectedTab == 0) {
                TasksScreen(
                    viewModel = viewModel,
                    modifier = Modifier.weight(1f)
                )
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
                    val safeIndex = uiState.currentNoteIndex.coerceIn(0, (uiState.notes.size - 1).coerceAtLeast(0))
                    val currentNote = uiState.notes.getOrNull(safeIndex) ?: uiState.notes.firstOrNull()
                    if (currentNote != null) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = 80.dp)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            NoteSwipeCard(
                                note = currentNote,
                                currentIndex = safeIndex,
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
    }

    if (showTaskBottomSheet) {
        CreateTaskBottomSheet(
            onDismissRequest = { showTaskBottomSheet = false },
            onSaveTask = { title, dueAt, priority, category ->
                viewModel.saveTask(title, dueAt, priority, category)
                showTaskBottomSheet = false
            }
        )
    }

    if (uiState.isCreateOrEditVisible) {
        CreateOrEditDialog(
            initialTab = uiState.dialogTab,
            editingTask = uiState.editingTask,
            editingNote = uiState.editingNote,
            onDismiss = { viewModel.closeDialog() },
            onSaveTask = { title, dueAt, priority, category -> viewModel.saveTask(title, dueAt, priority, category) },
            onSaveNote = { content -> viewModel.saveNote(content) }
        )
    }
    }
}
