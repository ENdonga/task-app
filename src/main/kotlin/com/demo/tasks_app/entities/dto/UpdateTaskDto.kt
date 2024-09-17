package com.demo.tasks_app.entities.dto

import com.demo.tasks_app.entities.Priority
import com.demo.tasks_app.entities.ValueOfEnum
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank

data class UpdateTaskDto(
    @NotBlank(message = "Task ID is required")
    val id: Long,
    val description: String?,
    @JsonProperty("isReminderSet")
    val isReminderSet: Boolean?,
    @JsonProperty("isTaskOpen")
    val isTaskOpen: Boolean?,
    @field:ValueOfEnum(enumClass = Priority::class, message = "")
    val priority: String?
)
