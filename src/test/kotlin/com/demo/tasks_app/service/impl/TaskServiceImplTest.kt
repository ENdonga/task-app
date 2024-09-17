package com.demo.tasks_app.service.impl

import com.demo.tasks_app.entities.Priority
import com.demo.tasks_app.entities.Task
import com.demo.tasks_app.entities.dto.CreateTaskDto
import com.demo.tasks_app.entities.dto.UpdateTaskDto
import com.demo.tasks_app.exception.TaskAlreadyExistsException
import com.demo.tasks_app.repository.TaskRepository
import com.demo.tasks_app.toTaskEntity
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import jakarta.persistence.EntityNotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.dao.DataIntegrityViolationException
import java.util.*

@ExtendWith(MockKExtension::class)
class TaskServiceImplTest {
    @RelaxedMockK
    private lateinit var repository: TaskRepository

    @InjectMockKs
    private lateinit var service: TaskServiceImpl
    private lateinit var createRequest: CreateTaskDto
    private val tasks = listOf(
        Task(id = 1L, description = "Task 1", isReminderSet = false, isTaskOpen = true, priority = Priority.HIGH),
        Task(id = 2L, description = "Task 2", isReminderSet = true, isTaskOpen = false, priority = Priority.MEDIUM),
        Task(id = 3L, description = "Task 3", isReminderSet = false, isTaskOpen = true, priority = Priority.LOW),
    )

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        createRequest = CreateTaskDto("New Task", Priority.LOW.toString())
    }

    @Test
    fun `should return all tasks`() {
        // given - when
        every { repository.findAll() } returns tasks
        val results = service.findAllTasks()
        // assert
        assertThat(results.size).isEqualTo(3)
        assertEquals("Task 1", results[0].description)
        assertEquals(Priority.HIGH, results[0].priority)
    }

    @Test
    fun `should create a new task`() {
        // given
        val newTask = createRequest.toTaskEntity()
        //when
        every { repository.save(any()) } returns newTask
        val result = service.createTask(createRequest)

        assertThat(result).isNotNull
        assertThat(result.description).isEqualTo(createRequest.description)
    }

    @Test
    fun `should throw an exception when task description exists in the database`() {
        val existingTask = createRequest.toTaskEntity()
        val dataIntegrityViolationException =
            DataIntegrityViolationException("Task with description '${existingTask.description}' already exists")
        every { repository.save(any()) } throws dataIntegrityViolationException
        val exception = assertThrows<TaskAlreadyExistsException> { service.createTask(createRequest) }
        assertThat(exception.message).isEqualTo("Task with description '${existingTask.description}' already exists")
    }

    @Test
    fun `find task by id should return task when called with a valid id`() {
        // given
        val taskId = 1L
        val task = Task(id = taskId, description = "Sample task")
        // when
        every { repository.findById(taskId) } returns Optional.of(task)
        val result = service.findTaskById(taskId)
        // then
        assertThat(result).isEqualTo(task)
        assertThat(result.description).isEqualTo(task.description)
    }

    @Test
    fun `should throw EntityNotFoundException when task is not found`() {
        // given
        val taskId = 1L
        // when
        every { repository.findById(any()) } returns Optional.empty()
        val exception = assertThrows<EntityNotFoundException> { service.findTaskById(taskId = 1) }

        assertThat(exception.message).isEqualTo("Task with $taskId not found")
        verify(exactly = 1) { repository.findById(any()) }
    }

    @Test
    fun `should update a task when the task is found in the database`() {
        // given
        val existingTask = Task(
            id = 1L,
            description = "Sample description",
            isReminderSet = false,
            isTaskOpen = true,
            priority = Priority.LOW
        )
        val updateRequest = UpdateTaskDto(
            id = 1L,
            description = "Updated description",
            isReminderSet = true,
            isTaskOpen = false,
            priority = Priority.MEDIUM.toString()
        )
        val updatedTask = updateRequest.toTaskEntity(existingTask)
        // when
        every { repository.findById(1L) } returns Optional.of(existingTask)
        every { repository.save(any()) } returns updatedTask
        val result = service.updateTask(updateRequest)
        // then
        assertThat(result).isEqualTo(updatedTask)
        assertThat(result.description).isEqualTo(updatedTask.description)
        verify(exactly = 1) { repository.findById(1L) }
        verify(exactly = 1) { repository.save(updatedTask) }
    }

    @Test
    fun `should throw EntityNotFoundException if task to update is not found`() {
        // given
        val updateRequest = UpdateTaskDto(
            id = 1L,
            description = "Updated description",
            isReminderSet = true,
            isTaskOpen = false,
            priority = Priority.MEDIUM.toString()
        )
        // when
        every { repository.findById(1L) } returns Optional.empty()
        val exception = assertThrows<EntityNotFoundException> { service.updateTask(updateRequest) }
        // then
        assertThat(exception.message).isEqualTo("Task with ${updateRequest.id} not found")
        verify(exactly = 1) { repository.findById(1L) }
        verify(exactly = 0) { repository.save(any()) }
    }

    @Test
    fun `should return a list of tasks of all open tasks`() {
        // given
        val openTasks = listOf(
            Task(id = 1L, description = "Task 1", isReminderSet = false, isTaskOpen = true, priority = Priority.LOW),
            Task(id = 2L, description = "Task 2", isReminderSet = false, isTaskOpen = true, priority = Priority.MEDIUM)
        )
        // when
        every { repository.findTasksByStatus(any()) } returns openTasks
        val results = service.findTasksByStatus(true)

        // then
        assertThat(results.size).isEqualTo(openTasks.size)
    }

    @Test
    fun `should return a list of tasks of all closed tasks`() {
        // given
        val openTasks = listOf(
            Task(id = 1L, description = "Task 1", isReminderSet = false, isTaskOpen = false, priority = Priority.LOW),
            Task(id = 2L, description = "Task 2", isReminderSet = false, isTaskOpen = false, priority = Priority.MEDIUM)
        )
        // when
        every { repository.findTasksByStatus(any()) } returns openTasks
        val results = service.findTasksByStatus(true)

        // then
        assertThat(results.size).isEqualTo(openTasks.size)
    }

    @Test
    fun `should delete a task successfully`() {
        // given
        val taskId = 1L
        val task = Task(
            id = taskId, description = "Sample Task", isReminderSet = false, isTaskOpen = true, priority = Priority.HIGH
        )
        // when
        every { repository.findById(1L) } returns Optional.of(task)
        every { repository.deleteById(any()) } just Runs
        service.deleteTask(1L)
        // assert
        verify(exactly = 1) { repository.deleteById(any()) }
    }

    @Test
    fun `delete task throws EntityNotFoundException if task to delete is not found by the ID`() {
        // given
        val taskId = 1L
        // when
        every { repository.findById(taskId) } returns Optional.empty()
        val exception = assertThrows<EntityNotFoundException> { service.deleteTask(taskId) }
        // assert
        assertThat(exception.message).isEqualTo("Task with $taskId not found")
        verify(exactly = 0) { repository.deleteById(any()) }
    }
}