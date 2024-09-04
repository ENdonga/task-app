package com.demo.tasks_app

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
class TasksAppApplication

fun main(args: Array<String>) {
    runApplication<TasksAppApplication>(*args)
}