package com.demo.tasks_app.entities.dto

import com.demo.tasks_app.entities.Priority
import com.demo.tasks_app.entities.ValueOfEnum
import jakarta.validation.constraints.NotBlank

data class UpdateTaskDto(
    @NotBlank(message = "Task ID is required")
    val id: Long,
    val description: String?,
    val isReminderSet: Boolean?,
    val isTaskOpen: Boolean?,
    @field:ValueOfEnum(enumClass = Priority::class, message = "")
    val priority: String?
)
