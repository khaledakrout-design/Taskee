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
            Task(id = 1L, title = "Design mobile app prototype", isCompleted = false, dueAtMillis = now + oneHour * 2),
            Task(id = 2L, title = "Review team pull requests", isCompleted = true, dueAtMillis = now - oneHour),
            Task(id = 3L, title = "Grocery shopping & errands", isCompleted = false, dueAtMillis = now + oneHour * 5)
        )
    }

    private fun getDefaultNotes(): List<Note> {
        val now = System.currentTimeMillis()
        return listOf(
            Note(
                id = 1L,
                content = "Meeting notes: Prioritize dark mode contrast and smooth animations for task completion.",
                createdAtMillis = now - 7200_000L
            ),
            Note(
                id = 2L,
                content = "Ideas for next sprint: Add recurring task reminders, custom categories, and tag filtering.",
                createdAtMillis = now - 3600_000L
            )
        )
    }

    suspend fun addTask(title: String, dueAtMillis: Long?) = withContext(Dispatchers.IO) {
        val newTask = Task(
            id = System.currentTimeMillis(),
            title = title.trim(),
            isCompleted = false,
            dueAtMillis = dueAtMillis
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
}
