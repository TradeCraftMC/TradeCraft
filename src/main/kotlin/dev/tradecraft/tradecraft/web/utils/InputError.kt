package dev.tradecraft.tradecraft.web.utils

import java.lang.Error

class InputError(override val message: String?) : Error(message)