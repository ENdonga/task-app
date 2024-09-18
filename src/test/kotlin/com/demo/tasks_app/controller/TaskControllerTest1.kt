package com.demo.tasks_app.controller

import com.demo.tasks_app.entities.Priority
import com.demo.tasks_app.entities.Task
import com.demo.tasks_app.entities.dto.CreateTaskDto
import com.demo.tasks_app.service.TaskService
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.time.LocalDateTime

private const val BASE_URL = "/api/tasks"

@ExtendWith(SpringExtension::class)
@WebMvcTest(controllers = [TaskController::class])
internal class TaskControllerTest1 @Autowired constructor(private val mockMvc: MockMvc) {
    @MockkBean
    private lateinit var taskService: TaskService
    private val mapper = ObjectMapper()
    private val requestDto = CreateTaskDto(description = "Test", priority = "low")
    private val task = Task(
        id = 1L,
        description = "Test",
        isReminderSet = false,
        isTaskOpen = true,
        Priority.LOW,
        createdDate = LocalDateTime.now(),
        lastModifiedDate = LocalDateTime.now()
    )
    private val tasks = listOf(task)

    @Test
    fun `test that create task returns HTTP 201 status on a successful create`() {
        every { taskService.createTask(any()) }.returns(task)

        mockMvc.post(BASE_URL) {
            contentType = APPLICATION_JSON
            accept = APPLICATION_JSON
            content = mapper.writeValueAsString(requestDto)
        }.andExpect {
            status { isCreated() }
            jsonPath("$.message", Matchers.equalTo("Task created successfully"))
        }.andReturn()
        assertThat(task.description).isEqualTo(requestDto.description)
        verify(exactly = 1) { taskService.createTask(requestDto) }
    }

    @Test
    fun `test that an empty list is returned when there are no saved tasks`() {
        every { taskService.findAllTasks() } answers { emptyList() }
        mockMvc.get(BASE_URL) {
            contentType = APPLICATION_JSON
            accept = APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            jsonPath("$.data.size()", Matchers.equalTo(0))
        }.andReturn()
    }

}