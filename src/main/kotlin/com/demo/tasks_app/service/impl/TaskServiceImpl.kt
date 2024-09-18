package com.demo.tasks_app.service.impl

import com.demo.tasks_app.entities.Task
import com.demo.tasks_app.entities.dto.CreateTaskDto
import com.demo.tasks_app.entities.dto.UpdateTaskDto
import com.demo.tasks_app.exception.TaskAlreadyExistsException
import com.demo.tasks_app.exception.TaskNotFoundException
import com.demo.tasks_app.repository.TaskRepository
import com.demo.tasks_app.service.TaskService
import com.demo.tasks_app.toTaskEntity
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service

@Service
class TaskServiceImpl(private val repository: TaskRepository) : TaskService {

    override fun findAllTasks(): List<Task> {
        return repository.findAll()
    }

    override fun findTaskById(taskId: Long): Task {
        return repository.findById(taskId).orElseThrow { TaskNotFoundException("Task with $taskId not found") }
    }

    override fun findTasksByStatus(status: Boolean): List<Task> {
        return repository.findTasksByStatus(status)
    }

    override fun createTask(request: CreateTaskDto): Task {
        try {
            val task = request.toTaskEntity()
            return repository.save(task)
        } catch (ex: DataIntegrityViolationException) {
            throw TaskAlreadyExistsException("Task with description '${request.description}' already exists")
        }
    }

    override fun updateTask(request: UpdateTaskDto): Task {
        // find the task in the list/db
        val task = findTaskById(request.id)
        return repository.save(request.toTaskEntity(task))
    }

    override fun deleteTask(taskId: Long) {
        val task = findTaskById(taskId)
        repository.deleteById(task.id)
    }
}
