package com.demo.tasks_app

import com.demo.tasks_app.entities.Priority
import com.demo.tasks_app.entities.Task
import com.demo.tasks_app.entities.dto.CreateTaskDto
import com.demo.tasks_app.entities.dto.TaskDto
import com.demo.tasks_app.entities.dto.UpdateTaskDto

fun Task.toDto(): TaskDto {
    return TaskDto(
        id = this.id,
        description = this.description,
        isReminderSet = this.isReminderSet,
        isTaskOpen = this.isTaskOpen,
        priority = this.priority,
    )
}

fun CreateTaskDto.toTaskEntity(): Task {
    return Task(
        description = this.description,
        priority = Priority.valueOf(this.priority.toString().uppercase())
    )
}

fun UpdateTaskDto.applyUpdatesTo(task: Task): Task {
    this.description?.let { task.description = it }
    this.isReminderSet?.let { task.isReminderSet = it }
    this.isTaskOpen?.let { task.isTaskOpen = it }
    this.priority?.let { task.priority = Priority.valueOf(it.uppercase()) }
    return task
}

fun UpdateTaskDto.toTaskEntity(existingTask: Task): Task {
    return existingTask.apply {
        id = this@toTaskEntity.id
        description = this@toTaskEntity.description ?: description
        isReminderSet = this@toTaskEntity.isReminderSet ?: isReminderSet
        isTaskOpen = this@toTaskEntity.isTaskOpen ?: isTaskOpen
        priority = (this@toTaskEntity.priority ?: priority) as Priority
    }
}