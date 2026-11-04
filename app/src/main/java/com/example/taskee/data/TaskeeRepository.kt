package com.example.taskee.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class TaskeeRepository(private val context: Context) {

    private val tasksFile: File by lazy { File(context.filesDir, "tasks.json") }
    private val notesFile: File by lazy { File(context.filesDir, "notes.json") }
    private val prefs = context.getSharedPreferences("taskee_prefs", Context.MODE_PRIVATE)

    private val _tasksFlow = MutableStateFlow<List<Task>>(emptyList())
    val tasksFlow: StateFlow<List<Task>> = _tasksFlow.asStateFlow()

    private val _notesFlow = MutableStateFlow<List<Note>>(emptyList())
    val notesFlow: StateFlow<List<Note>> = _notesFlow.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        val loadedTasks = if (tasksFile.exists()) {
            try {
                val json = tasksFile.readText()
                val array = JSONArray(json)
                val list = mutableListOf<Task>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(
                        Task(
                            id = obj.optLong("id", System.currentTimeMillis()),
                            title = obj.getString("title"),
                            isCompleted = obj.optBoolean("isCompleted", false),
                            dueAtMillis = if (obj.has("dueAtMillis") && !obj.isNull("dueAtMillis")) {
                                obj.getLong("dueAtMillis")
                            } else null
                        )
                    )
                }
                list
            } catch (e: Exception) {
                getDefaultTasks()
            }
        } else {
            val defaults = getDefaultTasks()
            saveTasksInternal(defaults)
            defaults
        }

        val loadedNotes = if (notesFile.exists()) {
            try {
                val json = notesFile.readText()
                val array = JSONArray(json)
                val list = mutableListOf<Note>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(
                        Note(
                            id = obj.optLong("id", System.currentTimeMillis()),
                            content = obj.getString("content"),
                            createdAtMillis = obj.optLong("createdAtMillis", System.currentTimeMillis())
                        )
                    )
                }
                list
            } catch (e: Exception) {
                getDefaultNotes()
            }
        } else {
            val defaults = getDefaultNotes()
            saveNotesInternal(defaults)
            defaults
        }

        _tasksFlow.value = loadedTasks
        _notesFlow.value = loadedNotes
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

    private fun saveTasksInternal(tasks: List<Task>) {
        try {
            val array = JSONArray()
            tasks.forEach { task ->
                val obj = JSONObject()
                obj.put("id", task.id)
                obj.put("title", task.title)
                obj.put("isCompleted", task.isCompleted)
                if (task.dueAtMillis != null) {
                    obj.put("dueAtMillis", task.dueAtMillis)
                }
                array.put(obj)
            }
            tasksFile.writeText(array.toString())
        } catch (_: Exception) {}
    }

    private fun saveNotesInternal(notes: List<Note>) {
        try {
            val array = JSONArray()
            notes.forEach { note ->
                val obj = JSONObject()
                obj.put("id", note.id)
                obj.put("content", note.content)
                obj.put("createdAtMillis", note.createdAtMillis)
                array.put(obj)
            }
            notesFile.writeText(array.toString())
        } catch (_: Exception) {}
    }

    suspend fun addTask(title: String, dueAtMillis: Long?) = withContext(Dispatchers.IO) {
        val newTask = Task(
            id = System.currentTimeMillis(),
            title = title.trim(),
            isCompleted = false,
            dueAtMillis = dueAtMillis
        )
        val updated = listOf(newTask) + _tasksFlow.value
        _tasksFlow.value = updated
        saveTasksInternal(updated)
    }

    suspend fun updateTask(task: Task) = withContext(Dispatchers.IO) {
        val updated = _tasksFlow.value.map { if (it.id == task.id) task else it }
        _tasksFlow.value = updated
        saveTasksInternal(updated)
    }

    suspend fun toggleTaskComplete(id: Long) = withContext(Dispatchers.IO) {
        val updated = _tasksFlow.value.map {
            if (it.id == id) it.copy(isCompleted = !it.isCompleted) else it
        }
        _tasksFlow.value = updated
        saveTasksInternal(updated)
    }

    suspend fun deleteTask(id: Long) = withContext(Dispatchers.IO) {
        val updated = _tasksFlow.value.filter { it.id != id }
        _tasksFlow.value = updated
        saveTasksInternal(updated)
    }

    suspend fun undoDeleteTask(task: Task) = withContext(Dispatchers.IO) {
        val updated = _tasksFlow.value + task
        _tasksFlow.value = updated
        saveTasksInternal(updated)
    }

    suspend fun addNote(content: String) = withContext(Dispatchers.IO) {
        val newNote = Note(
            id = System.currentTimeMillis(),
            content = content.trim(),
            createdAtMillis = System.currentTimeMillis()
        )
        val updated = listOf(newNote) + _notesFlow.value
        _notesFlow.value = updated
        saveNotesInternal(updated)
    }

    suspend fun updateNote(note: Note) = withContext(Dispatchers.IO) {
        val updated = _notesFlow.value.map { if (it.id == note.id) note else it }
        _notesFlow.value = updated
        saveNotesInternal(updated)
    }

    suspend fun deleteNote(id: Long) = withContext(Dispatchers.IO) {
        val updated = _notesFlow.value.filter { it.id != id }
        _notesFlow.value = updated
        saveNotesInternal(updated)
    }

    fun getSelectedTab(): Int = prefs.getInt("selected_tab", 0)

    fun setSelectedTab(tab: Int) {
        prefs.edit().putInt("selected_tab", tab).apply()
    }
}
