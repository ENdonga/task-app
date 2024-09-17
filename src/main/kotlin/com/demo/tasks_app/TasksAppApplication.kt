package com.demo.tasks_app

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TasksAppApplication

fun main(args: Array<String>) {
    runApplication<TasksAppApplication>(*args)
}