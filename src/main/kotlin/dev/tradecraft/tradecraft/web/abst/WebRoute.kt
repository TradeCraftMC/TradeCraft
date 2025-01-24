package dev.tradecraft.tradecraft.web.abst

@Target(AnnotationTarget.CLASS)
annotation class WebRoute(val method: String, val path: String)
