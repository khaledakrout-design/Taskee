package com.example.taskee.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val isCompleted: Boolean = false,
    val dueAtMillis: Long? = null
)

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey
    val id: Long = System.currentTimeMillis(),
    val content: String,
    val createdAtMillis: Long = System.currentTimeMillis()
)
