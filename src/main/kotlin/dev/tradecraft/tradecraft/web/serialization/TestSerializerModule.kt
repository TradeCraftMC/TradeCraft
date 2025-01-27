package dev.tradecraft.tradecraft.web.serialization

import com.fasterxml.jackson.databind.BeanDescription
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializationConfig
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier
import org.hibernate.proxy.HibernateProxy

class TestSerializerModule : SimpleModule() {
    override fun setupModule(context: SetupContext?) {
        super.setupModule(context)
        context!!.addBeanSerializerModifier(object : BeanSerializerModifier() {
            override fun modifySerializer(
                config: SerializationConfig?, beanDesc: BeanDescription?, serializer: JsonSerializer<*>?
            ): JsonSerializer<*> {
                return SkipProxySerializer(serializer as JsonSerializer<*>)
            }
        })
    }
}