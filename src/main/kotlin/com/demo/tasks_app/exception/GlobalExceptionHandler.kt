package com.demo.tasks_app.exception

import com.demo.tasks_app.apiresponse.ApiResponse
import jakarta.persistence.EntityNotFoundException
import org.springframework.dao.DataAccessException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.*
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.time.LocalDateTime

@RestControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {
    private val exceptionMessage = "An exception occurred"

    override fun handleExceptionInternal(
        ex: Exception,
        body: Any?,
        headers: HttpHeaders,
        statusCode: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        val message = ex.message ?: "No message available"
        return buildErrorResponse(message, reason = ex.message.toString(), statusCode)
    }

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException, headers: HttpHeaders,
        statusCode: HttpStatusCode, request: WebRequest
    ): ResponseEntity<Any>? {
        val fieldErrors = ex.bindingResult.fieldErrors
        val validationErrors = fieldErrors.map { it.defaultMessage }.toSet()
        return ResponseEntity(
            ApiResponse(
                timestamp = LocalDateTime.now().toString(),
                status = resolve(statusCode.value()),
                statusCode = statusCode.value(),
                message = "Validation failed. Invalid field(s) in the request body",
                validationErrors = validationErrors
            ), statusCode
        )
    }

    override fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        val message = "Invalid request body"
        return buildErrorResponse(message, reason = ex.message.toString(), status)
    }

    @ExceptionHandler(ApiException::class)
    fun handleApiException(ex: ApiException): ResponseEntity<ApiResponse> {
        return buildErrorResponse(ex.message, BAD_REQUEST)
    }

    @ExceptionHandler(TaskNotFoundException::class)
    fun handleTaskNotFoundException(ex: TaskNotFoundException): ResponseEntity<ApiResponse> {
        return buildErrorResponse(ex.message, NOT_FOUND)
    }

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleEntityNotFoundException(ex: EntityNotFoundException): ResponseEntity<ApiResponse> {
        return buildErrorResponse(ex.message.toString(), BAD_REQUEST)
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleMethodArgumentTypeMismatchException(ex: MethodArgumentTypeMismatchException): ResponseEntity<ApiResponse> {
        val reason = "Failed to convert value '${ex.value}' to required type '${ex.requiredType?.simpleName}'"
        return buildErrorResponse(exceptionMessage, reason, BAD_REQUEST)
    }

    @ExceptionHandler(TaskAlreadyExistsException::class)
    fun handleTaskAlreadyExistsException(ex: TaskAlreadyExistsException): ResponseEntity<ApiResponse> {
        return buildErrorResponse(exceptionMessage, ex.message, CONFLICT)
    }

    @ExceptionHandler(DataAccessException::class)
    fun handleDaoException(ex: DataAccessException): ResponseEntity<ApiResponse> {
        return buildErrorResponse(exceptionMessage, ErrorCodes.SQL_QUERY_ERROR.description, INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneralException(ex: Exception): ResponseEntity<ApiResponse> {
        return buildErrorResponse(exceptionMessage, ex.message.toString(), INTERNAL_SERVER_ERROR)
    }

    private fun buildErrorResponse(message: String, status: HttpStatus): ResponseEntity<ApiResponse> {
        return ResponseEntity(
            ApiResponse(
                timestamp = LocalDateTime.now().toString(),
                status = status,
                statusCode = status.value(),
                message = exceptionMessage,
                reason = message
            ), status
        )
    }

    private fun buildErrorResponse(message: String, reason: String, status: HttpStatus): ResponseEntity<ApiResponse> {
        return ResponseEntity(
            ApiResponse(
                timestamp = LocalDateTime.now().toString(),
                status = status,
                statusCode = status.value(),
                message = message,
                reason = reason
            ), status
        )
    }

    private fun buildErrorResponse(message: String, reason: String, status: HttpStatusCode): ResponseEntity<Any> {
        return ResponseEntity(
            ApiResponse(
                timestamp = LocalDateTime.now().toString(),
                status = resolve(status.value()),
                statusCode = status.value(),
                message = message,
                reason = reason
            ), status
        )
    }
}
