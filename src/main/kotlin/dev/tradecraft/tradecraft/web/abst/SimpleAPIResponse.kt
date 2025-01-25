package dev.tradecraft.tradecraft.web.abst

data class SimpleAPIResponse(val status: Int, val message: String, val success: Boolean, val data: Any? = null)
