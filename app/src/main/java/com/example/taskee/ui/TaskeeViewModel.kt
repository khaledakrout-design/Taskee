package com.example.taskee.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskee.data.Note
import com.example.taskee.data.Task
import com.example.taskee.data.TaskeeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TaskeeUiState(
    val tasks: List<Task> = emptyList(),
    val notes: List<Note> = emptyList(),
    val selectedTab: Int = 0,
    val currentNoteIndex: Int = 0,
    val isCreateOrEditVisible: Boolean = false,
    val dialogTab: Int = 0, // 0 for Task, 1 for Note
    val editingTask: Task? = null,
    val editingNote: Note? = null,
    val lastDeletedTask: Task? = null
)

class TaskeeViewModel(private val repository: TaskeeRepository) : ViewModel() {

    private val _selectedTab = MutableStateFlow(repository.getSelectedTab())
    private val _currentNoteIndex = MutableStateFlow(0)
    private val _isCreateOrEditVisible = MutableStateFlow(false)
    private val _dialogTab = MutableStateFlow(0)
    private val _editingTask = MutableStateFlow<Task?>(null)
    private val _editingNote = MutableStateFlow<Note?>(null)
    private val _lastDeletedTask = MutableStateFlow<Task?>(null)
    private val _isDarkMode = MutableStateFlow(repository.isDarkMode())

    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    fun toggleTheme() {
        val newMode = !_isDarkMode.value
        _isDarkMode.value = newMode
        repository.setDarkMode(newMode)
    }

    val uiState: StateFlow<TaskeeUiState> = combine(
        repository.tasksFlow,
        repository.notesFlow,
        _selectedTab,
        _currentNoteIndex,
        _isCreateOrEditVisible,
        _dialogTab,
        _editingTask,
        _editingNote,
        _lastDeletedTask
    ) { params ->
        val tasks = params[0] as List<Task>
        val notes = params[1] as List<Note>
        val selectedTab = params[2] as Int
        val noteIdx = params[3] as Int
        val isVisible = params[4] as Boolean
        val dialogTab = params[5] as Int
        val editingTask = params[6] as Task?
        val editingNote = params[7] as Note?
        val lastDeleted = params[8] as Task?

        val safeNoteIndex = if (notes.isEmpty()) 0 else noteIdx.coerceIn(0, notes.size - 1)

        TaskeeUiState(
            tasks = tasks,
            notes = notes,
            selectedTab = selectedTab,
            currentNoteIndex = safeNoteIndex,
            isCreateOrEditVisible = isVisible,
            dialogTab = dialogTab,
            editingTask = editingTask,
            editingNote = editingNote,
            lastDeletedTask = lastDeleted
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TaskeeUiState(selectedTab = repository.getSelectedTab())
    )

    fun selectTab(tab: Int) {
        _selectedTab.value = tab
        repository.setSelectedTab(tab)
    }

    fun openCreate(initialTab: Int = _selectedTab.value) {
        _editingTask.value = null
        _editingNote.value = null
        _dialogTab.value = initialTab
        _isCreateOrEditVisible.value = true
    }

    fun openEditTask(task: Task) {
        _editingTask.value = task
        _editingNote.value = null
        _dialogTab.value = 0
        _isCreateOrEditVisible.value = true
    }

    fun openEditNote(note: Note) {
        _editingNote.value = note
        _editingTask.value = null
        _dialogTab.value = 1
        _isCreateOrEditVisible.value = true
    }

    fun closeDialog() {
        _isCreateOrEditVisible.value = false
        _editingTask.value = null
        _editingNote.value = null
    }

    fun setDialogTab(tab: Int) {
        _dialogTab.value = tab
    }

    fun toggleTaskComplete(id: Long) {
        viewModelScope.launch {
            repository.toggleTaskComplete(id)
        }
    }

    fun deleteTask(id: Long) {
        viewModelScope.launch {
            val task = uiState.value.tasks.find { it.id == id }
            _lastDeletedTask.value = task
            repository.deleteTask(id)
        }
    }

    fun undoDeleteTask() {
        val task = _lastDeletedTask.value ?: return
        viewModelScope.launch {
            repository.undoDeleteTask(task)
            _lastDeletedTask.value = null
        }
    }

    fun clearUndo() {
        _lastDeletedTask.value = null
    }

    fun saveTask(
        title: String,
        dueAtMillis: Long?,
        priority: com.example.taskee.data.Priority = com.example.taskee.data.Priority.MEDIUM,
        category: com.example.taskee.data.TaskCategory = com.example.taskee.data.TaskCategory.PERSONAL
    ) {
        viewModelScope.launch {
            val current = _editingTask.value
            if (current != null) {
                repository.updateTask(
                    current.copy(title = title, dueAtMillis = dueAtMillis, priority = priority, category = category)
                )
            } else {
                repository.addTask(title, dueAtMillis, priority, category)
            }
            closeDialog()
        }
    }

    fun deleteNote(id: Long) {
        viewModelScope.launch {
            repository.deleteNote(id)
            val currentListSize = uiState.value.notes.size - 1
            if (_currentNoteIndex.value >= currentListSize && currentListSize > 0) {
                _currentNoteIndex.value = currentListSize - 1
            }
        }
    }

    fun saveNote(content: String) {
        viewModelScope.launch {
            val current = _editingNote.value
            if (current != null) {
                repository.updateNote(
                    current.copy(content = content)
                )
            } else {
                repository.addNote(content)
            }
            closeDialog()
        }
    }

    fun nextNote() {
        val total = uiState.value.notes.size
        if (total > 0) {
            _currentNoteIndex.value = (_currentNoteIndex.value + 1) % total
        }
    }

    fun previousNote() {
        val total = uiState.value.notes.size
        if (total > 0) {
            _currentNoteIndex.value = if (_currentNoteIndex.value - 1 < 0) total - 1 else _currentNoteIndex.value - 1
        }
    }
}
