package com.demo.tasks_app.controller

import com.demo.tasks_app.apiresponse.ApiResponse
import com.demo.tasks_app.entities.dto.CreateTaskDto
import com.demo.tasks_app.entities.dto.UpdateTaskDto
import com.demo.tasks_app.service.TaskService
import com.demo.tasks_app.toDto
import jakarta.validation.Valid
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.OK
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/tasks")
class TaskController(private val service: TaskService) {
    @GetMapping
    fun findAllTasks(): ResponseEntity<ApiResponse> {
        val tasks = service.findAllTasks().map { it.toDto() }.toList()
        return ResponseEntity.ok().body(
            ApiResponse(
                timestamp = LocalDateTime.now().toString(),
                status = OK,
                statusCode = OK.value(),
                message = "Tasks fetched successfully",
                data = tasks
            )
        )
    }

    @GetMapping("/{task-id}")
    fun findTaskById(@PathVariable("task-id") taskId: Long): ResponseEntity<ApiResponse> {
        val task = service.findTaskById(taskId).toDto()
        return ResponseEntity.ok().body(
            ApiResponse(
                timestamp = LocalDateTime.now().toString(),
                status = OK,
                statusCode = OK.value(),
                message = "Task fetched successfully",
                data = task
            )
        )
    }

    @PostMapping
    fun createTask(@Valid @RequestBody task: CreateTaskDto): ResponseEntity<ApiResponse> {
        service.createTask(task)
        return ResponseEntity.created(URI("")).body(
            ApiResponse(
                timestamp = LocalDateTime.now().toString(),
                status = CREATED,
                statusCode = CREATED.value(),
                message = "Task created successfully",
            )
        )
    }

    @PatchMapping
    fun updateTask(@Valid @RequestBody updateTaskDto: UpdateTaskDto): ResponseEntity<ApiResponse> {
        val task = service.updateTask(updateTaskDto).toDto()
        return ResponseEntity.ok().body(
            ApiResponse(
                timestamp = LocalDateTime.now().toString(),
                status = OK,
                statusCode = OK.value(),
                message = "Task updated successfully",
                data = task
            )
        )
    }

    @DeleteMapping("/{task-id}")
    fun deleteTask(@PathVariable("task-id") taskId: Long): ResponseEntity<ApiResponse> {
        service.deleteTask(taskId)
        return ResponseEntity.ok().body(
            ApiResponse(
                timestamp = LocalDateTime.now().toString(),
                status = OK,
                statusCode = OK.value(),
                message = "Task deleted successfully",
            )
        )
    }
}