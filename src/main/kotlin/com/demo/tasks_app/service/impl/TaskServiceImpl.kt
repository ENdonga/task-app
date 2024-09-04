package com.demo.tasks_app.service.impl

import com.demo.tasks_app.entities.Priority
import com.demo.tasks_app.entities.Task
import com.demo.tasks_app.entities.dto.CreateTaskDto
import com.demo.tasks_app.entities.dto.UpdateTaskDto
import com.demo.tasks_app.exception.TaskNotFoundException
import com.demo.tasks_app.repository.TaskRepository
import com.demo.tasks_app.service.TaskService
import com.demo.tasks_app.toTaskEntity
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class TaskServiceImpl(private val repository: TaskRepository) : TaskService {
    val tasks = mutableListOf(
        Task().apply {
            id = 1
            description = "Task 1"
            isReminderSet = true
            isTaskOpen = true
            priority = Priority.HIGH
            createdDate = LocalDateTime.of(2024, 9, 2, 19, 10, 33)
            lastModifiedDate = LocalDateTime.of(2024, 9, 2, 19, 10, 33)
        },
        Task().apply {
            id = 2
            description = "Task 2"
            isReminderSet = false
            isTaskOpen = false
            priority = Priority.MEDIUM
            createdDate = LocalDateTime.of(2024, 8, 23, 19, 10, 33)
            lastModifiedDate = LocalDateTime.of(2024, 9, 2, 19, 10, 33)
        },
        Task().apply {
            id = 3
            description = "Task 3"
            isReminderSet = false
            isTaskOpen = true
            priority = Priority.LOW
            createdDate = LocalDateTime.of(2024, 8, 23, 19, 10, 33)
            lastModifiedDate = LocalDateTime.of(2024, 9, 2, 19, 10, 33)
        }
    )

    override fun findAllTasks(): List<Task> {
//        return repository.findAll()
        return tasks
    }

    override fun findTaskById(taskId: Long): Task {
//        return repository.findById(taskId).orElseThrow { EntityNotFoundException("Task with $taskId not found") }
        return tasks.find { it.id == taskId } ?: throw Exception("General Exception")
    }

    override fun createTask(taskDto: CreateTaskDto): Task {
        val task = taskDto.toTaskEntity()
        task.id = (tasks.size + 1).toLong()
        tasks.add(task)
        return tasks.last()
    }

    override fun updateTask(updateTaskDto: UpdateTaskDto): Task {
        // find the task in the list/db
        val task = tasks.find { it.id == updateTaskDto.id }
            ?: throw TaskNotFoundException("Task with id: ${updateTaskDto.id} not found")
        // convert the DTO to task entity
        return updateTaskDto.toTaskEntity(task)
    }

    override fun deleteTask(taskId: Long) {
        val task = tasks.find { it.id == taskId } ?: throw TaskNotFoundException("Task with id: $taskId not found")
        tasks.remove(task)
        return
    }
}
