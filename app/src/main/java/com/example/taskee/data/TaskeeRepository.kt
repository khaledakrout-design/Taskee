package com.example.taskee.data

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TaskeeRepository(
    private val taskDao: TaskDao,
    private val noteDao: NoteDao,
    private val context: Context
) {

    constructor(context: Context) : this(
        TaskeeDatabase.getDatabase(context).taskDao(),
        TaskeeDatabase.getDatabase(context).noteDao(),
        context
    )

    private val prefs = context.getSharedPreferences("taskee_prefs", Context.MODE_PRIVATE)
    private val scope = CoroutineScope(Dispatchers.IO)

    val tasksFlow: Flow<List<Task>> = taskDao.getAllTasks()
    val notesFlow: Flow<List<Note>> = noteDao.getAllNotes()

    init {
        scope.launch {
            if (taskDao.getTaskCount() == 0) {
                taskDao.insertTasks(getDefaultTasks())
            }
            if (noteDao.getNoteCount() == 0) {
                noteDao.insertNotes(getDefaultNotes())
            }
        }
    }

    private fun getDefaultTasks(): List<Task> {
        val now = System.currentTimeMillis()
        val oneHour = 3600_000L
        return listOf(
            Task(id = 1L, title = "Concevoir le prototype de l'application", completed = false, dueAtMillis = now + oneHour * 2, priority = Priority.HIGH, category = TaskCategory.WORK),
            Task(id = 2L, title = "Revoir les pull requests de l'équipe", completed = true, dueAtMillis = now - oneHour, priority = Priority.MEDIUM, category = TaskCategory.WORK),
            Task(id = 3L, title = "Faire les courses alimentaires et commissions", completed = false, dueAtMillis = now + oneHour * 5, priority = Priority.LOW, category = TaskCategory.SHOPPING)
        )
    }

    private fun getDefaultNotes(): List<Note> {
        val now = System.currentTimeMillis()
        return listOf(
            Note(
                id = 1L,
                content = "Notes de réunion : Prioriser le contraste en mode sombre et la fluidité des animations.",
                createdAtMillis = now - 7200_000L
            ),
            Note(
                id = 2L,
                content = "Idées pour le prochain sprint : Ajouter des rappels récurrents, des catégories personnalisées et des filtres.",
                createdAtMillis = now - 3600_000L
            )
        )
    }

    suspend fun addTask(
        title: String,
        dueAtMillis: Long?,
        priority: Priority = Priority.MEDIUM,
        category: TaskCategory = TaskCategory.PERSONAL
    ) = withContext(Dispatchers.IO) {
        val newTask = Task(
            id = System.currentTimeMillis(),
            title = title.trim(),
            completed = false,
            dueAtMillis = dueAtMillis,
            priority = priority,
            category = category
        )
        taskDao.insertTask(newTask)
    }

    suspend fun updateTask(task: Task) = withContext(Dispatchers.IO) {
        taskDao.updateTask(task)
    }

    suspend fun toggleTaskComplete(id: Long) = withContext(Dispatchers.IO) {
        taskDao.toggleTaskComplete(id)
    }

    suspend fun deleteTask(id: Long) = withContext(Dispatchers.IO) {
        taskDao.deleteTaskById(id)
    }

    suspend fun undoDeleteTask(task: Task) = withContext(Dispatchers.IO) {
        taskDao.insertTask(task)
    }

    suspend fun addNote(content: String) = withContext(Dispatchers.IO) {
        val newNote = Note(
            id = System.currentTimeMillis(),
            content = content.trim(),
            createdAtMillis = System.currentTimeMillis()
        )
        noteDao.insertNote(newNote)
    }

    suspend fun updateNote(note: Note) = withContext(Dispatchers.IO) {
        noteDao.updateNote(note)
    }

    suspend fun deleteNote(id: Long) = withContext(Dispatchers.IO) {
        noteDao.deleteNoteById(id)
    }

    fun getSelectedTab(): Int = prefs.getInt("selected_tab", 0)

    fun setSelectedTab(tab: Int) {
        prefs.edit().putInt("selected_tab", tab).apply()
    }

    fun isDarkMode(): Boolean = prefs.getBoolean("is_dark_mode", true)

    fun setDarkMode(enabled: Boolean) {
        prefs.edit().putBoolean("is_dark_mode", enabled).apply()
    }
}
