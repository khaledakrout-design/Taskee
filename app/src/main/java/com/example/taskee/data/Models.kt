package com.example.taskee.data

data class Task(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val isCompleted: Boolean = false,
    val dueAtMillis: Long? = null
)

data class Note(
    val id: Long = System.currentTimeMillis(),
    val content: String,
    val createdAtMillis: Long = System.currentTimeMillis()
)
