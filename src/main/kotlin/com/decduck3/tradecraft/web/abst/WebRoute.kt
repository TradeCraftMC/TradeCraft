package com.decduck3.tradecraft.web.abst

@Target(AnnotationTarget.CLASS)
annotation class WebRoute(val method: String, val path: String)
