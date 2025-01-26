package dev.tradecraft.tradecraft.web.utils

interface CrudValidator <T> {
    fun validate(value: T): String?

    fun validateAny(value: Any): String? = validate(value as T)
}