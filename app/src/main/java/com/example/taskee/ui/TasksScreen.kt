package com.example.taskee.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.taskee.R
import com.example.taskee.data.Task
import com.example.taskee.ui.components.CreateTaskBottomSheet
import com.example.taskee.ui.components.SwipeableTaskItem
import com.example.taskee.ui.components.TaskSummaryDashboardCard
import com.example.taskee.ui.theme.*

enum class TaskFilter(val label: String) {
    ALL("All"),
    PENDING("Pending"),
    COMPLETED("Completed")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    viewModel: TaskeeViewModel,
    modifier: Modifier = Modifier,
    showFloatingActionButton: Boolean = false,
    onEditTask: (Task) -> Unit = { viewModel.openEditTask(it) }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedFilter by remember { mutableStateOf(TaskFilter.ALL) }
    var searchQuery by remember { mutableStateOf("") }
    var showCreateTaskSheet by remember { mutableStateOf(false) }

    val allTasks = uiState.tasks
    val pendingCount = allTasks.count { !it.completed }
    val completedCount = allTasks.count { it.completed }
    val totalCount = allTasks.size

    val filteredTasks = remember(allTasks, selectedFilter, searchQuery) {
        allTasks.filter { task ->
            val matchesFilter = when (selectedFilter) {
                TaskFilter.ALL -> true
                TaskFilter.PENDING -> !task.completed
                TaskFilter.COMPLETED -> task.completed
            }
            val matchesSearch = searchQuery.isBlank() || task.title.contains(searchQuery.trim(), ignoreCase = true)
            matchesFilter && matchesSearch
        }
    }

    val pendingFiltered = filteredTasks.filter { !it.completed }
    val completedFiltered = filteredTasks.filter { it.completed }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("tasks_screen")
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("task_lazy_column"),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // 1. Summary Analytics Header
            item(key = "tasks_summary_dashboard_header") {
                TaskSummaryDashboardCard(tasks = allTasks)
            }

            // 2. Search Bar Header
            item(key = "tasks_search_header") {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Rechercher une tâche...", color = TextMuted, fontSize = 14.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Rechercher",
                            tint = TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Effacer",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentLime,
                        unfocusedBorderColor = BorderColor,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = CardBackground,
                        unfocusedContainerColor = CardBackground
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                        .height(52.dp)
                        .testTag("task_search_input")
                )
            }

            // 3. Filter Chips Header
            item(key = "tasks_filter_header") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TaskFilter.entries.forEach { filter ->
                        val isSelected = selectedFilter == filter
                        val count = when (filter) {
                            TaskFilter.ALL -> totalCount
                            TaskFilter.PENDING -> pendingCount
                            TaskFilter.COMPLETED -> completedCount
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) AccentLime else CardBackground)
                                .clickable { selectedFilter = filter }
                                .padding(horizontal = 14.dp, vertical = 7.dp)
                                .testTag("filter_chip_${filter.name.lowercase()}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = filter.label,
                                    color = if (isSelected) TextDark else TextSecondary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(if (isSelected) TextDark.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.08f))
                                        .padding(horizontal = 6.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "$count",
                                        color = if (isSelected) TextDark else TextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 4. Empty State or Task Items
            if (filteredTasks.isEmpty()) {
                item(key = "tasks_empty_state") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp, horizontal = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Image(
                                painter = painterResource(id = R.drawable.img_nodata),
                                contentDescription = "Aucune tâche",
                                modifier = Modifier.size(160.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = if (searchQuery.isNotEmpty()) "Aucun résultat pour « $searchQuery »" else "Aucune tâche trouvée",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (searchQuery.isNotEmpty()) "Essayez avec d'autres mots clés" else "Appuyez sur + ci-dessous pour créer votre première tâche",
                                color = TextSecondary,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(
                                onClick = { showCreateTaskSheet = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AccentLime,
                                    contentColor = TextDark
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.testTag("empty_state_add_button")
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Créer une tâche", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                if (pendingFiltered.isNotEmpty()) {
                    item(key = "header_pending") {
                        Text(
                            text = "En cours (${pendingFiltered.size})",
                            color = TextSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                        )
                    }
                    items(pendingFiltered, key = { it.id }) { task ->
                        SwipeableTaskItem(
                            task = task,
                            onToggleComplete = { viewModel.toggleTaskComplete(task.id) },
                            onClick = { onEditTask(task) },
                            onDelete = { viewModel.deleteTask(task.id) }
                        )
                    }
                }

                if (completedFiltered.isNotEmpty()) {
                    item(key = "header_completed") {
                        Text(
                            text = "Terminées (${completedFiltered.size})",
                            color = TextMuted,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                        )
                    }
                    items(completedFiltered, key = { it.id }) { task ->
                        SwipeableTaskItem(
                            task = task,
                            onToggleComplete = { viewModel.toggleTaskComplete(task.id) },
                            onClick = { onEditTask(task) },
                            onDelete = { viewModel.deleteTask(task.id) }
                        )
                    }
                }
            }
        }

        // Floating Action Button
        if (showFloatingActionButton) {
            FloatingActionButton(
                onClick = { showCreateTaskSheet = true },
                containerColor = AccentLime,
                contentColor = TextDark,
                shape = CircleShape,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 24.dp, end = 24.dp)
                    .size(58.dp)
                    .testTag("add_task_fab")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Créer une nouvelle tâche",
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        // Modal Bottom Sheet for Task Creation
        if (showCreateTaskSheet) {
            CreateTaskBottomSheet(
                onDismissRequest = { showCreateTaskSheet = false },
                onSaveTask = { title, dueAtMillis, priority, category ->
                    viewModel.saveTask(title, dueAtMillis, priority, category)
                    showCreateTaskSheet = false
                }
            )
        }
    }
}
