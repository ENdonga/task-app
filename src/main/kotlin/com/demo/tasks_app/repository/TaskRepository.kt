package com.demo.tasks_app.repository

import com.demo.tasks_app.entities.Task
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface TaskRepository : JpaRepository<Task, Long> {
//    fun findTaskById(taskId: Long): Task

    @Query(value = """SELECT * FROM tasks WHERE is_task_open = :status""", nativeQuery = true)
    fun findTasksByStatus(@Param("status") status: Boolean): List<Task>

    @Query(
        value = """
        SELECT CASE WHEN COUNT(t) > 0 THEN TRUE ELSE FALSE END FROM tasks t WHERE t.description = :description
    """, nativeQuery = true
    )
    fun doesTaskDescriptionExist(@Param("description") description: String): Boolean
}