package com.demo.tasks_app.exception

data class TaskNotFoundException(override val message: String): RuntimeException(message)
