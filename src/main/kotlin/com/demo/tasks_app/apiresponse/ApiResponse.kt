package com.demo.tasks_app.apiresponse

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT
import org.springframework.http.HttpStatus

@JsonInclude(NON_DEFAULT)
data class ApiResponse(
    val timestamp: String,
    val status: HttpStatus?,
    val statusCode: Int = 0,
    val message: String? = "",
    val reason: String = "",
    val apiPath: String = "",
    val validationErrors: Set<String?> = emptySet(),
    val data: Any? = null
)
