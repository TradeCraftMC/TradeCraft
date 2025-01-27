package dev.tradecraft.tradecraft.web.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import org.hibernate.Hibernate
import org.hibernate.proxy.HibernateProxy
import org.hibernate.query.results.Builders.entity
import java.lang.reflect.Type


class SkipProxySerializer<T : Any>(private val defaultSerializer: JsonSerializer<T>) : JsonSerializer<T>() {
    override fun serialize(value: T, gen: JsonGenerator?, serializers: SerializerProvider?) {
        println("${value.javaClass.name}: ${Hibernate.isInitialized(value)}")
        if (HibernateProxy::class.java.isInstance(value)) {
            println("fuck this shit im out")
            val proxy = HibernateProxy::class.java.cast(value)
            if (proxy.hibernateLazyInitializer.isUninitialized) {
                return;
            }
        }
        defaultSerializer.serialize(value, gen, serializers)
    }
}