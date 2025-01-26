package dev.tradecraft.tradecraft.web.abst

@Repeatable
@Target(AnnotationTarget.CLASS)
annotation class WebRoute(val method: String, val path: String)
