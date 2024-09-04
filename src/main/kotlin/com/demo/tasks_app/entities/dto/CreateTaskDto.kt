package com.demo.tasks_app.entities.dto

import com.demo.tasks_app.entities.Priority
import com.demo.tasks_app.entities.ValueOfEnum
import jakarta.validation.constraints.NotBlank

data class CreateTaskDto(
    @field:NotBlank(message = "Task description cannot be null or empty")
    val description: String,
    @field:NotBlank(message = "Priority cannot be null or empty")
    @field:ValueOfEnum(enumClass = Priority::class, message = "")
    val priority: String?
)
