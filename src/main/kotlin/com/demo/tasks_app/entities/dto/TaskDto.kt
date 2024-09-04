package com.demo.tasks_app.entities.dto

import com.demo.tasks_app.entities.Priority

data class TaskDto(
    val id: Long,
    val description: String,
    val isReminderSet: Boolean,
    val isTaskOpen: Boolean,
    val priority: Priority
)
