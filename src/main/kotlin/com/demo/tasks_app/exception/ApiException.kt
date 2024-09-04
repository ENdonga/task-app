package com.demo.tasks_app.exception

data class ApiException(override val message: String) : RuntimeException(message)