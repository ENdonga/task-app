package com.demo.tasks_app.repository

import com.demo.tasks_app.entities.Task
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface TaskRepository: JpaRepository<Task, Long> {
//    fun findTaskById(taskId: Long): Task

    @Query(value = """
        SELECT * FROM tasks WHERE is_task_open = true
    """, nativeQuery = true)
    fun findAllOpenTasks(): List<Task>

    @Query(value = """
        SELECT * FROM tasks WHERE is_task_open = false
    """, nativeQuery = true)
    fun findAllClosedTasks(): List<Task>
}