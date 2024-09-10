package com.demo.tasks_app.entities

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(
    name = "tasks",
    uniqueConstraints = [UniqueConstraint(name = "uk_task_description", columnNames = ["description"])]
)
@EntityListeners(AuditingEntityListener::class)
class Task (
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tasks_sequence")
    @SequenceGenerator(name = "tasks_sequence", sequenceName = "tasks_sequence", allocationSize = 50)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    var description: String = "",

    @Column(nullable = false)
    var isReminderSet: Boolean = false,

    @Column(nullable = false)
    var isTaskOpen: Boolean = true,

    @Enumerated(EnumType.STRING)
    var priority: Priority = Priority.LOW,

    @CreatedDate
    @Column(nullable = false, updatable = false)
    val createdDate: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    @Column(insertable = false)
    var lastModifiedDate: LocalDateTime = LocalDateTime.now()
)