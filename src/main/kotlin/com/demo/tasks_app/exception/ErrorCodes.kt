package com.demo.tasks_app.exception

enum class ErrorCodes(val description: String) {
    GENERIC_ERROR_MESSAGE("An unexpected error occurred. Please try again"),
    SQL_QUERY_ERROR("An error occurred while processing your request"),
    INCORRECT_CURRENT_PASSWORD("Current password is incorrect"),
    NEW_PASSWORD_DOES_NOT_MATCH("New password does not match"),
    ACCOUNT_LOCKED("User account is locked"),
    ACCOUNT_DISABLED("User account is disabled"),
    ACCESS_DENIED("Access denied. You do not have access to this resource"),
    BAD_CREDENTIALS("Bad credentials"),
    RECORD_ALREADY_EXISTS("Record already exists"),
    RECORD_NOT_FOUND("Could not find record"),
    EMAIL_MESSAGING_EXCEPTION("Could not send email"),
    DATA_INTEGRITY_MESSAGE("An error occurred while processing your request")
}