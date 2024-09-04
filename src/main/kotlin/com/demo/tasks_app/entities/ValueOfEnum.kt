package com.demo.tasks_app.entities

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.*
import kotlin.reflect.KClass

@Target(FIELD, VALUE_PARAMETER)
@Retention(RUNTIME)
@Constraint(validatedBy = [ValueOfEnumValidator::class])
annotation class ValueOfEnum (
    val enumClass: KClass<out  Enum<*>>,
    val message: String = "Priority value must be any of: {enumValues}",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)