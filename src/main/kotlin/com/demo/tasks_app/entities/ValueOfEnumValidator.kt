package com.demo.tasks_app.entities

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class ValueOfEnumValidator : ConstraintValidator<ValueOfEnum, String> {
    private lateinit var enumValues: Set<String>
    private lateinit var enumClass: Class<out Enum<*>>
    override fun initialize(validEnum: ValueOfEnum) {
        enumClass = validEnum.enumClass.java
        enumValues = enumClass.enumConstants.map { it.name }.toSet()
    }

    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        if (value == null) {
            return true
        }
        if (!enumValues.contains(value.uppercase())) {
            context?.disableDefaultConstraintViolation()
            val message = "${enumClass.simpleName} must be one of the following: ${enumValues.joinToString(", ")}"
            context?.buildConstraintViolationWithTemplate(message)?.addConstraintViolation()
            return false
        }
        return true
    }
}