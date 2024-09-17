package com.demo.tasks_app.controller

import com.demo.tasks_app.entities.Priority.LOW
import com.demo.tasks_app.entities.Priority.MEDIUM
import com.demo.tasks_app.entities.Task
import com.demo.tasks_app.entities.dto.CreateTaskDto
import com.demo.tasks_app.entities.dto.TaskDto
import com.demo.tasks_app.entities.dto.UpdateTaskDto
import com.demo.tasks_app.exception.TaskAlreadyExistsException
import com.demo.tasks_app.service.TaskService
import com.demo.tasks_app.toTaskEntity
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import jakarta.persistence.EntityNotFoundException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.OK
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDateTime

@ExtendWith(SpringExtension::class)
@WebMvcTest(controllers = [TaskController::class])
class TaskControllerTest(@Autowired private val mockMvc: MockMvc) {
    @MockBean
    private lateinit var mockService: TaskService
    private val mapper = jacksonObjectMapper()
    private val baseUrl = "/api/tasks"
    private val taskId: Long = 1
    private val taskDto = TaskDto(id = 1, description = "Task 1", isReminderSet = false, isTaskOpen = true, LOW)
    private lateinit var taskDtos: List<TaskDto>

    @BeforeEach
    fun setUp() {
        mapper.registerModules(JavaTimeModule())
        taskDtos = listOf(
            TaskDto(id = 1, description = "Task 1", isReminderSet = false, isTaskOpen = true, LOW),
            TaskDto(id = 2, description = "Task 2", isReminderSet = true, isTaskOpen = false, MEDIUM),
        )
    }

    // GET All tasks tests
    @Test
    fun `should list all tasks when no status is provided`() {
        // given & when & then
        `when`(mockService.findAllTasks()).thenReturn(taskDtos.map { it.toTaskEntity() })
        mockMvc.perform(get(baseUrl))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.data.size()").value(taskDtos.size))
    }

