package com.demo.tasks_app.exception

data class TaskAlreadyExistsException(override val message: String) : RuntimeException(message)