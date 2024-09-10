package com.demo.tasks_app.service

import com.demo.tasks_app.entities.Task
import com.demo.tasks_app.entities.dto.CreateTaskDto
import com.demo.tasks_app.entities.dto.UpdateTaskDto

interface TaskService {
    fun findAllTasks(): List<Task>
    fun findTaskById(taskId: Long): Task
    fun createTask(request: CreateTaskDto): Task
    fun updateTask(request: UpdateTaskDto): Task
    fun deleteTask(taskId: Long): Unit
    fun findTasksByStatus(status: Boolean): List<Task>
}