package dev.tradecraft.tradecraft.web.utils

import dev.tradecraft.tradecraft.database.objects.User

interface RPCValidator {
    fun validate(fieldName: String, user: User?): Boolean
}