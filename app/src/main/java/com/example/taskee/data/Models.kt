package com.example.taskee.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

enum class Priority(val label: String) {
    LOW("Basse"),
    MEDIUM("Moyenne"),
    HIGH("Haute")
}

enum class TaskCategory(val label: String) {
    WORK("Travail"),
    PERSONAL("Personnel"),
    SHOPPING("Courses"),
    HEALTH("Santé"),
    OTHER("Autre")
}

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey
    val id: Long = System.currentTimeMillis(),
    val title: String,
    @ColumnInfo(name = "completed")
    val completed: Boolean = false,
    val dueAtMillis: Long? = null,
    val priority: Priority = Priority.MEDIUM,
    val category: TaskCategory = TaskCategory.PERSONAL
) {
    @get:Ignore
    val isCompleted: Boolean
        get() = completed
}

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey
    val id: Long = System.currentTimeMillis(),
    val content: String,
    val createdAtMillis: Long = System.currentTimeMillis()
)