    @Test
    fun `should return list of only open tasks when status true is provided`() {
        // given
        val dtos = listOf(
            TaskDto(id = 1, description = "Task 1", isReminderSet = false, isTaskOpen = true, LOW),
        )
        // when
        `when`(mockService.findTasksByStatus(true)).thenReturn(dtos.map { it.toTaskEntity() })
        mockMvc.perform(get(baseUrl).param("status", "true"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.data.size()").value(dtos.size))
            .andExpect(jsonPath("$.data[0].isTaskOpen").value(dtos[0].isTaskOpen))
    }

    @Test
    fun `should return list of only closed tasks when status false is provided`() {
        // given
        val dtos = listOf(
            TaskDto(id = 1, description = "Task 1", isReminderSet = false, isTaskOpen = false, LOW),
        )
        // when & then
        `when`(mockService.findTasksByStatus(false)).thenReturn(dtos.map { it.toTaskEntity() })
        mockMvc.perform(get(baseUrl).param("status", "false"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.data.size()").value(dtos.size))
            .andExpect(jsonPath("$.data[0].isTaskOpen").value(dtos[0].isTaskOpen))
    }

    @Test
    fun `should return an empty list if there are no tasks saved`() {
        //given when & then
        `when`(mockService.findAllTasks()).thenReturn(emptyList())
        mockMvc.perform(get(baseUrl).param("status", "false"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.data.size()").value(0))
    }

    @Test
    fun `should throw an exception when status is not boolean`() {
        val status = "hashsj"
        mockMvc.perform(
            get(baseUrl)
                .param("status", status)
                .contentType(APPLICATION_JSON)
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.status").value(BAD_REQUEST.name))
            .andExpect(jsonPath("$.statusCode").value(BAD_REQUEST.value()))
            .andExpect(jsonPath("$.reason").value("Failed to convert value '$status' to required type 'Boolean'"))
    }

    // GET task by ID tests
    @Test
    fun `should throw an exception when id parameter is invalid`() {
        val taskId = "test"
        mockMvc.perform(get("${baseUrl}/${taskId}").contentType(APPLICATION_JSON))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.status").value(BAD_REQUEST.name))
            .andExpect(jsonPath("$.statusCode").value(BAD_REQUEST.value()))
            .andExpect(jsonPath("$.reason").value("Failed to convert value '$taskId' to required type 'long'"))
    }

    @Test
    fun `should return a bad request when id parameter is not found`() {
        `when`(mockService.findTaskById(taskId))
            .thenThrow(EntityNotFoundException("Task with $taskId not found"))
        mockMvc.perform(get("${baseUrl}/{id}", taskId).contentType(APPLICATION_JSON))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.status").value(BAD_REQUEST.name))
            .andExpect(jsonPath("$.statusCode").value(BAD_REQUEST.value()))
            .andExpect(jsonPath("$.reason").value("Task with $taskId not found"))
    }

    @Test
    fun `should return a 200 success request when a task is found with the ID provided`() {
        val task = Task(
            id = 1L,
            description = "Test",
            isReminderSet = false,
            isTaskOpen = true,
            LOW,
            createdDate = LocalDateTime.now(),
            lastModifiedDate = LocalDateTime.now()
        )
        `when`(mockService.findTaskById(taskId)).thenReturn(task)
        mockMvc.perform(get("${baseUrl}/{taskId}", taskId).contentType(APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value(OK.name))
            .andExpect(jsonPath("$.statusCode").value(OK.value()))
            .andExpect(jsonPath("$.message").value("Task fetched successfully"))
    }

    // DELETE endpoint tests
    @Test
    fun `should throw an exception when delete API id parameter is invalid`() {
        val taskId = "test"
        mockMvc.perform(delete("${baseUrl}/{taskId}", taskId).contentType(APPLICATION_JSON))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.status").value(BAD_REQUEST.name))
            .andExpect(jsonPath("$.statusCode").value(BAD_REQUEST.value()))
            .andExpect(jsonPath("$.reason").value("Failed to convert value '$taskId' to required type 'long'"))
    }

    @Test
    fun `should return a bad request when delete API id parameter is not found`() {
        `when`(mockService.deleteTask(taskId))
            .thenThrow(EntityNotFoundException("Task with $taskId not found"))
        mockMvc.perform(delete("${baseUrl}/{taskId}", taskId).contentType(APPLICATION_JSON))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.status").value(BAD_REQUEST.name))
            .andExpect(jsonPath("$.statusCode").value(BAD_REQUEST.value()))
            .andExpect(jsonPath("$.reason").value("Task with $taskId not found"))
    }

    @Test
    fun `should return a 200 success request when a task is deleted successfully`() {
        doNothing().`when`(mockService).deleteTask(taskId)
        mockMvc.perform(delete("${baseUrl}/{taskId}", taskId).contentType(APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value(OK.name))
            .andExpect(jsonPath("$.statusCode").value(OK.value()))
            .andExpect(jsonPath("$.message").value("Task deleted successfully"))
    }

    // Patch endpoint tests
    @Test
    fun `should update a task successfully`() {
        // given
        val request = UpdateTaskDto(
            id = taskDto.id,
            description = taskDto.description,
            isReminderSet = taskDto.isReminderSet,
            isTaskOpen = taskDto.isTaskOpen,
            priority = taskDto.priority.toString()
        )
        val task = Task(
            id = taskDto.id,
            description = taskDto.description,
            isReminderSet = taskDto.isReminderSet,
            isTaskOpen = taskDto.isTaskOpen,
            priority = taskDto.priority,
            createdDate = LocalDateTime.now(),
            lastModifiedDate = LocalDateTime.now()
        )
        // when
        `when`(mockService.updateTask(request)).thenReturn(task)
        val requestBody = mapper.writeValueAsString(request)
        // then
        mockMvc.perform(patch(baseUrl).contentType(APPLICATION_JSON).content(requestBody))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value(OK.name))
            .andExpect(jsonPath("$.message").value("Task updated successfully"))
            .andExpect(jsonPath("$.data.description").value(taskDto.description))
    }

    @Test
    fun `should return bad request when id is not provided or a task with id is not found`() {
        val invalidRequest = UpdateTaskDto(
            id = 0,
            description = taskDto.description,
            isReminderSet = taskDto.isReminderSet,
            isTaskOpen = taskDto.isTaskOpen,
            priority = taskDto.priority.toString()
        )
        `when`(mockService.updateTask(invalidRequest)).thenThrow(EntityNotFoundException("Task with ${invalidRequest.id} not found"))
        val requestBody = mapper.writeValueAsString(invalidRequest)
        mockMvc.perform(patch(baseUrl).contentType(APPLICATION_JSON).content(requestBody))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.status").value(BAD_REQUEST.name))
            .andExpect(jsonPath("$.reason").value("Task with ${invalidRequest.id} not found"))
    }

    @Test
    fun `should return bad request when priority is not LOW MEDIUM or HIGH`() {
        val invalidRequest = UpdateTaskDto(
            id = 1,
            description = taskDto.description,
            isReminderSet = taskDto.isReminderSet,
            isTaskOpen = taskDto.isTaskOpen,
            priority = "INVALID_PRIORITY"
        )
        val requestBody = mapper.writeValueAsString(invalidRequest)
        mockMvc.perform(patch(baseUrl).contentType(APPLICATION_JSON).content(requestBody))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.status").value(BAD_REQUEST.name))
            .andExpect(jsonPath("$.validationErrors[0]").value("Priority must be one of the following: LOW, MEDIUM, HIGH"))
    }

    // POST endpoint tests
    @Test
    fun `should validate a task is created successfully`() {
        // given
        val request = CreateTaskDto(description = "Task", priority = "low")
        val task = Task(id = 1, description = "Task", isReminderSet = false, isTaskOpen = true, priority = LOW)
        // when
        `when`(mockService.createTask(request)).thenReturn(task)
        val requestBody = mapper.writeValueAsString(request)
        // then
        mockMvc.perform(post(baseUrl).contentType(APPLICATION_JSON).content(requestBody))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.message").value("Task created successfully"))
    }

    @Test
    fun `should validate a validation error is displayed when description is blank`() {
        // given
        val request = CreateTaskDto(description = "", priority = "low")
        // when
        val requestBody = mapper.writeValueAsString(request)
        // then
        mockMvc.perform(post(baseUrl).contentType(APPLICATION_JSON).content(requestBody))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.validationErrors").value("Task description cannot be null or empty"))
    }

    @Test
    fun `should validate a validation error is displayed when priority is invalid`() {
        // given
        val request = CreateTaskDto(description = "Task", priority = "INVALID_PRIORITY")
        // when
        val requestBody = mapper.writeValueAsString(request)
        // then
        mockMvc.perform(post(baseUrl).contentType(APPLICATION_JSON).content(requestBody))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.validationErrors").value("Priority must be one of the following: LOW, MEDIUM, HIGH"))
    }

    @Test
    fun `should validate a task cannot be created with the same description more than once`() {
        // given
        val request = CreateTaskDto(description = "Task", priority = "LOW")
        // when
        val requestBody = mapper.writeValueAsString(request)
        `when`(mockService.createTask(request)).thenThrow(TaskAlreadyExistsException("Task with description '${request.description}' already exists"))
        // then
        mockMvc.perform(post(baseUrl).contentType(APPLICATION_JSON).content(requestBody))
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.reason").value("Task with description '${request.description}' already exists"))
    }
}